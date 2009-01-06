package com.aoindustries.util;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        return getTopLevelThreadGroup().activeCount();
    }

    public static ThreadGroup getTopLevelThreadGroup() {
        ThreadGroup TG=Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while((parent=TG.getParent())!=null) TG=parent;
        return TG;
    }
}