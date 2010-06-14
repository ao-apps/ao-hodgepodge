/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.table;

import java.util.Iterator;
import java.util.List;

/**
 * An abstract structure for tables.
 *
 * @author  AO Industries, Inc.
 */
public interface Table<C extends Column, R extends Row> {

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  The default
     * batching is used.
     *
     * @see  #addTableListener(TableListener,long)
     */
    void addTableListener(TableListener<? super C, ? super R> listener);

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  Repetitive incoming
     * requests will be batched into fewer events, in increments
     * provided by batchTime.  If batchTime is 0, the event is immediately
     * and always distributed.  Batched events are performed in
     * concurrent Threads, while immediate events are triggered by the
     * central cache invalidation thread.  In other words, don't use
     * a batchTime of zero unless you absolutely need your code to
     * run immediately, because it causes serial processing of the event
     * and may potentially slow down the responsiveness of the server.
     */
    void addTableListener(TableListener<? super C, ? super R> listener, long batchTime);

    /**
     * Removes a <code>TableListener</code> from the list of
     * objects being notified when the data is updated.
     */
    void removeTableListener(TableListener<? super C, ? super R> listener);

    /**
     * Gets the name of this table.
     */
    String getTableName();

    /**
     * Gets the unmodifiable list of column names.
     */
    List<? extends C> getColumns();

    /**
     * Gets the unmodifiable set of all rows.
     */
    Iterator<? extends R> getRows();
}
