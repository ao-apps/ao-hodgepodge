package com.aoindustries.util;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public interface ErrorHandler {
    
    void reportError(Throwable T, Object[] extraInfo);

    void reportWarning(Throwable T, Object[] extraInfo);
}
