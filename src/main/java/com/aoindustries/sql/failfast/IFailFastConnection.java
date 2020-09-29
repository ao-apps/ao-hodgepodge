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

import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.IConnectionWrapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Executor;

/**
 * Makes a {@link Connection} perform in a fail-fast manner.  All access to the connection will fail once a
 * {@link Throwable} has been thrown by the underlying driver, with this state only being cleared by rollback.
 *
 * @author  AO Industries, Inc.
 */
// Note: Comment matches FailFastConnection
public interface IFailFastConnection extends IConnectionWrapper {

	/**
	 * Registers a cause for the current failure.  Multiple causes are merged in the following order:
	 * <ol>
	 * <li>
	 * When the current cause is a {@link TerminalSQLException}, the new cause will be added to it as
	 * {@linkplain Throwable#addSuppressed(java.lang.Throwable) suppressed}, unless already suppressed by the current
	 * cause.
	 * </li>
	 * <li>
	 * When the new cause is a {@link TerminalSQLException}, any current cause will be added to it as
	 * {@linkplain Throwable#addSuppressed(java.lang.Throwable) suppressed}, unless already suppressed by the new cause.
	 * </li>
	 * <li>
	 * The new cause is merged with any existing via {@link Throwables#addSuppressed(java.lang.Throwable, java.lang.Throwable)}.
	 * </li>
	 * </ol>
	 *
	 * @param  cause  The additional cause, ignored when {@code null}
	 */
	void addFailFastCause(Throwable cause);

	/**
	 * Gets the cause of the current fail-fast state.
	 *
	 * @return  The cause or {@code null} when not in failure state (operating normally).
	 */
	Throwable getFailFastCause();

	/**
	 * Clears the cause of the current fail-fast state.
	 * This will typically be invoked automatically during one of the following successful operations:
	 * <ol>
	 * <li>{@link Connection#rollback()}</li>
	 * <li>{@link Connection#rollback(java.sql.Savepoint)}</li>
	 * </ol>
	 *
	 * @return  The cause or {@code null} when was not in failure state (operating normally).
	 *
	 * @throws  TerminalSQLException if the connection is in a terminal fail-fast state, such as closed or aborted.
	 *
	 * @see  #getFailFastCause()
	 */
	Throwable clearFailFastCause() throws TerminalSQLException;

	/**
	 * When not in a {@linkplain TerminalSQLException terminal fail-fast state}, will
	 * {@linkplain #clearFailFastCause() clear the fail-fast state} upon a successful call to
	 * {@code super.rollback()}.
	 *
	 * @throws  TerminalSQLException if already in a terminal fail-fast state
	 * @throws  SQLException if any other failure occurs during rollback
	 */
	@Override
	void rollback() throws TerminalSQLException, SQLException;

	/**
	 * Puts the connection into a terminal {@link ClosedSQLException} fail-fast state then calls
	 * {@link FailFastConnection#doClose(java.lang.Throwable)}.
	 * <p>
	 * When already in a terminal state (closed or aborted), is a no-op and does not call
	 * {@link FailFastConnection#doClose(java.lang.Throwable)}.
	 * </p>
	 *
	 * @see  #addFailFastCause(java.lang.Throwable)
	 * @see  ClosedSQLException
	 * @see  FailFastConnection#doClose(java.lang.Throwable)
	 */
	@Override
	void close() throws SQLException;

	/**
	 * When not in a {@linkplain TerminalSQLException terminal fail-fast state}, will
	 * {@linkplain #clearFailFastCause() clear the fail-fast state} upon a successful call to
	 * {@code super.rollback(savepoint)}.
	 *
	 * @throws  FailFastSQLException if already in a terminal fail-fast state
	 * @throws  SQLException if any other failure occurs during rollback
	 */
	@Override
	void rollback(Savepoint savepoint) throws TerminalSQLException, SQLException;

	/**
	 * Puts the connection into a terminal {@link AbortedSQLException} fail-fast state then calls
	 * {@link FailFastConnection#doAbort(java.lang.Throwable, java.util.concurrent.Executor)}.
	 * <p>
	 * When already in a terminal state (closed or aborted), is a no-op and does not call
	 * {@link FailFastConnection#doAbort(java.lang.Throwable, java.util.concurrent.Executor)}
	 * </p>
	 *
	 * @see  #addFailFastCause(java.lang.Throwable)
	 * @see  AbortedSQLException
	 * @see  FailFastConnection#doAbort(java.lang.Throwable, java.util.concurrent.Executor)
	 */
	@Override
	void abort(Executor executor) throws SQLException;
}
