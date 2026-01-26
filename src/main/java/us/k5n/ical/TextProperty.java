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
 * Base class for text-based iCalendar properties that support ALTREP and LANGUAGE attributes.
 *
 * <p>This class eliminates code duplication across Summary, Description, Comment, and similar
 * text properties. It provides consistent handling of the ALTREP and LANGUAGE parameters
 * defined in RFC 5545.</p>
 *
 * <p><b>RFC 5545 Compliance:</b></p>
 * <ul>
 *   <li>Section 3.2.2 - ALTREP Parameter (Alternate Text Representation)</li>
 *   <li>Section 3.2.10 - LANGUAGE Parameter</li>
 * </ul>
 *
 * <p><b>Properties extending this class:</b></p>
 * <ul>
 *   <li>Section 3.2.35 - SUMMARY</li>
 *   <li>Section 3.2.4 - DESCRIPTION</li>
 *   <li>Section 3.2.16 - COMMENT</li>
 *   <li>Section 3.2.1 - CATEGORIES</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.2.2">RFC 5545, Section 3.2.2 (ALTREP)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.2.10">RFC 5545, Section 3.2.10 (LANGUAGE)</a>
 */
public abstract class TextProperty extends Property {

    /** Alternate representation URI */
    protected String altrep = null;

    /** Language specification */
    protected String language = null;

    /**
     * Constructor for TextProperty
     *
     * @param propertyName The name of the property (e.g., "SUMMARY", "DESCRIPTION")
     */
    protected TextProperty(String propertyName) {
        super(propertyName, "");
    }

    /**
     * Constructor for TextProperty from iCalendar data
     *
     * @param propertyName The name of the property
     * @param icalStr      The iCalendar string to parse
     */
    protected TextProperty(String propertyName, String icalStr) throws ParseException {
        this(propertyName, icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor for TextProperty from iCalendar data with parse mode
     *
     * @param propertyName The name of the property
     * @param icalStr      The iCalendar string to parse
     * @param parseMode    PARSE_STRICT or PARSE_LOOSE
     */
    protected TextProperty(String propertyName, String icalStr, int parseMode) throws ParseException {
        super(icalStr, parseMode);
        parseTextAttributes(parseMode, icalStr);
    }

    /**
     * Parse ALTREP and LANGUAGE attributes from the property's attribute list.
     * This method is called by subclasses after super() constructor completes.
     *
     * @param parseMode The parse mode (PARSE_STRICT or PARSE_LOOSE)
     * @param icalStr   The original iCalendar string (for error messages)
     * @throws ParseException if strict parsing and invalid attributes are found
     */
    protected final void parseTextAttributes(int parseMode, String icalStr) throws ParseException {
        for (Attribute a : attributeList) {
            String aname = a.name.toUpperCase();
            if ("ALTREP".equals(aname)) {
                if (altrep != null && parseMode == PARSE_STRICT) {
                    throw new ParseException("More than one ALTREP found", icalStr);
                }
                altrep = a.value;
            } else if ("LANGUAGE".equals(aname)) {
                if (language != null && parseMode == PARSE_STRICT) {
                    throw new ParseException("More than one LANGUAGE found", icalStr);
                }
                language = a.value;
            } else if (parseMode == PARSE_STRICT) {
                throw new ParseException("Invalid " + getName() + " attribute '" + a.name + "'", icalStr);
            }
        }
    }

    /**
     * Get the alternate representation URI
     *
     * @return the alternate representation URI, or null if not set
     */
    public String getAltrep() {
        return altrep;
    }

    /**
     * Set the alternate representation URI
     *
     * @param altrep the alternate representation URI
     */
    public void setAltrep(String altrep) {
        this.altrep = altrep;
        if (altrep != null) {
            addAttribute("ALTREP", altrep);
        }
    }

    /**
     * Get the language specification
     *
     * @return the language, or null if not set
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language specification
     *
     * @param language the language code
     */
    public void setLanguage(String language) {
        this.language = language;
        if (language != null) {
            addAttribute("LANGUAGE", language);
        }
    }

    /**
     * Override toICalendar to include ALTREP and LANGUAGE attributes in output
     */
    @Override
    public String toICalendar() {
        StringBuilder ret = new StringBuilder(64);
        ret.append(getName());

        if (altrep != null) {
            ret.append(";ALTREP=\"").append(altrep).append('"');
        }
        if (language != null) {
            ret.append(";LANGUAGE=").append(language);
        }

        ret.append(':').append(getValue());
        return StringUtils.foldLine(ret.toString());
    }
}
