package com.aoindustries.security;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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