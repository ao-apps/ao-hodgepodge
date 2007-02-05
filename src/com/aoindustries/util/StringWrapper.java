package com.aoindustries.util;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * Wraps a <code>String</code> so that swing widgets will work correctly
 * when provided multiple items with the same string.
 *
 * @author  AO Industries, Inc.
 */
public class StringWrapper {

    private final String string;

    public StringWrapper(String string) {
	this.string=string;
    }

    public String toString() {
	return string;
    }
}