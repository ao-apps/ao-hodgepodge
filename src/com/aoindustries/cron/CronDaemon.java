package com.aoindustries.cron;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.util.*;

/**
 * Run cron jobs based on their scheduling requirements.  Once per minute
 * it polls each cron job and runs it if it is currently scheduled.
 *
 * @see  CronJob
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public final class CronDaemon extends Thread {
    
    /**
     * The cron daemon errors will be reported here (not the individual cron jobs).
     */
    private static ErrorHandler errorHandler=new StandardErrorHandler();

    /**
     * Sets the error handler for the cron daemon errors (not the individual cron jobs).
     */
    public static void setErrorHandler(ErrorHandler eh) {
        errorHandler=eh;
    }

    /**
     * Once started, this thread will run forever.
     */
    private static CronDaemon runningDaemon;

    private static final List<CronJob> cronJobs=new ArrayList<CronJob>();
    private static final List<ErrorHandler> errorHandlers=new ArrayList<ErrorHandler>();
    private static final List<CronDaemonThread> runningJobs=new ArrayList<CronDaemonThread>();
    
    /**
     * @deprecated  Please use <code>addCronJob(CronJob,ErrorHandler)</code>.
     *
     * @see  #addCronJob(CronJob,ErrorHandler)
     */
    public static void addCronJob(CronJob job) {
        addCronJob(job, new StandardErrorHandler());
    }

    /**
     * Adds a <code>CronJob</code> to the list of jobs.
     */
    synchronized public static void addCronJob(CronJob job, ErrorHandler errorHandler) {
        Profiler.startProfile(Profiler.FAST, CronDaemon.class, "addCronJob(CronJob,ErrorHandler)", null);
        try {
            cronJobs.add(job);
            errorHandlers.add(errorHandler);
            if(runningDaemon==null) {
                runningDaemon=new CronDaemon();
                runningDaemon.setPriority(Thread.MAX_PRIORITY);
                runningDaemon.setDaemon(true);
                runningDaemon.start();
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void run() {
        while(true) {
            try {
                while(true) {
                    long nextRun=runJobs();
                    long sleepTime=nextRun-System.currentTimeMillis();
                    if(sleepTime>0) sleep(sleepTime);
                }
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                errorHandler.reportError(T, null);
            }
            try {
                Thread.sleep(30000);
            } catch(InterruptedException err) {
                errorHandler.reportWarning(err, null);
            }
        }
    }
    
    /**
     * Runs the jobs for the current minute, returns the next time to run.
     */
    synchronized private static long runJobs() {
        Profiler.startProfile(Profiler.FAST, CronDaemon.class, "runJobs()", null);
        try {
            Calendar cal=Calendar.getInstance();
            int minute=cal.get(Calendar.MINUTE);
            int hour=cal.get(Calendar.HOUR_OF_DAY);
            int dayOfMonth=cal.get(Calendar.DAY_OF_MONTH);
            int month=cal.get(Calendar.MONTH)+1;
            int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
            for(int c=0;c<cronJobs.size();c++) {
                CronJob job=cronJobs.get(c);
                ErrorHandler jobErrorHandler=errorHandlers.get(c);
                try {
                    if(job.isCronJobScheduled(minute, hour, dayOfMonth, month, dayOfWeek)) {
                        int scheduleMode=job.getCronJobScheduleMode();
                        boolean run;
                        if(scheduleMode==CronJob.CRON_JOB_SCHEDULE_SKIP) {
                            // Skip if already running
                            boolean found=false;
                            for(CronDaemonThread runningJob : runningJobs) {
                                if(runningJob.cronJob==job) {
                                    found=true;
                                    break;
                                }
                            }
                            run=!found;
                        } else if(scheduleMode==CronJob.CRON_JOB_SCHEDULE_CONCURRENT) {
                            run=true;
                        } else throw new RuntimeException("Unknown value from CronJob.getCronJobScheduleMode: "+scheduleMode);
                        if(run) {
                            CronDaemonThread thread=new CronDaemonThread(job, jobErrorHandler, minute, hour, dayOfMonth, month, dayOfWeek);
                            thread.setDaemon(false);
                            thread.setPriority(job.getCronJobThreadPriority());
                            runningJobs.add(thread);
                            thread.start();
                        }
                    }
                } catch(ThreadDeath TD) {
                    throw TD;
                } catch(Throwable T) {
                    Object[] extraInfo=new Object[] {"cron_job.name="+job.getCronJobName()};
                    jobErrorHandler.reportError(T, extraInfo);
                }
            }
            cal.add(Calendar.MINUTE, 1);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    synchronized static void threadDone(CronDaemonThread thread) {
        for(int c=0;c<runningJobs.size();c++) {
            if(runningJobs.get(c)==thread) {
                runningJobs.remove(c);
                return;
            }
        }
        Throwable T=new Throwable("Warning: thread not found on threadDone(CronDaemonThread)");
        Object[] extraInfo={
            "cron_job.name="+thread.cronJob.getCronJobName()
        };
        thread.errorHandler.reportWarning(T, extraInfo);
    }
}
