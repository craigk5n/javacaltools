
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
  * iCalendar Attribute class.  Can be used to store an attribute such as
  * LANGUAGE as in: <br/>
  * <tt>LOCATION;LANGUAGE=en:Germany</tt>
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
