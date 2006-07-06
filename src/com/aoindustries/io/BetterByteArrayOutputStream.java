package com.aoindustries.io;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
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
public class BetterByteArrayOutputStream extends ByteArrayOutputStream {

    public BetterByteArrayOutputStream() {
        super();
        Profiler.startProfile(Profiler.INSTANTANEOUS, BetterByteArrayOutputStream.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public BetterByteArrayOutputStream(int size) {
        super(size);
        Profiler.startProfile(Profiler.INSTANTANEOUS, BetterByteArrayOutputStream.class, "<init>(int)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public byte[] getInternalByteArray() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, BetterByteArrayOutputStream.class, "getInternalByteArray()", null);
        try {
            return this.buf;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public void writeTo(RandomAccessFile raf) throws IOException {
        Profiler.startProfile(Profiler.IO, BetterByteArrayOutputStream.class, "writeTo(RandomAccessFile)", null);
        try {
            synchronized(this) {
                raf.write(buf, 0, count);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
