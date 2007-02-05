package com.aoindustries.util;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * Provides access to an associated list of int's and booleans.
 *
 * @author  AO Industries, Inc.
 */
public class IntsAndLongs {

    private IntList ints;
    private LongList longs;

    public IntsAndLongs(IntList ints, LongList longs) {
        if(ints.size()!=longs.size()) throw new AssertionError("ints.size()!=longs.size()");
        this.ints=ints;
        this.longs=longs;
    }

    public int size() {
        return ints.size();
    }
    
    public int getInt(int index) {
        return ints.getInt(index);
    }
    
    public long getLong(int index) {
        return longs.getLong(index);
    }
    
    public boolean contains(int value) {
        return ints.contains(value);
    }

    public int indexOf(int value) {
        return ints.indexOf(value);
    }
}
