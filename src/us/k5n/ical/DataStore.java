

package us.k5n.ical;


/**
  * The DataStore interface defines how parsed iCal data
  * (VTIMEZONE, VEVENT, VTODO, VJOURNAL, VFREEBUSY) will be stored.
  * By default, all objects will be stored internally.
  * However, you can specify that:
  * <ul>
  *   <li>the data should be stored internally and a new DataStore
  *       should also receive the events </li>
  *   <li>the data should <em>not</em> be stored internally and a new DataStore
  *       should receive the events </li>
  * </ul>
  * See the IcalParser interface for how to change these settings.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  * @see IcalParser
  */
interface DataStore
{

  /**
    * This method will be called the parser finds a VTIMEZONE object.
    */
  public void storeTimezone ( Timezone timezone );

  /**
    * This method will be called the parser finds a VEVENT object.
    */
  public void storeEvent ( Event event );

  /**
    * This method will be called the parser finds a VTODO object.
    */
  public void storeTodo ( Todo todo );

  /**
    * This method will be called the parser finds a VJOURNAL object.
    */
  public void storeJournal ( Journal journal );

  /**
    * This method will be called the parser finds a VFREEBUSY object.
    */
  public void storeFreebusy ( Freebusy freebusy );


  /**
    * Get all Event objects.
    * @return	A Vector if Event objects
    */
  public java.util.Vector getAllEvents ();
}

