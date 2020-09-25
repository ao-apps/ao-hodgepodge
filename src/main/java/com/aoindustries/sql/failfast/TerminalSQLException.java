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

/**
 * An exception that puts a connection into a terminal state.  Once in a terminal state, the state cannot be
 * {@linkplain IFailFastConnection#addFailFastCause(java.lang.Throwable) replaced} or
 * {@linkplain IFailFastConnection#clearFailFastCause() cleared}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class TerminalSQLException extends FailFastSQLException {

	private static final long serialVersionUID = 1L;

	//public TerminalSQLException(String reason, String sqlState, int vendorCode) {
	//	super(reason, sqlState, vendorCode);
	//}

	//public TerminalSQLException(String reason, String sqlState) {
	//	super(reason, sqlState);
	//}

	public TerminalSQLException(String reason) {
		super(reason);
	}

	//public TerminalSQLException() {
	//	super();
	//}

	//public TerminalSQLException(Throwable cause) {
	//	super(cause);
	//}

	//public TerminalSQLException(String reason, Throwable cause) {
	//	super(reason, cause);
	//}

	//public TerminalSQLException(String reason, String sqlState, Throwable cause) {
	//	super(reason, sqlState, cause);
	//}

	public TerminalSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
		super(reason, sqlState, vendorCode, cause);
	}
}
