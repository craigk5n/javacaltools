package us.k5n.ical;

import java.io.IOException;
import java.io.Reader;

import com.csvreader.CsvReader;

/**
 * A CSV data parser for calendar data. This class was specifically created to
 * parse CSV output from M$ Outlook, but could work with any CSV calendar data
 * if you set the header field names/locations properly.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class CSVParser extends CalendarParser {
	private String titleName = "Subject";
	private String descriptionName = "Description";
	private String startDateName = "Start Date";
	private String startTimeName = "Start Time";
	private String locationName = "Location";
	private String endDataName = "End Date";
	private String endTimeName = "End Time";
	private String allDayName = "All day event";
	private String booleanTrueValue = "True";
	private String booleanFalseValue = "False";
	private int titleColumn = -1;
	private int descriptionColumn = -1;
	private int startDateColumn = -1;
	private int startTimeColumn = -1;
	private int endDateColumn = -1;
	private int endTimeColumn = -1;
	private int locationColumn = -1;
	private int allDayColumn = -1;
	private boolean scanHeaderForColumnLocations = true;

	/**
	 * Create a CSVParser.
	 * 
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public CSVParser(int parseMode) {
		super ( parseMode );
	}

	public void setColumnLocations ( int titleColumn, int descriptionColumn,
	    int startDateColumn, int startTimeColumn, int endDateColumn,
	    int endTimeColumn, int locationColumn, int allDayColumn ) {
		this.titleColumn = titleColumn;
		this.descriptionColumn = descriptionColumn;
		this.startDateColumn = startDateColumn;
		this.startTimeColumn = startTimeColumn;
		this.endDateColumn = endDateColumn;
		this.endTimeColumn = endTimeColumn;
		this.locationColumn = locationColumn;
		this.allDayColumn = allDayColumn;
		this.scanHeaderForColumnLocations = false;
	}

	public boolean parse ( Reader reader ) throws IOException {
		int nerrors = 0;

		CsvReader csv = new CsvReader ( reader );
		if ( !csv.readHeaders () ) {
			this.reportParseError ( new ParseError ( 0,
			    "Error parsing 1st line as header" ) );
			return true;
		}
		// for ( int i = 0; i < csv.getHeaderCount (); i++ ) {
		// System.out.println ( "header#" + i + ": " + csv.getHeader ( i ) );
		// }
		if ( this.scanHeaderForColumnLocations ) {
			for ( int i = 0; i < csv.getHeaderCount (); i++ ) {
				String h = csv.getHeader ( i );
				if ( h.equalsIgnoreCase ( this.titleName ) )
					this.titleColumn = i;
				else if ( h.equalsIgnoreCase ( this.descriptionName ) )
					this.descriptionColumn = i;
				else if ( h.equalsIgnoreCase ( this.startDateName ) )
					this.startDateColumn = i;
				else if ( h.equalsIgnoreCase ( this.startTimeName ) )
					this.startTimeColumn = i;
				else if ( h.equalsIgnoreCase ( this.endDataName ) )
					this.endDateColumn = i;
				else if ( h.equalsIgnoreCase ( this.endTimeName ) )
					this.endTimeColumn = i;
				else if ( h.equalsIgnoreCase ( this.locationName ) )
					this.locationColumn = i;
				else if ( h.equalsIgnoreCase ( this.allDayName ) )
					this.allDayColumn = i;
				else {
					// Ignore this column
				}
			}
			// Make sure we have at least title and date/time
			if ( this.titleColumn < 0 ) {
				this.reportParseError ( new ParseError ( 0, "Could not find '"
				    + this.titleName + "' in CSV header" ) );
				nerrors++;
			}
			if ( this.startDateColumn < 0 ) {
				this.reportParseError ( new ParseError ( 0, "Could not find '"
				    + this.startDateName + "' in CSV header" ) );
				nerrors++;
			}
			if ( this.startTimeColumn < 0 ) {
				this.reportParseError ( new ParseError ( 0, "Could not find '"
				    + this.startTimeName + "' in CSV header" ) );
				nerrors++;
			}
			if ( nerrors > 0 )
				return true;
		}

		int recNum = 1;
		while ( csv.readRecord () ) {
			String title = "Untitled", description = null, location = null;
			Date startDate = null, endDate = null;
			boolean allDay = false;
			boolean valid = true;
			if ( this.titleColumn >= 0 )
				title = csv.get ( this.titleColumn );
			if ( this.allDayColumn >= 0 ) {
				String allDayStr = csv.get ( this.allDayColumn );
				if ( this.booleanFalseValue.equalsIgnoreCase ( allDayStr ) )
					allDay = false;
				else if ( this.booleanTrueValue.equalsIgnoreCase ( allDayStr ) )
					allDay = true;
				else {
					// This is an error, but we may still be able to parse it
				}
			}
			if ( this.startDateColumn >= 0 ) {
				String dateStr = csv.get ( this.startDateColumn );
				String timeStr = null;
				if ( this.startTimeColumn >= 0 )
					timeStr = csv.get ( this.startTimeColumn );
				try {
					startDate = parseDateTime ( "DTSTART", dateStr, allDay ? null
					    : timeStr );
				} catch ( ParseException e1 ) {
					this.reportParseError ( new ParseError ( recNum++,
					    "Invalid start date '" + dateStr + "'", csv.getRawRecord () ) );
					valid = false;
				}
			}
			if ( this.startDateColumn >= 0 ) {
				String dateStr = csv.get ( this.endDateColumn );
				String timeStr = null;
				if ( this.endTimeColumn >= 0 )
					timeStr = csv.get ( this.endTimeColumn );
				try {
					endDate = parseDateTime ( "DTEND", dateStr, allDay ? null : timeStr );
				} catch ( ParseException e1 ) {
					this.reportParseError ( new ParseError ( recNum++,
					    "Invalid end date '" + dateStr + "'", csv.getRawRecord () ) );
					valid = false;
				}
			}
			if ( this.locationColumn >= 0 ) {
				location = csv.get ( this.locationColumn );
			}
			if ( this.descriptionColumn >= 0 ) {
				description = csv.get ( this.descriptionColumn );
			}
			Event event = new Event ( title, description, startDate );
			if ( endDate != null ) {
				event.setEndDate ( endDate );
			}
			// Add event to all DataStore objects
			for ( int i = 0; i < super.numDataStores (); i++ ) {
				DataStore ds = super.getDataStoreAt ( i );
				ds.storeEvent ( event );
			}
			recNum++;
		}
		return false;
	}

	// TODO: support date formats other than "12/31/1999" or "12/31/99"
	private Date parseDateTime ( String dateType, String date, String time )
	    throws ParseException {
		int Y, M, D, h, m, s;
		String[] args = null;
		Date ret = null;
		args = date.split ( "[/-]" );
		if ( args == null || args.length != 3 ) {
			throw new ParseException ( "Invalid date", date );
		}
		try {
			Y = Integer.parseInt ( args[2] );
			// hack: handle 2-digit years
			if ( Y < 100 && Y < 70 )
				Y += 1900;
			else if ( Y < 100 )
				Y += 2000;
			M = Integer.parseInt ( args[0] );
			D = Integer.parseInt ( args[1] );
		} catch ( NumberFormatException e1 ) {
			throw new ParseException ( "Invalid date: " + e1.getMessage (), date );
		}

		try {
			ret = new Date ( dateType, Y, M, D );
		} catch ( BogusDataException e1 ) {
			throw new ParseException ( "Invalid date: " + e1.getMessage (), date );
		}

		if ( time != null && time.length () > 0 ) {
			args = time.split ( "[ :]" );
			try {
				h = Integer.parseInt ( args[0] );
				m = Integer.parseInt ( args[1] );
				s = Integer.parseInt ( args[2] );
				if ( args.length > 3 ) {
					// look for AM or PM
					if ( args[3].toUpperCase ().equals ( "PM" ) ) {
						if ( h < 12 )
							h += 12;
					} else if ( args[3].toUpperCase ().equals ( "AM" ) ) {
						if ( h == 12 )
							h = 0; // 12AM = 0
					}
				}
			} catch ( NumberFormatException e1 ) {
				throw new ParseException ( "Invalid date time: " + e1.getMessage (),
				    date );
			}
			ret.setHour ( h );
			ret.setMinute ( m );
			ret.setSecond ( s );
			ret.setDateOnly ( false );
		}
		return ret;
	}

	public String getAllDayName () {
		return allDayName;
	}

	public void setAllDayName ( String allDayName ) {
		this.allDayName = allDayName;
	}

	public String getBooleanFalseValue () {
		return booleanFalseValue;
	}

	public void setBooleanFalseValue ( String booleanFalseValue ) {
		this.booleanFalseValue = booleanFalseValue;
	}

	public String getBooleanTrueValue () {
		return booleanTrueValue;
	}

	public void setBooleanTrueValue ( String booleanTrueValue ) {
		this.booleanTrueValue = booleanTrueValue;
	}

	public String getDescriptionName () {
		return descriptionName;
	}

	public void setDescriptionName ( String descriptionName ) {
		this.descriptionName = descriptionName;
	}

	public String getEndDataName () {
		return endDataName;
	}

	public void setEndDataName ( String endDataName ) {
		this.endDataName = endDataName;
	}

	public String getEndTimeName () {
		return endTimeName;
	}

	public void setEndTimeName ( String endTimeName ) {
		this.endTimeName = endTimeName;
	}

	public String getLocationName () {
		return locationName;
	}

	public void setLocationName ( String locationName ) {
		this.locationName = locationName;
	}

	public boolean isScanHeaderForColumnLocations () {
		return scanHeaderForColumnLocations;
	}

	public void setScanHeaderForColumnLocations (
	    boolean scanHeaderForColumnLocations ) {
		this.scanHeaderForColumnLocations = scanHeaderForColumnLocations;
	}

	public String getStartDateName () {
		return startDateName;
	}

	public void setStartDateName ( String startDateName ) {
		this.startDateName = startDateName;
	}

	public String getStartTimeName () {
		return startTimeName;
	}

	public void setStartTimeName ( String startTimeName ) {
		this.startTimeName = startTimeName;
	}

	public String getTitleName () {
		return titleName;
	}

	public void setTitleName ( String titleName ) {
		this.titleName = titleName;
	}

}
