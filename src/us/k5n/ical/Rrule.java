

package us.k5n.ical;


import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



class RruleByday
{
  public boolean positive;
  public int weekday; // (0=Sun, etc.)
  public int number;
  public boolean valid = false;

  public RruleByday ( String str )
  {
    int i = 0;
    weekday = -1;
    if ( str.charAt ( i ) == '+' ) {
      positive = true;
      i++;
    } else if ( str.charAt ( i ) == '-' ) {
      positive = false;
      i++;
    }
    if ( str.charAt ( i ) >= '0' && str.charAt ( i ) <= '9' ) {
      number = (int) ( str.charAt ( i ) - '0' );
      i++;
    }
    String sub = str.substring ( i, i + 2 );
    if ( sub.equals ( "SU" ) )
      weekday = 0;
    else if ( sub.equals ( "MO" ) )
      weekday = 1;
    else if ( sub.equals ( "TU" ) )
      weekday = 2;
    else if ( sub.equals ( "WE" ) )
      weekday = 3;
    else if ( sub.equals ( "TH" ) )
      weekday = 4;
    else if ( sub.equals ( "FR" ) )
      weekday = 5;
    else if ( sub.equals ( "SA" ) )
      weekday = 6;
    if ( weekday >= 0 && str.length() == i + 2 )
      valid = true;
  }

  public String toIcal ()
  {
    StringBuffer ret = new StringBuffer ();
    if ( ! positive )
      ret.append ( '-' );
    ret.append ( number );
    switch ( weekday ) {
      case 0: ret.append ( "SU" ); break;
      case 1: ret.append ( "MO" ); break;
      case 2: ret.append ( "TU" ); break;
      case 3: ret.append ( "WE" ); break;
      case 4: ret.append ( "TH" ); break;
      case 5: ret.append ( "FR" ); break;
      case 6: ret.append ( "SA" ); break;
    }
    return ret.toString ();
  }
}


/**
  * Class for holding recurrence information for an event/todo as specified
  * in the iCal RRULE property.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Rrule extends Property
  implements Constants
{
  /** Repeat frequency (required) */
  public int freq;
  /** Interval between recurrences (default is 1) */
  public int interval;
  /** Last day of recurrence (optional) */
  public Date untilDate;
  /** Count specifies the max number of recurrences */
  public int count;
  /** Second event falls on */
  public int []bysecond = null;
  /** Minute event falls on */
  public int []byminute = null;
  /** Hour event falls on */
  public int []byhour = null;
  /** Day event falls on */
  public RruleByday []byday = null;
  /** Day of month event falls on (1,2,-1,2, etc.) */
  public int []bymonthday = null;
  /** Day of year event falls on (1,2,-1,2, etc.) */
  public int []byyearday = null;
  /** Month event falls on (1,2 etc.) */
  public int []bymonth = null;

  /** Days where event should not occur (Vector of Date) */
  public Vector exceptions;
  /** Days where event should occur (Vector of Date) */
  public Vector inclusions;

  public static final int FREQ_NOT_SPECIFIED = -1;
  public static final int FREQ_YEARLY = 1;
  public static final int FREQ_MONTHLY = 2;
  public static final int FREQ_WEEKLY = 3;
  public static final int FREQ_DAILY = 4;
  public static final int FREQ_HOURLY = 5;
  public static final int FREQ_MINUTELY = 6;
  public static final int FREQ_SECONDLY = 7;

  // TODO: constructor
  /**
    * Construct based on iCal RRULE text
    * @param icalStr	The line(s) of iCal text (should be unfolded already)
    * @param parseMode	PARSE_STRICT or PARSE_LOOSE
    */
  public Rrule ( String icalStr, int parseMode )
    throws ParseException, BogusDataException
  {
    super ( icalStr, parseMode );

    // Set defaults
    freq = FREQ_NOT_SPECIFIED;
    interval = 1; // default
    untilDate = null;
    count = -1; // not in use

    // No attributes allowed on RRULE
    for ( int i = 0; i < attributeList.size(); i++ ) {
      Attribute a = attributeAt ( i );
      if ( parseMode == PARSE_STRICT ) {
        throw new ParseException ( "No attribute '" + a.name +
          "' allowed in RRULE", icalStr );
      }
    }

    String []args = value.split ( ";" );
    for ( int i = 0; i < args.length; i++ ) {
      String []param = args[i].split ( "=", 2 );
      if ( param.length != 2 )
        throw new ParseException ( "Invalid RRULE '" + args[i] + "'",
          icalStr );
      String aname = param[0].toUpperCase();
      String aval = param[1].toUpperCase();
      if ( aname.equals ( "FREQ" ) ) {
        // only one allowed
        if ( freq != FREQ_NOT_SPECIFIED )
          throw new BogusDataException (
            "More than one RRULE FREQ is not allowed", icalStr );
        if ( aval.equals ( "YEARLY" ) ) {
          freq = FREQ_YEARLY;
        } else if ( aval.equals ( "MONTHLY" ) ) {
          freq = FREQ_MONTHLY;
        } else if ( aval.equals ( "DAILY" ) ) {
          freq = FREQ_DAILY;
        } else if ( aval.equals ( "HOURLY" ) ) {
          freq = FREQ_HOURLY;
        } else if ( aval.equals ( "MINUTELY" ) ) {
          freq = FREQ_MINUTELY;
        } else if ( aval.equals ( "SECONDLY" ) ) {
          freq = FREQ_SECONDLY;
        } else {
          throw new BogusDataException ( "Invalid RRULE FREQ '" +
            aval + "'", icalStr );
        }
      } else if ( aname.equals ( "INTERVAL" ) ) {
        try {
          interval = Integer.parseInt ( aval );
        } catch ( NumberFormatException nef ) {
          throw new BogusDataException ( "Invalid RRULE INTERVAL '" +
            aval + "'", icalStr );
        }
      } else if ( aname.equals ( "UNTIL" ) ) {
        try {
          untilDate = new Date ( "XXX:" + aval );
        } catch ( BogusDataException bde ) {
          throw new BogusDataException ( "Invalid RRULE UNTIL date: " +
            bde.error, icalStr );
        } catch ( ParseException pe ) {
          throw new BogusDataException ( "Invalid RRULE UNTIL date: " +
            pe.error, icalStr );
        }
      } else if ( aname.equals ( "COUNT" ) ) {
        try {
          count = Integer.parseInt ( aval );
        } catch ( NumberFormatException nef ) {
          throw new BogusDataException ( "Invalid RRULE COUNT '" +
            aval + "'", icalStr );
        }
      } else if ( aname.equals ( "WKST" ) ) {
        // TODO
      } else if ( aname.equals ( "BYYEARNO" ) ) {
        // TODO
      } else if ( aname.equals ( "BYWEEKNO" ) ) {
        // TODO
      } else if ( aname.equals ( "BYSETPOS" ) ) {
        // TODO
      } else if ( aname.equals ( "BYMONTH" ) ) {
        String []s = aval.split ( "," );
        bymonth = new int[s.length];
        for ( int j = 0; j < s.length; j++ ) {
          if ( StringUtils.isNumber ( s[j], true ) )
            bymonth[j] = Integer.parseInt ( s[j] );
          else {
            throw new BogusDataException ( "Invalid RRULE BYMONTH '" +
              s[j] + "'", icalStr );
          }
        }
      } else if ( aname.equals ( "BYYEARDAY" ) ) {
        String []s = aval.split ( "," );
        byyearday = new int[s.length];
        for ( int j = 0; j < s.length; j++ ) {
          if ( StringUtils.isNumber ( s[j], true ) ) {
            byyearday[j] = Integer.parseInt ( s[j] );
            if ( byyearday[j] < -366 || byyearday[j] > 366 ) {
              throw new BogusDataException ( "Invalid RRULE BYYEARDAY '" +
                s[j] + "'", icalStr );
            }
          } else {
            throw new BogusDataException ( "Invalid RRULE BYYEARDAY '" +
              s[j] + "'", icalStr );
          }
        }
      } else if ( aname.equals ( "BYMONTHDAY" ) ) {
        String []s = aval.split ( "," );
        bymonthday = new int[s.length];
        for ( int j = 0; j < s.length; j++ ) {
          if ( StringUtils.isNumber ( s[j], true ) ) {
            bymonthday[j] = Integer.parseInt ( s[j] );
            if ( bymonthday[j] < -31 || bymonthday[j] > 31 ) {
              throw new BogusDataException ( "Invalid RRULE BYMONTHDAY '" +
                s[j] + "'", icalStr );
            }
          } else {
            throw new BogusDataException ( "Invalid RRULE BYMONTHDAY '" +
              s[j] + "'", icalStr );
          }
        }
      } else if ( aname.equals ( "BYDAY" ) ) {
        String []bydaystr = aval.split ( "," );
        byday = new RruleByday[bydaystr.length];
        for ( int j = 0; j < bydaystr.length; j++ ) {
          byday[j] = new RruleByday ( bydaystr[j] );
          if ( ! byday[j].valid ) {
            throw new BogusDataException ( "Invalid RRULE BYDAY '" +
              bydaystr[j] + "'", icalStr );
          }
        }
      } else if ( aname.equals ( "BYHOUR" ) ) {
        String []s = aval.split ( "," );
        byhour = new int[s.length];
        // validate
        for ( int j = 0; j < s.length; j++ ) {
          if ( ! StringUtils.isNumber ( s[j] ) ||
            Integer.parseInt ( s[j] ) > 23 ) {
            throw new BogusDataException ( "Invalid RRULE BYHOUR '" +
              s[j] + "'", icalStr );
          } else {
            byhour[j] = Integer.parseInt ( s[j] );
          }
        }
      } else if ( aname.equals ( "BYMINUTE" ) ) {
        String []s = aval.split ( "," );
        byminute = new int[s.length];
        // validate
        for ( int j = 0; j < s.length; j++ ) {
          if ( ! StringUtils.isNumber ( s[j] ) ||
            Integer.parseInt ( s[j] ) > 59 ) {
            throw new BogusDataException ( "Invalid RRULE BYMINUTE '" +
              s[j] + "'", icalStr );
          } else {
            byminute[j] = Integer.parseInt ( s[j] );
          }
        }
      } else if ( aname.equals ( "BYSECOND" ) ) {
        String []s = aval.split ( "," );
        bysecond = new int[s.length];
        // validate
        for ( int j = 0; j < s.length; j++ ) {
          if ( ! StringUtils.isNumber ( s[j] ) ||
            Integer.parseInt ( s[j] ) > 59 ) {
            throw new BogusDataException ( "Invalid RRULE BYSECOND '" +
              s[j] + "'", icalStr );
          } else {
            bysecond[j] = Integer.parseInt ( s[j] );
          }
        }
      } else if ( parseMode == PARSE_STRICT ) {
        // Only generate exception if strict parsing
        throw new ParseException ( "Invalid RRULE attribute '" +
          aname + "'", icalStr );
      }
    }

    // freq must be defined
    if ( freq == FREQ_NOT_SPECIFIED ) {
      throw new BogusDataException ( "No FREQ attribute found in RRULE",
        icalStr );
    }
  }


  /**
    * Convert to a RRULE iCal line
    */
  public String toIcal ()
  {
    StringBuffer ret = new StringBuffer ();
    ret.append ( "RRULE:" );
    // regenerate value in case anything was updated and so we can validate
    // parse was correct
    switch ( freq ) {
      case FREQ_YEARLY: ret.append ( "FREQ=YEARLY" ); break;
      case FREQ_MONTHLY: ret.append ( "FREQ=MONTHLY" ); break;
      case FREQ_DAILY: ret.append ( "FREQ=DAILY" ); break;
      case FREQ_HOURLY: ret.append ( "FREQ=HOURLY" ); break;
      case FREQ_MINUTELY: ret.append ( "FREQ=MINUTELY" ); break;
      case FREQ_SECONDLY: ret.append ( "FREQ=SECONDLY" ); break;
      default: ret.append ( "FREQ=UNKNOWN" ); break; //error
    }

    if ( count > 0 ) {
      ret.append ( ";COUNT=" );
      ret.append ( count );
    }
    if ( interval > 1 ) {
      ret.append ( ";INTERVAL=" );
      ret.append ( interval );
    }
    if ( untilDate != null ) {
      ret.append ( ";UNTIL=" );
      ret.append ( untilDate.value );
    }
    if ( bysecond != null ) {
      ret.append ( ";BYSECOND=" );
      for ( int i = 0; i < bysecond.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( bysecond[i] );
      }
    }
    if ( byminute != null ) {
      ret.append ( ";BYMINUTE=" );
      for ( int i = 0; i < byminute.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( byminute[i] );
      }
    }
    if ( bymonthday != null ) {
      ret.append ( ";BYMONTHDAY=" );
      for ( int i = 0; i < bymonthday.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( bymonthday[i] );
      }
    }
    if ( byyearday != null ) {
      ret.append ( ";BYYEARDAY=" );
      for ( int i = 0; i < byyearday.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( byyearday[i] );
      }
    }
    if ( byday != null ) {
      ret.append ( ";BYDAY=" );
      for ( int i = 0; i < byday.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( byday[i].toIcal() );
      }
    }
    if ( bymonth != null ) {
      ret.append ( ";BYMONTH=" );
      for ( int i = 0; i < bymonth.length; i++ ) {
        if ( i > 0 ) ret.append ( ',' );
        ret.append ( bymonth[i] );
      }
    }

    value = ret.toString ();

    return super.toIcal();
  }



  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java Rrule "RRULE:FREQ=MONTHLY;BYMONTH=10;BYDAY=2MO"
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      try {
        java.io.File f = new java.io.File ( args[i] );
        Rrule a = null;
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
        a = new Rrule ( input, PARSE_STRICT );
        System.out.println ( "input:\n  " + args[i] );
        System.out.println ( "\ntext:\n" + a.value );
        System.out.println ( "\noutput:\n  " + a.toIcal () );
      } catch ( ParseException e ) {
        System.err.println ( "iCal Parse Exception: " + e );
      } catch ( BogusDataException e2 ) {
        System.err.println ( "iCal Data Exception: " + e2 );
      }
    }
  }
}
