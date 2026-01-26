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
 * Base class for simple iCalendar properties that store a value without additional attributes.
 *
 * <p>This class eliminates code duplication across Location, Name, and similar simple properties
 * that only need to store a single value without special parameters.</p>
 *
 * <p><b>RFC 5545 Compliance:</b></p>
 * <ul>
 *   <li>Section 3.2 - General property value specifications</li>
 * </ul>
 *
 * <p><b>Properties extending this class:</b></p>
 * <ul>
 *   <li>Section 3.2.10 - LOCATION</li>
 *   <li>Section 5.1 - NAME (RFC 7986)</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.2">RFC 5545, Section 3.2</a>
 */
public abstract class SimpleProperty extends Property {

    /**
     * Constructor for SimpleProperty
     *
     * @param propertyName The name of the property (e.g., "LOCATION", "NAME")
     */
    protected SimpleProperty(String propertyName) {
        super(propertyName, "");
    }

    /**
     * Constructor for SimpleProperty from iCalendar data
     *
     * @param propertyName The name of the property
     * @param icalStr      The iCalendar string to parse
     */
    protected SimpleProperty(String propertyName, String icalStr) throws ParseException {
        super(icalStr); // Parse the iCalendar line (e.g., "NAME:value")
    }

    /**
     * Constructor for SimpleProperty from iCalendar data with parse mode
     *
     * @param propertyName The name of the property
     * @param icalStr      The iCalendar string to parse
     * @param parseMode    PARSE_STRICT or PARSE_LOOSE
     */
    protected SimpleProperty(String propertyName, String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode); // Parse the iCalendar line with mode
    }

    /**
     * Convert to string representation
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[value=" + getValue() + "]";
    }
}
