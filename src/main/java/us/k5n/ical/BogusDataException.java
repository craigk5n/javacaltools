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
 * iCalendar invalid data exception
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class BogusDataException extends Exception {
  private static final long serialVersionUID = 1L;
  public String error;
  public String icalText;

  public BogusDataException(String error, String icalText) {
    super(error);
    this.error = error;
    this.icalText = icalText;
  }

  public String toString() {
    String ret = super.toString();
    ret += "\nInput iCalendar data: '" + icalText + "'\n";
    return ret;
  }

}
