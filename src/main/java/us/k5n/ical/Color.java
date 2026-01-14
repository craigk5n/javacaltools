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
 * iCalendar COLOR property - This object represents a color
 * and corresponds to the COLOR iCalendar property as defined in RFC 7986.
 *
 * COLOR specifies a color used for displaying calendar data.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class Color extends Property {

    /**
     * Constructor
     */
    public Color() {
        super("COLOR", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a color
     */
    public Color(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a color
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public Color(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the color to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("COLOR");

        // Add parameters if any
        if (!attributeList.isEmpty()) {
            for (Attribute attr : attributeList) {
                ret.append(";").append(attr.name);
                if (attr.value != null && !attr.value.isEmpty()) {
                    ret.append("=").append(attr.value);
                }
            }
        }

        ret.append(":").append(getValue()).append(CRLF);
        return ret.toString();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "Color[value=" + getValue() + "]";
    }

    /**
     * Check if the color value is a valid CSS3 color name
     * This is a basic validation - in practice, clients should accept any color name
     * and handle unknown ones gracefully.
     *
     * @return true if potentially valid, false otherwise
     */
    public boolean isValidColor() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        // Basic check: should be alphabetic characters only (CSS3 color names)
        return value.matches("[a-zA-Z]+");
    }
}