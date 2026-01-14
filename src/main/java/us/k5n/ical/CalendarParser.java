package us.k5n.ical;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * The CalendarParser is an abstract class that should be extended to implement
 * a specific parser (iCalendar, CSV, etc.)
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public abstract class CalendarParser implements Constants {
	protected List<ParseErrorListener> errorListeners;
	protected List<ParseError> errors;
	protected List<DataStore> dataStores;
	protected int parseMethod = PARSE_LOOSE;

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
	public CalendarParser(int parseMethod) {
		this.parseMethod = parseMethod;
		errorListeners = new ArrayList<ParseErrorListener>();
		errors = new ArrayList<ParseError>();
		dataStores = new ArrayList<DataStore>();
		// Add the default DataStore
		dataStores.add(new DefaultDataStore());
	}

	/**
	 * Get the current setting for parse method (PARSE_STRICT or PARSE_LOOSE)
	 * 
	 * @return PARSE_STRICT or PARSE_LOOSE
	 */
	public int getParseMethod() {
		return parseMethod;
	}

	/**
	 * Add a DataStore. Each DataStore will be called during the parsing process
	 * as each timezone, event, todo, or journal object is discovered.
	 * 
	 * @param dataStore
	 *                  The new DataStore to add
	 */
	public void addDataStore(DataStore dataStore) {
		dataStores.add(dataStore);
	}

	/**
	 * Return the number of DataStores currently registered.
	 */
	public int numDataStores() {
		return dataStores.size();
	}

	/**
	 * Return the specified DataStore.
	 * 
	 * @param ind
	 *            The DataStore index number (0=first)
	 */
	public DataStore getDataStoreAt(int ind) {
		return dataStores.get(ind);
	}

	/**
	 * Remove the specified DataStore.
	 * 
	 * @param ind
	 *            the DataStore index number (0=first)
	 * @return true if the DataStore was found and removed
	 */
	public boolean removeDataStoreAt(int ind) {
		if (ind < dataStores.size()) {
			dataStores.remove(ind);
			return true;
		}
		// not found
		return false;
	}

	/**
	 * Is the current parse method set to PARSE_STRICT?
	 * 
	 * @return true if the current parse method is PARSE_STRICT
	 */
	public boolean isParseStrict() {
		return (parseMethod == PARSE_STRICT);
	}

	/**
	 * Is the current parse method set to PARSE_LOOSE?
	 * 
	 * @return true if the current parse method is PARSE_LOOSE
	 */
	public boolean isParseLoose() {
		return (parseMethod == PARSE_LOOSE);
	}

	/**
	 * Set the current parse method
	 * 
	 * @param parseMethod
	 *                    The new parse method (PARSE_STRICT, PARSE_LOOSE)
	 */
	public void setParseMethod(int parseMethod) {
		this.parseMethod = parseMethod;
	}

	/**
	 * Add a listener for parse error messages.
	 * 
	 * @param pel The listener for parse errors
	 */
	public void addParseErrorListener(ParseErrorListener pel) {
		errorListeners.add(pel);
	}

	/**
	 * Send a parse error message to all parse error listeners
	 * 
	 * @param error
	 *                The parse error object containing message and offending data
	 */
	public void reportParseError(ParseError error) {
		errors.add(error);
		for (ParseErrorListener pel : errorListeners) {
			pel.reportParseError(error);
		}
	}

	/**
	 * Get a List of all errors encountered;.
	 * 
	 * @return A List of ParseError objects
	 */
	public List<ParseError> getAllErrors() {
		return errors;
	}

	/**
	 * Convert all data into an iCalendar String.
	 * 
	 * @return iCalendar String of all data
	 */
	public String toICalendar() {
		StringBuffer ret = new StringBuffer(1024);
		ret.append("BEGIN:VCALENDAR");
		ret.append(CRLF);
		ret.append("VERSION:2.0");
		ret.append(CRLF);
		// Should we use the PRODID we parsed on input? Since we are generating
		// output, I think we will use ours.
		// TODO: add version number in the following
		ret.append("PRODID:-//k5n.us//Java Calendar Tools//EN");
		ret.append(CRLF);

		// Include events
		List<Event> events = ((DataStore) getDataStoreAt(0)).getAllEvents();
		for (int i = 0; i < events.size(); i++) {
			Event ev = (Event) events.get(i);
			ret.append(ev.toICalendar());
		}
		// Include journal entries
		List<Journal> journals = ((DataStore) getDataStoreAt(0))
				.getAllJournals();
		for (int i = 0; i < journals.size(); i++) {
			Journal j = (Journal) journals.get(i);
			ret.append(j.toICalendar());
		}

		ret.append("END:VCALENDAR");
		ret.append(CRLF);
		return ret.toString();
	}

	/**
	 * Parse a Reader object.
	 * 
	 * @param reader
	 * @return true if no errors, false if errors found
	 * @throws IOException
	 */
	public abstract boolean parse(Reader reader) throws IOException;

}
