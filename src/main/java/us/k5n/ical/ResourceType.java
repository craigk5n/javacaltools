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
 * iCalendar RESOURCE-TYPE property - This object represents a resource type
 * and corresponds to the RESOURCE-TYPE iCalendar property as defined in RFC 9073.
 *
 * RESOURCE-TYPE provides a way to differentiate multiple resources in VRESOURCE components.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class ResourceType extends Property {

    // RFC 9073 defined resource type values
    public static final String ROOM = "ROOM";
    public static final String PROJECTOR = "PROJECTOR";
    public static final String REMOTE_CONFERENCE_AUDIO = "REMOTE-CONFERENCE-AUDIO";
    public static final String REMOTE_CONFERENCE_VIDEO = "REMOTE-CONFERENCE-VIDEO";

    /**
     * Constructor
     */
    public ResourceType() {
        super("RESOURCE-TYPE", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a resource type
     */
    public ResourceType(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a resource type
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public ResourceType(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
    }

    /**
     * Convert the resource type to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("RESOURCE-TYPE");

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
        return "ResourceType[value=" + getValue() + "]";
    }

    /**
     * Check if the resource type is valid according to RFC 9073
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidType() {
        String value = getValue();
        if (value == null || value.trim().isEmpty()) return false;

        return ROOM.equals(value) ||
               PROJECTOR.equals(value) ||
               REMOTE_CONFERENCE_AUDIO.equals(value) ||
               REMOTE_CONFERENCE_VIDEO.equals(value) ||
               isIanaToken(value);
    }

    /**
     * Check if the value is a valid IANA token (starts with X- for experimental)
     *
     * @param value the value to check
     * @return true if it's a valid IANA token
     */
    private boolean isIanaToken(String value) {
        // IANA tokens should start with X- for experimental values
        return value.startsWith("X-");
    }
}