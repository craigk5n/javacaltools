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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

/**
 * iCalendar Utility class
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class Utils implements Constants {

	static final String[] mimeArray = { ".3dm", "x-world/x-3dmf", ".3dmf",
	    "x-world/x-3dmf", ".a", "application/octet-stream", ".aab",
	    "application/x-authorware-bin", ".aam", "application/x-authorware-map",
	    ".aas", "application/x-authorware-seg", ".abc", "text/vnd.abc", ".acgi",
	    "text/html", ".afl", "video/animaflex", ".ai", "application/postscript",
	    ".aif", "audio/aiff", ".aifc", "audio/aiff", ".aiff", "audio/aiff",
	    ".aim", "application/x-aim", ".aip", "text/x-audiosoft-intra", ".ani",
	    "application/x-navi-animation", ".aos",
	    "application/x-nokia-9000-communicator-add-on-software", ".aps",
	    "application/mime", ".arc", "application/octet-stream", ".arj",
	    "application/arj", ".art", "image/x-jg", ".asf", "video/x-ms-asf",
	    ".asm", "text/x-asm", ".asp", "text/asp", ".asx", "video/x-ms-asf",
	    ".au", "audio/basic", ".avi", "video/avi", ".bcpio",
	    "application/x-bcpio", ".bin", "application/octet-stream", ".bm",
	    "image/bmp", ".bmp", "image/bmp", ".boo", "application/book", ".book",
	    "application/book", ".boz", "application/x-bzip2", ".bsh",
	    "application/x-bsh", ".bz", "application/x-bzip", ".bz2",
	    "application/x-bzip2", ".c", "text/plain", ".c++", "text/plain", ".cat",
	    "application/vnd.ms-pki.seccat", ".cc", "text/plain", ".ccad",
	    "application/clariscad", ".cco", "application/x-cocoa", ".cdf",
	    "application/cdf", ".cer", "application/pkix-cert", ".cha",
	    "application/x-chat", ".chat", "application/x-chat", ".class",
	    "application/java", ".com", "application/octet-stream", ".conf",
	    "text/plain", ".cpio", "application/x-cpio", ".cpp", "text/x-c", ".cpt",
	    "application/mac-compactpro", ".crl", "application/pkcs-crl", ".crt",
	    "application/pkix-cert", ".csh", "application/x-csh", ".css", "text/css",
	    ".cxx", "text/plain", ".dcr", "application/x-director", ".deepv",
	    "application/x-deepv", ".def", "text/plain", ".der",
	    "application/x-x509-ca-cert", ".dif", "video/x-dv", ".dir",
	    "application/x-director", ".dl", "video/dl", ".doc",
	    "application/msword", ".dot", "application/msword", ".dp",
	    "application/commonground", ".drw", "application/drafting", ".dump",
	    "application/octet-stream", ".dv", "video/x-dv", ".dvi",
	    "application/x-dvi", ".dwf", "model/vnd.dwf", ".dwg", "application/acad",
	    ".dxf", "application/dxf", ".dxr", "application/x-director", ".el",
	    "text/x-script.elisp", ".elc", "application/x-elc", ".env",
	    "application/x-envoy", ".eps", "application/postscript", ".es",
	    "application/x-esrehber", ".etx", "text/x-setext", ".evy",
	    "application/envoy", ".exe", "application/octet-stream", ".f",
	    "text/x-fortran", ".f77", "text/x-fortran", ".f90", "text/x-fortran",
	    ".fdf", "application/vnd.fdf", ".fif", "application/fractals", ".fli",
	    "video/fli", ".flo", "image/florian", ".flx", "text/vnd.fmi.flexstor",
	    ".fmf", "video/x-atomic3d-feature", ".for", "text/x-fortran", ".fpx",
	    "image/vnd.fpx", ".frl", "application/freeloader", ".funk", "audio/make",
	    ".g", "text/plain", ".g3", "image/g3fax", ".gif", "image/gif", ".gl",
	    "video/gl", ".gsd", "audio/x-gsm", ".gsm", "audio/x-gsm", ".gsp",
	    "application/x-gsp", ".gss", "application/x-gss", ".gtar",
	    "application/x-gtar", ".gz", "application/x-gzip", ".gzip",
	    "application/x-gzip", ".h", "text/x-h", ".hdf", "application/x-hdf",
	    ".help", "application/x-helpfile", ".hgl", "application/vnd.hp-hpgl",
	    ".hh", "text/x-h", ".hlb", "text/x-script", ".hlp", "application/hlp",
	    ".hpg", "application/vnd.hp-hpgl", ".hpgl", "application/vnd.hp-hpgl",
	    ".hqx", "application/binhex", ".hta", "application/hta", ".htc",
	    "text/x-component", ".htm", "text/html", ".html", "text/html", ".htmls",
	    "text/html", ".htt", "text/webviewhtml", ".htx", "text/html", ".ice",
	    "x-conference/x-cooltalk", ".ico", "image/x-icon", ".idc", "text/plain",
	    ".ief", "image/ief", ".iefs", "image/ief", ".iges", "application/iges",
	    ".igs", "application/iges", ".ima", "application/x-ima", ".imap",
	    "application/x-httpd-imap", ".inf", "application/inf", ".ins",
	    "application/x-internett-signup", ".ip", "application/x-ip2", ".isu",
	    "video/x-isvideo", ".it", "audio/it", ".iv", "application/x-inventor",
	    ".ivr", "i-world/i-vrml", ".ivy", "application/x-livescreen", ".jam",
	    "audio/x-jam", ".jav", "text/x-java-source", ".java",
	    "text/x-java-source", ".jcm", "application/x-java-commerce", ".jfif",
	    "image/jpeg", ".jfif-tbnl", "image/jpeg", ".jpe", "image/jpeg", ".jpeg",
	    "image/jpeg", ".jpg", "image/jpeg", ".jps", "image/x-jps", ".js",
	    "application/x-javascript", ".jut", "image/jutvision", ".kar",
	    "audio/midi", ".ksh", "application/x-ksh", ".la", "audio/nspaudio",
	    ".lam", "audio/x-liveaudio", ".latex", "application/x-latex", ".lha",
	    "application/lha", ".list", "text/plain", ".lma", "audio/nspaudio",
	    ".log", "text/plain", ".lsp", "application/x-lisp", ".lst", "text/plain",
	    ".lsx", "text/x-la-asf", ".ltx", "application/x-latex", ".lzx",
	    "application/lzx", ".m", "text/x-m", ".m1v", "video/mpeg", ".m2a",
	    "audio/mpeg", ".m2v", "video/mpeg", ".m3u", "audio/x-mpequrl", ".man",
	    "application/x-troff-man", ".map", "application/x-navimap", ".mar",
	    "text/plain", ".mbd", "application/mbedlet", ".mc$",
	    "application/x-magic-cap-package-1.0", ".mcd", "application/mcad",
	    ".mcf", "text/mcf", ".mcp", "application/netmc", ".me",
	    "application/x-troff-me", ".mht", "message/rfc822", ".mhtml",
	    "message/rfc822", ".mid", "audio/midi", ".midi", "audio/midi", ".mif",
	    "application/x-mif", ".mime", "message/rfc822", ".mjf",
	    "audio/x-vnd.audioexplosion.mjuicemediafile", ".mjpg",
	    "video/x-motion-jpeg", ".mm", "application/base64", ".mme",
	    "application/base64", ".mod", "audio/mod", ".moov", "video/quicktime",
	    ".mov", "video/quicktime", ".movie", "video/x-sgi-movie", ".mp2",
	    "video/mpeg", ".mp3", "audio/mpeg3", ".mp4", "video/mpeg4", ".mpa",
	    "video/mpeg", ".mpc", "application/x-project", ".mpe", "video/mpeg",
	    ".mpeg", "video/mpeg", ".mpg", "video/mpeg", ".mpga", "audio/mpeg",
	    ".mpp", "application/vnd.ms-project", ".mpt", "application/x-project",
	    ".mpv", "application/x-project", ".mpx", "application/x-project", ".mrc",
	    "application/marc", ".ms", "application/x-troff-ms", ".mv",
	    "video/x-sgi-movie", ".my", "audio/make", ".mzz",
	    "application/x-vnd.audioexplosion.mzz", ".nap", "image/naplps",
	    ".naplps", "image/naplps", ".nc", "application/x-netcdf", ".ncm",
	    "application/vnd.nokia.configuration-message", ".nif", "image/x-niff",
	    ".niff", "image/x-niff", ".nix", "application/x-mix-transfer", ".nsc",
	    "application/x-conference", ".nvd", "application/x-navidoc", ".o",
	    "application/octet-stream", ".oda", "application/oda", ".omc",
	    "application/x-omc", ".omcd", "application/x-omcdatamaker", ".omcr",
	    "application/x-omcregerator", ".p", "text/x-pascal", ".p10",
	    "application/pkcs10", ".p12", "application/pkcs-12", ".p7a",
	    "application/x-pkcs7-signature", ".p7c", "application/pkcs7-mime",
	    ".p7m", "application/pkcs7-mime", ".p7r",
	    "application/x-pkcs7-certreqresp", ".p7s", "application/pkcs7-signature",
	    ".part", "application/pro_eng", ".pas", "text/pascal", ".pbm",
	    "image/x-portable-bitmap", ".pcl", "application/vnd.hp-pcl", ".pct",
	    "image/x-pict", ".pcx", "image/x-pcx", ".pdb", "chemical/x-pdb", ".pdf",
	    "application/pdf", ".pfunk", "audio/make", ".pgm",
	    "image/x-portable-graymap", ".pic", "image/pict", ".pict", "image/pict",
	    ".pkg", "application/x-newton-compatible-pkg", ".pko",
	    "application/vnd.ms-pki.pko", ".pl", "text/x-script.perl", ".plx",
	    "application/x-pixclscript", ".pm", "text/x-script.perl-module", ".pm4",
	    "application/x-pagemaker", ".pm5", "application/x-pagemaker", ".png",
	    "image/png", ".pnm", "image/x-portable-anymap", ".pot",
	    "application/mspowerpoint", ".pov", "model/x-pov", ".ppa",
	    "application/vnd.ms-powerpoint", ".ppm", "image/x-portable-pixmap",
	    ".pps", "application/mspowerpoint", ".ppt", "application/mspowerpoint",
	    ".ppz", "application/mspowerpoint", ".pre", "application/x-freelance",
	    ".prt", "application/pro_eng", ".ps", "application/postscript", ".psd",
	    "application/octet-stream", ".pvu", "paleovu/x-pv", ".pwz",
	    "application/vnd.ms-powerpoint", ".py", "text/x-script.phyton", ".pyc",
	    "applicaiton/x-bytecode.python", ".qcp", "audio/vnd.qcelp", ".qd3",
	    "x-world/x-3dmf", ".qd3d", "x-world/x-3dmf", ".qif", "image/x-quicktime",
	    ".qt", "video/quicktime", ".qtc", "video/x-qtc", ".qti",
	    "image/x-quicktime", ".qtif", "image/x-quicktime", ".ra",
	    "audio/x-realaudio", ".ram", "audio/x-pn-realaudio", ".ras",
	    "image/cmu-raster", ".rast", "image/cmu-raster", ".rexx",
	    "text/x-script.rexx", ".rf", "image/vnd.rn-realflash", ".rgb",
	    "image/x-rgb", ".rm", "audio/x-pn-realaudio", ".rmi", "audio/mid",
	    ".rmm", "audio/x-pn-realaudio", ".rmp", "audio/x-pn-realaudio", ".rng",
	    "application/ringing-tones", ".rnx", "application/vnd.rn-realplayer",
	    ".roff", "application/x-troff", ".rp", "image/vnd.rn-realpix", ".rpm",
	    "audio/x-pn-realaudio-plugin", ".rt", "text/richtext", ".rtf",
	    "text/richtext", ".rtx", "text/richtext", ".rv",
	    "video/vnd.rn-realvideo", ".s", "text/x-asm", ".s3m", "audio/s3m",
	    ".saveme", "application/octet-stream", ".sbk", "application/x-tbook",
	    ".scm", "text/x-script.guile", ".sdml", "text/plain", ".sdp",
	    "application/sdp", ".sdr", "application/sounder", ".sea",
	    "application/sea", ".set", "application/set", ".sgm", "text/sgml",
	    ".sgml", "text/sgml", ".sh", "application/x-sh", ".shar",
	    "application/x-shar", ".shtml", "text/x-server-parsed-html", ".sid",
	    "audio/x-psid", ".sit", "application/x-stuffit", ".skd",
	    "application/x-koan", ".skm", "application/x-koan", ".skp",
	    "application/x-koan", ".skt", "application/x-koan", ".sl",
	    "application/x-seelogo", ".smi", "application/smil", ".smil",
	    "application/smil", ".snd", "audio/basic", ".sol", "application/solids",
	    ".spc", "application/x-pkcs7-certificates", ".spl",
	    "application/futuresplash", ".spr", "application/x-sprite", ".sprite",
	    "application/x-sprite", ".src", "application/x-wais-source", ".ssi",
	    "text/x-server-parsed-html", ".ssm", "application/streamingmedia",
	    ".sst", "application/vnd.ms-pki.certstore", ".step", "application/step",
	    ".stp", "application/step", ".sv4cpio", "application/x-sv4cpio",
	    ".sv4crc", "application/x-sv4crc", ".svf", "image/x-dwg", ".svr",
	    "x-world/x-svr", ".swf", "application/x-shockwave-flash", ".t",
	    "application/x-troff", ".talk", "text/x-speech", ".tar",
	    "application/x-tar", ".tbk", "application/toolbook", ".tbk",
	    "application/x-tbook", ".tcl", "text/x-script.tcl", ".tcsh",
	    "text/x-script.tcsh", ".tex", "application/x-tex", ".texi",
	    "application/x-texinfo", ".texinfo", "application/x-texinfo", ".text",
	    "text/plain", ".tgz", "application/gnutar", ".tif", "image/tiff",
	    ".tiff", "image/tiff", ".tr", "application/x-troff", ".tsi",
	    "audio/tsp-audio", ".tsp", "audio/tsplayer", ".tsv",
	    "text/tab-separated-values", ".turbot", "image/florian", ".txt",
	    "text/plain", ".uil", "text/x-uil", ".uni", "text/uri-list", ".unis",
	    "text/uri-list", ".unv", "application/i-deas", ".uri", "text/uri-list",
	    ".uris", "text/uri-list", ".ustar", "application/x-ustar", ".uu",
	    "text/x-uuencode", ".uue", "text/x-uuencode", ".vcd",
	    "application/x-cdlink", ".vcs", "text/x-vcalendar", ".vda",
	    "application/vda", ".vdo", "video/vdo", ".vew", "application/groupwise",
	    ".viv", "video/vivo", ".vivo", "video/vivo", ".vmd",
	    "application/vocaltec-media-desc", ".vmf",
	    "application/vocaltec-media-file", ".voc", "audio/voc", ".vos",
	    "video/vosaic", ".vox", "audio/voxware", ".vqe", "audio/x-twinvq-plugin",
	    ".vqf", "audio/x-twinvq", ".vql", "audio/x-twinvq-plugin", ".vrml",
	    "application/x-vrml", ".vrt", "x-world/x-vrt", ".vsd",
	    "application/x-visio", ".vst", "application/x-visio", ".vsw",
	    "application/x-visio", ".w60", "application/wordperfect6.0", ".w61",
	    "application/wordperfect6.1", ".w6w", "application/msword", ".wav",
	    "audio/wav", ".wb1", "application/x-qpro", ".wbmp", "image/vnd.wap.wbmp",
	    ".web", "application/vnd.xara", ".wiz", "application/msword", ".wk1",
	    "application/x-123", ".wmf", "windows/metafile", ".wml",
	    "text/vnd.wap.wml", ".wmlc", "application/vnd.wap.wmlc", ".wmls",
	    "text/vnd.wap.wmlscript", ".wmlsc", "application/vnd.wap.wmlscriptc",
	    ".word", "application/msword", ".wp", "application/wordperfect", ".wp5",
	    "application/wordperfect", ".wp6", "application/wordperfect", ".wpd",
	    "application/wordperfect", ".wq1", "application/x-lotus", ".wri",
	    "application/mswrite", ".wrl", "model/vrml", ".wrz", "model/vrml",
	    ".wsc", "text/scriplet", ".wsrc", "application/x-wais-source", ".wtk",
	    "application/x-wintalk", ".xbm", "image/xbm", ".xdr",
	    "video/x-amt-demorun", ".xgz", "xgl/drawing", ".xif", "image/vnd.xiff",
	    ".xl", "application/excel", ".xla", "application/excel", ".xlb",
	    "application/excel", ".xlc", "application/excel", ".xld",
	    "application/excel", ".xlk", "application/excel", ".xll",
	    "application/excel", ".xlm", "application/excel", ".xls",
	    "application/excel", ".xlt", "application/excel", ".xlv",
	    "application/excel", ".xlw", "application/excel", ".xm", "audio/xm",
	    ".xml", "text/xml", ".xmz", "xgl/movie", ".xpix",
	    "application/x-vnd.ls-xpix", ".xpm", "image/xpm", ".x-png", "image/png",
	    ".xsr", "video/x-amt-showrun", ".xwd", "image/x-xwd", ".xyz",
	    "chemical/x-pdb", ".z", "application/x-compressed", ".zip",
	    "application/zip", ".zoo", "application/octet-stream", ".zsh",
	    "text/x-script.zsh" };

	static public String getFileContents ( File f ) throws IOException,
	    FileNotFoundException {
		StringBuffer ret = new StringBuffer ();
		BufferedReader input = null;
		input = new BufferedReader ( new FileReader ( f ) );
		String line = null;
		while ( ( line = input.readLine () ) != null ) {
			ret.append ( line );
			ret.append ( CRLF );
		}
		if ( input != null ) {
			input.close ();
		}
		return ret.toString ();
	}

	/**
	 * Convert a java.util.Calendar object to a YYYYMMDD String.
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a String in YYYYMMDD format
	 */
	public static String CalendarToYYYYMMDD ( Calendar inDate ) {
		StringBuffer ret = new StringBuffer ( 8 );
		ret.append ( inDate.get ( Calendar.YEAR ) );
		if ( inDate.get ( Calendar.MONTH ) + 1 < 10 )
			ret.append ( '0' );
		ret.append ( ( inDate.get ( Calendar.MONTH ) + 1 ) );
		if ( inDate.get ( Calendar.DAY_OF_MONTH ) < 10 )
			ret.append ( '0' );
		ret.append ( inDate.get ( Calendar.DAY_OF_MONTH ) );
		return ret.toString ();
	}

	/**
	 * Convert a Date object to a YYYYMMDD String.
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a String in YYYYMMDD format
	 */
	public static String DateToYYYYMMDD ( Date inDate ) {
		StringBuffer ret = new StringBuffer ( 8 );
		ret.append ( inDate.year );
		if ( inDate.month < 10 )
			ret.append ( '0' );
		ret.append ( inDate.month );
		if ( inDate.day < 10 )
			ret.append ( '0' );
		ret.append ( inDate.day );
		return ret.toString ();
	}

	/**
	 * Convert a YYYYMMDD String into a java.util.Calendar object. Possible
	 * formats include:
	 * <ul>
	 * <li> 20001231 (date only, no time) </li>
	 * <li> 20001231T235900 (date with local time) </li>
	 * <li> 20001231T235900Z (date with utc time) </li>
	 * </ul>
	 * 
	 * @param inDate
	 *          Date to convert
	 * @return The Date as a Calendar
	 */
	public static Calendar YYYYMMDDToCalendar ( String inDate )
	    throws BogusDataException, ParseException {
		Calendar ret = Calendar.getInstance ();
		if ( inDate.length () < 8 )
			throw new BogusDataException ( "Invalid date format '" + inDate + "'",
			    inDate );
		int year = Integer.parseInt ( inDate.substring ( 0, 4 ) );
		int month = Integer.parseInt ( inDate.substring ( 4, 6 ) );
		int day = Integer.parseInt ( inDate.substring ( 6, 8 ) );
		// TODO: validate for each month and leap years, too
		// TODO: can java.util.Calendar handle dates before 1970 and after 2038?
		if ( day < 1 || day > 31 || month < 1 || month > 12 )
			throw new BogusDataException ( "Invalid date '" + inDate + "'", inDate );
		// TODO: parse time, handle localtime, handle timezone
		if ( inDate.length () > 8 ) {
			if ( inDate.charAt ( 8 ) == 'T' ) {
				try {
					int hour = Integer.parseInt ( inDate.substring ( 9, 11 ) );
					int minute = Integer.parseInt ( inDate.substring ( 11, 13 ) );
					int second = Integer.parseInt ( inDate.substring ( 13, 15 ) );
					ret.set ( year, month, day, hour, minute, second );
				} catch ( NumberFormatException nef ) {
					throw new BogusDataException ( "Invalid time in date string '"
					    + inDate + "' - " + nef, inDate );
				}
			} else {
				// Invalid format
				throw new ParseException ( "Invalid date format '" + inDate + "'",
				    inDate );
			}
		} else {
			// Just date, no time
			ret.set ( year, month, day );
		}
		return ret;
	}

	/**
	 * Get the date for the first day of the week for the specified date.
	 */
	public static Calendar startOfWeek ( Calendar cal, boolean sundayStartsWeek ) {
		int dow = sundayStartsWeek ? Calendar.SUNDAY : Calendar.MONDAY;
		Calendar ret = (Calendar) cal.clone ();
		while ( ret.get ( Calendar.DAY_OF_WEEK ) != dow ) {
			ret.add ( Calendar.DATE, -1 );
		}
		return ret;
	}

	/**
	 * Get the date for the first day of the week for the specified date.
	 */
	public static Calendar endOfWeek ( Calendar cal, boolean sundayStartsWeek ) {
		int dow = sundayStartsWeek ? Calendar.SATURDAY : Calendar.SUNDAY;
		Calendar ret = (Calendar) cal.clone ();
		while ( ret.get ( Calendar.DAY_OF_WEEK ) != dow ) {
			ret.add ( Calendar.DATE, 1 );
		}
		return ret;
	}

	/**
	 * Get the day of the week (0=Sun to 6=Sat) for any specified date.
	 * 
	 * @param y
	 *          year (NNNN format, > 0)
	 * @param m
	 *          month (1=Jan)
	 * @param d
	 *          Day of month
	 * @return
	 */
	public static int getDayOfWeek ( int y, int m, int d ) {
		int[] month = { -1, 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };

		if ( m == 1 )
			y--;
		if ( m == 2 )
			y--;

		int wday = ( ( d + month[m] + y + ( y / 4 ) - ( y / 100 ) + ( y / 400 ) ) % 7 );
		return wday;
	}

	/**
	 * Get the weekday for the first day of the year (Jan 1)
	 * 
	 * @param y
	 *          The 4-digit year
	 * @return The weekday (0=sunday)
	 */
	public static int getFirstDayOfWeekForYear ( int y ) {
		try {
			Date d = new Date ( "FOOBAR", y, 1, 1 );
			return getDayOfWeek ( d.getYear (), d.getMonth (), d.getDay () );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return -1;
		}
	}

	/**
	 * Get the weekday for the last day of the year (Dec 31)
	 * 
	 * @param y
	 *          The 4-digit year
	 * @return The weekday (0=sunday)
	 */
	public static int getLastDayOfWeekForYear ( int y ) {
		try {
			Date d = new Date ( "FOOBAR", y, 12, 31 );
			return getDayOfWeek ( d.getYear (), d.getMonth (), d.getDay () );
		} catch ( BogusDataException e1 ) {
			e1.printStackTrace ();
			return -1;
		}
	}

	/**
	 * Get the MIME type for the specified filename. This method uses an internal
	 * lookup table containing many common MIME types. Additionally, many file
	 * types have multiple valid MIME types, in which case the most common will be
	 * used. This method <b>does not</b> examine the contents of the file. Only
	 * the filename extension will be used.
	 * 
	 * @param filename
	 *          The filename ("somefile.jpg")
	 * @return The MIME type ("image/jpg")
	 */
	public static String getMimeTypeForExtension ( String filename ) {
		int ind = filename.lastIndexOf ( '.' );
		if ( ind < 0 )
			return "text/plain";
		String fileExt = filename.substring ( ind ).toLowerCase ();
		for ( int i = 0; i < mimeArray.length; i += 2 ) {
			if ( fileExt.equals ( mimeArray[i] ) )
				return mimeArray[i + 1];
		}
		// not found
		return "text/plain";
	}

	/**
	 * Generate a unique identifier for the current user.
	 * 
	 * @param prefix
	 *          A prefix to include in the identifier
	 * @return
	 */
	public static String generateUniqueId ( String prefix ) {
		StringBuffer ret = new StringBuffer ();
		ret.append ( prefix == null ? "JAVACALTOOLS" : prefix.toUpperCase () );
		ret.append ( '-' );
		String user = (String) System.getProperty ( "user.name" );
		if ( user == null ) {
			ret.append ( "UNKNOWN" );
			user = "UNKNOWN";
		} else {
			ret.append ( user.toUpperCase () );
		}
		ret.append ( java.util.Calendar.getInstance ().getTimeInMillis () );
		return ret.toString ();
	}

}
