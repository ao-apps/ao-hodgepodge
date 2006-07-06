package com.aoindustries.io;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an InputStream to count the number of bytes transferred.
 * The counter is not synchronized.  Any necessary synchronization
 * should be externally applied.
 *
 * @author  AO Industries, Inc.
 */
public class ByteCountInputStream extends FilterInputStream {

    private long count = 0;

    public ByteCountInputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException {
	int b = in.read();
        count++;
        return b;
    }

    public int read(byte b[]) throws IOException {
	int bytes = in.read(b);
        count+=bytes;
        return bytes;
    }

    public int read(byte b[], int off, int len) throws IOException {
	int bytes = in.read(b, off, len);
        count+=bytes;
        return bytes;
    }
    
    public long getCount() {
        return count;
    }
}
