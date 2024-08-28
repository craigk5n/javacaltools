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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JApplet;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.CSVParser;
import us.k5n.ical.CalendarParser;
import us.k5n.ical.DataStore;
import us.k5n.ical.Date;
import us.k5n.ical.Event;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.Utils;

/**
 * The CalendarPanelApplet class displays a month-at-a-glance view to the user
 * by using the CalendarPanel class. Calendar data is specified as applet
 * parameters.
 *
 * Applet iCalendar URLs are specified as applet parameters <code>file1</code>,
 * <code>file2</code>, etc. While corresponding colors (optional) are specified
 * as <code>color1</code>, <code>color2</code>, etc. Below is an example:
 *
 * <pre>
 * &lt;param name=&quot;file1&quot; value=&quot;http://ical.mac.com/ical/US32Holidays.ics&quot;/&gt;
 * &lt;param name=&quot;color1&quot; value=&quot;#ff0000&quot;/&gt;
 * &lt;param name=&quot;file2&quot; value=&quot;http://localhost/outlook-export.csv&quot;/&gt;
 * &lt;param name=&quot;color2&quot; value=&quot;#ffff00&quot;/&gt;
 * </pre>
 *
 * By default, the file parameters will be treated as in iCalendar file.
 * However, if the parameter ends with ".csv", then the file will be parsed as a
 * comma-separated values (CSV) file exported from Microsoft Outlook.
 *
 * <br/>
 * <b>Note:</b> The applet <b>must be signed</b> if you want to load calendar
 * files from any URL that is not on the same server as the applet.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class CalendarPanelApplet extends JApplet {
	public final Color[] defaultColors = { Color.BLUE, Color.RED, Color.GREEN, Color.GRAY, Color.CYAN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.YELLOW };
	CalendarPanel cpanel;
	AppletDataRepository data = null;

	/**
	 * Load all data files from URLs specified as applet parameters. Build user
	 * interface.
	 */
	public void init() {
		List<RemoteCalendar> calendars = new ArrayList<RemoteCalendar>();

		try {
			for (int i = 1; true; i++) {
				String param = "file" + i;
				String urlStr = getParameter(param);
				if (urlStr != null) {
					CalendarParser parser = null;
					URL url = new URL(urlStr);
					URLConnection conn = url.openConnection();
					InputStream inStream = conn.getInputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
					if (urlStr.toLowerCase().endsWith(".csv")) {
						System.out.println("Parsing CVS calendar: " + url);
						parser = new CSVParser(CSVParser.PARSE_STRICT);
					} else {
						System.out.println("Parsing ICS calendar: " + url);
						parser = new ICalendarParser(ICalendarParser.PARSE_LOOSE);
					}
					parser.parse(in);
					System.out.println("  found " + parser.getDataStoreAt(0).getAllEvents().size() + " events");
					inStream.close();
					param = "color" + i;
					Color color = defaultColors[(i - 1) % defaultColors.length];
					if (getParameter(param) != null) {
						color = parseColor(getParameter(param));
					}
					RemoteCalendar rc = new RemoteCalendar(parser, color);
					calendars.add(rc);
				} else {
					break;
				}
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		this.data = new AppletDataRepository(calendars, false);

		buildUI();
	}

	public void buildUI() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		cpanel = new GradientCalendarPanel(this.data);
		contentPane.add(cpanel, BorderLayout.CENTER);
		this.setVisible(true);
	}

	/**
	 * Parse the color value.
	 *
	 * @param colorStr
	 *            The color expressed as a RRGGBB String (example: "#ffffff")
	 * @return
	 */
	public static Color parseColor(String colorStr) {
		int r = 192, g = 192, b = 192;

		try {
			if (colorStr == null) {
				// ignore
			} else if (colorStr.indexOf(",") > 0) {
				String[] params = colorStr.split(",");
				if (params.length == 3) {
					r = Integer.parseInt(params[0]);
					g = Integer.parseInt(params[1]);
					b = Integer.parseInt(params[2]);
				}
			} else if (colorStr.startsWith("#") && colorStr.length() == 7) {
				r = hexValue(colorStr.substring(1, 3));
				g = hexValue(colorStr.substring(3, 5));
				b = hexValue(colorStr.substring(5, 7));
			} else if (colorStr.length() == 6) {
				r = hexValue(colorStr.substring(0, 2));
				g = hexValue(colorStr.substring(2, 4));
				b = hexValue(colorStr.substring(4, 6));
			} else {
				System.err.println("Invalid color specification: " + colorStr);
			}
		} catch (Exception e) {
			System.err.println("Invalid color specification for" + colorStr);
		}
		return new Color(r, g, b);
	}

	/**
	 * Convert a hex value into an integer.
	 *
	 * @param st
	 *            Two-digit ex value ("FF", "00", "A0", etc.)
	 */
	public static int hexValue(String st) {
		st = st.toUpperCase();
		int ret = 0;

		char ch1 = st.charAt(0);
		if (ch1 >= '0' && ch1 <= '9')
			ret += 16 * Integer.parseInt("" + ch1);
		else {
			switch (ch1) {
			case 'A':
				ret += 16 * 10;
				break;
			case 'B':
				ret += 16 * 11;
				break;
			case 'C':
				ret += 16 * 12;
				break;
			case 'D':
				ret += 16 * 13;
				break;
			case 'E':
				ret += 16 * 14;
				break;
			case 'F':
				ret += 16 * 15;
				break;
			}
		}

		char ch2 = st.charAt(1);
		if (ch2 >= '0' && ch2 <= '9')
			ret += Integer.parseInt("" + ch2);
		else {
			switch (ch2) {
			case 'A':
				ret += 10;
				break;
			case 'B':
				ret += 11;
				break;
			case 'C':
				ret += 12;
				break;
			case 'D':
				ret += 13;
				break;
			case 'E':
				ret += 14;
				break;
			case 'F':
				ret += 15;
				break;
			}
		}

		return ret;
	}
}

/**
 * Implement the EventInstance as required by the CalendarPanel class.
 */
class SingleEvent implements EventInstance, Comparable {
	String title, description, location;
	int Y, M, D, h, m, s;
	boolean hasTime, allDay;
	Color fg, bg, border;
	// The Event that this SingleEvent is derived from
	protected Event event;
	protected Color color;

	public SingleEvent(String title, String description, int Y, int M, int D) {
		this(title, description, Y, M, D, 0, 0, 0, false, false);
	}

	public SingleEvent(String title, String description, int Y, int M, int D, int h, int m, int s) {
		this(title, description, Y, M, D, h, m, s, true, false);
	}

	public SingleEvent(String title, String description, int Y, int M, int D, int h, int m, int s, boolean hasTime,
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
		// Set default color to blue
		this.bg = Color.blue;
		this.fg = Color.white;
		this.border = Color.white;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		if (this.event != null && this.event.getLocation() != null)
			return this.event.getLocation().getValue();
		return null;
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

	public void setBackgroundColor(Color bg) {
		this.bg = bg;
	}

	public void setBorderColor(Color border) {
		this.border = border;
	}

	public void setDayOfMonth(int d) {
		D = d;
	}

	public void setForegroundColor(Color fg) {
		this.fg = fg;
	}

	public void setHour(int h) {
		this.h = h;
	}

	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
	}

	public void setMinute(int m) {
		this.m = m;
	}

	public void setSecond(int s) {
		this.s = s;
	}

	public void setYear(int y) {
		Y = y;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
}

class RemoteCalendar {
	CalendarParser parser;
	Color color;

	public RemoteCalendar(CalendarParser parser, Color color) {
		this.parser = parser;
		this.color = color;
	}
}

/**
 * Implement the CalendarDataRepository required by the CalendarPanel class.
 * This class implements a simple in-memory storage mechanism for all the
 * iCalendar data loaded by the applet.
 */
class AppletDataRepository implements CalendarDataRepository {
	List<RemoteCalendar> remoteCalendars;
	int parseErrorCount = 0;
	int eventCount = 0;
	private Map<String,List<EventInstance>> cachedEvents;
	private boolean needsRebuilding = true;

	public AppletDataRepository(List<RemoteCalendar> remoteCalendars, boolean strictParsing) {
		this.remoteCalendars = remoteCalendars;
		this.cachedEvents = new HashMap<String,List<EventInstance>>();
		this.needsRebuilding = true;
		rebuildPrivateData();
	}

	/**
	 * Get all Event objects.
	 *
	 * @return
	 */
	public List<Event> getAllEntries() {
		List<Event> ret = new ArrayList<Event>();
		for (int i = 0; i < this.remoteCalendars.size(); i++) {
			RemoteCalendar rc = this.remoteCalendars.get(i);
			DataStore ds = rc.parser.getDataStoreAt(0);
			ret.addAll(ds.getAllEvents());
		}
		return ret;
	}

	/**
	 * Rebuild internal cached data after one or more calendar
	 */
	public void rebuild() {
		this.needsRebuilding = true;
	}

	/**
	 * Rebuild internal cached data after one or more calendar. Update the
	 * EventInstance objects array. Update the List of existing categories.
	 * The following objects will be updated: categories, cachedEvents
	 */
	private void rebuildPrivateData() {
		if (!needsRebuilding)
			return;
		// TODO: handle canceled and tentative events
		boolean showCancelled = false;
		boolean showTentative = true;
		this.cachedEvents = new HashMap<String,List<EventInstance>>();
		for (int i = 0; this.remoteCalendars != null && i < this.remoteCalendars.size(); i++) {
			RemoteCalendar rc = this.remoteCalendars.get(i);
			DataStore ds = rc.parser.getDataStoreAt(0);
			List<Event> events = ds.getAllEvents();
			for (int j = 0; j < events.size(); j++) {
				Event event = events.get(j);
				if (event.getStartDate() != null) {
					boolean display = true;
					switch (event.getStatus()) {
					case Event.STATUS_CANCELLED:
						display = showCancelled;
						break;
					case Event.STATUS_TENTATIVE:
						display = showTentative;
						break;
					}
					if (display) {
						SingleEvent se = null;
						if (event.isValid() && event.getStartDate() != null) {
							Date startDate = event.getStartDate();
							String title = event.getSummary().getValue();
							String description = event.getDescription() != null ? event.getDescription().getValue()
									: title;
							if (startDate.isDateOnly()) {
								se = new SingleEvent(title, description, startDate.getYear(), startDate.getMonth(),
										startDate.getDay());
							} else {
								se = new SingleEvent(title, description, startDate.getYear(), startDate.getMonth(),
										startDate.getDay(), startDate.getHour(), startDate.getMinute(),
										startDate.getSecond());
							}
							se.setEvent(event);
							se.bg = rc.color;
							se.border = rc.color.brighter();
							if (rc.color.getRed() > 180 && rc.color.getBlue() > 180 && rc.color.getGreen() > 180) {
								// Color is very light, so don't use white as
								// foreground
								se.fg = se.bg.darker().darker();
							} else {
								se.fg = Color.WHITE;
							}
							String YMD = Utils.DateToYYYYMMDD(startDate);
							List<EventInstance> dateList = null;
							if (cachedEvents.containsKey(YMD)) {
								dateList =  cachedEvents.get(YMD);
							} else {
								dateList = new ArrayList<EventInstance>();
								cachedEvents.put(YMD, dateList);
							}
							dateList.add(se);
							// Add recurrence events
							List<Date> more = event.getRecurranceDates();
							for (int k = 0; more != null && k < more.size(); k++) {
								Date d2 = more.get(k);
								if (startDate.isDateOnly()) {
									se = new SingleEvent(title, description, d2.getYear(), d2.getMonth(), d2.getDay());
								} else {
									se = new SingleEvent(title, description, d2.getYear(), d2.getMonth(), d2.getDay(),
											d2.getHour(), d2.getMinute(), d2.getSecond());
								}
								se.setEvent(event);
								se.bg = rc.color;
								se.border = rc.color.brighter();
								if (rc.color.getRed() > 180 && rc.color.getBlue() > 180 && rc.color.getGreen() > 180) {
									// Color is very light, so don't use white
									// as foreground
									se.fg = se.bg.darker().darker();
								} else {
									se.fg = Color.WHITE;
								}
								YMD = Utils.DateToYYYYMMDD(d2);
								dateList = null;
								if (cachedEvents.containsKey(YMD)) {
									dateList = cachedEvents.get(YMD);
								} else {
									dateList = new ArrayList<EventInstance>();
									cachedEvents.put(YMD, dateList);
								}
								dateList.add(se);
							}
						}
					}
				}
			}
		}
		this.needsRebuilding = false;
	}

	public List<EventInstance> getEventInstancesForDate(int year, int month, int day) {
		if (needsRebuilding)
			this.rebuildPrivateData();
		try {
			Date date = new Date("DTSTART", year, month, day);
			String YMD = Utils.DateToYYYYMMDD(date);
			return cachedEvents.get(YMD);
		} catch (BogusDataException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}

/**
 * Extend the CalendarPanel class so that we can draw a custom cell background
 * using a gradient. This is not included in the main CalendarPanel class
 * because gradient drawing is not part of Java 1.2.
 */
class GradientCalendarPanel extends CalendarPanel {
	private boolean useGradientBackground = true;

	public GradientCalendarPanel(CalendarDataRepository repo) {
		super(repo);
	}

	/**
	 * Specify whether a gradient background should be used for background
	 * colors. If enabled, then the background will start at the lower right
	 * corner of each table cell with the specified background color. The color
	 * will slowly change towards the upper left corner, where the color will be
	 * the average of the background color and white.
	 *
	 * @param useGradient
	 *            Should gradient backgrounds be used?
	 */
	public void setUseGradientBackground(boolean useGradient) {
		this.useGradientBackground = useGradient;
	}

	/**
	 * Override the default method so we can draw with a gradient background.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bottomColor
	 */
	public void drawDayOfMonthBackground(Graphics g, int x, int y, int w, int h, Color bottomColor) {
		if (this.useGradientBackground) {
			Color topColor = new Color((255 + bottomColor.getRed()) / 2, (255 + bottomColor.getGreen()) / 2,
					(255 + bottomColor.getBlue()) / 2);
			Graphics2D g2 = (Graphics2D) g;
			GradientPaint gp = new GradientPaint(x, y, topColor, x + w, y + h, bottomColor);
			g2.setPaint(gp);
			g2.fillRect(x, y, w, h);
		} else {
			g.setColor(bottomColor);
			g.fillRect(x, y, w, h);
		}
	}
}
