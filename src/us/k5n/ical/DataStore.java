/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
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

package us.k5n.ical;

/**
 * The DataStore interface defines how parsed iCalendar data (VTIMEZONE, VEVENT,
 * VTODO, VJOURNAL, VFREEBUSY) will be stored. By default, all objects will be
 * stored internally. However, you can specify that:
 * <ul>
 * <li>the data should be stored internally and a new DataStore should also
 * receive the events </li>
 * <li>the data should <em>not</em> be stored internally and a new DataStore
 * should receive the events </li>
 * </ul>
 * See the IcalParser interface for how to change these settings.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 * @see ICalendarParser
 */
public interface DataStore {

	/**
	 * This method will be called the parser finds a VTIMEZONE object.
	 */
	public void storeTimezone(Timezone timezone);

	/**
	 * This method will be called the parser finds a VEVENT object.
	 */
	public void storeEvent(Event event);

	/**
	 * This method will be called the parser finds a VTODO object.
	 */
	public void storeTodo(Todo todo);

	/**
	 * This method will be called the parser finds a VJOURNAL object.
	 */
	public void storeJournal(Journal journal);

	/**
	 * This method will be called the parser finds a VFREEBUSY object.
	 */
	public void storeFreebusy(Freebusy freebusy);

	/**
	 * Get all Event objects.
	 * 
	 * @return A Vector if Event objects
	 */
	public java.util.Vector getAllEvents();

	/**
	 * Get all Journal objects.
	 * 
	 * @return A Vector of Journal objects
	 */
	public java.util.Vector getAllJournals();

}
