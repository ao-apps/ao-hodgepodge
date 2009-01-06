package com.aoindustries.security;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class AccountDisabledException extends LoginException {

    public AccountDisabledException() {
        super();
    }
    
    public AccountDisabledException(String message) {
        super(message);
    }

    public AccountDisabledException(Throwable cause) {
        super(cause);
    }
    
    public AccountDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}