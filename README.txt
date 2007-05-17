                           k5n java calendar tools
****************************************************************************

Version:      0.3.0
URL:          http://javacaltools.sourceforge.net
Author:       Craig Knudsen, craig [< at >] k5n.us
License:      GNU GPL
Requires:     Java 1.5 or later

---------------------------------------------------------------------------
                         BUILDING
---------------------------------------------------------------------------
To build the source, you will need to use ant with the provided build.xml
file.  (Ant 1.6 and or later is required.  Java 1.5 or later is required.)

To build with ant:

ant

This build process will create the following jar files:

	dist/lib/k5n-ical-0.3.0.jar
	dist/k5njournal-0.3.0.jar

The k5n-ical jar is the main iCalendar library that can be used by
other applications.

The k5njournal jar is a jar file for the k5njournal application.

---------------------------------------------------------------------------
                         RUNNING THE SAMPLE APP
---------------------------------------------------------------------------

The k5njournal jar file contains a usable sample application for the
iCalendar library.  This jar file contains the files for both the iCalendar
library and the k5njournal application.  To run the k5njournal application,
you can either double-click on the jar file in your file viewer (Windows
Explorer or Mac OS X Finder), or you can use the following command from a
command prompt:

	java -jar k5njournal-0.3.0.jar


---------------------------------------------------------------------------
                         UNIT TESTS
---------------------------------------------------------------------------

There are also unit tests in the "test" directory.  You will need Ant
and JUnit for these.  You will need to add junit.jar to your CLASSPATH
setting.  To run the unit tests, use "ant test" from the command line.

---------------------------------------------------------------------------
                         LICENSE
---------------------------------------------------------------------------

This library and all associated tools and applications are licensed under
the GNU General Public License.

For information about this license:

	http://www.gnu.org/licenses/gpl.html
	

---------------------------------------------------------------------------
                         3RD PARTY PACKAGES
---------------------------------------------------------------------------
	

This package makes use of the following packages.  The class files from
these packages are bundled into the ical jar file, so you do not need
to download them separately.

Joda Time
  URL: http://joda-time.sourceforge.net/index.html
  License: Apache License 2.0
  License URL: http://joda-time.sourceforge.net/license.html
  
Google RFC2445
  URL: http://code.google.com/p/google-rfc-2445/
  License: Apache License 2.0
  License URL: http://www.apache.org/licenses/
