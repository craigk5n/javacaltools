/*
 * Copyright (C) 2005-2024 Craig Knudsen and other authors
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
 * iCalendar Summary class - This object represents a summary and corresponds to
 * the SUMMARY iCalendar property.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class Summary extends TextProperty {

    /**
     * Default constructor
     */
    public Summary() {
        super("SUMMARY");
    }

    /**
     * Constructor from iCalendar string
     *
     * @param icalStr One or more lines of iCalendar that specifies an event/todo summary
     */
    public Summary(String icalStr) throws ParseException {
        super("SUMMARY", icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor from iCalendar string with parse mode
     *
     * @param icalStr   One or more lines of iCalendar that specifies an event/todo summary
     * @param parseMode PARSE_STRICT or PARSE_LOOSE
     */
    public Summary(String icalStr, int parseMode) throws ParseException {
        super("SUMMARY", icalStr, parseMode);
    }
}
