package com.aoindustries.io;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    int UNLIMITED_BANDWIDTH = -1;

    /**
     * Gets the bit rate in bits per second, <code>UNLIMITED_BANDWIDTH</code> indicates unlimited bandwidth.
     */
    int getBitRate() throws IOException;
 
    /**
     * Gets the block size in bytes.
     */
    int getBlockSize();
}