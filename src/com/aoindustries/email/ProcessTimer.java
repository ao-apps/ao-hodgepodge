package com.aoindustries.email;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.StringUtility;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A <code>ProcessTimer</code> monitors how long something takes,
 * and logs warnings when the task takes too long.
 *
 * This should be submitted to an <code>ExecutorService</code>, but probably
 * one that is unbounded and prepared for long-running tasks.
 *
 * @author  AO Industries, Inc.
 */
public class ProcessTimer implements Runnable {

    final private Logger logger;
    final private Random random;
    final private String subject;
    final private String processDescription;
    final private long startTime;
    final private long maximumTime;
    final private long reminderInterval;
    private Thread thread;
    private boolean isSleeping;

    public ProcessTimer(
        Logger logger,
        Random random,
        String subject,
        String processDescription,
        long maximumTime,
        long reminderInterval
    ) {
        this.logger = logger;
        this.random=random;
        this.subject=subject;
        this.processDescription=processDescription;
        this.startTime=System.currentTimeMillis();
        this.maximumTime=maximumTime;
        this.reminderInterval=reminderInterval;
    }
    
    public void finished() {
        synchronized(this) {
            Thread T=thread;
            if(T!=null) {
                thread=null;
                if(isSleeping) T.interrupt();
            }
        }
    }
    
    public void run() {
        synchronized(this) {
            thread=Thread.currentThread();
        }
        try {
            isSleeping=true;
            Thread.sleep(maximumTime);
            isSleeping=false;
            if(thread==Thread.currentThread()) {
                logWarning(false);
                while(thread==Thread.currentThread()) {
                    isSleeping=true;
                    Thread.sleep(reminderInterval);
                    isSleeping=false;
                    if(thread==Thread.currentThread()) {
                        logWarning(true);
                    }
                }
            }
        } catch(InterruptedException err) {
            // Normal when the run method is stopping
        }
    }
    
    private void logWarning(boolean isReminder) {
        long currentTime=System.currentTimeMillis();
        logger.log(Level.WARNING, subject+": Process="+processDescription+", Duration="+StringUtility.getTimeLengthString(currentTime-startTime));
    }
}