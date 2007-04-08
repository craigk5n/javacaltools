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


/**
  * Base class for use with a variety of duration-related iCalendar fields.
  * <br/>From RFC 2445:<blockquote>
  * Formal Definition: The value type is defined by the following
  * notation: <ul>
  *
  * <li> dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week) </li>
  * <li> dur-date   = dur-day [dur-time] </li>
  * <li> dur-time   = "T" (dur-hour / dur-minute / dur-second) </li>
  * <li> dur-week   = 1*DIGIT "W" </li>
  * <li> dur-hour   = 1*DIGIT "H" [dur-minute] </li>
  * <li> dur-minute = 1*DIGIT "M" [dur-second] </li>
  * <li> dur-second = 1*DIGIT "S" </li>
  * <li> dur-day    = 1*DIGIT "D" </li>
  * </ul>
  *
  *   Description: If the property permits, multiple "duration" values are
  *   specified by a COMMA character (US-ASCII decimal 44) separated list
  *   of values. The format is expressed as the [ISO 8601] basic format for
  *   the duration of time. The format can represent durations in terms of
  *   weeks, days, hours, minutes, and seconds.
  *   <br/><br/>
  *   No additional content value encoding (i.e., BACKSLASH character
  *   encoding) are defined for this value type.
  *   <br/><br/>
  *   Example: A duration of 15 days, 5 hours and 20 seconds would be:
  *   <br/><br/>
  *     P15DT5H0M20S
  *   <br/><br/>
  *   A duration of 7 weeks would be:
  *   <br/><br/>
  *     P7W
  *   </blockquote>
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Duration extends Property
  implements Constants
{
  int duration = 0; // duration in seconds

  /**
    * Constructor
    * @param icalStr	One or more lines of iCalendar that specifies
    *			a duration.  Durations should follow the ISO 8601
    *			format.
    */
  public Duration ( String icalStr )
    throws ParseException, BogusDataException
  {
    this ( icalStr, PARSE_LOOSE );
  }


  /**
    * Constructor
    * @param seconds	The number of seconds for the duration
    */
  public Duration ( int seconds )
  {
    super ( "DURATION", "" );
    this.duration = seconds;
  }


  /**
    * Constructor
    * @param icalStr	One or more lines of iCalendar that specifies
    *			a duration
    * @param parseMode	PARSE_STRICT or PARSE_LOOSE
    */
  public Duration ( String icalStr, int parseMode )
    throws ParseException, BogusDataException
  {
    super ( icalStr, parseMode );

    for ( int i = 0; i < attributeList.size(); i++ ) {
      Attribute a = attributeAt ( i );
      String aval = a.value.toUpperCase();
      // TODO: not sure if any attributes are allowed here...
    }

    // Convert value into a duration
    duration = parseDuration ( value );

    if ( duration == 0 )
      throw new ParseException ( "No valid duration found", icalStr );
  }


  /**
    * Parse a duration specified in ISO 8601 format.
    * @return	The number of seconds for the duration
    */
  public static int parseDuration ( String durStr )
    throws ParseException
  {
    int numSecs = 0;
    int start = 0;
    boolean isNeg = false;
    StringBuffer sb = new StringBuffer ();

    // trim it and swith to uppercase to be safe
    durStr = durStr.toUpperCase().trim ();

    // ignore a leading '+'
    if ( durStr.charAt ( 0 ) == '+' )
      start = 1;
    else if ( durStr.charAt ( 0 ) == '-' ) {
      start = 1;
      isNeg = true;
    }

    if ( durStr.charAt ( start ) != 'P' ) {
      throw new ParseException ( "Duration '" + durStr +
        "' must start with 'P'", durStr );
    }
    // skip over 'P'
    start++;

    for ( int i = start; i < durStr.length(); i++ ) {
      char ch = durStr.charAt ( i );
      if ( ch >= '0' && ch <= '9' ) {
        // it's a digit
        sb.append ( ch );
      } else {
        // Not a digit
        int n = 0;
        if ( sb.length () > 0 )
          n = Integer.parseInt ( sb.toString () );
        switch ( ch ) {
          case 'T': // week
            // means we are switching from day duration to H/M/S duration
            if ( sb.length() != 0 )
              throw new ParseException ( "Duration has leading '" +
                sb + "' before 'T'", durStr );
            break;
          case 'W': // week
            numSecs += n * 7 * 24 * 3600;
            break;
          case 'D': // day
            numSecs += n * 24 * 3600;
            break;
          case 'H': // hour
            numSecs += n * 3600;
            break;
          case 'M': // minute
            numSecs += n * 60;
            break;
          case 'S': // second
            numSecs += n;
            break;
          default: // ???
            throw new ParseException ( "Duration has invalid char '" +
              ch + "'", durStr );
        }
        sb.setLength ( 0 ); // truncate StringBuffer
      }
    }
    return ( isNeg ? 0 - numSecs : numSecs );
  }

  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java Duration "DTSTAMP;20030701T000000Z"
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      try {
        java.io.File f = new java.io.File ( args[i] );
        Duration a = null;
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
        a = new Duration ( input, PARSE_STRICT );
        System.out.println ( "Duration input:\n  " + args[i] );
        System.out.println ( "\nDuration text:\n" + a.value );
        System.out.println ( "\nDuration output:\n  " + a.toIcal () );
        System.out.println ( "\nNumber of seconds: " + a.duration );
      } catch ( ParseException e ) {
        System.err.println ( "iCalendar Parse Exception: " + e );
      } catch ( BogusDataException e2 ) {
        System.err.println ( "iCalendar Data Exception: " + e2 );
      }
    }
  }

}
