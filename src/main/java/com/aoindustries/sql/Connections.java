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

import java.sql.Connection;

/**
 * Utilities for working with {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public class Connections {

	private Connections() {}

	/**
	 * The default {@linkplain Connection#getTransactionIsolation() transaction isolation}.
	 * It is expected that all new {@link Connection} will have this as a default, as well
	 * as all pooled connections be reset to this default.
	 *
	 * @see  Connection#TRANSACTION_READ_COMMITTED
	 */
	// TODO: Globally use and enforce this as the default
	public static final int DEFAULT_TRANSACTION_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
}