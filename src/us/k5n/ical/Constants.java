

package us.k5n.ical;


/**
  * iCal constants
  * @version $Id$
  * @author Craig Knudsen, craig@k5n.us
  */
interface Constants
{
  /** Parse iCal data strictly.  ParseException errors will be generated
    * for all errors.  This would typically be used to validate an iCal
    * data file rather than by an application intersted in loading iCal data. */
  static public final int PARSE_STRICT = 1;
  /** Don't parse iCal data strictly.  (This is the default in most cases.)
    * Attempt to parse and derive as much from the iCal data as possible.
    */
  static public final int PARSE_LOOSE = 2;

  /** Line termination string. */
  static public final String CRLF = "\r\n";

  /** Carriage return character */
  static public final int CR = 13;
  /** Line feed character */
  static public final int LF = 10;
  /** Tab character */
  static public final int TAB = 9;
  /** Space character */
  static public final int SPACE = 32;

  /** Maximum line length acceptable in iCal (excluding CRLF) */
  static public final int MAX_LINE_LENGTH = 75;

  /** iCal major version */
  static public final int ICAL_VERSION_MAJOR = 2;
  /** iCal minor version */
  static public final int ICAL_VERSION_MINOR = 0;

  /* iCal version (in N.N String format) */
  static public final String ICAL_VERSION =
    ICAL_VERSION_MAJOR + "." + ICAL_VERSION_MINOR;
}
