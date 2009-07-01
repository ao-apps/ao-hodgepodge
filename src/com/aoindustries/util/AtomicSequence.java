package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates incrementing identifiers in a thread-safe manner using atomic
 * primitives.
 *
 * @author  AO Industries, Inc.
 */
public class AtomicSequence implements Sequence {

    final private AtomicLong counter;

    /**
     * Starts at the value of 1.
     */
    public AtomicSequence() {
        this(1);
    }

    public AtomicSequence(long initialValue) {
        counter = new AtomicLong(initialValue);
    }

    public long getNextSequenceValue() {
        return counter.getAndIncrement();
    }
}
