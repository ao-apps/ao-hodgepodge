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
package com.aoindustries.sql.wrapper;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Wraps a {@link Driver}.
 *
 * @author  AO Industries, Inc.
 */
public interface IDriverWrapper extends IWrapper, Driver {

	/**
	 * Gets the SQL output that is wrapped.
	 */
	@Override
	Driver getWrapped();

	@Override
	IConnectionWrapper connect(String url, Properties info) throws SQLException;

	@Override
	default boolean acceptsURL(String url) throws SQLException {
		return getWrapped().acceptsURL(url);
	}

	@Override
	default DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return getWrapped().getPropertyInfo(url, info);
	}

	@Override
	default int getMajorVersion() {
		return getWrapped().getMajorVersion();
	}

	@Override
	default int getMinorVersion() {
		return getWrapped().getMinorVersion();
	}

	@Override
	default boolean jdbcCompliant() {
		return getWrapped().jdbcCompliant();
	}

	@Override
	default Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return getWrapped().getParentLogger();
	}
}
