package com.aoindustries.util.sort;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.util.*;

/**
 * Sorting statistics to help determine best algorithm for a specific task.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class SortStatistics {

    private long startTime;
    private int currentRecursion;

    private long totalTime;
    private long algorithmSwitches;
    private long gets;
    private long sets;
    private long objectComparisons;
    private long inListComparisons;
    private long swaps;
    private int maxRecursion;

    public SortStatistics() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "<init>()", null);
        try {
            reset();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void reset() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "reset()", null);
        try {
            startTime=-1;
            currentRecursion=0;

            totalTime=0;
            algorithmSwitches=0;
            gets=0;
            sets=0;
            objectComparisons=0;
            inListComparisons=0;
            swaps=0;
            maxRecursion=0;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortStarting() {
        Profiler.startProfile(Profiler.FAST, SortStatistics.class, "sortStarting()", null);
        try {
            if(startTime!=-1) throw new RuntimeException("sortStarting already called");
            startTime=System.currentTimeMillis();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void sortEnding() {
        Profiler.startProfile(Profiler.FAST, SortStatistics.class, "sortEnding()", null);
        try {
            if(startTime==-1) throw new RuntimeException("sortStarting not yet called");
            totalTime+=(System.currentTimeMillis()-startTime);
            startTime=-1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public void sortRecursing() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortRecursing()", null);
        try {
            currentRecursion++;
            if(currentRecursion>maxRecursion) maxRecursion=currentRecursion;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortUnrecursing() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortUnrecursing()", null);
        try {
            currentRecursion--;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortSwitchingAlgorithms() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortSwitchingAlgorithms()", null);
        try {
            algorithmSwitches++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getAlgorithmSwitchCount() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getAlgorithmSwitchCount()", null);
        try {
            return algorithmSwitches;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortGetting() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortGetting()", null);
        try {
            gets++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getGetCount() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getGetCount()", null);
        try {
            return gets;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortSetting() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortSetting()", null);
        try {
            sets++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getSetCount() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getSetCount()", null);
        try {
            return sets;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortObjectComparing() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortObjectComparing()", null);
        try {
            objectComparisons++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getObjectComparisons() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getObjectComparisons()", null);
        try {
            return objectComparisons;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortInListComparing() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortInListComparing()", null);
        try {
            inListComparisons++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getInListComparisons() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getInListComparisons()", null);
        try {
            return inListComparisons;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void sortSwapping() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "sortSwapping()", null);
        try {
            swaps++;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getSwapCount() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getSwapCount()", null);
        try {
            return swaps;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getTotalTime() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getTotalTime()", null);
        try {
            return totalTime;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public int getMaxRecursion() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortStatistics.class, "getMaxRecursion()", null);
        try {
            return maxRecursion;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void printStats(PrintWriter out) {
        Profiler.startProfile(Profiler.IO, SortStatistics.class, "printStats(PrintWriter)", null);
        try {
            out.print("Total Time...........: ");
            if(totalTime>Integer.MAX_VALUE) out.println(StringUtility.getTimeLengthString(totalTime));
            else {
                out.print(SQLUtility.getMilliDecimal((int)totalTime));
                out.println(" seconds");
            }
            out.print("Max Recursion........: "); out.println(maxRecursion);
            out.print("Algorithm Switches...: "); out.println(algorithmSwitches);
            out.print("Gets.................: "); out.println(gets);
            out.print("Sets.................: "); out.println(sets);
            out.print("In-List Swaps........: "); out.println(swaps);
            out.print("Object Comparisons...: "); out.println(objectComparisons);
            out.print("In-List Comparisons..: "); out.println(inListComparisons);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}