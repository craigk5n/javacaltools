

package us.k5n.ical;



/**
  * iCal Attribute class.  Can be used to store an attribute such as
  * LANGUAGE as in: <br/>
  * <tt>LOCATION;LANGUAGE=en:Germany</tt>
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
public class Attribute
{
  /** Attribute name (always uppercase) */
  public String name;
  /** Attribute value */
  public String value;

  public Attribute ( String name, String value )
  {
    this.name = name.toUpperCase();
    this.value = value.trim ();
  }
}
