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
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Note: this class is coded using Java 1.2. So, don't add any Java 1.4/1.5/1.6
 * dependencies in here without good reason.
 * 
 * @see CalendarDataRepository
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class CalendarPanel extends JPanel implements MouseWheelListener {
	private static final long serialVersionUID = 1000L;
	CalendarDataRepository repository;
	JLabel title;
	JPanel drawArea;
	JScrollBar scrollBar;
	Calendar startDate; // Date of first day displayed
	int firstDayOfWeek; // Day of week that week starts on (SUNDAY, MONDAY, etc.)
	Color backgroundColor1, backgroundColor2;
	Color todayBackgroundColor;
	Color gridColor;
	Color selectionColor;
	Color headerForeground, headerBackground;
	Color hintBackground, hintForeground;
	Font headerFont = null, eventFont = null;
	Font hintFont = null;
	int lastWidth = -1, lastHeight = -1;
	double cellWidth = 100, cellHeight = 100;
	int headerHeight = 10;
	int[] columnX;
	int[] rowY;
	final static int NUM_WEEKS_TO_DISPLAY = 5;
	// TODO: I18N
	final static String[] weekdays = { "Sunday", "Monday", "Tuesday",
	    "Wednesday", "Thursday", "Friday", "Saturday" };
	// TODO: I18N
	final static String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May",
	    "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	boolean changingScrollbar = false;
	int CELL_MARGIN = 2;
	Vector displayedEvents;
	Vector displayedDates;
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
	private Vector selectionListeners;
	DisplayedEvent currentMouseOverEvent = null;

	class Date {
		public int year, month, day;

		public Date(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}

	class DisplayedEvent {
		EventInstance event;
		Rectangle rect;
		int eventNoForDay;

		public DisplayedEvent(EventInstance event, Rectangle rect, int eventNo) {
			this.event = event;
			this.rect = rect;
			this.eventNoForDay = eventNo;
		}

		public boolean isSameEvent ( Object o ) {
			if ( ! ( o instanceof DisplayedEvent ) )
				return false;
			DisplayedEvent e2 = (DisplayedEvent) o;
			return ( rect.equals ( e2.rect ) );
		}
	}

	class DisplayedDate {
		Date date;
		Rectangle rect;

		public DisplayedDate(Date date, Rectangle rect) {
			this.date = date;
			this.rect = rect;
		}
	}

	class MonthPanel extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1000L;
		DisplayedEvent lastMouseEvent = null;

		public MonthPanel() {
			super ();
			this.addMouseListener ( this );
			this.addMouseMotionListener ( this );
		}

		public String formattedTime ( int hour, int minute ) {
			// TODO: support alternate formats
			StringBuffer ret = new StringBuffer ();
			String ampm = null;
			if ( hour < 12 ) {
				// AM
				ampm = "am";
			} else {
				// PM
				hour %= 12;
				ampm = "pm";
			}
			if ( hour == 0 )
				hour = 12;
			ret.append ( hour );
			ret.append ( ':' );
			if ( minute < 10 )
				ret.append ( '0' );
			ret.append ( minute );
			ret.append ( ampm );
			return ret.toString ();
		}

		protected DisplayedEvent getEventForPosition ( int x, int y ) {
			for ( int i = 0; displayedEvents != null && i < displayedEvents.size (); i++ ) {
				DisplayedEvent de = (DisplayedEvent) displayedEvents.elementAt ( i );
				if ( x >= de.rect.x && x <= de.rect.x + de.rect.width && y >= de.rect.y
				    && y <= de.rect.y + de.rect.height ) {
					return de;
				}
			}
			return null;
		}

		public DisplayedEvent getMouseOverEvent ( MouseEvent e ) {
			return getEventForPosition ( e.getX (), e.getY () );
		}

		public void paint ( Graphics g ) {
			super.paint ( g );
			paintMonth ( g );
		}

		private DisplayedEvent getEventForMouseEvent ( MouseEvent e1 ) {
			for ( int i = 0; displayedEvents != null && i < displayedEvents.size (); i++ ) {
				DisplayedEvent de = (DisplayedEvent) displayedEvents.elementAt ( i );
				if ( e1.getX () >= de.rect.x && e1.getX () <= de.rect.x + de.rect.width
				    && e1.getY () >= de.rect.y
				    && e1.getY () <= de.rect.y + de.rect.height ) {
					// Found item
					return de;
				}
			}
			return null; // not found
		}

		public void mouseClicked ( MouseEvent e1 ) {
			boolean wasSelected = selectedDate != null;
			boolean doRepaint = false;
			selectedDate = null;
			selectedItemInd = -1;
			DisplayedEvent selectedEvent = null;
			if ( currentMouseOverEvent != null )
				doRepaint = true;
			currentMouseOverEvent = null; // Don't display event popup
			for ( int i = 0; displayedEvents != null && i < displayedEvents.size (); i++ ) {
				DisplayedEvent de = (DisplayedEvent) displayedEvents.elementAt ( i );
				if ( e1.getX () >= de.rect.x && e1.getX () <= de.rect.x + de.rect.width
				    && e1.getY () >= de.rect.y
				    && e1.getY () <= de.rect.y + de.rect.height ) {
					// Found item
					selectedDate = new Date ( de.event.getYear (), de.event.getMonth (),
					    de.event.getDayOfMonth () );
					if ( getAllowsEventSelection () ) {
						selectedItemInd = de.eventNoForDay;
						selectedEvent = de;
					}
					break;
				}
			}
			if ( selectedEvent == null ) {
				for ( int i = 0; displayedDates != null && i < displayedDates.size (); i++ ) {
					DisplayedDate dd = (DisplayedDate) displayedDates.elementAt ( i );
					if ( e1.getX () >= dd.rect.x
					    && e1.getX () <= dd.rect.x + dd.rect.width
					    && e1.getY () >= dd.rect.y
					    && e1.getY () <= dd.rect.y + dd.rect.height ) {
						// Found date
						selectedDate = dd.date;
					}
				}
			}
			if ( wasSelected ) {
				for ( int i = 0; i < selectionListeners.size (); i++ ) {
					CalendarPanelSelectionListener l = (CalendarPanelSelectionListener) selectionListeners
					    .elementAt ( i );
					l.eventUnselected ();
				}
				doRepaint = true;
			}
			if ( selectedDate != null && selectedEvent != null ) {
				for ( int i = 0; i < selectionListeners.size (); i++ ) {
					CalendarPanelSelectionListener l = (CalendarPanelSelectionListener) selectionListeners
					    .elementAt ( i );
					l.eventSelected ( selectedEvent.event );
				}
				doRepaint = true;
			}
			// If this is a double-click, then invoke the l.eventDoubleClicked method
			if ( e1.getClickCount () == 2 && selectedDate != null
			    && selectedEvent != null ) {
				for ( int i = 0; i < selectionListeners.size (); i++ ) {
					CalendarPanelSelectionListener l = (CalendarPanelSelectionListener) selectionListeners
					    .elementAt ( i );
					l.eventDoubleClicked ( selectedEvent.event );
				}
			} else if ( e1.getClickCount () == 2 && selectedDate != null
			    && selectedEvent == null ) {
				// Date double-clicked
				for ( int i = 0; i < selectionListeners.size (); i++ ) {
					CalendarPanelSelectionListener l = (CalendarPanelSelectionListener) selectionListeners
					    .elementAt ( i );
					l.dateDoubleClicked ( selectedDate.year, selectedDate.month,
					    selectedDate.day );
				}
			}
			// System.out.println ( "sel event: " + selectedEvent.event
			// + ", selectedItemInd=" + selectedItemInd );
			if ( doRepaint )
				repaint ();
		}

		public void mouseEntered ( MouseEvent e1 ) {
			this.lastMouseEvent = getEventForMouseEvent ( e1 );
			currentMouseOverEvent = getMouseOverEvent ( e1 );
			if ( currentMouseOverEvent != null )
				repaint ();
		}

		public void mouseExited ( MouseEvent e1 ) {
			this.lastMouseEvent = null;
			if ( currentMouseOverEvent != null )
				repaint ();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mousePressed ( MouseEvent e1 ) {
			if ( currentMouseOverEvent != null )
				repaint ();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseReleased ( MouseEvent e1 ) {
			if ( currentMouseOverEvent != null )
				repaint ();
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseDragged ( MouseEvent e1 ) {
			currentMouseOverEvent = null; // Display event popup?
		}

		public void mouseMoved ( MouseEvent e1 ) {
			this.lastMouseEvent = getEventForMouseEvent ( e1 );
			// Display event popup?
			DisplayedEvent oldEvent = currentMouseOverEvent;
			currentMouseOverEvent = getMouseOverEvent ( e1 );
			if ( ( oldEvent == null && currentMouseOverEvent != null )
			    || ( oldEvent != null && currentMouseOverEvent == null ) )
				repaint ();
			else if ( oldEvent != null && currentMouseOverEvent != null
			    && !oldEvent.isSameEvent ( currentMouseOverEvent ) )
				repaint ();
		}
	}

	private int getFirstDayOfWeek () {
		switch ( Calendar.getInstance ().getFirstDayOfWeek () ) {
			case Calendar.SUNDAY:
				return ( 0 );
			case Calendar.MONDAY:
				return ( 1 );
			case Calendar.TUESDAY:
				return ( 2 );
			case Calendar.WEDNESDAY:
				return ( 3 );
			case Calendar.THURSDAY:
				return ( 4 );
			case Calendar.FRIDAY:
				return ( 5 );
			case Calendar.SATURDAY:
				return ( 6 );
		}
		return ( -1 );
	}

	public CalendarPanel(CalendarDataRepository repository) {
		super ();
		this.repository = repository;
		this.firstDayOfWeek = getFirstDayOfWeek ();
		this.selectionListeners = new Vector ();

		this.backgroundColor1 = new Color ( 232, 232, 232 );
		this.backgroundColor2 = new Color ( 212, 212, 212 );
		this.todayBackgroundColor = new Color ( 255, 255, 212 );
		this.headerForeground = Color.BLUE;
		this.headerBackground = Color.WHITE;
		this.gridColor = Color.BLACK;
		this.selectionColor = Color.RED;
		this.hintBackground = Color.DARK_GRAY;
		this.hintForeground = Color.white;
		this.displayedEvents = new Vector ();
		this.displayedDates = new Vector ();

		createUI ();

		this.setWeekOffset ( 0 );
	}

	protected void createUI () {
		this.setLayout ( new BorderLayout () );
		JPanel titlePanel = new JPanel ();
		titlePanel.setLayout ( new BorderLayout () );
		JButton todayButton = new JButton ( "Today" );
		todayButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Scroll calendar back to current date.
				setWeekOffset ( 0 );
			}
		} );
		titlePanel.add ( todayButton, BorderLayout.EAST );
		this.title = new JLabel ( "Calendar", JLabel.CENTER );
		Font f = this.title.getFont ();
		this.title
		    .setFont ( new Font ( f.getFamily (), Font.BOLD, f.getSize () + 4 ) );
		titlePanel.add ( title, BorderLayout.CENTER );
		this.add ( titlePanel, BorderLayout.NORTH );
		// ScrollBar values: 0 = current week, -N = N week before, +N = N weeks
		// after
		this.scrollBar = new JScrollBar ( JScrollBar.VERTICAL, 0, 5, -52, 52 );
		this.scrollBar.addAdjustmentListener ( new AdjustmentListener () {
			public void adjustmentValueChanged ( AdjustmentEvent e ) {
				// Ignore events caused by changing scrollbar min/max values below
				// or else we will get a stack overflow.
				if ( changingScrollbar )
					return;
				int val = e.getValue ();
				// If we have reached the max or min, then move our time window
				// by one week.
				changingScrollbar = true;
				if ( val <= scrollBar.getMinimum () ) {
					scrollBar.setMinimum ( scrollBar.getMinimum () - 1 );
					scrollBar.setMaximum ( scrollBar.getMaximum () - 1 );
				}
				if ( val >= scrollBar.getMaximum () - 5 ) {
					scrollBar.setMinimum ( scrollBar.getMinimum () + 1 );
					scrollBar.setMaximum ( scrollBar.getMaximum () + 1 );
				}
				drawDateHint = true;
				fadeStep = 0;
				ActionListener a = new ActionListener () {
					public void actionPerformed ( ActionEvent e ) {
						// We use fadeStep values (0-9) to indicate how translucent
						// we should draw the date hint.
						fadeStep++;
						if ( fadeStep > 9 ) {
							drawDateHint = false;
						} else {
							drawDateHint = true;
							timer.setInitialDelay ( 50 );
							timer.restart ();
						}
						drawArea.repaint ();
					}
				};

				if ( timer != null ) {
					timer.stop ();
					timer = null;
				}

				// erase the hint 2 seconds later
				timer = new Timer ( 3000, a );
				timer.start ();

				changingScrollbar = false;
				setWeekOffset ( val );
			}
		} );
		this.add ( this.scrollBar, BorderLayout.EAST );

		this.drawArea = new MonthPanel ();
		this.add ( drawArea, BorderLayout.CENTER );
		this.addMouseWheelListener ( this );
	}

	// Note that we should do the adjustment by day of year rather than week of
	// year, which would seem to be the logical choice. However, at the end of the
	// year, you can have a week of year of 1 at the end of December. This
	// seems to muck up the calculations, so we still need to fix this bug.
	public void setWeekOffset ( int weekOffset ) {
		int[] weekdayTranslation = { Calendar.SUNDAY, Calendar.MONDAY,
		    Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
		    Calendar.FRIDAY, Calendar.SATURDAY };
		Calendar c = Calendar.getInstance ();
		c.setLenient ( true );
		this.firstDayOfWeek = getFirstDayOfWeek ();
		int currentWeek = c.get ( Calendar.WEEK_OF_YEAR );
		c.set ( Calendar.DAY_OF_WEEK, weekdayTranslation[this.firstDayOfWeek] );
		c.set ( Calendar.WEEK_OF_YEAR, currentWeek + weekOffset );

		this.startDate = Calendar.getInstance ();
		this.startDate.setTimeInMillis ( c.getTimeInMillis () );

		// Update title to show dates displayed
		String label = monthNames[c.get ( Calendar.MONTH )] + " "
		    + c.get ( Calendar.DAY_OF_MONTH ) + " " + c.get ( Calendar.YEAR )
		    + " - ";
		c.add ( Calendar.DAY_OF_YEAR, 34 );
		label += monthNames[c.get ( Calendar.MONTH )] + " "
		    + c.get ( Calendar.DAY_OF_MONTH ) + " " + c.get ( Calendar.YEAR );
		this.title.setText ( label );

		this.repaint ();
	}

	/**
	 * Set the background colors for days of the month. Each month will alternate
	 * between the two colors.
	 * 
	 * @param color1
	 *          The first color
	 * @param color2
	 *          The next color
	 */
	public void setBackgroundColors ( Color color1, Color color2 ) {
		this.backgroundColor1 = color1;
		this.backgroundColor2 = color2;
	}

	/**
	 * Set the background color of the cell for the current date.
	 * 
	 * @param color
	 *          The new background color
	 */
	public void setTodayBackgroundColor ( Color color ) {
		this.todayBackgroundColor = color;
	}

	/**
	 * The the text and background colors for the header where weekdays are
	 * displayed.
	 * 
	 * @param headerForeground
	 *          New color for header text
	 * @param headerBackground
	 *          New background color for header
	 */
	public void setHeaderColors ( Color headerForeground, Color headerBackground ) {
		this.headerForeground = headerForeground;
		this.headerBackground = headerBackground;
	}

	private void handleResize ( Graphics g ) {
		this.lastWidth = drawArea.getWidth ();
		this.lastHeight = drawArea.getHeight ();
		this.headerHeight = g.getFontMetrics ( headerFont ).getHeight ();

		this.cellWidth = (double) this.lastWidth / (double) 7;
		this.cellHeight = (double) ( this.lastHeight - this.headerHeight )
		    / (double) NUM_WEEKS_TO_DISPLAY;

		columnX = new int[7];
		rowY = new int[5];

		for ( int col = 0; col < 7; col++ ) {
			double x = this.cellWidth * (double) col;
			columnX[col] = (int) Math.floor ( x );
		}

		for ( int row = 0; row < 5; row++ ) {
			double y = this.cellHeight * (double) row;
			rowY[row] = this.headerHeight + (int) Math.floor ( y );
		}
	}

	public String formattedTime ( int hour, int minute ) {
		// TODO: support alternate formats
		StringBuffer ret = new StringBuffer ();
		String ampm = null;
		if ( hour < 12 ) {
			// AM
			ampm = "am";
		} else {
			// PM
			hour %= 12;
			ampm = "pm";
		}
		if ( hour == 0 )
			hour = 12;
		ret.append ( hour );
		ret.append ( ':' );
		if ( minute < 10 )
			ret.append ( '0' );
		ret.append ( minute );
		ret.append ( ampm );
		return ret.toString ();
	}

	public void paintMonth ( Graphics g ) {
		Color defaultColor = g.getColor ();

		this.displayedEvents.clear ();
		this.displayedDates.clear ();

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint ( RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON );

		if ( headerFont == null ) {
			headerFont = g.getFont ();
		}
		if ( eventFont == null ) {
			eventFont = new Font ( headerFont.getFamily (), headerFont.getStyle (),
			    headerFont.getSize () - 2 );
			g.setFont ( eventFont );
		}
		if ( hintFont == null ) {
			hintFont = new Font ( headerFont.getFamily (), headerFont.getStyle (),
			    headerFont.getSize () + 8 );
		}

		if ( this.lastWidth != drawArea.getWidth ()
		    || this.lastHeight != drawArea.getHeight () ) {
			// component was resized. recalculate dimensions
			handleResize ( g );
		}

		// Draw header
		g.setFont ( headerFont );
		for ( int i = 0; i < 7; i++ ) {
			g.setColor ( this.headerBackground );
			g.fillRect ( columnX[i], 0, i < 6 ? columnX[i + 1] - columnX[i]
			    : (int) cellWidth, headerHeight );
			String text = weekdays[ ( firstDayOfWeek + i ) % 7];
			int xOffset = (int) Math.floor ( ( this.cellWidth - (double) g
			    .getFontMetrics ( headerFont ).stringWidth ( text ) )
			    / (double) 2 );
			g.setColor ( this.headerForeground );
			g.drawString ( text, columnX[i] + xOffset, g.getFontMetrics ( headerFont )
			    .getAscent () );
		}

		// Draw grid
		g.setColor ( gridColor );
		int maxX = columnX[6] + (int) this.cellWidth;
		int maxY = rowY[4] + (int) this.cellHeight;
		g.drawRect ( 0, 0, maxX, maxY );
		for ( int wday = 1; wday < 7; wday++ ) {
			g.drawLine ( columnX[wday], 0, columnX[wday], maxY );
		}
		for ( int row = 0; row < 5; row++ ) {
			g.drawLine ( 0, rowY[row], maxX, rowY[row] );
		}

		// Draw dates including all the events
		g.setColor ( defaultColor );
		Calendar c = Calendar.getInstance ();
		c.setLenient ( true );
		c.setTimeInMillis ( startDate.getTimeInMillis () );
		g.setFont ( eventFont );
		for ( int week = 0; week < 5; week++ ) {
			for ( int col = 0; col < 7; col++ ) {
				int w = ( col < 6 ) ? columnX[col + 1] - columnX[col] : (int) cellWidth;
				int h = ( week < 4 ) ? rowY[week + 1] - rowY[week] : (int) cellHeight;
				boolean includeMonthName = c.get ( Calendar.DAY_OF_MONTH ) == 1
				    || ( week == 0 && col == 0 );
				Date d = new Date ( c.get ( Calendar.YEAR ),
				    c.get ( Calendar.MONTH ) + 1, c.get ( Calendar.DAY_OF_MONTH ) );
				this.displayedDates.addElement ( new DisplayedDate ( d, new Rectangle (
				    columnX[col], rowY[week], w, h ) ) );
				drawDayOfMonth ( g, c, includeMonthName, columnX[col], rowY[week], w, h );
				c.set ( Calendar.DAY_OF_YEAR, c.get ( Calendar.DAY_OF_YEAR ) + 1 );
			}
		}

		if ( this.drawDateHint ) {
			Font oldFont = g.getFont ();
			StringBuffer hintBuf = new StringBuffer ();
			// Display name of first full month in view
			if ( this.startDate.get ( Calendar.DAY_OF_MONTH ) == 1 ) {
				hintBuf.append ( monthNames[this.startDate.get ( Calendar.MONTH )] );
				hintBuf.append ( ' ' );
				hintBuf.append ( this.startDate.get ( Calendar.YEAR ) );
			} else {
				int mon = this.startDate.get ( Calendar.MONTH ) + 1;
				hintBuf.append ( monthNames[mon % 12] );
				hintBuf.append ( ' ' );
				if ( mon == 12 )
					hintBuf.append ( this.startDate.get ( Calendar.YEAR ) + 1 );
				else
					hintBuf.append ( this.startDate.get ( Calendar.YEAR ) );
			}
			String hint = hintBuf.toString ();
			g.setFont ( hintFont );
			FontMetrics fm = g.getFontMetrics ();
			int w = fm.stringWidth ( hint ) + 10;
			int h = fm.getHeight () + 10;
			int x = ( this.getWidth () - w ) / 2;
			int y = ( this.getHeight () - h ) / 2;
			// Set the hint to be translucent
			if ( fadeStep < 10 ) {
				Graphics2D g2d = (Graphics2D) g;
				Composite oldComp = g2d.getComposite ();
				float alpha = 0.5f - ( (float) fadeStep * 0.05f );
				Composite alphaComp = AlphaComposite.getInstance (
				    AlphaComposite.SRC_OVER, alpha );
				g2d.setComposite ( alphaComp );
				g.setColor ( this.hintBackground );
				g.fillRoundRect ( x, y, w, h, 10, 10 );
				g.setColor ( this.hintForeground );
				g.drawString ( hint, x + 5, y + 5 + fm.getAscent () );
				g2d.setComposite ( oldComp );
			}
			g.setFont ( oldFont );
		}

		drawEventPopup ( g );
	}

	/**
	 * Draw the event details popup. This replaced the old behavior implemented
	 * with Swing's built-in ToolTip. We moved to this because of restrictions on
	 * how much you can customize the appearance of a ToolTip.
	 */
	public void drawEventPopup ( Graphics g ) {
		if ( this.currentMouseOverEvent != null ) {
			EventInstance evInst = this.currentMouseOverEvent.event;
			// Get event details to include in popup.
			Vector textLines = new Vector ();
			String header = null;
			// If event is in upper half of panel, put popup above. Otherwise,
			// put it below.
			boolean above = ( this.currentMouseOverEvent.rect.y > ( this.getHeight () / 2 ) );
			if ( evInst.hasTime () ) {
				header = formattedTime ( evInst.getHour (), evInst.getMinute () ) + " "
				    + evInst.getTitle ();
			} else {
				header = evInst.getTitle ();
			}
			if ( evInst.getLocation () != null ) {
				textLines.addElement ( "Location: " + evInst.getLocation () );
			}
			if ( evInst.getDescription () != null ) {
				// wrap long lines
				textLines.addAll ( Utils.wrapLines ( evInst.getDescription (), 50 ) );
			}
			FontMetrics fm = g.getFontMetrics ();
			int w = 0, h = 0, x = 0, y = 0;
			w = fm.stringWidth ( header );
			for ( int i = 0; i < textLines.size (); i++ ) {
				String s = (String) textLines.elementAt ( i );
				if ( fm.stringWidth ( s ) > w )
					w = fm.stringWidth ( s );
			}
			w += 4;
			h = fm.getHeight () * ( 1 + textLines.size () ) + 4;
			x = this.currentMouseOverEvent.rect.x
			    + ( this.currentMouseOverEvent.rect.width / 2 ) - ( w / 2 );
			boolean recalcW = false;
			if ( above ) {
				y = this.currentMouseOverEvent.rect.y - h - 15;
				// If too tall, then remove some lines at the end...
				while ( y < 1 && textLines.size () >= 2 ) {
					textLines.setSize ( textLines.size () - 2 );
					textLines.addElement ( "..." );
					h = fm.getHeight () * ( 1 + textLines.size () ) + 4;
					y = this.currentMouseOverEvent.rect.y - h - 15;
					recalcW = true;
				}
			} else {
				y = this.currentMouseOverEvent.rect.y
				    + this.currentMouseOverEvent.rect.height + 15;
				// If too tall, then remove some lines at the end...
				while ( ( y + h + 8 ) > ( this.getHeight () - 25 )
				    && textLines.size () >= 2 ) {
					textLines.setSize ( textLines.size () - 2 );
					textLines.addElement ( "..." );
					h = fm.getHeight () * ( 1 + textLines.size () ) + 4;
					recalcW = true;
				}
			}
			// Recalculate width if we removed text above
			if ( recalcW ) {
				w = fm.stringWidth ( header );
				for ( int i = 0; i < textLines.size (); i++ ) {
					String s = (String) textLines.elementAt ( i );
					if ( fm.stringWidth ( s ) > w )
						w = fm.stringWidth ( s );
				}
				w += 4;
			}
			if ( x < 5 )
				x = 5;
			else if ( ( x + w ) >= ( this.drawArea.getWidth () - 5 ) )
				x = this.drawArea.getWidth () - ( w + 5 );
			// Draw 8 lines of drop shadow. We do this buy drawing repeating rounded
			// rectangles using the same alpha transparency setting. By doing this,
			// they alpha values end up adding up since we draw on the same location
			// multiple times.
			Graphics2D g2d = (Graphics2D) g;
			Composite oldComp = g2d.getComposite ();
			Color shadow = Color.BLACK;
			for ( int i = 0; i < 8; i++ ) {
				// float alpha = 0.1f + ( (float) i * 0.04f );
				float alpha = 0.15f;
				Composite alphaComp = AlphaComposite.getInstance (
				    AlphaComposite.SRC_OVER, alpha );
				g2d.setComposite ( alphaComp );
				g.setColor ( shadow );
				g.fillRoundRect ( x + 1 + i, y + 1 + ( 8 - i ), w - 2 - ( 2 * i ),
				    h - 2, 12, 12 );
			}
			g2d.setComposite ( oldComp );
			Color color = evInst.getBackgroundColor ();
			// If color is not dark enough contrast to white, then make it darker
			if ( color.getRed () > 100 && color.getBlue () > 100
			    && color.getGreen () > 100 ) {
				color = new Color ( color.getRed () / 2, color.getGreen () / 2, color
				    .getBlue () / 2 );
			}
			g.setColor ( color );
			g.fillRoundRect ( x, y, w, h, 8, 8 );
			g.setColor ( Color.WHITE );
			g.drawString ( header, x + 2, y + fm.getHeight () );
			Rectangle omitHeader = new Rectangle ( x, y + fm.getHeight ()
			    + fm.getDescent (), w, h - fm.getHeight () );
			g.setClip ( omitHeader );
			g.setColor ( Color.WHITE );
			g.fillRoundRect ( x + 1, y + 1, w - 2, h - 2, 8, 8 );
			g.setClip ( null );
			g.setColor ( color );
			for ( int i = 0; i < textLines.size (); i++ ) {
				String s = (String) textLines.elementAt ( i );
				g.drawString ( s, x + 2, y + ( i + 2 ) * fm.getHeight () );
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
	protected void drawDayOfMonth ( Graphics g, Calendar day,
	    boolean showMonthName, int x, int y, int w, int h ) {
		FontMetrics fm = g.getFontMetrics ();
		String label;

		Color fg = g.getColor ();
		Calendar today = Calendar.getInstance ();
		Color bgColor;
		if ( today.get ( Calendar.YEAR ) == day.get ( Calendar.YEAR )
		    && today.get ( Calendar.MONTH ) == day.get ( Calendar.MONTH )
		    && today.get ( Calendar.DAY_OF_MONTH ) == day
		        .get ( Calendar.DAY_OF_MONTH ) ) {
			// Use the special background color for today.
			bgColor = this.todayBackgroundColor;
		} else {
			bgColor = day.get ( Calendar.MONTH ) % 2 == 0 ? backgroundColor1
			    : backgroundColor2;
		}

		drawDayOfMonthBackground ( g, x + 1, y + 1, w - 1, h - 1, bgColor );
		g.setColor ( fg );

		if ( showMonthName )
			label = monthNames[day.get ( Calendar.MONTH )] + " "
			    + day.get ( Calendar.DAY_OF_MONTH );
		else
			label = "" + day.get ( Calendar.DAY_OF_MONTH );
		int labelW = g.getFontMetrics ().stringWidth ( label );
		g.drawString ( label, x + w - labelW - 1, y + fm.getAscent () );

		if ( this.repository != null ) {
			Vector events = this.repository.getEventInstancesForDate ( day
			    .get ( Calendar.YEAR ), day.get ( Calendar.MONTH ) + 1, day
			    .get ( Calendar.DAY_OF_MONTH ) );
			if ( events != null ) {
				Collections.sort ( events );
				boolean dateIsSelected = this.selectedDate != null
				    && this.selectedDate.year == day.get ( Calendar.YEAR )
				    && this.selectedDate.month == ( day.get ( Calendar.MONTH ) + 1 )
				    && this.selectedDate.day == day.get ( Calendar.DAY_OF_MONTH );
				int startY = y + fm.getHeight ();
				for ( int i = 0; i < events.size (); i++ ) {
					EventInstance e = (EventInstance) events.elementAt ( i );
					Rectangle rect = new Rectangle ( x + CELL_MARGIN, startY, w
					    - ( 2 * CELL_MARGIN ), fm.getHeight () );
					startY += fm.getHeight () + ( 1 * CELL_MARGIN );
					drawMonthViewEvent ( g, rect, e, dateIsSelected
					    && i == this.selectedItemInd );
					DisplayedEvent de = new DisplayedEvent ( e, rect, i );
					this.displayedEvents.addElement ( de );
				}
			}
		}
		g.setColor ( fg );
	}

	protected String formatTime ( int hour, int minute, int second ) {
		StringBuffer sb = new StringBuffer ();
		if ( hour == 0 || hour == 12 )
			sb.append ( "12" );
		else if ( hour > 12 )
			sb.append ( hour % 12 );
		else
			sb.append ( hour );
		sb.append ( ':' );
		if ( minute < 10 )
			sb.append ( '0' );
		sb.append ( minute );
		if ( hour < 12 )
			sb.append ( "am" );
		else
			sb.append ( "pm" );
		return sb.toString ();
	}

	public void drawDayOfMonthBackground ( Graphics g, int x, int y, int w,
	    int h, Color c ) {
		g.setColor ( c );
		g.fillRect ( x, y, w, h );
	}

	protected void drawMonthViewEvent ( Graphics g, Rectangle r,
	    EventInstance event, boolean isSelected ) {
		Color c = g.getColor ();
		g.setColor ( event.getBackgroundColor () );
		int arclen = r.height;
		if ( isSelected ) {
			// TODO: if selection color is too close to border color,
			// we may want to change the selection color automatically.
			// Or maybe add an animation/blink for drawing the selection.
			g.setColor ( this.selectionColor );
			g.drawRoundRect ( r.x - 1, r.y - 1, r.width + 2, r.height + 2,
			    arclen + 2, arclen + 2 );
		}
		g.setColor ( event.getBackgroundColor () );
		g.fillRoundRect ( r.x, r.y, r.width, r.height, arclen, arclen );
		g.setColor ( event.getBorderColor () );
		g.drawRoundRect ( r.x, r.y, r.width, r.height, arclen, arclen );
		g.setClip ( r.x + 1, r.y + 1, r.width - 2, r.height - 3 );
		g.setColor ( event.getForegroundColor () );
		String text;
		if ( event.hasTime () && this.showTime ) {
			text = formatTime ( event.getHour (), event.getMinute (), event
			    .getSecond () )
			    + " " + event.getTitle ();
		} else {
			text = event.getTitle ();
		}
		g.drawString ( text, r.x + 3, r.y + g.getFontMetrics ().getAscent () );
		g.setColor ( c );
		// remove clip
		g.setClip ( null );
	}

	public boolean getShowTime () {
		return showTime;
	}

	public void setShowTime ( boolean showTime ) {
		this.showTime = showTime;
	}

	public boolean getAllowsEventSelection () {
		return allowsEventSelection;
	}

	public void setAllowsEventSelection ( boolean allowsEventSelection ) {
		this.allowsEventSelection = allowsEventSelection;
	}

	public void addSelectionListener ( CalendarPanelSelectionListener l ) {
		this.selectionListeners.add ( l );
	}

	public EventInstance getSelectedEvent () {
		if ( this.selectedDate == null )
			return null;
		Vector eventsForDate = this.repository.getEventInstancesForDate (
		    this.selectedDate.year, this.selectedDate.month, this.selectedDate.day );
		if ( eventsForDate != null )
			Collections.sort ( eventsForDate );
		// System.out.println ( "Found " + eventsForDate.size ()
		// + " events for date: " + this.selectedDate.getMonth () + "/"
		// + this.selectedDate.getDay () );
		if ( this.selectedItemInd >= 0
		    && this.selectedItemInd < eventsForDate.size () ) {
			EventInstance eventInstance = (EventInstance) eventsForDate
			    .elementAt ( this.selectedItemInd );
			return eventInstance;
		}
		return null;
	}

	/**
	 * Clear any user selection. This should be done anytime the contents of what
	 * is being displayed is modified. For example, if a calendar is added to the
	 * display or removed, this should be called. The event selected by the user
	 * is internally stored by date and index number for that date, so anything
	 * that may change the number of events displayed on a particular date could
	 * cause the selection to "move", so the app should call this method to clear
	 * the selection.
	 */
	public void clearSelection () {
		boolean doRepaint = ( this.selectedDate != null && this.selectedItemInd >= 0 );
		this.selectedDate = null;
		this.selectedItemInd = -1;
		if ( doRepaint )
			repaint ();
	}

	public void mouseWheelMoved ( MouseWheelEvent e1 ) {
		int notches = e1.getWheelRotation ();
		this.scrollBar.setValue ( this.scrollBar.getValue () + notches );
	}
}
