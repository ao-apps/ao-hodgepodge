package com.aoindustries.cron;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
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

    CronDaemonThread(CronJob cronJob, ErrorHandler errorHandler, int minute, int hour, int dayOfMonth, int month, int dayOfWeek) {
        super(cronJob.getCronJobName());
        Profiler.startProfile(Profiler.INSTANTANEOUS, CronDaemonThread.class, "<init>(CronJob)", null);
        try {
            this.cronJob=cronJob;
            this.errorHandler=errorHandler;
            this.minute=minute;
            this.hour=hour;
            this.dayOfMonth=dayOfMonth;
            this.month=month;
            this.dayOfWeek=dayOfWeek;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    /**
     * For internal API use only.
     */
    public void run() {
        Profiler.startProfile(Profiler.UNKNOWN, CronDaemonThread.class, "run()", cronJob.getCronJobName());
        try {
            try {
                try {
                    cronJob.runCronJob(minute, hour, dayOfMonth, month, dayOfWeek);
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
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}
