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
 * iCalendar Freebusy Period class.
 * Represents a single busy period within a VFREEBUSY component.
 * Format: FREEBUSY:19960403T140000Z/19960403T150000Z
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class FreebusyPeriod {
	/** Start date/time of busy period */
	protected Date startDate = null;
	/** End date/time of busy period */
	protected Date endDate = null;
	/** Duration of busy period */
	protected Duration duration = null;

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *                One or more lines of iCalendar that specifies freebusy period
	 */
	public FreebusyPeriod(String icalStr) throws ParseException, BogusDataException {
		String up = icalStr.toUpperCase();
		
		if (!up.startsWith("FREEBUSY")) {
			throw new ParseException("Not a FREEBUSY property: " + icalStr, icalStr);
		}
		
		int colonIdx = icalStr.indexOf(':');
		if (colonIdx == -1) {
			throw new ParseException("Invalid FREEBUSY format: " + icalStr, icalStr);
		}
		
		String value = icalStr.substring(colonIdx + 1).trim();
		
		parsePeriod(value);
	}

	/**
	 * Parse a single period
	 * Period can be:
	 * 1. start/end: 19960403T140000Z/19960403T150000Z
	 * 2. start/duration: 19960403T140000Z/PT1H30M
	 */
	private void parsePeriod(String periodStr) throws ParseException, BogusDataException {
		int slashIdx = periodStr.indexOf('/');
		if (slashIdx == -1) {
			throw new ParseException("Invalid period format (missing /): " + periodStr, periodStr);
		}
		
		String startPart = periodStr.substring(0, slashIdx);
		String endPart = periodStr.substring(slashIdx + 1);
		
		startDate = new Date("DTSTART:" + startPart);
		
		if (endPart.startsWith("P") || endPart.startsWith("-P")) {
			duration = new Duration("DURATION:" + endPart);
		} else {
			endDate = new Date("DTEND:" + endPart);
		}
	}

	/**
	 * Was enough information parsed for this FreebusyPeriod to be valid?
	 * 
	 * @return true if period is valid
	 */
	public boolean isValid() {
		return startDate != null && (endDate != null || duration != null);
	}

	/**
	 * Get start date
	 * 
	 * @return start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Get end date
	 * 
	 * @return end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Get duration
	 * 
	 * @return duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Convert this FreebusyPeriod to iCalendar format
	 * 
	 * @return iCalendar string representation
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(256);
		ret.append("FREEBUSY:");
		
		if (startDate != null) {
			String startStr = startDate.value;
			ret.append(startStr);
		}
		
		if (endDate != null) {
			String endStr = endDate.value;
			ret.append('/');
			ret.append(endStr);
		} else if (duration != null) {
			String durStr = duration.value;
			ret.append('/');
			ret.append(durStr);
		}
		
		return ret.toString();
	}

	/**
	 * Get a string representation of this FreebusyPeriod
	 * 
	 * @return string representation
	 */
	public String toString() {
		StringBuffer ret = new StringBuffer(256);
		ret.append("FreebusyPeriod [");
		ret.append(startDate != null ? startDate.value : "null");
		ret.append(" - ");
		ret.append(endDate != null ? endDate.value : 
			 (duration != null ? duration.value : "null"));
		ret.append("]");
		return ret.toString();
	}
}
