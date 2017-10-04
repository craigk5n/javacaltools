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

import java.util.Vector;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * iCalendar Parser class - This object is required for most parsing methods and
 * can be thought of as the main entry point into this package. It can serve as
 * a DataStore to store events/todos/etc when a custom DataStore is not needed. <br/>
 * Example usage: <blockquote>
 * 
 * <pre>
 * ICalendarParser parser = new ICalendarParser ();
 * File f = new File ( &quot;/tmp/test.ics&quot; );
 * try {
 * 	BufferedReader r = new BufferedReader ( new FileReader ( f ) );
 * 	parser.parse ( r );
 * } catch ( IOException e ) {
 * 	// ... report error
 * }
 * r.close ();
 * Vector events = parser.getAllEvents ();
 * Vector errors = parser.getAllErrors ();
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class ICalendarParser extends CalendarParser implements Constants {
	Property icalVersion = null;
	Property prodId = null;
	Property method = null;
	Property calscale = null;
	String language = "EN"; // default language setting
	static final int STATE_NONE = 0;
	static final int STATE_VCALENDAR = 1;
	static final int STATE_VEVENT = 2;
	static final int STATE_VTODO = 3;
	static final int STATE_VJOURNAL = 4;
	static final int STATE_VTIMEZONE = 5;
	static final int STATE_VFREEBUSY = 6;
	static final int STATE_DONE = 7;

	/**
	 * Create an ICalendarParser object. By default, this will also setup the
	 * default DataStore object. To remove the default DataStore, you can call
	 * removeDataStoreAt(0).
	 * 
	 * @param parseMethod
	 *          Specifies the parsing method, which should be either PARSE_STRICT
	 *          or PARSE_LOOSE. The PARSE_STRICT method will follow the RFC 2445
	 *          specification strictly and is intended to be used to validate
	 *          iCalendar data. Most clients should specify PARSE_LOOSE to capture
	 *          as much of the data as possible.
	 */
	public ICalendarParser(int parseMethod) {
		this ( parseMethod, "EN" );
	}

	/**
	 * Create an ICalendarParser object. By default, this will also setup the
	 * default DataStore object. To remove the default DataStore, you can call
	 * removeDataStoreAt(0).
	 * 
	 * @param parseMethod
	 *          Specifies the parsing method, which should be either PARSE_STRICT
	 *          or PARSE_LOOSE. The PARSE_STRICT method will follow the RFC 2445
	 *          specification strictly and is intended to be used to validate
	 *          iCalendar data. Most clients should specify PARSE_LOOSE to capture
	 *          as much of the data as possible.
	 * @param language
	 *          Default language setting. When parsing objects, the property that
	 *          matches this language setting will take priority. For example, if
	 *          "EN" is specified as a parameter here, and an event in iCalendar
	 *          has a summary in "EN" and also in "FR", then the summary in "EN"
	 *          will be returned when the event is queries for a summary.
	 */
	public ICalendarParser(int parseMethod, String language) {
		super ( parseMethod );
		this.language = language;
	}

	/**
	 * Parse a File.
	 * 
	 * @param reader
	 *          The java.io.Reader object to read the iCalendar data from. To
	 *          parse a String object use java.io.StringReader.
	 * @return true if no parse errors encountered
	 */
	public boolean parse ( java.io.Reader reader ) throws IOException {
		boolean noErrors = true;
		String line, nextLine;
		BufferedReader r = new BufferedReader ( reader );
		StringBuffer data = new StringBuffer ();
		StringBuffer notYetParsed;
		int state = STATE_NONE;
		int ln = 0; // line number
		int startLineNo = 0;
		Vector<String> textLines;
		boolean done = false;

		// Because iCalendar allows lines to be "folded" (continued) onto
		// multiple
		// lines, you need to peek ahead to the next line to know if you have
		// all the text for what you are trying to parse.
		// The "line" variable is what is currently being parsed. The "nextLine"
		// variable contains the next line of text to be processed.
		// TODO: line numbers in errors may be off for folded lines since the
		// last line number of the text will be reported.
		textLines = new Vector<String> ();
		nextLine = r.readLine ();
		notYetParsed = new StringBuffer ();
		if ( nextLine == null ) {
			// empty file
		} else {
			while ( !done ) {
				line = nextLine;
				ln++;
				if ( nextLine != null ) {
					nextLine = r.readLine ();
					// if nextLine is null, don't set done to true yet since we
					// still need another iteration through the while loop for the text
					// to get processed.
				}
				// Check to see if next line is a continuation of the current
				// line. If it is, then append the contents of the next line
				// onto the current line.
				if ( nextLine != null
				    && nextLine.length () > 0
				    && ( nextLine.charAt ( 0 ) == SPACE || nextLine.charAt ( 0 ) == TAB ) ) {
					// Line folding found. Add to previous line and continue.
					if ( notYetParsed.length () == 0 )
						notYetParsed.append ( line );
					notYetParsed.append ( CRLF );
					notYetParsed.append ( nextLine );
					// nextLine = line + CRLF + nextLine;
					continue; // skip back to start of while loop
				}
				// not a continuation line
				if ( notYetParsed.length () > 0 )
					line = notYetParsed.toString ();
				notYetParsed.setLength ( 0 );

				data.append ( line );
				String lineUp = line.toUpperCase ();

				// System.out.println ( "[DATA:" + state + "]" + line );
				switch ( state ) {

					case STATE_NONE:
						if ( lineUp.startsWith ( "BEGIN:VCALENDAR" ) )
							state = STATE_VCALENDAR;
						else if ( lineUp.length () == 0 ) {
							// ignore leading blank lines
						} else {
							// Hmmm... should always start with this.
							if ( isParseStrict () ) {
								reportParseError ( new ParseError ( ln,
								    "Data found outside VCALENDAR block", line ) );
							}
						}
						break;

					case STATE_VCALENDAR:
						if ( lineUp.startsWith ( "BEGIN:VTIMEZONE" ) ) {
							state = STATE_VTIMEZONE;
							startLineNo = ln; // mark starting line number
							textLines.removeAllElements ();
							textLines.addElement ( line );
						} else if ( lineUp.startsWith ( "BEGIN:VEVENT" ) ) {
							state = STATE_VEVENT;
							startLineNo = ln; // mark starting line number
							textLines.removeAllElements ();
							textLines.addElement ( line );
						} else if ( lineUp.startsWith ( "BEGIN:VTODO" ) ) {
							state = STATE_VTODO;
							startLineNo = ln; // mark starting line number
							textLines.removeAllElements ();
							textLines.addElement ( line );
						} else if ( lineUp.startsWith ( "BEGIN:VJOURNAL" ) ) {
							state = STATE_VJOURNAL;
							startLineNo = ln; // mark starting line number
							textLines.removeAllElements ();
							textLines.addElement ( line );
						} else if ( lineUp.startsWith ( "BEGIN:VFREEBUSY" ) ) {
							state = STATE_VFREEBUSY;
							startLineNo = ln; // mark starting line number
							textLines.removeAllElements ();
							textLines.addElement ( line );
						} else if ( lineUp.startsWith ( "END:VCALENDAR" ) ) {
							state = STATE_DONE;
						} else if ( lineUp.startsWith ( "VERSION" ) ) {
							if ( icalVersion != null && isParseStrict () ) {
								// only one of these allowed
								reportParseError ( new ParseError ( ln,
								    "Only one VERSION token allowed", line ) );
							} else {
								try {
									icalVersion = new Property ( line, getParseMethod () );
								} catch ( ParseException e ) {
									reportParseError ( new ParseError ( ln,
									    "Parse error in VERSION: " + e.toString (), line ) );
								}
							}
						} else if ( lineUp.startsWith ( "PRODID" ) ) {
							if ( prodId != null && isParseStrict () ) {
								// only one of these allowed
								reportParseError ( new ParseError ( ln,
								    "Only one PRODID token allowed", line ) );
							} else {
								try {
									prodId = new Property ( line, getParseMethod () );
								} catch ( ParseException e ) {
									reportParseError ( new ParseError ( ln,
									    "Parse error in PRODID: " + e.toString (), line ) );
								}
							}
						} else if ( lineUp.startsWith ( "CALSCALE" ) ) {
							try {
								calscale = new Property ( line, getParseMethod () );
							} catch ( ParseException e ) {
								reportParseError ( new ParseError ( ln,
								    "Parse error in CALSCALE: " + e.toString (), line ) );
							}
						} else if ( lineUp.startsWith ( "METHOD" ) ) {
							try {
								method = new Property ( line, getParseMethod () );
							} catch ( ParseException e ) {
								reportParseError ( new ParseError ( ln,
								    "Parse error in CALSCALE: " + e.toString (), line ) );
							}
						} else {
							// what else could this be???
							if ( lineUp.trim ().length () == 0 ) {
								// ignore blank lines
							} else if ( isParseStrict () ) {
								reportParseError ( new ParseError ( ln,
								    "Unrecognized data found in VCALENDAR block", line ) );
							}
						}
						break;

					case STATE_VTIMEZONE:
						textLines.addElement ( line );
						if ( lineUp.startsWith ( "END:VTIMEZONE" ) ) {
							state = STATE_VCALENDAR;
							Timezone timezone = new Timezone ( this, startLineNo, textLines );
							if ( timezone.isValid () ) {
								for ( int i = 0; i < dataStores.size (); i++ ) {
									DataStore ds = (DataStore) dataStores.elementAt ( i );
									ds.storeTimezone ( timezone );
								}
							}
							textLines.removeAllElements (); // truncate Vector
						}
						break;

					case STATE_VTODO:
						textLines.addElement ( line );
						if ( lineUp.startsWith ( "END:VTODO" ) ) {
							state = STATE_VCALENDAR;
							// Send the Todo object to all DataStore objects
							/*****************************************************************
							 * * TODO - not yet implemented Uncomment this when Todo.java is
							 * implemented Todo todo = new Todo ( this, startLineNo, textLines
							 * ); if ( todo.isValid() ) { for ( int i = 0; i <
							 * dataStores.size(); i++ ) { DataStore ds = (DataStore)
							 * dataStores.elementAt ( i ); ds.storeTodo ( todo ); } }
							 ****************************************************************/
							textLines.removeAllElements (); // truncate Vector
						}
						break;

					case STATE_VJOURNAL:
						textLines.addElement ( line );
						if ( lineUp.startsWith ( "END:VJOURNAL" ) ) {
							state = STATE_VCALENDAR;
							// Send the Journal object to all DataStore objects
							Journal journal = new Journal ( this, startLineNo, textLines );
							if ( journal.isValid () ) {
								for ( int i = 0; i < dataStores.size (); i++ ) {
									DataStore ds = (DataStore) dataStores.elementAt ( i );
									ds.storeJournal ( journal );
								}
							}
							textLines.removeAllElements (); // truncate Vector
						}
						break;

					case STATE_VEVENT:
						textLines.addElement ( line );
						if ( lineUp.startsWith ( "END:VEVENT" ) ) {
							state = STATE_VCALENDAR;
							Event event = new Event ( this, startLineNo, textLines );
							if ( event.isValid () ) {
								for ( int i = 0; i < dataStores.size (); i++ ) {
									DataStore ds = (DataStore) dataStores.elementAt ( i );
									ds.storeEvent ( event );
								}
							} else {
								System.err.println ( "ERROR: Invalid VEVENT found" );
							}
							textLines.removeAllElements (); // truncate Vector
						}
						break;

					case STATE_VFREEBUSY:
						textLines.addElement ( line );
						if ( lineUp.startsWith ( "END:VFREEBUSY" ) ) {
							state = STATE_VCALENDAR;
							// Send the Freebusy object to all DataStore objects
							/*****************************************************************
							 * * TODO - not yet implemented Uncomment this when Freebusy.java
							 * is implemented Freebusy fb = new Freebusy ( this, startLineNo,
							 * textLines ); if ( fb.isValid() ) { for ( int i = 0; i <
							 * dataStores.size(); i++ ) { DataStore ds = (DataStore)
							 * dataStores.elementAt ( i ); ds.storeFreebusy ( event ); } }
							 ****************************************************************/
							textLines.removeAllElements (); // truncate Vector
						}
						break;

					case STATE_DONE:
						// should be nothing else after "END:VCALENDAR"
						if ( lineUp.trim ().length () == 0 ) {
							// ignore blank lines at end of file
						} else if ( isParseStrict () ) {
							reportParseError ( new ParseError ( ln,
							    "Data found after END:VCALENDAR", line ) );
						}
						break;
				}
				if ( nextLine == null )
					done = true;
			}
		}
		r.close ();

		// Make sure PRODID and VERSION were specified since they are
		// required
		if ( icalVersion == null && isParseStrict () ) {
			reportParseError ( new ParseError ( ln,
			    "No required VERSION attribute found", "n/a" ) );
		}
		if ( prodId == null && isParseStrict () ) {
			reportParseError ( new ParseError ( ln,
			    "No required PRODID attribute found", "n/a" ) );
		}

		// iCalendar data is now in line
		// TODO: split into event, todo, timezone and journal strings

		return noErrors;
	}

}
