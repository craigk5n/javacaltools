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

import java.util.ArrayList;
import java.util.List;

import us.k5n.ical.Constants;

/**
 * A simple implementation of the DataStore interface. This is the default
 * implementation used by IcalParser class.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @see CalendarParser
 */
public class DefaultDataStore implements DataStore {
	List<Timezone> timezones;
	List<Event> events;
	List<Todo> todos;
	List<Journal> journals;
	List<Freebusy> freebusys;
	List<VLocation> vlocations;
	List<VResource> vresources;
	List<VAvailability> vavailabilities;
	List<Participant> participants;
	Property method;
	String name;
	String calendarAddress;

	/**
	 * Constructor
	 */
	public DefaultDataStore() {
		timezones = new ArrayList<Timezone>();
		events = new ArrayList<Event>();
		todos = new ArrayList<Todo>();
		journals = new ArrayList<Journal>();
		freebusys = new ArrayList<Freebusy>();
		vlocations = new ArrayList<VLocation>();
		vresources = new ArrayList<VResource>();
		vavailabilities = new ArrayList<VAvailability>();
		participants = new ArrayList<Participant>();
	}

	/**
	 * This method will be called as the parser finds a VTIMEZONE object.
	 */
	public void storeTimezone(Timezone timezone) {
		timezones.add(timezone);
	}

	/**
	 * This method will be called as the parser finds a VEVENT object.
	 */
	public void storeEvent(Event event) {
		events.add(event);
	}

	/**
	 * This method will be called as the parser finds a VTODO object.
	 */
	public void storeTodo(Todo todo) {
		todos.add(todo);
	}

	/**
	 * This method will be called as the parser finds a VJOURNAL object.
	 */
	public void storeJournal(Journal journal) {
		journals.add(journal);
	}

	/**
	 * This method will be called as the parser finds a VFREEBUSY object.
	 */
	public void storeFreebusy(Freebusy freebusy) {
		freebusys.add(freebusy);
	}

	/**
	 * This method will be called as the parser finds a VLOCATION object.
	 */
	public void storeVLocation(VLocation vlocation) {
		vlocations.add(vlocation);
	}

	/**
	 * This method will be called as the parser finds a VRESOURCE object.
	 */
	public void storeVResource(VResource vresource) {
		vresources.add(vresource);
	}

	/**
	 * This method will be called as the parser finds a VAVAILABILITY object.
	 */
	public void storeVAvailability(VAvailability vavailability) {
		vavailabilities.add(vavailability);
	}

	/**
	 * This method will be called when the parser finds a PARTICIPANT object.
	 */
	public void storeParticipant(Participant participant) {
		participants.add(participant);
	}

	/**
	 * This method will be called when the METHOD property is found in VCALENDAR.
	 */
	public void setMethod(Property method) {
		this.method = method;
	}

	/**
	 * Get a List of all Event objects
	 * 
	 * @return A List of Event objects
	 */
	public List<Event> getAllEvents() {
		return events;
	}

	/**
	 * Get all Journal objects.
	 * 
	 * @return A List of Journal objects
	 */
	public List<Journal> getAllJournals() {
		return journals;
	}

	/**
	 * Get all Timezone objects.
	 * 
	 * @return A List of Timezone objects
	 */
	public List<Timezone> getAllTimezones() {
		return timezones;
	}

	/**
	 * Get all Freebusy objects.
	 *
	 * @return A List of Freebusy objects
	 */
	public List<Freebusy> getAllFreebusys() {
		return freebusys;
	}

	/**
	 * Get all VLocation objects.
	 *
	 * @return A List of VLocation objects
	 */
	public List<VLocation> getAllVLocations() {
		return vlocations;
	}

	/**
	 * Get all VResource objects.
	 *
	 * @return A List of VResource objects
	 */
	public List<VResource> getAllVResources() {
		return vresources;
	}

	/**
	 * Get all VAvailability objects.
	 *
	 * @return A List of VAvailability objects
	 */
	public List<VAvailability> getAllVAvailabilities() {
		return vavailabilities;
	}

	/**
	 * Get all Participant objects.
	 *
	 * @return A List of Participant objects
	 */
	public List<Participant> getAllParticipants() {
		return participants;
	}

	/**
	 * Get the METHOD property.
	 *
	 * @return The METHOD property, or null if not set
	 */
	public Property getMethod() {
		return method;
	}

	/**
	 * Get the METHOD value as string.
	 *
	 * @return The METHOD value, or null if not set
	 */
	public String getMethodValue() {
		return method != null ? method.value : null;
	}

	/**
	 * Set the NAME property for VCALENDAR.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the NAME property.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the CALENDAR-ADDRESS property for VCALENDAR.
	 *
	 * @param calendarAddress the calendar address
	 */
	public void setCalendarAddress(String calendarAddress) {
		this.calendarAddress = calendarAddress;
	}

	/**
	 * Get the CALENDAR-ADDRESS property.
	 *
	 * @return the calendar address
	 */
	public String getCalendarAddress() {
		return calendarAddress;
	}

	/**
	 * Generate iCalendar format string for the entire calendar.
	 *
	 * @return The iCalendar string
	 */
	public String toICalendar() {
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN:VCALENDAR").append(Constants.CRLF);
		sb.append("VERSION:2.0").append(Constants.CRLF);
		sb.append("PRODID:-//K5N//JavaCalTools//EN").append(Constants.CRLF);

		if (method != null) {
			sb.append(method.toICalendar());
		}
		if (name != null) {
			sb.append("NAME:").append(name).append(Constants.CRLF);
		}
		if (calendarAddress != null) {
			sb.append("CALENDAR-ADDRESS:").append(calendarAddress).append(Constants.CRLF);
		}

		// Add components
		for (Timezone tz : timezones) {
			sb.append(tz.toICalendar());
		}
		for (Event event : events) {
			sb.append(event.toICalendar());
		}
		for (Todo todo : todos) {
			sb.append(todo.toICalendar());
		}
		for (Journal journal : journals) {
			sb.append(journal.toICalendar());
		}
		for (Freebusy fb : freebusys) {
			sb.append(fb.toICalendar());
		}
		for (VLocation loc : vlocations) {
			sb.append(loc.toICalendar());
		}
		for (VResource res : vresources) {
			sb.append(res.toICalendar());
		}
		for (VAvailability va : vavailabilities) {
			sb.append(va.toICalendar());
		}

		sb.append("END:VCALENDAR").append(Constants.CRLF);

		return Utils.foldLines(sb.toString());
	}
}