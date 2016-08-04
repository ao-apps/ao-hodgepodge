/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.security;

import java.security.GeneralSecurityException;

/**
 * @author  AO Industries, Inc.
 */
public class LoginException extends GeneralSecurityException {

    private static final long serialVersionUID = 725311633658233258L;

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