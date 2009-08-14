/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.email;

import com.aoindustries.util.*;
import com.oreilly.servlet.*;
import java.io.*;
import java.util.*;

/**
 * An error emailer will email errors and
 * print a stack trace to System.err.  Emails will not
 * be sent more than once per minute, however,
 * to avoid flooding email systems.
 *
 * @author  AO Industries, Inc.
 *
 * @deprecated  Please use standard java logging api.
 */
@Deprecated
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
        emailError(random, message, smtpServer, from, address, subject);
    }

    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    public static void reportError(Random random, Throwable T, Object[] extraInfo, String smtpServer, String from, String address, String subject) {
        emailError(random, new Object[] {T, extraInfo}, smtpServer, from, address, subject);
    }

    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    public static void emailError(Random random, String message, String smtpServer, String from, String address, String subject) {
        emailError(random, (Object)message, smtpServer, from, address, subject);
    }
    /**
     * Sends an email message to <code>address</code>, limiting the frequency of messages.
     */
    private static void emailError(Random random, Object messageObject, String smtpServer, String from, String address, String subject) {
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
    }
}
