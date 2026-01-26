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
 * iCalendar Organizer class that corresponds to the ORGANIZER iCalendar property.
 *
 * <p>An ORGANIZER property specifies the calendar user who is acting as the
 * organizer of a calendar component. The organizer is responsible for
 * managing the component and may be different from attendees.</p>
 *
 * <p><b>RFC 5545 Compliance:</b></p>
 * <ul>
 *   <li>Section 3.2.13 - Organizer Property</li>
 *   <li>Section 3.2.24 - Cutype Parameter (Calendar User Type)</li>
 *   <li>Section 3.2.2 - Cn Parameter (Common Name)</li>
 *   <li>Section 3.2.8 - Dir Parameter (Directory Entry Reference)</li>
 *   <li>Section 3.2.25 - Sent-by Parameter</li>
 * </ul>
 *
 * <p><b>RFC 9073 Extensions:</b></p>
 * <ul>
 *   <li>Section 4.6 - PARTICIPANT-ID parameter</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.2.13">RFC 5545, Section 3.2.13</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9073">RFC 9073</a>
 */
public class Organizer extends Property {
	String organizerValue = null;
	String participantId = null;

	public Organizer(String icalStr) throws ParseException {
		this(icalStr, PARSE_LOOSE);
	}

	public Organizer(String icalStr, int parseMode) throws ParseException {
		super(icalStr, parseMode);
		organizerValue = value;

		// Parse attributes
		for (int i = 0; i < attributeList.size(); i++) {
			Attribute a = attributeAt(i);
			if (a.name.equals("PARTICIPANT-ID")) {
				participantId = a.value;
			}
		}

		// Validate CAL-ADDRESS format
		if (parseMode == PARSE_STRICT && !Utils.isValidCalAddress(this.getValue())) {
			throw new ParseException("Invalid CAL-ADDRESS format: " + this.getValue(), icalStr);
		}
	}

	public String getOrganizer() {
		return organizerValue;
	}

	public String getParticipantId() {
		return participantId;
	}
}
