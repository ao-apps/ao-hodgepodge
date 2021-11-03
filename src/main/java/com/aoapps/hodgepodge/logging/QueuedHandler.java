/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2018, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * <p>
 * An implementation of {@link Handler} that queues log records and handles
 * them in the background.  The log records are processed in the order received,
 * regardless of level.
 * </p>
 * <p>
 * Configures itself similar to {@link ConsoleHandler} via
 * {@link HandlerUtil#configure(java.util.logging.Handler)}.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class QueuedHandler extends Handler {

	/**
	 * Creates a new executor.  Must be {@linkplain #shutdownExecutor(java.util.concurrent.ExecutorService) shutdown} when no longer needed.
	 */
	protected static ExecutorService newExecutor(String executorThreadName) {
		return Executors.newSingleThreadExecutor(r -> {
			Thread thread = new Thread(r);
			thread.setName(executorThreadName);
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		});
	}

	/**
	 * Shuts down the executor, waiting up to one minute for tasks to complete.
	 */
	protected static void shutdownExecutor(ExecutorService executor) throws SecurityException {
		executor.shutdown();
		try {
			// Wait up to one minute to complete its tasks
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch(InterruptedException err) {
			// Restore the interrupted status
			Thread.currentThread().interrupt();
		}
	}

	private final ExecutorService executor;
	private final boolean isOwnExecutor;

	/**
	 * Manages the queue internally.
	 */
	protected QueuedHandler(String executorThreadName) {
		executor = newExecutor(executorThreadName);
		isOwnExecutor = true;
		HandlerUtil.configure(this);
	}

	/**
	 * Uses the provided executor.  The executor is
	 * not {@linkplain ExecutorService#shutdown() shutdown} on {@link #close()};
	 * it is up to the caller to manage the executor lifecycle.
	 *
	 * @see #newExecutor(java.lang.String)
	 * @see #shutdownExecutor(java.util.concurrent.ExecutorService)
	 */
	protected QueuedHandler(ExecutorService executor) {
		this.executor = executor;
		this.isOwnExecutor = false;
		HandlerUtil.configure(this);
	}

	/**
	 * @see StreamHandler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		// Don't queue if not needed
		if(isLoggable(record)) {
			// Format first to have correct threading information
			Formatter formatter;
			String msg;
			try {
				formatter = getFormatter();
				msg = formatter.format(record);
			} catch(Exception ex) {
				// We don't want to throw an exception here, but we
				// report the exception to any registered ErrorManager.
				reportError(null, ex, ErrorManager.FORMAT_FAILURE);
				return;
			}
			// Queue for custom action
	        try {
				executor.submit(
					() -> {
						try {
							backgroundPublish(
								formatter,
								record,
								msg
							);
						} catch (Exception ex) {
							// We don't want to throw an exception here, but we
							// report the exception to any registered ErrorManager.
							reportError(null, ex, ErrorManager.WRITE_FAILURE);
						}
					}
				);
			} catch (Exception ex) {
				// We don't want to throw an exception here, but we
				// report the exception to any registered ErrorManager.
				reportError(null, ex, ErrorManager.GENERIC_FAILURE);
			}
		}
	}

	/**
	 * TODO: Could wait until all queued log records up to this moment have been handled.
	 * If new log records are added while we wait, don't wait for them.
	 */
	@Override
	@SuppressWarnings("NoopMethodInAbstractClass")
	public void flush() {
		// Do nothing
	}

	@Override
	public void close() throws SecurityException {
		if(isOwnExecutor) shutdownExecutor(executor);
	}

	/**
	 * This is called in a background thread.
	 *
	 * @param formatter  the formatter at the time the record was queued
	 * @param record     the queued record
	 * @param fullReport the complete message generated by the formatter before
	 *                    the record was queued.  The message is generated before
	 *                    so it can have accurate thread and time information.
	 */
	protected abstract void backgroundPublish(Formatter formatter, LogRecord record, String fullReport) throws Exception;
}
