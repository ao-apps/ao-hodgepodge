package com.aoindustries.io.unix;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * Contains the result for one call to <code>FilesystemIterator.getNextResult()</code>.
 *
 * @see  FilesystemIterator#getNextResult
 * @see  FilesystemIterator#getNextResults
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIteratorResult {

    UnixFile unixFile;
    String convertedFilename;
    
    public FilesystemIteratorResult() {
    }

    FilesystemIteratorResult(UnixFile unixFile, String convertedFilename) {
        this.unixFile = unixFile;
        this.convertedFilename = convertedFilename;
    }
    
    public UnixFile getUnixFile() {
        return unixFile;
    }
    
    public String getConvertedFilename() {
        return convertedFilename;
    }
}
