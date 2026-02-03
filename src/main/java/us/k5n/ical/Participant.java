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

import java.util.ArrayList;
import java.util.List;

/**
 * iCalendar PARTICIPANT component - This object represents a participant and
 * corresponds to the PARTICIPANT iCalendar component as defined in RFC 9073.
 *
 * The PARTICIPANT component provides rich participant metadata beyond the
 * basic ATTENDEE property.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class Participant extends Property {
    /** Participant UID */
    public String uid = null;
    /** Participant type */
    public String participantType = null;
    /** Calendar address */
    public CalendarAddress calendarAddress = null;
    /** Structured data */
    public String structuredData = null;
    /** Display name */
    public String name = null;
    /** Description */
    public String description = null;

    /** List of attributes for this participant */
    protected List<Attribute> attributeList;

    /**
     * Constructor
     */
    public Participant() {
        super("PARTICIPANT", "");
        attributeList = new ArrayList<Attribute>();
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a participant
     */
    public Participant(String icalStr) {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor for simple participant creation
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a participant
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public Participant(String icalStr, int parseMode) {
        super("PARTICIPANT", ""); // Initialize as empty participant
        attributeList = new ArrayList<Attribute>();

        // Parse participant-specific properties from the content
        parseParticipantPropertiesFromString(icalStr, parseMode);
    }

    /**
     * Constructor
     *
     * @param parser
     *          The ICalendarParser instance
     * @param startLineNo
     *          Starting line number in the input
     * @param textLines
     *          List of text lines for this component
     */
    public Participant(ICalendarParser parser, int startLineNo, List<String> textLines) {
        super("PARTICIPANT", ""); // Initialize with empty participant
        attributeList = new ArrayList<Attribute>();

        // Parse the participant properties from textLines
        for (int i = 0; i < textLines.size(); i++) {
            String line = textLines.get(i);
            parseParticipantLine(line, parser.getParseMethod());
        }
    }

    /**
     * Parse a single participant line
     */
    private void parseParticipantLine(String line, int parseMode) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return;

        if (trimmed.startsWith("UID:")) {
            uid = trimmed.substring(4).trim();
        } else if (trimmed.startsWith("PARTICIPANT-TYPE:")) {
            participantType = trimmed.substring(17).trim();
        } else if (trimmed.startsWith("CALENDAR-ADDRESS")) {
            try {
                calendarAddress = new CalendarAddress(line);
            } catch (ParseException e) {
                // Handle parse error - could log or ignore
            }
        } else if (trimmed.startsWith("STRUCTURED-DATA:")) {
            structuredData = trimmed.substring(16).trim();
        } else if (trimmed.startsWith("NAME:")) {
            name = trimmed.substring(5).trim();
        } else if (trimmed.startsWith("DESCRIPTION:")) {
            description = trimmed.substring(12).trim();
        }
        // Ignore other properties for now
    }

    /**
     * Parse participant-specific properties from the content (for simple constructor)
     */
    private void parseParticipantPropertiesFromString(String icalStr, int parseMode) {
        String[] lines = icalStr.split("\n");
        for (String line : lines) {
            parseParticipantLine(line, parseMode);
        }
    }

    /**
     * Get the participant UID
     *
     * @return the participant UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Set the participant UID
     *
     * @param uid the participant UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get the participant type
     *
     * @return the participant type
     */
    public String getParticipantType() {
        return participantType;
    }

    /**
     * Set the participant type
     *
     * @param participantType the participant type
     */
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    /**
     * Get the calendar address
     *
     * @return the calendar address
     */
    public CalendarAddress getCalendarAddress() {
        return calendarAddress;
    }

    /**
     * Set the calendar address
     *
     * @param calendarAddress the calendar address
     */
    public void setCalendarAddress(CalendarAddress calendarAddress) {
        this.calendarAddress = calendarAddress;
    }

    /**
     * Get the structured data
     *
     * @return the structured data
     */
    public String getStructuredData() {
        return structuredData;
    }

    /**
     * Set the structured data
     *
     * @param structuredData the structured data
     */
    public void setStructuredData(String structuredData) {
        this.structuredData = structuredData;
    }

    /**
     * Get the display name
     *
     * @return the display name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the display name
     *
     * @param name the display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Convert the participant to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("BEGIN:PARTICIPANT").append(CRLF);

        if (uid != null) {
            ret.append("UID:").append(uid).append(CRLF);
        }
        if (participantType != null) {
            ret.append("PARTICIPANT-TYPE:").append(participantType).append(CRLF);
        }
        if (calendarAddress != null) {
            ret.append(calendarAddress.toICalendar());
        }
        if (structuredData != null) {
            ret.append("STRUCTURED-DATA:").append(structuredData).append(CRLF);
        }
        if (name != null) {
            ret.append("NAME:").append(name).append(CRLF);
        }
        if (description != null) {
            ret.append("DESCRIPTION:").append(description).append(CRLF);
        }

        ret.append("END:PARTICIPANT").append(CRLF);
        return ret.toString();
    }

    /**
     * Validate the participant data
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        // UID is required for participants
        return uid != null && !uid.trim().isEmpty();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "Participant[uid=" + uid + ", type=" + participantType +
               ", address=" + calendarAddress + ", name=" + name + "]";
    }
}