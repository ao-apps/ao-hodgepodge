package com.aoindustries.security;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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