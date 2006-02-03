

package us.k5n.ical;


import java.util.Vector;


/**
  * iCal Timezone class
  * NOTE: not yet implemented!
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Timezone
{
  public String tzid = null;
  public String lastMod = null;
  public String tzOffsetFrom = null;
  public String tzOffsetTo = null;
  public String tzName = null;

  public Timezone ( IcalParser parser, int initialLine, Vector textLines )
  {
    // TODO
  }

  public void parseLine ( String icalStr, int parseMethod )
    throws ParseException, BogusDataException
  {
    // TODO

    // so we can compile :-)
    boolean x = false;
    if ( x )
      throw new ParseException ( "TODO!", "XXX" );
    if ( x )
      throw new BogusDataException ( "TODO!", "XXX" );
  }

  public boolean isValid ()
  {
    return false;
  }



}
