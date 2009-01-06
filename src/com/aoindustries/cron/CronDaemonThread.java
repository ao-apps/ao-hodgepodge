package com.aoindustries.cron;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;

/**
 * Each <code>CronJob</code> is ran in a separate <code>CronDaemonThread</code>.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class CronDaemonThread extends Thread {
    
    final CronJob cronJob;
    final ErrorHandler errorHandler;
    final int minute;
    final int hour;
    final int dayOfMonth;
    final int month;
    final int dayOfWeek;
    final int year;
    
    CronDaemonThread(CronJob cronJob, ErrorHandler errorHandler, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
        super(cronJob.getCronJobName());
        this.cronJob=cronJob;
        this.errorHandler=errorHandler;
        this.minute=minute;
        this.hour=hour;
        this.dayOfMonth=dayOfMonth;
        this.month=month;
        this.dayOfWeek=dayOfWeek;
        this.year = year;
    }
    
    /**
     * For internal API use only.
     */
    public void run() {
        try {
            try {
                cronJob.runCronJob(minute, hour, dayOfMonth, month, dayOfWeek, year);
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                Object[] extraInfo={
                    "cron_job.name="+cronJob.getCronJobName()
                };
                errorHandler.reportError(T, extraInfo);
            }
        } finally {
            CronDaemon.threadDone(this);
        }
    }
}
