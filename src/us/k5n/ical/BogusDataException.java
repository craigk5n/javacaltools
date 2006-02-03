

package us.k5n.ical;


/**
  * iCal invalid data exception
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class BogusDataException extends Exception
{
  public String error;
  public String icalText;

  public BogusDataException ( String error, String icalText) 
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
