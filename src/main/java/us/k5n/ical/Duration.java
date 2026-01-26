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
 * iCalendar Duration class that corresponds to duration-related iCalendar properties.
 *
 * <p>A DURATION property specifies a positive duration of time. Duration values
 * are used in properties like DURATION, TRIGGER, and REFRESH-INTERVAL.</p>
 *
 * <p><b>RFC 5545 Compliance:</b></p>
 * <ul>
 *   <li>Section 3.3.6 - DURATION value type</li>
 *   <li>Section 3.2.8 - DURATION property</li>
 *   <li>Section 3.2.38 - TRIGGER property (in VALARM)</li>
 * </ul>
 *
 * <p><b>ISO 8601 Duration Format:</b></p>
 * <ul>
 *   <li>P1W - 1 week</li>
 *   <li>P1D - 1 day</li>
 *   <li>PT1H - 1 hour</li>
 *   <li>PT1M - 1 minute</li>
 *   <li>PT1S - 1 second</li>
 *   <li>P15DT5H0M20S - 15 days, 5 hours, 20 seconds</li>
 *   <li>-PT30M - negative 30 minutes (for alarm triggers)</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5545#section-3.3.6">RFC 5545, Section 3.3.6</a>
 */
public class Duration extends Property {
	/** duration in seconds */
	int duration = 0;

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *                One or more lines of iCalendar that specifies a duration.
	 *                Durations should follow the ISO 8601 format.
	 */
	public Duration(String icalStr) throws ParseException, BogusDataException {
		this(icalStr, PARSE_LOOSE);
	}

	/**
	 * Constructor
	 * 
	 * @param seconds
	 *                The number of seconds for the duration
	 */
	public Duration(int seconds) {
		super("DURATION", "");
		this.duration = seconds;
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *                  One or more lines of iCalendar that specifies a duration
	 * @param parseMode
	 *                  PARSE_STRICT or PARSE_LOOSE
	 */
	public Duration(String icalStr, int parseMode) throws ParseException,
			BogusDataException {
		super(icalStr, parseMode);

		for (int i = 0; i < attributeList.size(); i++) {
			Attribute a = attributeAt(i);
			String aval = a.value.toUpperCase();
			// TODO: not sure if any attributes are allowed here...
		}

		// Convert value into a duration
		duration = parseDuration(value);
	}

	/**
	 * Parse a duration specified in ISO 8601 format.
	 * 
	 * @return The number of seconds for the duration
	 */
	public static int parseDuration(String durStr) throws ParseException {
		int numSecs = 0;
		int start = 0;
		boolean isNeg = false;
		StringBuffer sb = new StringBuffer();

		// trim it and swith to uppercase to be safe
		durStr = durStr.toUpperCase().trim();

		// ignore a leading '+'
		if (durStr.charAt(0) == '+')
			start = 1;
		else if (durStr.charAt(0) == '-') {
			start = 1;
			isNeg = true;
		}

		if (durStr.charAt(start) != 'P') {
			throw new ParseException("Duration '" + durStr
					+ "' must start with 'P'", durStr);
		}
		// skip over 'P'
		start++;

		for (int i = start; i < durStr.length(); i++) {
			char ch = durStr.charAt(i);
			if (ch >= '0' && ch <= '9') {
				// it's a digit
				sb.append(ch);
			} else {
				// Not a digit
				int n = 0;
				if (sb.length() > 0)
					n = Integer.parseInt(sb.toString());
				switch (ch) {
					case 'T': // week
						// means we are switching from day duration to H/M/S duration
						if (sb.length() != 0)
							throw new ParseException("Duration has leading '" + sb
									+ "' before 'T'", durStr);
						break;
					case 'W': // week
						numSecs += n * 7 * 24 * 3600;
						break;
					case 'D': // day
						numSecs += n * 24 * 3600;
						break;
					case 'H': // hour
						numSecs += n * 3600;
						break;
					case 'M': // minute
						numSecs += n * 60;
						break;
					case 'S': // second
						numSecs += n;
						break;
					default: // ???
						throw new ParseException(
								"Duration has invalid char '" + ch + "'", durStr);
				}
				sb.setLength(0); // truncate StringBuffer
			}
		}
		return (isNeg ? 0 - numSecs : numSecs);
	}

}
