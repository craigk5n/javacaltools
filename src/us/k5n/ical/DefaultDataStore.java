

package us.k5n.ical;


import java.util.Vector;


/**
  * A simple implementation of the DataStore interface.  This is the default
  * implementation used by the IcalParser class.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  * @see IcalParser
  */
public class DefaultDataStore
  implements DataStore
{
  Vector timezones, events, todos, journals, freebusys;

  /**
    * Constructor
    */
  public DefaultDataStore ()
  {
    timezones = new Vector ();
    events = new Vector ();
    todos = new Vector ();
    journals = new Vector ();
    freebusys = new Vector ();
  }

  /**
    * This method will be called the parser finds a VTIMEZONE object.
    */
  public void storeTimezone ( Timezone timezone )
  {
    timezones.addElement ( timezone );
  }

  /**
    * This method will be called the parser finds a VEVENT object.
    */
  public void storeEvent ( Event event )
  {
    events.addElement ( event );
  }

  /**
    * This method will be called the parser finds a VTODO object.
    */
  public void storeTodo ( Todo todo )
  {
    todos.addElement ( todo );
  }

  /**
    * This method will be called the parser finds a VJOURNAL object.
    */
  public void storeJournal ( Journal journal )
  {
    journals.addElement ( journal );
  }

  /**
    * This method will be called the parser finds a VFREEBUSY object.
    */
  public void storeFreebusy ( Freebusy freebusy )
  {
    freebusys.addElement ( freebusy );
  }

  /**
    * Get a Vector of all Event objects
    */
  public Vector getAllEvents ()
  {
    return events;
  }

}

