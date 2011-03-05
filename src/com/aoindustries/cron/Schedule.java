/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.cron;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Parses a cron-like schedule line, including support for lists, steps, ranges, asterisks, names, and special strings.
 *
 * See man 5 crontab
 *
 * @see  CronJob
 *
 * @author  AO Industries, Inc.
 */
public class Schedule {

    /**
     * Parses an entire schedule line.
     */
    public static Schedule parseSchedule(String str) throws IllegalArgumentException {
        if("@yearly".equalsIgnoreCase(str) || "@annually".equalsIgnoreCase(str)) str = "0 0 1 1 *";
        else if("@monthly".equalsIgnoreCase(str)) str = "0 0 1 * *";
        else if("@weekly".equalsIgnoreCase(str)) str = "0 0 * * 0";
        else if("@daily".equalsIgnoreCase(str) || "@midnight".equalsIgnoreCase(str)) str = "0 0 * * *";
        else if("@hourly".equalsIgnoreCase(str)) str = "0 * * * *";
        StringTokenizer st = new StringTokenizer(str);
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        CronMatcher minute = CronMatcher.parseMinute(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        CronMatcher hour = CronMatcher.parseHour(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        CronMatcher dayOfMonth = CronMatcher.parseDayOfMonth(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        CronMatcher month = CronMatcher.parseMonth(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        CronMatcher dayOfWeek = CronMatcher.parseDayOfWeek(st.nextToken());
        if(st.hasMoreTokens()) throw new IllegalArgumentException();
        return new Schedule(minute, hour, dayOfMonth, month, dayOfWeek);
    }

    private final CronMatcher minute;
    private final CronMatcher hour;
    private final CronMatcher dayOfMonth;
    private final CronMatcher month;
    private final CronMatcher dayOfWeek;

    public Schedule(
        CronMatcher minute,
        CronMatcher hour,
        CronMatcher dayOfMonth,
        CronMatcher month,
        CronMatcher dayOfWeek
    ) {
        this.minute = minute;
        this.hour = hour;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
    }

    public CronMatcher getMinute() {
        return minute;
    }

    public CronMatcher getHour() {
        return hour;
    }

    public CronMatcher getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Note: months are 1-12 like cron, not 0-11 like Calendar.
     */
    public CronMatcher getMonth() {
        return month;
    }

    /**
     * Note: Sunday is 0, not 1 like Calendar.
     */
    public CronMatcher getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean matches(int minute, int hour, int dayOfMonth, int month, int dayOfWeek) {
        return
            this.minute.matches(minute)
            && this.hour.matches(hour)
            && this.month.matches(1 + (month - Calendar.JANUARY))
            && (
                this.dayOfMonth.matches(dayOfMonth)
                || this.dayOfWeek.matches(0 + (dayOfWeek - Calendar.SUNDAY))
            )
        ;
    }
}
