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
 * @author  AO Industries, Inc.
 */
public final class CronDaemon {

    /**
     * The cron daemon errors will be reported here (not the individual cron jobs).
     */
    private static volatile Logger logger = Logger.getLogger(CronDaemon.class.getName());

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
     * Adds a <code>CronJob</code> to the list of jobs.  If the job is already
     * in the list, it will not be added again.
     */
    public static void addCronJob(CronJob newJob, Logger logger) {
        synchronized(cronJobs) {
            boolean found = false;
            for(CronJob cronJob : cronJobs) {
                if(cronJob==newJob) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                cronJobs.add(newJob);
                loggers.add(logger);
                if(runningDaemon==null) {
                    runningDaemon=new CronDaemon();
                    runningDaemon.start();
                }
            }
        }
    }

    /**
     * Removes a <code>CronJob</code> from the list of jobs.
     */
    public static void removeCronJob(CronJob job) {
        synchronized(cronJobs) {
            for(int i=0, len=cronJobs.size(); i<len; i++) {
                if(cronJobs.get(i)==job) {
                    cronJobs.remove(i);
                    loggers.remove(i);
                    break;
                }
            }
            if(runningDaemon!=null && cronJobs.isEmpty()) {
                runningDaemon.stop();
                runningDaemon = null;
            }
        }
    }

    private Thread thread;
    private CronDaemon() {
    }

    /**
     * Starts this daemon if it is not already running.
     */
    private void start() {
        thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        synchronized(cronJobs) {
                            if(runningDaemon!=CronDaemon.this) break;
                        }
                        try {
                            long nextRun=runJobs();
                            long sleepTime=nextRun-System.currentTimeMillis();
                            if(sleepTime>0) Thread.sleep(sleepTime);
                        } catch(ThreadDeath TD) {
                            throw TD;
                        } catch(Throwable T) {
                            logger.log(Level.SEVERE, null, T);
                            try {
                                Thread.sleep(30000);
                            } catch(InterruptedException err) {
                                logger.log(Level.WARNING, null, err);
                            }
                        }
                    }
                }
            },
            CronDaemon.class.getName()
        );
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }

    private void stop() {
        thread.interrupt();
    }

    /**
     * Runs the jobs for the current minute, returns the next time to run.
     */
    private static long runJobs() {
        Calendar cal=Calendar.getInstance();
        int minute=cal.get(Calendar.MINUTE);
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth=cal.get(Calendar.DAY_OF_MONTH);
        int month=cal.get(Calendar.MONTH);
        int dayOfWeek=cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        synchronized(cronJobs) {
            for(int i=0, size=cronJobs.size(); i<size; i++) {
                CronJob job=cronJobs.get(i);
                Logger jobLogger=loggers.get(i);
                try {
                    if(job.isCronJobScheduled(minute, hour, dayOfMonth, month, dayOfWeek, year)) {
                        CronJobScheduleMode scheduleMode=job.getCronJobScheduleMode();
                        boolean run;
                        if(scheduleMode==CronJobScheduleMode.SKIP) {
                            // Skip if already running
                            boolean found=false;
                            for(CronDaemonThread runningJob : runningJobs) {
                                if(runningJob.cronJob==job) {
                                    found=true;
                                    break;
                                }
                            }
                            run=!found;
                        } else if(scheduleMode==CronJobScheduleMode.CONCURRENT) {
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
        }
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    static void threadDone(CronDaemonThread thread) {
        synchronized(cronJobs) {
            for(int c=0;c<runningJobs.size();c++) {
                if(runningJobs.get(c)==thread) {
                    runningJobs.remove(c);
                    return;
                }
            }
        }
        thread.logger.log(Level.WARNING, "cron_job.name="+thread.cronJob.getCronJobName(), new Throwable("Warning: thread not found on threadDone(CronDaemonThread)"));
    }
}
