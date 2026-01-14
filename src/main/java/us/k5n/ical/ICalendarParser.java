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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * iCalendar Parser class - This object is required for most parsing methods and
 * can be thought of as the main entry point into this package. It can serve as
 * a DataStore to store events/todos/etc when a custom DataStore is not needed.
 * <br/>
 * Example usage: <blockquote>
 * 
 * <pre>
 * ICalendarParser parser = new ICalendarParser();
 * File f = new File(&quot;/tmp/test.ics&quot;);
 * try {
 * 	BufferedReader r = new BufferedReader(new FileReader(f));
 * 	parser.parse(r);
 * } catch (IOException e) {
 * 	// ... report error
 * }
 * r.close();
 * List events = parser.getAllEvents();
 * List errors = parser.getAllErrors();
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class ICalendarParser extends CalendarParser implements Constants {
	Property icalVersion = null;
	Property prodId = null;
	Property method = null;
	Property calscale = null;
	String language = "EN"; // default language setting
	Timezone currentTimezone = null; // current timezone being parsed
	VLocation currentVLocation = null; // current vlocation being parsed
	VResource currentVResource = null; // current vresource being parsed
	Participant currentParticipant = null; // current participant being parsed
	VAvailability currentVAvailability = null; // current vavailability being parsed
	/** Enable streaming parsing for large files */
	protected boolean streamingMode = false;
	/** Maximum component size for streaming mode (lines) */
	protected int maxComponentSize = 10000;
	/** Enable performance monitoring */
	protected boolean performanceMonitoring = false;
	/** Performance metrics */
	protected long parseStartTime = 0;
	protected long linesProcessed = 0;
	protected long componentsParsed = 0;
	static final int STATE_NONE = 0;
	static final int STATE_VCALENDAR = 1;
	static final int STATE_VEVENT = 2;
	static final int STATE_VTODO = 3;
	static final int STATE_VJOURNAL = 4;
	static final int STATE_VTIMEZONE = 5;
	static final int STATE_VTIMEZONE_STANDARD = 6;
	static final int STATE_VTIMEZONE_DAYLIGHT = 7;
	static final int STATE_VFREEBUSY = 8;
	static final int STATE_VALARM = 9;
	static final int STATE_VLOCATION = 10;
	static final int STATE_VRESOURCE = 11;
	static final int STATE_VAVAILABILITY = 12;
	static final int STATE_PARTICIPANT = 13;
	static final int STATE_DONE = 14;

	/**
	 * Create an ICalendarParser object. By default, this will also setup the
	 * default DataStore object. To remove the default DataStore, you can call
	 * removeDataStoreAt(0).
	 * 
	 * @param parseMethod
	 *                    Specifies the parsing method, which should be either
	 *                    PARSE_STRICT
	 *                    or PARSE_LOOSE. The PARSE_STRICT method will follow the
	 *                    RFC 2445
	 *                    specification strictly and is intended to be used to
	 *                    validate
	 *                    iCalendar data. Most clients should specify PARSE_LOOSE to
	 *                    capture
	 *                    as much of the data as possible.
	 */
	public ICalendarParser(int parseMethod) {
		this(parseMethod, "EN");
	}

	/**
	 * Create an ICalendarParser object. By default, this will also setup the
	 * default DataStore object. To remove the default DataStore, you can call
	 * removeDataStoreAt(0).
	 * 
	 * @param parseMethod
	 *                    Specifies the parsing method, which should be either
	 *                    PARSE_STRICT
	 *                    or PARSE_LOOSE. The PARSE_STRICT method will follow the
	 *                    RFC 2445
	 *                    specification strictly and is intended to be used to
	 *                    validate
	 *                    iCalendar data. Most clients should specify PARSE_LOOSE to
	 *                    capture
	 *                    as much of the data as possible.
	 * @param language
	 *                    Default language setting. When parsing objects, the
	 *                    property that
	 *                    matches this language setting will take priority. For
	 *                    example, if
	 *                    "EN" is specified as a parameter here, and an event in
	 *                    iCalendar
	 *                    has a summary in "EN" and also in "FR", then the summary
	 *                    in "EN"
	 *                    will be returned when the event is queries for a summary.
	 */
	public ICalendarParser(int parseMethod, String language) {
		super(parseMethod);
		this.language = language;
	}

	/**
	 * Enable or disable streaming parsing mode for large files.
	 * Streaming mode reduces memory usage by limiting the size of component text buffers.
	 *
	 * @param streamingMode true to enable streaming mode, false for normal mode
	 */
	public void setStreamingMode(boolean streamingMode) {
		this.streamingMode = streamingMode;
	}

	/**
	 * Set the maximum component size for streaming mode.
	 * Components larger than this size will be processed in chunks.
	 *
	 * @param maxComponentSize maximum number of lines per component chunk
	 */
	public void setMaxComponentSize(int maxComponentSize) {
		this.maxComponentSize = maxComponentSize;
	}

	/**
	 * Get the current streaming mode setting.
	 *
	 * @return true if streaming mode is enabled
	 */
	public boolean isStreamingMode() {
		return streamingMode;
	}

	/**
	 * Get the maximum component size setting.
	 *
	 * @return maximum component size in lines
	 */
	public int getMaxComponentSize() {
		return maxComponentSize;
	}

	/**
	 * Enable or disable performance monitoring.
	 *
	 * @param performanceMonitoring true to enable performance monitoring
	 */
	public void setPerformanceMonitoring(boolean performanceMonitoring) {
		this.performanceMonitoring = performanceMonitoring;
	}

	/**
	 * Get the number of lines processed in the last parse operation.
	 *
	 * @return number of lines processed
	 */
	public long getLinesProcessed() {
		return linesProcessed;
	}

	/**
	 * Get the number of components parsed in the last parse operation.
	 *
	 * @return number of components parsed
	 */
	public long getComponentsParsed() {
		return componentsParsed;
	}

	/**
	 * Get the parse time in milliseconds for the last parse operation.
	 *
	 * @return parse time in milliseconds, or 0 if monitoring not enabled
	 */
	public long getParseTime() {
		return performanceMonitoring ? System.currentTimeMillis() - parseStartTime : 0;
	}

	/**
	 * Parse a File.
	 * 
	 * @param reader
	 *               The java.io.Reader object to read the iCalendar data from. To
	 *               parse a String object use java.io.StringReader.
	 * @return true if no parse errors encountered
	 */
	public boolean parse(java.io.Reader reader) throws IOException {
		if (performanceMonitoring) {
			parseStartTime = System.currentTimeMillis();
			linesProcessed = 0;
			componentsParsed = 0;
		}

		boolean noErrors = true;
		String line, nextLine;
		BufferedReader r = new BufferedReader(reader, 16384); // Use larger buffer for better performance
		StringBuilder data = new StringBuilder(4096); // Use StringBuilder instead of StringBuffer
		StringBuilder notYetParsed = null;
		int state = STATE_NONE;
		int ln = 0; // line number
		int startLineNo = 0;
		List<String> textLines;
		boolean done = false;

		// Because iCalendar allows lines to be "folded" (continued) onto
		// multiple
		// lines, you need to peek ahead to the next line to know if you have
		// all the text for what you are trying to parse.
		// The "line" variable is what is currently being parsed. The "nextLine"
		// variable contains the next line of text to be processed.
		// TODO: line numbers in errors may be off for folded lines since the
		// last line number of the text will be reported.
		textLines = streamingMode ? new ArrayList<String>(Math.min(maxComponentSize, 1000))
		                          : new ArrayList<String>(1000);
		nextLine = r.readLine();
		notYetParsed = new StringBuilder(1024);
		if (nextLine == null) {
			// empty file
		} else {
			while (!done) {
				line = nextLine;
				ln++;
				if (performanceMonitoring) {
					linesProcessed++;
				}
				if (nextLine != null) {
					nextLine = r.readLine();
					// if nextLine is null, don't set done to true yet since we
					// still need another iteration through the while loop for the text
					// to get processed.
				}
				// Check to see if next line is a continuation of the current
				// line. If it is, then append the contents of the next line
				// onto the current line.
				if (nextLine != null
						&& nextLine.length() > 0
						&& (nextLine.charAt(0) == SPACE || nextLine.charAt(0) == TAB)) {
					// Line folding found. Add to previous line and continue.
					if (notYetParsed.length() == 0)
						notYetParsed.append(line);
					notYetParsed.append(CRLF);
					notYetParsed.append(nextLine);
					// nextLine = line + CRLF + nextLine;
					continue; // skip back to start of while loop
				}
				// not a continuation line
				if (notYetParsed.length() > 0)
					line = notYetParsed.toString();
				notYetParsed.setLength(0);

				data.append(line);
				String lineUp = line.toUpperCase();

				// System.out.println ( "[DATA:" + state + "]" + line );
				switch (state) {

					case STATE_NONE:
						if (lineUp.startsWith("BEGIN:VCALENDAR"))
							state = STATE_VCALENDAR;
						else if (lineUp.length() == 0) {
							// ignore leading blank lines
						} else {
							// Hmmm... should always start with this.
							if (isParseStrict()) {
								reportParseError(new ParseError(ln,
										"Data found outside VCALENDAR block", line));
							}
						}
						break;

					case STATE_VCALENDAR:
						if (lineUp.startsWith("BEGIN:VTIMEZONE")) {
							state = STATE_VTIMEZONE;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VEVENT")) {
							state = STATE_VEVENT;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VTODO")) {
							state = STATE_VTODO;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VJOURNAL")) {
							state = STATE_VJOURNAL;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VFREEBUSY")) {
							state = STATE_VFREEBUSY;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VLOCATION")) {
							state = STATE_VLOCATION;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
							try {
								currentVLocation = new VLocation(this, startLineNo, textLines);
							} catch (ParseException | BogusDataException e) {
								reportParseError(new ParseError(startLineNo,
									"Parse error in VLOCATION: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("BEGIN:VRESOURCE")) {
							state = STATE_VRESOURCE;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
							try {
								currentVResource = new VResource(this, startLineNo, textLines);
							} catch (ParseException | BogusDataException e) {
								reportParseError(new ParseError(startLineNo,
									"Parse error in VRESOURCE: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("BEGIN:PARTICIPANT")) {
							state = STATE_PARTICIPANT;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
							currentParticipant = new Participant(this, startLineNo, textLines);
						} else if (lineUp.startsWith("BEGIN:VAVAILABILITY")) {
							state = STATE_VAVAILABILITY;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
							currentVAvailability = new VAvailability(this, startLineNo, textLines);
						} else if (lineUp.startsWith("END:VCALENDAR")) {
							state = STATE_DONE;
						} else if (lineUp.startsWith("VERSION")) {
							if (icalVersion != null && isParseStrict()) {
								// only one of these allowed
								reportParseError(new ParseError(ln,
										"Only one VERSION token allowed", line));
							} else {
								try {
									icalVersion = new Property(line, getParseMethod());
								} catch (ParseException e) {
									reportParseError(new ParseError(ln,
											"Parse error in VERSION: " + e.toString(), line));
								}
							}
						} else if (lineUp.startsWith("PRODID")) {
							if (prodId != null && isParseStrict()) {
								// only one of these allowed
								reportParseError(new ParseError(ln,
										"Only one PRODID token allowed", line));
							} else {
								try {
									prodId = new Property(line, getParseMethod());
								} catch (ParseException e) {
									reportParseError(new ParseError(ln,
											"Parse error in PRODID: " + e.toString(), line));
								}
							}
						} else if (lineUp.startsWith("CALSCALE")) {
							try {
								calscale = new Property(line, getParseMethod());
							} catch (ParseException e) {
								reportParseError(new ParseError(ln,
										"Parse error in CALSCALE: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("METHOD")) {
							try {
								method = new Property(line, getParseMethod());

								// Validate METHOD value against RFC 5546 iTIP methods
								if (getParseMethod() == PARSE_STRICT) {
									String methodValue = method.getValue();
									if (!isValidItipMethod(methodValue)) {
										reportParseError(new ParseError(ln,
												"Invalid METHOD value '" + methodValue +
												"'. Must be one of: PUBLISH, REQUEST, REPLY, ADD, CANCEL, REFRESH, COUNTER, DECLINECOUNTER",
												line));
									}
								}

								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.setMethod(method);
								}
							} catch (ParseException e) {
								reportParseError(new ParseError(ln,
										"Parse error in METHOD: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("NAME")) {
							try {
								Property nameProp = new Property(line, getParseMethod());
								for (int i = 0; i < dataStores.size(); i++) {
									DefaultDataStore ds = (DefaultDataStore) dataStores.get(i);
									ds.setName(nameProp.value);
								}
							} catch (ParseException e) {
								reportParseError(new ParseError(ln,
										"Parse error in NAME: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("CALENDAR-ADDRESS")) {
							try {
								Property calAddr = new Property(line, getParseMethod());
								for (int i = 0; i < dataStores.size(); i++) {
									DefaultDataStore ds = (DefaultDataStore) dataStores.get(i);
									ds.setCalendarAddress(calAddr.value);
								}
							} catch (ParseException e) {
								reportParseError(new ParseError(ln,
										"Parse error in CALENDAR-ADDRESS: " + e.toString(), line));
							}
						} else if (lineUp.startsWith("X-")) {
							// These are extensions like: X-WR-CALNAME, X-WR-CALDESC, X-WR-TIMEZONE, X-WR-RELCALID,
							// X-PUBLISHED-TTL, X-APPLE-CALENDAR-COLOR, X-MS-OLK-APPTSEQTIME, X-MS-OLK-CONFTYPE,
							// X-MS-OLK-DTSTART, X-MS-OLK-DTEND, X-GOOGLE-CALENDAR-COLOR
							try {
								method = new Property(line, getParseMethod());
							} catch (ParseException e) {
								reportParseError(new ParseError(ln,
										"Parse error in CALSCALE: " + e.toString(), line));
							}
						} else {
							// what else could this be???
							if (lineUp.trim().length() == 0) {
								// ignore blank lines
							} else if (isParseStrict()) {
								reportParseError(new ParseError(ln,
										"Unrecognized data found in VCALENDAR block", line));
							}
						}
						break;

					case STATE_VTIMEZONE:
						textLines.add(line);
						if (lineUp.startsWith("END:VTIMEZONE")) {
							state = STATE_VCALENDAR;
							if (currentTimezone != null) {
								if (currentTimezone.isValid()) {
									for (int i = 0; i < dataStores.size(); i++) {
										DataStore ds = (DataStore) dataStores.get(i);
										ds.storeTimezone(currentTimezone);
									}
									if (performanceMonitoring) {
										componentsParsed++;
									}
								}
								currentTimezone = null;
								textLines.clear(); // truncate List
							}
						} else if (lineUp.startsWith("BEGIN:STANDARD")) {
							state = STATE_VTIMEZONE_STANDARD;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:DAYLIGHT")) {
							state = STATE_VTIMEZONE_DAYLIGHT;
							startLineNo = ln; // mark starting line number
							textLines.clear();
							textLines.add(line);
						} else if (lineUp.startsWith("BEGIN:VTIMEZONE")) {
							currentTimezone = new Timezone(this, ln, textLines);
						}
						break;

					case STATE_VTODO:
						textLines.add(line);
						if (lineUp.startsWith("END:VTODO")) {
							state = STATE_VCALENDAR;
							Todo todo = new Todo(this, startLineNo, textLines);
							if (todo.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeTodo(todo);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VJOURNAL:
						textLines.add(line);
						if (lineUp.startsWith("END:VJOURNAL")) {
							state = STATE_VCALENDAR;
							// Send the Journal object to all DataStore objects
							Journal journal = new Journal(this, startLineNo, textLines);
							if (journal.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeJournal(journal);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VEVENT:
						textLines.add(line);
						if (lineUp.startsWith("END:VEVENT")) {
							state = STATE_VCALENDAR;
							Event event = new Event(this, startLineNo, textLines);
							if (event.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeEvent(event);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							} else {
								System.err.println("ERROR: Invalid VEVENT found");
							}
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VFREEBUSY:
						textLines.add(line);
						if (lineUp.startsWith("END:VFREEBUSY")) {
							state = STATE_VCALENDAR;
							Freebusy fb = new Freebusy(this, startLineNo, textLines);
							if (fb.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeFreebusy(fb);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							textLines.clear(); // truncate List
						}
						break;



					case STATE_VTIMEZONE_DAYLIGHT:
						textLines.add(line);
						if (lineUp.startsWith("END:DAYLIGHT")) {
							state = STATE_VTIMEZONE;
							TimezoneDaylight daylight = new TimezoneDaylight(this, startLineNo, textLines);
							if (daylight.isValid() && currentTimezone != null) {
								currentTimezone.addDaylight(daylight);
							}
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VTIMEZONE_STANDARD:
						textLines.add(line);
						if (lineUp.startsWith("END:STANDARD")) {
							state = STATE_VTIMEZONE;
							TimezoneStandard standard = new TimezoneStandard(this, startLineNo, textLines);
							if (standard.isValid() && currentTimezone != null) {
								currentTimezone.addStandard(standard);
							}
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VAVAILABILITY:
						textLines.add(line);
						if (lineUp.startsWith("END:VAVAILABILITY")) {
							state = STATE_VCALENDAR;
							if (currentVAvailability != null && currentVAvailability.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeVAvailability(currentVAvailability);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							currentVAvailability = null;
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VRESOURCE:
						textLines.add(line);
						if (lineUp.startsWith("END:VRESOURCE")) {
							state = STATE_VCALENDAR;
							if (currentVResource != null && currentVResource.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeVResource(currentVResource);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							currentVResource = null;
							textLines.clear(); // truncate List
						}
						break;

					case STATE_PARTICIPANT:
						textLines.add(line);
						if (lineUp.startsWith("END:PARTICIPANT")) {
							state = STATE_VCALENDAR;
							currentParticipant = new Participant(this, startLineNo, textLines);
							if (currentParticipant != null && currentParticipant.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeParticipant(currentParticipant);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							 }
							currentParticipant = null;
							textLines.clear(); // truncate List
						}
						break;

					case STATE_VLOCATION:
						textLines.add(line);
						if (lineUp.startsWith("END:VLOCATION")) {
							state = STATE_VCALENDAR;
							if (currentVLocation != null && currentVLocation.isValid()) {
								for (int i = 0; i < dataStores.size(); i++) {
									DataStore ds = (DataStore) dataStores.get(i);
									ds.storeVLocation(currentVLocation);
								}
								if (performanceMonitoring) {
									componentsParsed++;
								}
							}
							currentVLocation = null;
							textLines.clear(); // truncate List
						}
						break;

					case STATE_DONE:
						// should be nothing else after "END:VCALENDAR"
						if (lineUp.trim().length() == 0) {
							// ignore blank lines at end of file
						} else if (isParseStrict()) {
							reportParseError(new ParseError(ln,
									"Data found after END:VCALENDAR", line));
						}
						break;
				}
				if (nextLine == null)
					done = true;
			}
		}
		r.close();

		// Make sure PRODID and VERSION were specified since they are
		// required
		if (icalVersion == null && isParseStrict()) {
			reportParseError(new ParseError(ln,
					"No required VERSION attribute found", "n/a"));
		}
		if (prodId == null && isParseStrict()) {
			reportParseError(new ParseError(ln,
					"No required PRODID attribute found", "n/a"));
		}

		// iCalendar data is now in line
		// TODO: split into event, todo, timezone and journal strings

		return noErrors;
	}

	/**
	 * Validate that a METHOD value is a valid iTIP method according to RFC 5546.
	 *
	 * @param methodValue the METHOD value to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidItipMethod(String methodValue) {
		if (methodValue == null) {
			return false;
		}

		String upperValue = methodValue.toUpperCase();
		return Constants.METHOD_PUBLISH.equals(upperValue) ||
			   Constants.METHOD_REQUEST.equals(upperValue) ||
			   Constants.METHOD_REPLY.equals(upperValue) ||
			   Constants.METHOD_ADD.equals(upperValue) ||
			   Constants.METHOD_CANCEL.equals(upperValue) ||
			   Constants.METHOD_REFRESH.equals(upperValue) ||
			   Constants.METHOD_COUNTER.equals(upperValue) ||
			   Constants.METHOD_DECLINECOUNTER.equals(upperValue);
	}

}
