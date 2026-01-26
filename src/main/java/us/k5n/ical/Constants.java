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
 * iCalendar constants interface defining all RFC-compliant constants.
 *
 * <p>Note: In Java interfaces, all fields are implicitly {@code public static final}.
 * While implicit declaration works, explicit declaration is preferred for clarity
 * and to follow Java best practices.</p>
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public interface Constants {
	/**
	 * Parse iCalendar data strictly. ParseException errors will be generated for
	 * all errors. This would typically be used to validate an iCalendar data file
	 * rather than by an application interested in loading iCalendar data.
	 */
	public static final int PARSE_STRICT = 1;
	/**
	 * Don't parse iCalendar data strictly. (This is the default in most cases.)
	 * Attempt to parse and derive as much from the iCalendar data as possible.
	 */
	public static final int PARSE_LOOSE = 2;

	/**
	 * Parse iCalendar data in RFC 2445 compatibility mode.
	 * Supports deprecated properties and syntax from the original iCalendar specification.
	 */
	public static final int PARSE_RFC2445 = 3;

	/** Line termination string. */
	public static final String CRLF = "\r\n";

	/** Carriage return character */
	public static final int CR = 13;
	/** Line feed character */
	public static final int LF = 10;
	/** Tab character */
	public static final int TAB = 9;
	/** Space character */
	public static final int SPACE = 32;

	/** Maximum line length acceptable in iCalendar (excluding CRLF) */
	public static final int MAX_LINE_LENGTH = 75;

	/** iCalendar major version */
	public static final int ICAL_VERSION_MAJOR = 2;
	/** iCalendar minor version */
	public static final int ICAL_VERSION_MINOR = 0;

	/** iCalendar PUBLIC class (default) */
	public static final int PUBLIC = 0;
	/** iCalendar PRIVATE class */
	public static final int PRIVATE = 1;
	/** iCalendar CONFIDENTIAL class */
	public static final int CONFIDENTIAL = 2;

	/** iCalendar Status not defined */
	public static final int STATUS_UNDEFINED = -1;
	/** iCalendar Status tentative (VEVENT only) */
	public static final int STATUS_TENTATIVE = 1;
	/** iCalendar Status confirmed (VEVENT only) */
	public static final int STATUS_CONFIRMED = 2;
	/** iCalendar Status cancelled (VEVENT only) */
	public static final int STATUS_CANCELLED = 3;
	/** iCalendar Status needs action (VTODO only) */
	public static final int STATUS_NEEDS_ACTION = 4;
	/** iCalendar Status completed (VTODO only) */
	public static final int STATUS_COMPLETED = 5;
	/** iCalendar Status in process (VTODO only) */
	public static final int STATUS_IN_PROCESS = 6;
	/** iCalendar Status draft (VJOURNAL only) */
	public static final int STATUS_DRAFT = 7;
	/** iCalendar Status final (VJOURNAL only) */
	public static final int STATUS_FINAL = 8;

	/** iCalendar transparent (event does not show up in freebusy searches) */
	public static final int TRANSP_TRANSPARENT = 1;
	/** iCalendar opaque (event does show up in freebusy searches) */
	public static final int TRANSP_OPAQUE = 1;

	/** iCalendar version string (in N.N format) */
	public static final String ICAL_VERSION = ICAL_VERSION_MAJOR + "." + ICAL_VERSION_MINOR;

	/* iTIP METHOD types (RFC 5546) */
	public static final String METHOD_PUBLISH = "PUBLISH";
	public static final String METHOD_REQUEST = "REQUEST";
	public static final String METHOD_REPLY = "REPLY";
	public static final String METHOD_ADD = "ADD";
	public static final String METHOD_CANCEL = "CANCEL";
	public static final String METHOD_REFRESH = "REFRESH";
	public static final String METHOD_COUNTER = "COUNTER";
	public static final String METHOD_DECLINECOUNTER = "DECLINECOUNTER";

	/* RFC 9073 Property Names */
	public static final String LOCATION_TYPE = "LOCATION-TYPE";
	public static final String RESOURCE_TYPE = "RESOURCE-TYPE";
	public static final String PARTICIPANT_TYPE = "PARTICIPANT-TYPE";
	public static final String CALENDAR_ADDRESS = "CALENDAR-ADDRESS";
	public static final String STYLED_DESCRIPTION = "STYLED-DESCRIPTION";

	/* RFC 9074 Property Names */
	public static final String PROXIMITY = "PROXIMITY";

	/* RFC 7986 Property Names */
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String UID = "UID";
	public static final String URL = "URL";
	public static final String LAST_MODIFIED = "LAST-MODIFIED";
	public static final String REFRESH_INTERVAL = "REFRESH-INTERVAL";
	public static final String SOURCE = "SOURCE";
	public static final String COLOR = "COLOR";
}
