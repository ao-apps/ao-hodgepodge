package com.aoindustries.util;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.util.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public final class ThreadUtility {

    private ThreadUtility() {
    }

    public static int getThreadCount() {
        Profiler.startProfile(Profiler.FAST, ThreadUtility.class, "getThreadCount()", null);
        try {
            return getTopLevelThreadGroup().activeCount();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static ThreadGroup getTopLevelThreadGroup() {
        Profiler.startProfile(Profiler.FAST, ThreadUtility.class, "getTopLevelThreadGroup()", null);
        try {
            ThreadGroup TG=Thread.currentThread().getThreadGroup();
            ThreadGroup parent;
            while((parent=TG.getParent())!=null) TG=parent;
            return TG;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}