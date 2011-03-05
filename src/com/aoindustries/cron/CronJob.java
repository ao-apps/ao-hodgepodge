/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
 * One task that is performed on a routine basis.
 *
 * @author  AO Industries, Inc.
 */
public interface CronJob {

    /**
     * Determine if the job should run right now.
     *
     * @param minute 0-59
     * @param hour 0-23
     * @param dayOfMonth 1-31
     * @param month 0-11
     * @param dayOfWeek 1-7, <code>Calendar.SUNDAY</code> through <code>Calendar.SATURDAY</code>
     */
    boolean isCronJobScheduled(int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year);
    
    /**
     * Gets the job scheduling mode.
     *
     * @see  CronJobScheduleMode
     */
    CronJobScheduleMode getCronJobScheduleMode();
    
    /**
     * Gets the name for this cron job
     */
    String getCronJobName();
    
    /**
     * Performs the scheduled task.
     *
     * @see #isCronJobScheduled
     */
    void runCronJob(int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year);
    
    /**
     * Gets the Thread priority for this job.  Previously defaulted to <code>Thread.NORM_PRIORITY-2</code>
     *
     * @see  Thread#setPriority
     */
    int getCronJobThreadPriority();
}
