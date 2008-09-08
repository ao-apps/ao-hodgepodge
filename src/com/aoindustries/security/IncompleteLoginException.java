package com.aoindustries.security;

/*
 * Copyright 2004-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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