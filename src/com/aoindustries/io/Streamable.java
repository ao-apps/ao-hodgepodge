package com.aoindustries.io;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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