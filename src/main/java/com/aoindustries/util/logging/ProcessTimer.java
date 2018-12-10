/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2018  AO Industries, Inc.
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

// TODO: Rather than implementing Runnable and occupying a thread while waiting,
//       this could take advantage of the ao-concurrent delayed executor functionality,
//       but this would have to be moved to a different project to avoid picking-up
//       a dependency.

// Java 1.7: become AutoCloseable
public class ProcessTimer implements Runnable {

	final private Logger logger;
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
		String sourceClass,
		String sourceMethod,
		String subject,
		String processDescription,
		long maximumTime,
		long reminderInterval
	) {
		this.logger = logger;
		this.sourceClass = sourceClass;
		this.sourceMethod = sourceMethod;
		this.subject=subject;
		this.processDescription=processDescription;
		this.startTime=System.currentTimeMillis();
		this.maximumTime=maximumTime;
		this.reminderInterval=reminderInterval;
	}

	/**
	 *
	 * @see  #ProcessTimer(java.util.logging.Logger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, long)
	 *
	 * @deprecated  The {@link Random} instance is unused.  Please use {@link #ProcessTimer(java.util.logging.Logger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, long)} instead.
	 */
	@Deprecated
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
		this(logger, sourceClass, sourceMethod, subject, processDescription, maximumTime, reminderInterval);
	}

	public void finished() {
		isFinished = true;
		if(isSleeping) {
			Thread T=thread;
			if(T!=null) T.interrupt();
		}
	}

	@Override
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
		long currentTime = System.currentTimeMillis();
		if(logger.isLoggable(Level.INFO)) logger.logp(Level.INFO, sourceClass, sourceMethod, subject+": Process="+processDescription+", Duration="+StringUtility.getTimeLengthString(currentTime-startTime));
	}
}
