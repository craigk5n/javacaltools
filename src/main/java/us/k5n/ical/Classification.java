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
 * The Classification class represents the iCalendar Classification for Event,
 * Todo and Journal objects.
 * 
 * <br/>
 * From section 4.8.1.3 RFC 2445:<blockquote> This property defines the access
 * classification for a calendar component. <br/>
 * <br/>
 * Property Parameters: Non-standard property parameters can be specified on
 * this property. <br/>
 * <br/>
 * Conformance: The property can be specified once in a "VEVENT", "VTODO" or
 * "VJOURNAL" calendar components. <br/>
 * <br/>
 * Description: An access classification is only one component of the general
 * security system within a calendar application. It provides a method of
 * capturing the scope of the access the calendar owner intends for information
 * within an individual calendar entry. The access classification of an
 * individual iCalendar component is useful when measured along with the other
 * security components of a calendar system (e.g., calendar user authentication,
 * authorization, access rights, access role, etc.). Hence, the semantics of the
 * individual access classifications cannot be completely defined by this memo
 * alone. Additionally, due to the "blind" nature of most exchange processes
 * using this memo, these access classifications cannot serve as an enforcement
 * statement for a system receiving an iCalendar object. Rather, they provide a
 * method for capturing the intention of the calendar owner for the access to
 * the calendar component. </blockquote>
 * 
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Classification extends Property implements Constants {
	int classification = PUBLIC;

	/**
	 * Generate an iCalendar classification based on an iCalendar line of text.
	 * 
	 * @param icalStr One or more lines of iCalendar that specifies a duration.
	 *                Durations should follow the ISO 8601 format.
	 */
	public Classification(String icalStr) throws ParseException, BogusDataException {
		this(icalStr, PARSE_LOOSE);
	}

	/**
	 * Constructor
	 * 
	 * @param classification The type of classification (PUBLIC, PRIVATE,
	 *                       CONFIDENTIAL)
	 */
	public Classification(int classification) {
		super("CLASS", "");
		this.classification = classification;
		switch (classification) {
			case PUBLIC:
				this.value = "PUBLIC";
				break;
			case CONFIDENTIAL:
				this.value = "CONFIDENTIAL";
				break;
			case PRIVATE:
				this.value = "PRIVATE";
				break;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr   One or more lines of iCalendar that specifies a duration
	 * @param parseMode PARSE_STRICT or PARSE_LOOSE
	 */
	public Classification(String icalStr, int parseMode) throws ParseException, BogusDataException {
		super(icalStr, parseMode);

		for (int i = 0; i < attributeList.size(); i++) {
			Attribute a = attributeAt(i);
			String aval = a.value.toUpperCase();
			// TODO: not sure if any attributes are allowed here...
		}

		if (value == null || value.length() == 0)
			throw new ParseException("No valid classification found", icalStr);
		String aval = value.toUpperCase().trim();
		if (aval.equals("PUBLIC")) {
			this.classification = PUBLIC;
		} else if (aval.equals("CONFIDENTIAL")) {
			this.classification = CONFIDENTIAL;
		} else if (aval.equals("PRIVATE")) {
			this.classification = PRIVATE;
		} else {
			// Allowed to have other values here...
		}
	}

	/**
	 * Get the classification (PUBLIC, PRIVATE, CONFIDENTIAL)
	 * 
	 * @return Returns the classification
	 */
	public int getClassification() {
		return classification;
	}

	/**
	 * Set the classification (PUBLIC, PRIVATE, CONFIDENTIAL)
	 * 
	 * @param classification The classification to set. PUBLIC, PRIVATE,
	 *                       CONFIDENTIAL)
	 */
	public void setClassification(int classification) {
		this.classification = classification;
	}

}
