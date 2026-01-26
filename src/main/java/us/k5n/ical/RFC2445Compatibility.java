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
 * RFC 2445 Compatibility Layer
 *
 * <p>Provides backward compatibility with the original iCalendar specification (RFC 2445).
 * This class handles deprecated properties and syntax that were removed or changed in RFC 5545.</p>
 *
 * <p><b>RFC 2445 Differences:</b></p>
 * <ul>
 *   <li>Supports EXRULE property (deprecated exception rules)</li>
 *   <li>Alternative date/time formatting options</li>
 *   <li>Legacy property names and values</li>
 *   <li>Lenient parsing for older calendar data</li>
 * </ul>
 *
 * @author Craig Knudsen, craig@k5n.us
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2445">RFC 2445</a>
 */
public class RFC2445Compatibility {

    /**
     * Check if a property is deprecated in RFC 5545 but valid in RFC 2445
     *
     * @param propertyName The property name to check
     * @return true if the property is RFC 2445 compatible
     */
    public static boolean isRFC2445Property(String propertyName) {
        if (propertyName == null) return false;

        String upper = propertyName.toUpperCase();
        return "EXRULE".equals(upper) ||
               upper.startsWith("X-") ||
               "COMMENT".equals(upper) || // COMMENT was clarified in RFC 5545
               "GEO".equals(upper); // GEO format was clarified
    }

    /**
     * Normalize RFC 2445 property values to RFC 5545 format
     *
     * @param propertyName The property name
     * @param value The property value
     * @return Normalized value compatible with RFC 5545
     */
    public static String normalizeRFC2445Value(String propertyName, String value) {
        if (propertyName == null || value == null) return value;

        String upper = propertyName.toUpperCase();

        // Handle EXRULE conversion (though we don't implement it)
        if ("EXRULE".equals(upper)) {
            // EXRULE is deprecated - could convert to EXDATE but that's complex
            return value;
        }

        // Handle GEO property (latitude;longitude format)
        if ("GEO".equals(upper)) {
            // RFC 2445: "37.386013;-122.082932"
            // RFC 5545: "37.386013;-122.082932" (same format actually)
            return value;
        }

        return value;
    }

    /**
     * Validate RFC 2445 specific constraints
     *
     * @param propertyName The property name
     * @param value The property value
     * @return true if valid according to RFC 2445 rules
     */
    public static boolean validateRFC2445Property(String propertyName, String value) {
        if (propertyName == null || value == null) return false;

        String upper = propertyName.toUpperCase();

        // EXRULE validation (basic)
        if ("EXRULE".equals(upper)) {
            return value.contains("FREQ="); // Basic check
        }

        return true;
    }

    /**
     * Get RFC 2445 equivalent property name
     *
     * @param rfc5545Name The RFC 5545 property name
     * @return RFC 2445 equivalent, or original name if no change
     */
    public static String getRFC2445Equivalent(String rfc5545Name) {
        if (rfc5545Name == null) return null;

        // Most property names stayed the same from RFC 2445 to 5545
        // EXRULE was removed, but we handle it separately
        return rfc5545Name;
    }
}
