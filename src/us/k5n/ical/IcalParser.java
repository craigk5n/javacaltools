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
import java.io.BufferedReader;
import java.io.IOException;


/**
  * iCal Parser class -
  * This object is required for most parsing methods and can be thought
  * of as the main entry point into this package.
  * It can serve as a DataStore to store events/todos/etc when a custom
  * DataStore is not needed.
  * <br/>
  * Example usage:
  * <blockquote><pre>
  *   IcalParser parser = new IcalParser ();
  *   File f = new File ( "/tmp/test.ics" );
  *   try {
  *     BufferedReader r = new BufferedReader ( new FileReader ( f ) );
  *     parser.parse ( r );
  *   } catch ( IOException e ) {
  *     // ... report error
  *   }
  *   r.close ();
  *   Vector events = parser.getAllEvents ();
  *   Vector errors = parser.getAllErrors ();
  * </pre></blockquote>
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class IcalParser
  implements Constants
{
  int parseMethod; // PARSE_STRICT or PARSE_LOOSE
  Vector errorListeners;
  Vector errors;
  Property icalVersion = null;
  Property prodId = null;
  Property method = null;
  Property calscale = null;
  String language = "EN"; // default language setting

  Vector dataStores; // DataStore objects in a Vector

  static final int STATE_NONE = 0;
  static final int STATE_VCALENDAR = 1;
  static final int STATE_VEVENT = 2;
  static final int STATE_VTODO = 3;
  static final int STATE_VJOURNAL = 4;
  static final int STATE_VTIMEZONE = 5;
  static final int STATE_VFREEBUSY = 6;
  static final int STATE_DONE = 7;

  /**
    * Create an IcalParser object.  By default, this will also setup
    * the default DataStore object.
    * To remove the default DataStore, you can call removeDataStoreAt(0).
    * @param parseMethod	Specifies the parsing method, which should be
    *			either PARSE_STRICT or PARSE_LOOSE.  The PARSE_STRICT
    *			method will follow the RFC 2445 specification
    *			strictly and is intended to be used to validate
    *			iCal data.  Most clients should specify PARSE_LOOSE
    *			to capture as much of the data as possible.
    */
  public IcalParser ( int parseMethod )
  {
    this ( parseMethod, "EN" );
  }

  /**
    * Create an IcalParser object.  By default, this will also setup
    * the default DataStore object.
    * To remove the default DataStore, you can call removeDataStoreAt(0).
    * @param parseMethod	Specifies the parsing method, which should be
    *			either PARSE_STRICT or PARSE_LOOSE.  The PARSE_STRICT
    *			method will follow the RFC 2445 specification
    *			strictly and is intended to be used to validate
    *			iCal data.  Most clients should specify PARSE_LOOSE
    *			to capture as much of the data as possible.
    * @param language	Default language setting.  When parsing objects,
    *			the property that matches this language setting
    *			will take priority.  For example, if "EN" is
    *			specified as a parameter here, and an event
    *			in iCal has a summary in "EN" and also in "FR", then
    *			the summary in "EN" will be returned when
    *			the event is queries for a summary.
    */
  public IcalParser ( int parseMethod, String language )
  {
    this.parseMethod = parseMethod;
    this.language = language;
    errorListeners = new Vector ();
    errors = new Vector ();
    dataStores = new Vector ();
    // Add the default DataStore
    dataStores.addElement ( new DefaultDataStore () );
  }

  /**
    * Get the current setting for parse method (PARSE_STRICT or PARSE_LOOSE)
    * @return	PARSE_STRICT or PARSE_LOOSE
    */
  public int getParseMethod ()
  {
    return parseMethod;
  }

  /**
    * Add a DataStore.  Each DataStore will be called during the parsing
    * process as each timezone, event, todo, or journal object is
    * discovered.
    * @param dataStore	The new DataStore to add
    */
  public void addDataStore ( DataStore dataStore )
  {
    dataStores.addElement ( dataStore );
  }

  /**
    * Return the number of DataStores currently registered.
    */
  public int numDataStores ()
  {
    return dataStores.size();
  }

  /**
    * Return the specified DataStore.
    * @param ind	The DataStore index number (0=first)
    */
  public DataStore getDataStoreAt ( int ind )
  {
    return (DataStore) dataStores.elementAt ( ind );
  }

  /**
    * Remove the specified DataStore.
    * @param ind	the DataStore index number (0=first)
    * @return	true if the DataStore was found and removed
    */
  public boolean removeDataStoreAt ( int ind )
  {
    if ( ind < dataStores.size() ) {
      dataStores.removeElementAt ( ind );
      return true;
    }
    // not found
    return false;
  }
  
  /**
    * Is the current parse method set to PARSE_STRICT?
    * @return	true if the current parse method is PARSE_STRICT
    */
  public boolean isParseStrict ()
  {
    return ( parseMethod == PARSE_STRICT );
  }

  /**
    * Is the current parse method set to PARSE_LOOSE?
    * @return	true if the current parse method is PARSE_LOOSE
    */
  public boolean isParseLoose ()
  {
    return ( parseMethod == PARSE_LOOSE );
  }

  /**
    * Set the current parse method
    * @param parseMethod	The new parse method (PARSE_STRICT, PARSE_LOOSE)
    */
  public void setParseMethod ( int parseMethod )
  {
    this.parseMethod = parseMethod;
  }


  /**
    * Add a listener for parse error messages.
    * @pel	The listener for parse errors
    */
  public void addParseErrorListener ( ParseErrorListener pel )
  {
    errorListeners.addElement ( pel );
  }

  /**
    * Send a parse error message to all parse error listeners
    * @param msg	The error message
    * @param icalStr	The offending line(s) of iCal
    */
  public void reportParseError ( ParseError error )
  {
    errors.addElement ( error );
    for ( int i = 0; i < errorListeners.size(); i++ ) {
      ParseErrorListener pel =
        (ParseErrorListener) errorListeners.elementAt ( i );
      pel.reportParseError ( error );
    }
  }

  /**
    * Get a Vector of all errors encountered;.
    * @return	A Vector of ParseError objects
    */
  public Vector getAllErrors ()
  {
    return errors;
  }


  /**
    * Parse a File.
    * @param reader	The java.io.Reader object to read the
    *			iCal data from.  To parse a String object
    *			use java.io.StringReader.
    * @return		true if no parse errors encountered
    */
  public boolean parse ( java.io.Reader reader )
    throws IOException
  {
    boolean noErrors = true;
    String line, nextLine;
    BufferedReader r = new BufferedReader ( reader );
    StringBuffer data = new StringBuffer ();
    int state = STATE_NONE;
    int ln = 0; // line number
    int startLineNo = 0;
    Vector textLines;
    boolean done = false;

    // Because iCal allows lines to be "folded" (continued) onto multiple
    // lines, you need to peek ahead to the next line to know if you have
    // all the text for what you are trying to parse.
    // The "line" variable is what is currently being parsed.  The "nextLine"
    // variable contains the next line of text to be processed.
    // TODO: line numbers in errors may be off for folded lines since the
    // last line number of the text will be reported.
    textLines = new Vector ();
    nextLine = r.readLine ();
    while ( ! done ) {
      line = nextLine;
      ln++;
      if ( nextLine != null ) {
        nextLine = r.readLine();
        // if nextLine is null, don't set done to true yet since we still
        // need another iteration through the while loop for the text
        // to get processed.
      }
      // Check to see if next line is a continuation of the current
      // line.  If it is, then append the contents of the next line
      // onto the current line.
      if ( nextLine != null && nextLine.length() > 0 &&
        ( nextLine.charAt ( 0 ) == SPACE || nextLine.charAt ( 0 ) == TAB ) ) {
        // Line folding found.  Add to previous line and continue.
        nextLine = line + CRLF + nextLine;
        continue; // skip back to start of while loop
      }
      data.append ( line );
      String lineUp = line.toUpperCase();

//System.out.println ( "[DATA:" + state + "]" + line );
      switch ( state ) {

        case STATE_NONE:
          if ( lineUp.startsWith ( "BEGIN:VCALENDAR" ) )
            state = STATE_VCALENDAR;
          else if ( lineUp.length() == 0 ) {
            // ignore leading blank lines
          } else {
            // Hmmm... should always start with this.
            if ( isParseStrict () ) {
              reportParseError ( new ParseError (
                ln, "Data found outside VCALENDAR block", line ) );
            }
          }
          break;

        case STATE_VCALENDAR:
          if ( lineUp.startsWith ( "BEGIN:VTIMEZONE" ) ) {
            state = STATE_VTIMEZONE;
            startLineNo = ln; // mark starting line number
            textLines.removeAllElements ();
            textLines.addElement ( line );
          } else if ( lineUp.startsWith ( "BEGIN:VEVENT" ) ) {
            state = STATE_VEVENT;
            startLineNo = ln; // mark starting line number
            textLines.removeAllElements ();
            textLines.addElement ( line );
          } else if ( lineUp.startsWith ( "BEGIN:VTODO" ) ) {
            state = STATE_VTODO;
            startLineNo = ln; // mark starting line number
            textLines.removeAllElements ();
            textLines.addElement ( line );
          } else if ( lineUp.startsWith ( "BEGIN:VJOURNAL" ) ) {
            state = STATE_VJOURNAL;
            startLineNo = ln; // mark starting line number
            textLines.removeAllElements ();
            textLines.addElement ( line );
          } else if ( lineUp.startsWith ( "BEGIN:VFREEBUSY" ) ) {
            state = STATE_VFREEBUSY;
            startLineNo = ln; // mark starting line number
            textLines.removeAllElements ();
            textLines.addElement ( line );
          } else if ( lineUp.startsWith ( "END:VCALENDAR" ) ) {
            state = STATE_DONE;
          } else if ( lineUp.startsWith ( "VERSION" ) ) {
            if ( icalVersion != null && isParseStrict() ) {
              // only one of these allowed
              reportParseError ( new ParseError (
                ln, "Only one VERSION token allowed", line ) );
            } else {
              try {
                icalVersion = new Property ( line, getParseMethod() );
              } catch ( ParseException e ) {
                reportParseError ( new ParseError (
                  ln, "Parse error in VERSION: " + e.toString(), line ) );
              }
            }
          } else if ( lineUp.startsWith ( "PRODID" ) ) {
            if ( prodId != null && isParseStrict() ) {
              // only one of these allowed
              reportParseError ( new ParseError (
                ln, "Only one PRODID token allowed", line ) );
            } else {
              try {
                prodId = new Property ( line, getParseMethod() );
              } catch ( ParseException e ) {
                reportParseError ( new ParseError (
                  ln, "Parse error in PRODID: " + e.toString(), line ) );
              }
            }
          } else if ( lineUp.startsWith ( "CALSCALE" ) ) {
            try {
              calscale = new Property ( line, getParseMethod() );
            } catch ( ParseException e ) {
              reportParseError ( new ParseError (
                ln, "Parse error in CALSCALE: " + e.toString(), line ) );
            }
          } else if ( lineUp.startsWith ( "METHOD" ) ) {
            try {
              method = new Property ( line, getParseMethod() );
            } catch ( ParseException e ) {
              reportParseError ( new ParseError (
                ln, "Parse error in CALSCALE: " + e.toString(), line ) );
            }
          } else {
            // what else could this be???
            if ( lineUp.trim().length() == 0 ) {
              // ignore blank lines
            } else if ( isParseStrict () ) {
              reportParseError ( new ParseError (
                ln, "Unrecognized data found in VCALENDAR block", line ) );
            }
          }
          break;

        case STATE_VTIMEZONE:
          textLines.addElement ( line );
          if ( lineUp.startsWith ( "END:VTIMEZONE" ) ) {
            state = STATE_VCALENDAR;
            Timezone timezone = new Timezone ( this, startLineNo, textLines );
            if ( timezone.isValid() ) {
              for ( int i = 0; i < dataStores.size(); i++ ) {
                DataStore ds = (DataStore) dataStores.elementAt ( i );
                ds.storeTimezone ( timezone );
              }
            }
            textLines.removeAllElements (); // truncate Vector
          }
          break;

        case STATE_VTODO:
          textLines.addElement ( line );
          if ( lineUp.startsWith ( "END:VTODO" ) ) {
            state = STATE_VCALENDAR;
            // Send the Todo object to all DataStore objects
            /*** TODO - not yet implemented
                 Uncomment this when Todo.java is implemented
            Todo todo = new Todo ( this, startLineNo, textLines );
            if ( todo.isValid() ) {
              for ( int i = 0; i < dataStores.size(); i++ ) {
                DataStore ds = (DataStore) dataStores.elementAt ( i );
                ds.storeTodo ( todo );
              }
            }
            ***/
            textLines.removeAllElements (); // truncate Vector
          }
          break;

        case STATE_VJOURNAL:
          textLines.addElement ( line );
          if ( lineUp.startsWith ( "END:VJOURNAL" ) ) {
            state = STATE_VCALENDAR;
            // Send the Journal object to all DataStore objects
            /*** TODO - not yet implemented
                 Uncomment this when Journal.java is implemented
            Journal journal = new Journal ( this, startLineNo, textLines );
            if ( journal.isValid() ) {
              for ( int i = 0; i < dataStores.size(); i++ ) {
                DataStore ds = (DataStore) dataStores.elementAt ( i );
                ds.storeJournal ( event );
              }
            }
            ***/
            textLines.removeAllElements (); // truncate Vector
          }
          break;

        case STATE_VEVENT:
          textLines.addElement ( line );
          if ( lineUp.startsWith ( "END:VEVENT" ) ) {
            state = STATE_VCALENDAR;
            Event event = new Event ( this, startLineNo, textLines );
            if ( event.isValid() ) {
              for ( int i = 0; i < dataStores.size(); i++ ) {
                DataStore ds = (DataStore) dataStores.elementAt ( i );
                ds.storeEvent ( event );
              }
            } else {
System.out.println ( "ERROR: Invalid VEVENT found" );
            }
            textLines.removeAllElements (); // truncate Vector
          }
          break;

        case STATE_VFREEBUSY:
          textLines.addElement ( line );
          if ( lineUp.startsWith ( "END:VFREEBUSY" ) ) {
            state = STATE_VCALENDAR;
            // Send the Freebusy object to all DataStore objects
            /*** TODO - not yet implemented
                 Uncomment this when Freebusy.java is implemented
            Freebusy fb = new Freebusy ( this, startLineNo, textLines );
            if ( fb.isValid() ) {
              for ( int i = 0; i < dataStores.size(); i++ ) {
                DataStore ds = (DataStore) dataStores.elementAt ( i );
                ds.storeFreebusy ( event );
              }
            }
            ***/
            textLines.removeAllElements (); // truncate Vector
          }
          break;

        case STATE_DONE:
          // should be nothing else after "END:VCALENDAR"
          if ( lineUp.trim().length() == 0 ) {
            // ignore blank lines at end of file
          } else if ( isParseStrict () ) {
            reportParseError ( new ParseError (
              ln, "Data found after END:VCALENDAR", line ) );
          }
          break;
      }
      if ( nextLine == null )
        done = true;
    }
    r.close ();

    // Make sure PRODID and VERSION were specified since they are
    // required
    if ( icalVersion == null && isParseStrict() ) {
      reportParseError ( new ParseError (
        ln, "No required VERSION attribute found", "n/a" ) );
    }
    if ( prodId == null && isParseStrict() ) {
      reportParseError ( new ParseError (
        ln, "No required PRODID attribute found", "n/a" ) );
    }

    // iCal data is now in line
    // TODO: split into event, todo, timezone and journal strings

    return noErrors;
  }

  public String toIcal ()
  {
    StringBuffer ret = new StringBuffer ( 1024 );
    ret.append ( "BEGIN:VCALENDAR" );
    ret.append ( CRLF );
    ret.append ( "VERSION:2.0" );
    ret.append ( CRLF );
    // Should we use the PRODID we parsed on input.  Since we are generating
    // output, I think we will use ours.
    // TODO: add version number in the following
    ret.append ( "PRODID:-//k5n.us//Java Calendar Tools//EN" );
    ret.append ( CRLF );

    // Include events
    Vector events = ((DataStore) getDataStoreAt ( 0 )).getAllEvents ();
    for ( int i = 0; i < events.size(); i++ ) {
      Event ev = (Event) events.elementAt ( i );
      ret.append ( ev.toIcal () );
    }

    ret.append ( "END:VCALENDAR" );
    ret.append ( CRLF );
    return ret.toString ();
  }



  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java us.k5n.ical.IcalParser testfile.ics
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      java.io.File f = new java.io.File ( args[i] );
      IcalParser a = new IcalParser ( PARSE_STRICT );
      java.io.BufferedReader reader = null;
      if ( f.exists () ) {
        try {
          reader = new java.io.BufferedReader ( new java.io.FileReader ( f ) );
          a.parse ( reader );
        } catch ( IOException e ) {
          System.err.println ( "Error opening " + f + ": " + e );
          System.exit ( 1 );
        }
      } else {
        System.err.println ( "Usage: java IcalParser filename.ics" );
        System.exit ( 1 );
      }
      System.out.println ( "Filename:\n  " + args[i] );
      //System.out.println ( "\nFormatted output:\n\n" + a.toIcal () );
      Vector errors = a.getAllErrors ();
      for ( int j = 0; j < errors.size (); j++ ) {
        ParseError err = (ParseError) errors.elementAt ( j );
        System.out.println ( "Error # " + (j+1) + ":\n" + err.toString ( 2 ) );
      }
    }
  }
}

