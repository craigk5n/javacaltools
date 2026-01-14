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
 * iCalendar VLOCATION component - This object represents a location component
 * and corresponds to the VLOCATION iCalendar component as defined in RFC 9073.
 *
 * VLOCATION provides rich information about event locations using structured data.
 *
 * @author Craig Knudsen, craig@k5n.us
 * @ai-generated Grok-4.1-Fast
 */
public class VLocation extends Property {
    /** Location UID */
    public String uid = null;
    /** Location name */
    public String name = null;
    /** Location description */
    public String description = null;
    /** Geographic location (latitude;longitude) */
    public String geo = null;
    /** Location type */
    public LocationType locationType = null;
    /** List of structured data URIs */
    public List<String> structuredDataList;

    /**
     * Constructor
     */
    public VLocation() {
        super("VLOCATION", "");
        structuredDataList = new ArrayList<String>();
    }

    /**
     * Constructor for parser
     *
     * @param parser the parser instance
     * @param initialLineNo the initial line number
     * @param textLines the text lines
     */
    public VLocation(ICalendarParser parser, int initialLineNo, List<String> textLines) throws ParseException, BogusDataException {
        this();
        parseComponent(parser, initialLineNo, textLines);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies the VLOCATION component
     */
    public VLocation(String icalStr) throws ParseException {
        this();
        parseVLocation(icalStr);
    }

    /**
     * Parse component using parser infrastructure
     */
    private void parseComponent(ICalendarParser parser, int initialLineNo, List<String> textLines) throws ParseException, BogusDataException {
        // Convert text lines back to a single string and parse
        StringBuilder sb = new StringBuilder();
        for (String line : textLines) {
            sb.append(line).append("\n");
        }
        parseVLocation(sb.toString());
    }

    /**
     * Parse VLOCATION component from iCalendar string
     */
    private void parseVLocation(String icalStr) throws ParseException {
        String[] lines = icalStr.split("\n");
        boolean inVLocation = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.equals("BEGIN:VLOCATION")) {
                inVLocation = true;
            } else if (trimmed.equals("END:VLOCATION")) {
                break;
            } else if (inVLocation) {
                if (trimmed.startsWith("UID:")) {
                    uid = trimmed.substring(4).trim();
                } else if (trimmed.startsWith("NAME")) {
                    try {
                        name = new Name(line).getValue();
                    } catch (ParseException e) {
                        // Handle parse error
                    }
                } else if (trimmed.startsWith("DESCRIPTION")) {
                    try {
                        description = new Description(line).getValue();
                    } catch (ParseException e) {
                        // Handle parse error
                    }
                } else if (trimmed.startsWith("GEO:")) {
                    geo = trimmed.substring(4).trim();
                } else if (trimmed.startsWith("LOCATION-TYPE")) {
                    try {
                        locationType = new LocationType(line);
                    } catch (ParseException e) {
                        // Handle parse error
                    }
                } else if (trimmed.startsWith("STRUCTURED-DATA")) {
                    // Extract URI value from STRUCTURED-DATA property
                    if (line.contains(":")) {
                        String value = line.substring(line.indexOf(":") + 1).trim();
                        structuredDataList.add(value);
                    }
                }
                // Ignore other properties for now
            }
        }
    }

    /**
     * Convert the VLOCATION to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("BEGIN:VLOCATION").append(CRLF);

        if (uid != null) {
            ret.append("UID:").append(uid).append(CRLF);
        }
        if (name != null) {
            ret.append("NAME:").append(name).append(CRLF);
        }
        if (description != null) {
            ret.append("DESCRIPTION:").append(description).append(CRLF);
        }
        if (geo != null) {
            ret.append("GEO:").append(geo).append(CRLF);
        }
        if (locationType != null) {
            ret.append(locationType.toICalendar());
        }

        for (String sd : structuredDataList) {
            ret.append("STRUCTURED-DATA;VALUE=URI:").append(sd).append(CRLF);
        }

        ret.append("END:VLOCATION").append(CRLF);
        return ret.toString();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "VLocation[uid=" + uid + ", name=" + name + "]";
    }

    /**
     * Check if the VLOCATION is valid (has required UID)
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return uid != null && !uid.trim().isEmpty();
    }

    /**
     * Get the location UID
     *
     * @return the UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Set the location UID
     *
     * @param uid the UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get the location name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the location name
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the location description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the location description
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the geographic location
     *
     * @return the geo coordinates as string
     */
    public String getGeo() {
        return geo;
    }

    /**
     * Set the geographic location
     *
     * @param geo the geo coordinates as string
     */
    public void setGeo(String geo) {
        this.geo = geo;
    }

    /**
     * Get the location type
     *
     * @return the location type
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * Set the location type
     *
     * @param locationType the location type
     */
    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    /**
     * Get the structured data URI list
     *
     * @return the structured data URI list
     */
    public List<String> getStructuredDataList() {
        return structuredDataList;
    }

    /**
     * Add structured data URI
     *
     * @param structuredDataUri the structured data URI to add
     */
    public void addStructuredData(String structuredDataUri) {
        this.structuredDataList.add(structuredDataUri);
    }
}