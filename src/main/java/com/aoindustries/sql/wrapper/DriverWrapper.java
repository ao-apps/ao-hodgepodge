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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverAction;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Wraps {@linkplain Connection connections} obtained from other {@linkplain Driver drivers}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class DriverWrapper implements Driver {

	private static final String JDBC_SCHEMA = "jdbc:";

	public DriverWrapper() {}

	@Override
	public String toString() {
		return JDBC_SCHEMA + getUrlPrefix() + ":*";
	}

	/**
	 * Gets the JDBC prefix used for this driver wrapper.  This will be inserted after "jdbc:" in the wrapped
	 * driver URL.  For example, "jdbc:<var>prefix</var>:postgresql://<var>host</var>/<var>database</var>"
	 */
	protected abstract String getUrlPrefix();

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
			if(_connectionWrapper.getDriver().orElse(null) == this) {
				return _connectionWrapper;
			}
		}
		return newConnectionWrapper(connection);
	}

	/**
	 * Gets the JDBC URL used by the wrapped driver.  This removes the prefix from the URL.
	 *
	 * @return  The modified URL or {@code null} when prefix not found in the URL.
	 */
	protected String toWrappedUrl(String wrapperUrl) {
		String prefix = JDBC_SCHEMA + getUrlPrefix() + ":";
		if(wrapperUrl.startsWith(prefix)) {
			return JDBC_SCHEMA + wrapperUrl.substring(prefix.length());
		} else {
			return null;
		}
	}

	/**
	 * Gets the JDBC URL used by the wrapper driver.  This adds the prefix to the URL.
	 *
	 * @return  The modified URL, with prefix in the URL.
	 *
	 * @throws  SQLException  When the wrapped URL does not begin with {@link #JDBC_SCHEMA}
	 *
	 * @see  IDatabaseMetaDataWrapper#getURL()
	 */
	protected String toWrapperUrl(String wrappedUrl) throws SQLException {
		if(!wrappedUrl.startsWith(JDBC_SCHEMA)) {
			throw new SQLException("Malformed JDBC URL, schema (" + JDBC_SCHEMA + ")  not found: " + wrappedUrl);
		}
		String urlPrefix = getUrlPrefix();
		int wrappedUrlLen = wrappedUrl.length();
		int sbLen = wrappedUrlLen + urlPrefix.length() + 1;
		StringBuilder sb = new StringBuilder(sbLen);
		sb
			.append(JDBC_SCHEMA)
			.append(urlPrefix).append(':')
			.append(wrappedUrl, JDBC_SCHEMA.length(), wrappedUrlLen);
		assert sbLen == sb.length();
		return sb.toString();
	}

	@Override
	public ConnectionWrapper connect(String url, Properties info) throws SQLException {
		String wrappedUrl = toWrappedUrl(url);
		if(wrappedUrl != null) {
			try {
				return wrapConnection(DriverManager.getDriver(wrappedUrl).connect(url, info));
			} catch(SQLException e) {
				// DriverManager.getDriver(String) throws exception when no match found
				// Fall-through to return null
			}
		}
		return null;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return toWrappedUrl(url) != null;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public abstract int getMajorVersion();

	@Override
	public abstract int getMinorVersion();

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	@Override
	public abstract Logger getParentLogger() throws SQLFeatureNotSupportedException;

	/**
	 * Called on driver deregistration.
	 *
	 * @see  DriverAction
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	protected void onDeregister() {
		// Nothing to do
	}
}
