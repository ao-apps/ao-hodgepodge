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

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.ClientInfoStatus;
import java.sql.DataTruncation;
import java.sql.SQLClientInfoException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLNonTransientException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.sql.SQLWarning;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.serial.SerialException;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.sql.rowset.spi.SyncProviderException;

/**
 * Package-private utilities for managing throwables.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Move the whole wrapping stuff to a public utility class "SQLExceptions"?
class ThrowableUtil {

	private ThrowableUtil() {}

	static boolean isSuppressed(Throwable t0, Throwable suppressed) {
		for(Throwable t : t0.getSuppressed()) {
			if(t == suppressed) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new {@link IOException} with the given cause.
	 */
	static IOException newIOException(Throwable cause) {
		// Subclasses of IOException?
		return new IOException(cause);
	}

	/**
	 * Creates a new {@link AbortedSQLException} with the given cause.
	 */
	static AbortedSQLException newAbortedSQLException(AbortedSQLException cause) {
		if(cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) {
			return new AbortedSQLException();
		} else {
			return new AbortedSQLException(
				cause.getMessage(),
				cause.getSQLState(),
				cause.getErrorCode(),
				cause
			);
		}
	}

	/**
	 * Creates a new {@link BatchUpdateException} with the given cause.
	 */
	static BatchUpdateException newBatchUpdateException(BatchUpdateException cause) {
		int[] updateCounts = cause.getUpdateCounts();
		if(updateCounts != null) {
			return new BatchUpdateException(
				cause.getMessage(),
				cause.getSQLState(),
				cause.getErrorCode(),
				updateCounts,
				cause
			);
		}
		return new BatchUpdateException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause.getLargeUpdateCounts(),
			cause
		);
	}

	/**
	 * Creates a new {@link ClosedSQLException} with the given cause.
	 */
	static ClosedSQLException newClosedSQLException(ClosedSQLException cause) {
		if(cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) {
			return new ClosedSQLException();
		} else {
			return new ClosedSQLException(
				cause.getMessage(),
				cause.getSQLState(),
				cause.getErrorCode(),
				cause
			);
		}
	}

	/**
	 * Creates a new {@link DataTruncation} with the given cause.
	 */
	static DataTruncation newDataTruncation(DataTruncation cause) {
		return new DataTruncation(
			cause.getIndex(),
			cause.getParameter(),
			cause.getRead(),
			cause.getDataSize(),
			cause.getTransferSize(),
			cause
		);
	}

	/**
	 * Creates a new {@link RowSetWarning} with the given cause.
	 */
	static RowSetWarning newRowSetWarning(RowSetWarning cause) {
		RowSetWarning newWarning = new RowSetWarning(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode()
		);
		newWarning.initCause(cause);
		return newWarning;
	}

	/**
	 * Creates a new {@link SQLClientInfoException} with the given cause.
	 */
	static SQLClientInfoException newSQLClientInfoException(SQLClientInfoException cause) {
		return new SQLClientInfoException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause.getFailedProperties(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLClientInfoException} with the given cause.
	 */
	static SQLClientInfoException newSQLClientInfoException(Supplier<? extends Map<String,ClientInfoStatus>> failedPropertiesSupplier, Throwable cause) {
		if(cause instanceof SQLException) {
			SQLException sqlEx = (SQLException)cause;
			return new SQLClientInfoException(
				sqlEx.getMessage(),
				sqlEx.getSQLState(),
				sqlEx.getErrorCode(),
				failedPropertiesSupplier.get(),
				sqlEx
			);
		}
		return new SQLClientInfoException(failedPropertiesSupplier.get(), cause);
	}

	/**
	 * Creates a new {@link SQLDataException} with the given cause.
	 */
	static SQLDataException newSQLDataException(SQLDataException cause) {
		return new SQLDataException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLException} with the given cause.
	 */
	@SuppressWarnings("null")
	static SQLException newSQLException(SQLException cause) {
		if(cause instanceof BatchUpdateException) {
			return newBatchUpdateException((BatchUpdateException)cause);
		}
		if(cause instanceof RowSetWarning) {
			return newRowSetWarning((RowSetWarning)cause);
		}
		if(cause instanceof SerialException) {
			return newSerialException((SerialException)cause);
		}
		if(cause instanceof SQLClientInfoException) {
			return newSQLClientInfoException((SQLClientInfoException)cause);
		}
		if(cause instanceof SQLNonTransientException) {
			return newSQLNonTransientException((SQLNonTransientException)cause);
		}
		if(cause instanceof SQLRecoverableException) {
			return newSQLRecoverableException((SQLRecoverableException)cause);
		}
		if(cause instanceof SQLTransientException) {
			return newSQLTransientException((SQLTransientException)cause);
		}
		if(cause instanceof SQLWarning) {
			return newSQLWarning((SQLWarning)cause);
		}
		if(cause instanceof SyncFactoryException) {
			return newSyncFactoryException((SyncFactoryException)cause);
		}
		if(cause instanceof SyncProviderException) {
			return newSyncProviderException((SyncProviderException)cause);
		}
		if(cause instanceof TerminalSQLException) {
			return newTerminalSQLException((TerminalSQLException)cause);
		}
		return new SQLException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLException} or {@link FailFastSQLException} with the given cause.
	 */
	static SQLException newFailFastSQLException(Throwable cause) {
		if(cause instanceof SQLException) {
			return newSQLException((SQLException)cause);
		} else {
			return new FailFastSQLException(cause);
		}
	}

	/**
	 * Creates a new {@link SQLFeatureNotSupportedException} with the given cause.
	 */
	static SQLFeatureNotSupportedException newSQLFeatureNotSupportedException(SQLFeatureNotSupportedException cause) {
		return new SQLFeatureNotSupportedException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLIntegrityConstraintViolationException} with the given cause.
	 */
	static SQLIntegrityConstraintViolationException newSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException cause) {
		return new SQLIntegrityConstraintViolationException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLInvalidAuthorizationSpecException} with the given cause.
	 */
	static SQLInvalidAuthorizationSpecException newSQLInvalidAuthorizationSpecException(SQLInvalidAuthorizationSpecException cause) {
		return new SQLInvalidAuthorizationSpecException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLNonTransientConnectionException} with the given cause.
	 */
	static SQLNonTransientConnectionException newSQLNonTransientConnectionException(SQLNonTransientConnectionException cause) {
		return new SQLNonTransientConnectionException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLNonTransientException} with the given cause.
	 */
	@SuppressWarnings("null")
	static SQLNonTransientException newSQLNonTransientException(SQLNonTransientException cause) {
		if(cause instanceof SQLDataException) {
			return newSQLDataException((SQLDataException)cause);
		}
		if(cause instanceof SQLFeatureNotSupportedException) {
			return newSQLFeatureNotSupportedException((SQLFeatureNotSupportedException)cause);
		}
		if(cause instanceof SQLIntegrityConstraintViolationException) {
			return newSQLIntegrityConstraintViolationException((SQLIntegrityConstraintViolationException)cause);
		}
		if(cause instanceof SQLInvalidAuthorizationSpecException) {
			return newSQLInvalidAuthorizationSpecException((SQLInvalidAuthorizationSpecException)cause);
		}
		if(cause instanceof SQLNonTransientConnectionException) {
			return newSQLNonTransientConnectionException((SQLNonTransientConnectionException)cause);
		}
		if(cause instanceof SQLSyntaxErrorException) {
			return newSQLSyntaxErrorException((SQLSyntaxErrorException)cause);
		}
		return new SQLNonTransientException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLRecoverableException} with the given cause.
	 */
	static SQLRecoverableException newSQLRecoverableException(SQLRecoverableException cause) {
		return new SQLRecoverableException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLSyntaxErrorException} with the given cause.
	 */
	static SQLSyntaxErrorException newSQLSyntaxErrorException(SQLSyntaxErrorException cause) {
		return new SQLSyntaxErrorException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLTimeoutException} with the given cause.
	 */
	static SQLTimeoutException newSQLTimeoutException(SQLTimeoutException cause) {
		return new SQLTimeoutException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLTransactionRollbackException} with the given cause.
	 */
	static SQLTransactionRollbackException newSQLTransactionRollbackException(SQLTransactionRollbackException cause) {
		return new SQLTransactionRollbackException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLTransientConnectionException} with the given cause.
	 */
	static SQLTransientConnectionException newSQLTransientConnectionException(SQLTransientConnectionException cause) {
		return new SQLTransientConnectionException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLTransientException} with the given cause.
	 */
	@SuppressWarnings("null")
	static SQLTransientException newSQLTransientException(SQLTransientException cause) {
		if(cause instanceof SQLTimeoutException) {
			return newSQLTimeoutException((SQLTimeoutException)cause);
		}
		if(cause instanceof SQLTransactionRollbackException) {
			return newSQLTransactionRollbackException((SQLTransactionRollbackException)cause);
		}
		if(cause instanceof SQLTransientConnectionException) {
			return newSQLTransientConnectionException((SQLTransientConnectionException)cause);
		}
		return new SQLTransientException(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SQLWarning} with the given cause.
	 */
	@SuppressWarnings("null")
	static SQLWarning newSQLWarning(SQLWarning cause) {
		if(cause instanceof DataTruncation) {
			return newDataTruncation((DataTruncation)cause);
		}
		return new SQLWarning(
			cause.getMessage(),
			cause.getSQLState(),
			cause.getErrorCode(),
			cause
		);
	}

	/**
	 * Creates a new {@link SerialException} with the given cause.
	 */
	static SerialException newSerialException(SerialException cause) {
		return new SerialException(cause.getMessage());
	}

	/**
	 * Creates a new {@link SyncFactoryException} with the given cause.
	 */
	static SyncFactoryException newSyncFactoryException(SyncFactoryException cause) {
		SyncFactoryException newEx = new SyncFactoryException(cause.getMessage());
		newEx.initCause(cause);
		return newEx;
	}

	/**
	 * Creates a new {@link SyncProviderException} with the given cause.
	 */
	static SyncProviderException newSyncProviderException(SyncProviderException cause) {
		SyncProviderException newEx = new SyncProviderException(cause.getMessage());
		newEx.initCause(cause);
		newEx.setSyncResolver(cause.getSyncResolver());
		return newEx;
	}

	/**
	 * Creates a new {@link TerminalSQLException} with the given cause.
	 */
	static TerminalSQLException newTerminalSQLException(TerminalSQLException cause) {
		if(cause instanceof AbortedSQLException) {
			return newAbortedSQLException((AbortedSQLException)cause);
		}
		if(cause instanceof ClosedSQLException) {
			return newClosedSQLException((ClosedSQLException)cause);
		}
		throw new AssertionError("Unexpected exception type", cause);
	}
}
