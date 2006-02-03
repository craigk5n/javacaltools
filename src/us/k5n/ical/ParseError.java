

package us.k5n.ical;


/**
  * A single parse error.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class ParseError
{
  /** Line number (if available) */
  public int lineNo;
  /** Error message */
  public String error;
  /** Offending iCal data */
  public String icalStr;

  /**
    * Create a parse error
    * @param lineNo	Line number of error (if available)
    * @param error	Error message
    * @param icalStr	Offending iCal data
    */
  public ParseError ( int lineNo, String error, String icalStr )
  {
    this.lineNo = lineNo;
    this.error = error;
    this.icalStr = icalStr;
  }

  /**
    * Convert the error to a String.
    */
  public String toString ()
  {
    return toString ( 0 );
  }


  /**
    * Convert the error to a String.
    * @param indent	The number of spaces to indent each line
    */
  public String toString ( int indent )
  {
    StringBuffer ret = new StringBuffer ();
    String ind = "";
    for ( int i = 0; i < indent; i++ )
      ind += " ";

    ret.append ( ind );
    ret.append ("Error  : " );
    ret.append ( error );
    ret.append ( "\n" );

    ret.append ( ind );
    ret.append ( "Line No: " );
    ret.append ( lineNo );
    ret.append ( "\n" );

    ret.append ( ind );
    ret.append ( "Input  : " );
    ret.append ( icalStr );
    ret.append ( "\n" );

    return ret.toString ();
  
  }


}
