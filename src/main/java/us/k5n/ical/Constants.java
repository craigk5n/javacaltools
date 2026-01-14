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
 * iCalendar constants
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public interface Constants {
	/**
	 * Parse iCalendar data strictly. ParseException errors will be generated for
	 * all errors. This would typically be used to validate an iCalendar data file
	 * rather than by an application intersted in loading iCalendar data.
	 */
	static public final int PARSE_STRICT = 1;
	/**
	 * Don't parse iCalendar data strictly. (This is the default in most cases.)
	 * Attempt to parse and derive as much from the iCalendar data as possible.
	 */
	static public final int PARSE_LOOSE = 2;

	/** Line termination string. */
	static public final String CRLF = "\r\n";

	/** Carriage return character */
	static public final int CR = 13;
	/** Line feed character */
	static public final int LF = 10;
	/** Tab character */
	static public final int TAB = 9;
	/** Space character */
	static public final int SPACE = 32;

	/** Maximum line length acceptable in iCalendar (excluding CRLF) */
	static public final int MAX_LINE_LENGTH = 75;

	/** iCalendar major version */
	static public final int ICAL_VERSION_MAJOR = 2;
	/** iCalendar minor version */
	static public final int ICAL_VERSION_MINOR = 0;

	/** iCalendar PUBLIC class (default) */
	static public final int PUBLIC = 0;
	/** iCalendar PRIVATE class */
	static public final int PRIVATE = 1;
	/** iCalendar CONFIDENTIAL class */
	static public final int CONFIDENTIAL = 2;

	/** iCalendar Status not defined */
	static public final int STATUS_UNDEFINED = -1;
	/** iCalendar Status tentative (VEVENT only) */
	static public final int STATUS_TENTATIVE = 1;
	/** iCalendar Status confirmed (VEVENT only) */
	static public final int STATUS_CONFIRMED = 2;
	/** iCalendar Status confirmed (VEVENT only) */
	static public final int STATUS_CANCELLED = 3;
	/** iCalendar Status needs action (VTODO only) */
	static public final int STATUS_NEEDS_ACTION = 4;
	/** iCalendar Status needs action (VTODO only) */
	static public final int STATUS_COMPLETED = 5;
	/** iCalendar Status in process (VTODO only) */
	static public final int STATUS_IN_PROCESS = 6;
	/** iCalendar Status needs action (VJOURNAL only) */
	static public final int STATUS_DRAFT = 7;
	/** iCalendar Status needs action (VJOURNAL only) */
	static public final int STATUS_FINAL = 8;

	/** iCalendar transparent (event does not show up in freebusy searches) */
	static public final int TRANSP_TRANSPARENT = 1;
	/** iCalendar opaque (event does show up in freebusy searches) */
	static public final int TRANSP_OPAQUE = 1; // default value in RFC2445 is
																							// OPAQUE

	/* iCalendar version (in N.N String format) */
	static public final String ICAL_VERSION = ICAL_VERSION_MAJOR + "."
	    + ICAL_VERSION_MINOR;

	/* iTIP METHOD types */
	static public final String METHOD_PUBLISH = "PUBLISH";
	static public final String METHOD_REQUEST = "REQUEST";
	static public final String METHOD_REPLY = "REPLY";
	static public final String METHOD_ADD = "ADD";
	static public final String METHOD_CANCEL = "CANCEL";
	static public final String METHOD_REFRESH = "REFRESH";
	static public final String METHOD_COUNTER = "COUNTER";
	static public final String METHOD_DECLINECOUNTER = "DECLINECOUNTER";
}
