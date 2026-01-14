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
 * iCalendar STYLED-DESCRIPTION property - This object represents a styled
 * description and corresponds to the STYLED-DESCRIPTION iCalendar property
 * as defined in RFC 9073.
 *
 * STYLED-DESCRIPTION provides rich-text descriptions (HTML, Markdown, etc.)
 * for events, tasks, and journals.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class StyledDescription extends Property {
    /** Format type (e.g., "text/html", "text/markdown") */
    public String fmtType = null;
    /** Whether description is derived/generated */
    public String derived = null;

    /**
     * Constructor
     */
    public StyledDescription() {
        super("STYLED-DESCRIPTION", "");
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a styled description
     */
    public StyledDescription(String icalStr) throws ParseException {
        this(icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor
     *
     * @param icalStr
     *          One or more lines of iCalendar that specifies a styled description
     * @param parseMode
     *          PARSE_STRICT or PARSE_LOOSE
     */
    public StyledDescription(String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);

        // Parse styled description-specific attributes
        parseStyledDescriptionAttributes(parseMode);
    }

    /**
     * Parse styled description-specific attributes
     */
    private void parseStyledDescriptionAttributes(int parseMode) {
        for (int i = 0; i < attributeList.size(); i++) {
            Attribute a = attributeAt(i);
            String aname = a.name.toUpperCase();
            if (aname.equals("FMTTYPE")) {
                fmtType = a.value;
            } else if (aname.equals("DERIVED")) {
                derived = a.value;
            } else {
                // Only generate exception if strict parsing
                if (parseMode == PARSE_STRICT) {
                    // For now, allow unknown attributes in loose mode
                    // Could add validation here for known attributes
                }
            }
        }
    }

    /**
     * Get the format type
     *
     * @return the format type (e.g., "text/html")
     */
    public String getFmtType() {
        return fmtType;
    }

    /**
     * Set the format type
     *
     * @param fmtType the format type
     */
    public void setFmtType(String fmtType) {
        this.fmtType = fmtType;
    }

    /**
     * Get the derived flag
     *
     * @return the derived flag ("TRUE" or "FALSE")
     */
    public String getDerived() {
        return derived;
    }

    /**
     * Set the derived flag
     *
     * @param derived the derived flag
     */
    public void setDerived(String derived) {
        this.derived = derived;
    }

    /**
     * Check if this description is derived
     *
     * @return true if derived, false otherwise
     */
    public boolean isDerived() {
        return "TRUE".equalsIgnoreCase(derived);
    }

    /**
     * Convert the styled description to iCalendar format
     *
     * @return iCalendar formatted string
     */
    public String toICalendar() {
        StringBuffer ret = new StringBuffer();
        ret.append("STYLED-DESCRIPTION");

        // Add parameters
        List<String> params = new ArrayList<>();
        if (fmtType != null) {
            params.add("FMTTYPE=" + fmtType);
        }
        if (derived != null) {
            params.add("DERIVED=" + derived);
        }

        if (!params.isEmpty()) {
            ret.append(";").append(String.join(";", params));
        }

        ret.append(":").append(getValue()).append(CRLF);
        return ret.toString();
    }

    /**
     * Convert to string representation
     */
    public String toString() {
        return "StyledDescription[fmtType=" + fmtType + ", derived=" + derived +
               ", value=" + getValue() + "]";
    }
}