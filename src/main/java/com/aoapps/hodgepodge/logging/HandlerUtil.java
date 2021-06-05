/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

/**
 * Provides configuration method similar to {@link ConsoleHandler}.
 * 
 * @see ConsoleHandler
 * 
 * @author  AO Industries, Inc.
 */
public class HandlerUtil {

	/**
	 * Private method to configure a ConsoleHandler from LogManager
	 * properties and/or default values as specified in the class
	 * javadoc.
	 */
	// @see ConsoleHandler#configure()
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	public static void configure(Handler handler) {
		LogManager manager = LogManager.getLogManager();
		String cname = handler.getClass().getName();

		handler.setLevel(LogManagerUtil.getLevelProperty(manager, cname +".level", Level.INFO));
		handler.setFilter(LogManagerUtil.getFilterProperty(manager, cname +".filter", null));
		handler.setFormatter(LogManagerUtil.getFormatterProperty(manager, cname +".formatter", new SimpleFormatter()));
		try {
			handler.setEncoding(LogManagerUtil.getStringProperty(manager, cname +".encoding", null));
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable t) {
			LogManagerUtil.warn(t);
			try {
				handler.setEncoding(null);
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable t2) {
				LogManagerUtil.warn(t2);
				// doing a setEncoding with null should always work.
				// assert false;
			}
		}
	}

	private HandlerUtil() {}
}
