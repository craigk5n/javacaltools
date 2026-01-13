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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for VALARM component parsing and functionality
 * 
 * @author Craig Knudsen, craig@k5n.us
 */
public class ValarmTest {

	@Test
	@DisplayName("Test VALARM with ACTION property parsing")
	void testValarmAction() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nEND:VALARM");
		assertTrue(alarm.isValid());
		assertEquals("DISPLAY", alarm.getAction());
	}

	@Test
	@DisplayName("Test VALARM with TRIGGER property parsing")
	void testValarmTrigger() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nEND:VALARM");
		assertTrue(alarm.isValid());
		assertEquals("-PT15M", alarm.getTrigger());
	}

	@Test
	@DisplayName("Test VALARM with SUMMARY property parsing")
	void testValarmSummary() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nSUMMARY:Meeting\nEND:VALARM");
		assertTrue(alarm.isValid());
		assertEquals("Meeting", alarm.getSummary().value);
	}

	@Test
	@DisplayName("Test VALARM with DESCRIPTION property parsing")
	void testValarmDescription() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nDESCRIPTION:Test description\nEND:VALARM");
		assertTrue(alarm.isValid());
		assertEquals("Test description", alarm.getDescription().value);
	}

	@Test
	@DisplayName("Test VALARM invalid - missing ACTION")
	void testValarmInvalidMissingAction() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nTRIGGER:-PT15M\nEND:VALARM");
		assertFalse(alarm.isValid());
		assertNull(alarm.getAction());
	}

	@Test
	@DisplayName("Test VALARM toICalendar output")
	void testValarmToICalendar() throws Exception {
		Valarm alarm = new Valarm("BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER:-PT15M\nSUMMARY:Test alarm\nEND:VALARM");
		String output = alarm.toICalendar();
		assertTrue(output.contains("BEGIN:VALARM"));
		assertTrue(output.contains("ACTION:DISPLAY"));
		assertTrue(output.contains("TRIGGER:"));
		assertTrue(output.contains("SUMMARY:Test alarm"));
		assertTrue(output.contains("END:VALARM"));
	}
}
