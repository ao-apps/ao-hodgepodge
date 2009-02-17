package com.aoindustries.io.unix;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * The internal protocol values used between ParallelPack and ParallelUnpack.
 * 
 * @see  ParallelPack
 * @see  ParallelUnpack
 *
 * @author  AO Industries, Inc.
 */
class PackProtocol {

    /**
     * Make no instances.
     */
    private PackProtocol() {}

    /**
     * The header (magic value).
     */
    static final String HEADER="ParallelPack";

    /**
     * The version supported.
     * 
     * 1 - Original version
     * 2 - Added single byte response from unpack when connected over TCP to
     *     avoid EOFException on socket close
     * 3 - Added compression option
     */
    static final int VERSION=3;

    /**
     * These values are used on the main loop.
     */
    static final byte
        REGULAR_FILE = 0,
        DIRECTORY = 1,
        SYMLINK = 2,
        BLOCK_DEVICE = 3,
        CHARACTER_DEVICE = 4,
        FIFO = 5,
        END = 6
    ;
    
    /**
     * The buffer size.
     */
    static final short BUFFER_SIZE = 4096;
    
    static final int DEFAULT_PORT = 10000;
}
