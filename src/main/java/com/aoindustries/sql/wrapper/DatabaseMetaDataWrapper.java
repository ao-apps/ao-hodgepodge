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
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * Wraps a {@link DatabaseMetaData}.
 *
 * @author  AO Industries, Inc.
 */
public interface DatabaseMetaDataWrapper extends Wrapper, DatabaseMetaData, AutoCloseable {

	/**
	 * Gets the database meta data that is wrapped.
	 */
	@Override
	DatabaseMetaData getWrapped();

	/**
	 * Releases resources associated with this wrapper.
	 */
	@Override
	default void close() throws SQLException {
		// Do nothing by default
	}

	@Override
	default boolean allProceduresAreCallable() throws SQLException {
		return getWrapped().allProceduresAreCallable();
	}

	@Override
	default boolean allTablesAreSelectable() throws SQLException {
		return getWrapped().allTablesAreSelectable();
	}

	/**
	 * @return  When this wrapper is associated with a wrapping driver, inserts the driver prefix into the JDBC URL.
	 *
	 * @see  DriverWrapper#toWrapperUrl(java.lang.String)
	 */
	@Override
	String getURL() throws SQLException;

	@Override
	default String getUserName() throws SQLException {
		return getWrapped().getUserName();
	}

	@Override
	default boolean isReadOnly() throws SQLException {
		return getWrapped().isReadOnly();
	}

	@Override
	default boolean nullsAreSortedHigh() throws SQLException {
		return getWrapped().nullsAreSortedHigh();
	}

	@Override
	default boolean nullsAreSortedLow() throws SQLException {
		return getWrapped().nullsAreSortedLow();
	}

	@Override
	default boolean nullsAreSortedAtStart() throws SQLException {
		return getWrapped().nullsAreSortedAtStart();
	}

	@Override
	default boolean nullsAreSortedAtEnd() throws SQLException {
		return getWrapped().nullsAreSortedAtEnd();
	}

	@Override
	default String getDatabaseProductName() throws SQLException {
		return getWrapped().getDatabaseProductName();
	}

	@Override
	default String getDatabaseProductVersion() throws SQLException {
		return getWrapped().getDatabaseProductVersion();
	}

	/**
	 * @return  When this wrapper is associated with a wrapping driver, uses wrapping-driver-provided modification of
	 *          the wrapped driver's name.
	 *
	 * @see  DriverWrapper#getDriverName(java.lang.String)
	 */
	@Override
	String getDriverName() throws SQLException;

	/**
	 * @return  When this wrapper is associated with a wrapping driver, uses wrapping-driver-provided modification of
	 *          the wrapped driver's version.
	 *
	 * @see  DriverWrapper#getDriverVersion(java.lang.String)
	 */
	@Override
	String getDriverVersion() throws SQLException;

	/**
	 * @return  When this wrapper is associated with a wrapping driver, uses wrapping-driver-provided modification of
	 *          the wrapped driver's version.
	 *
	 * @see  DriverWrapper#getDriverMajorVersion(int)
	 */
	@Override
	int getDriverMajorVersion();

	/**
	 * @return  When this wrapper is associated with a wrapping driver, uses wrapping-driver-provided modification of
	 *          the wrapped driver's version.
	 *
	 * @see  DriverWrapper#getDriverMinorVersion(int)
	 */
	@Override
	int getDriverMinorVersion();

	@Override
	default boolean usesLocalFiles() throws SQLException {
		return getWrapped().usesLocalFiles();
	}

	@Override
	default boolean usesLocalFilePerTable() throws SQLException {
		return getWrapped().usesLocalFilePerTable();
	}

	@Override
	default boolean supportsMixedCaseIdentifiers() throws SQLException {
		return getWrapped().supportsMixedCaseIdentifiers();
	}

	@Override
	default boolean storesUpperCaseIdentifiers() throws SQLException {
		return getWrapped().storesUpperCaseIdentifiers();
	}

	@Override
	default boolean storesLowerCaseIdentifiers() throws SQLException {
		return getWrapped().storesLowerCaseIdentifiers();
	}

	@Override
	default boolean storesMixedCaseIdentifiers() throws SQLException {
		return getWrapped().storesMixedCaseIdentifiers();
	}

	@Override
	default boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrapped().supportsMixedCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return getWrapped().storesUpperCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return getWrapped().storesLowerCaseQuotedIdentifiers();
	}

	@Override
	default boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return getWrapped().storesMixedCaseQuotedIdentifiers();
	}

	@Override
	default String getIdentifierQuoteString() throws SQLException {
		return getWrapped().getIdentifierQuoteString();
	}

	@Override
	default String getSQLKeywords() throws SQLException {
		return getWrapped().getSQLKeywords();
	}

	@Override
	default String getNumericFunctions() throws SQLException {
		return getWrapped().getNumericFunctions();
	}

	@Override
	default String getStringFunctions() throws SQLException {
		return getWrapped().getStringFunctions();
	}

	@Override
	default String getSystemFunctions() throws SQLException {
		return getWrapped().getSystemFunctions();
	}

	@Override
	default String getTimeDateFunctions() throws SQLException {
		return getWrapped().getTimeDateFunctions();
	}

	@Override
	default String getSearchStringEscape() throws SQLException {
		return getWrapped().getSearchStringEscape();
	}

	@Override
	default String getExtraNameCharacters() throws SQLException {
		return getWrapped().getExtraNameCharacters();
	}

	@Override
	default boolean supportsAlterTableWithAddColumn() throws SQLException {
		return getWrapped().supportsAlterTableWithAddColumn();
	}

	@Override
	default boolean supportsAlterTableWithDropColumn() throws SQLException {
		return getWrapped().supportsAlterTableWithDropColumn();
	}

	@Override
	default boolean supportsColumnAliasing() throws SQLException {
		return getWrapped().supportsColumnAliasing();
	}

	@Override
	default boolean nullPlusNonNullIsNull() throws SQLException {
		return getWrapped().nullPlusNonNullIsNull();
	}

	@Override
	default boolean supportsConvert() throws SQLException {
		return getWrapped().supportsConvert();
	}

	@Override
	default boolean supportsConvert(int fromType, int toType) throws SQLException {
		return getWrapped().supportsConvert(fromType, toType);
	}

	@Override
	default boolean supportsTableCorrelationNames() throws SQLException {
		return getWrapped().supportsTableCorrelationNames();
	}

	@Override
	default boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return getWrapped().supportsDifferentTableCorrelationNames();
	}

	@Override
	default boolean supportsExpressionsInOrderBy() throws SQLException {
		return getWrapped().supportsExpressionsInOrderBy();
	}

	@Override
	default boolean supportsOrderByUnrelated() throws SQLException {
		return getWrapped().supportsOrderByUnrelated();
	}

	@Override
	default boolean supportsGroupBy() throws SQLException {
		return getWrapped().supportsGroupBy();
	}

	@Override
	default boolean supportsGroupByUnrelated() throws SQLException {
		return getWrapped().supportsGroupByUnrelated();
	}

	@Override
	default boolean supportsGroupByBeyondSelect() throws SQLException {
		return getWrapped().supportsGroupByBeyondSelect();
	}

	@Override
	default boolean supportsLikeEscapeClause() throws SQLException {
		return getWrapped().supportsLikeEscapeClause();
	}

	@Override
	default boolean supportsMultipleResultSets() throws SQLException {
		return getWrapped().supportsMultipleResultSets();
	}

	@Override
	default boolean supportsMultipleTransactions() throws SQLException {
		return getWrapped().supportsMultipleTransactions();
	}

	@Override
	default boolean supportsNonNullableColumns() throws SQLException {
		return getWrapped().supportsNonNullableColumns();
	}

	@Override
	default boolean supportsMinimumSQLGrammar() throws SQLException {
		return getWrapped().supportsMinimumSQLGrammar();
	}

	@Override
	default boolean supportsCoreSQLGrammar() throws SQLException {
		return getWrapped().supportsCoreSQLGrammar();
	}

	@Override
	default boolean supportsExtendedSQLGrammar() throws SQLException {
		return getWrapped().supportsExtendedSQLGrammar();
	}

	@Override
	default boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return getWrapped().supportsANSI92EntryLevelSQL();
	}

	@Override
	default boolean supportsANSI92IntermediateSQL() throws SQLException {
		return getWrapped().supportsANSI92IntermediateSQL();
	}

	@Override
	default boolean supportsANSI92FullSQL() throws SQLException {
		return getWrapped().supportsANSI92FullSQL();
	}

	@Override
	default boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return getWrapped().supportsIntegrityEnhancementFacility();
	}

	@Override
	default boolean supportsOuterJoins() throws SQLException {
		return getWrapped().supportsOuterJoins();
	}

	@Override
	default boolean supportsFullOuterJoins() throws SQLException {
		return getWrapped().supportsFullOuterJoins();
	}

	@Override
	default boolean supportsLimitedOuterJoins() throws SQLException {
		return getWrapped().supportsLimitedOuterJoins();
	}

	@Override
	default String getSchemaTerm() throws SQLException {
		return getWrapped().getSchemaTerm();
	}

	@Override
	default String getProcedureTerm() throws SQLException {
		return getWrapped().getProcedureTerm();
	}

	@Override
	default String getCatalogTerm() throws SQLException {
		return getWrapped().getCatalogTerm();
	}

	@Override
	default boolean isCatalogAtStart() throws SQLException {
		return getWrapped().isCatalogAtStart();
	}

	@Override
	default String getCatalogSeparator() throws SQLException {
		return getWrapped().getCatalogSeparator();
	}

	@Override
	default boolean supportsSchemasInDataManipulation() throws SQLException {
		return getWrapped().supportsSchemasInDataManipulation();
	}

	@Override
	default boolean supportsSchemasInProcedureCalls() throws SQLException {
		return getWrapped().supportsSchemasInProcedureCalls();
	}

	@Override
	default boolean supportsSchemasInTableDefinitions() throws SQLException {
		return getWrapped().supportsSchemasInTableDefinitions();
	}

	@Override
	default boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return getWrapped().supportsSchemasInIndexDefinitions();
	}

	@Override
	default boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return getWrapped().supportsSchemasInPrivilegeDefinitions();
	}

	@Override
	default boolean supportsCatalogsInDataManipulation() throws SQLException {
		return getWrapped().supportsCatalogsInDataManipulation();
	}

	@Override
	default boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return getWrapped().supportsCatalogsInProcedureCalls();
	}

	@Override
	default boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return getWrapped().supportsCatalogsInTableDefinitions();
	}

	@Override
	default boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return getWrapped().supportsCatalogsInIndexDefinitions();
	}

	@Override
	default boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return getWrapped().supportsCatalogsInPrivilegeDefinitions();
	}

	@Override
	default boolean supportsPositionedDelete() throws SQLException {
		return getWrapped().supportsPositionedDelete();
	}

	@Override
	default boolean supportsPositionedUpdate() throws SQLException {
		return getWrapped().supportsPositionedUpdate();
	}

	@Override
	default boolean supportsSelectForUpdate() throws SQLException {
		return getWrapped().supportsSelectForUpdate();
	}

	@Override
	default boolean supportsStoredProcedures() throws SQLException {
		return getWrapped().supportsStoredProcedures();
	}

	@Override
	default boolean supportsSubqueriesInComparisons() throws SQLException {
		return getWrapped().supportsSubqueriesInComparisons();
	}

	@Override
	default boolean supportsSubqueriesInExists() throws SQLException {
		return getWrapped().supportsSubqueriesInExists();
	}

	@Override
	default boolean supportsSubqueriesInIns() throws SQLException {
		return getWrapped().supportsSubqueriesInIns();
	}

	@Override
	default boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return getWrapped().supportsSubqueriesInQuantifieds();
	}

	@Override
	default boolean supportsCorrelatedSubqueries() throws SQLException {
		return getWrapped().supportsCorrelatedSubqueries();
	}

	@Override
	default boolean supportsUnion() throws SQLException {
		return getWrapped().supportsUnion();
	}

	@Override
	default boolean supportsUnionAll() throws SQLException {
		return getWrapped().supportsUnionAll();
	}

	@Override
	default boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return getWrapped().supportsOpenCursorsAcrossCommit();
	}

	@Override
	default boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return getWrapped().supportsOpenCursorsAcrossRollback();
	}

	@Override
	default boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return getWrapped().supportsOpenStatementsAcrossCommit();
	}

	@Override
	default boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return getWrapped().supportsOpenStatementsAcrossRollback();
	}

	@Override
	default int getMaxBinaryLiteralLength() throws SQLException {
		return getWrapped().getMaxBinaryLiteralLength();
	}

	@Override
	default int getMaxCharLiteralLength() throws SQLException {
		return getWrapped().getMaxCharLiteralLength();
	}

	@Override
	default int getMaxColumnNameLength() throws SQLException {
		return getWrapped().getMaxColumnNameLength();
	}

	@Override
	default int getMaxColumnsInGroupBy() throws SQLException {
		return getWrapped().getMaxColumnsInGroupBy();
	}

	@Override
	default int getMaxColumnsInIndex() throws SQLException {
		return getWrapped().getMaxColumnsInIndex();
	}

	@Override
	default int getMaxColumnsInOrderBy() throws SQLException {
		return getWrapped().getMaxColumnsInOrderBy();
	}

	@Override
	default int getMaxColumnsInSelect() throws SQLException {
		return getWrapped().getMaxColumnsInSelect();
	}

	@Override
	default int getMaxColumnsInTable() throws SQLException {
		return getWrapped().getMaxColumnsInTable();
	}

	@Override
	default int getMaxConnections() throws SQLException {
		return getWrapped().getMaxConnections();
	}

	@Override
	default int getMaxCursorNameLength() throws SQLException {
		return getWrapped().getMaxCursorNameLength();
	}

	@Override
	default int getMaxIndexLength() throws SQLException {
		return getWrapped().getMaxIndexLength();
	}

	@Override
	default int getMaxSchemaNameLength() throws SQLException {
		return getWrapped().getMaxSchemaNameLength();
	}

	@Override
	default int getMaxProcedureNameLength() throws SQLException {
		return getWrapped().getMaxProcedureNameLength();
	}

	@Override
	default int getMaxCatalogNameLength() throws SQLException {
		return getWrapped().getMaxCatalogNameLength();
	}

	@Override
	default int getMaxRowSize() throws SQLException {
		return getWrapped().getMaxRowSize();
	}

	@Override
	default boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return getWrapped().doesMaxRowSizeIncludeBlobs();
	}

	@Override
	default int getMaxStatementLength() throws SQLException {
		return getWrapped().getMaxStatementLength();
	}

	@Override
	default int getMaxStatements() throws SQLException {
		return getWrapped().getMaxStatements();
	}

	@Override
	default int getMaxTableNameLength() throws SQLException {
		return getWrapped().getMaxTableNameLength();
	}

	@Override
	default int getMaxTablesInSelect() throws SQLException {
		return getWrapped().getMaxTablesInSelect();
	}

	@Override
	default int getMaxUserNameLength() throws SQLException {
		return getWrapped().getMaxUserNameLength();
	}

	@Override
	default int getDefaultTransactionIsolation() throws SQLException {
		return getWrapped().getDefaultTransactionIsolation();
	}

	@Override
	default boolean supportsTransactions() throws SQLException {
		return getWrapped().supportsTransactions();
	}

	@Override
	default boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return getWrapped().supportsTransactionIsolationLevel(level);
	}

	@Override
	default boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return getWrapped().supportsDataDefinitionAndDataManipulationTransactions();
	}

	@Override
	default boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return getWrapped().supportsDataManipulationTransactionsOnly();
	}

	@Override
	default boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return getWrapped().dataDefinitionCausesTransactionCommit();
	}

	@Override
	default boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return getWrapped().dataDefinitionIgnoredInTransactions();
	}

	@Override
	ResultSetWrapper getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) throws SQLException;

	@Override
	ResultSetWrapper getSchemas() throws SQLException;

	@Override
	ResultSetWrapper getCatalogs() throws SQLException;

	@Override
	ResultSetWrapper getTableTypes() throws SQLException;

	@Override
	ResultSetWrapper getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException;

	@Override
	ResultSetWrapper getVersionColumns(String catalog, String schema, String table) throws SQLException;

	@Override
	ResultSetWrapper getPrimaryKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	ResultSetWrapper getImportedKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	ResultSetWrapper getExportedKeys(String catalog, String schema, String table) throws SQLException;

	@Override
	ResultSetWrapper getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException;

	@Override
	ResultSetWrapper getTypeInfo() throws SQLException;

	@Override
	ResultSetWrapper getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException;

	@Override
	default boolean supportsResultSetType(int type) throws SQLException {
		return getWrapped().supportsResultSetType(type);
	}

	@Override
	default boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return getWrapped().supportsResultSetConcurrency(type, concurrency);
	}

	@Override
	default boolean ownUpdatesAreVisible(int type) throws SQLException {
		return getWrapped().ownUpdatesAreVisible(type);
	}

	@Override
	default boolean ownDeletesAreVisible(int type) throws SQLException {
		return getWrapped().ownDeletesAreVisible(type);
	}

	@Override
	default boolean ownInsertsAreVisible(int type) throws SQLException {
		return getWrapped().ownInsertsAreVisible(type);
	}

	@Override
	default boolean othersUpdatesAreVisible(int type) throws SQLException {
		return getWrapped().othersUpdatesAreVisible(type);
	}

	@Override
	default boolean othersDeletesAreVisible(int type) throws SQLException {
		return getWrapped().othersDeletesAreVisible(type);
	}

	@Override
	default boolean othersInsertsAreVisible(int type) throws SQLException {
		return getWrapped().othersInsertsAreVisible(type);
	}

	@Override
	default boolean updatesAreDetected(int type) throws SQLException {
		return getWrapped().updatesAreDetected(type);
	}

	@Override
	default boolean deletesAreDetected(int type) throws SQLException {
		return getWrapped().deletesAreDetected(type);
	}

	@Override
	default boolean insertsAreDetected(int type) throws SQLException {
		return getWrapped().insertsAreDetected(type);
	}

	@Override
	default boolean supportsBatchUpdates() throws SQLException {
		return getWrapped().supportsBatchUpdates();
	}

	@Override
	ResultSetWrapper getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException;

	@Override
	ConnectionWrapper getConnection() throws SQLException;

	@Override
	default boolean supportsSavepoints() throws SQLException {
		return getWrapped().supportsSavepoints();
	}

	@Override
	default boolean supportsNamedParameters() throws SQLException {
		return getWrapped().supportsNamedParameters();
	}

	@Override
	default boolean supportsMultipleOpenResults() throws SQLException {
		return getWrapped().supportsMultipleOpenResults();
	}

	@Override
	default boolean supportsGetGeneratedKeys() throws SQLException {
		return getWrapped().supportsGetGeneratedKeys();
	}

	@Override
	ResultSetWrapper getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException;

	@Override
	default boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return getWrapped().supportsResultSetHoldability(holdability);
	}

	@Override
	default int getResultSetHoldability() throws SQLException {
		return getWrapped().getResultSetHoldability();
	}

	@Override
	default int getDatabaseMajorVersion() throws SQLException {
		return getWrapped().getDatabaseMajorVersion();
	}

	@Override
	default int getDatabaseMinorVersion() throws SQLException {
		return getWrapped().getDatabaseMinorVersion();
	}

	/**
	 * @return  The lower of the wrapped JDBC version and this API JDBC version (currently JDBC 4.2 for Java 8).
	 */
	// Java 9: 4.3
	@Override
	default int getJDBCMajorVersion() throws SQLException {
		final int MAJOR = 4;
		return Math.min(MAJOR, getWrapped().getJDBCMajorVersion());
	}

	/**
	 * @return  The lower of the wrapped JDBC version and this API JDBC version (currently JDBC 4.2 for Java 8).
	 */
	// Java 9: 4.3
	@Override
	default int getJDBCMinorVersion() throws SQLException {
		final int MAJOR = 4, MINOR = 2;
		DatabaseMetaData wrapped = getWrapped();
		int wrappedMajor = wrapped.getJDBCMajorVersion();
		if(wrappedMajor < MAJOR) {
			// Wrapped has lower major, use its minor directly
			return wrapped.getJDBCMinorVersion();
		} else if(wrappedMajor > MAJOR) {
			// Wrapped has higher major, use our minor directly
			return MINOR;
		} else {
			// Same major, use lower minor
			assert wrappedMajor == MAJOR;
			return Math.min(MINOR, wrapped.getJDBCMinorVersion());
		}
	}

	@Override
	default int getSQLStateType() throws SQLException {
		return getWrapped().getSQLStateType();
	}

	@Override
	default boolean locatorsUpdateCopy() throws SQLException {
		return getWrapped().locatorsUpdateCopy();
	}

	@Override
	default boolean supportsStatementPooling() throws SQLException {
		return getWrapped().supportsStatementPooling();
	}

	@Override
	default RowIdLifetime getRowIdLifetime() throws SQLException {
		return getWrapped().getRowIdLifetime();
	}

	@Override
	ResultSetWrapper getSchemas(String catalog, String schemaPattern) throws SQLException;

	@Override
	default boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return getWrapped().supportsStoredFunctionsUsingCallSyntax();
	}

	@Override
	default boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return getWrapped().autoCommitFailureClosesAllResultSets();
	}

	@Override
	ResultSetWrapper getClientInfoProperties() throws SQLException;

	@Override
	ResultSetWrapper getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException;

	@Override
	ResultSetWrapper getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException;

	@Override
	default boolean generatedKeyAlwaysReturned() throws SQLException {
		return getWrapped().generatedKeyAlwaysReturned();
	}

	@Override
	default long getMaxLogicalLobSize() throws SQLException {
		return getWrapped().getMaxLogicalLobSize();
	}

	@Override
	default boolean supportsRefCursors() throws SQLException {
		return getWrapped().supportsRefCursors();
	}

	// Java 9: boolean supportsSharding() throws SQLException;

	// Note: When going to JDBC version above 4.2, update getJDBCMajorVersion() and getJDBCMinorVersion() above.
}
