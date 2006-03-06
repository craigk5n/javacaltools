/*
 * Copyright (C) 2005-2006 Craig Knudsen and other authors
 * (see AUTHORS for a complete list)
 *
 * JavaCalTools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * A copy of the GNU Lesser General Public License is included in the Wine
 * distribution in the file COPYING.LIB. If you did not receive this copy,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
*/

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
