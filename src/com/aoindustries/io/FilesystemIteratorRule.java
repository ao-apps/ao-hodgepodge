package com.aoindustries.io;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;

/**
 * Controls the behavior of a <code>FilesystemIterator</code>.  Each rule
 * has is mapped in via either a filename or a file prefix.  If a rule is provided
 * with an empty filename for regular rules (not prefix-matched), it will be the
 * default for all filesystem roots.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class FilesystemIteratorRule {

    /**
     * Gets if this item should be included.
     */
    abstract public boolean isIncluded(String filename) throws IOException;
    
    /**
     * A rule that will always be backed-up.
     */
    public static final FilesystemIteratorRule OK = new FilesystemIteratorRule() {

        public boolean isIncluded(String filename) {
            return true;
        }
    };

    /**
     * A rule that will not be backed-up.
     */
    public static final FilesystemIteratorRule SKIP = new FilesystemIteratorRule() {

        public boolean isIncluded(String filename) {
            return false;
        }
    };
}
