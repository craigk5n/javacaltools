

package us.k5n.ical;


import java.util.Calendar;


/**
  * iCal Categories class -
  * This object represents a category list and
  * corresponds to the CATEGORIES iCal property.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Categories extends Property
{
  /** Language specification */
  public String language = null;
  // TODO provide an API to parse through the comma-separated list
  // of category name

  /**
    * Constructor
    */
  public Categories ()
  {
    super ( "CATEGORIES", "" );
  }

  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			an event/todo description
    */
  public Categories ( String icalStr )
    throws ParseException
  {
    this ( icalStr, PARSE_LOOSE );
  }


  /**
    * Constructor
    * @param icalStr	One or more lines of iCal that specifies
    *			a category list (comma separated)
    * @param parseMode	PARSE_STRICT or PARSE_LOOSE
    */
  public Categories ( String icalStr, int parseMode )
    throws ParseException
  {
    super ( icalStr, parseMode );

    for ( int i = 0; i < attributeList.size(); i++ ) {
      Attribute a = attributeAt ( i );
      String aval = a.value.toUpperCase();
      if ( a.name.equals ( "LANGUAGE" ) ) {
        // Can only have one of these
        if ( language != null && parseMode == PARSE_STRICT ) {
          throw new ParseException ( "More than one LANGUAGE found", icalStr );
        }
        language = a.value;
      } else {
        // Only generate exception if strict parsing
        if ( parseMode == PARSE_STRICT ) {
          throw new ParseException ( "Invalid CATEGORIES attribute '" +
            a.name + "'", icalStr );
        }
      }
    }
  }

  // Test routine - will parse input string and then export back
  // into ical format.
  // Usage: java Categories "DESCRIPTION;LANGUAGE=EN:This is\\na test."
  //   
  public static void main ( String args[] )
  {
    for ( int i = 0; i < args.length; i++ ) {
      try {
        java.io.File f = new java.io.File ( args[i] );
        Categories a = null;
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
        a = new Categories ( input, PARSE_STRICT );
        System.out.println ( "Categories input:\n  " + args[i] );
        System.out.println ( "\nCategories text:\n" + a.value );
        System.out.println ( "\nCategories output:\n  " + a.toIcal () );
      } catch ( ParseException e ) {
        System.err.println ( "iCal Parse Exception: " + e );
      }
    }
  }

}
