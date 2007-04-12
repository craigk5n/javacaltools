package us.k5n.journal;

import java.io.File;
import java.io.FileFilter;

/**
 * A FileFilter implementation that will include only ".ics" filenames.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class IcsFileFilter implements FileFilter {

	public boolean accept ( File pathname ) {
		return pathname.toString ().toUpperCase ().endsWith ( ".ICS" );
	}

}
