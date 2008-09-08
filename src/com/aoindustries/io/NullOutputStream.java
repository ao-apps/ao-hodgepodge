package com.aoindustries.io;

/*
 * Copyright 2002-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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