

package us.k5n.ical;


/**
  * iCal Parsing Exception.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class ParseException extends Exception
{
  public String error;
  public String icalText;

  public ParseException ( String error, String icalText) 
  {
    super ( error );
    this.error = error;
    this.icalText = icalText;
  }

  public String toString ()
  {
    String ret = super.toString ();
    ret += "\nInput iCal data: '" + icalText + "'\n";
    return ret;
  }

}
