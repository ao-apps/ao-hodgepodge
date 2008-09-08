package com.aoindustries.io;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;

/**
 * A <code>FileListObject</code> is able to be placed into a <code>FileList</code>.
 *
 * @see  FileList
 *
 * @author  AO Industries, Inc.
 */
public interface FileListObjectFactory<T extends FileListObject> {

    T createInstance() throws IOException;
}