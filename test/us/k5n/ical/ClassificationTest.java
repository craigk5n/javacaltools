package us.k5n.ical;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for Classification.
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class ClassificationTest extends TestCase implements Constants {

	public void setUp () {
	}

	public void testOne () {
		try {
			Classification c = new Classification ( "CLASS: PUBLIC" );
			assertTrue ( "PUBLIC != " + c.getClassification (), c
			    .getClassification () == PUBLIC );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( "CLASS: public" );
			assertTrue ( "public != " + c.getClassification (), c
			    .getClassification () == PUBLIC );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( PUBLIC );
			assertTrue ( "public != " + c.getClassification (), c
			    .getClassification () == PUBLIC );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testTwo () {
		try {
			Classification c = new Classification ( "CLASS: PRIVATE" );
			assertTrue ( "PRIVATE != " + c.getClassification () + ", "
			    + c.toICalendar (), c.getClassification () == PRIVATE );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( "CLASS: private" );
			assertTrue ( "private != " + c.getClassification (), c
			    .getClassification () == PRIVATE );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( PRIVATE );
			assertTrue ( "private != " + c.getClassification (), c
			    .getClassification () == PRIVATE );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
	}

	public void testThree () {
		try {
			Classification c = new Classification ( "CLASS: CONFIDENTIAL" );
			assertTrue ( "CONFIDENTIAL != " + c.getClassification (), c
			    .getClassification () == CONFIDENTIAL );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( "CLASS: confidential" );
			assertTrue ( "confidential != " + c.getClassification (), c
			    .getClassification () == CONFIDENTIAL );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
		try {
			Classification c = new Classification ( CONFIDENTIAL );
			assertTrue ( "confidential != " + c.getClassification (), c
			    .getClassification () == CONFIDENTIAL );
		} catch ( Exception e ) {
			fail ( "Failed: " + e.toString () );
		}
	}

	public static Test suite () {
		return new TestSuite ( ClassificationTest.class );
	}

	public static void main ( String args[] ) {
		junit.textui.TestRunner.run ( ClassificationTest.class );
	}

}
