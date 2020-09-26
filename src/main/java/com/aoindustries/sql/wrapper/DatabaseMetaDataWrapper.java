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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Wraps a {@link DatabaseMetaData}.
 *
 * @author  AO Industries, Inc.
 */
public class DatabaseMetaDataWrapper implements IDatabaseMetaDataWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final DatabaseMetaData wrapped;

	public DatabaseMetaDataWrapper(ConnectionWrapper connectionWrapper, DatabaseMetaData wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public DatabaseMetaData getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapResultSet(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		return getConnectionWrapper().wrapResultSet(null, results);
	}

	@Override
	public String getURL() throws SQLException {
		String wrappedUrl = getWrapped().getURL();
		Optional<? extends DriverWrapper> driver = getConnectionWrapper().getDriver();
		if(driver.isPresent()) {
			return driver.get().toWrapperUrl(wrappedUrl);
		} else {
			return wrappedUrl;
		}
	}

	@Override
	public String getDriverName() throws SQLException {
		String wrappedName = getWrapped().getDriverName();
		return getConnectionWrapper().getDriver()
			.map(driver -> driver.getDriverName(wrappedName))
			.orElse(wrappedName);
	}

	@Override
	public String getDriverVersion() throws SQLException {
		String wrappedVersion = getWrapped().getDriverVersion();
		return getConnectionWrapper().getDriver()
			.map(driver -> driver.getDriverVersion(wrappedVersion))
			.orElse(wrappedVersion);
	}

	@Override
	public int getDriverMajorVersion() {
		int wrappedMajor = getWrapped().getDriverMajorVersion();
		return getConnectionWrapper().getDriver()
			.map(driver -> driver.getDriverMajorVersion(wrappedMajor))
			.orElse(wrappedMajor);
	}

	@Override
	public int getDriverMinorVersion() {
		int wrappedMinor = getWrapped().getDriverMinorVersion();
		return getConnectionWrapper().getDriver()
			.map(driver -> driver.getDriverMinorVersion(wrappedMinor))
			.orElse(wrappedMinor);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getProcedures(catalog, schemaPattern, procedureNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) throws SQLException {
		return wrapResultSet(getWrapped().getTables(catalog, schemaPattern, tableNamePattern, types));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getSchemas() throws SQLException {
		return wrapResultSet(getWrapped().getSchemas());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getCatalogs() throws SQLException {
		return wrapResultSet(getWrapped().getCatalogs());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	// TODO: Review uses of {@inheritDoc} that are not necessary
	@Override
	public ResultSetWrapper getTableTypes() throws SQLException {
		return wrapResultSet(getWrapped().getTableTypes());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getColumnPrivileges(catalog, schema, table, columnNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getTablePrivileges(catalog, schemaPattern, tableNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		return wrapResultSet(getWrapped().getBestRowIdentifier(catalog, schema, table, scope, nullable));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getVersionColumns(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrapped().getVersionColumns(catalog, schema, table));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrapped().getPrimaryKeys(catalog, schema, table));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrapped().getImportedKeys(catalog, schema, table));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getExportedKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrapped().getExportedKeys(catalog, schema, table));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		return wrapResultSet(getWrapped().getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getTypeInfo() throws SQLException {
		return wrapResultSet(getWrapped().getTypeInfo());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		return wrapResultSet(getWrapped().getIndexInfo(catalog, schema, table, unique, approximate));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
		return wrapResultSet(getWrapped().getUDTs(catalog, schemaPattern, typeNamePattern, types));
	}

	@Override
	public ConnectionWrapper getConnection() throws SQLException {
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		assert getWrapped().getConnection() == _connectionWrapper.getWrapped();
		return _connectionWrapper;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getSuperTypes(catalog, schemaPattern, typeNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getSuperTables(catalog, schemaPattern, tableNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getSchemas(String catalog, String schemaPattern) throws SQLException {
		return wrapResultSet(getWrapped().getSchemas(catalog, schemaPattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getClientInfoProperties() throws SQLException {
		return wrapResultSet(getWrapped().getClientInfoProperties());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getFunctions(catalog, schemaPattern, functionNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrapped().getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
	}
}
