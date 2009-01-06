package com.aoindustries.io;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;

/**
 * A <code>BitRateProvider</code> specifies precisely how many bits
 * per second of bandwidth a task should use.
 *
 * @author  AO Industries, Inc.
 */
public interface BitRateProvider {

    int UNLIMITED_BANDWIDTH = -1;
    
    /**
     * The recommended minimum bit rate.
     */
    int MINIMUM_BIT_RATE = 4800;

    /**
     * Gets the bit rate in bits per second, <code>UNLIMITED_BANDWIDTH</code> indicates unlimited bandwidth.
     */
    int getBitRate() throws IOException;
 
    /**
     * Gets the block size in bytes.
     */
    int getBlockSize();
}