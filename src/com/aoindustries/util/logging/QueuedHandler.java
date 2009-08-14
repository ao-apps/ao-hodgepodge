/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * <p>
 * An implementation of <code>Handler</code> that queues log records and handles
 * them in the background.  Two actions are taken for each record, each in a
 * separate background process.  The first writes the <code>System.err</code>
 * with a high priority.  The second is an implementation-defined logging method.
 * The log records are processed in the order received, regardless of level.
 * </p>
 * <p>
 * Defaults to using ErrorPrinterFormatter.
 * </p>
 * 
 * @see ErrorPrinterFormatter
 * 
 * @author  AO Industries, Inc.
 */
abstract public class QueuedHandler extends Handler {

    private final ExecutorService consoleExecutor;
    private final ExecutorService customExecutor;

    protected QueuedHandler(final String consoleExecutorThreadName, final String customExecutorThreadName) {
        setFormatter(ErrorPrinterFormatter.getInstance());
        consoleExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(consoleExecutorThreadName);
                    thread.setDaemon(true);
                    thread.setPriority(Thread.NORM_PRIORITY+1);
                    return thread;
                }
            }
        );
        customExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(customExecutorThreadName);
                    thread.setDaemon(true);
                    thread.setPriority(Thread.NORM_PRIORITY-1);
                    return thread;
                }
            }
        );
    }

    @Override
    final public void publish(final LogRecord record) {
        // Call getSourceClassName and getSourceMethodName to set their values before the background processing.
        //record.getSourceClassName();
        //record.getSourceMethodName();

        // Format first to have correct threading information
        final Formatter formatter = getFormatter();
        final String fullReport = formatter.format(record);

        // Queue for System.err output
        consoleExecutor.submit(
            new Runnable() {
                @Override
                public void run() {
                    synchronized(System.err) {
                        System.err.print(fullReport);
                    }
                }
            }
        );
        // Queue for custom action, don't queue if not needed
        if(useCustomLogging(record)) {
            customExecutor.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        doCustomLogging(formatter, record, fullReport);
                    }
                }
            );
        }
    }

    @Override
    public void flush() {
        System.err.flush();
        // Alternately, could wait until all queued log records up to this moment have been handled.  If
        // no log records are added while we wait, don't wait for them.
    }

    @Override
    public void close() throws SecurityException {
        consoleExecutor.shutdown();
        customExecutor.shutdown();
        try {
            // Wait up to one minute for System.err to complete its tasks
            // NoSuchFieldError in Java 1.5: consoleExecutor.awaitTermination(1, TimeUnit.MINUTES);
            consoleExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch(InterruptedException err) {
            // Ignored
        }
        try {
            // Wait up to one minute for tickets to complete its tasks
            // NoSuchFieldError in Java 1.5: consoleExecutor.awaitTermination(1, TimeUnit.MINUTES);
            consoleExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch(InterruptedException err) {
            // Ignored
        }
    }

    /**
     * The log record will only be queued when this returns <code>true</code>.
     */
    protected abstract boolean useCustomLogging(LogRecord record);

    /**
     * This is called in a background Thread.
     *
     * @param formatter  the formatter at the time the record was queued
     * @param record     the queued record
     * @param fullReport the complete message generated by the formatter before
     *                    the record was queued.  The message is generated before
     *                    so it can have accurate thread and time information.
     */
    protected abstract void doCustomLogging(Formatter formatter, LogRecord record, String fullReport);
}
