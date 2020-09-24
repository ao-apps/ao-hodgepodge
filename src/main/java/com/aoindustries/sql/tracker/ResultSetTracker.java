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
package com.aoindustries.sql.tracker;

import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.ResultSetWrapper;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.synchronizedMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks a {@link ResultSet} for unclosed or unfreed objects.
 *
 * @author  AO Industries, Inc.
 */
public class ResultSetTracker extends ResultSetWrapper implements IResultSetTracker {

	public ResultSetTracker(ConnectionTracker connectionTracker, StatementTracker stmtTracker, ResultSet wrapped) {
		super(connectionTracker, stmtTracker, wrapped);
	}

	private final List<Runnable> onCloseHandlers = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void addOnClose(Runnable onCloseHandler) {
		onCloseHandlers.add(onCloseHandler);
	}

	private final Map<Array,ArrayTracker> trackedArrays = synchronizedMap(new IdentityHashMap<>());
	private final Map<Blob,BlobTracker> trackedBlobs = synchronizedMap(new IdentityHashMap<>());
	private final Map<Clob,ClobTracker> trackedClobs = synchronizedMap(new IdentityHashMap<>());
	private final Map<InputStream,InputStreamTracker> trackedInputStreams = synchronizedMap(new IdentityHashMap<>());
	private final Map<NClob,NClobTracker> trackedNClobs = synchronizedMap(new IdentityHashMap<>());
	private final Map<Reader,ReaderTracker> trackedReaders = synchronizedMap(new IdentityHashMap<>());
	private final Map<Ref,RefTracker> trackedRefs = synchronizedMap(new IdentityHashMap<>());
	private final Map<ResultSetMetaData,ResultSetMetaDataTracker> trackedResultSetMetaDatas = synchronizedMap(new IdentityHashMap<>());
	private final Map<RowId,RowIdTracker> trackedRowIds = synchronizedMap(new IdentityHashMap<>());
	private final Map<SQLXML,SQLXMLTracker> trackedSQLXMLs = synchronizedMap(new IdentityHashMap<>());

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<Array,ArrayTracker> getTrackedArrays() {
		return trackedArrays;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<Blob,BlobTracker> getTrackedBlobs() {
		return trackedBlobs;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<Clob,ClobTracker> getTrackedClobs() {
		return trackedClobs;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<InputStream,InputStreamTracker> getTrackedInputStreams() {
		return trackedInputStreams;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<NClob,NClobTracker> getTrackedNClobs() {
		return trackedNClobs;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<Reader,ReaderTracker> getTrackedReaders() {
		return trackedReaders;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<Ref,RefTracker> getTrackedRefs() {
		return trackedRefs;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<ResultSetMetaData,ResultSetMetaDataTracker> getTrackedResultSetMetaDatas() {
		return trackedResultSetMetaDatas;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<RowId,RowIdTracker> getTrackedRowIds() {
		return trackedRowIds;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<SQLXML,SQLXMLTracker> getTrackedSQLXMLs() {
		return trackedSQLXMLs;
	}

	@Override
	protected ArrayTracker wrapArray(Array array) {
		return ConnectionTracker.getIfAbsent(
			trackedArrays, array,
			() -> (ArrayTracker)super.wrapArray(array),
			ArrayTracker::getWrapped
		);
	}

	@Override
	protected BlobTracker wrapBlob(Blob blob) {
		return ConnectionTracker.getIfAbsent(
			trackedBlobs, blob,
			() -> (BlobTracker)super.wrapBlob(blob),
			BlobTracker::getWrapped
		);
	}

	@Override
	protected ClobTracker wrapClob(Clob clob) {
		return ConnectionTracker.getIfAbsent(
			trackedClobs, clob,
			() -> (ClobTracker)super.wrapClob(clob),
			ClobTracker::getWrapped
		);
	}

	@Override
	protected InputStreamTracker wrapInputStream(InputStream in) {
		return ConnectionTracker.getIfAbsent(
			trackedInputStreams, in,
			() -> (InputStreamTracker)super.wrapInputStream(in),
			InputStreamTracker::getWrapped
		);
	}

	@Override
	protected NClobTracker wrapNClob(NClob nclob) {
		return ConnectionTracker.getIfAbsent(
			trackedNClobs, nclob,
			() -> (NClobTracker)super.wrapNClob(nclob),
			NClobTracker::getWrapped
		);
	}

	@Override
	protected ReaderTracker wrapReader(Reader in) {
		return ConnectionTracker.getIfAbsent(
			trackedReaders, in,
			() -> (ReaderTracker)super.wrapReader(in),
			ReaderTracker::getWrapped
		);
	}

	@Override
	protected RefTracker wrapRef(Ref ref) {
		return ConnectionTracker.getIfAbsent(
			trackedRefs, ref,
			() -> (RefTracker)super.wrapRef(ref),
			RefTracker::getWrapped
		);
	}

	@Override
	protected ResultSetMetaDataTracker wrapResultSetMetaData(ResultSetMetaData metaData) {
		return ConnectionTracker.getIfAbsent(
			trackedResultSetMetaDatas, metaData,
			() -> (ResultSetMetaDataTracker)super.wrapResultSetMetaData(metaData),
			ResultSetMetaDataTracker::getWrapped
		);
	}

	@Override
	protected RowIdTracker wrapRowId(RowId rowId) {
		return ConnectionTracker.getIfAbsent(
			trackedRowIds, rowId,
			() -> (RowIdTracker)super.wrapRowId(rowId),
			RowIdTracker::getWrapped
		);
	}

	@Override
	protected SQLXMLTracker wrapSQLXML(SQLXML sqlXml) {
		return ConnectionTracker.getIfAbsent(
			trackedSQLXMLs, sqlXml,
			() -> (SQLXMLTracker)super.wrapSQLXML(sqlXml),
			SQLXMLTracker::getWrapped
		);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  ArrayTracker#close()
	 * @see  BlobTracker#close()
	 * @see  ClobTracker#close()
	 * @see  InputStreamTracker#close()
	 * @see  NClobTracker#close()
	 * @see  ReaderTracker#close()
	 * @see  RefTracker#close()
	 * @see  ResultSetMetaDataTracker#close()
	 * @see  RowIdTracker#close()
	 * @see  SQLXMLTracker#close()
	 */
	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch", "unchecked"})
	public void close() throws SQLException {
		Throwable t0 = ConnectionTracker.clearRunAndCatch(onCloseHandlers);
		// Close tracked objects
		t0 = ConnectionTracker.clearCloseAndCatch(t0,
			trackedArrays,
			trackedBlobs,
			trackedClobs,
			trackedInputStreams,
			trackedNClobs,
			trackedReaders,
			trackedRefs,
			trackedResultSetMetaDatas,
			trackedRowIds,
			trackedSQLXMLs
		);
		try {
			super.close();
		} catch(Throwable t) {
			t0 = Throwables.addSuppressed(t0, t);
		}
		if(t0 != null) throw Throwables.wrap(t0, SQLException.class, SQLException::new);
	}
}
