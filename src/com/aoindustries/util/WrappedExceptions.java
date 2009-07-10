package com.aoindustries.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Wraps multiple exceptions into one.  ErrorPrinter will unwrap each exception
 * into a single exception report.  Any exception provided more than once will
 * only be stored once.  The first exception is also passed to <code>initCause</code>.
 * Any null exception is ignored.
 *
 * @author  AO Industries, Inc.
 */
public class WrappedExceptions extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Gets an unmodifiable, unique set of exceptions.
     */
    private static final List<Throwable> getUniqueCauses(Throwable ... causes) {
        int len = causes.length;
        List<Throwable> uniqueCauses = new ArrayList<Throwable>(len);
        for(Throwable cause : causes) {
            if(cause!=null && !uniqueCauses.contains(cause)) uniqueCauses.add(cause);
        }
        return Collections.unmodifiableList(uniqueCauses);
    }

    private final List<Throwable> causes;

    public WrappedExceptions(Throwable ... causes) {
        super();
        this.causes = getUniqueCauses(causes);
        if(!this.causes.isEmpty()) initCause(this.causes.get(0));
    }

    public WrappedExceptions(String message, Throwable ... causes) {
        super(message);
        this.causes = getUniqueCauses(causes);
        if(!this.causes.isEmpty()) initCause(this.causes.get(0));
    }

    /**
     * Gets the unmodifiable list of causes.  The first cause is also
     * the value returned from getCause();
     */
    public List<Throwable> getCauses() {
        return causes;
    }
}