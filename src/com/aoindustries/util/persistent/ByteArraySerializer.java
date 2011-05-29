/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
import java.io.InputStream;
import java.io.OutputStream;
// import org.checkthread.annotations.NotThreadSafe;
// import org.checkthread.annotations.ThreadSafe;

/**
 * Serializes <code>byte[]</code> objects.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class ByteArraySerializer implements Serializer<byte[]> {

    // @ThreadSafe
    public boolean isFixedSerializedSize() {
        return false;
    }

    // @NotThreadSafe
    public long getSerializedSize(byte[] value) {
        return 4+value.length;
    }

    private final byte[] buffer = new byte[4];

    // @NotThreadSafe
    public void serialize(byte[] value, OutputStream out) throws IOException {
        PersistentCollections.intToBuffer(value.length, buffer, 0);
        out.write(buffer, 0, 4);
        out.write(value);
    }

    // @NotThreadSafe
    public byte[] deserialize(InputStream in) throws IOException {
        PersistentCollections.readFully(in, buffer, 0, 4);
        int length = PersistentCollections.bufferToInt(buffer, 0);
        byte[] value = new byte[length];
        PersistentCollections.readFully(in, value, 0, length);
        return value;
    }
}
