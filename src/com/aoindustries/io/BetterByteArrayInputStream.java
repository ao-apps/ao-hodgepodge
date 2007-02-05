package com.aoindustries.io;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * Provides direct access to the internal <code>byte[]</code>
 *
 * @author  AO Industries, Inc.
 */
public class BetterByteArrayInputStream extends ByteArrayInputStream {

    public BetterByteArrayInputStream(byte[] buf) {
        super(buf);
        Profiler.startProfile(Profiler.INSTANTANEOUS, BetterByteArrayInputStream.class, "<init>(byte[])", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public byte[] getInternalByteArray() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, BetterByteArrayInputStream.class, "getInternalByteArray()", null);
        try {
            return this.buf;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void readFrom(RandomAccessFile raf) throws IOException {
        Profiler.startProfile(Profiler.IO, BetterByteArrayInputStream.class, "readFrom(RandomAccessFile)", null);
        try {
            synchronized(this) {
                raf.readFully(buf);
                mark=0;
                pos=0;
                count=buf.length;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
