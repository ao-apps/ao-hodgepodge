package com.aoindustries.io;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * Wraps an InputStream to protect it from close calls.  This is useful for at least
 * GZIPInputStream where the native resources of the GZIPInputStream need to be released
 * using the close call while the underlying stream is left intact.
 *
 * @author  AO Industries, Inc.
 */
public class DontCloseInputStream extends FilterInputStream {

    public DontCloseInputStream(InputStream in) {
        super(in);
    }

    /**
     * Does nothing on close to protect the wrapped InputStream.
     */
    public void close() {
    }
}
