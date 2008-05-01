package com.aoindustries.util;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class WrappedException extends RuntimeException {

    private final Object[] extraInfo;

    public WrappedException() {
        super();
        this.extraInfo=null;
    }

    public WrappedException(String message) {
        super(message);
        this.extraInfo=null;
    }

    public WrappedException(Throwable cause) {
        super(cause);
        this.extraInfo=null;
    }

    public WrappedException(Throwable cause, Object[] extraInfo) {
        super(cause);
        this.extraInfo=extraInfo;
    }

    public WrappedException(String message, Throwable cause) {
        super(message, cause);
        this.extraInfo=null;
    }

    public WrappedException(String message, Throwable cause, Object[] extraInfo) {
        super(message, cause);
        this.extraInfo=extraInfo;
    }
    
    public Object[] getExtraInfo() {
        return extraInfo;
    }
}