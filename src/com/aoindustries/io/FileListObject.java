package com.aoindustries.io;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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