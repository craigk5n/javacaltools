package us.k5n.journal;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import us.k5n.ical.Date;
import us.k5n.ical.Journal;
import us.k5n.ical.Utils;

/**
 * The Repository class manages all loading and saving of data files. All
 * methods are intended to work with just Journal objects. However, if an
 * iCalendar file is loaded with Event objects, they should be preserved in the
 * data if it is written back out.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class Repository {
	File directory;
	Vector dataFiles;
	HashMap dataFileHash;
	int parseErrorCount = 0;
	int journalCount = 0;
	Date[] listOfDates;
	HashMap uidHash;

	public Repository(File dir, boolean strictParsing) {
		this.directory = dir;
		this.dataFiles = new Vector ();
		this.dataFileHash = new HashMap ();
		this.uidHash = new HashMap ();

		// Load all files.
		File[] files = this.directory.listFiles ( new IcsFileFilter () );
		for ( int i = 0; files != null && i < files.length; i++ ) {
			DataFile f = new DataFile ( files[i].getAbsolutePath (), strictParsing );
			if ( f != null ) {
				this.dataFiles.addElement ( f );
				journalCount += f.getJournalCount ();
				parseErrorCount += f.getParseErrorCount ();
				// Store in HashMap using just the filename (19991231.ics)
				// as the key
				this.dataFileHash.put ( files[i].getName ().toLowerCase (), f );
			}
		}

		updateDateList ();
	}

	public DataFile findDataFile ( Journal j ) {
		String YMD = Utils.DateToYYYYMMDD ( j.getStartDate () );
		String fileName = YMD + ".ics";
		DataFile dataFile = (DataFile) this.dataFileHash.get ( fileName );
		return dataFile;
	}

	/**
	 * Get an array of int values indicating which years have Journal entries.
	 * 
	 * @return
	 */
	public int[] getYears () {
		if ( listOfDates == null )
			return null;
		HashMap h = new HashMap ();
		Vector years = new Vector ();
		for ( int i = 0; i < listOfDates.length; i++ ) {
			Integer ival = new Integer ( listOfDates[i].getYear () );
			if ( !h.containsKey ( ival ) ) {
				h.put ( ival, ival );
				years.addElement ( ival );
			}
		}
		int[] ret = new int[years.size ()];
		for ( int i = 0; i < years.size (); i++ )
			ret[i] = ( (Integer) years.elementAt ( i ) ).intValue ();
		return ret;
	}

	/**
	 * Get an array of int values that will indicate which months of the specified
	 * year have Journal entries.
	 * 
	 * @param year
	 *          4-digit year
	 * @return
	 */
	public int[] getMonthsForYear ( int year ) {
		if ( listOfDates == null )
			return null;
		HashMap h = new HashMap ();
		Vector months = new Vector ();
		for ( int i = 0; i < listOfDates.length; i++ ) {
			if ( listOfDates[i].getYear () == year ) {
				Integer ival = new Integer ( listOfDates[i].getMonth () );
				if ( !h.containsKey ( ival ) ) {
					h.put ( ival, ival );
					months.addElement ( ival );
				}
			}
		}
		int[] ret = new int[months.size ()];
		for ( int i = 0; i < months.size (); i++ )
			ret[i] = ( (Integer) months.elementAt ( i ) ).intValue ();
		return ret;
	}

	/**
	 * Get all Journal objects for the specified month.
	 * 
	 * @param year
	 *          The 4-digit year
	 * @param month
	 *          The month (Jan=1, Feb=2, etc.)
	 * @return
	 */
	public Vector getEntriesByMonth ( int year, int month ) {
		if ( listOfDates == null )
			return null;
		Vector ret = new Vector ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = (DataFile) dataFiles.elementAt ( i );
			for ( int j = 0; j < df.getJournalCount (); j++ ) {
				Journal journal = df.journalEntryAt ( j );
				if ( journal.getStartDate ().getYear () == year
				    && journal.getStartDate ().getMonth () == month )
					ret.addElement ( journal );
			}
		}
		return ret;
	}

	/**
	 * Get all Journal objects for the specified year.
	 * 
	 * @param year
	 *          The 4-digit year
	 * @return
	 */
	public Vector getEntriesByYear ( int year ) {
		if ( listOfDates == null )
			return null;
		Vector ret = new Vector ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = (DataFile) dataFiles.elementAt ( i );
			for ( int j = 0; j < df.getJournalCount (); j++ ) {
				Journal journal = df.journalEntryAt ( j );
				if ( journal.getStartDate ().getYear () == year )
					ret.addElement ( journal );
			}
		}
		return ret;
	}

	/**
	 * Get all Journal objects.
	 * 
	 * @return
	 */
	public Vector getAllEntries () {
		if ( listOfDates == null )
			return null;
		Vector ret = new Vector ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = (DataFile) dataFiles.elementAt ( i );
			for ( int j = 0; j < df.getJournalCount (); j++ ) {
				Journal journal = df.journalEntryAt ( j );
				ret.addElement ( journal );
			}
		}
		return ret;
	}

	public void updateDateList () {
		Vector dates = new Vector ();
		HashMap h = new HashMap ();
		for ( int i = 0; i < dataFiles.size (); i++ ) {
			DataFile df = (DataFile) dataFiles.elementAt ( i );
			for ( int j = 0; j < df.getJournalCount (); j++ ) {
				Journal journal = df.journalEntryAt ( j );
				if ( journal.getStartDate () != null ) {
					String YMD = Utils.DateToYYYYMMDD ( journal.getStartDate () );
					if ( !h.containsKey ( YMD ) ) {
						h.put ( YMD, YMD );
						dates.addElement ( journal.getStartDate () );
						// System.out.println ( "Added date: " + journal.startDate);
					}
				}
			}
		}
		if ( dates.size () > 0 ) {
			Collections.sort ( dates );
			listOfDates = new Date[dates.size ()];
			for ( int i = 0; i < dates.size (); i++ ) {
				listOfDates[i] = (Date) dates.elementAt ( i );
				System.out.println ( "Found date: " + listOfDates[i] );
			}
			// listOfDates = (Date[]) dates.toArray ();
		} else {
			listOfDates = null;
		}
	}

	/**
	 * Save the specified Journal object. If the Journal is part of an existing
	 * iCalendar file, the entire file will be written out. If this Journal object
	 * is new, then a new iCalendar file will be created. Note: It is up to the
	 * caller to update the Sequence object each time a Journal entry is saved.
	 * The "LAST-MODIFIED" setting will be updated automatically.
	 * 
	 * @param j
	 * @throws IOException
	 */
	public void saveJournal ( Journal j ) throws IOException {
		DataFile dataFile = (DataFile) j.getUserData ();
		if ( dataFile == null ) {
			// New journal. Add to existing data file named YYYYMMDD.ics if
			// it exists.
			dataFile = findDataFile ( j );
			if ( dataFile == null ) {
				// No file for this date (YYYYMMDD.ics) exists yet.
				// So, we need to create a new one.
				File f = new File ( this.directory, Utils.DateToYYYYMMDD ( j
				    .getStartDate () )
				    + ".ics" );
				dataFile = new DataFile ( f.getAbsolutePath () );
				this.dataFiles.addElement ( dataFile );
				// Store in HashMap using just the filename (19991231.ics)
				// as the key
				this.dataFileHash.put ( dataFile.getName ().toLowerCase (), dataFile );
				this.updateDateList ();
			}
			// Now add this journal entry to the file
			dataFile.dataStore.storeJournal ( j );
		} else {
			// If we are updating, then the Journal to be updated should
			// already be updated in the DataStore.
		}
		j.setLastModified ( Date.getCurrentDateTime ( "LAST-MODIFIED" ) );
		dataFile.write ();
	}
}
