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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * iCalendar Categories class - This object represents a category list and
 * corresponds to the CATEGORIES iCalendar property.
 *
 * @author Craig Knudsen, craig@k5n.us
 */
public class Categories extends TextProperty {

    /**
     * Default constructor
     */
    public Categories() {
        super("CATEGORIES");
    }

    /**
     * Constructor from iCalendar string
     *
     * @param icalStr One or more lines of iCalendar that specifies an event/todo description
     */
    public Categories(String icalStr) throws ParseException {
        super("CATEGORIES", icalStr, PARSE_LOOSE);
    }

    /**
     * Constructor from iCalendar string with parse mode
     *
     * @param icalStr   One or more lines of iCalendar that specifies a category list (comma separated)
     * @param parseMode PARSE_STRICT or PARSE_LOOSE
     */
    public Categories(String icalStr, int parseMode) throws ParseException {
        super("CATEGORIES", icalStr, parseMode);
    }

    /**
     * Get categories as a list
     *
     * @return list of category names
     */
    public List<String> getCategoryList() {
        String val = getValue();
        if (val == null || val.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(val.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Set categories from a list
     *
     * @param categories list of category names
     */
    public void setCategoryList(List<String> categories) {
        setValue(String.join(",", categories));
    }

    /**
     * Get categories as comma-separated string
     *
     * @return comma-separated category names
     */
    public String getCategories() {
        return getValue();
    }

    /**
     * Set categories from comma-separated string
     *
     * @param categories comma-separated category names
     */
    public void setCategories(String categories) {
        setValue(categories);
    }

    /**
     * Convert categories to string representation
     *
     * @return comma-separated category list
     */
    @Override
    public String toString() {
        return getValue() != null ? getValue() : "";
    }
}
