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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Wraps a {@link Driver}.
 *
 * @author  AO Industries, Inc.
 */
public class DriverWrapper implements IDriverWrapper {

	private final Driver wrapped;

	public DriverWrapper(Driver wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Driver getWrapped() {
		return wrapped;
	}

	/**
	 * Creates a new {@link ConnectionWrapper}.
	 *
	 * @see  #wrapConnection(java.sql.Connection)
	 */
	protected ConnectionWrapper newConnectionWrapper(Connection connection) {
		return new ConnectionWrapper(this, connection);
	}

	/**
	 * Wraps a {@link Connection}, if not already wrapped by this wrapper.
	 *
	 * @see  #newConnectionWrapper(java.sql.Connection)
	 */
	protected ConnectionWrapper wrapConnection(Connection connection) {
		if(connection == null) {
			return null;
		}
		if(connection instanceof ConnectionWrapper) {
			ConnectionWrapper _connectionWrapper = (ConnectionWrapper)connection;
			if(_connectionWrapper.getDriverWrapper().orElse(null) == this) {
				return _connectionWrapper;
			}
		}
		return newConnectionWrapper(connection);
	}

	@Override
	public ConnectionWrapper connect(String url, Properties info) throws SQLException {
		return wrapConnection(getWrapped().connect(url, info));
	}
}
