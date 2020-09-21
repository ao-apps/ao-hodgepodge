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
package com.aoindustries.sql;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Wraps a {@link PreparedStatement}.
 *
 * @author  AO Industries, Inc.
 */
public class PreparedStatementWrapper extends StatementWrapper implements IPreparedStatementWrapper {

	public PreparedStatementWrapper(ConnectionWrapper connectionWrapper, PreparedStatement wrapped) {
		super(connectionWrapper, wrapped);
	}

	@Override
	public PreparedStatement getWrapped() {
		return (PreparedStatement)super.getWrapped();
	}

	/**
	 * Wraps a {@link ResultSetMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newResultSetMetaDataWrapper(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper wrapResultSetMetaData(ResultSetMetaData metaData) {
		if(metaData == null) {
			return null;
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		if(metaData instanceof ResultSetMetaDataWrapper) {
			ResultSetMetaDataWrapper metaDataWrapper = (ResultSetMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == _connectionWrapper) {
				return metaDataWrapper;
			}
		}
		return _connectionWrapper.newResultSetMetaDataWrapper(metaData);
	}

	@Override
	public ResultSetWrapper executeQuery() throws SQLException {
		return wrapResultSet(getWrapped().executeQuery());
	}

	@Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
		IPreparedStatementWrapper.super.setArray(parameterIndex, getConnectionWrapper().unwrapArray(x));
	}

	@Override
    public ResultSetMetaDataWrapper getMetaData() throws SQLException {
		return wrapResultSetMetaData(getWrapped().getMetaData());
	}
}
