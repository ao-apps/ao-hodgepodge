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
import com.aoindustries.sql.wrapper.SQLXMLWrapper;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * @see  FailFastConnection
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastSQLXML extends SQLXMLWrapper {

	public FailFastSQLXML(FailFastConnection failFastConnection, SQLXML wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnection getConnectionWrapper() {
		return (FailFastConnection)super.getConnectionWrapper();
	}

	@Override
	public void free() throws SQLException {
		try {
			super.free();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastInputStream getBinaryStream() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getBinaryStream();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastOutputStream setBinaryStream() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastOutputStream)super.setBinaryStream();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastReader getCharacterStream() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastReader)super.getCharacterStream();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastWriter setCharacterStream() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastWriter)super.setCharacterStream();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getString() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getString();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setString(String value) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.setString(value);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getSource(sourceClass);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.setResult(resultClass);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}
}
