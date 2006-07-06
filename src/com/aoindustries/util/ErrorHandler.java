package com.aoindustries.util;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public interface ErrorHandler {
    
    void reportError(Throwable T, Object[] extraInfo);

    void reportWarning(Throwable T, Object[] extraInfo);
}
