/*
 * Copyright (C) 2005-2024 Craig Knudsen and other authors
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

/**
 * iTIP (iCalendar Transport-Independent Interoperability Protocol) Manager
 *
 * <p>Implements RFC 2446/RFC 5546 iTIP protocol for managing calendar scheduling
 * workflows including REQUEST→REPLY→UPDATE cycles and attendee state management.</p>
 *
 * <p><b>RFC 5546 Compliance:</b></p>
 * <ul>
 *   <li>Section 3 - iTIP Methods (PUBLISH, REQUEST, REPLY, ADD, CANCEL, REFRESH, COUNTER, DECLINECOUNTER)</li>
 *   <li>Section 4 - Interoperability Models</li>
 *   <li>Section 5 - Application Protocol Elements</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5546">RFC 5546</a>
 */
public class ITIPManager implements Constants {

    /**
     * iTIP method types for scheduling operations
     */
    public enum ITIPMethod {
        PUBLISH("PUBLISH"),
        REQUEST("REQUEST"),
        REPLY("REPLY"),
        ADD("ADD"),
        CANCEL("CANCEL"),
        REFRESH("REFRESH"),
        COUNTER("COUNTER"),
        DECLINECOUNTER("DECLINECOUNTER");

        private final String value;

        ITIPMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ITIPMethod fromString(String value) {
            for (ITIPMethod method : values()) {
                if (method.value.equalsIgnoreCase(value)) {
                    return method;
                }
            }
            return null;
        }
    }

    /**
     * Validate iTIP method usage for a given component
     *
     * @param method The iTIP method
     * @param componentType The calendar component type (VEVENT, VTODO, etc.)
     * @return true if the method is valid for the component type
     */
    public static boolean isValidMethodForComponent(ITIPMethod method, String componentType) {
        if (method == null || componentType == null) return false;

        String upperComponent = componentType.toUpperCase();

        switch (method) {
            case PUBLISH:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent) ||
                       "VJOURNAL".equals(upperComponent) || "VFREEBUSY".equals(upperComponent);

            case REQUEST:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case REPLY:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case ADD:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case CANCEL:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case REFRESH:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case COUNTER:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            case DECLINECOUNTER:
                return "VEVENT".equals(upperComponent) || "VTODO".equals(upperComponent);

            default:
                return false;
        }
    }

    /**
     * Get the appropriate iTIP method for a scheduling operation
     *
     * @param operation The operation type
     * @return The corresponding iTIP method
     */
    public static ITIPMethod getMethodForOperation(String operation) {
        if (operation == null) return null;

        switch (operation.toLowerCase()) {
            case "publish":
            case "create":
                return ITIPMethod.PUBLISH;
            case "invite":
            case "request":
                return ITIPMethod.REQUEST;
            case "respond":
            case "reply":
                return ITIPMethod.REPLY;
            case "update":
            case "modify":
                return ITIPMethod.REQUEST; // Updates use REQUEST
            case "cancel":
                return ITIPMethod.CANCEL;
            case "add":
                return ITIPMethod.ADD;
            case "refresh":
                return ITIPMethod.REFRESH;
            default:
                return null;
        }
    }

    /**
     * Validate iTIP workflow transitions
     *
     * @param fromMethod The originating iTIP method
     * @param toMethod The responding iTIP method
     * @return true if the transition is valid
     */
    public static boolean isValidWorkflowTransition(ITIPMethod fromMethod, ITIPMethod toMethod) {
        if (fromMethod == null || toMethod == null) return false;

        switch (fromMethod) {
            case REQUEST:
                return toMethod == ITIPMethod.REPLY ||
                       toMethod == ITIPMethod.COUNTER ||
                       toMethod == ITIPMethod.DECLINECOUNTER;
            case COUNTER:
                return toMethod == ITIPMethod.DECLINECOUNTER ||
                       toMethod == ITIPMethod.REPLY;
            default:
                return false;
        }
    }

    /**
     * Generate appropriate iTIP response based on attendee action
     *
     * @param attendeeAction The attendee's action (ACCEPT, DECLINE, TENTATIVE, etc.)
     * @return The corresponding iTIP method for the response
     */
    public static ITIPMethod getResponseMethodForAttendeeAction(String attendeeAction) {
        if (attendeeAction == null) return null;

        switch (attendeeAction.toUpperCase()) {
            case "ACCEPT":
            case "DECLINE":
            case "TENTATIVE":
                return ITIPMethod.REPLY;
            case "COUNTER":
                return ITIPMethod.COUNTER;
            case "DECLINE_COUNTER":
                return ITIPMethod.DECLINECOUNTER;
            default:
                return null;
        }
    }
}
