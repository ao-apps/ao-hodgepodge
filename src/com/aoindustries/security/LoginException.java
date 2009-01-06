package com.aoindustries.security;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.security.*;

/**
 * @author  AO Industries, Inc.
 */
public class LoginException extends GeneralSecurityException {

    public LoginException() {
        super();
    }
    
    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super();
        initCause(cause);
    }
    
    public LoginException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}