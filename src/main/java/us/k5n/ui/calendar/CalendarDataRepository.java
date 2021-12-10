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

import java.util.List;

/**
 * The CalendarDataRepository defines the interface that the CalendarPanel will
 * use to get event data. It is up to the calling application to implement this
 * interface. Note that the CalendarPanel does not cache any event data, so the
 * implementation of this interface should keep performance in mind.
 * 
 * @see CalendarPanel
 * @author Craig Knudsen, craig@k5n.us
 * 
 */
public interface CalendarDataRepository {

	/**
	 * Return a Vector of EventInstance objects for the specified date.
	 * 
	 * @param year
	 *          The year in YYYY format
	 * @param month
	 *          The month (Jan = 1, Feb = 2, ..., Dec = 12)
	 * @param day
	 *          The day of the month (1-31)
	 * @return Vector of EventInstance objects
	 */
	public abstract List<EventInstance> getEventInstancesForDate ( int year, int month, int day );

}
