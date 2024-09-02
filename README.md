# k5n Java Calendar Tools
- Version: 1.0.2
- URL: https://github.com/craigk5n/javacaltools
- Author: Craig Knudsen, craig [< at >] k5n.us
- License: GNU GPL
- Requires: Java 11

## About
The k5n Java Calendar Tools package contains various calendar-related
libraries written in Java.  See the URL above for more information.

Note: This project now bundles the Google RFC 2445 Rrule code.
The project is now retired, but it's project page can be
found here:

  https://code.google.com/archive/p/google-rfc-2445/

See the LICENSE-google-rfc-2445	file for the license for that project.

## Building
To build the source, you will need to use maven:
```
    mvn package
```

This build process will create the following jar file:

- `target/javacaltools-1.0.2.jar`

# License
This library and all associated tools and applications are licensed under
the GNU Lesser General Public License v2.1.

For information about this license, see the LICENSE file.

# Swing Calendar Panel
There is a Java Swing component for display events.
You can use the `CalendarPanelTest` class to view a sample of what it looks like:
```
java -jar target/javacaltools-1.0.2.jar
```

This will show you something like this:
![Sample Image for CalendarPanelTest](CalendarPanelTest.png)
