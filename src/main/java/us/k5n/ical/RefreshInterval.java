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
 * iCalendar REFRESH-INTERVAL property - This object represents a refresh interval
 * and corresponds to the REFRESH-INTERVAL iCalendar property as defined in RFC 7986.
 *
 * REFRESH-INTERVAL specifies a suggested minimum interval for polling for changes.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class RefreshInterval extends Property {

    /**
     * Constructor
     */
    public RefreshInterval() {
        super("REFRESH-INTERVAL", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a refresh interval
     */
    public RefreshInterval(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a refresh interval
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public RefreshInterval(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the refresh interval to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("REFRESH-INTERVAL;VALUE=DURATION:").append(getValue()).append(CRLF);
        return ret.toString();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "RefreshInterval[value=" + getValue() + "]";
    }

    /**
     * Check if the refresh interval is valid (positive duration)
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidInterval() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        // Check for negative durations (not allowed for refresh intervals)
        if (value.startsWith("-")) return false;

        // Check if we can create a valid Duration object
        try {
            new Duration("DURATION:" + value);
            return true;
        } catch (ParseException | BogusDataException e) {
            return false;
        }
    }

    /**
     * Get the refresh interval as a Duration object
     *
     * @return Duration object representing the interval
     */
    public Duration getDuration() {
        try {
            return new Duration("DURATION:" + getValue());
        } catch (ParseException | BogusDataException e) {
            return null;
        }
    }
}