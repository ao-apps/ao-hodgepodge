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
package com.aoindustries.sql.failfast;

import java.sql.Connection;

/**
 * A terminal state when a {@linkplain Connection#abort(java.util.concurrent.Executor) connection is aborted}.
 *
 * @author  AO Industries, Inc.
 *
 * @see  FailFastConnection#abort(java.util.concurrent.Executor)
 */
public class AbortedSQLException extends TerminalSQLException {

	private static final long serialVersionUID = 1L;

	/**
	 * This instance is stored during abort.  It will never have suppressed throwables added to it, will never be
	 * used as an exception cause, and will never be thrown.  When this is the fail-fast cause, exceptions will be
	 * thrown without a cause.
	 */
	static final AbortedSQLException FAST_MARKER_KEEP_PRIVATE = new AbortedSQLException();
	static {
		FAST_MARKER_KEEP_PRIVATE.setStackTrace(new StackTraceElement[0]);
	}

	//public AbortedSQLException(String reason, String sqlState, int vendorCode) {
	//	super(reason, sqlState, vendorCode);
	//}

	//public AbortedSQLException(String reason, String sqlState) {
	//	super(reason, sqlState);
	//}

	//public AbortedSQLException(String reason) {
	//	super(reason);
	//}

	public AbortedSQLException() {
		super("Connection aborted");
	}

	//public AbortedSQLException(Throwable cause) {
	//	super(cause);
	//	if(cause == FAST_MARKER_KEEP_PRIVATE) throw new IllegalArgumentException();
	//}

	//public AbortedSQLException(String reason, Throwable cause) {
	//	super(reason, cause);
	//	if(cause == FAST_MARKER_KEEP_PRIVATE) throw new IllegalArgumentException();
	//}

	//public AbortedSQLException(String reason, String sqlState, Throwable cause) {
	//	super(reason, sqlState, cause);
	//	if(cause == FAST_MARKER_KEEP_PRIVATE) throw new IllegalArgumentException();
	//}

	public AbortedSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
		super(reason, sqlState, vendorCode, cause);
		if(cause == FAST_MARKER_KEEP_PRIVATE) throw new IllegalArgumentException();
	}
}
