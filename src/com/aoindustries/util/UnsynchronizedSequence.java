package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Generates incrementing identifiers in a thread-unsafe manner using
 * a simple primitive without any synchronization.
 *
 * @author  AO Industries, Inc.
 */
public class UnsynchronizedSequence implements Sequence {

    private long counter;

    /**
     * Starts at the value of 1.
     */
    public UnsynchronizedSequence() {
        this(1);
    }

    public UnsynchronizedSequence(long initialValue) {
        counter = initialValue;
    }

    public long getNextSequenceValue() {
        return counter++;
    }
}
