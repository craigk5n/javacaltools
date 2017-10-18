package us.k5n.ui.calendar;

import java.util.ArrayList;
import java.util.List;

public class EventFormatter {
	public static final int DEFAULT_LINE_LENGTH = 50;

	/**
	 * Format the event description.
	 *
	 * @param inputString
	 * @return
	 */
	public List<String> formatDescription(String inputString) {
		return Utils.wrapLines(inputString, DEFAULT_LINE_LENGTH);
	}

	/**
	 * Format the event location.
	 *
	 * @param location
	 * @return
	 */
	public List<String> formatLocation(String location) {
		List<String> ret = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("Location: ");
		sb.append(location);
		ret.add(sb.toString());
		return ret;
	}

}
