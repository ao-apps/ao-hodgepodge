/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util.sort;

import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.StringUtility;
import java.io.PrintWriter;

/**
 * Sorting statistics to help determine best algorithm for a specific task.
 *
 * @author  AO Industries, Inc.
 */
final public class SortStatistics {

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

    public void sortGetting(int increment) {
        gets += increment;
    }

    public long getGetCount() {
        return gets;
    }

    public void sortSetting() {
        sets++;
    }

    public void sortSetting(int increment) {
        sets += increment;
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
