package com.aoindustries.email;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import com.oreilly.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An error emailer will email errors and
 * print a stack trace to System.err.  Emails will not
 * be sent more than once per minute, however,
 * to avoid flooding email systems.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public final class ErrorMailer {

    public static final int MINIMUM_PERIOD=15*60*1000;

    public static final int MAX_MESSAGE_LENGTH=160;

    /**
     * The last time email was sent to each address is stored
     * so that fast occuring errors will not create many emails.
     */
    private static final Map<String,Long> lastTimes=new HashMap<String,Long>();

    /**
     * Sends email messages to <code>address</code>, limiting the frequency of messages.
     */
    public static void reportError(Random random, String message, String smtpServer, String from, String address, String subject) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ErrorMailer.class, "reportError(Random,String,String,String,String,String)", null);
        try {
            emailError(random, message, smtpServer, from, address, subject);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    public static void reportError(Random random, Throwable T, Object[] extraInfo, String smtpServer, String from, String address, String subject) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ErrorMailer.class, "reportError(Random,Throwable,Object[],String,String,String,String)", null);
        try {
            emailError(random, new Object[] {T, extraInfo}, smtpServer, from, address, subject);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    public static void emailError(Random random, String message, String smtpServer, String from, String address, String subject) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ErrorMailer.class, "emailError(Random,String,String,String,String,String)", null);
        try {
            emailError(random, (Object)message, smtpServer, from, address, subject);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    private static void emailError(Random random, Object messageObject, String smtpServer, String from, String address, String subject) {
        Profiler.startProfile(Profiler.IO, ErrorMailer.class, "emailError(Random,Object,String,String,String,String)", null);
        try {
            boolean send=false;
	    synchronized(ErrorMailer.class) {
		Long lastTime=lastTimes.get(address);
		if(
		   lastTime==null
		   || (System.currentTimeMillis()-lastTime.longValue())>=MINIMUM_PERIOD
                ) {
                    send=true;
		    lastTimes.put(address, Long.valueOf(System.currentTimeMillis()));
                }
            }
            if(send) {
                try {
                    String[] orAddys=StringUtility.splitString(address, '|');
                    if(orAddys.length>0) {
                        // Email the message
                        MailMessage msg=new MailMessage(smtpServer);
                        msg.from(from);
                        String to = orAddys[random.nextInt(orAddys.length)];
                        msg.to(to);
                        msg.setSubject(subject);
                        PrintStream email=msg.getPrintStream();
                        // Figure out if the email is going to a cell phone
                        boolean isCell =
                            to.length() > 11
                            && to.charAt(0) >='0' && to.charAt(0) <= '9'
                            && to.charAt(1) >='0' && to.charAt(1) <= '9'
                            && to.charAt(2) >='0' && to.charAt(2) <= '9'
                            && to.charAt(3) >='0' && to.charAt(3) <= '9'
                            && to.charAt(4) >='0' && to.charAt(4) <= '9'
                            && to.charAt(5) >='0' && to.charAt(5) <= '9'
                            && to.charAt(6) >='0' && to.charAt(6) <= '9'
                            && to.charAt(7) >='0' && to.charAt(7) <= '9'
                            && to.charAt(8) >='0' && to.charAt(8) <= '9'
                            && to.charAt(9) >='0' && to.charAt(9) <= '9'
                            && to.charAt(10) == '@'
                        ;
                        if(isCell) {
                            // Print abbreviate message
                            String message;
                            if(messageObject instanceof Object[]) message = ((Object[])messageObject)[0].toString();
                            else message = messageObject.toString();
                            if(message.length()>MAX_MESSAGE_LENGTH) email.print(message.substring(0, MAX_MESSAGE_LENGTH));
                            else email.print(message);
                        } else {
                            // Print full message
                            if(messageObject instanceof Object[]) {
                                Object[] OA = (Object[])messageObject;
                                ErrorPrinter.printStackTraces((Throwable)OA[0], email, (Object[])OA[1]);
                            } else email.print(messageObject.toString());
                        }
                        msg.sendAndClose();
                    }
                } catch(IOException err) {
                    ErrorPrinter.printStackTraces(
                        err,
                        new Object[] {
                            "smtpServer="+smtpServer,
                            "from="+from,
                            "address="+address,
                            "subject="+subject
                        }
                    );
                }
	    }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
