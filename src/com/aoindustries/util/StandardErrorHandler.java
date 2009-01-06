package com.aoindustries.util;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.PrintWriter;

/**
 * An implementation of <code>ErrorHandler</code> that simply prints the errors
 * to <code>System.err</code> or the provided <code>PrintWriter</code> using <code>ErrorPrinter</code>.
 *
 * @see ErrorHandler
 * @see ErrorPrinter#printStackTraces(Throwable,Object[])
 *
 * @author  AO Industries, Inc.
 */
public class StandardErrorHandler implements ErrorHandler {
    
    private PrintWriter errWriter;

    public StandardErrorHandler() {
        this(new PrintWriter(System.err));
    }
    
    public StandardErrorHandler(PrintWriter errWriter) {
        this.errWriter=errWriter;
    }

    public void reportError(Throwable T, Object[] extraInfo) {
        ErrorPrinter.printStackTraces(T, errWriter, extraInfo);
    }

    public void reportWarning(Throwable T, Object[] extraInfo) {
        ErrorPrinter.printStackTraces(T, errWriter, extraInfo);
    }
}
