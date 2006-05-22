package com.aoindustries.io.unix;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import com.aoindustries.util.sort.*;
import java.io.*;
import java.util.*;

/**
 * Iterates through all of the files in a filesystem.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIterator {

    private final List<String> skipList;
    
    public FilesystemIterator(List<String> skipList) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FilesystemIterator.class, "<init>(List<String>)", null);
        try {
            this.skipList=skipList;
            currentDirectories=null;
            currentLists=null;
            currentIndexes=null;
            filesDone=false;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    private Stack<String> currentDirectories;
    private Stack<String[]> currentLists;
    private Stack<Integer> currentIndexes;
    private boolean filesDone=false;

    public UnixFile getNextUnixFile() throws IOException {
        Profiler.startProfile(Profiler.IO, FilesystemIterator.class, "getNextUnixFile()", null);
        try {
            synchronized(this) {
                // Loop trying to get the file because some special character filenames throw exceptions
                while(true) {
                    if(filesDone) return null;

                    // Initialize the stacks, if needed
                    if(currentDirectories==null) {
                        (currentDirectories=new Stack<String>()).push("");
                        (currentLists=new Stack<String[]>()).push(new String[] {""});
                        (currentIndexes=new Stack<Integer>()).push(Integer.valueOf(0));
                    }
                    String currentDirectory=null;
                    String[] currentList=null;
                    int currentIndex=-1;
                    try {
                        currentDirectory=currentDirectories.peek();
                        currentList=currentLists.peek();
                        currentIndex=currentIndexes.peek().intValue();

                        // Undo the stack as far as needed
                        while(currentDirectory!=null && currentIndex>=currentList.length) {
                            currentDirectories.pop();
                            currentDirectory=currentDirectories.peek();
                            currentLists.pop();
                            currentList=currentLists.peek();
                            currentIndexes.pop();
                            currentIndex=currentIndexes.peek().intValue();
                        }
                    } catch(EmptyStackException err) {
                        currentDirectory=null;
                    }
                    if(currentDirectory==null) {
                        filesDone=true;
                        return null;
                    } else {
                        // Get the current filename
                        String filename;
                        if(currentDirectory.equals("/")) filename="/"+currentList[currentIndex++];
                        else filename=currentDirectory+'/'+currentList[currentIndex++];

                        // Set to the next file
                        currentIndexes.pop();
                        currentIndexes.push(Integer.valueOf(currentIndex));

                        try {
                            // Recurse for directories
                            UnixFile unixFile=new UnixFile(filename);
                            long statMode=unixFile.getStatMode();
                            if(
                                !UnixFile.isSymLink(statMode)
                                && UnixFile.isDirectory(statMode)
                                && !isSkip(filename)
                            ) {
                                // Push on stacks for next level
                                currentDirectories.push(filename);
                                String[] list=unixFile.list();
                                if(list==null) list=new String[0];
                                AutoSort.sortStatic(list);
                                currentLists.push(list);
                                currentIndexes.push(Integer.valueOf(0));
                            }
                            // Return if not a skip file
                            if(!isSkip(filename)) return unixFile;
                        } catch(FileNotFoundException err) {
                            // Normal if the file was deleted while accessing
                        }
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int getNextUnixFiles(UnixFile[] ufs, int batchSize) throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "getNextFiles(UnixFile[],int)", null);
        try {
            int c=0;
            while(c<batchSize) {
                UnixFile uf=getNextUnixFile();
                if(uf==null) break;
                ufs[c++]=uf;
            }
            return c;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public boolean isSkip(String filename) {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "isSkip(String)", null);
        try {
            return skipList.contains(filename);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}