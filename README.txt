****************************************************************************
			k5n Java Calendar Tools
****************************************************************************

Version:      0.4.2
URL:          http://javacaltools.sourceforge.net
Author:       Craig Knudsen, craig [< at >] k5n.us
License:      GNU GPL
Requires:     Java 1.5 for ical library
              Java 1.2 for CalendarPanel

---------------------------------------------------------------------------
                         ABOUT
---------------------------------------------------------------------------
The k5n Java Calendar Tools package contains various calendar-related
libraries written in Java.  See the URL above for more information.


---------------------------------------------------------------------------
                         BUILDING
---------------------------------------------------------------------------
To build the source, you will need to use ant with the provided build.xml
file.  (Ant 1.6 and or later is required.  Java 1.5 or later is required.)

To build with ant:

	ant

This build process will create the following jar files:

	dist/lib/k5n-ical-0.4.2.jar
	dist/lib/k5n-calendarpanel-0.4.2.jar
	dist/k5njournal-0.4.2.jar

---------------------------------------------------------------------------
                         DOCUMENTATION
---------------------------------------------------------------------------
To generate the javadoc-based documentation, use the following command:

	ant javadoc

---------------------------------------------------------------------------
                         OVERVIEW OF CONTENTS
---------------------------------------------------------------------------

k5n-ical:
  The k5n-ical jar is the iCalendar library that can be used by
  other applications.

k5n-calendarpanel:
  A Java Swing component for displaying a calendar with events by
  extending the Swing JPanel class.

k5n-journal:
  An earlier release of the k5nJournal application includes as an
  example of how to use the k5n-ical library.  (The most recent
  version of this application can be found at k5njournal.sourceforge.net)


---------------------------------------------------------------------------
                         RUNNING THE SAMPLES
---------------------------------------------------------------------------

The k5nJournal jar file contains a usable sample application for the
iCalendar library.  This jar file contains the files for both the iCalendar
library and the k5nJournal application.  To run the k5njournal application,
you can either double-click on the jar file in your file viewer (Windows
Explorer or Mac OS X Finder), or you can use the following command from a
command prompt:

	java -jar k5n-journal-0.4.2.jar

The CalendarPanel jar file contains a test class that demonstrates the
appearance of this Swing component.  To view the test class:

	java -jar k5n-calendarpanel-0.4.2.jar


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

Java CSV Library:
  URL: http://sourceforge.net/projects/javacsv/
  License: LGPL
  License URL: http://www.gnu.org/licenses/lgpl.html
  
Apache Commons Codec:
  URL: http://commons.apache.org/codec/
  License: Apache License Version 2.0
  License URL: http://www.apache.org/licenses/LICENSE-2.0.html
