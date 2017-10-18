package us.k5n.ui.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Simple test/demo class for CalendarPanel that illustrates how to use the
 * CalendarPanel class and can be used to view the appearance of the
 * CalendarPanel class.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class CalendarPanelTest extends JFrame implements CalendarDataRepository {
	private static final long serialVersionUID = 1000L;
	CalendarPanel cpanel;
	static final String longDescr = "Mary had a little lamb,\n" + "Little lamb, little lamb,\n"
			+ "Mary had a little lamb,\n" + "Its fleece was white as snow\n" + "And everywhere that Mary went\n"
			+ "Mary went, Mary went,\n" + "Everywhere that Mary went\n" + "The lamb was sure to go\n"
			+ "It followed her to school one day\n" + "School one day, school one day\n"
			+ "It followed her to school one day\n" + "Which was against the rules.\n"
			+ "It made the children laugh and play,\n" + "Laugh and play, laugh and play,\n"
			+ "It made the children laugh and play\n" + "To see a lamb at school\n"
			+ "And so the teacher turned it out,\n" + "Turned it out, turned it out,\n"
			+ "And so the teacher turned it out,\n" + "But still it lingered near\n" + "And waited patiently about,\n"
			+ "Patiently about, patiently about,\n" + "And waited patiently about\n" + "Till Mary did appear\n"
			+ "\"Why does the lamb love Mary so?\"\n" + "Love Mary so? Love Mary so?\n"
			+ "\"Why does the lamb love Mary so?\"\n" + "The eager children cry\n"
			+ "\"Why, Mary loves the lamb, you know.\"\n" + "Loves the lamb, you know, loves the lamb, you know\n"
			+ "\"Why, Mary loves the lamb, you know.\"\n" + "The teacher did reply.";
	static final String wordWrapTest = "We the People of the United States, in Order to form a "
			+ "more perfect Union,  establish Justice, insure domestic "
			+ "Tranquility, provide for the common defence, promote the "
			+ "general Welfare, and secure the Blessings of Liberty to "
			+ "ourselves and our Posterity, do ordain and establish this "
			+ "Constitution for the United States of America.";

	public CalendarPanelTest() {
		super("Calendar Test");
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton larger = createButton("larger.png", "+");
		larger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				larger();
			}
		});
		buttonPanel.add(larger);

		JButton smaller = createButton("smaller.png", "-");
		smaller.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				smaller();
			}
		});
		buttonPanel.add(smaller);

		JButton up = createButton("up.png", "<");
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cpanel.decrementWeek(1);
			}
		});
		buttonPanel.add(up);

		JButton down = createButton("down.png", ">");
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cpanel.incrementWeek(1);
			}
		});
		buttonPanel.add(down);

		JButton oneRow = createButton("1-row.png", "1");
		oneRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cpanel.setNumWeeksToDisplay(1);
				cpanel.repaint();
			}
		});
		buttonPanel.add(oneRow);

		JButton twoRows = createButton("2-rows.png", "2");
		twoRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cpanel.setNumWeeksToDisplay(2);
				cpanel.repaint();
			}
		});
		buttonPanel.add(twoRows);

		JButton fiveRows = createButton("5-rows.png", "5");
		fiveRows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cpanel.setNumWeeksToDisplay(5);
				cpanel.repaint();
			}
		});
		buttonPanel.add(fiveRows);

		contentPane.add(buttonPanel, BorderLayout.NORTH);

		cpanel = new CalendarPanel(this);
		cpanel.setFormatter(new MyEventFormatter());
		Calendar startOf2015 = Calendar.getInstance();
		startOf2015.setLenient(true);
		startOf2015.set(Calendar.YEAR, 2015);
		startOf2015.set(Calendar.MONTH, 0);
		startOf2015.set(Calendar.DAY_OF_MONTH, 1);
		startOf2015.set(Calendar.HOUR_OF_DAY, 0);
		startOf2015.set(Calendar.MINUTE, 0);
		startOf2015.set(Calendar.SECOND, 0);
		startOf2015.set(Calendar.MILLISECOND, 0);
		cpanel.setAbsoluteStartDay(startOf2015);

		Calendar endOf2018 = Calendar.getInstance();
		endOf2018.setLenient(true);
		endOf2018.set(Calendar.YEAR, 2018);
		endOf2018.set(Calendar.MONTH, 11);
		endOf2018.set(Calendar.DAY_OF_MONTH, 31);
		endOf2018.set(Calendar.HOUR_OF_DAY, 23);
		endOf2018.set(Calendar.MINUTE, 59);
		endOf2018.set(Calendar.SECOND, 59);
		endOf2018.set(Calendar.MILLISECOND, 999);
		cpanel.setAbsoluteEndDay(endOf2018);

		contentPane.add(cpanel, BorderLayout.CENTER);
		this.setVisible(true);
	}

	private JButton createButton(String filename, String backupLabel) {
		URL url = this.getClass().getResource(filename);
		ImageIcon icon = null;
		if (url != null)
			icon = new ImageIcon(url, "+");
		JButton ret = icon == null ? new JButton(backupLabel) : new JButton();
		if (url != null) {
			ret.setHorizontalTextPosition(SwingConstants.CENTER);
			ret.setVerticalTextPosition(SwingConstants.BOTTOM);
			ret.setIcon(icon);
		}
		return ret;
	}

	void larger() {
		Font f = cpanel.getFont();
		Font newFont = new Font(f.getFamily(), f.getStyle(), f.getSize() + 2);
		cpanel.setFont(newFont);
	}

	void smaller() {
		Font f = cpanel.getFont();
		if (f.getSize() > 4) {
			Font newFont = new Font(f.getFamily(), f.getStyle(), f.getSize() - 2);
			cpanel.setFont(newFont);
		}
	}

	/**
	 * Get events for the specified date. This method implements the
	 * CalendarDataRepository interface. The CalendarPanel class does not cache
	 * this info, so this method should be fast and implement its own caching.
	 *
	 * @see CalendarDataRepository
	 * @return Vector of EventInstance objects.
	 */
	public Vector getEventInstancesForDate(int year, int month, int day) {
		Vector<Event> ret = new Vector<Event>();
		ret.add(new Event("Test event", "This is a test event.\nTest description", year, month, day));
		if (day % 3 == 0) {
			ret.add(new Event("Test 9:15", "This is a test event.\nTest description", year, month, day, 9, 15, 0));
		} else if (day % 3 == 1) {
			ret.add(new Event("Test 6PM", "This is a test event.\nTest description", year, month, day, 18, 00, 0));
		} else if (day % 3 == 2) {
			ret.add(new Event("Test 12:30", "This is a test event.\nTest description", year, month, day, 0, 30, 0));
			ret.add(new Event("Test 3:30pm", "This is a test event.\nTest description", year, month, day, 15, 30, 0));
		}
		if (day % 7 == 0) {
			ret.add(new Event("Format test", "Line 1.<br>Line 2.\\nLine 3.\nLine 4.", year, month, day));
		}
		if (day % 5 == 0) {
			ret.add(new Event("Long Description Event", longDescr, year, month, day));
		}
		if (day % 4 == 0) {
			ret.add(new Event("Word Wrap Test", wordWrapTest, year, month, day));
		}
		if (day == 15) {
			// Add 20 events for the 15th
			for (int i = 0; i < 20; i++) {
				ret.add(new Event("Event#" + (i + 1),
						"This is a test event.\n"
								+ "We're checking to see how the calendar renders when you put a whole bunch of "
								+ "events on the same day.",
						year, month, day));
			}

		}
		return ret;
	}

	class MyEventFormatter extends EventFormatter {
		@Override
		public List<String> formatDescription(String inputString) {
			List<String> ret = new ArrayList<String>();
			inputString = inputString.replace("\\n", "\n").replace("<br>", "\n");
			String[] lines = inputString.split("[\r\n]");
			for (int i = 0; i < lines.length; i++) {
				List<String> lines2 = Utils.wrapLine(lines[i], 50);
				ret.addAll(lines2);
			}
			return ret;
		}
	}

	/**
	 * Main for test app
	 */
	public static void main(String[] args) {
		new CalendarPanelTest();
	}

	/**
	 * Inner class that implements the EventInstance interface.
	 */
	class Event implements EventInstance {
		String title, description;
		int Y, M, D, h, m, s;
		boolean hasTime, allDay;
		Color fg, bg, border;
		Color[] colors = { Color.blue, Color.red, Color.orange, Color.pink, Color.gray, Color.green, Color.yellow,
				Color.cyan, Color.magenta };

		public Event(String title, String description, int Y, int M, int D) {
			this(title, description, Y, M, D, 0, 0, 0, false, false);
		}

		public Event(String title, String description, int Y, int M, int D, int h, int m, int s) {
			this(title, description, Y, M, D, h, m, s, true, false);
		}

		public Event(String title, String description, int Y, int M, int D, int h, int m, int s, boolean hasTime,
				boolean allDay) {
			this.title = title;
			this.description = description;
			this.Y = Y;
			this.M = M;
			this.D = D;
			this.h = h;
			this.m = m;
			this.s = s;
			this.hasTime = hasTime;
			this.allDay = allDay;
			// this.fg = new Color ( D * 8 % 256, h * 10 % 256, m * 4 % 256 );
			this.bg = colors[(Y + M + D + h + m + s) % colors.length];
			this.fg = new Color(this.bg.getRed() / 2, this.bg.getGreen() / 2, this.bg.getBlue() / 2);
			this.border = this.fg;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public boolean isAllDay() {
			return allDay;
		}

		public boolean hasTime() {
			return hasTime;
		}

		public int getYear() {
			return Y;
		}

		public int getMonth() {
			return M;
		}

		public int getDayOfMonth() {
			return D;
		}

		public int getHour() {
			return h;
		}

		public int getMinute() {
			return m;
		}

		public int getSecond() {
			return s;
		}

		public boolean hasDuration() {
			return false;
		}

		public String getLocation() {
			return null;
		}

		public int getDurationSeconds() {
			return 0;
		}

		public Color getForegroundColor() {
			return fg;
		}

		public Color getBackgroundColor() {
			return bg;
		}

		public Color getBorderColor() {
			return border;
		}

		/** Implement the Comparable interface so events can be sorted */
		public int compareTo(Object o) {
			EventInstance e2 = (EventInstance) o;
			if (this.getYear() < e2.getYear())
				return -1;
			else if (this.getYear() > e2.getYear())
				return 1;
			if (this.getMonth() < e2.getMonth())
				return -1;
			else if (this.getMonth() > e2.getMonth())
				return 1;
			if (this.getDayOfMonth() < e2.getDayOfMonth())
				return -1;
			else if (this.getDayOfMonth() > e2.getDayOfMonth())
				return 1;
			if (!this.hasTime && e2.hasTime())
				return -1;
			else if (this.hasTime() && !e2.hasTime())
				return 1;
			else if (!this.hasTime && !e2.hasTime())
				return 0;
			if (this.isAllDay() && !e2.isAllDay())
				return -1;
			if (!this.isAllDay() && e2.isAllDay())
				return 1;
			if (this.isAllDay() && e2.isAllDay())
				return 0;
			// both events have a time
			if (this.getHour() < e2.getHour())
				return -1;
			else if (this.getHour() > e2.getHour())
				return 1;
			if (this.getMinute() < e2.getMinute())
				return -1;
			else if (this.getMinute() > e2.getMinute())
				return 1;
			if (this.getSecond() < e2.getSecond())
				return -1;
			else if (this.getSecond() > e2.getSecond())
				return 1;

			return 0;
		}
	}

}
