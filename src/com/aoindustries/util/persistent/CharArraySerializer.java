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

import com.aoindustries.util.BufferManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes <code>char[]</code> objects.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class CharArraySerializer implements Serializer<char[]> {

    public int getSerializedSize(char[] value) throws IOException {
        return 4+value.length/2;
    }

    public void serialize(char[] chars, OutputStream out) throws IOException {
        byte[] bytes = BufferManager.getBytes();
        try {
            int len = chars.length;
            Utils.intToBuffer(len, bytes, 0);
            out.write(bytes, 0, 4);
            int pos = 0;
            while(len>0) {
                int count = BufferManager.BUFFER_SIZE/2;
                if(len<count) count = len;
                for(int charsIndex=0, bytesIndex = 0; charsIndex<count; charsIndex++, bytesIndex+=2) {
                    Utils.charToBuffer(chars[pos+charsIndex], bytes, bytesIndex);
                }
                out.write(bytes, 0, count*2);
                pos += count;
                len -= count;
            }
        } finally {
            BufferManager.release(bytes);
        }
    }

    public char[] deserialize(InputStream in) throws IOException {
        byte[] bytes = BufferManager.getBytes();
        try {
            Utils.readFully(in, bytes, 0, 4);
            int len = Utils.bufferToInt(bytes, 0);
            char[] chars = new char[len];
            int pos = 0;
            while(len>0) {
                int count = BufferManager.BUFFER_SIZE/2;
                if(len<count) count = len;
                Utils.readFully(in, bytes, pos, len);
                for(int charsIndex=0, bytesIndex = 0; charsIndex<count; charsIndex++, bytesIndex+=2) {
                    chars[pos+charsIndex] = Utils.bufferToChar(bytes, bytesIndex);
                }
                pos += count;
                len -= count;
            }
            return chars;
        } finally {
            BufferManager.release(bytes);
        }
    }
}
