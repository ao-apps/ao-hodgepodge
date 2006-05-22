package com.aoindustries.io;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * Wraps an OutputStream to protect it from close calls.  This is useful for at least
 * GZIPOutputStream where the native resources of the GZIPOutputStream need to be released
 * using the close call while the underlying stream is left intact.
 *
 * @author  AO Industries, Inc.
 */
public class DontCloseOutputStream extends FilterOutputStream {

    public DontCloseOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Does nothing on close to protect the wrapped OutputStream.
     */
    public void close() {
    }
}
