package com.aoindustries.util.zip;

/*
 * Copyright 2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Finishes the deflator on each flush to allow compressed, two-way protocols.
 *
 * @author  AO Industries, Inc.
 */
public class AutoFinishGZIPOutputStream extends GZIPOutputStream {

    public AutoFinishGZIPOutputStream(OutputStream out) throws IOException {
        super(out);
    }
    
    public AutoFinishGZIPOutputStream(OutputStream out, int size) throws IOException {
        super(out, size);
    }

    @Override
    public void flush() throws IOException {
        finish();
        super.flush();
    }
}
