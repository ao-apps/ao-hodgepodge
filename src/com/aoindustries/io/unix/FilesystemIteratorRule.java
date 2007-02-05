package com.aoindustries.io.unix;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.Profiler;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

/**
 * Controls the behavior of a <code>FilesystemIterator</code>.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class FilesystemIteratorRule {

    abstract public boolean isNoRecurse(String filename) throws IOException;

    abstract public boolean isSkip(String filename) throws IOException;
    
    /**
     * A rule that will recurse and not be skipped.
     */
    public static final FilesystemIteratorRule OK = new FilesystemIteratorRule() {

        public boolean isNoRecurse(String filename) {
            return false;
        }

        public boolean isSkip(String filename) {
            return false;
        }
    };

    /**
     * A rule that will not recurse but is not skipped.
     */
    public static final FilesystemIteratorRule NO_RECURSE = new FilesystemIteratorRule() {

        public boolean isNoRecurse(String filename) {
            return true;
        }

        public boolean isSkip(String filename) {
            return false;
        }
    };

    /**
     * A rule that will be skipped (and therefore not recursed).
     */
    public static final FilesystemIteratorRule SKIP = new FilesystemIteratorRule() {

        public boolean isNoRecurse(String filename) {
            return true;
        }

        public boolean isSkip(String filename) {
            return true;
        }
    };
}
