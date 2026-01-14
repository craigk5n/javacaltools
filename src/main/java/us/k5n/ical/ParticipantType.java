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
 * iCalendar PARTICIPANT-TYPE property - This object represents a participant type
 * and corresponds to the PARTICIPANT-TYPE iCalendar property as defined in RFC 9073.
 *
 * PARTICIPANT-TYPE defines the type of participation in events/tasks.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class ParticipantType extends Property {

    // RFC 9073 defined participant type values
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String SPONSOR = "SPONSOR";
    public static final String CONTACT = "CONTACT";
    public static final String BOOKING_CONTACT = "BOOKING-CONTACT";
    public static final String EMERGENCY_CONTACT = "EMERGENCY-CONTACT";
    public static final String PUBLICITY_CONTACT = "PUBLICITY-CONTACT";
    public static final String PLANNER_CONTACT = "PLANNER-CONTACT";
    public static final String PERFORMER = "PERFORMER";
    public static final String SPEAKER = "SPEAKER";

    /**
     * Constructor
     */
    public ParticipantType() {
        super("PARTICIPANT-TYPE", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a participant type
     */
    public ParticipantType(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a participant type
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public ParticipantType(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the participant type to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("PARTICIPANT-TYPE");

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
        return "ParticipantType[value=" + getValue() + "]";
    }

    /**
     * Check if the participant type is valid according to RFC 9073
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidType() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        return ACTIVE.equals(value) ||
               INACTIVE.equals(value) ||
               SPONSOR.equals(value) ||
               CONTACT.equals(value) ||
               BOOKING_CONTACT.equals(value) ||
               EMERGENCY_CONTACT.equals(value) ||
               PUBLICITY_CONTACT.equals(value) ||
               PLANNER_CONTACT.equals(value) ||
               PERFORMER.equals(value) ||
               SPEAKER.equals(value) ||
               isIanaToken(value);
    }

    /**
     * Check if the value is a valid IANA token (starts with X- or is all uppercase)
     *
     * @param value the value to check
     * @return true if it's a valid IANA token
     */
    private boolean isIanaToken(String value) {
        // IANA tokens should be uppercase and may start with X-
        return value.toUpperCase().equals(value) ||
               value.toUpperCase().startsWith("X-");
    }
}