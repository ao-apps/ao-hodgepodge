package com.aoindustries.security;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class AccountNotFoundException extends LoginException {

    public AccountNotFoundException() {
        super();
    }
    
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}