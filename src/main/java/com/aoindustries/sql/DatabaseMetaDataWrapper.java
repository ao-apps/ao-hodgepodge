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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * Wraps a {@link DatabaseMetaData}.
 *
 * @author  AO Industries, Inc.
 */
public class DatabaseMetaDataWrapper implements DatabaseMetaData {

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

	/**
	 * Gets the database meta data that is wrapped.
	 */
	protected DatabaseMetaData getWrappedDatabaseMetaData() {
		return wrapped;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  StatementWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  ConnectionWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			if(!resultsWrapper.getStatementWrapper().isPresent()) {
				return resultsWrapper;
			}
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		StatementWrapper stmtWrapper = _connectionWrapper.wrapStatement(results.getStatement());
		if(stmtWrapper != null) {
			return stmtWrapper.wrapResultSet(results);
		} else {
			return _connectionWrapper.wrapResultSet(results);
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		DatabaseMetaData metaData = getWrappedDatabaseMetaData();
		if(iface.isInstance(metaData)) return iface.cast(metaData);
		return metaData.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		DatabaseMetaData metaData = getWrappedDatabaseMetaData();
		return iface.isInstance(metaData) || metaData.isWrapperFor(iface);
	}

	@Override
	public boolean allProceduresAreCallable() throws SQLException {
		return getWrappedDatabaseMetaData().allProceduresAreCallable();
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		return getWrappedDatabaseMetaData().allTablesAreSelectable();
	}

	@Override
	public String getURL() throws SQLException {
		return getWrappedDatabaseMetaData().getURL();
	}

	@Override
	public String getUserName() throws SQLException {
		return getWrappedDatabaseMetaData().getUserName();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return getWrappedDatabaseMetaData().isReadOnly();
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedHigh();
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedLow();
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedAtStart();
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedAtEnd();
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseProductName();
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseProductVersion();
	}

	@Override
	public String getDriverName() throws SQLException {
		return getWrappedDatabaseMetaData().getDriverName();
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDriverVersion();
	}

	@Override
	public int getDriverMajorVersion() {
		return getWrappedDatabaseMetaData().getDriverMajorVersion();
	}

	@Override
	public int getDriverMinorVersion() {
		return getWrappedDatabaseMetaData().getDriverMinorVersion();
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return getWrappedDatabaseMetaData().usesLocalFiles();
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return getWrappedDatabaseMetaData().usesLocalFilePerTable();
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMixedCaseIdentifiers();
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesUpperCaseIdentifiers();
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesLowerCaseIdentifiers();
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesMixedCaseIdentifiers();
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMixedCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesUpperCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesLowerCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesMixedCaseQuotedIdentifiers();
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return getWrappedDatabaseMetaData().getIdentifierQuoteString();
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		return getWrappedDatabaseMetaData().getSQLKeywords();
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getNumericFunctions();
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getStringFunctions();
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getSystemFunctions();
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getTimeDateFunctions();
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		return getWrappedDatabaseMetaData().getSearchStringEscape();
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return getWrappedDatabaseMetaData().getExtraNameCharacters();
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return getWrappedDatabaseMetaData().supportsAlterTableWithAddColumn();
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return getWrappedDatabaseMetaData().supportsAlterTableWithDropColumn();
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return getWrappedDatabaseMetaData().supportsColumnAliasing();
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return getWrappedDatabaseMetaData().nullPlusNonNullIsNull();
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return getWrappedDatabaseMetaData().supportsConvert();
	}

	@Override
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return getWrappedDatabaseMetaData().supportsConvert(fromType, toType);
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return getWrappedDatabaseMetaData().supportsTableCorrelationNames();
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDifferentTableCorrelationNames();
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return getWrappedDatabaseMetaData().supportsExpressionsInOrderBy();
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOrderByUnrelated();
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupBy();
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupByUnrelated();
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupByBeyondSelect();
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return getWrappedDatabaseMetaData().supportsLikeEscapeClause();
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleResultSets();
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleTransactions();
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return getWrappedDatabaseMetaData().supportsNonNullableColumns();
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMinimumSQLGrammar();
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCoreSQLGrammar();
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsExtendedSQLGrammar();
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92EntryLevelSQL();
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92IntermediateSQL();
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92FullSQL();
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return getWrappedDatabaseMetaData().supportsIntegrityEnhancementFacility();
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOuterJoins();
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsFullOuterJoins();
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsLimitedOuterJoins();
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getSchemaTerm();
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getProcedureTerm();
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getCatalogTerm();
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return getWrappedDatabaseMetaData().isCatalogAtStart();
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return getWrappedDatabaseMetaData().getCatalogSeparator();
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInDataManipulation();
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInProcedureCalls();
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInTableDefinitions();
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInIndexDefinitions();
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInPrivilegeDefinitions();
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInDataManipulation();
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInProcedureCalls();
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInTableDefinitions();
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInIndexDefinitions();
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInPrivilegeDefinitions();
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return getWrappedDatabaseMetaData().supportsPositionedDelete();
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return getWrappedDatabaseMetaData().supportsPositionedUpdate();
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSelectForUpdate();
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStoredProcedures();
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInComparisons();
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInExists();
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInIns();
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInQuantifieds();
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCorrelatedSubqueries();
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return getWrappedDatabaseMetaData().supportsUnion();
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return getWrappedDatabaseMetaData().supportsUnionAll();
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenCursorsAcrossCommit();
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenCursorsAcrossRollback();
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenStatementsAcrossCommit();
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenStatementsAcrossRollback();
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxBinaryLiteralLength();
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCharLiteralLength();
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnNameLength();
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInGroupBy();
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInIndex();
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInOrderBy();
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInSelect();
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInTable();
	}

	@Override
	public int getMaxConnections() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxConnections();
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCursorNameLength();
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxIndexLength();
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxSchemaNameLength();
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxProcedureNameLength();
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCatalogNameLength();
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxRowSize();
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return getWrappedDatabaseMetaData().doesMaxRowSizeIncludeBlobs();
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxStatementLength();
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxStatements();
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxTableNameLength();
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxTablesInSelect();
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxUserNameLength();
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return getWrappedDatabaseMetaData().getDefaultTransactionIsolation();
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsTransactions();
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return getWrappedDatabaseMetaData().supportsTransactionIsolationLevel(level);
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDataDefinitionAndDataManipulationTransactions();
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDataManipulationTransactionsOnly();
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return getWrappedDatabaseMetaData().dataDefinitionCausesTransactionCommit();
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().dataDefinitionIgnoredInTransactions();
	}

	@Override
	public ResultSetWrapper getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getProcedures(catalog, schemaPattern, procedureNamePattern));
	}

	@Override
	public ResultSetWrapper getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
	}

	@Override
	public ResultSetWrapper getTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getTables(catalog, schemaPattern, tableNamePattern, types));
	}

	@Override
	public ResultSetWrapper getSchemas() throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getSchemas());
	}

	@Override
	public ResultSetWrapper getCatalogs() throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getCatalogs());
	}

	@Override
	public ResultSetWrapper getTableTypes() throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getTableTypes());
	}

	@Override
	public ResultSetWrapper getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
	}

	@Override
	public ResultSetWrapper getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getColumnPrivileges(catalog, schema, table, columnNamePattern));
	}

	@Override
	public ResultSetWrapper getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getTablePrivileges(catalog, schemaPattern, tableNamePattern));
	}

	@Override
	public ResultSetWrapper getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getBestRowIdentifier(catalog, schema, table, scope, nullable));
	}

	@Override
	public ResultSetWrapper getVersionColumns(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getVersionColumns(catalog, schema, table));
	}

	@Override
	public ResultSetWrapper getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getPrimaryKeys(catalog, schema, table));
	}

	@Override
	public ResultSetWrapper getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getImportedKeys(catalog, schema, table));
	}

	@Override
	public ResultSetWrapper getExportedKeys(String catalog, String schema, String table) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getExportedKeys(catalog, schema, table));
	}

	@Override
	public ResultSetWrapper getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable));
	}

	@Override
	public ResultSetWrapper getTypeInfo() throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getTypeInfo());
	}

	@Override
	public ResultSetWrapper getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getIndexInfo(catalog, schema, table, unique, approximate));
	}

	@Override
	public boolean supportsResultSetType(int type) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetType(type);
	}

	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetConcurrency(type, concurrency);
	}

	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownUpdatesAreVisible(type);
	}

	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownDeletesAreVisible(type);
	}

	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownInsertsAreVisible(type);
	}

	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersUpdatesAreVisible(type);
	}

	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersDeletesAreVisible(type);
	}

	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersInsertsAreVisible(type);
	}

	@Override
	public boolean updatesAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().updatesAreDetected(type);
	}

	@Override
	public boolean deletesAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().deletesAreDetected(type);
	}

	@Override
	public boolean insertsAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().insertsAreDetected(type);
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return getWrappedDatabaseMetaData().supportsBatchUpdates();
	}

	@Override
	public ResultSetWrapper getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getUDTs(catalog, schemaPattern, typeNamePattern, types));
	}

	@Override
	public ConnectionWrapper getConnection() throws SQLException {
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		assert getWrappedDatabaseMetaData().getConnection() == _connectionWrapper.getWrappedConnection();
		return _connectionWrapper;
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSavepoints();
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		return getWrappedDatabaseMetaData().supportsNamedParameters();
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleOpenResults();
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGetGeneratedKeys();
	}

	@Override
	public ResultSetWrapper getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getSuperTypes(catalog, schemaPattern, typeNamePattern));
	}

	@Override
	public ResultSetWrapper getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getSuperTables(catalog, schemaPattern, tableNamePattern));
	}

	@Override
	public ResultSetWrapper getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern));
	}

	@Override
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetHoldability(holdability);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return getWrappedDatabaseMetaData().getResultSetHoldability();
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseMajorVersion();
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseMinorVersion();
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getJDBCMajorVersion();
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getJDBCMinorVersion();
	}

	@Override
	public int getSQLStateType() throws SQLException {
		return getWrappedDatabaseMetaData().getSQLStateType();
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		return getWrappedDatabaseMetaData().locatorsUpdateCopy();
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStatementPooling();
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return getWrappedDatabaseMetaData().getRowIdLifetime();
	}

	@Override
	public ResultSetWrapper getSchemas(String catalog, String schemaPattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getSchemas(catalog, schemaPattern));
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStoredFunctionsUsingCallSyntax();
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return getWrappedDatabaseMetaData().autoCommitFailureClosesAllResultSets();
	}

	@Override
	public ResultSetWrapper getClientInfoProperties() throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getClientInfoProperties());
	}

	@Override
	public ResultSetWrapper getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getFunctions(catalog, schemaPattern, functionNamePattern));
	}

	@Override
	public ResultSetWrapper getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern));
	}

	@Override
	public ResultSetWrapper getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return wrapResultSet(getWrappedDatabaseMetaData().getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
	}

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return getWrappedDatabaseMetaData().generatedKeyAlwaysReturned();
	}

	@Override
	public long getMaxLogicalLobSize() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxLogicalLobSize();
	}

	@Override
	public boolean supportsRefCursors() throws SQLException {
		return getWrappedDatabaseMetaData().supportsRefCursors();
	}

	// Java 9: boolean supportsSharding() throws SQLException;
}
