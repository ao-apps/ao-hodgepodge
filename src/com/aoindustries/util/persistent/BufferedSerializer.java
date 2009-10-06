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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Serializes any objects by using a buffer between the <code>getSerializedSize</code>
 * and <code>serialize</code> calls.  This and all subclasses are not fixed size.
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
abstract public class BufferedSerializer<E> implements Serializer<E> {

    private E lastSerialized = null;
    final private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public BufferedSerializer() {
    }

    private void serializeToBuffer(E value) throws IOException {
        if(lastSerialized!=value) {
            lastSerialized = null;
            buffer.reset();
            serialize(value, buffer);
            lastSerialized = value;
        }
    }

    final public boolean isFixedSerializedSize() {
        return false;
    }

    final public long getSerializedSize(E value) throws IOException {
        serializeToBuffer(value);
        return buffer.size();
    }

    final public void serialize(E value, OutputStream out) throws IOException {
        serializeToBuffer(value);
        buffer.writeTo(out);
    }

    abstract protected void serialize(E value, ByteArrayOutputStream buffer) throws IOException;
}
