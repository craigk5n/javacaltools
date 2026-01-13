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
}