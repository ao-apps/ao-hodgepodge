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
// Note: Comment matches FailFastConnectionImpl
public interface IFailFastConnection extends Connection {

	enum State {
		/**
		 * Normal operation
		 */
		OK,

		/**
		 * {@link ApplicationSQLException} are application-level issues where the underlying database connection and
		 * transaction are expected to still be valid.  These are recoverable by simply
		 * {@link IFailFastConnection#clearApplicationFailFast() clearing the fail-fast state}.
		 */
		APPLICATION,

		/**
		 * All {@link Throwable} except {@link ApplicationSQLException}.  These might be recoverable through
		 * {@link IFailFastConnection#rollback()} or {@link IFailFastConnection#rollback(java.sql.Savepoint)}.
		 */
		EXCEPTION,

		/**
		 * Non-recoverable {@link TerminalSQLException}.
		 */
		TERMINAL;

		/**
		 * Gets the precedence state for the given throwable.
		 *
		 * @return  The precedence state or {@code null} when given {@code null} argument
		 */
		public static State getState(Throwable cause) {
			if(cause == null) {
				return OK;
			} else if(cause instanceof TerminalSQLException) {
				return TERMINAL;
			} else if(!(cause instanceof ApplicationSQLException)) {
				return EXCEPTION;
			} else {
				return APPLICATION;
			}
		}
	}

	/**
	 * Registers a cause for the current failure.  Multiple causes are merged in the following order:
	 * <ol>
	 * <li>{@link TerminalSQLException} take highest precedence, since these are non-recoverable.</li>
	 * <li>
	 *   All {@link Throwable} except {@link ApplicationSQLException} are next precedence.  These might be recoverable
	 *   through {@link IFailFastConnection#rollback()} or {@link IFailFastConnection#rollback(java.sql.Savepoint)}.
	 * </li>
	 * <li>
	 *   {@link ApplicationSQLException} are lowest precedence, since these are application-level issues where the
	 *   underlying database connection and transaction are expected to still be valid.  These are recoverable by
	 *   simply {@link IFailFastConnection#clearApplicationFailFast() clearing the fail-fast state}.
	 * </li>
	 * </ol>
	 * <p>
	 * Higher precedence causes will suppress any existing cause of a lower precedence (new adds the current as
	 * suppressed).
	 * </p>
	 * <p>
	 * Lower precedence causes will be suppressed by any existing cause of higher precedence (current adds the new as
	 * suppressed).
	 * </p>
	 * <p>
	 * Causes within the same precedence are merged via
	 * {@link Throwables#addSuppressed(java.lang.Throwable, java.lang.Throwable)}.
	 * </p>
	 *
	 * @param  cause  The additional cause, ignored when {@code null}
	 */
	void addFailFastCause(Throwable cause);

	/**
	 * Gets the cause of the current fail-fast state.
	 *
	 * @return  The cause or {@code null} when not in failure state (operating normally).
	 *
	 * @see  #getFailFastState()
	 */
	Throwable getFailFastCause();

	/**
	 * Gets the current fail-fail state.
	 *
	 * @return  The state or {@link State#OK} when not in failure state (operating normally).
	 *
	 * @see  #getFailFastCause()
	 */
	default State getFailFastState() {
		return State.getState(getFailFastCause());
	}

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
	// TODO: Should this still be part of the interface and have a public implementation method?
	Throwable clearFailFast() throws TerminalSQLException;

	/**
	 * Clears the cause of the current fail-fast state if it is an {@link ApplicationSQLException}.
	 * This will typically be invoked when an application has caught and handled a {@link ApplicationSQLException}
	 * and is prepared to continue with the transaction and/or connection.
	 *
	 * @return  The cause or {@code null} when was not in an application failure state (operating normally).
	 *
	 * @throws  TerminalSQLException if the connection is in a terminal fail-fast state, such as closed or aborted.
	 * @throws  SQLException  if the connection is in a non-application (driver or database sytsem) fail-fast state.
	 *
	 * @see  #getFailFastCause()
	 */
	// TODO: Find all uses of NoRowException (and others), and make sure to call this clear - where and how?
	//       What about when no longer have access to the connection?  How do we know if is handled or not?
	//       If this whole idea fundamentally broken?
	//       Instead, do we require explicit commit(), and just group commits by automatic nested transactions (only
	//       actually perform commit on the outer-most).
	//       Do we instead not have any ThreadLocal-based automatic transactions?
	ApplicationSQLException clearApplicationFailFast() throws TerminalSQLException, SQLException;

	/**
	 * When not in a {@linkplain TerminalSQLException terminal fail-fast state}, will
	 * {@link #clearFailFast() clear the fail-fast state} upon a successful call to
	 * {@code super.rollback()}.
	 *
	 * @throws  TerminalSQLException if already in a terminal fail-fast state
	 * @throws  SQLException if any other failure occurs during rollback
	 */
	@Override
	void rollback() throws TerminalSQLException, SQLException;

	/**
	 * Puts the connection into a terminal {@link ClosedSQLException} fail-fast state then calls
	 * {@link FailFastConnectionImpl#doClose(java.lang.Throwable)}.
	 * <p>
	 * When already in a terminal state (closed or aborted), is a no-op and does not call
	 * {@link FailFastConnectionImpl#doClose(java.lang.Throwable)}.
	 * </p>
	 *
	 * @see  #addFailFastCause(java.lang.Throwable)
	 * @see  ClosedSQLException
	 * @see  FailFastConnectionImpl#doClose(java.lang.Throwable)
	 */
	@Override
	void close() throws SQLException;

	/**
	 * When not in a {@linkplain TerminalSQLException terminal fail-fast state}, will
	 * {@link #clearFailFast() clear the fail-fast state} upon a successful call to
	 * {@code super.rollback(savepoint)}.
	 *
	 * @throws  FailFastSQLException if already in a terminal fail-fast state
	 * @throws  SQLException if any other failure occurs during rollback
	 */
	@Override
	void rollback(Savepoint savepoint) throws TerminalSQLException, SQLException;

	/**
	 * Puts the connection into a terminal {@link AbortedSQLException} fail-fast state then calls
	 * {@link FailFastConnectionImpl#doAbort(java.lang.Throwable, java.util.concurrent.Executor)}.
	 * <p>
	 * When already in a terminal state (closed or aborted), is a no-op and does not call
	 * {@link FailFastConnectionImpl#doAbort(java.lang.Throwable, java.util.concurrent.Executor)}
	 * </p>
	 *
	 * @see  #addFailFastCause(java.lang.Throwable)
	 * @see  AbortedSQLException
	 * @see  FailFastConnectionImpl#doAbort(java.lang.Throwable, java.util.concurrent.Executor)
	 */
	@Override
	void abort(Executor executor) throws SQLException;
}
