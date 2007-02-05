package com.aoindustries.io;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Indicates that an object may be sent across a
 * <code>CompressedDataInputStream</code> and
 * <code>CompressedDataOutputStream</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface Streamable {

    void read(CompressedDataInputStream in) throws IOException;

    void write(CompressedDataOutputStream out, String version) throws IOException;
}