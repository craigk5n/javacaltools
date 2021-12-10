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
 * iCalendar Attendee class - This object represents either an individual or a
 * resource and corresponds to the ATTENDEE iCalendar property.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Attendee extends Property {
	static final int TYPE_INDIVIDUAL = 1;
	static final int TYPE_GROUP = 2;
	static final int TYPE_RESOURCE = 3;
	static final int TYPE_ROOM = 4;
	static final int TYPE_UNKNOWN = 5;

	static final int STATUS_NEEDS_ACTION = 0; // default VEVENT, VTODO, VJOURNAL
	static final int STATUS_ACCEPTED = 1; // VEVENT, VTODO, VJOURNAL
	static final int STATUS_DECLINED = 2; // VEVENT, VTODO, VJOURNAL
	static final int STATUS_TENTATIVE = 3; // VEVENT, VTODO
	static final int STATUS_DELEGATED = 4; // VTODO only
	static final int STATUS_COMPLETED = 5; // VTODO only
	static final int STATUS_IN_PROCESS = 6; // VTODO only

	static final int ROLE_REQ_PARTICIPANT = 0; // default
	static final int ROLE_OPT_PARTICIPANT = 1;
	static final int ROLE_NON_PARTICIPANT = 2;
	static final int ROLE_CHAIR = 3;

	/** Attendee name */
	public String name = null;
	/** Common name */
	public String cn = null;
	/** Type of calendar user */
	public int type = TYPE_INDIVIDUAL; // default
	public int status = STATUS_NEEDS_ACTION; // default
	/**
	 * specify whether there is an expectation of a favor of a reply from the
	 * calendar user
	 */
	public boolean rsvp = false;
	/** specify the participation role for the calendar user */
	public int role = ROLE_REQ_PARTICIPANT; // default

	// TODO "DELEGATED-TO" support
	// TODO "DELEGATED-FROM" support
	// TODO "SENT-BY" support
	// TODO "DIR" support
	// TODO add methods to API to allow updating values

	/**
	 * Constructor
	 */
	public Attendee() {
		super("ATTENDEE", "Unknown Name");
		this.type = TYPE_INDIVIDUAL;
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr One or more lines of iCalendar that specifies an event
	 *                attendee
	 */
	public Attendee(String icalStr) throws ParseException {
		this(icalStr, PARSE_LOOSE);
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr   One or more lines of iCalendar that specifies an event
	 *                  attendee
	 * @param parseMode PARSE_STRICT or PARSE_LOOSE
	 */
	public Attendee(String icalStr, int parseMode) throws ParseException {
		super(icalStr, parseMode);

		for (Attribute a : attributeList) {
			String aval = a.value.toUpperCase();
			if (a.name.equals("CN")) {
				cn = a.value;
			} else if (a.name.equals("ROLE")) {
				if (aval.equals("REQ-PARTICIPANT")) {
					role = ROLE_REQ_PARTICIPANT;
				} else if (aval.equals("OPT-PARTICIPANT")) {
					role = ROLE_OPT_PARTICIPANT;
				} else if (aval.equals("NON-PARTICIPANT")) {
					role = ROLE_NON_PARTICIPANT;
				} else if (aval.equals("CHAIR")) {
					role = ROLE_CHAIR;
				} else {
					if (parseMode == PARSE_STRICT) {
						throw new ParseException("Unknown ROLE '" + a.value + "'", icalStr);
					}
				}
			} else if (a.name.equals("PARTSTAT")) {
				// TODO: only certain values are allowed in VEVENT vs VTODO
				if (aval.equals("NEEDS-ACTION")) {
					status = STATUS_NEEDS_ACTION;
				} else if (aval.equals("ACCEPTED")) {
					status = STATUS_ACCEPTED;
				} else if (aval.equals("DECLINED")) {
					status = STATUS_DECLINED;
				} else if (aval.equals("TENTATIVE")) {
					status = STATUS_TENTATIVE;
				} else if (aval.equals("DELEGATED")) {
					status = STATUS_DELEGATED;
				} else if (aval.equals("COMPLETED")) {
					status = STATUS_COMPLETED;
				} else if (aval.equals("IN-PROCESS")) {
					status = STATUS_IN_PROCESS;
				} else {
					if (parseMode == PARSE_STRICT) {
						throw new ParseException("Unknown PARTSTAT '" + a.value + "'", icalStr);
					}
				}
			} else if (a.name.equals("RSVP")) {
				if (aval.equals("TRUE")) {
					rsvp = true;
				} else if (aval.equals("FALSE")) {
					rsvp = true;
				} else {
					if (parseMode == PARSE_STRICT) {
						throw new ParseException("Unknown RSVP '" + a.value + "'", icalStr);
					}
				}
			} else if (a.name.equals("CUTYPE")) {
				if (aval.equals("INDIVIDUAL")) {
					type = TYPE_INDIVIDUAL;
				} else if (aval.equals("GROUP")) {
					type = TYPE_GROUP;
				} else if (aval.equals("RESOURCE")) {
					type = TYPE_RESOURCE;
				} else if (aval.equals("ROOM")) {
					type = TYPE_ROOM;
				} else if (aval.equals("UNKNOWN")) {
					type = TYPE_UNKNOWN;
				} else {
					if (parseMode == PARSE_STRICT) {
						throw new ParseException("Unknown CUTYPE '" + a.value + "'", icalStr);
					}
				}
			} else if (a.name.equals("DELEGATED-FROM")) {
				// TODO
			} else if (a.name.equals("SENT-BY")) {
				// TODO
			} else {
				// TODO: generate errors on unrecognized attributes?
				System.out.println("Ignoring unknown attribute '" + a.name + "' in " + this.getName());
			}
		}
	}
}
