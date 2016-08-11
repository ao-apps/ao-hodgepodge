/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
package com.aoindustries.lang;

/**
 * Runtime utilities that enhance behavior of java.lang.Runtime.
 */
final public class RuntimeUtils {

	/**
	 * Make no instances.
	 */
	private RuntimeUtils() {}

	private static final Object availableProcessorsLock = new Object();
	private static long availableProcessorsLastRetrieved = Long.MIN_VALUE;
	private static int availableProcessors = 0;

	/**
	 * Faster way to get the number of processors in the system.
	 * <p>
	 * The call the Runtime.availableProcessors is prohibitively slow (at least
	 * in Java 1.6 on Debian 6).  The number of processors in a system is unlikely
	 * to change frequently.  This will only call Runtime.availableProcessors
	 * once a second.
	 * </p>
	 */
	public static int getAvailableProcessors() {
		long currentTime = System.currentTimeMillis();
		synchronized(availableProcessorsLock) {
			long timeSince;
			if(
				availableProcessors==0
				|| (timeSince = availableProcessorsLastRetrieved - currentTime) >= 1000
				|| timeSince <= -1000 // System time set to the past
			) {
				availableProcessors = Runtime.getRuntime().availableProcessors();
				availableProcessorsLastRetrieved = currentTime;
			}
			return availableProcessors;
		}
	}
}
