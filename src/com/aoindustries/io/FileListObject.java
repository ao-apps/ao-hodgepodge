package com.aoindustries.io;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A <code>FileListObject</code> is able to be placed into a <code>FileList</code>.
 *
 * @see  FileList
 *
 * @author  AO Industries, Inc.
 */
public interface FileListObject {

    void writeRecord(DataOutputStream out) throws IOException;
    
    void readRecord(DataInputStream in) throws IOException;
}