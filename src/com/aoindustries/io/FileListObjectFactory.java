package com.aoindustries.io;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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