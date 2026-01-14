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
 * iCalendar SOURCE property - This object represents a source URI
 * and corresponds to the SOURCE iCalendar property as defined in RFC 7986.
 *
 * SOURCE identifies a URI where calendar data can be refreshed from.
 *
 * @author Assistant
 */
public class Source extends Property {

    /**
     * Constructor
     */
    public Source() {
        super("SOURCE", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a source URI
     */
    public Source(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a source URI
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public Source(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the source to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("SOURCE;VALUE=URI:").append(getValue()).append(CRLF);
        return ret.toString();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "Source[value=" + getValue() + "]";
    }

    /**
     * Check if the source URI is valid (basic validation)
     *
     * @return true if potentially valid URI, false otherwise
     */
    public boolean isValidSource() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        // Basic validation: should start with a scheme
        return value.contains("://") || value.startsWith("mailto:") ||
               value.startsWith("data:") || value.startsWith("urn:");
    }

    /**
     * Get the source as a URI object
     *
     * @return URI string
     */
    public String getUri() {
        return getValue();
    }
}