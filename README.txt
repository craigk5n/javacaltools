                           k5n java calendar tools
****************************************************************************

Version:      0.1.1
URL:          http://javacaltools.sourceforge.net
Author:       Craig Knudsen, craig [< at >] k5n.us

To build the source, you will need to use ant with the provided build.xml
file.  (Ant 1.6 or later is required.)

To build with ant:

ant

Both of these build processes will create a jar file in the dist/lib
directory.

There are also unit tests in the "test" directory.  You will need Ant
and JUnit for these.  You will need to add junit.jar to your CLASSPATH
setting.  To run the unit tests, use "ant test" from the command line.
