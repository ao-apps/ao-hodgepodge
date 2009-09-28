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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Serializes any <code>Serializable</code> objects.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class ObjectSerializer<E> implements Serializer<E> {

    private E lastSerialized = null;
    final private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private void serializeToBuffer(E value) throws IOException {
        if(lastSerialized!=value) {
            lastSerialized = null;
            buffer.reset();
            ObjectOutputStream oout = new ObjectOutputStream(buffer);
            try {
                oout.writeObject(value);
            } finally {
                oout.close();
            }
            lastSerialized = value;
        }
    }
    public int getSerializedSize(E value) throws IOException {
        serializeToBuffer(value);
        return buffer.size();
    }

    public void serialize(E value, OutputStream out) throws IOException {
        buffer.writeTo(out);
    }

    @SuppressWarnings("unchecked")
    public E deserialize(InputStream in) throws IOException {
        ObjectInputStream oin = new ObjectInputStream(in);
        try {
            return (E)oin.readObject();
        } catch(ClassNotFoundException err) {
            IOException ioErr = new IOException();
            ioErr.initCause(err);
            throw ioErr;
        } finally {
            oin.close();
        }
    }
}
