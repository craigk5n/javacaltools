package us.k5n.journal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.k5n.ical.Categories;
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
 */
public class Repository {
	File directory;
	List<DataFile> dataFiles;
	Map<String, File> dataFileHash;
	int parseErrorCount = 0;
	int journalCount = 0;
	Date[] listOfDates;
	Map<String, String> uidHash;
	private List<RepositoryChangeListener> changeListeners;
	private List<String> categories; // List of String categories

	public Repository(File dir, boolean strictParsing) {
		this.directory = dir;
		this.dataFiles = new ArrayList<DataFile>();
		this.dataFileHash = new HashMap<String, File>();
		this.uidHash = new HashMap<String, String>();
		this.changeListeners = new ArrayList<RepositoryChangeListener>();
		this.categories = new ArrayList<String>();

		// Load all files.
		File[] files = this.directory.listFiles(new IcsFileFilter());
		for (int i = 0; files != null && i < files.length; i++) {
			DataFile f = new DataFile(files[i].getAbsolutePath(), strictParsing);
			if (f != null) {
				this.addDataFile(f);
			}
		}

		rebuildPrivateData();
	}

	public void addDataFile(DataFile f) {
		this.dataFiles.add(f);
		journalCount += f.getJournalCount();
		parseErrorCount += f.getParseErrorCount();
		// Store in HashMap using just the filename (19991231.ics)
		// as the key
		this.dataFileHash.put(f.getName().toLowerCase(), f);
	}

	public DataFile findDataFile(Journal j) {
		String YMD = Utils.DateToYYYYMMDD(j.getStartDate());
		String fileName = YMD + ".ics";
		DataFile dataFile = (DataFile) this.dataFileHash.get(fileName);
		return dataFile;
	}

	/**
	 * Get an array of int values indicating which years have Journal entries.
	 * 
	 * @return
	 */
	public int[] getYears() {
		if (listOfDates == null)
			return null;
		Map<Integer, Integer> h = new HashMap<Integer, Integer>();
		List<Integer> years = new ArrayList<Integer>();
		for (int i = 0; i < listOfDates.length; i++) {
			Integer ival = new Integer(listOfDates[i].getYear());
			if (!h.containsKey(ival)) {
				h.put(ival, ival);
				years.add(ival);
			}
		}
		int[] ret = new int[years.size()];
		for (int i = 0; i < years.size(); i++)
			ret[i] = years.get(i);
		return ret;
	}

	/**
	 * Get an array of int values that will indicate which months of the specified
	 * year have Journal entries.
	 * 
	 * @param year
	 *             4-digit year
	 * @return
	 */
	public int[] getMonthsForYear(int year) {
		if (listOfDates == null)
			return null;
		HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
		List<Integer> months = new ArrayList<Integer>();
		for (int i = 0; i < listOfDates.length; i++) {
			if (listOfDates[i].getYear() == year) {
				Integer ival = new Integer(listOfDates[i].getMonth());
				if (!h.containsKey(ival)) {
					h.put(ival, ival);
					months.add(ival);
				}
			}
		}
		int[] ret = new int[months.size()];
		for (int i = 0; i < months.size(); i++)
			ret[i] = months.get(i);
		return ret;
	}

	/**
	 * Get all Journal objects for the specified month.
	 * 
	 * @param year
	 *              The 4-digit year
	 * @param month
	 *              The month (Jan=1, Feb=2, etc.)
	 * @return
	 */
	public List<Journal> getEntriesByMonth(int year, int month) {
		if (listOfDates == null)
			return null;
		List<Journal> ret = new ArrayList<Journal>();
		for (int i = 0; i < dataFiles.size(); i++) {
			DataFile df = dataFiles.get(i);
			for (int j = 0; j < df.getJournalCount(); j++) {
				Journal journal = df.journalEntryAt(j);
				if (journal.getStartDate() == null) {
					System.err.println("Error: no DTSTART date for entry #" + (j + 1)
							+ " of " + df);
				} else {
					if (journal.getStartDate().getYear() == year
							&& journal.getStartDate().getMonth() == month)
						ret.add(journal);
				}
			}
		}
		return ret;
	}

	/**
	 * Get all Journal objects for the specified year.
	 * 
	 * @param year
	 *             The 4-digit year
	 * @return
	 */
	public List<Journal> getEntriesByYear(int year) {
		if (listOfDates == null)
			return null;
		List<Journal> ret = new ArrayList<Journal>();
		for (int i = 0; i < dataFiles.size(); i++) {
			DataFile df = dataFiles.get(i);
			for (int j = 0; j < df.getJournalCount(); j++) {
				Journal journal = df.journalEntryAt(j);
				if (journal.getStartDate() == null) {
					System.err.println("Error: no DTSTART date for entry #" + (j + 1)
							+ " of " + df);
				} else {
					if (journal.getStartDate().getYear() == year)
						ret.add(journal);
				}
			}
		}
		return ret;
	}

	/**
	 * Get all Journal objects.
	 * 
	 * @return
	 */
	public List<Journal> getAllEntries() {
		if (listOfDates == null)
			return null;
		List<Journal> ret = new ArrayList<Journal>();
		for (int i = 0; i < dataFiles.size(); i++) {
			DataFile df = dataFiles.get(i);
			for (int j = 0; j < df.getJournalCount(); j++) {
				Journal journal = df.journalEntryAt(j);
				ret.add(journal);
			}
		}
		return ret;
	}

	/**
	 * Update the listOfDates array. Update the List of existing categories.
	 */
	private void rebuildPrivateData() {
		List<Date> dates = new ArrayList<Date>();
		this.categories = new ArrayList<String>();
		HashMap<String, String> catH = new HashMap<String, String>();
		HashMap<String, String> h = new HashMap<String, String>();
		for (int i = 0; i < dataFiles.size(); i++) {
			DataFile df = dataFiles.get(i);
			// System.out.println ( "DataFile#" + i + ": " + df.toString () );
			// System.out.println ( " df.getJournalCount () =" + df.getJournalCount ()
			// );
			for (int j = 0; j < df.getJournalCount(); j++) {
				Journal journal = df.journalEntryAt(j);
				if (journal.getStartDate() != null) {
					String YMD = Utils.DateToYYYYMMDD(journal.getStartDate());
					if (!h.containsKey(YMD)) {
						h.put(YMD, YMD);
						dates.add(journal.getStartDate());
						// System.out.println ( "Added date: " + journal.getStartDate () );
					}
				}
				Categories cats = journal.getCategories();
				if (cats != null && cats.getValue() != null) {
					String[] catArray = splitCategories(cats.getValue());
					for (int k = 0; catArray != null && k < catArray.length; k++) {
						String c1 = catArray[k].trim();
						if (c1.length() > 0) {
							String c1up = c1.toUpperCase();
							if (!catH.containsKey(c1up)) {
								this.categories.add(c1);
								catH.put(c1up, c1up);
							}
						}
					}
				}
			}
		}
		if (dates.size() > 0) {
			Collections.sort(dates);
			listOfDates = new Date[dates.size()];
			for (int i = 0; i < dates.size(); i++) {
				listOfDates[i] = dates.get(i);
				// System.out.println ( "Found date: " + listOfDates[i] );
			}
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
	public void saveJournal(Journal j) throws IOException {
		boolean added = false;

		DataFile dataFile = (DataFile) j.getUserData();
		if (dataFile == null) {
			// New journal. Add to existing data file named YYYYMMDD.ics if
			// it exists.
			dataFile = findDataFile(j);
			if (dataFile == null) {
				added = true;
				// No file for this date (YYYYMMDD.ics) exists yet.
				// So, we need to create a new one.
				File f = new File(this.directory, Utils.DateToYYYYMMDD(j
						.getStartDate())
						+ ".ics");
				dataFile = new DataFile(f.getAbsolutePath());
				dataFile.addJournal(j);
				this.addDataFile(dataFile);
			} else {
				// Add this journal entry to the file
				dataFile.addJournal(j);
			}
		}
		j.setLastModified(Date.getCurrentDateTime("LAST-MODIFIED"));
		j.setUserData(dataFile);
		dataFile.write();

		rebuildPrivateData();

		if (added) {
			for (int i = 0; this.changeListeners != null
					&& i < this.changeListeners.size(); i++) {
				RepositoryChangeListener l = (RepositoryChangeListener) this.changeListeners
						.get(i);
				l.journalAdded(j);
			}
		} else {
			// If we are updating, then the Journal to be updated should
			// already be updated in the DataStore.
			for (int i = 0; this.changeListeners != null
					&& i < this.changeListeners.size(); i++) {
				RepositoryChangeListener l = (RepositoryChangeListener) this.changeListeners
						.get(i);
				l.journalUpdated(j);
			}
		}
	}

	/**
	 * Delete the specified Journal object.
	 * 
	 * @param j
	 * @throws IOException
	 */
	public boolean deleteJournal(Journal j) throws IOException {
		boolean deleted = false;
		DataFile dataFile = (DataFile) j.getUserData();
		if (dataFile == null) {
			// New journal. Nothing to do...
			System.err.println("Not found...");
		} else {
			// Journal to be deleted should be in the DataStore.
			if (dataFile.removeJournal(j)) {
				deleted = true;
				dataFile.write();
				rebuildPrivateData();
				for (int i = 0; this.changeListeners != null
						&& i < this.changeListeners.size(); i++) {
					RepositoryChangeListener l = (RepositoryChangeListener) this.changeListeners
							.get(i);
					l.journalDeleted(j);
				}
			} else {
				// System.out.println ( "Not deleted" );
			}
		}
		return deleted;
	}

	/**
	 * Ask to be notified when changes are made to the Repository.
	 * 
	 * @param l
	 */
	public void addChangeListener(RepositoryChangeListener l) {
		if (this.changeListeners == null)
			this.changeListeners = new ArrayList<RepositoryChangeListener>();
		this.changeListeners.add(l);
	}

	public List<String> getCategories() {
		return this.categories;
	}

	private static String[] splitCategories(String categories) {
		return categories.trim().split(",");
	}
}
