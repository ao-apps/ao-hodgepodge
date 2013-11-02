/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util;

import java.util.Calendar;

/**
 * Calendar utilities.
 *
 * @author  AO Industries, Inc.
 */
public class CalendarUtils {

    private CalendarUtils() {
    }

    /**
     * Gets the date from the YYYY-MM-DD format.
	 * Allows negative years like "-344-01-23".
	 * Allows shorter months and days like "1976-1-9".
     */
    public static Calendar parseDate(String yyyy_mm_dd) throws IllegalArgumentException {
		int pos1 = yyyy_mm_dd.indexOf('-', 1); // Start search at second character to allow negative years: -1000-01-23
		if(pos1==-1) throw new IllegalArgumentException("Invalid date: "+yyyy_mm_dd);
		int pos2 = yyyy_mm_dd.indexOf('-', pos1 + 1);
		if(pos2==-1) throw new IllegalArgumentException("Invalid date: "+yyyy_mm_dd);
        int year = Integer.parseInt(yyyy_mm_dd.substring(0, pos1));
        int month = Integer.parseInt(yyyy_mm_dd.substring(pos1 + 1, pos2));
        if(month<1 || month>12) throw new IllegalArgumentException("Invalid month: "+yyyy_mm_dd);
        int day = Integer.parseInt(yyyy_mm_dd.substring(pos2 + 1));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        if (day<1 || day>cal.getMaximum(Calendar.DATE)) throw new IllegalArgumentException("Invalid day of month: "+yyyy_mm_dd);
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
