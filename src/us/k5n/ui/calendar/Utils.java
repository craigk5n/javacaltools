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
