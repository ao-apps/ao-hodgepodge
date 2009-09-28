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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes <code>Float</code> objects.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class FloatSerializer implements Serializer<Float> {

    public int getSerializedSize(Float value) throws IOException {
        return 4;
    }

    private final byte[] buffer = new byte[4];

    public void serialize(Float value, OutputStream out) throws IOException {
        Utils.intToBuffer(Float.floatToRawIntBits(value), buffer, 0);
        out.write(buffer, 0, 4);
    }

    public Float deserialize(InputStream in) throws IOException {
        Utils.readFully(in, buffer, 0, 4);
        return Float.intBitsToFloat(Utils.bufferToInt(buffer, 0));
    }
}
