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
import com.aoindustries.sql.wrapper.SQLDataWrapper;
import com.aoindustries.sql.wrapper.SQLInputWrapper;
import com.aoindustries.sql.wrapper.SQLOutputWrapper;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.synchronizedMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks a {@link SQLData} for unclosed or unfreed objects.
 *
 * @author  AO Industries, Inc.
 */
public class SQLDataTracker extends SQLDataWrapper implements ISQLDataTracker {

	public SQLDataTracker(ConnectionTracker connectionTracker, SQLData wrapped) {
		super(connectionTracker, wrapped);
	}

	private final List<Runnable> onCloseHandlers = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void addOnClose(Runnable onCloseHandler) {
		onCloseHandlers.add(onCloseHandler);
	}

	private final Map<SQLInput,SQLInputTracker> trackedSQLInputs = synchronizedMap(new IdentityHashMap<>());
	private final Map<SQLOutput,SQLOutputTracker> trackedSQLOutputs = synchronizedMap(new IdentityHashMap<>());

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<SQLInput,SQLInputTracker> getTrackedSQLInputs() {
		return trackedSQLInputs;
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // No defensive copy
	final public Map<SQLOutput,SQLOutputTracker> getTrackedSQLOutputs() {
		return trackedSQLOutputs;
	}

	@Override
	protected SQLInputWrapper wrapSQLInput(SQLInput sqlInput) {
		return ConnectionTracker.getIfAbsent(
			trackedSQLInputs, sqlInput,
			() -> (SQLInputTracker)super.wrapSQLInput(sqlInput),
			SQLInputTracker::getWrapped
		);
	}

	@Override
	protected SQLOutputWrapper wrapSQLOutput(SQLOutput sqlOutput) {
		return ConnectionTracker.getIfAbsent(
			trackedSQLOutputs, sqlOutput,
			() -> (SQLOutputTracker)super.wrapSQLOutput(sqlOutput),
			SQLOutputTracker::getWrapped
		);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  SQLInputTracker#close()
	 * @see  SQLOutputTracker#close()
	 */
	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch", "unchecked"})
	public void close() throws SQLException {
		Throwable t0 = ConnectionTracker.clearRunAndCatch(onCloseHandlers);
		// Close tracked objects
		t0 = ConnectionTracker.clearCloseAndCatch(t0,
			trackedSQLInputs,
			trackedSQLOutputs
		);
		try {
			super.close();
		} catch(Throwable t) {
			t0 = Throwables.addSuppressed(t0, t);
		}
		if(t0 != null) throw Throwables.wrap(t0, SQLException.class, SQLException::new);
	}
}
