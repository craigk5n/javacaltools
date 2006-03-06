/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
*/

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

