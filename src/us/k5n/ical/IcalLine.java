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
 * This class represents one or more text lines of iCal data that have been
 * unfolded into a single line.
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
class IcalLine {
  /** The unfolded single line of text */
  public String value;
  /** The starting line number in the originial iCal data for this text */
  public int startingLineNo;

  /**
   * Constructor
   * 
   * @param value
   *          The already unfolded line of text
   * @param startingLineNo
   *          The starting line number of the first line of text in the
   *          originial iCal data
   */
  public IcalLine ( String value, int startingLineNo ) {
    this.value = value;
    this.startingLineNo = startingLineNo;
  }

}
