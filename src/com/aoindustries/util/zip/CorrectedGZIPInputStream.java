package com.aoindustries.util.zip;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.util.zip.*;

/**
 * Works around the "Corrupt GZIP trailer" problem in <code>GZIPInputStream</code> by catching and ignoring this exception.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class CorrectedGZIPInputStream extends GZIPInputStream {

    public CorrectedGZIPInputStream(InputStream in) throws IOException {
        super(in);
    }
    
    public CorrectedGZIPInputStream(InputStream in, int size) throws IOException {
        super(in, size);
    }
    
    private final Object foundErrorLock=new Object();
    private boolean foundError=false;

    public int read(byte[] buf, int off, int len) throws IOException {
        synchronized(foundErrorLock) {
            if(foundError) return -1;
            try {
                return super.read(buf, off, len);
            } catch(IOException err) {
                String message=err.getMessage();
                if(message.indexOf("Corrupt GZIP trailer")!=-1) {
                    foundError=true;
                    return -1;
                } else throw err;
            }
        }
    }
}
