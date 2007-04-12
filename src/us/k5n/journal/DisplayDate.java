package us.k5n.journal;

import java.text.SimpleDateFormat;

import us.k5n.ical.Date;

/**
 * Repackage a us.k5n.ical.Date object so that we can format the date for
 * display using SimpleDateFormat.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class DisplayDate implements Comparable {
	// Date formats are specified in the Java API doc.
	// TODO: allow setting this format in user preferences
	private static String dateFormat = "EEE, d MMM yyyy h:mm a";
	private java.util.Date javaDate;

	public DisplayDate(Date d) {
		javaDate = d.toCalendar ().getTime ();
	}

	public String toString () {
		SimpleDateFormat format = new SimpleDateFormat ( dateFormat );
		return format.format ( javaDate );
	}

	public int compareTo ( Object arg0 ) {
		DisplayDate d2 = (DisplayDate) arg0;
		return javaDate.compareTo ( d2.javaDate );
	}

}
