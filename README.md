# k5n Java Calendar Tools
- Version: 1.0.0
- URL: https://github.com/craigk5n/javacaltools
- Author: Craig Knudsen, craig [< at >] k5n.us
- License: GNU GPL
- Requires: Java 1.8

## About
The k5n Java Calendar Tools package contains various calendar-related
libraries written in Java.  See the URL above for more information.

## Building
To build the source, you will need to use ant with the provided build.xml
file.  (Ant 1.6 and or later is required.  Java 1.5 or later is required.)

To build with maven:

    mvn package

This build process will create the following jar files:

- target/javacaltools-1.0.0.jar

# Overview of Contents

- iCalendar parser
  The k5n-ical jar is the iCalendar library that can be used by
  other applications.
- Swing CalendarPanel
  A Java Swing component for displaying a calendar with events by
  extending the Swing JPanel class.
- Simple Swing-based Journalling app
  An earlier release of the k5nJournal application includes as an
  example of how to use the k5n-ical library.  (The most recent
  version of this application can be found at k5njournal.sourceforge.net)


# Running The Samples
The jar file contains a usable sample application for the
iCalendar library.  This jar file contains the files for both the iCalendar
library and the k5nJournal application.  To run the k5njournal application,
use the following command from a command prompt:

    java -cp javaltools-1.0.0.jar us.k5n.journal.Main

The CalendarPanel jar file contains a test class that demonstrates the
appearance of this Swing component.  To view the test class:

    java -cp javacaltools-1.0.0.jar us.k5n.ui.calendar.CalendarPanelTest


# Unit Tests
There are also unit tests in the "src/test" directory.  These rely
on JUnit 5 and will be run during "mvn package".

# License
This library and all associated tools and applications are licensed under
the GNU Lesser General Public License v2.1.

For information about this license, see the LICENSE file.
