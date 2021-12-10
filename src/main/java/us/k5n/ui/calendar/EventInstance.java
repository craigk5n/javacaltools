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

import java.awt.Color;

/**
 * The EventInstance interface defines how event information will be obtained by
 * the CalendarPanel class. An EventInstance object will contain information
 * about an event on a single occurrence. If an event repeats, there should be a
 * separate EventInstance object for each occurrence.
 * 
 * @see CalendarDataRepository
 * @see CalendarPanel
 * @author Craig Knudsen, craig@k5n.us
 */
public interface EventInstance extends Comparable {

	/** Get the event title (what appears in the CalendarPanel) */
	public abstract String getTitle ();

	/** Get the event description (appears in the mouse-over tooltip) */
	public abstract String getDescription ();

	/** Is the event an all-day event? */
	public abstract boolean isAllDay ();

	/** Does the event have a time of day specified? */
	public abstract boolean hasTime ();

	/** Get the event year (YYYY format) */
	public abstract int getYear ();

	/** Get the event month (Jan=1) */
	public abstract int getMonth ();

	/** Get the day of the month (1-31) */
	public abstract int getDayOfMonth ();

	/** Get hour of event (0-23) */
	public abstract int getHour ();

	/** Get minute of event (0-59) */
	public abstract int getMinute ();

	/** Get seconds of event (0-59) */
	public abstract int getSecond ();

	/** Does the event have a duration? */
	public abstract boolean hasDuration ();

	/** Get location of event (can return null if none) */
	public abstract String getLocation ();

	/** Get the event duration (in seconds) */
	public abstract int getDurationSeconds ();

	/** Get the text color to use for this event */
	public Color getForegroundColor ();

	/** Get the background color to use for this event */
	public Color getBackgroundColor ();

	/** Get the border color to use for this event */
	public Color getBorderColor ();

}
