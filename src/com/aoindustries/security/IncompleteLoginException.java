package com.aoindustries.security;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class IncompleteLoginException extends LoginException {

    public IncompleteLoginException() {
        super();
    }
    
    public IncompleteLoginException(String message) {
        super(message);
    }

    public IncompleteLoginException(Throwable cause) {
        super(cause);
    }
    
    public IncompleteLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}