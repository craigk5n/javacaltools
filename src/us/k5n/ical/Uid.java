

package us.k5n.ical;


import java.util.Calendar;


/**
  * iCal Uid class -
  * This object represents a uid and
  * corresponds to the UID iCal property.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Uid extends Property
{
  /**
    * Constructor
    */
  public Uid ()
  {
    super ( "UID", "" );
  }

  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			an event/todo uid
    */
  public Uid ( String icalStr )
    throws ParseException
  {
    this ( icalStr, PARSE_LOOSE );
  }


  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			the unique identifier
    * @param parseMode	PARSE_STRICT or PARSE_LOOSE
    */
  public Uid ( String icalStr, int parseMode )
    throws ParseException
  {
    super ( icalStr, parseMode );

    // UID cannot have any attributes
    for ( int i = 0; i < attributeList.size(); i++ ) {
      Attribute a = attributeAt ( i );
      // Only generate exception if strict parsing
      if ( parseMode == PARSE_STRICT ) {
        throw new ParseException ( "Invalid UID attribute '" +
          a.name + "'", icalStr );
      }
    }
  }

  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java Uid "UID;SFSDFSDFSDF-SDFDFSDFSD@xxx.com"
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      try {
        java.io.File f = new java.io.File ( args[i] );
        Uid a = null;
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
        a = new Uid ( input, PARSE_STRICT );
        System.out.println ( "Uid input:\n  " + args[i] );
        System.out.println ( "\nUid text:\n" + a.value );
        System.out.println ( "\nUid output:\n  " + a.toIcal () );
      } catch ( ParseException e ) {
        System.err.println ( "iCal Parse Exception: " + e );
      }
    }
  }

}
