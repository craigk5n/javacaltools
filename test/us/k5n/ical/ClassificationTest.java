
package us.k5n.ical;

import junit.framework.*;

public class ClassificationTest extends TestCase
  implements Constants {

  public void setUp ()
  {
  }

  public void testOne () {
    try {
      Classification c = new Classification ( "CLASS: PUBLIC\n" );
      assertTrue ( c.getClassification() == PUBLIC );
    } catch ( Exception e ) {
      fail ( "Failed: " + e.toString () );
    }
  }

  public static Test suite() {
    return new TestSuite(ClassificationTest.class);
  }

  public static void main ( String args[] ) {
    junit.textui.TestRunner.run(ClassificationTest.class);
  }

}

