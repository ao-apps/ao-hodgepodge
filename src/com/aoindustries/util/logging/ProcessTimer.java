package com.aoindustries.util.logging;

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
 * and logs info when the task takes too long.
 *
 * This should be submitted to an <code>ExecutorService</code>, but probably
 * one that is unbounded and prepared for long-running tasks.
 *
 * @author  AO Industries, Inc.
 */
public class ProcessTimer implements Runnable {

    final private Logger logger;
    final private Random random;
    final private String sourceClass;
    final private String sourceMethod;
    final private String subject;
    final private String processDescription;
    final private long startTime;
    final private long maximumTime;
    final private long reminderInterval;
    private volatile Thread thread;
    private volatile boolean isFinished;
    private volatile boolean isSleeping;

    public ProcessTimer(
        Logger logger,
        Random random,
        String sourceClass,
        String sourceMethod,
        String subject,
        String processDescription,
        long maximumTime,
        long reminderInterval
    ) {
        this.logger = logger;
        this.random=random;
        this.sourceClass = sourceClass;
        this.sourceMethod = sourceMethod;
        this.subject=subject;
        this.processDescription=processDescription;
        this.startTime=System.currentTimeMillis();
        this.maximumTime=maximumTime;
        this.reminderInterval=reminderInterval;
    }

    public void finished() {
        isFinished = true;
        if(isSleeping) {
            Thread T=thread;
            if(T!=null) T.interrupt();
        }
    }

    public void run() {
        thread=Thread.currentThread();
        try {
            // Initial delay
            try {
                isSleeping=true;
                Thread.sleep(maximumTime);
            } catch(InterruptedException err) {
                // Only normal when finish is called
                if(!isFinished) logger.log(Level.WARNING, "Interrupted when not finished", err);
            }
            isSleeping=false;
            if(!isFinished) {
                logInfo(false);
                // Reminder loop
                while(!isFinished) {
                    try {
                        isSleeping=true;
                        Thread.sleep(reminderInterval);
                    } catch(InterruptedException err) {
                        // Only normal when finish is called
                        if(!isFinished) logger.log(Level.WARNING, "Interrupted when not finished", err);
                    }
                    isSleeping=false;
                    if(!isFinished) logInfo(true);
                }
            }
        } finally {
            thread = null;
        }
    }

    private void logInfo(boolean isReminder) {
        long currentTime=System.currentTimeMillis();
        logger.logp(Level.INFO, sourceClass, sourceMethod, subject+": Process="+processDescription+", Duration="+StringUtility.getTimeLengthString(currentTime-startTime));
    }
}