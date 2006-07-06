package com.aoindustries.security;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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