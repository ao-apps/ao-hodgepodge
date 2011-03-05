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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Parses a cron-like schedule line, including support for lists, steps, ranges, asterisks, names, and special strings.
 * This also extends the syntax to allow multiple cron-like schedules separated by semicolon (;).
 *
 * See man 5 crontab
 *
 * @see  CronJob
 *
 * @author  AO Industries, Inc.
 */
public class MatcherSchedule implements Schedule {

    /**
     * Parses an entire schedule.
     */
    public static Schedule parseSchedule(String str) throws IllegalArgumentException {
        // Handle multiple schedules separated by semicolon
        if(str.indexOf(';')!=-1) {
            Collection<Schedule> schedules = new ArrayList<Schedule>();
            StringTokenizer st = new StringTokenizer(";");
            while(st.hasMoreTokens()) schedules.add(parseSchedule(st.nextToken()));
            return new MultiSchedule(schedules);
        }
        // Special strings
        if("@yearly".equalsIgnoreCase(str) || "@annually".equalsIgnoreCase(str)) str = "0 0 1 1 *";
        else if("@monthly".equalsIgnoreCase(str)) str = "0 0 1 * *";
        else if("@weekly".equalsIgnoreCase(str)) str = "0 0 * * 0";
        else if("@daily".equalsIgnoreCase(str) || "@midnight".equalsIgnoreCase(str)) str = "0 0 * * *";
        else if("@hourly".equalsIgnoreCase(str)) str = "0 * * * *";
        // Individual fields
        StringTokenizer st = new StringTokenizer(str);
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        Matcher minute = Matcher.parseMinute(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        Matcher hour = Matcher.parseHour(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        Matcher dayOfMonth = Matcher.parseDayOfMonth(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        Matcher month = Matcher.parseMonth(st.nextToken());
        if(!st.hasMoreTokens()) throw new IllegalArgumentException();
        Matcher dayOfWeek = Matcher.parseDayOfWeek(st.nextToken());
        if(st.hasMoreTokens()) throw new IllegalArgumentException();
        return new MatcherSchedule(minute, hour, dayOfMonth, month, dayOfWeek);
    }

    private final Matcher minute;
    private final Matcher hour;
    private final Matcher dayOfMonth;
    private final Matcher month;
    private final Matcher dayOfWeek;

    public MatcherSchedule(
        Matcher minute,
        Matcher hour,
        Matcher dayOfMonth,
        Matcher month,
        Matcher dayOfWeek
    ) {
        this.minute = minute;
        this.hour = hour;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
    }

    public Matcher getMinute() {
        return minute;
    }

    public Matcher getHour() {
        return hour;
    }

    public Matcher getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Note: months are 1-12 like cron, not 0-11 like Calendar.
     */
    public Matcher getMonth() {
        return month;
    }

    /**
     * Note: Sunday is 0, not 1 like Calendar.
     */
    public Matcher getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public boolean isCronJobScheduled(int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
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
