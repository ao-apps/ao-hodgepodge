package com.aoindustries.util;

/*
 * Copyright 2006-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.BitSet;

/**
 * Provides access to an associated list of int's and booleans.
 *
 * @author  AO Industries, Inc.
 */
public class IntsAndBooleans {

    private IntList ints;
    private BitSet booleans;

    public IntsAndBooleans(IntList ints, BitSet booleans) {
        this.ints=ints;
        this.booleans=booleans;
    }

    public int size() {
        return ints.size();
    }
    
    public int getInt(int index) {
        return ints.getInt(index);
    }
    
    public boolean getBoolean(int index) {
        return booleans.get(index);
    }

    public boolean contains(int value) {
        return ints.contains(value);
    }

    public int indexOf(int value) {
        return ints.indexOf(value);
    }
}
