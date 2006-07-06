package com.aoindustries.cron;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;

/**
 * One task that is performed on a routine basis.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public interface CronJob {

    /**
     * Indicates the jobs should be ran concurrently when running together.
     */
    int CRON_JOB_SCHEDULE_CONCURRENT=0;
    
    /**
     * Indicates the new job should be skipped to avoid running the same job concurrently.
     */
    int CRON_JOB_SCHEDULE_SKIP=1;
    
    /**
     * Determine if the job should run right now.
     *
     * @param minute 0-59
     * @param hour 0-23
     * @param dayOfMonth 1-31
     * @param month 1-12
     * @param dayOfWeek 1-7, <code>Calendar.SUNDAY</code> through <code>Calendar.SATURDAY</code>
     */
    boolean isCronJobScheduled(int minute, int hour, int dayOfMonth, int month, int dayOfWeek);
    
    /**
     * Gets the job scheduling mode.
     *
     * @see  #CRON_JOB_SCHEDULE_CONCURRENT
     * @see  #CRON_JOB_SCHEDULE_SKIP
     */
    int getCronJobScheduleMode();
    
    /**
     * Gets the name for this cron job
     */
    String getCronJobName();
    
    /**
     * Performs the scheduled task.
     */
    void runCronJob(int minute, int hour, int dayOfMonth, int month, int dayOfWeek);
    
    /**
     * Gets the Thread priority for this job.  Previously defaulted to <code>Thread.NORM_PRIORITY-2</code>
     *
     * @see  Thread#setPriority
     */
    int getCronJobThreadPriority();
}
