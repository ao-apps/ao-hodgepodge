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

/**
 * A job is scheduled by matching any of a provided set of schedules.
 *
 * @author  AO Industries, Inc.
 */
public class MultiSchedule implements Schedule {

    private final Iterable<Schedule> schedules;

    public MultiSchedule(Iterable<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public boolean isCronJobScheduled(int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
        for(Schedule schedule : schedules) {
            if(schedule.isCronJobScheduled(minute, hour, dayOfMonth, month, dayOfWeek, year)) return true;
        }
        return false;
    }
}
