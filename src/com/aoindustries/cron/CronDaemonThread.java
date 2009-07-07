package com.aoindustries.cron;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Each <code>CronJob</code> is ran in a separate <code>CronDaemonThread</code>.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class CronDaemonThread extends Thread {
    
    final CronJob cronJob;
    final Logger logger;
    final int minute;
    final int hour;
    final int dayOfMonth;
    final int month;
    final int dayOfWeek;
    final int year;
    
    CronDaemonThread(CronJob cronJob, Logger logger, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
        super(cronJob.getCronJobName());
        this.cronJob=cronJob;
        this.logger=logger;
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
    @Override
    public void run() {
        try {
            try {
                cronJob.runCronJob(minute, hour, dayOfMonth, month, dayOfWeek, year);
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                logger.log(Level.SEVERE, "cron_job.name="+cronJob.getCronJobName(), T);
            }
        } finally {
            CronDaemon.threadDone(this);
        }
    }
}
