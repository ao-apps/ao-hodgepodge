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

import java.sql.SQLException;

/**
 * An SQL exception that is generated at the application level, not by the JDBC driver or underlying database system.
 * The underlying connection, as well as any transaction in-progress, are both expected to still be valid.
 * <p>
 * Exceptions of this type can put a connection into a fail-fast state, but this state may be
 * {@linkplain IFailFastConnection#clearApplicationFailFast() cleared directly}, instead of requiring a
 * {@link IFailFastConnection#rollback()} or {@link IFailFastConnection#rollback(java.sql.Savepoint)}.
 * </p>
 * <p>
 * Applications are expected to be able to catch this exception and continue normally.  When they do, however, the
 * application must {@linkplain IFailFastConnection#clearApplicationFailFast() clear the fail-fast state}.
 * Otherwise, subsequent operations will still fail and the overall transaction may be rolled-back instead of committed.
 * </p>
 *
 * @author  AO Industries, Inc.
 *
 * @see  IFailFastConnection#abort(java.util.concurrent.Executor)
 */
public class ApplicationSQLException extends SQLException {

	private static final long serialVersionUID = 1L;

	public ApplicationSQLException(String reason, String sqlState, int vendorCode) {
		super(reason, sqlState, vendorCode);
	}

	public ApplicationSQLException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public ApplicationSQLException(String reason) {
		super(reason);
	}

	public ApplicationSQLException() {
		super();
	}

	public ApplicationSQLException(Throwable cause) {
		super(cause);
	}

	public ApplicationSQLException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public ApplicationSQLException(String reason, String sqlState, Throwable cause) {
		super(reason, sqlState, cause);
	}

	public ApplicationSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
		super(reason, sqlState, vendorCode, cause);
	}
}
