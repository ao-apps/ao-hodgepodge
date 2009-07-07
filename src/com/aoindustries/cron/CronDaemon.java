package com.aoindustries.cron;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Run cron jobs based on their scheduling requirements.  Once per minute
 * it polls each cron job and runs it if it is currently scheduled.
 *
 * TODO: Make sure that it will run once per minute, even if delayed more than a minute?
 * TODO: Use a user-provided ExecutorService, have aoserv-daemon use same executor.
 *           Also have aoserv-client allow user-provided executor service, use same executor.
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
    private static Logger logger = Logger.getLogger(CronDaemon.class.getName());

    /**
     * Sets the logger for the cron daemon errors (not the individual cron jobs).
     */
    public static void setLogger(Logger logger) {
        CronDaemon.logger = logger;
    }

    /**
     * Once started, this thread will run forever.
     */
    private static CronDaemon runningDaemon;

    private static final List<CronJob> cronJobs=new ArrayList<CronJob>();
    private static final List<Logger> loggers=new ArrayList<Logger>();
    private static final List<CronDaemonThread> runningJobs=new ArrayList<CronDaemonThread>();
    
    /**
     * Adds a <code>CronJob</code> to the list of jobs.
     */
    synchronized public static void addCronJob(CronJob job, Logger logger) {
        cronJobs.add(job);
        loggers.add(logger);
        if(runningDaemon==null) {
            runningDaemon=new CronDaemon();
            runningDaemon.setPriority(Thread.MAX_PRIORITY);
            runningDaemon.setDaemon(true);
            runningDaemon.start();
        }
    }

    @Override
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
                logger.log(Level.SEVERE, null, T);
            }
            try {
                Thread.sleep(30000);
            } catch(InterruptedException err) {
                logger.log(Level.WARNING, null, err);
            }
        }
    }
    
    /**
     * Runs the jobs for the current minute, returns the next time to run.
     */
    synchronized private static long runJobs() {
        Calendar cal=Calendar.getInstance();
        int minute=cal.get(Calendar.MINUTE);
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth=cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH)+1;
        int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        for(int c=0;c<cronJobs.size();c++) {
            CronJob job=cronJobs.get(c);
            Logger jobLogger=loggers.get(c);
            try {
                if(job.isCronJobScheduled(minute, hour, dayOfMonth, month, dayOfWeek, year)) {
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
                        CronDaemonThread thread=new CronDaemonThread(job, jobLogger, minute, hour, dayOfMonth, month, dayOfWeek, year);
                        thread.setDaemon(false);
                        thread.setPriority(job.getCronJobThreadPriority());
                        runningJobs.add(thread);
                        thread.start();
                    }
                }
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                jobLogger.log(Level.SEVERE, "cron_job.name="+job.getCronJobName(), T);
            }
        }
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    synchronized static void threadDone(CronDaemonThread thread) {
        for(int c=0;c<runningJobs.size();c++) {
            if(runningJobs.get(c)==thread) {
                runningJobs.remove(c);
                return;
            }
        }
        Throwable T=new Throwable("Warning: thread not found on threadDone(CronDaemonThread)");
        thread.logger.log(Level.WARNING, "cron_job.name="+thread.cronJob.getCronJobName(), T);
    }
}
