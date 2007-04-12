package us.k5n.journal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.Journal;
import us.k5n.ical.ParseError;

/**
 * Extend the File class to include iCalendar data created from parsing the
 * file. Normally, the application will just store a single Journal entry in
 * each file. However, if a user copies an ICS file into their directory, we
 * don't want to loose track of the original filename to avoid creating
 * duplicates.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class DataFile extends File implements Constants {
	ICalendarParser parser;
	DataStore dataStore;

	public DataFile(String filename) {
		this ( filename, false );
	}

	public DataFile(String filename, boolean strictParsing) {
		super ( filename );
		parser = new ICalendarParser ( strictParsing ? PARSE_STRICT : PARSE_LOOSE );
		BufferedReader reader = null;
		try {
			reader = new BufferedReader ( new FileReader ( this ) );
			parser.parse ( reader );
			reader.close ();
		} catch ( IOException e ) {
			System.err.println ( "Error opening " + toString () + ": " + e );
		}
		dataStore = parser.getDataStoreAt ( 0 );
	}

	/**
	 * Return the number of journal entries in this file.
	 * 
	 * @return
	 */
	public int getJournalCount () {
		return dataStore.getAllJournals ().size ();
	}

	/**
	 * Get the Journal entry at the specified location.
	 * 
	 * @param ind
	 *          The index number (0 is first)
	 * @return
	 */
	public Journal journalEntryAt ( int ind ) {
		return (Journal) dataStore.getAllJournals ().elementAt ( ind );
	}

	/**
	 * Get the number of parse errors found in the file.
	 * 
	 * @return
	 */
	public int getParseErrorCount () {
		return parser.getAllErrors ().size ();
	}

	/**
	 * Get the parse error at the specified location
	 * 
	 * @param ind
	 * @return
	 */
	public ParseError getParseErrorAt ( int ind ) {
		return (ParseError) parser.getAllErrors ().elementAt ( ind );
	}
}
