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

import java.util.List;
import java.util.ArrayList;

/**
 * iCalendar VLOCATION class that corresponds to the VLOCATION iCalendar component.
 * A VLOCATION component describes a location with structured properties.
 *
 * From RFC 5545: The "VLOCATION" component defines a location with
 * structured properties. It can be used to define locations that can be
 * referenced by other components.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class VLocation implements Constants {
	/** Unique Id */
	protected Uid uid = null;
	/** Display name */
	protected String name = null;
	/** Description */
	protected Description description = null;
	/** Geographic position */
	protected String geo = null;
	/** Location type */
	protected String locationType = null;
	/** Categories */
	protected Categories categories = null;
	/** Time created */
	protected Date createdDate = null;
	/** Time last modified */
	protected Date lastModified = null;
	/** URL */
	protected URL url = null;
	/** Private user object for caller to set/get */
	private Object userData = null;

	/**
	 * Create a VLocation object based on specified iCalendar data
	 *
	 * @param parser
	 *                    The ICalendarParser object
	 * @param initialLine
	 *                    The starting line number
	 * @param textLines
	 *        List of iCalendar text lines
	 */
	public VLocation(CalendarParser parser, int initialLine, List<String> textLines) {
		for (int i = 0; i < textLines.size(); i++) {
			String line = textLines.get(i);
			try {
				parseLine(line, parser.getParseMethod());
			} catch (BogusDataException bde) {
				parser.reportParseError(new ParseError(initialLine + i, bde.error,
						line));
			} catch (ParseException pe) {
				parser.reportParseError(new ParseError(initialLine + i, pe.error,
						line));
			}
		}
		// VLOCATION requires UID to be explicitly provided (unlike other components)
		// Don't auto-generate UID
	}

	/**
	 * Was enough information parsed for this VLocation to be valid?
	 */
	public boolean isValid() {
		// Must have at least a UID and name
		return (uid != null && name != null);
	}

	/**
	 * Parse a line of iCalendar text.
	 *
	 * @param line
	 *                    The line of text
	 * @param parseMethod
	 *                    PARSE_STRICT or PARSE_LOOSE
	 */
	public void parseLine(String icalStr, int parseMethod)
			throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		if (up.equals("BEGIN:VLOCATION") || up.equals("END:VLOCATION")) {
			// ignore
		} else if (up.trim().length() == 0) {
			// ignore empty lines
		} else if (up.startsWith("NAME")) {
			name = icalStr.substring(icalStr.indexOf(':') + 1);
		} else if (up.startsWith("DESCRIPTION")) {
			description = new Description(icalStr);
		} else if (up.startsWith("GEO")) {
			Property p = new Property(icalStr);
			geo = p.value;
		} else if (up.startsWith("LOCATION-TYPE")) {
			locationType = icalStr.substring(icalStr.indexOf(':') + 1);
		} else if (up.startsWith("CATEGORIES")) {
			categories = new Categories(icalStr);
		} else if (up.startsWith("CREATED")) {
			createdDate = new Date(icalStr);
		} else if (up.startsWith("LAST-MODIFIED")) {
			lastModified = new Date(icalStr);
		} else if (up.startsWith("UID")) {
			uid = new Uid(icalStr);
		} else if (up.startsWith("URL")) {
			url = new URL(icalStr, parseMethod);
		} else {
			System.err.println("Ignoring VLOCATION line: " + icalStr);
		}
	}

	/**
	 * Convert this VLocation into iCalendar text
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(128);
		ret.append("BEGIN:VLOCATION");
		ret.append(CRLF);

		if (uid != null)
			ret.append(uid.toICalendar());
		if (name != null) {
			ret.append("NAME:");
			ret.append(name);
			ret.append(CRLF);
		}
		if (description != null)
			ret.append(description.toICalendar());
		if (geo != null) {
			ret.append("GEO:");
			ret.append(geo);
			ret.append(CRLF);
		}
		if (locationType != null) {
			ret.append("LOCATION-TYPE:");
			ret.append(locationType);
			ret.append(CRLF);
		}
		if (categories != null)
			ret.append(categories.toICalendar());
		if (createdDate != null)
			ret.append(createdDate.toICalendar());
		if (lastModified != null)
			ret.append(lastModified.toICalendar());
		if (url != null)
			ret.append(url.toICalendar());

		ret.append("END:VLOCATION");
		ret.append(CRLF);

		return ret.toString();
	}

	// Getter and setter methods

	public Uid getUid() {
		return uid;
	}

	public void setUid(Uid uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public Categories getCategories() {
		return categories;
	}

	public void setCategories(Categories categories) {
		this.categories = categories;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}
}