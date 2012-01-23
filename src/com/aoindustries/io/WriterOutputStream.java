/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.io;

import com.aoindustries.util.*;
import java.io.*;

/**
 * A writer output stream makes a <code>Writer</code> behave like an
 * <code>OutputStream</code>.  No encoding/decoding is performed.
 *
 * @author  AO Industries, Inc.
 */
final public class WriterOutputStream extends OutputStream {

    private final Writer out;

    /**
     * The conversions are done in this buffer for minimal memory allocation.
     * Released on close.
     */
    private char[] buff=BufferManager.getChars();

    /**
     * Create a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    public WriterOutputStream(Writer out) {
        this.out=out;
    }

    @Override
    public void close() throws IOException {
        synchronized(this) {
            out.close();
            if(buff!=null) {
                BufferManager.release(buff);
                buff=null;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        synchronized(this) {
            if (b == null) throw new NullPointerException();
            int pos=0;
            while(pos<len) {
                int blockSize=len-pos;
                if(blockSize>BufferManager.BUFFER_SIZE) blockSize=BufferManager.BUFFER_SIZE;
                for(int cpos=0;cpos<blockSize;cpos++) buff[cpos]=(char)b[off+(pos++)];
                out.write(buff, 0, blockSize);
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if(buff!=null) {
                BufferManager.release(buff);
                buff=null;
            }
        } finally {
            super.finalize();
        }
    }
}
