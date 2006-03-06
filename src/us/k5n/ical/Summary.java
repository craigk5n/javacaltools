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


import java.util.Calendar;


/**
  * iCal Summary class -
  * This object represents a summary and
  * corresponds to the SUMMARY iCal property.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Summary extends Property
{
  /** Alternate representation URI */
  public String altrep = null;
  /** Language specification */
  public String language = null;

  /**
    * Constructor
    */
  public Summary ()
  {
    super ( "SUMMARY", "" );
  }

  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			an event/todo summary
    */
  public Summary ( String icalStr )
    throws ParseException
  {
    this ( icalStr, PARSE_LOOSE );
  }


  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			an event/todo summary
    * @param parseMode	PARSE_STRICT or PARSE_LOOSE
    */
  public Summary ( String icalStr, int parseMode )
    throws ParseException
  {
    super ( icalStr, parseMode );

    for ( int i = 0; i < attributeList.size(); i++ ) {
      Attribute a = attributeAt ( i );
      String aval = a.value.toUpperCase();
      if ( a.name.equals ( "ALTREP" ) ) {
        // Can only have one of these
        if ( altrep != null && parseMode == PARSE_STRICT ) {
          throw new ParseException ( "More than one ALTREP found", icalStr );
        }
        altrep = a.value;
      } else if ( a.name.equals ( "LANGUAGE" ) ) {
        // Can only have one of these
        if ( language != null && parseMode == PARSE_STRICT ) {
          throw new ParseException ( "More than one LANGUAGE found", icalStr );
        }
        language = a.value;
      } else {
        // Only generate exception if strict parsing
        if ( parseMode == PARSE_STRICT ) {
          throw new ParseException ( "Invalid SUMMARY attribute '" +
            a.name + "'", icalStr );
        }
      }
    }
  }

  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java Summary "SUMMARY;LANGUAGE=EN:This is\\na test."
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      try {
        java.io.File f = new java.io.File ( args[i] );
        Summary a = null;
        String input = null;
        if ( f.exists () ) {
          try {
            input = Utils.getFileContents ( f );
          } catch ( Exception e ) {
            System.err.println ( "Error opening " + f + ": " + e );
            System.exit ( 1 );
          }
        } else {
          input = args[i];
        }
        a = new Summary ( input, PARSE_STRICT );
        System.out.println ( "Summary input:\n  " + args[i] );
        System.out.println ( "\nSummary text:\n" + a.value );
        System.out.println ( "\nSummary output:\n  " + a.toIcal () );
      } catch ( ParseException e ) {
        System.err.println ( "iCal Parse Exception: " + e );
      }
    }
  }

}
