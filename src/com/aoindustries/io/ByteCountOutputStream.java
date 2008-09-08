package com.aoindustries.io;

/*
 * Copyright 2006-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps an OutputStream to count the number of bytes transferred.
 * The counter is not synchronized.  Any necessary synchronization
 * should be externally applied.
 *
 * @author  AO Industries, Inc.
 */
public class ByteCountOutputStream extends FilterOutputStream {

    private long count = 0;

    public ByteCountOutputStream(OutputStream out) {
        super(out);
    }

    public void write(int b) throws IOException {
	out.write(b);
        count++;
    }

    public void write(byte b[]) throws IOException {
	out.write(b, 0, b.length);
        count+=b.length;
    }

    public void write(byte b[], int off, int len) throws IOException {
        out.write(b, off, len);
        count+=len;
    }

    public long getCount() {
        return count;
    }
}
