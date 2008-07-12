package com.aoindustries.io;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Discards all data.
 */
public class NullOutputStream extends OutputStream {

    public NullOutputStream() {
    }

    public void close() {
    }
    
    public void flush() {
    }
    
    public void write(byte[] b) {
    }
    
    public void write(byte[] b, int off, int len) {
    }
    
    public void write(int b) {
    }
}