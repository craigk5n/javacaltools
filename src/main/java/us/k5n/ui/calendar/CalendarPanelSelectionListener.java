/*
 * Copyright (C) 2005-2017 Craig Knudsen and other authors
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
package us.k5n.ui.calendar;


/**
 * This interface should be implements for any class that wants to receive
 * CalendarPanelSelection events from the CalendarPanel class.
 * 
 * @see CalendarPanel
 * @author Craig Knudsen, craig@k5n.us
 */
public interface CalendarPanelSelectionListener {

	public abstract void eventSelected ( EventInstance eventInstance );

	public abstract void eventUnselected ();

	public abstract void eventDoubleClicked ( EventInstance eventInstance );

	public abstract void dateDoubleClicked ( int year, int month, int dayOfMonth );

}
