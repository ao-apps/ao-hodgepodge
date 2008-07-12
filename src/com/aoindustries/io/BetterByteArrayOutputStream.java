package com.aoindustries.io;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Provides direct access to the internal <code>byte[]</code>
 *
 * @author  AO Industries, Inc.
 */
public class BetterByteArrayOutputStream extends ByteArrayOutputStream {

    public BetterByteArrayOutputStream() {
        super();
    }

    public BetterByteArrayOutputStream(int size) {
        super(size);
    }

    public byte[] getInternalByteArray() {
        return this.buf;
    }
    
    public void writeTo(RandomAccessFile raf) throws IOException {
        synchronized(this) {
            raf.write(buf, 0, count);
        }
    }
}
