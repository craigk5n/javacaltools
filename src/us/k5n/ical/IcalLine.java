

package us.k5n.ical;


import java.util.Vector;


/**
  * This class represents one or more text lines of iCal data that
  * have been unfolded into a single line.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
class IcalLine
{
  /** The unfolded single line of text */
  public String value;
  /** The starting line number in the originial iCal data for this text */
  public int startingLineNo;


  /**
    * Constructor
    * @param value	The already unfolded line of text
    * @param startingLineNo	The starting line number of the first
    *				line of text in the originial iCal data
    */
  public IcalLine ( String value, int startingLineNo )
  {
    this.value = value;
    this.startingLineNo = startingLineNo;
  }

}
