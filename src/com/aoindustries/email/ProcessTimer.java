package com.aoindustries.email;

/*
 * Copyright 2002-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import com.oreilly.servlet.*;
import java.io.*;
import java.util.*;

/**
 * A <code>ProcessTimer</code> monitors how long something takes,
 * and notifies administrative personnel via email when the task
 * takes too long.
 *
 * @author  AO Industries, Inc.
 */
public class ProcessTimer implements Runnable {
    
    final private Random random;
    final private String smtpServer;
    final private String from;
    final private String toList;
    final private String subject;
    final private String processDescription;
    final private long startTime;
    final private long maximumTime;
    final private long reminderInterval;
    private Thread thread;
    private boolean isSleeping;

    public ProcessTimer(
        Random random,
        String smtpServer,
        String from,
        String toList,
        String subject,
        String processDescription,
        long maximumTime,
        long reminderInterval
    ) {
        this.random=random;
        this.smtpServer=smtpServer;
        this.from=from;
        this.toList=toList;
        this.subject=subject;
        this.processDescription=processDescription;
        this.startTime=System.currentTimeMillis();
        this.maximumTime=maximumTime;
        this.reminderInterval=reminderInterval;
    }
    
    public void start() {
        synchronized(this) {
            if(thread==null) {
                (thread=new Thread(this)).start();
            }
        }
    }
    
    public void stop() {
        synchronized(this) {
            Thread T=thread;
            if(T!=null) {
                thread=null;
                if(isSleeping) T.interrupt();
            }
        }
    }
    
    public void run() {
        try {
            isSleeping=true;
            Thread.sleep(maximumTime);
            isSleeping=false;
            if(thread==Thread.currentThread()) {
                sendEmail(false);
                while(thread==Thread.currentThread()) {
                    isSleeping=true;
                    Thread.sleep(reminderInterval);
                    isSleeping=false;
                    if(thread==Thread.currentThread()) {
                        sendEmail(true);
                    }
                }
            }
        } catch(InterruptedException err) {
            // Normal when the run method is stopping
        }
    }
    
    private void sendEmail(boolean isReminder) {
        long currentTime=System.currentTimeMillis();
        StringBuilder SB=new StringBuilder();
        SB.append("Desc.: ").append(processDescription).append("\n"
                + "Start: ").append(SQLUtility.getDateTime(startTime)).append("\n"
                + "Maximum Duration: ").append(StringUtility.getTimeLengthString(currentTime-startTime)).append("\n");
        String message=SB.toString();
        System.err.println(message);
        List<String> addys=StringUtility.splitStringCommaSpace(toList);
        for(int c=0;c<addys.size();c++) {
            ErrorMailer.reportError(random, message, smtpServer, from, addys.get(c), subject);
        }
    }
}