package com.aoindustries.util.sort;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
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
        reset();
    }

    public void reset() {
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
    }

    public void sortStarting() {
        if(startTime!=-1) throw new RuntimeException("sortStarting already called");
        startTime=System.currentTimeMillis();
    }

    public void sortEnding() {
        if(startTime==-1) throw new RuntimeException("sortStarting not yet called");
        totalTime+=(System.currentTimeMillis()-startTime);
        startTime=-1;
    }
    
    public void sortRecursing() {
        currentRecursion++;
        if(currentRecursion>maxRecursion) maxRecursion=currentRecursion;
    }

    public void sortUnrecursing() {
        currentRecursion--;
    }

    public void sortSwitchingAlgorithms() {
        algorithmSwitches++;
    }

    public long getAlgorithmSwitchCount() {
        return algorithmSwitches;
    }

    public void sortGetting() {
        gets++;
    }

    public long getGetCount() {
        return gets;
    }

    public void sortSetting() {
        sets++;
    }

    public long getSetCount() {
        return sets;
    }

    public void sortObjectComparing() {
        objectComparisons++;
    }

    public long getObjectComparisons() {
        return objectComparisons;
    }

    public void sortInListComparing() {
        inListComparisons++;
    }

    public long getInListComparisons() {
        return inListComparisons;
    }

    public void sortSwapping() {
        swaps++;
    }

    public long getSwapCount() {
        return swaps;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public int getMaxRecursion() {
        return maxRecursion;
    }

    public void printStats(PrintWriter out) {
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
    }
}