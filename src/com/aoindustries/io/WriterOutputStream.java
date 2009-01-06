package com.aoindustries.io;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        this.out=out;
    }

    public void close() throws IOException {
        synchronized(this) {
            out.close();
            if(buff!=null) {
                BufferManager.release(buff);
                buff=null;
            }
        }
    }

    public void flush() throws IOException {
        out.flush();
    }

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

    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void finalize() throws Throwable {
        if(buff!=null) {
            BufferManager.release(buff);
            buff=null;
        }
        super.finalize();
    }
}
