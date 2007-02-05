package com.aoindustries.io;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * Discards all data.
 */
public class NullOutputStream extends OutputStream {

    public NullOutputStream() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public void close() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "close()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
    
    public void flush() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "flush()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
    
    public void write(byte[] b) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "write(byte[])", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
    
    public void write(byte[] b, int off, int len) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "write(byte[],int,int)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
    
    public void write(int b) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, NullOutputStream.class, "write(int)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
}