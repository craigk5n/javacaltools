package us.k5n.journal;

import us.k5n.ical.Journal;

/**
 * Interface for receiving updates from Repository.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public interface RepositoryChangeListener {

	public abstract void journalAdded ( Journal journal );

	public abstract void journalUpdated ( Journal journal );

	public abstract void journalDeleted ( Journal journal );
}
