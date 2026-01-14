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
 * iCalendar Organizer class - This object represents an organizer and corresponds to
 * ORGANIZER iCalendar property.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
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
