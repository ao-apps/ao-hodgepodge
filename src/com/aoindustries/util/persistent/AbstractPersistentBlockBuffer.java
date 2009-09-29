/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util.persistent;

import java.io.IOException;

/**
 * Base class for any implementation that treats a <code>PersistentBuffer</code>
 * as a set of allocatable blocks.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractPersistentBlockBuffer implements PersistentBlockBuffer {

    protected final PersistentBuffer pbuffer;

    public AbstractPersistentBlockBuffer(PersistentBuffer pbuffer) {
        this.pbuffer = pbuffer;
    }

    public boolean isClosed() {
        return pbuffer.isClosed();
    }

    public void close() throws IOException {
        pbuffer.close();
    }

    public boolean isReadOnly() {
        return pbuffer.isReadOnly();
    }

    public void barrier(boolean force) throws IOException {
        pbuffer.barrier(force);
    }
}
