package com.aoindustries.io;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;

/**
 * A writer output stream makes a <code>Writer</code> behave like an
 * <code>OutputStream</code>.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class WriterOutputStream extends OutputStream {

    private final Writer out;

    /**
     * The conversions are done in this buffer for minimal memory allocation.
     */
    private char[] buff=BufferManager.getChars();

    /**
     * Create a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    public WriterOutputStream(Writer out) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, WriterOutputStream.class, "<init>(Writer)", null);
        try {
            this.out=out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void close() throws IOException {
        Profiler.startProfile(Profiler.IO, WriterOutputStream.class, "close()", null);
        try {
	    synchronized(this) {
		out.close();
		if(buff!=null) {
		    BufferManager.release(buff);
		    buff=null;
		}
	    }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void flush() throws IOException {
        Profiler.startProfile(Profiler.IO, WriterOutputStream.class, "flush()", null);
        try {
            out.flush();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(byte b[], int off, int len) throws IOException {
        Profiler.startProfile(Profiler.IO, WriterOutputStream.class, "write(byte[],int,int)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(int b) throws IOException {
        Profiler.startProfile(Profiler.IO, WriterOutputStream.class, "write(int)", null);
        try {
            out.write(b);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    public void finalize() throws Throwable {
        if(buff!=null) {
            BufferManager.release(buff);
            buff=null;
        }
        super.finalize();
    }
}
