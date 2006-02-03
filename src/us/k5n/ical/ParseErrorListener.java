

package us.k5n.ical;


/**
  * Defines an interface for receive parse errors.
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
interface ParseErrorListener
{

  /**
    * This method will be called when a iCal parse error is encountered.
    */
  public void reportParseError ( ParseError error );

}

