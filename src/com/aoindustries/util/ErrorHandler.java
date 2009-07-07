package com.aoindustries.util;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 *
 * @deprecated  Please use the logging facilities provided by JDK 1.4+
 */
@Deprecated
public interface ErrorHandler {
    
    void reportError(Throwable T, Object[] extraInfo);

    void reportWarning(Throwable T, Object[] extraInfo);
}
