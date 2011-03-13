/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.util.zip;

import java.io.*;
import java.util.zip.*;

/**
 * Works around the "Corrupt GZIP trailer" problem in <code>GZIPInputStream</code> by catching and ignoring this exception.
 *
 * @author  AO Industries, Inc.
 */
public class CorrectedGZIPInputStream extends GZIPInputStream {

    public CorrectedGZIPInputStream(InputStream in) throws IOException {
        super(in);
    }
    
    public CorrectedGZIPInputStream(InputStream in, int size) throws IOException {
        super(in, size);
    }
    
    private final Object foundErrorLock=new Object();
    private boolean foundError=false;

    public int read(byte[] buf, int off, int len) throws IOException {
        synchronized(foundErrorLock) {
            if(foundError) return -1;
            try {
                return super.read(buf, off, len);
            } catch(IOException err) {
                String message=err.getMessage();
                if(message.indexOf("Corrupt GZIP trailer")!=-1) {
                    foundError=true;
                    return -1;
                } else throw err;
            }
        }
    }
}
