/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2020  AO Industries, Inc.
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

import com.aoindustries.util.ErrorPrinter;
import java.lang.reflect.Method;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * {@link LogManager} has several methods that would be very useful in setting
 * up logging, but they are package-private.  This implements the same set of
 * methods here.
 * <p>
 * This first tries to access to private methods directly through
 * {@link Method#setAccessible(boolean)}, which might give up-to-date implementations.
 * When this fails, uses an implementation copied here from Java 1.8.0_231.
 * </p>
 * 
 * @see LogManager
 * 
 * @author  AO Industries, Inc.
 */
public class LogManagerUtil {

	/**
	 * The number of warnings that can be logged before being throttled.
	 */
	private static final int WARN_BURST = 100;

	/**
	 * The number of milliseconds between warning logging after the burst is hit.
	 */
	private static final int WARN_INTERVAL = 10000;

	private static class WarnLock {}
	private static final WarnLock warnLock = new WarnLock();

	private static int burstRemaining = WARN_BURST;
	private static long lastWarned = Long.MIN_VALUE;

	/**
	 * Logs a warning, with burst and rate-limited.
	 * Does not log {@link SecurityException}, which is expected in more
	 * restricted runtimes.
	 */
	static void warn(Throwable t, Object ... extraInfo) {
		if(!(t instanceof SecurityException)) {
			synchronized(warnLock) {
				long currentTime = System.currentTimeMillis();
				boolean logNow;
				if(burstRemaining > 0) {
					burstRemaining--;
					logNow = true;
				} else {
					long timeSince = currentTime - lastWarned;
					logNow =
						timeSince >= WARN_INTERVAL
						// System time set to the past
						|| timeSince <= WARN_INTERVAL;
				}
				if(logNow) {
					ErrorPrinter.printStackTraces(t, System.err, extraInfo);
					lastWarned = currentTime;
				}
			}
		}
	}

	private static Method getStringPropertyMethod;
	static {
		try {
			Method method = LogManager.class.getDeclaredMethod("getStringProperty", String.class, String.class);
			method.setAccessible(true);
		} catch(RuntimeException | ReflectiveOperationException e) {
			warn(e);
		}
	}

	/**
	 * Package private method to get a String property.
     * If the property is not defined we return the given
     * default value.
	 */
	// @see LogManager#getStringProperty(java.lang.String, java.lang.String)
    public static String getStringProperty(LogManager manager, String name, String defaultValue) {
		if(getStringPropertyMethod != null) {
			try {
				return (String)getStringPropertyMethod.invoke(manager, name, defaultValue);
			} catch(RuntimeException | ReflectiveOperationException e) {
				warn(e);
				// Fall-through to copied implementation
			}
		}
        String val = manager.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        return val.trim();
    }

	private static Method getLevelPropertyMethod;
	static {
		try {
			Method method = LogManager.class.getDeclaredMethod("getLevelProperty", String.class, Level.class);
			method.setAccessible(true);
		} catch(RuntimeException | ReflectiveOperationException e) {
			warn(e);
		}
	}

	/**
	 * Package private method to get a Level property.
	 * If the property is not defined or cannot be parsed
	 * we return the given default value.
	 */
	// @see LogManager#getLevelProperty(java.lang.String, java.util.logging.Level)
	public static Level getLevelProperty(LogManager manager, String name, Level defaultValue) {
		if(getLevelPropertyMethod != null) {
			try {
				return (Level)getLevelPropertyMethod.invoke(manager, name, defaultValue);
			} catch(RuntimeException | ReflectiveOperationException e) {
				warn(e);
				// Fall-through to copied implementation
			}
		}
		String val = manager.getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		return Level.parse(val.trim());
	}

	private static Method getFilterPropertyMethod;
	static {
		try {
			Method method = LogManager.class.getDeclaredMethod("getFilterProperty", String.class, Filter.class);
			method.setAccessible(true);
		} catch(RuntimeException | ReflectiveOperationException e) {
			warn(e);
		}
	}

	/**
	 * Package private method to get a filter property.
	 * We return an instance of the class named by the "name"
	 * property. If the property is not defined or has problems
	 * we return the defaultValue.
	 */
	// @see LogManager#getFilterProperty(java.lang.String, java.util.logging.Filter)
	public static Filter getFilterProperty(LogManager manager, String name, Filter defaultValue) {
		if(getFilterPropertyMethod != null) {
			try {
				return (Filter)getFilterPropertyMethod.invoke(manager, name, defaultValue);
			} catch(RuntimeException | ReflectiveOperationException e) {
				warn(e);
				// Fall-through to copied implementation
			}
		}
		String val = manager.getProperty(name);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Filter) clz.newInstance();
			}
		} catch (RuntimeException | ReflectiveOperationException ex) {
			warn(ex);
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception.  Return the defaultValue.
		return defaultValue;
	}

	private static Method getFormatterPropertyMethod;
	static {
		try {
			Method method = LogManager.class.getDeclaredMethod("getFormatterProperty", String.class, Formatter.class);
			method.setAccessible(true);
		} catch(RuntimeException | ReflectiveOperationException e) {
			warn(e);
		}
	}

	/**
	 * Package private method to get a formatter property.
	 * We return an instance of the class named by the "name"
	 * property. If the property is not defined or has problems
	 * we return the defaultValue.
	 */
	// @see LogManager#getFormatterProperty(java.lang.String, java.util.logging.Formatter)
	public static Formatter getFormatterProperty(LogManager manager, String name, Formatter defaultValue) {
		if(getFormatterPropertyMethod != null) {
			try {
				return (Formatter)getFormatterPropertyMethod.invoke(manager, name, defaultValue);
			} catch(RuntimeException | ReflectiveOperationException e) {
				warn(e);
				// Fall-through to copied implementation
			}
		}
		String val = manager.getProperty(name);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Formatter) clz.newInstance();
			}
		} catch (RuntimeException | ReflectiveOperationException ex) {
			warn(ex);
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception.  Return the defaultValue.
		return defaultValue;
	}

	private LogManagerUtil() {}
}