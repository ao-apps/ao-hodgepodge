package com.aoindustries.io;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * A <code>BitRateProvider</code> specifies precisely how many bits
 * per second of bandwidth a task should use.
 *
 * @author  AO Industries, Inc.
 */
public interface BitRateProvider {

    /**
     * Gets the bit rate in bits per second, <code>-1</code> indicates unlimited bandwidth.
     */
    int getBitRate() throws IOException;
 
    /**
     * Gets the block size in bytes.
     */
    int getBlockSize();
}