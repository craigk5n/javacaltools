/*
 * Copyright (C) 2005-2017 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 */
package us.k5n.ui.calendar;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Timer;

/**
 * The CalendarPanel class is a Swing component for displaying a monthly
 * calendar with events. The calling application must implement the
 * CalendarDataRepository interface in order for this class to obtain events to
 * display. Note that this class does not cache any event information outside of
 * what is currently on the screen. So, the calling application should implement
 * an efficient methods for the CalendarDataRepository interfance. (For example,
 * it would be a bad idea to query a database each time.)
 *
 * The default display will include 5 weeks, but by calling setNumWeeksToDisplay
 * you can change the view to a single week, two weeks, etc.
 *
 * @see CalendarDataRepository
 * @author Craig Knudsen, craig@k5n.us
 */
public class CalendarPanel extends JPanel implements MouseWheelListener {
	private static final long serialVersionUID = 1000L;
	private CalendarDataRepository repository;
	private EventFormatter formatter;
	private JLabel title;
	private JPanel drawArea;
	private JScrollBar scrollBar;
	private Calendar startDate; // Date of first day displayed
	private int firstDayOfWeek; // Day of week that week starts on (SUNDAY,
								// MONDAY, etc.)
	private Calendar absoluteStart, absoluteEnd; // don't scroll past these
	private Calendar absoluteEndWeekStart;
	private Color backgroundColor1, backgroundColor2;
	private Color todayBackgroundColor;
	private Color gridColor;
	private Color selectionColor;
	private Color headerForeground, headerBackground;
	private Color hintBackground, hintForeground;
	private Font headerFont = null, eventFont = null;
	private Font hintFont = null;
	private int lastWidth = -1, lastHeight = -1;
	private double cellWidth = 100, cellHeight = 100;
	private int headerHeight = 10;
	private int[] columnX;
	private int[] rowY;
	public final static int DEFAULT_NUM_WEEKS_TO_DISPLAY = 5;
	private int numWeeksToDisplay = DEFAULT_NUM_WEEKS_TO_DISPLAY;
	private boolean forceLayout = false;
	private String[] weekdays = null;
	private String[] monthNames = null;
	private boolean changingScrollbar = false;
	private int CELL_MARGIN = 2;
	private List<DisplayedEvent> displayedEvents;
	private List<DisplayedDate> displayedDates;
	private Timer timer = null;
	private boolean drawDateHint = false;
	private int fadeStep = 0;
	private boolean showTime = true;
	private boolean allowsEventSelection = true;
	// Because we don't store a copy of the Event objects (we use
	// DisplayedEvent objects which include only events visible in
	// the scrolled area), we cannot store the selection status
	// in the DisplayedEvent object. Instead we will track what
	// event the user has selected by date and event number for
	// that date.
	private Date selectedDate = null;
	private int selectedItemInd = -1;// 0=first event of day selected
	private List<CalendarPanelSelectionListener> selectionListeners;
	private DisplayedEvent currentMouseOverEvent = null;

	private class Date {
		public int year, month, day;

		public Date(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}

	private class DisplayedEvent {
		EventInstance event;
		Rectangle rect;
		int eventNoForDay;

		public DisplayedEvent(EventInstance event, Rectangle rect, int eventNo) {
			this.event = event;
			this.rect = rect;
			this.eventNoForDay = eventNo;
		}

		public boolean isSameEvent(Object o) {
			if (!(o instanceof DisplayedEvent))
				return false;
			DisplayedEvent e2 = (DisplayedEvent) o;
			return (rect.equals(e2.rect));
		}
	}

	private class DisplayedDate {
		Date date;
		Rectangle rect;

		public DisplayedDate(Date date, Rectangle rect) {
			this.date = date;
			this.rect = rect;
		}
	}

	// We use the MonthPanel to do our custom drawing to display the events on.
	private class MonthPanel extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1000L;

		public MonthPanel() {
			super();
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		protected DisplayedEvent getEventForPosition(int x, int y) {
			for (DisplayedEvent de : displayedEvents) {
				if (x >= de.rect.x && x <= de.rect.x + de.rect.width && y >= de.rect.y
						&& y <= de.rect.y + de.rect.height) {
					return de;
				}
			}
			return null;
		}

		public DisplayedEvent getMouseOverEvent(MouseEvent e) {
			return getEventForPosition(e.getX(), e.getY());
		}

		public void paint(Graphics g) {
			super.paint(g);
			paintMonth(g);
		}

		public void mouseClicked(MouseEvent e1) {
			boolean wasSelected = selectedDate != null;
			boolean doRepaint = false;
			selectedDate = null;
			selectedItemInd = -1;
			DisplayedEvent selectedEvent = null;
			if (currentMouseOverEvent != null)
				doRepaint = true;
			currentMouseOverEvent = null; // Don't display event popup
			for (DisplayedEvent de : displayedEvents) {
				if (e1.getX() >= de.rect.x && e1.getX() <= de.rect.x + de.rect.width && e1.getY() >= de.rect.y
						&& e1.getY() <= de.rect.y + de.rect.height) {
					// Found item
					selectedDate = new Date(de.event.getYear(), de.event.getMonth(), de.event.getDayOfMonth());
					if (getAllowsEventSelection()) {
						selectedItemInd = de.eventNoForDay;
						selectedEvent = de;
					}
					break;
				}
			}
			if (selectedEvent == null) {
				for (DisplayedDate dd : displayedDates) {
					if (e1.getX() >= dd.rect.x && e1.getX() <= dd.rect.x + dd.rect.width && e1.getY() >= dd.rect.y
							&& e1.getY() <= dd.rect.y + dd.rect.height) {
						// Found date
						selectedDate = dd.date;
					}
				}
			}
			if (wasSelected) {
				for (CalendarPanelSelectionListener l : selectionListeners)
					l.eventUnselected();
				doRepaint = true;
			}
			if (selectedDate != null && selectedEvent != null) {
				for (CalendarPanelSelectionListener l : selectionListeners)
					l.eventSelected(selectedEvent.event);
				doRepaint = true;
			}
			// If this is a double-click, then invoke the l.eventDoubleClicked
			// method
			if (e1.getClickCount() == 2 && selectedDate != null && selectedEvent != null) {
				for (CalendarPanelSelectionListener l : selectionListeners)
					l.eventDoubleClicked(selectedEvent.event);
			} else if (e1.getClickCount() == 2 && selectedDate != null && selectedEvent == null) {
				// Date double-clicked
				for (CalendarPanelSelectionListener l : selectionListeners)
					l.dateDoubleClicked(selectedDate.year, selectedDate.month, selectedDate.day);
			}
			// System.out.println ( "sel event: " + selectedEvent.event
			// + ", selectedItemInd=" + selectedItemInd );
			if (doRepaint)
				repaint();
		}

		public void mouseEntered(MouseEvent e1) {
			currentMouseOverEvent = getMouseOverEvent(e1);
			if (currentMouseOverEvent != null)
				repaint();
		}

		public void mouseExited(MouseEvent e1) {
			if (currentMouseOverEvent != null)
				repaint();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mousePressed(MouseEvent e1) {
			if (currentMouseOverEvent != null)
				repaint();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseReleased(MouseEvent e1) {
			if (currentMouseOverEvent != null)
				repaint();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseDragged(MouseEvent e1) {
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseMoved(MouseEvent e1) {
			// Display event popup?
			DisplayedEvent oldEvent = currentMouseOverEvent;
			currentMouseOverEvent = getMouseOverEvent(e1);
			if ((oldEvent == null && currentMouseOverEvent != null)
					|| (oldEvent != null && currentMouseOverEvent == null))
				repaint();
			else if (oldEvent != null && currentMouseOverEvent != null && !oldEvent.isSameEvent(currentMouseOverEvent))
				repaint();
		}
	}

	// Get the first day of the week for the current locale, typically Sunday in
	// the US.
	private static int getFirstDayOfWeek() {
		switch (Calendar.getInstance().getFirstDayOfWeek()) {
		case Calendar.SUNDAY:
			return (0);
		case Calendar.MONDAY:
			return (1);
		case Calendar.TUESDAY:
			return (2);
		case Calendar.WEDNESDAY:
			return (3);
		case Calendar.THURSDAY:
			return (4);
		case Calendar.FRIDAY:
			return (5);
		case Calendar.SATURDAY:
			return (6);
		}
		return (-1);
	}

	public CalendarPanel(CalendarDataRepository repository) {
		super();
		this.repository = repository;
		this.formatter = new EventFormatter();
		this.firstDayOfWeek = CalendarPanel.getFirstDayOfWeek();
		this.selectionListeners = new ArrayList<CalendarPanelSelectionListener>();

		this.backgroundColor1 = new Color(232, 232, 232);
		this.backgroundColor2 = new Color(212, 212, 212);
		this.todayBackgroundColor = new Color(255, 255, 212);
		this.headerForeground = Color.BLUE;
		this.headerBackground = Color.WHITE;
		this.gridColor = Color.BLACK;
		this.selectionColor = Color.RED;
		this.hintBackground = Color.DARK_GRAY;
		this.hintForeground = Color.white;
		this.displayedEvents = new ArrayList<DisplayedEvent>();
		this.displayedDates = new ArrayList<DisplayedDate>();

		monthNames = new String[12];
		Calendar c = Calendar.getInstance();
		// Use "MMM" for the short month name.
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
		for (int i = 0; i < 12; i++) {
			c.set(Calendar.MONTH, i);
			monthNames[i] = monthFormat.format(c.getTime());
		}
		// Use "EEE" for short weekday names
		int[] weekdayTranslation = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
				Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY };
		weekdays = new String[7];
		SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE");
		for (int i = 0; i < 7; i++) {
			c.set(Calendar.DAY_OF_WEEK, weekdayTranslation[i]);
			weekdays[i] = weekdayFormat.format(c.getTime());
		}

		createUI();

		this.setWeekOffset(0);
	}

	/**
	 * Set a new line wrap and text formatter for converting event details into
	 * a List of Strings for display on the panel as a tooltip.
	 *
	 * @param newFormatter
	 */
	public void setFormatter(EventFormatter newFormatter) {
		this.formatter = newFormatter;
	}

	private int weekDiff(Calendar c1, Calendar c2) {
		long diff = Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis());
		long numWeeks = diff / (7 * 24 * 3600 * 1000L);
		return (int) numWeeks + 1;
	}

	protected void createUI() {
		this.setLayout(new BorderLayout());
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JButton todayButton = new JButton("Today");
		todayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Scroll calendar back to current date.
				setWeekOffset(0);
				// Change scrollbar settings so that 0 is in the middle again
				if (absoluteStart != null && absoluteEnd != null) {
					Calendar now = Calendar.getInstance();
					if (now.after(absoluteEnd))
						now = (Calendar) absoluteEndWeekStart.clone();
					else if (now.before(absoluteStart))
						now = (Calendar) absoluteStart.clone();
					int totalWeeks = weekDiff(absoluteStart, absoluteEndWeekStart);
					int weeksToNow = weekDiff(absoluteStart, now);
					scrollBar.setMinimum(-weeksToNow);
					scrollBar.setMaximum(totalWeeks - weeksToNow);
					scrollBar.setValue(0);
				} else if (absoluteEnd != null) {
					// start is null
					Calendar now = Calendar.getInstance();
					if (now.after(absoluteEnd))
						now = (Calendar) absoluteEndWeekStart.clone();
					int weeksLeft = weekDiff(now, absoluteEndWeekStart);
					if (weeksLeft < 52) {
						scrollBar.setMinimum(-52);
						scrollBar.setMaximum(weeksLeft);
						scrollBar.setValue(0);
					} else {
						scrollBar.setMinimum(-52);
						scrollBar.setMaximum(52);
						scrollBar.setValue(0);
					}
				} else if (absoluteStart != null) {
					// end is null
					Calendar now = Calendar.getInstance();
					if (now.before(absoluteStart))
						now = (Calendar) absoluteStart.clone();
					int weeksUntilNow = weekDiff(absoluteStart, now);
					if (weeksUntilNow < 52) {
						scrollBar.setMinimum(-weeksUntilNow);
						scrollBar.setMaximum(52);
						scrollBar.setValue(0);
					} else {
						scrollBar.setMinimum(-52);
						scrollBar.setMaximum(52);
						scrollBar.setValue(0);
					}
				} else {
					scrollBar.setMinimum(-52);
					scrollBar.setMaximum(52);
					scrollBar.setValue(0);
				}
			}
		});
		titlePanel.add(todayButton, BorderLayout.EAST);
		this.title = new JLabel("Calendar", JLabel.CENTER);
		Font f = this.title.getFont();
		this.title.setFont(new Font(f.getFamily(), Font.BOLD, f.getSize() + 4));
		titlePanel.add(title, BorderLayout.CENTER);
		this.add(titlePanel, BorderLayout.NORTH);
		// ScrollBar values: 0 = current week, -N = N week before, +N = N weeks
		// after
		this.scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 5, -52, 52);
		this.scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// Ignore events caused by changing scrollbar min/max values
				// below
				// or else we will get a stack overflow.
				if (changingScrollbar)
					return;
				int val = e.getValue();
				// If we have reached the max or min, then move our time window
				// by one week.
				changingScrollbar = true;
				if (val <= scrollBar.getMinimum() && canScrollBack()) {
					scrollBar.setMinimum(scrollBar.getMinimum() - 1);
					scrollBar.setMaximum(scrollBar.getMaximum() - 1);
				}
				if (val >= scrollBar.getMaximum() - getNumWeeksToDisplay() && canScrollForward()) {
					scrollBar.setMinimum(scrollBar.getMinimum() + 1);
					scrollBar.setMaximum(scrollBar.getMaximum() + 1);
				}
				drawDateHint = true;
				fadeStep = 0;
				ActionListener a = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// We use fadeStep values (0-9) to indicate how
						// translucent
						// we should draw the date hint.
						fadeStep++;
						if (fadeStep > 9) {
							drawDateHint = false;
						} else {
							drawDateHint = true;
							timer.setInitialDelay(50);
							timer.restart();
						}
						drawArea.repaint();
					}
				};

				if (timer != null) {
					timer.stop();
					timer = null;
				}

				// erase the hint 2 seconds later
				timer = new Timer(3000, a);
				timer.start();

				changingScrollbar = false;
				setWeekOffset(val);
			}
		});
		this.add(this.scrollBar, BorderLayout.EAST);

		this.drawArea = new MonthPanel();
		this.add(drawArea, BorderLayout.CENTER);
		this.addMouseWheelListener(this);
	}

	/**
	 * Force a scroll ahead the specified number of weeks.
	 *
	 * @param numWeeksToIncrement
	 */
	public void incrementWeek(int numWeeksToIncrement) {
		int val = scrollBar.getValue();
		val += numWeeksToIncrement;
		if (val <= scrollBar.getMinimum() && canScrollBack()) {
			scrollBar.setMinimum(scrollBar.getMinimum() - 1);
			scrollBar.setMaximum(scrollBar.getMaximum() - 1);
		}
		if (val >= scrollBar.getMaximum() - getNumWeeksToDisplay() && canScrollForward()) {
			scrollBar.setMinimum(scrollBar.getMinimum() + 1);
			scrollBar.setMaximum(scrollBar.getMaximum() + 1);
		}
		// The scrollbar adjustment listener will take care of adjusting the
		// date
		// and repainting.
		scrollBar.setValue(val);
	}

	private boolean canScrollBack() {
		if (absoluteStart == null)
			return true;
		Calendar prevWeek = (Calendar) startDate.clone();
		prevWeek.setLenient(true);
		prevWeek.add(Calendar.DATE, -7);
		if (prevWeek.before(absoluteStart))
			return false;
		return true;
	}

	private boolean canScrollForward() {
		if (absoluteEnd == null)
			return true;
		Calendar endOfNewView = (Calendar) startDate.clone();
		endOfNewView.setLenient(true);
		endOfNewView.add(Calendar.DATE, getNumWeeksToDisplay() * 7);
		if (endOfNewView.after(absoluteEnd))
			return false;
		return true;
	}

	/**
	 * Force a scroll back the specified number of weeks.
	 *
	 * @param numWeeksToDecrement
	 */
	public void decrementWeek(int numWeeksToDecrement) {
		incrementWeek(-numWeeksToDecrement);
	}

	/**
	 * Set an absolute day that we should not display dates after. The user will
	 * not be allowed to scroll to weeks after this date. This overrides the
	 * default behavior where the user can scroll indefinitely and the UI will
	 * keep adding weeks to the display.
	 *
	 * @param absoluteStartDate
	 */
	public void setAbsoluteStartDay(Calendar absoluteStartDate) {
		absoluteStart = (Calendar) absoluteStartDate.clone();
		// Set to 12AM on the start of this week.
		absoluteStart.setLenient(true);
		while (absoluteStart.get(Calendar.DAY_OF_WEEK) % 7 != getFirstDayOfWeek() % 7) {
			absoluteStart.add(Calendar.DATE, -1);
		}
		absoluteStart.set(Calendar.HOUR_OF_DAY, 0);
		absoluteStart.set(Calendar.MINUTE, 0);
		absoluteStart.set(Calendar.SECOND, 0);
		absoluteStart.set(Calendar.MILLISECOND, 0);
		// If this affects the current display, repaint
		if (startDate.before(absoluteStart)) {
			startDate = (Calendar) absoluteStart.clone();
			repaint();
		}
	}

	/**
	 * Set an absolute day that we should not display dates before. The user
	 * will not be allowed to scroll to weeks before this date. This overrides
	 * the default behavior where the user can scroll indefinitely and the UI
	 * will keep adding weeks to the display.
	 *
	 * @param absoluteEndDate
	 */
	public void setAbsoluteEndDay(Calendar absoluteEndDate) {
		absoluteEnd = (Calendar) absoluteEndDate.clone();
		// First find the first day of the next week
		absoluteEnd.add(Calendar.DATE, 1);
		while (absoluteEnd.get(Calendar.DAY_OF_WEEK) % 7 != getFirstDayOfWeek() % 7) {
			absoluteEnd.add(Calendar.DATE, 1);
		}
		// Now go back a day to get to the last day of previous week
		absoluteEnd.add(Calendar.DATE, -1);
		// Set to 23:59:59 on the last day of this week.
		absoluteEnd.set(Calendar.HOUR_OF_DAY, 23);
		absoluteEnd.set(Calendar.MINUTE, 59);
		absoluteEnd.set(Calendar.SECOND, 59);
		absoluteEnd.set(Calendar.MILLISECOND, 999);
		// Now calc the week start for the same week
		absoluteEndWeekStart = (Calendar) absoluteEnd.clone();
		absoluteEndWeekStart.setLenient(true);
		while (absoluteEndWeekStart.get(Calendar.DAY_OF_WEEK) % 7 != getFirstDayOfWeek() % 7)
			absoluteEndWeekStart.add(Calendar.DATE, -1);
		absoluteEndWeekStart.set(Calendar.HOUR_OF_DAY, 0);
		absoluteEndWeekStart.set(Calendar.MINUTE, 0);
		absoluteEndWeekStart.set(Calendar.SECOND, 0);
		absoluteEndWeekStart.set(Calendar.MILLISECOND, 0);
		// If this affects the current display, repaint
		if (startDate.after(absoluteEnd)) {
			// Set to end date (which is Sunday 23:59:59), then move back to
			// first day
			// of that week (Monday)
			startDate = (Calendar) absoluteEndWeekStart.clone();
			repaint();
		}
	}

	/**
	 * Set the Font for the CalendarPanel. This will apply to the title font
	 * (where the current date range is displayed), the days of the month, the
	 * header (for weekday labels). Additionally, the event font will be two
	 * points smaller and the hint font (shown in the middle when you scroll
	 * will be 8 points larger).
	 */
	public void setFont(Font newFont) {
		// We may need to recalculate dimensions since the header height is
		// dependent
		// on the header font height.
		if (newFont != null) {
			super.setFont(newFont);
			if (this.drawArea != null) {
				// this.title.setFont ( newFont );
				this.drawArea.setFont(newFont);
				this.headerFont = newFont;
				this.eventFont = new Font(newFont.getFamily(), newFont.getStyle(), newFont.getSize() - 2);
				this.hintFont = new Font(newFont.getFamily(), newFont.getStyle(), newFont.getSize() + 8);
			}
			this.lastWidth = this.lastHeight = -1; // force resize calculation
			repaint();
		}
	}

	// Note that we should do the adjustment by day of year rather than week of
	// year, which would seem to be the logical choice. However, at the end of
	// the
	// year, you can have a week of year of 1 at the end of December. This
	// seems to muck up the calculations, so we still need to fix this bug.
	public void setWeekOffset(int weekOffset) {
		int[] weekdayTranslation = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
				Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY };

		Calendar c = Calendar.getInstance();
		c.setLenient(true);
		this.firstDayOfWeek = CalendarPanel.getFirstDayOfWeek();
		int currentWeek = c.get(Calendar.WEEK_OF_YEAR);
		// Set c to first day of the week
		c.set(Calendar.DAY_OF_WEEK, weekdayTranslation[this.firstDayOfWeek]);
		// Now move weekOffset weeks
		c.set(Calendar.WEEK_OF_YEAR, currentWeek + weekOffset);
		if (absoluteStart != null && c.before(absoluteStart)) {
			// don't allow this
			c.setTimeInMillis(absoluteStart.getTimeInMillis());
		}
		if (absoluteEnd != null && c.after(absoluteEnd)) {
			// don't allow this
			c.setTimeInMillis(absoluteEndWeekStart.getTimeInMillis());
		}

		this.startDate = Calendar.getInstance();
		this.startDate.setTimeInMillis(c.getTimeInMillis());

		// Update title to show dates displayed
		String label = monthNames[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.YEAR) + " - ";
		c.add(Calendar.DAY_OF_YEAR, 7 * numWeeksToDisplay - 1);
		label += monthNames[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
		this.title.setText(label);

		this.repaint();
	}

	private void updateTitle() {
		int[] weekdayTranslation = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
				Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY };
		Calendar c = (Calendar) startDate.clone();
		c.setLenient(true);
		this.firstDayOfWeek = CalendarPanel.getFirstDayOfWeek();
		int currentWeek = c.get(Calendar.WEEK_OF_YEAR);
		// Set c to first day of the week
		c.set(Calendar.DAY_OF_WEEK, weekdayTranslation[this.firstDayOfWeek]);
		// Now move weekOffset weeks
		c.set(Calendar.WEEK_OF_YEAR, currentWeek);

		this.startDate = Calendar.getInstance();
		this.startDate.setTimeInMillis(c.getTimeInMillis());
		// Update title to show dates displayed
		String label = monthNames[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.YEAR) + " - ";
		c.add(Calendar.DAY_OF_YEAR, 7 * numWeeksToDisplay - 1);
		label += monthNames[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
		this.title.setText(label);
	}

	public int getNumWeeksToDisplay() {
		return numWeeksToDisplay;
	}

	/**
	 * Set the number of weeks to display at once in the view (default is 5
	 * weeks).
	 *
	 * @param numWeeksToDisplay
	 */
	public void setNumWeeksToDisplay(int numWeeksToDisplay) {
		this.numWeeksToDisplay = numWeeksToDisplay;
		updateTitle();
		forceLayout = true;
	}

	/**
	 * Set the background colors for days of the month. Each month will
	 * alternate between the two colors.
	 *
	 * @param color1
	 *            The first color
	 * @param color2
	 *            The next color
	 */
	public void setBackgroundColors(Color color1, Color color2) {
		this.backgroundColor1 = color1;
		this.backgroundColor2 = color2;
	}

	/**
	 * Set the background color of the cell for the current date.
	 *
	 * @param color
	 *            The new background color
	 */
	public void setTodayBackgroundColor(Color color) {
		this.todayBackgroundColor = color;
	}

	/**
	 * The the text and background colors for the header where weekdays are
	 * displayed.
	 *
	 * @param headerForeground
	 *            New color for header text
	 * @param headerBackground
	 *            New background color for header
	 */
	public void setHeaderColors(Color headerForeground, Color headerBackground) {
		this.headerForeground = headerForeground;
		this.headerBackground = headerBackground;
	}

	private void handleResize(Graphics g) {
		this.lastWidth = drawArea.getWidth();
		this.lastHeight = drawArea.getHeight();
		this.headerHeight = g.getFontMetrics(headerFont).getHeight();

		this.cellWidth = (double) this.lastWidth / (double) 7;
		this.cellHeight = (double) (this.lastHeight - this.headerHeight) / (double) numWeeksToDisplay;

		columnX = new int[7];
		rowY = new int[5];

		for (int col = 0; col < 7; col++) {
			double x = this.cellWidth * (double) col;
			columnX[col] = (int) Math.floor(x);
		}

		for (int row = 0; row < 5; row++) {
			double y = this.cellHeight * (double) row;
			rowY[row] = this.headerHeight + (int) Math.floor(y);
		}
	}

	public static String formattedTime(int hour, int minute) {
		// TODO: support alternate time formats
		String strDateFormat = minute == 0 ? "ha" : "h:mma";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return sdf.format(c.getTime());
	}

	// Make sure that absolute start/end dates make sense and cover at least 5
	// weeks.
	private void doStartEndSanityCheck() {
		if (absoluteStart != null && absoluteEnd != null) {
			if (absoluteEnd.before(absoluteStart))
				throw new RuntimeException("absolute end date cannot before absolute start");
			// Make sure we have 5 weeks to display
			long duration = absoluteEndWeekStart.getTimeInMillis() - absoluteStart.getTimeInMillis();
			if (duration < 34 * 24 * 3600 * 1000L) {
				System.err.println("Absolute start and end must be at least 5 weeks apart.");
				// Bump out the end date
				Calendar newEnd = (Calendar) absoluteStart.clone();
				newEnd.setLenient(true);
				newEnd.add(Calendar.DATE, 34);
				setAbsoluteEndDay(newEnd);
			}
		}
	}

	public void paintMonth(Graphics g) {
		doStartEndSanityCheck();
		Color defaultColor = g.getColor();

		this.displayedEvents.clear();
		this.displayedDates.clear();

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (headerFont == null) {
			headerFont = g.getFont();
		}
		if (eventFont == null) {
			eventFont = new Font(headerFont.getFamily(), headerFont.getStyle(), headerFont.getSize() - 2);
			g.setFont(eventFont);
		}
		if (hintFont == null) {
			hintFont = new Font(headerFont.getFamily(), headerFont.getStyle(), headerFont.getSize() + 8);
		}

		if (this.lastWidth != drawArea.getWidth() || this.lastHeight != drawArea.getHeight() || forceLayout) {
			// component was resized. recalculate dimensions
			handleResize(g);
			forceLayout = false;
		}

		// Draw header
		g.setFont(headerFont);
		for (int i = 0; i < 7; i++) {
			g.setColor(this.headerBackground);
			g.fillRect(columnX[i], 0, i < 6 ? columnX[i + 1] - columnX[i] : (int) cellWidth, headerHeight);
			String text = weekdays[(firstDayOfWeek + i) % 7];
			int xOffset = (int) Math
					.floor((this.cellWidth - (double) g.getFontMetrics(headerFont).stringWidth(text)) / (double) 2);
			g.setColor(this.headerForeground);
			g.drawString(text, columnX[i] + xOffset, g.getFontMetrics(headerFont).getAscent());
		}

		// Draw grid
		g.setColor(gridColor);
		int maxX = columnX[6] + (int) this.cellWidth;
		int maxY = rowY[4] + (int) this.cellHeight;
		g.drawRect(0, 0, maxX, maxY);
		for (int wday = 1; wday < 7; wday++) {
			g.drawLine(columnX[wday], 0, columnX[wday], maxY);
		}
		for (int row = 0; row < 5; row++) {
			g.drawLine(0, rowY[row], maxX, rowY[row]);
		}

		// Draw dates including all the events
		g.setColor(defaultColor);
		Calendar c = Calendar.getInstance();
		c.setLenient(true);
		c.setTimeInMillis(startDate.getTimeInMillis());
		g.setFont(eventFont);
		for (int week = 0; week < numWeeksToDisplay; week++) {
			for (int col = 0; col < 7; col++) {
				int w = (col < 6) ? columnX[col + 1] - columnX[col] : (int) cellWidth;
				int h = (week < 4) ? rowY[week + 1] - rowY[week] : (int) cellHeight;
				boolean includeMonthName = c.get(Calendar.DAY_OF_MONTH) == 1 || (week == 0 && col == 0);
				Date d = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
				this.displayedDates.add(new DisplayedDate(d, new Rectangle(columnX[col], rowY[week], w, h)));
				drawDayOfMonth(g, c, includeMonthName, columnX[col], rowY[week], w, h);
				c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
			}
		}

		if (this.drawDateHint) {
			Font oldFont = g.getFont();
			StringBuffer hintBuf = new StringBuffer();
			// Display name of first full month in view
			if (this.startDate.get(Calendar.DAY_OF_MONTH) == 1) {
				hintBuf.append(monthNames[this.startDate.get(Calendar.MONTH)]);
				hintBuf.append(' ');
				hintBuf.append(this.startDate.get(Calendar.YEAR));
			} else {
				int mon = this.startDate.get(Calendar.MONTH) + 1;
				hintBuf.append(monthNames[mon % 12]);
				hintBuf.append(' ');
				if (mon == 12)
					hintBuf.append(this.startDate.get(Calendar.YEAR) + 1);
				else
					hintBuf.append(this.startDate.get(Calendar.YEAR));
			}
			String hint = hintBuf.toString();
			g.setFont(hintFont);
			FontMetrics fm = g.getFontMetrics();
			int w = fm.stringWidth(hint) + 10;
			int h = fm.getHeight() + 10;
			int x = (this.getWidth() - w) / 2;
			int y = (this.getHeight() - h) / 2;
			// Set the hint to be translucent
			if (fadeStep < 10) {
				Graphics2D g2d = (Graphics2D) g;
				Composite oldComp = g2d.getComposite();
				float alpha = 0.5f - ((float) fadeStep * 0.05f);
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);
				g.setColor(this.hintBackground);
				g.fillRoundRect(x, y, w, h, 10, 10);
				g.setColor(this.hintForeground);
				g.drawString(hint, x + 5, y + 5 + fm.getAscent());
				g2d.setComposite(oldComp);
			}
			g.setFont(oldFont);
		}

		drawEventPopup(g);
	}

	/**
	 * Draw the event details popup. This replaced the old behavior implemented
	 * with Swing's built-in ToolTip. We moved to this because of restrictions
	 * on how much you can customize the appearance of a ToolTip.
	 */
	public void drawEventPopup(Graphics g) {
		if (this.currentMouseOverEvent != null) {
			EventInstance evInst = this.currentMouseOverEvent.event;
			// Get event details to include in popup.
			List<String> textLines = new ArrayList<String>();
			String header = null;
			// If event is in upper half of panel, put popup above. Otherwise,
			// put it below.
			boolean above = (this.currentMouseOverEvent.rect.y > (this.getHeight() / 2));
			if (evInst.hasTime()) {
				header = CalendarPanel.formattedTime(evInst.getHour(), evInst.getMinute()) + " " + evInst.getTitle();
			} else {
				header = evInst.getTitle();
			}
			if (evInst.getLocation() != null) {
				textLines.addAll(formatter.formatLocation(evInst.getLocation()));
			}
			if (evInst.getDescription() != null) {
				// wrap long lines
				textLines.addAll(formatter.formatDescription(evInst.getDescription()));
			}
			FontMetrics fm = g.getFontMetrics();
			int w = 0, h = 0, x = 0, y = 0;
			w = fm.stringWidth(header);
			for (String s : textLines) {
				if (fm.stringWidth(s) > w)
					w = fm.stringWidth(s);
			}
			w += 4;
			h = fm.getHeight() * (1 + textLines.size()) + 4;
			x = this.currentMouseOverEvent.rect.x + (this.currentMouseOverEvent.rect.width / 2) - (w / 2);
			boolean recalcW = false;
			if (above) {
				y = this.currentMouseOverEvent.rect.y - h - 15;
				// If too tall, then remove some lines at the end...
				while (y < 1 && textLines.size() >= 2) {
					textLines.remove(textLines.size() - 1);
					textLines.remove(textLines.size() - 1);
					textLines.add("...");
					h = fm.getHeight() * (1 + textLines.size()) + 4;
					y = this.currentMouseOverEvent.rect.y - h - 15;
					recalcW = true;
				}
			} else {
				y = this.currentMouseOverEvent.rect.y + this.currentMouseOverEvent.rect.height + 15;
				// If too tall, then remove some lines at the end...
				while ((y + h + 8) > (this.getHeight() - 25) && textLines.size() >= 2) {
					textLines.remove(textLines.size() - 1);
					textLines.remove(textLines.size() - 1);
					textLines.add("...");
					h = fm.getHeight() * (1 + textLines.size()) + 4;
					recalcW = true;
				}
			}
			// Recalculate width if we removed text above
			if (recalcW) {
				w = fm.stringWidth(header);
				for (String s : textLines) {
					if (fm.stringWidth(s) > w)
						w = fm.stringWidth(s);
				}
				w += 4;
			}
			if (x < 5)
				x = 5;
			else if ((x + w) >= (this.drawArea.getWidth() - 5))
				x = this.drawArea.getWidth() - (w + 5);
			// Draw 8 lines of drop shadow. We do this buy drawing repeating
			// rounded
			// rectangles using the same alpha transparency setting. By doing
			// this,
			// they alpha values end up adding up since we draw on the same
			// location
			// multiple times.
			Graphics2D g2d = (Graphics2D) g;
			Composite oldComp = g2d.getComposite();
			Color shadow = Color.BLACK;
			for (int i = 0; i < 8; i++) {
				// float alpha = 0.1f + ( (float) i * 0.04f );
				float alpha = 0.04f;
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);
				g.setColor(shadow);
				g.fillRoundRect(x + 1 + i, y + 1 + (8 - i), w - 2 - (2 * i), h - 2, 12, 12);
				int n = 8 - i;
				alpha = 0.01f;
				alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);
				g.fillRoundRect(x - n, y - n, w + (2 * n), h + 2 * n, 30, 30);
			}
			g2d.setComposite(oldComp);
			Color color = evInst.getBackgroundColor();
			// If color is not dark enough contrast to white, then make it
			// darker
			if ((255 - color.getRed()) + (255 - color.getBlue()) + (255 - color.getGreen()) < 400) {
				color = new Color(color.getRed() * 2 / 3, color.getGreen() * 2 / 3, color.getBlue() * 2 / 3);
			}
			g.setColor(color);
			g.fillRoundRect(x, y, w, h, 8, 8);
			g.setColor(Color.WHITE);
			g.drawString(header, x + 2, y + fm.getHeight());
			Rectangle omitHeader = new Rectangle(x, y + fm.getHeight() + fm.getDescent(), w, h - fm.getHeight());
			g.setClip(omitHeader);
			g.setColor(Color.WHITE);
			g.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 8, 8);
			g.setClip(null);
			g.setColor(color);
			for (int i = 0; i < textLines.size(); i++) {
				String s = textLines.get(i);
				g.drawString(s, x + 2, y + (i + 2) * fm.getHeight());
			}
		}
	}

	/**
	 * Draw a single day of the month, including all the events for that date.
	 *
	 * @param g
	 * @param day
	 * @param showMonthName
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	protected void drawDayOfMonth(Graphics g, Calendar day, boolean showMonthName, int x, int y, int w, int h) {
		FontMetrics fm = g.getFontMetrics();
		String label;

		Color fg = g.getColor();
		Calendar today = Calendar.getInstance();
		Color bgColor;
		if (today.get(Calendar.YEAR) == day.get(Calendar.YEAR) && today.get(Calendar.MONTH) == day.get(Calendar.MONTH)
				&& today.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH)) {
			// Use the special background color for today.
			bgColor = this.todayBackgroundColor;
		} else {
			bgColor = day.get(Calendar.MONTH) % 2 == 0 ? backgroundColor1 : backgroundColor2;
		}

		drawDayOfMonthBackground(g, x + 1, y + 1, w - 1, h - 1, bgColor);
		g.setColor(fg);

		if (showMonthName)
			label = monthNames[day.get(Calendar.MONTH)] + " " + day.get(Calendar.DAY_OF_MONTH);
		else
			label = "" + day.get(Calendar.DAY_OF_MONTH);
		int labelW = g.getFontMetrics().stringWidth(label);
		g.drawString(label, x + w - labelW - 1, y + fm.getAscent());

		if (this.repository != null) {
			List<EventInstance> events = this.repository.getEventInstancesForDate(day.get(Calendar.YEAR),
					day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH));
			if (events != null) {
				Collections.sort(events);
				boolean dateIsSelected = this.selectedDate != null && this.selectedDate.year == day.get(Calendar.YEAR)
						&& this.selectedDate.month == (day.get(Calendar.MONTH) + 1)
						&& this.selectedDate.day == day.get(Calendar.DAY_OF_MONTH);
				int startY = y + fm.getHeight();
				// Calculate how to layout the events for this date. Normally,
				// the
				// events will just be shown in a single column vertically.
				// However, if
				// there are too many events to fit in the given space, we will
				// have to
				// use more than one column.
				int visibleRows = (h - fm.getHeight()) / (fm.getHeight() + (1 + CELL_MARGIN));
				int cols = 1;
				while (cols * visibleRows < events.size())
					cols++;
				int colWidth = w / cols;
				for (int i = 0; i < events.size(); i++) {
					int thisCol = cols == 1 ? 0 : (i % cols);
					int thisRow = cols == 1 ? i : (i / cols);
					EventInstance e = events.get(i);
					Rectangle rect = new Rectangle(x + CELL_MARGIN + (thisCol * colWidth),
							startY + ((fm.getHeight() + CELL_MARGIN) * thisRow), colWidth - (2 * CELL_MARGIN),
							fm.getHeight());
					drawMonthViewEvent(g, rect, e, dateIsSelected && i == this.selectedItemInd);
					DisplayedEvent de = new DisplayedEvent(e, rect, i);
					this.displayedEvents.add(de);
				}
			}
		}
		g.setColor(fg);
	}

	protected static String formatTime(int hour, int minute, int second) {
		String strDateFormat = null;
		if (second > 0)
			strDateFormat = "h:mm:ssa";
		else if (minute > 0)
			strDateFormat = "h:mma";
		else
			strDateFormat = "ha";
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return sdf.format(c.getTime());
		/*
		 * StringBuffer sb = new StringBuffer (); if ( hour == 0 || hour == 12 )
		 * sb.append ( "12" ); else if ( hour > 12 ) sb.append ( hour % 12 );
		 * else sb.append ( hour ); sb.append ( ':' ); if ( minute < 10 )
		 * sb.append ( '0' ); sb.append ( minute ); if ( hour < 12 ) sb.append (
		 * "am" ); else sb.append ( "pm" ); return sb.toString ();
		 */
	}

	public void drawDayOfMonthBackground(Graphics g, int x, int y, int w, int h, Color c) {
		g.setColor(c);
		g.fillRect(x, y, w, h);
	}

	protected void drawMonthViewEvent(Graphics g, Rectangle r, EventInstance event, boolean isSelected) {
		Color c = g.getColor();
		g.setColor(event.getBackgroundColor());
		int arclen = r.height;
		if (isSelected) {
			// TODO: if selection color is too close to border color,
			// we may want to change the selection color automatically.
			// Or maybe add an animation/blink for drawing the selection.
			g.setColor(this.selectionColor);
			g.drawRoundRect(r.x - 1, r.y - 1, r.width + 2, r.height + 2, arclen + 2, arclen + 2);
		}
		g.setColor(event.getBackgroundColor());
		g.fillRoundRect(r.x, r.y, r.width, r.height, arclen, arclen);
		g.setColor(event.getBorderColor());
		g.drawRoundRect(r.x, r.y, r.width, r.height, arclen, arclen);
		g.setClip(r.x + 1, r.y + 1, r.width - 2, r.height - 3);
		g.setColor(event.getForegroundColor());
		String text;
		if (event.hasTime() && this.showTime) {
			text = formatTime(event.getHour(), event.getMinute(), event.getSecond()) + " " + event.getTitle();
		} else {
			text = event.getTitle();
		}
		g.drawString(text, r.x + 3, r.y + g.getFontMetrics().getAscent());
		g.setColor(c);
		// remove clip
		g.setClip(null);
	}

	public boolean getShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public boolean getAllowsEventSelection() {
		return allowsEventSelection;
	}

	public void setAllowsEventSelection(boolean allowsEventSelection) {
		this.allowsEventSelection = allowsEventSelection;
	}

	public void addSelectionListener(CalendarPanelSelectionListener l) {
		this.selectionListeners.add(l);
	}

	public EventInstance getSelectedEvent() {
		if (this.selectedDate == null)
			return null;
		List<EventInstance> eventsForDate = this.repository.getEventInstancesForDate(this.selectedDate.year,
				this.selectedDate.month, this.selectedDate.day);
		if (eventsForDate != null)
			Collections.sort(eventsForDate);
		// System.out.println ( "Found " + eventsForDate.size ()
		// + " events for date: " + this.selectedDate.getMonth () + "/"
		// + this.selectedDate.getDay () );
		if (this.selectedItemInd >= 0 && this.selectedItemInd < eventsForDate.size()) {
			EventInstance eventInstance = eventsForDate.get(this.selectedItemInd);
			return eventInstance;
		}
		return null;
	}

	/**
	 * Clear any user selection. This should be done anytime the contents of
	 * what is being displayed is modified. For example, if a calendar is added
	 * to the display or removed, this should be called. The event selected by
	 * the user is internally stored by date and index number for that date, so
	 * anything that may change the number of events displayed on a particular
	 * date could cause the selection to "move", so the app should call this
	 * method to clear the selection.
	 */
	public void clearSelection() {
		boolean doRepaint = (this.selectedDate != null && this.selectedItemInd >= 0);
		this.selectedDate = null;
		this.selectedItemInd = -1;
		if (doRepaint)
			repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent e1) {
		int notches = e1.getWheelRotation();
		this.scrollBar.setValue(this.scrollBar.getValue() + notches);
	}

}
