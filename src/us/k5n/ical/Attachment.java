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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

/**
 * iCalendar Attachment class - This object represents the iCalendar ATTACH
 * object <br/> From the RFC: <br/> <quote>
 * Purpose: The property provides the capability to associate a document
 * object with a calendar component.
 * <br/>
 * Value Type: The default value type for this property is URI. The
 * value type can also be set to BINARY to indicate inline binary
 * encoded content information.
 * <br/>
 * Property Parameters: Non-standard, inline encoding, format type and
 * value data type property parameters can be specified on this
 * property.
 * <br/>
 * Conformance: The property can be specified in a "VEVENT", "VTODO",
 * "VJOURNAL" or "VALARM" calendar components.
 * <br/>
 * Description: The property can be specified within "VEVENT", "VTODO",
 * "VJOURNAL", or "VALARM" calendar components. This property can be
 * specified multiple times within an iCalendar object.
 * </quote>
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class Attachment extends Property {
	/**
	 * Constructor
	 */
	public Attachment() {
		super ( "ATTACH", "" );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specifies an event/todo URL
	 */
	public Attachment(String icalStr) throws ParseException {
		this ( icalStr, PARSE_LOOSE );
	}

	/**
	 * Constructor
	 * 
	 * @param icalStr
	 *          One or more lines of iCalendar that specify an attachment.
	 * @param parseMode
	 *          PARSE_STRICT or PARSE_LOOSE
	 */
	public Attachment(String icalStr, int parseMode) throws ParseException {
		super ( icalStr, parseMode );
	}

	/**
	 * Create an attachment from the specified file.
	 * 
	 * @param filename
	 * @throws ParseException
	 * @throws IOException
	 */
	public Attachment(File filename) throws ParseException, IOException {
		super ( "ATTACH", "" );
		this.addAttribute ( "ENCODING", "BASE64" );
		this.addAttribute ( "VALUE", "BINARY" );
		long size = filename.length ();
		byte[] bytes = new byte[(int)size];
		FileInputStream is = new FileInputStream ( filename );
		is.read ( bytes );
		byte[] encoded = Base64.encodeBase64 ( bytes );
		this.value = new String ( encoded );
	}

	/**
	 * Create an attachment from the specified url. This will not embed the
	 * attachment into the event/journal. It will specify the URL. So, the "file:"
	 * URL should only be used if the event/journal will only be used on the same
	 * machine.
	 * 
	 * @param url
	 *          The URL of the attachment. (No attempt is made to validate that
	 *          the content is at the specified URL.)
	 * @throws ParseException
	 * @throws IOException
	 */
	public Attachment(URL url) throws ParseException  {
		super ( "ATTACH", "" );
		this.setValue ( url.toString () );
	}
	
	/**
	 * Export to a properly folded iCalendar line.
	 */
	public String toICalendar () {
		StringBuffer ret = new StringBuffer ( 40 );
		ret.append ( name );
		if ( attributeList.size () > 0 ) {
			for ( int i = 0; i < attributeList.size (); i++ ) {
				ret.append ( ';' );
				Attribute a = (Attribute) attributeList.elementAt ( i );
				ret.append ( a.name );
				// Always quote the value just to be safe
				ret.append ( "=\"" );
				ret.append ( a.value );
				ret.append ( '"' );
			}
		}
		ret.append ( ':' );
		ret.append ( value );

		return ( StringUtils.foldLine ( ret.toString () ) );
	}
	
}
