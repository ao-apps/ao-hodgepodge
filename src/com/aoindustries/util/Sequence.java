package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Generates unique long identifiers.
 *
 * @author  AO Industries, Inc.
 */
public interface Sequence {
    long getNextSequenceValue();
}
