package com.aoindustries.security;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class BadPasswordException extends LoginException {

    public BadPasswordException() {
        super();
    }
    
    public BadPasswordException(String message) {
        super(message);
    }

    public BadPasswordException(Throwable cause) {
        super(cause);
    }
    
    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}