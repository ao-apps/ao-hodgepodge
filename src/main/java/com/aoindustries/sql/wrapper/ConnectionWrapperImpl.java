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
package com.aoindustries.sql.wrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public class ConnectionWrapperImpl implements ConnectionWrapper {

	private final DriverWrapper driver;
	private final Connection wrapped;

	public ConnectionWrapperImpl(DriverWrapper driverWrapper, Connection wrapped) {
		this.driver = driverWrapper;
		this.wrapped = wrapped;
	}

	public ConnectionWrapperImpl(Connection wrapped) {
		this(null, wrapped);
	}

	protected Optional<? extends DriverWrapper> getDriver() {
		return Optional.ofNullable(driver);
	}

	@Override
	public Connection getWrapped() {
		return wrapped;
	}

	/**
	 * Creates a new {@link ArrayWrapperImpl}.
	 *
	 * @see  #wrapArray(com.aoindustries.sql.StatementWrapperImpl, java.sql.Array)
	 */
	protected ArrayWrapperImpl newArrayWrapper(StatementWrapperImpl stmtWrapper, Array array) {
		return new ArrayWrapperImpl(this, stmtWrapper, array);
	}

	/**
	 * Creates a new {@link BlobWrapperImpl}.
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapperImpl newBlobWrapper(Blob blob) {
		return new BlobWrapperImpl(this, blob);
	}

	/**
	 * Creates a new {@link CallableStatementWrapperImpl}.
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapperImpl newCallableStatementWrapper(CallableStatement cstmt) {
		return new CallableStatementWrapperImpl(this, cstmt);
	}

	/**
	 * Creates a new {@link ClobWrapperImpl}.
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	protected ClobWrapperImpl newClobWrapper(Clob clob) {
		return new ClobWrapperImpl(this, clob);
	}

	/**
	 * Creates a new {@link DatabaseMetaDataWrapperImpl}.
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapperImpl newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new DatabaseMetaDataWrapperImpl(this, metaData);
	}

	/**
	 * Creates a new {@link InputStreamWrapper}.
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper newInputStreamWrapper(InputStream in) {
		return new InputStreamWrapper(this, in);
	}

	/**
	 * Creates a new {@link NClobWrapperImpl}.
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapperImpl newNClobWrapper(NClob nclob) {
		return new NClobWrapperImpl(this, nclob);
	}

	/**
	 * Creates a new {@link OutputStreamWrapper}.
	 *
	 * @see  #wrapOutputStream(java.io.OutputStream)
	 */
	protected OutputStreamWrapper newOutputStreamWrapper(OutputStream out) {
		return new OutputStreamWrapper(this, out);
	}

	/**
	 * Creates a new {@link ParameterMetaDataWrapperImpl}.
	 *
	 * @see  #wrapParameterMetaData(java.sql.ParameterMetaData)
	 */
	protected ParameterMetaDataWrapperImpl newParameterMetaDataWrapper(ParameterMetaData metaData) {
		return new ParameterMetaDataWrapperImpl(this, metaData);
	}

	/**
	 * Creates a new {@link PreparedStatementWrapperImpl}.
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapperImpl newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new PreparedStatementWrapperImpl(this, pstmt);
	}

	/**
	 * Creates a new {@link ReaderWrapper}.
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	protected ReaderWrapper newReaderWrapper(Reader in) {
		return new ReaderWrapper(this, in);
	}

	/**
	 * Creates a new {@link RefWrapperImpl}.
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	protected RefWrapperImpl newRefWrapper(Ref ref) {
		return new RefWrapperImpl(this, ref);
	}

	/**
	 * Creates a new {@link ResultSetWrapperImpl}.
	 *
	 * @see  #wrapResultSet(com.aoindustries.sql.StatementWrapperImpl, java.sql.ResultSet)
	 */
	protected ResultSetWrapperImpl newResultSetWrapper(StatementWrapperImpl stmtWrapper, ResultSet results) {
		return new ResultSetWrapperImpl(this, stmtWrapper, results);
	}

	/**
	 * Creates a new {@link ResultSetMetaDataWrapperImpl}.
	 *
	 * @see  #wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapperImpl newResultSetMetaDataWrapper(ResultSetMetaData metaData) {
		return new ResultSetMetaDataWrapperImpl(this, metaData);
	}

	/**
	 * Creates a new {@link RowIdWrapperImpl}.
	 *
	 * @see  #wrapRowId(RowId)
	 */
	protected RowIdWrapperImpl newRowIdWrapper(RowId rowId) {
		return new RowIdWrapperImpl(this, rowId);
	}

	/**
	 * Creates a new {@link SQLDataWrapperImpl}.
	 *
	 * @see  #wrapSQLData(java.sql.SQLData)
	 */
	protected SQLDataWrapperImpl newSQLDataWrapper(SQLData sqlData) {
		return new SQLDataWrapperImpl(this, sqlData);
	}

	/**
	 * Creates a new {@link SQLInputWrapperImpl}.
	 *
	 * @see  #wrapSQLInput(java.sql.SQLInput)
	 */
	protected SQLInputWrapperImpl newSQLInputWrapper(SQLInput sqlInput) {
		return new SQLInputWrapperImpl(this, sqlInput);
	}

	/**
	 * Creates a new {@link SQLOutputWrapperImpl}.
	 *
	 * @see  #wrapSQLOutput(java.sql.SQLOutput)
	 */
	protected SQLOutputWrapperImpl newSQLOutputWrapper(SQLOutput sqlOutput) {
		return new SQLOutputWrapperImpl(this, sqlOutput);
	}

	/**
	 * Creates a new {@link SQLXMLWrapperImpl}.
	 *
	 * @see  #wrapSQLXML(SQLXML)
	 */
	protected SQLXMLWrapperImpl newSQLXMLWrapper(SQLXML sqlXml) {
		return new SQLXMLWrapperImpl(this, sqlXml);
	}

	/**
	 * Creates a new {@link SavepointWrapperImpl}.
	 *
	 * @see  #wrapSavepoint(Savepoint)
	 */
	protected SavepointWrapperImpl newSavepointWrapper(Savepoint savepoint) {
		return new SavepointWrapperImpl(this, savepoint);
	}

	/**
	 * Creates a new {@link StatementWrapperImpl}.
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapperImpl newStatementWrapper(Statement stmt) {
		return new StatementWrapperImpl(this, stmt);
	}

	/**
	 * Creates a new {@link StructWrapperImpl}.
	 *
	 * @see  #wrapStruct(Struct)
	 */
	protected StructWrapperImpl newStructWrapper(Struct struct) {
		return new StructWrapperImpl(this, struct);
	}

	/**
	 * Creates a new {@link WriterWrapper}.
	 *
	 * @see  #wrapWriter(java.io.Writer)
	 */
	protected WriterWrapper newWriterWrapper(Writer out) {
		return new WriterWrapper(this, out);
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  #newArrayWrapper(com.aoindustries.sql.StatementWrapperImpl, java.sql.Array)
	 * @see  CallableStatementWrapperImpl#wrapArray(java.sql.Array)
	 * @see  ResultSetWrapperImpl#wrapArray(java.sql.Array)
	 * @see  SQLInputWrapperImpl#wrapArray(java.sql.Array)
	 */
	protected ArrayWrapperImpl wrapArray(StatementWrapperImpl stmtWrapper, Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapperImpl) {
			ArrayWrapperImpl arrayWrapper = (ArrayWrapperImpl)array;
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
	 * @see  PreparedStatementWrapperImpl#unwrapArray(java.sql.Array)
	 * @see  ResultSetWrapperImpl#unwrapArray(java.sql.Array)
	 * @see  SQLOutputWrapperImpl#unwrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapperImpl) {
			ArrayWrapperImpl arrayWrapper = (ArrayWrapperImpl)array;
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
	 * @see  CallableStatementWrapperImpl#wrapBlob(java.sql.Blob)
	 * @see  ResultSetWrapperImpl#wrapBlob(java.sql.Blob)
	 * @see  SQLInputWrapperImpl#wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapperImpl wrapBlob(Blob blob) {
		if(blob == null) {
			return null;
		}
		if(blob instanceof BlobWrapperImpl) {
			BlobWrapperImpl blobWrapper = (BlobWrapperImpl)blob;
			if(blobWrapper.getConnectionWrapper() == this) {
				return blobWrapper;
			}
		}
		return newBlobWrapper(blob);
	}

	/**
	 * Unwraps a {@link Blob}, if wrapped by this wrapper.
	 *
	 * @see  BlobWrapperImpl#unwrapBlob(java.sql.Blob)
	 * @see  PreparedStatementWrapperImpl#unwrapBlob(java.sql.Blob)
	 * @see  ResultSetWrapperImpl#unwrapBlob(java.sql.Blob)
	 * @see  SQLOutputWrapperImpl#unwrapBlob(java.sql.Blob)
	 */
	protected Blob unwrapBlob(Blob blob) {
		if(blob == null) {
			return null;
		}
		if(blob instanceof BlobWrapperImpl) {
			BlobWrapperImpl blobWrapper = (BlobWrapperImpl)blob;
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
	protected CallableStatementWrapperImpl wrapCallableStatement(CallableStatement cstmt) {
		if(cstmt == null) {
			return null;
		}
		if(cstmt instanceof CallableStatementWrapperImpl) {
			CallableStatementWrapperImpl stmtWrapper = (CallableStatementWrapperImpl)cstmt;
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
	 * @see  CallableStatementWrapperImpl#wrapClob(java.sql.Clob)
	 * @see  ResultSetWrapperImpl#wrapClob(java.sql.Clob)
	 * @see  SQLInputWrapperImpl#wrapClob(java.sql.Clob)
	 */
	protected ClobWrapperImpl wrapClob(Clob clob) {
		if(clob == null) {
			return null;
		}
		if(clob instanceof NClob) {
			return wrapNClob((NClob)clob);
		}
		if(clob instanceof ClobWrapperImpl) {
			ClobWrapperImpl clobWrapper = (ClobWrapperImpl)clob;
			if(clobWrapper.getConnectionWrapper() == this) {
				return clobWrapper;
			}
		}
		return newClobWrapper(clob);
	}

	/**
	 * Unwraps a {@link Clob}, if wrapped by this wrapper.
	 *
	 * @see  ClobWrapperImpl#unwrapClob(java.sql.Clob)
	 * @see  PreparedStatementWrapperImpl#unwrapClob(java.sql.Clob)
	 * @see  ResultSetWrapperImpl#unwrapClob(java.sql.Clob)
	 * @see  SQLOutputWrapperImpl#unwrapClob(java.sql.Clob)
	 */
	protected Clob unwrapClob(Clob clob) {
		if(clob == null) {
			return null;
		}
		if(clob instanceof ClobWrapperImpl) {
			ClobWrapperImpl clobWrapper = (ClobWrapperImpl)clob;
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
	protected DatabaseMetaDataWrapperImpl wrapDatabaseMetaData(DatabaseMetaData metaData) {
		if(metaData instanceof DatabaseMetaDataWrapperImpl) {
			DatabaseMetaDataWrapperImpl metaDataWrapper = (DatabaseMetaDataWrapperImpl)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newDatabaseMetaDataWrapper(metaData);
	}

	/**
	 * Wraps an {@link InputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  #newInputStreamWrapper(java.io.InputStream)
	 * @see  BlobWrapperImpl#wrapInputStream(java.io.InputStream)
	 * @see  ClobWrapperImpl#wrapInputStream(java.io.InputStream)
	 * @see  ResultSetWrapperImpl#wrapInputStream(java.io.InputStream)
	 * @see  SQLInputWrapperImpl#wrapInputStream(java.io.InputStream)
	 * @see  SQLXMLWrapperImpl#wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper wrapInputStream(InputStream in) {
		if(in == null) {
			return null;
		}
		if(in instanceof InputStreamWrapper) {
			InputStreamWrapper inWrapper = (InputStreamWrapper)in;
			if(inWrapper.getConnectionWrapper() == this) {
				return inWrapper;
			}
		}
		return newInputStreamWrapper(in);
	}

	/**
	 * Unwraps an {@link InputStream}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapInputStream(java.io.InputStream)
	 * @see  ResultSetWrapperImpl#unwrapInputStream(java.io.InputStream)
	 * @see  SQLOutputWrapperImpl#unwrapInputStream(java.io.InputStream)
	 */
	protected InputStream unwrapInputStream(InputStream in) {
		if(in == null) {
			return null;
		}
		if(in instanceof InputStreamWrapper) {
			InputStreamWrapper inWrapper = (InputStreamWrapper)in;
			if(inWrapper.getConnectionWrapper() == this) {
				return inWrapper.getWrapped();
			}
		}
		return in;
	}

	/**
	 * Wraps a {@link NClob}, if not already wrapped by this wrapper.
	 *
	 * @see  #newNClobWrapper(java.sql.NClob)
	 * @see  CallableStatementWrapperImpl#wrapNClob(java.sql.NClob)
	 * @see  ResultSetWrapperImpl#wrapNClob(java.sql.NClob)
	 * @see  SQLInputWrapperImpl#wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapperImpl wrapNClob(NClob nclob) {
		if(nclob == null) {
			return null;
		}
		if(nclob instanceof NClobWrapperImpl) {
			NClobWrapperImpl nclobWrapper = (NClobWrapperImpl)nclob;
			if(nclobWrapper.getConnectionWrapper() == this) {
				return nclobWrapper;
			}
		}
		return newNClobWrapper(nclob);
	}

	/**
	 * Unwraps a {@link NClob}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapNClob(java.sql.NClob)
	 * @see  ResultSetWrapperImpl#unwrapNClob(java.sql.NClob)
	 * @see  SQLOutputWrapperImpl#unwrapNClob(java.sql.NClob)
	 */
	protected NClob unwrapNClob(NClob nclob) {
		if(nclob == null) {
			return null;
		}
		if(nclob instanceof NClobWrapperImpl) {
			NClobWrapperImpl nclobWrapper = (NClobWrapperImpl)nclob;
			if(nclobWrapper.getConnectionWrapper() == this) {
				return nclobWrapper.getWrapped();
			}
		}
		return nclob;
	}

	/**
	 * Wraps an {@link OutputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  #newOutputStreamWrapper(java.io.OutputStream)
	 * @see  BlobWrapperImpl#wrapOutputStream(java.io.OutputStream)
	 * @see  ClobWrapperImpl#wrapOutputStream(java.io.OutputStream)
	 * @see  SQLXMLWrapperImpl#wrapOutputStream(java.io.OutputStream)
	 */
	protected OutputStreamWrapper wrapOutputStream(OutputStream out) {
		if(out == null) {
			return null;
		}
		if(out instanceof OutputStreamWrapper) {
			OutputStreamWrapper outWrapper = (OutputStreamWrapper)out;
			if(outWrapper.getConnectionWrapper() == this) {
				return outWrapper;
			}
		}
		return newOutputStreamWrapper(out);
	}

	/**
	 * Wraps a {@link ParameterMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newParameterMetaDataWrapper(java.sql.ParameterMetaData)
	 * @see  PreparedStatementWrapperImpl#wrapParameterMetaData(java.sql.ParameterMetaData)
	 */
	protected ParameterMetaDataWrapperImpl wrapParameterMetaData(ParameterMetaData metaData) {
		if(metaData == null) {
			return null;
		}
		if(metaData instanceof ParameterMetaDataWrapperImpl) {
			ParameterMetaDataWrapperImpl metaDataWrapper = (ParameterMetaDataWrapperImpl)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newParameterMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link PreparedStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newPreparedStatementWrapper(java.sql.PreparedStatement)
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected PreparedStatementWrapperImpl wrapPreparedStatement(PreparedStatement pstmt) {
		if(pstmt == null) {
			return null;
		}
		if(pstmt instanceof CallableStatement) {
			return wrapCallableStatement((CallableStatement)pstmt);
		}
		if(pstmt instanceof PreparedStatementWrapperImpl) {
			PreparedStatementWrapperImpl stmtWrapper = (PreparedStatementWrapperImpl)pstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newPreparedStatementWrapper(pstmt);
	}

	/**
	 * Wraps a {@link Reader}, if not already wrapped by this wrapper.
	 *
	 * @see  #newReaderWrapper(java.io.Reader)
	 * @see  CallableStatementWrapperImpl#wrapReader(java.io.Reader)
	 * @see  ClobWrapperImpl#wrapReader(java.io.Reader)
	 * @see  ResultSetWrapperImpl#wrapReader(java.io.Reader)
	 * @see  SQLInputWrapperImpl#wrapReader(java.io.Reader)
	 * @see  SQLXMLWrapperImpl#wrapReader(java.io.Reader)
	 */
	protected ReaderWrapper wrapReader(Reader in) {
		if(in == null) {
			return null;
		}
		if(in instanceof ReaderWrapper) {
			ReaderWrapper inWrapper = (ReaderWrapper)in;
			if(inWrapper.getConnectionWrapper() == this) {
				return inWrapper;
			}
		}
		return newReaderWrapper(in);
	}

	/**
	 * Unwraps a {@link Reader}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapReader(java.io.Reader)
	 * @see  ResultSetWrapperImpl#unwrapReader(java.io.Reader)
	 * @see  SQLOutputWrapperImpl#unwrapReader(java.io.Reader)
	 */
	protected Reader unwrapReader(Reader in) {
		if(in == null) {
			return null;
		}
		if(in instanceof ReaderWrapper) {
			ReaderWrapper inWrapper = (ReaderWrapper)in;
			if(inWrapper.getConnectionWrapper() == this) {
				return inWrapper.getWrapped();
			}
		}
		return in;
	}

	/**
	 * Wraps a {@link Ref}, if not already wrapped by this wrapper.
	 *
	 * @see  #newRefWrapper(java.sql.Ref)
	 * @see  CallableStatementWrapperImpl#wrapRef(java.sql.Ref)
	 * @see  ResultSetWrapperImpl#wrapRef(java.sql.Ref)
	 * @see  SQLInputWrapperImpl#wrapRef(java.sql.Ref)
	 */
	protected RefWrapperImpl wrapRef(Ref ref) {
		if(ref == null) {
			return null;
		}
		if(ref instanceof RefWrapperImpl) {
			RefWrapperImpl refWrapper = (RefWrapperImpl)ref;
			if(refWrapper.getConnectionWrapper() == this) {
				return refWrapper;
			}
		}
		return newRefWrapper(ref);
	}

	/**
	 * Unwraps a {@link Ref}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapRef(java.sql.Ref)
	 * @see  ResultSetWrapperImpl#unwrapRef(java.sql.Ref)
	 * @see  SQLOutputWrapperImpl#unwrapRef(java.sql.Ref)
	 */
	protected Ref unwrapRef(Ref ref) {
		if(ref == null) {
			return null;
		}
		if(ref instanceof RefWrapperImpl) {
			RefWrapperImpl refWrapper = (RefWrapperImpl)ref;
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
	 * @see  #newResultSetWrapper(com.aoindustries.sql.StatementWrapperImpl, java.sql.ResultSet)
	 * @see  ArrayWrapperImpl#wrapResultSet(java.sql.ResultSet)
	 * @see  DatabaseMetaDataWrapperImpl#wrapResultSet(java.sql.ResultSet)
	 * @see  StatementWrapperImpl#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapperImpl wrapResultSet(StatementWrapperImpl stmtWrapper, ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapperImpl) {
			ResultSetWrapperImpl resultsWrapper = (ResultSetWrapperImpl)results;
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
	 * @see  PreparedStatementWrapperImpl#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 * @see  ResultSetWrapperImpl#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapperImpl wrapResultSetMetaData(ResultSetMetaData metaData) {
		if(metaData == null) {
			return null;
		}
		if(metaData instanceof ResultSetMetaDataWrapperImpl) {
			ResultSetMetaDataWrapperImpl metaDataWrapper = (ResultSetMetaDataWrapperImpl)metaData;
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
	 * @see  CallableStatementWrapperImpl#wrapRowId(java.sql.RowId)
	 * @see  ResultSetWrapperImpl#wrapRowId(java.sql.RowId)
	 * @see  SQLInputWrapperImpl#wrapRowId(java.sql.RowId)
	 */
	protected RowIdWrapperImpl wrapRowId(RowId rowId) {
		if(rowId == null) {
			return null;
		}
		if(rowId instanceof RowIdWrapperImpl) {
			RowIdWrapperImpl rowIdWrapper = (RowIdWrapperImpl)rowId;
			if(rowIdWrapper.getConnectionWrapper() == this) {
				return rowIdWrapper;
			}
		}
		return newRowIdWrapper(rowId);
	}

	/**
	 * Unwraps a {@link RowId}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapRowId(java.sql.RowId)
	 * @see  ResultSetWrapperImpl#unwrapRowId(java.sql.RowId)
	 * @see  RowIdWrapperImpl#unwrapRowId(java.sql.RowId)
	 * @see  SQLOutputWrapperImpl#unwrapRowId(java.sql.RowId)
	 */
	protected RowId unwrapRowId(RowId rowId) {
		if(rowId == null) {
			return null;
		}
		if(rowId instanceof RowIdWrapperImpl) {
			RowIdWrapperImpl rowIdWrapper = (RowIdWrapperImpl)rowId;
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
	protected SavepointWrapperImpl wrapSavepoint(Savepoint savepoint) {
		if(savepoint == null) {
			return null;
		}
		if(savepoint instanceof SavepointWrapperImpl) {
			SavepointWrapperImpl savepointWrapper = (SavepointWrapperImpl)savepoint;
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
		if(savepoint instanceof SavepointWrapperImpl) {
			SavepointWrapperImpl savepointWrapper = (SavepointWrapperImpl)savepoint;
			if(savepointWrapper.getConnectionWrapper() == this) {
				return savepointWrapper.getWrapped();
			}
		}
		return savepoint;
	}

	/**
	 * Wraps a {@link SQLData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSQLDataWrapper(java.sql.SQLData)
	 * @see  SQLOutputWrapperImpl#wrapSQLData(java.sql.SQLData)
	 */
	protected SQLDataWrapperImpl wrapSQLData(SQLData sqlData) {
		if(sqlData == null) {
			return null;
		}
		if(sqlData instanceof SQLDataWrapperImpl) {
			SQLDataWrapperImpl sqlDataWrapper = (SQLDataWrapperImpl)sqlData;
			if(sqlDataWrapper.getConnectionWrapper() == this) {
				return sqlDataWrapper;
			}
		}
		return newSQLDataWrapper(sqlData);
	}

	/**
	 * Wraps a {@link SQLInput}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSQLInputWrapper(java.sql.SQLInput)
	 * @see  SQLDataWrapperImpl#wrapSQLInput(java.sql.SQLInput)
	 */
	protected SQLInputWrapperImpl wrapSQLInput(SQLInput sqlInput) {
		if(sqlInput == null) {
			return null;
		}
		if(sqlInput instanceof SQLInputWrapperImpl) {
			SQLInputWrapperImpl sqlInputWrapper = (SQLInputWrapperImpl)sqlInput;
			if(sqlInputWrapper.getConnectionWrapper() == this) {
				return sqlInputWrapper;
			}
		}
		return newSQLInputWrapper(sqlInput);
	}

	/**
	 * Wraps a {@link SQLOutput}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSQLOutputWrapper(java.sql.SQLOutput)
	 * @see  SQLDataWrapperImpl#wrapSQLOutput(java.sql.SQLOutput)
	 */
	protected SQLOutputWrapperImpl wrapSQLOutput(SQLOutput sqlOutput) {
		if(sqlOutput == null) {
			return null;
		}
		if(sqlOutput instanceof SQLOutputWrapperImpl) {
			SQLOutputWrapperImpl sqlOutputWrapper = (SQLOutputWrapperImpl)sqlOutput;
			if(sqlOutputWrapper.getConnectionWrapper() == this) {
				return sqlOutputWrapper;
			}
		}
		return newSQLOutputWrapper(sqlOutput);
	}

	/**
	 * Wraps a {@link SQLXML}, if not already wrapped by this wrapper.
	 *
	 * @see  #newSQLXMLWrapper(java.sql.SQLXML)
	 * @see  CallableStatementWrapperImpl#wrapSQLXML(java.sql.SQLXML)
	 * @see  ResultSetWrapperImpl#wrapSQLXML(java.sql.SQLXML)
	 * @see  SQLInputWrapperImpl#wrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXMLWrapperImpl wrapSQLXML(SQLXML sqlXml) {
		if(sqlXml == null) {
			return null;
		}
		if(sqlXml instanceof SQLXMLWrapperImpl) {
			SQLXMLWrapperImpl sqlXmlWrapper = (SQLXMLWrapperImpl)sqlXml;
			if(sqlXmlWrapper.getConnectionWrapper() == this) {
				return sqlXmlWrapper;
			}
		}
		return newSQLXMLWrapper(sqlXml);
	}

	/**
	 * Unwraps a {@link SQLXML}, if wrapped by this wrapper.
	 *
	 * @see  PreparedStatementWrapperImpl#unwrapSQLXML(java.sql.SQLXML)
	 * @see  ResultSetWrapperImpl#unwrapSQLXML(java.sql.SQLXML)
	 * @see  SQLOutputWrapperImpl#unwrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXML unwrapSQLXML(SQLXML sqlXml) {
		if(sqlXml == null) {
			return null;
		}
		if(sqlXml instanceof SQLXMLWrapperImpl) {
			SQLXMLWrapperImpl sqlXmlWrapper = (SQLXMLWrapperImpl)sqlXml;
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
	 * @see  ResultSetWrapperImpl#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapperImpl wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		if(stmt instanceof PreparedStatement) {
			return wrapPreparedStatement((PreparedStatement)stmt);
		}
		if(stmt instanceof StatementWrapperImpl) {
			StatementWrapperImpl stmtWrapper = (StatementWrapperImpl)stmt;
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
	protected StructWrapperImpl wrapStruct(Struct struct) {
		if(struct == null) {
			return null;
		}
		if(struct instanceof StructWrapperImpl) {
			StructWrapperImpl structWrapper = (StructWrapperImpl)struct;
			if(structWrapper.getConnectionWrapper() == this) {
				return structWrapper;
			}
		}
		return newStructWrapper(struct);
	}

	/**
	 * Unwraps a {@link Struct}, if wrapped by this wrapper.
	 *
	 * @see  SQLOutputWrapperImpl#unwrapStruct(java.sql.Struct)
	 */
	protected Struct unwrapStruct(Struct struct) {
		if(struct == null) {
			return null;
		}
		if(struct instanceof StructWrapperImpl) {
			StructWrapperImpl structWrapper = (StructWrapperImpl)struct;
			if(structWrapper.getConnectionWrapper() == this) {
				return structWrapper.getWrapped();
			}
		}
		return struct;
	}

	/**
	 * Wraps a {@link Writer}, if not already wrapped by this wrapper.
	 *
	 * @see  #newWriterWrapper(java.io.Writer)
	 * @see  ClobWrapperImpl#wrapWriter(java.io.Writer)
	 * @see  SQLXMLWrapperImpl#wrapWriter(java.io.Writer)
	 */
	protected WriterWrapper wrapWriter(Writer out) {
		if(out == null) {
			return null;
		}
		if(out instanceof WriterWrapper) {
			WriterWrapper outWrapper = (WriterWrapper)out;
			if(outWrapper.getConnectionWrapper() == this) {
				return outWrapper;
			}
		}
		return newWriterWrapper(out);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapperImpl createStatement() throws SQLException {
		return wrapStatement(getWrapped().createStatement());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapperImpl prepareCall(String sql) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation calls {@code getWrapped().close()}.
	 * </p>
	 */
	@Override
	public void close() throws SQLException {
		getWrapped().close();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	@Override
	public DatabaseMetaDataWrapperImpl getMetaData() throws SQLException {
		return wrapDatabaseMetaData(getWrapped().getMetaData());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapperImpl createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapperImpl prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public SavepointWrapperImpl setSavepoint() throws SQLException {
		return wrapSavepoint(getWrapped().setSavepoint());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSavepoint(java.sql.Savepoint)
	 */
	@Override
	public SavepointWrapperImpl setSavepoint(String name) throws SQLException {
		return wrapSavepoint(getWrapped().setSavepoint(name));
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
		getWrapped().releaseSavepoint(unwrapSavepoint(savepoint));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
	public StatementWrapperImpl createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	@Override
	public CallableStatementWrapperImpl prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, autoGeneratedKeys));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnIndexes));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	@Override
	public PreparedStatementWrapperImpl prepareStatement(String sql, String columnNames[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnNames));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapperImpl createClob() throws SQLException {
		return wrapClob(getWrapped().createClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapperImpl createBlob() throws SQLException {
		return wrapBlob(getWrapped().createBlob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapperImpl createNClob() throws SQLException {
		return wrapNClob(getWrapped().createNClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapperImpl createSQLXML() throws SQLException {
		return wrapSQLXML(getWrapped().createSQLXML());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(com.aoindustries.sql.StatementWrapperImpl, java.sql.Array)
	 */
	@Override
	public ArrayWrapperImpl createArrayOf(String typeName, Object[] elements) throws SQLException {
		return wrapArray(null, getWrapped().createArrayOf(typeName, elements));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStruct(java.sql.Struct)
	 */
	@Override
	public StructWrapperImpl createStruct(String typeName, Object[] attributes) throws SQLException {
		return wrapStruct(getWrapped().createStruct(typeName, attributes));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation calls {@code getWrapped().abort(executor)}.
	 * </p>
	 */
	@Override
	public void abort(Executor executor) throws SQLException {
		getWrapped().abort(executor);
	}
}
