/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
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
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;

/**
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public class ConnectionWrapper implements IConnectionWrapper {

	private final Connection wrapped;

	public ConnectionWrapper(Connection wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Connection getWrapped() {
		return wrapped;
	}

	/**
	 * Creates a new {@link ArrayWrapper}.
	 *
	 * @see  #wrapArray(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	protected ArrayWrapper newArrayWrapper(StatementWrapper stmtWrapper, Array array) {
		return new ArrayWrapper(this, stmtWrapper, array);
	}

	/**
	 * Creates a new {@link BlobWrapper}.
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapper newBlobWrapper(Blob blob) {
		return new BlobWrapper(this, blob);
	}

	/**
	 * Creates a new {@link CallableStatementWrapper}.
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper newCallableStatementWrapper(CallableStatement cstmt) {
		return new CallableStatementWrapper(this, cstmt);
	}

	/**
	 * Creates a new {@link ClobWrapper}.
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	protected ClobWrapper newClobWrapper(Clob clob) {
		return new ClobWrapper(this, clob);
	}

	/**
	 * Creates a new {@link DatabaseMetaDataWrapper}.
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new DatabaseMetaDataWrapper(this, metaData);
	}

	/**
	 * Creates a new {@link NClobWrapper}.
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapper newNClobWrapper(NClob nclob) {
		return new NClobWrapper(this, nclob);
	}

	/**
	 * Creates a new {@link PreparedStatementWrapper}.
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapper newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new PreparedStatementWrapper(this, pstmt);
	}

	/**
	 * Creates a new {@link RefWrapper}.
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	protected RefWrapper newRefWrapper(Ref ref) {
		return new RefWrapper(this, ref);
	}

	/**
	 * Creates a new {@link ResultSetWrapper}.
	 *
	 * @see  #wrapResultSet(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 */
	protected ResultSetWrapper newResultSetWrapper(StatementWrapper stmtWrapper, ResultSet results) {
		return new ResultSetWrapper(this, stmtWrapper, results);
	}

	/**
	 * Creates a new {@link ResultSetMetaDataWrapper}.
	 *
	 * @see  #wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper newResultSetMetaDataWrapper(ResultSetMetaData metaData) {
		return new ResultSetMetaDataWrapper(this, metaData);
	}

	/**
	 * Creates a new {@link RowIdWrapper}.
	 *
	 * @see  #wrapRowId(RowId)
	 */
	protected RowIdWrapper newRowIdWrapper(RowId rowId) {
		return new RowIdWrapper(this, rowId);
	}

	/**
	 * Creates a new {@link SavepointWrapper}.
	 *
	 * @see  #wrapSavepoint(Savepoint)
	 */
	protected SavepointWrapper newSavepointWrapper(Savepoint savepoint) {
		return new SavepointWrapper(this, savepoint);
	}

	/**
	 * Creates a new {@link SQLXMLWrapper}.
	 *
	 * @see  #wrapSQLXML(SQLXML)
	 */
	protected SQLXMLWrapper newSQLXMLWrapper(SQLXML sqlXml) {
		return new SQLXMLWrapper(this, sqlXml);
	}

	/**
	 * Creates a new {@link StatementWrapper}.
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper newStatementWrapper(Statement stmt) {
		return new StatementWrapper(this, stmt);
	}

	/**
	 * Creates a new {@link StructWrapper}.
	 *
	 * @see  #wrapStruct(Struct)
	 */
	protected StructWrapper newStructWrapper(Struct struct) {
		return new StructWrapper(this, struct);
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  #newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 * @see  CallableStatementWrapper#wrapArray(java.sql.Array)
	 * @see  ResultSetWrapper#wrapArray(java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(StatementWrapper stmtWrapper, Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(
				arrayWrapper.getConnectionWrapper() == this
				&& arrayWrapper.getStatementWrapper().orElse(null) == stmtWrapper
			) {
				return arrayWrapper;
			}
		}
		return newArrayWrapper(stmtWrapper, array);
	}

	/**
	 * Unwraps an {@link Array}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapper#unwrapArray(java.sql.Array)
	 * @see  ResultSetWrapper#unwrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(arrayWrapper.getConnectionWrapper() == this) {
				return arrayWrapper.getWrapped();
			}
		}
		return array;
	}

	/**
	 * Wraps a {@link Blob}, if not already wrapped by this wrapper.
	 *
	 * @see  #newBlobWrapper(java.sql.Blob)
	 * @see  CallableStatementWrapper#wrapBlob(java.sql.Blob)
	 * @see  ResultSetWrapper#wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapper wrapBlob(Blob blob) {
		if(blob == null) {
			return null;
		}
		if(blob instanceof BlobWrapper) {
			BlobWrapper blobWrapper = (BlobWrapper)blob;
			if(blobWrapper.getConnectionWrapper() == this) {
				return blobWrapper;
			}
		}
		return newBlobWrapper(blob);
	}

	/**
	 * Unwraps a {@link Blob}, if wrapped by this wrapper.
	 *
	 * @see  BlobWrapper#unwrapBlob(java.sql.Blob)
	 * @see  PreparedStatementWrapper#unwrapBlob(java.sql.Blob)
	 * @see  ResultSetWrapper#unwrapBlob(java.sql.Blob)
	 */
	protected Blob unwrapBlob(Blob blob) {
		if(blob == null) {
			return null;
		}
		if(blob instanceof BlobWrapper) {
			BlobWrapper blobWrapper = (BlobWrapper)blob;
			if(blobWrapper.getConnectionWrapper() == this) {
				return blobWrapper.getWrapped();
			}
		}
		return blob;
	}

	/**
	 * Wraps a {@link CallableStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newCallableStatementWrapper(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper wrapCallableStatement(CallableStatement cstmt) {
		if(cstmt == null) {
			return null;
		}
		if(cstmt instanceof CallableStatementWrapper) {
			CallableStatementWrapper stmtWrapper = (CallableStatementWrapper)cstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newCallableStatementWrapper(cstmt);
	}

	/**
	 * Wraps a {@link Clob}, if not already wrapped by this wrapper.
	 *
	 * @see  #newClobWrapper(java.sql.Clob)
	 * @see  #wrapNClob(java.sql.NClob)
	 * @see  CallableStatementWrapper#wrapClob(java.sql.Clob)
	 * @see  ResultSetWrapper#wrapClob(java.sql.Clob)
	 */
	protected ClobWrapper wrapClob(Clob clob) {
		if(clob == null) {
			return null;
		}
		if(clob instanceof NClob) {
			return wrapNClob((NClob)clob);
		}
		if(clob instanceof ClobWrapper) {
			ClobWrapper clobWrapper = (ClobWrapper)clob;
			if(clobWrapper.getConnectionWrapper() == this) {
				return clobWrapper;
			}
		}
		return newClobWrapper(clob);
	}

	/**
	 * Unwraps a {@link Clob}, if wrapped by this wrapper.
	 *
	 * @see  ClobWrapper#unwrapClob(java.sql.Clob)
	 * @see  PreparedStatementWrapper#unwrapClob(java.sql.Clob)
	 * @see  ResultSetWrapper#unwrapClob(java.sql.Clob)
	 */
	protected Clob unwrapClob(Clob clob) {
		if(clob == null) {
			return null;
		}
		if(clob instanceof ClobWrapper) {
			ClobWrapper clobWrapper = (ClobWrapper)clob;
			if(clobWrapper.getConnectionWrapper() == this) {
				return clobWrapper.getWrapped();
			}
		}
		return clob;
	}

	/**
	 * Wraps a {@link DatabaseMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newDatabaseMetaDataWrapper(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper wrapDatabaseMetaData(DatabaseMetaData metaData) {
		if(metaData instanceof DatabaseMetaDataWrapper) {
			DatabaseMetaDataWrapper metaDataWrapper = (DatabaseMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newDatabaseMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link NClob}, if not already wrapped by this wrapper.
	 *
	 * @see  #newNClobWrapper(java.sql.NClob)
	 * @see  CallableStatementWrapper#wrapNClob(java.sql.NClob)
	 * @see  ResultSetWrapper#wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapper wrapNClob(NClob nclob) {
		if(nclob == null) {
			return null;
		}
		if(nclob instanceof NClobWrapper) {
			NClobWrapper nclobWrapper = (NClobWrapper)nclob;
			if(nclobWrapper.getConnectionWrapper() == this) {
				return nclobWrapper;
			}
		}
		return newNClobWrapper(nclob);
	}

	/**
	 * Unwraps a {@link NClob}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapper#unwrapNClob(java.sql.NClob)
	 * @see  ResultSetWrapper#unwrapNClob(java.sql.NClob)
	 */
	protected NClob unwrapNClob(NClob nclob) {
		if(nclob == null) {
			return null;
		}
		if(nclob instanceof NClobWrapper) {
			NClobWrapper nclobWrapper = (NClobWrapper)nclob;
			if(nclobWrapper.getConnectionWrapper() == this) {
				return nclobWrapper.getWrapped();
			}
		}
		return nclob;
	}

	/**
	 * Wraps a {@link PreparedStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newPreparedStatementWrapper(java.sql.PreparedStatement)
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected PreparedStatementWrapper wrapPreparedStatement(PreparedStatement pstmt) {
		if(pstmt == null) {
			return null;
		}
		if(pstmt instanceof CallableStatement) {
			return wrapCallableStatement((CallableStatement)pstmt);
		}
		if(pstmt instanceof PreparedStatementWrapper) {
			PreparedStatementWrapper stmtWrapper = (PreparedStatementWrapper)pstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newPreparedStatementWrapper(pstmt);
	}

	/**
	 * Wraps a {@link Ref}, if not already wrapped by this wrapper.
	 *
	 * @see  #newRefWrapper(java.sql.Ref)
	 * @see  CallableStatementWrapper#wrapRef(java.sql.Ref)
	 * @see  ResultSetWrapper#wrapRef(java.sql.Ref)
	 */
	protected RefWrapper wrapRef(Ref ref) {
		if(ref == null) {
			return null;
		}
		if(ref instanceof RefWrapper) {
			RefWrapper refWrapper = (RefWrapper)ref;
			if(refWrapper.getConnectionWrapper() == this) {
				return refWrapper;
			}
		}
		return newRefWrapper(ref);
	}

	/**
	 * Unwraps a {@link Ref}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapper#unwrapRef(java.sql.Ref)
	 * @see  ResultSetWrapper#unwrapRef(java.sql.Ref)
	 */
	protected Ref unwrapRef(Ref ref) {
		if(ref == null) {
			return null;
		}
		if(ref instanceof RefWrapper) {
			RefWrapper refWrapper = (RefWrapper)ref;
			if(refWrapper.getConnectionWrapper() == this) {
				return refWrapper.getWrapped();
			}
		}
		return ref;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 * @see  #newResultSetWrapper(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 * @see  ArrayWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  DatabaseMetaDataWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  StatementWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(StatementWrapper stmtWrapper, ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			if(
				resultsWrapper.getConnectionWrapper() == this
				&& resultsWrapper.getStatementWrapper().orElse(null) == stmtWrapper
			) {
				return resultsWrapper;
			}
		}
		Statement stmt = results.getStatement();
		if(
			stmtWrapper == null
			|| stmtWrapper.getWrapped() != stmt
		) {
			stmtWrapper = wrapStatement(stmt);
		}
		return newResultSetWrapper(stmtWrapper, results);
	}

	/**
	 * Wraps a {@link ResultSetMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newResultSetMetaDataWrapper(java.sql.ResultSetMetaData)
	 * @see  PreparedStatementWrapper#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 * @see  ResultSetWrapper#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper wrapResultSetMetaData(ResultSetMetaData metaData) {
		if(metaData == null) {
			return null;
		}
		if(metaData instanceof ResultSetMetaDataWrapper) {
			ResultSetMetaDataWrapper metaDataWrapper = (ResultSetMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newResultSetMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link RowId}, if not already wrapped by this wrapper.
	 *
	 * @see  #newRowIdWrapper(java.sql.RowId)
	 * @see  CallableStatementWrapper#wrapRowId(java.sql.RowId)
	 * @see  ResultSetWrapper#wrapRowId(java.sql.RowId)
	 */
	protected RowIdWrapper wrapRowId(RowId rowId) {
		if(rowId == null) {
			return null;
		}
		if(rowId instanceof RowIdWrapper) {
			RowIdWrapper rowIdWrapper = (RowIdWrapper)rowId;
			if(rowIdWrapper.getConnectionWrapper() == this) {
				return rowIdWrapper;
			}
		}
		return newRowIdWrapper(rowId);
	}

	/**
	 * Unwraps a {@link RowId}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapper#unwrapRowId(java.sql.RowId)
	 * @see  ResultSetWrapper#unwrapRowId(java.sql.RowId)
	 */
	protected RowId unwrapRowId(RowId rowId) {
		if(rowId == null) {
			return null;
		}
		if(rowId instanceof RowIdWrapper) {
			RowIdWrapper rowIdWrapper = (RowIdWrapper)rowId;
			if(rowIdWrapper.getConnectionWrapper() == this) {
				return rowIdWrapper.getWrapped();
			}
		}
		return rowId;
	}

	/**
	 * Wraps a {@link Savepoint}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSavepointWrapper(java.sql.Savepoint)
	 */
	protected SavepointWrapper wrapSavepoint(Savepoint savepoint) {
		if(savepoint == null) {
			return null;
		}
		if(savepoint instanceof SavepointWrapper) {
			SavepointWrapper savepointWrapper = (SavepointWrapper)savepoint;
			if(savepointWrapper.getConnectionWrapper() == this) {
				return savepointWrapper;
			}
		}
		return newSavepointWrapper(savepoint);
	}

	/**
	 * Unwraps a {@link Savepoint}, if wrapped by this wrapper.
	 */
	protected Savepoint unwrapSavepoint(Savepoint savepoint) {
		if(savepoint == null) {
			return null;
		}
		if(savepoint instanceof SavepointWrapper) {
			SavepointWrapper savepointWrapper = (SavepointWrapper)savepoint;
			if(savepointWrapper.getConnectionWrapper() == this) {
				return savepointWrapper.getWrapped();
			}
		}
		return savepoint;
	}

	/**
	 * Wraps a {@link SQLXML}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSQLXMLWrapper(java.sql.SQLXML)
	 * @see  CallableStatementWrapper#wrapSQLXML(java.sql.SQLXML)
	 * @see  ResultSetWrapper#wrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXMLWrapper wrapSQLXML(SQLXML sqlXml) {
		if(sqlXml == null) {
			return null;
		}
		if(sqlXml instanceof SQLXMLWrapper) {
			SQLXMLWrapper sqlXmlWrapper = (SQLXMLWrapper)sqlXml;
			if(sqlXmlWrapper.getConnectionWrapper() == this) {
				return sqlXmlWrapper;
			}
		}
		return newSQLXMLWrapper(sqlXml);
	}

	/**
	 * Unwraps a {@link SQLXML}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapper#unwrapSQLXML(java.sql.SQLXML)
	 * @see  ResultSetWrapper#unwrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXML unwrapSQLXML(SQLXML sqlXml) {
		if(sqlXml == null) {
			return null;
		}
		if(sqlXml instanceof SQLXMLWrapper) {
			SQLXMLWrapper sqlXmlWrapper = (SQLXMLWrapper)sqlXml;
			if(sqlXmlWrapper.getConnectionWrapper() == this) {
				return sqlXmlWrapper.getWrapped();
			}
		}
		return sqlXml;
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newStatementWrapper(java.sql.Statement)
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 * @see  ResultSetWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		if(stmt instanceof PreparedStatement) {
			return wrapPreparedStatement((PreparedStatement)stmt);
		}
		if(stmt instanceof StatementWrapper) {
			StatementWrapper stmtWrapper = (StatementWrapper)stmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newStatementWrapper(stmt);
	}

	/**
	 * Wraps a {@link Struct}, if not already wrapped by this wrapper.
	 *
	 * @see  #newStructWrapper(java.sql.Struct)
	 */
	protected StructWrapper wrapStruct(Struct struct) {
		if(struct == null) {
			return null;
		}
		if(struct instanceof StructWrapper) {
			StructWrapper structWrapper = (StructWrapper)struct;
			if(structWrapper.getConnectionWrapper() == this) {
				return structWrapper;
			}
		}
		return newStructWrapper(struct);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapper createStatement() throws SQLException {
		return wrapStatement(getWrapped().createStatement());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapper prepareCall(String sql) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	@Override
	public DatabaseMetaDataWrapper getMetaData() throws SQLException {
		return wrapDatabaseMetaData(getWrapped().getMetaData());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public SavepointWrapper setSavepoint() throws SQLException {
		return wrapSavepoint(getWrapped().setSavepoint());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public SavepointWrapper setSavepoint(String name) throws SQLException {
		return wrapSavepoint(getWrapped().setSavepoint());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		getWrapped().rollback(unwrapSavepoint(savepoint));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getWrapped().rollback(unwrapSavepoint(savepoint));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, autoGeneratedKeys));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnIndexes));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnNames));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapper createClob() throws SQLException {
		return wrapClob(getWrapped().createClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapper createBlob() throws SQLException {
		return wrapBlob(getWrapped().createBlob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapper createNClob() throws SQLException {
		return wrapNClob(getWrapped().createNClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapper createSQLXML() throws SQLException {
		return wrapSQLXML(getWrapped().createSQLXML());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	@Override
	public ArrayWrapper createArrayOf(String typeName, Object[] elements) throws SQLException {
		return wrapArray(null, getWrapped().createArrayOf(typeName, elements));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStruct(java.sql.Struct)
	 */
	@Override
	public StructWrapper createStruct(String typeName, Object[] attributes) throws SQLException {
		return wrapStruct(getWrapped().createStruct(typeName, attributes));
	}
}
