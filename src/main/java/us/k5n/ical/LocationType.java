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
 * iCalendar LOCATION-TYPE property - This object represents a location type
 * and corresponds to the LOCATION-TYPE iCalendar property as defined in RFC 9073.
 *
 * LOCATION-TYPE provides a way to differentiate multiple locations in VLOCATION components.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class LocationType extends Property {

    /**
     * Constructor
     */
    public LocationType() {
        super("LOCATION-TYPE", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a location type
     */
    public LocationType(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a location type
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public LocationType(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the location type to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("LOCATION-TYPE");

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
        return "LocationType[value=" + getValue() + "]";
    }
}