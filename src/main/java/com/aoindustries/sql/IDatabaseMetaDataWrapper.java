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
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * Wraps a {@link DatabaseMetaData}.
 *
 * @author  AO Industries, Inc.
 */
public interface IDatabaseMetaDataWrapper extends DatabaseMetaData {

	/**
	 * Gets the database meta data that is wrapped.
	 */
	DatabaseMetaData getWrappedDatabaseMetaData();

	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		DatabaseMetaData metaData = getWrappedDatabaseMetaData();
		if(iface.isInstance(metaData)) return iface.cast(metaData);
		return metaData.unwrap(iface);
	}

	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		DatabaseMetaData metaData = getWrappedDatabaseMetaData();
		return iface.isInstance(metaData) || metaData.isWrapperFor(iface);
	}

	@Override
	default boolean allProceduresAreCallable() throws SQLException {
		return getWrappedDatabaseMetaData().allProceduresAreCallable();
	}

	@Override
	default boolean allTablesAreSelectable() throws SQLException {
		return getWrappedDatabaseMetaData().allTablesAreSelectable();
	}

	@Override
	default String getURL() throws SQLException {
		return getWrappedDatabaseMetaData().getURL();
	}

	@Override
	default String getUserName() throws SQLException {
		return getWrappedDatabaseMetaData().getUserName();
	}

	@Override
	default boolean isReadOnly() throws SQLException {
		return getWrappedDatabaseMetaData().isReadOnly();
	}

	@Override
	default boolean nullsAreSortedHigh() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedHigh();
	}

	@Override
	default boolean nullsAreSortedLow() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedLow();
	}

	@Override
	default boolean nullsAreSortedAtStart() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedAtStart();
	}

	@Override
	default boolean nullsAreSortedAtEnd() throws SQLException {
		return getWrappedDatabaseMetaData().nullsAreSortedAtEnd();
	}

	@Override
	default String getDatabaseProductName() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseProductName();
	}

	@Override
	default String getDatabaseProductVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseProductVersion();
	}

	@Override
	default String getDriverName() throws SQLException {
		return getWrappedDatabaseMetaData().getDriverName();
	}

	@Override
	default String getDriverVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDriverVersion();
	}

	@Override
	default int getDriverMajorVersion() {
		return getWrappedDatabaseMetaData().getDriverMajorVersion();
	}

	@Override
	default int getDriverMinorVersion() {
		return getWrappedDatabaseMetaData().getDriverMinorVersion();
	}

	@Override
	default boolean usesLocalFiles() throws SQLException {
		return getWrappedDatabaseMetaData().usesLocalFiles();
	}

	@Override
	default boolean usesLocalFilePerTable() throws SQLException {
		return getWrappedDatabaseMetaData().usesLocalFilePerTable();
	}

	@Override
	default boolean supportsMixedCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMixedCaseIdentifiers();
	}

	@Override
	default boolean storesUpperCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesUpperCaseIdentifiers();
	}

	@Override
	default boolean storesLowerCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesLowerCaseIdentifiers();
	}

	@Override
	default boolean storesMixedCaseIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesMixedCaseIdentifiers();
	}

	@Override
	default boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMixedCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesUpperCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesLowerCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrappedDatabaseMetaData().storesMixedCaseQuotedIdentifiers();
	}

	@Override
	default String getIdentifierQuoteString() throws SQLException {
		return getWrappedDatabaseMetaData().getIdentifierQuoteString();
	}

	@Override
	default String getSQLKeywords() throws SQLException {
		return getWrappedDatabaseMetaData().getSQLKeywords();
	}

	@Override
	default String getNumericFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getNumericFunctions();
	}

	@Override
	default String getStringFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getStringFunctions();
	}

	@Override
	default String getSystemFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getSystemFunctions();
	}

	@Override
	default String getTimeDateFunctions() throws SQLException {
		return getWrappedDatabaseMetaData().getTimeDateFunctions();
	}

	@Override
	default String getSearchStringEscape() throws SQLException {
		return getWrappedDatabaseMetaData().getSearchStringEscape();
	}

	@Override
	default String getExtraNameCharacters() throws SQLException {
		return getWrappedDatabaseMetaData().getExtraNameCharacters();
	}

	@Override
	default boolean supportsAlterTableWithAddColumn() throws SQLException {
		return getWrappedDatabaseMetaData().supportsAlterTableWithAddColumn();
	}

	@Override
	default boolean supportsAlterTableWithDropColumn() throws SQLException {
		return getWrappedDatabaseMetaData().supportsAlterTableWithDropColumn();
	}

	@Override
	default boolean supportsColumnAliasing() throws SQLException {
		return getWrappedDatabaseMetaData().supportsColumnAliasing();
	}

	@Override
	default boolean nullPlusNonNullIsNull() throws SQLException {
		return getWrappedDatabaseMetaData().nullPlusNonNullIsNull();
	}

	@Override
	default boolean supportsConvert() throws SQLException {
		return getWrappedDatabaseMetaData().supportsConvert();
	}

	@Override
	default boolean supportsConvert(int fromType, int toType) throws SQLException {
		return getWrappedDatabaseMetaData().supportsConvert(fromType, toType);
	}

	@Override
	default boolean supportsTableCorrelationNames() throws SQLException {
		return getWrappedDatabaseMetaData().supportsTableCorrelationNames();
	}

	@Override
	default boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDifferentTableCorrelationNames();
	}

	@Override
	default boolean supportsExpressionsInOrderBy() throws SQLException {
		return getWrappedDatabaseMetaData().supportsExpressionsInOrderBy();
	}

	@Override
	default boolean supportsOrderByUnrelated() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOrderByUnrelated();
	}

	@Override
	default boolean supportsGroupBy() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupBy();
	}

	@Override
	default boolean supportsGroupByUnrelated() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupByUnrelated();
	}

	@Override
	default boolean supportsGroupByBeyondSelect() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGroupByBeyondSelect();
	}

	@Override
	default boolean supportsLikeEscapeClause() throws SQLException {
		return getWrappedDatabaseMetaData().supportsLikeEscapeClause();
	}

	@Override
	default boolean supportsMultipleResultSets() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleResultSets();
	}

	@Override
	default boolean supportsMultipleTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleTransactions();
	}

	@Override
	default boolean supportsNonNullableColumns() throws SQLException {
		return getWrappedDatabaseMetaData().supportsNonNullableColumns();
	}

	@Override
	default boolean supportsMinimumSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMinimumSQLGrammar();
	}

	@Override
	default boolean supportsCoreSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCoreSQLGrammar();
	}

	@Override
	default boolean supportsExtendedSQLGrammar() throws SQLException {
		return getWrappedDatabaseMetaData().supportsExtendedSQLGrammar();
	}

	@Override
	default boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92EntryLevelSQL();
	}

	@Override
	default boolean supportsANSI92IntermediateSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92IntermediateSQL();
	}

	@Override
	default boolean supportsANSI92FullSQL() throws SQLException {
		return getWrappedDatabaseMetaData().supportsANSI92FullSQL();
	}

	@Override
	default boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return getWrappedDatabaseMetaData().supportsIntegrityEnhancementFacility();
	}

	@Override
	default boolean supportsOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOuterJoins();
	}

	@Override
	default boolean supportsFullOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsFullOuterJoins();
	}

	@Override
	default boolean supportsLimitedOuterJoins() throws SQLException {
		return getWrappedDatabaseMetaData().supportsLimitedOuterJoins();
	}

	@Override
	default String getSchemaTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getSchemaTerm();
	}

	@Override
	default String getProcedureTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getProcedureTerm();
	}

	@Override
	default String getCatalogTerm() throws SQLException {
		return getWrappedDatabaseMetaData().getCatalogTerm();
	}

	@Override
	default boolean isCatalogAtStart() throws SQLException {
		return getWrappedDatabaseMetaData().isCatalogAtStart();
	}

	@Override
	default String getCatalogSeparator() throws SQLException {
		return getWrappedDatabaseMetaData().getCatalogSeparator();
	}

	@Override
	default boolean supportsSchemasInDataManipulation() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInDataManipulation();
	}

	@Override
	default boolean supportsSchemasInProcedureCalls() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInProcedureCalls();
	}

	@Override
	default boolean supportsSchemasInTableDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInTableDefinitions();
	}

	@Override
	default boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInIndexDefinitions();
	}

	@Override
	default boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSchemasInPrivilegeDefinitions();
	}

	@Override
	default boolean supportsCatalogsInDataManipulation() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInDataManipulation();
	}

	@Override
	default boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInProcedureCalls();
	}

	@Override
	default boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInTableDefinitions();
	}

	@Override
	default boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInIndexDefinitions();
	}

	@Override
	default boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCatalogsInPrivilegeDefinitions();
	}

	@Override
	default boolean supportsPositionedDelete() throws SQLException {
		return getWrappedDatabaseMetaData().supportsPositionedDelete();
	}

	@Override
	default boolean supportsPositionedUpdate() throws SQLException {
		return getWrappedDatabaseMetaData().supportsPositionedUpdate();
	}

	@Override
	default boolean supportsSelectForUpdate() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSelectForUpdate();
	}

	@Override
	default boolean supportsStoredProcedures() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStoredProcedures();
	}

	@Override
	default boolean supportsSubqueriesInComparisons() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInComparisons();
	}

	@Override
	default boolean supportsSubqueriesInExists() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInExists();
	}

	@Override
	default boolean supportsSubqueriesInIns() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInIns();
	}

	@Override
	default boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSubqueriesInQuantifieds();
	}

	@Override
	default boolean supportsCorrelatedSubqueries() throws SQLException {
		return getWrappedDatabaseMetaData().supportsCorrelatedSubqueries();
	}

	@Override
	default boolean supportsUnion() throws SQLException {
		return getWrappedDatabaseMetaData().supportsUnion();
	}

	@Override
	default boolean supportsUnionAll() throws SQLException {
		return getWrappedDatabaseMetaData().supportsUnionAll();
	}

	@Override
	default boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenCursorsAcrossCommit();
	}

	@Override
	default boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenCursorsAcrossRollback();
	}

	@Override
	default boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenStatementsAcrossCommit();
	}

	@Override
	default boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return getWrappedDatabaseMetaData().supportsOpenStatementsAcrossRollback();
	}

	@Override
	default int getMaxBinaryLiteralLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxBinaryLiteralLength();
	}

	@Override
	default int getMaxCharLiteralLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCharLiteralLength();
	}

	@Override
	default int getMaxColumnNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnNameLength();
	}

	@Override
	default int getMaxColumnsInGroupBy() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInGroupBy();
	}

	@Override
	default int getMaxColumnsInIndex() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInIndex();
	}

	@Override
	default int getMaxColumnsInOrderBy() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInOrderBy();
	}

	@Override
	default int getMaxColumnsInSelect() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInSelect();
	}

	@Override
	default int getMaxColumnsInTable() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxColumnsInTable();
	}

	@Override
	default int getMaxConnections() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxConnections();
	}

	@Override
	default int getMaxCursorNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCursorNameLength();
	}

	@Override
	default int getMaxIndexLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxIndexLength();
	}

	@Override
	default int getMaxSchemaNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxSchemaNameLength();
	}

	@Override
	default int getMaxProcedureNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxProcedureNameLength();
	}

	@Override
	default int getMaxCatalogNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxCatalogNameLength();
	}

	@Override
	default int getMaxRowSize() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxRowSize();
	}

	@Override
	default boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return getWrappedDatabaseMetaData().doesMaxRowSizeIncludeBlobs();
	}

	@Override
	default int getMaxStatementLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxStatementLength();
	}

	@Override
	default int getMaxStatements() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxStatements();
	}

	@Override
	default int getMaxTableNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxTableNameLength();
	}

	@Override
	default int getMaxTablesInSelect() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxTablesInSelect();
	}

	@Override
	default int getMaxUserNameLength() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxUserNameLength();
	}

	@Override
	default int getDefaultTransactionIsolation() throws SQLException {
		return getWrappedDatabaseMetaData().getDefaultTransactionIsolation();
	}

	@Override
	default boolean supportsTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsTransactions();
	}

	@Override
	default boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return getWrappedDatabaseMetaData().supportsTransactionIsolationLevel(level);
	}

	@Override
	default boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDataDefinitionAndDataManipulationTransactions();
	}

	@Override
	default boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return getWrappedDatabaseMetaData().supportsDataManipulationTransactionsOnly();
	}

	@Override
	default boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return getWrappedDatabaseMetaData().dataDefinitionCausesTransactionCommit();
	}

	@Override
	default boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return getWrappedDatabaseMetaData().dataDefinitionIgnoredInTransactions();
	}

	@Override
	IResultSetWrapper getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) throws SQLException;

	@Override
	IResultSetWrapper getSchemas() throws SQLException;

	@Override
	IResultSetWrapper getCatalogs() throws SQLException;

	@Override
	IResultSetWrapper getTableTypes() throws SQLException;

	@Override
	IResultSetWrapper getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException;

	@Override
	IResultSetWrapper getVersionColumns(String catalog, String schema, String table) throws SQLException;

	@Override
	IResultSetWrapper getPrimaryKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	IResultSetWrapper getImportedKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	IResultSetWrapper getExportedKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	IResultSetWrapper getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException;

	@Override
	IResultSetWrapper getTypeInfo() throws SQLException;

	@Override
	IResultSetWrapper getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException;

	@Override
	default boolean supportsResultSetType(int type) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetType(type);
	}

	@Override
	default boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetConcurrency(type, concurrency);
	}

	@Override
	default boolean ownUpdatesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownUpdatesAreVisible(type);
	}

	@Override
	default boolean ownDeletesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownDeletesAreVisible(type);
	}

	@Override
	default boolean ownInsertsAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().ownInsertsAreVisible(type);
	}

	@Override
	default boolean othersUpdatesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersUpdatesAreVisible(type);
	}

	@Override
	default boolean othersDeletesAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersDeletesAreVisible(type);
	}

	@Override
	default boolean othersInsertsAreVisible(int type) throws SQLException {
		return getWrappedDatabaseMetaData().othersInsertsAreVisible(type);
	}

	@Override
	default boolean updatesAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().updatesAreDetected(type);
	}

	@Override
	default boolean deletesAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().deletesAreDetected(type);
	}

	@Override
	default boolean insertsAreDetected(int type) throws SQLException {
		return getWrappedDatabaseMetaData().insertsAreDetected(type);
	}

	@Override
	default boolean supportsBatchUpdates() throws SQLException {
		return getWrappedDatabaseMetaData().supportsBatchUpdates();
	}

	@Override
	IResultSetWrapper getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException;

	@Override
	IConnectionWrapper getConnection() throws SQLException;

	@Override
	default boolean supportsSavepoints() throws SQLException {
		return getWrappedDatabaseMetaData().supportsSavepoints();
	}

	@Override
	default boolean supportsNamedParameters() throws SQLException {
		return getWrappedDatabaseMetaData().supportsNamedParameters();
	}

	@Override
	default boolean supportsMultipleOpenResults() throws SQLException {
		return getWrappedDatabaseMetaData().supportsMultipleOpenResults();
	}

	@Override
	default boolean supportsGetGeneratedKeys() throws SQLException {
		return getWrappedDatabaseMetaData().supportsGetGeneratedKeys();
	}

	@Override
	IResultSetWrapper getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException;

	@Override
	default boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return getWrappedDatabaseMetaData().supportsResultSetHoldability(holdability);
	}

	@Override
	default int getResultSetHoldability() throws SQLException {
		return getWrappedDatabaseMetaData().getResultSetHoldability();
	}

	@Override
	default int getDatabaseMajorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseMajorVersion();
	}

	@Override
	default int getDatabaseMinorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getDatabaseMinorVersion();
	}

	@Override
	default int getJDBCMajorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getJDBCMajorVersion();
	}

	@Override
	default int getJDBCMinorVersion() throws SQLException {
		return getWrappedDatabaseMetaData().getJDBCMinorVersion();
	}

	@Override
	default int getSQLStateType() throws SQLException {
		return getWrappedDatabaseMetaData().getSQLStateType();
	}

	@Override
	default boolean locatorsUpdateCopy() throws SQLException {
		return getWrappedDatabaseMetaData().locatorsUpdateCopy();
	}

	@Override
	default boolean supportsStatementPooling() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStatementPooling();
	}

	@Override
	default RowIdLifetime getRowIdLifetime() throws SQLException {
		return getWrappedDatabaseMetaData().getRowIdLifetime();
	}

	@Override
	IResultSetWrapper getSchemas(String catalog, String schemaPattern) throws SQLException;

	@Override
	default boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return getWrappedDatabaseMetaData().supportsStoredFunctionsUsingCallSyntax();
	}

	@Override
	default boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return getWrappedDatabaseMetaData().autoCommitFailureClosesAllResultSets();
	}

	@Override
	IResultSetWrapper getClientInfoProperties() throws SQLException;

	@Override
	IResultSetWrapper getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException;

	@Override
	IResultSetWrapper getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException;

	@Override
	default boolean generatedKeyAlwaysReturned() throws SQLException {
		return getWrappedDatabaseMetaData().generatedKeyAlwaysReturned();
	}

	@Override
	default long getMaxLogicalLobSize() throws SQLException {
		return getWrappedDatabaseMetaData().getMaxLogicalLobSize();
	}

	@Override
	default boolean supportsRefCursors() throws SQLException {
		return getWrappedDatabaseMetaData().supportsRefCursors();
	}

	// Java 9: boolean supportsSharding() throws SQLException;
}
