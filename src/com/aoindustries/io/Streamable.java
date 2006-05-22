package com.aoindustries.io;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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