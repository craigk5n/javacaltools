package us.k5n.journal;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import us.k5n.ical.Date;
import us.k5n.ical.Journal;
import us.k5n.ical.Utils;

/**
 * The Repository class manages all loading and saving of data files.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class Repository {
	File directory;
	Vector dataFiles;
	int parseErrorCount = 0;
	int journalCount = 0;
	Date[] listOfDates;

	public Repository(File dir, boolean strictParsing) {
		this.directory = dir;
		this.dataFiles = new Vector ();

		// Load all files.
		File[] files = this.directory.listFiles ( new IcsFileFilter () );
		for ( int i = 0; files != null && i < files.length; i++ ) {
			DataFile f = new DataFile ( files[i].getAbsolutePath (), strictParsing );
			if ( f != null ) {
				this.dataFiles.addElement ( f );
				journalCount += f.getJournalCount ();
				parseErrorCount += f.getParseErrorCount ();
			}
		}

		updateDateList ();
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
				if ( journal.startDate.getYear () == year
				    && journal.startDate.getMonth () == month )
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
				if ( journal.startDate != null ) {
					String YMD = Utils.DateToYYYYMMDD ( journal.startDate );
					if ( !h.containsKey ( YMD ) ) {
						h.put ( YMD, YMD );
						dates.addElement ( journal.startDate );
						System.out.println ( "Added date: " + journal.startDate);
					}
				}
			}
		}
		if ( dates.size () > 0 ) {
			Collections.sort ( dates );
			listOfDates = new Date[dates.size ()];
			for ( int i = 0; i < dates.size (); i++ ) {
				listOfDates[i] = (Date)dates.elementAt ( i );
			}
			//listOfDates = (Date[]) dates.toArray ();
		} else {
			listOfDates = null;
		}
	}

}
