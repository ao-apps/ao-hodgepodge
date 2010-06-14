/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util;

import java.io.PrintWriter;

/**
 * An implementation of <code>ErrorHandler</code> that simply prints the errors
 * to <code>System.err</code> or the provided <code>PrintWriter</code> using <code>ErrorPrinter</code>.
 *
 * @see ErrorHandler
 * @see ErrorPrinter#printStackTraces(Throwable,Object[])
 *
 * @author  AO Industries, Inc.
 *
 * @deprecated  Please use the logging facilities provided by JDK 1.4+
 */
@Deprecated
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
