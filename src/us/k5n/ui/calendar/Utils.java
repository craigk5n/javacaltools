/*
 * Copyright (C) 2005-2008 Craig Knudsen and other authors
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
package us.k5n.ui.calendar;

import java.util.Vector;

public class Utils {

	public static Vector wrapLines ( String inStr, int maxLength ) {
		Vector ret = new Vector ();
		String[] lines = inStr.split ( "[\r\n]+" );
		for ( int i = 0; i < lines.length; i++ ) {
			Vector lines2 = wrapLine ( lines[i], maxLength );
			ret.addAll ( lines2 );
		}
		return ret;
	}

	public static Vector wrapLine ( String inStr, int maxLength ) {
		Vector ret = new Vector ();
		if ( inStr.length () <= maxLength ) {
			ret.addElement ( inStr );
			return ret;
		}
		String[] words = inStr.split ( "[ \t]+" );
		StringBuffer cur = new StringBuffer ();
		for ( int i = 0; i < words.length; i++ ) {
			if ( cur.length () + 1 + words[i].length () >= maxLength ) {
				if ( cur.length () > 0 )
					ret.addElement ( cur.toString () );
				cur.setLength ( 0 );
				cur.append ( words[i] );
			} else {
				if ( cur.length () > 0 )
					cur.append ( ' ' );
				cur.append ( words[i] );
			}
		}
		if ( cur.length () > 0 )
			ret.addElement ( cur.toString () );
		return ret;
	}

}
