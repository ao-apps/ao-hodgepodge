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
import com.aoindustries.sql.wrapper.ParameterMetaDataWrapper;
import java.sql.ParameterMetaData;
import java.sql.SQLException;

/**
 * @see  FailFastConnection
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastParameterMetaData extends ParameterMetaDataWrapper {

	public FailFastParameterMetaData(FailFastConnection failFastConnection, ParameterMetaData wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnection getConnectionWrapper() {
		return (FailFastConnection)super.getConnectionWrapper();
	}

	@Override
	public int getParameterCount() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getParameterCount();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int isNullable(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.isNullable(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isSigned(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.isSigned(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getPrecision(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getPrecision(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getScale(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getScale(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getParameterType(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getParameterType(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getParameterTypeName(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getParameterTypeName(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getParameterClassName(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getParameterClassName(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getParameterMode(int param) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
 		try {
			return super.getParameterMode(param);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}
}
