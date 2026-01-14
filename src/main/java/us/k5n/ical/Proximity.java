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
 * iCalendar PROXIMITY property - This object represents a proximity trigger
 * and corresponds to the PROXIMITY iCalendar property as defined in RFC 9074.
 *
 * PROXIMITY indicates that a location-based trigger is applied to an alarm.
 *
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class Proximity extends Property {

    // RFC 9074 defined proximity values
    public static final String ARRIVE = "ARRIVE";
    public static final String DEPART = "DEPART";
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a proximity trigger
     * @throws ParseException if the string cannot be parsed
     */
    public Proximity(String icalStr) throws ParseException {
        super(icalStr, PARSE_LOOSE);
    }

    /**
     * Convert the proximity trigger to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("PROXIMITY");

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
        return "Proximity[value=" + getValue() + "]";
    }

    /**
     * Check if the proximity value is valid according to RFC 9074
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidProximity() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        return ARRIVE.equals(value) ||
               DEPART.equals(value) ||
               CONNECT.equals(value) ||
               DISCONNECT.equals(value) ||
               isIanaToken(value);
    }

    /**
     * Check if the value is a valid IANA token (starts with X-)
     *
     * @param value the value to check
     * @return true if it's a valid IANA token
     */
    private boolean isIanaToken(String value) {
        // IANA tokens should start with X- for experimental values
        return value.startsWith("X-");
    }

    /**
     * Check if this proximity trigger is for arrival
     *
     * @return true if ARRIVE
     */
    public boolean isArrive() {
        return ARRIVE.equals(getValue());
    }

    /**
     * Check if this proximity trigger is for departure
     *
     * @return true if DEPART
     */
    public boolean isDepart() {
        return DEPART.equals(getValue());
    }

    /**
     * Check if this proximity trigger is for connection
     *
     * @return true if CONNECT
     */
    public boolean isConnect() {
        return CONNECT.equals(getValue());
    }

    /**
     * Check if this proximity trigger is for disconnection
     *
     * @return true if DISCONNECT
     */
    public boolean isDisconnect() {
        return DISCONNECT.equals(getValue());
    }
}