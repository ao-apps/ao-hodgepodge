package com.aoindustries.profiler;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.io.*;
import java.util.*;

/**
 * Tracks the use and runtimes of methods or blocks of code.
 *
 * @author  AO Industries, Inc.
 */
final public class Profiler {

    /**
     * The different profiler levels.
     */
    public static final int
        NONE=0,
        UNKNOWN=1,
        SLOW=2,
        IO=3,
        FAST=4,
        INSTANTANEOUS=5
    ;
    private static final int START_SIZE=100;

    private static int profilerLevel=NONE;

    public static int getProfilerLevel() {
        return profilerLevel;
    }

    public static String getProfilerLevelString(int level) {
        switch(level) {
            case NONE: return "None";
            case UNKNOWN: return "Unknown";
            case SLOW: return "Slow";
            case IO: return "IO";
            case FAST: return "Fast";
            case INSTANTANEOUS: return "Instantaneous";
            default: throw new IllegalArgumentException("Unexpected value for level: " + level);
        }
    }

    public static int parseProfilerLevel(String S) throws IllegalArgumentException {
        if(S==null) return NONE;
        S=S.trim();
        if(S.length()==0 || S.equals("0") || S.equalsIgnoreCase("None")) return NONE;
        else if(S.equals("1") || S.equalsIgnoreCase("Unknown")) return UNKNOWN;
        else if(S.equals("2") || S.equalsIgnoreCase("Slow")) return SLOW;
        else if(S.equals("3") || S.equalsIgnoreCase("IO")) return IO;
        else if(S.equals("4") || S.equalsIgnoreCase("Fast")) return FAST;
        else if(S.equals("5") || S.equalsIgnoreCase("Instantaneous")) return INSTANTANEOUS;
        throw new IllegalArgumentException("Unable to parse profiler level: "+S);
    }

    public static void setProfilerLevel(String S) throws IllegalArgumentException {
        setProfilerLevel(parseProfilerLevel(S));
    }

    public static void setProfilerLevel(int level) throws IllegalArgumentException {
        if(level<NONE || level>INSTANTANEOUS) throw new IllegalArgumentException("Invalid profiler level: "+level);
        profilerLevel=level;
    }

    // Each thread has its own index, sorted by the threads identity hash code
    private static int threadsUsed=0;
    private static Thread[] threads=new Thread[START_SIZE];
    private static int[] stackDepths=new int[START_SIZE];
    private static long[][] startTimes=new long[START_SIZE][];
    private static PrivateMethodProfile[][] methodProfiles=new PrivateMethodProfile[START_SIZE][];

    /**
     * Caches profile method names.
     */
    private static final SortedArrayList<PrivateMethodProfile> methodProfileCache=new SortedArrayList<PrivateMethodProfile>();

    private static long methodCount=0;
    private static int concurrency=0;
    private static int maxConcurrency=0;

    public static void endProfile(int level) {
        if(profilerLevel>=level) {
            synchronized(Profiler.class) {
                int index;
                int stackDepth=--stackDepths[index=getThreadIndex()];
                if(stackDepth>=0) {
                    // Get the values
                    PrivateMethodProfile profile=methodProfiles[index][stackDepth];

                    // Debug level mismatches
                    if(profile.level!=level) throw new IllegalArgumentException("Profile level mismatch.  startProfile called with level="+profile.level+" and endProfile called with level="+level);

                    // Update the stats
                    long timeSpan=System.currentTimeMillis()-startTimes[index][stackDepth];
                    profile.totalTime+=timeSpan;
                    if(timeSpan<profile.minTime) profile.minTime=timeSpan;
                    if(timeSpan>profile.maxTime) profile.maxTime=timeSpan;
                    concurrency--;
                } else {
                    int copyLen=(--threadsUsed)-index;
                    int fromIndex=index+1;
                    System.arraycopy(threads, fromIndex, threads, index, copyLen);
                    System.arraycopy(stackDepths, fromIndex, stackDepths, index, copyLen);
                    System.arraycopy(startTimes, fromIndex, startTimes, index, copyLen);
                    System.arraycopy(methodProfiles, fromIndex, methodProfiles, index, copyLen);
                }
            }
        }
    }

    private static int getThreadIndex() {
        Thread thread=Thread.currentThread();
        int hashCode=System.identityHashCode(thread);

        // Find the location to insert the object at
        int bottom=0;
        int range=threadsUsed;
        while(range>0) {
            int half;
            int pos;
            int thash;
            if(
                hashCode==(
                    thash=System.identityHashCode(
                        threads[
                            pos=bottom+(
                                half=range>>>1
                            )
                        ]
                    )
                )
            ) return pos;
            if(hashCode>thash) {
                if(half==0) {
                    bottom++;
                    break;
                }
                bottom=pos;
                range-=half;
            } else range=half;
        }

        int destIndex=bottom+1;
        int copyLen=threadsUsed-bottom;
        int arraySize;
        if(threadsUsed>=(arraySize=threads.length)) {
            int newLen=threadsUsed<<1;
            Thread[] newThreads=new Thread[newLen];
            System.arraycopy(threads, 0, newThreads, 0, bottom);
            System.arraycopy(threads, bottom, newThreads, destIndex, copyLen);
            threads=newThreads;
            int[] newDepths=new int[newLen];
            System.arraycopy(stackDepths, 0, newDepths, 0, bottom);
            System.arraycopy(stackDepths, bottom, newDepths, destIndex, copyLen);
            stackDepths=newDepths;
            long[][] newTimes=new long[newLen][];
            System.arraycopy(startTimes, 0, newTimes, 0, bottom);
            System.arraycopy(startTimes, bottom, newTimes, destIndex, copyLen);
            startTimes=newTimes;
            PrivateMethodProfile[][] newProfiles=new PrivateMethodProfile[newLen][];
            System.arraycopy(methodProfiles, 0, newProfiles, 0, bottom);
            System.arraycopy(methodProfiles, bottom, newProfiles, destIndex, copyLen);
            methodProfiles=newProfiles;
        } else {
            System.arraycopy(threads, bottom, threads, destIndex, copyLen);
            System.arraycopy(stackDepths, bottom, stackDepths, destIndex, copyLen);
            System.arraycopy(startTimes, bottom, startTimes, destIndex, copyLen);
            System.arraycopy(methodProfiles, bottom, methodProfiles, destIndex, copyLen);
        }

        threadsUsed++;
        threads[bottom]=thread;
        stackDepths[bottom]=0;
        startTimes[bottom]=new long[START_SIZE];
        methodProfiles[bottom]=new PrivateMethodProfile[START_SIZE];

        return bottom;
    }

    public static int getConcurrency() {
        return concurrency;
    }

    public static int getMaxConcurrency() {
        return maxConcurrency;
    }

    public static long getMethodUses() {
        return methodCount;
    }

    /**
     * Gets a modifable list of a snapshot copies of the current profiler state.  The list is
     * modifiable to allow direct sorting after retrieval.
     */
    public synchronized static List<MethodProfile> getMethodProfiles() {
        int size=methodProfileCache.size();
        List<MethodProfile> mps = new ArrayList<MethodProfile>(size);
        for(int c=0;c<size;c++) mps.add(new MethodProfile(methodProfileCache.get(c)));
        return mps;
    }

    public synchronized static MethodProfile getMethodProfile(int level, Class clazz, String method, Object param1) {
        return new MethodProfile(getMethodProfile0(level, clazz, method, param1));
    }

    private static PrivateMethodProfile getMethodProfile0(int level, Class clazz, String method, Object param1) {
        int hash=PrivateMethodProfile.hashCode(clazz, method, param1);
        int index=methodProfileCache.indexOf(hash);
        if(index>=0) {
            // Look forward until a match is found
            int size=methodProfileCache.size();
            while(index<size) {
                PrivateMethodProfile mp=methodProfileCache.get(index);
                if(mp.hashCode()!=hash) break;
                if(mp.equals(level, clazz, method, param1)) return mp;
            }
        }
        return null;
    }

    public static void startProfile(int level, Class clazz, String method, Object param1) {
        if(profilerLevel>=level) {
            synchronized(Profiler.class) {
                int c;
                if((c=++concurrency)>maxConcurrency) maxConcurrency=c;
                methodCount++;

                PrivateMethodProfile profile=getMethodProfile0(level, clazz, method, param1);
                if(profile==null) {
                    methodProfileCache.add(profile=new PrivateMethodProfile(level, clazz, method, param1));
                }

                profile.useCount++;

                int index=getThreadIndex();

                int stackDepth=stackDepths[index]++;
                long[] times=startTimes[index];
                PrivateMethodProfile[] profiles=methodProfiles[index];
                
                int arraySize=profiles.length;
                if(stackDepth>=arraySize) {
                    // Grow the arrays
                    int newLen=stackDepth<<1;
                    long[] newTimes=new long[newLen];
                    System.arraycopy(times, 0, newTimes, 0, stackDepth);
                    startTimes[index]=times=newTimes;
                    PrivateMethodProfile[] newProfiles=new PrivateMethodProfile[newLen];
                    System.arraycopy(profiles, 0, newProfiles, 0, stackDepth);
                    methodProfiles[index]=profiles=newProfiles;
                }
                times[stackDepth]=System.currentTimeMillis();
                profiles[stackDepth]=profile;
            }
        }
    }
}