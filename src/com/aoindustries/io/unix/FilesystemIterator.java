package com.aoindustries.io.unix;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.Profiler;
import com.aoindustries.util.sort.AutoSort;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Iterates through all of the files in a filesystem.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIterator {

    private static final String[] emptyStringArray = new String[0];

    private final Map<String,FilesystemIteratorRule> rules;
    private final boolean convertPathsToASCII;

    /**
     * Constructs an iterator without any filename conversions.
     *
     * @see  #FilesystemIterator(Map,boolean)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules) {
        this(rules, false);
        Profiler.startProfile(Profiler.INSTANTANEOUS, FilesystemIterator.class, "<init>(Map<String,FilesystemIteratorRule>)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * Constructs a filesystem iterator with the provided rules and conversion.
     *
     * @param  rules  the rules that will be applied during iteration
     * @param  convertPathsToASCII  if <code>true</code> any non-ASCII characters in paths will be converted to <code>?</code> characters.
     *                              should two or more files end up with the same path after conversion, the second one will be appended with
     *                              a sequence number, such as .2, .3, .4, ...
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, boolean convertPathsToASCII) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FilesystemIterator.class, "<init>(Map<String,FilesystemIteratorRule>,boolean)", null);
        try {
            this.rules=rules;
            currentDirectories=null;
            currentLists=null;
            currentConvertedLists=null;
            currentIndexes=null;
            filesDone=false;
            this.convertPathsToASCII = convertPathsToASCII;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    private Stack<String> currentDirectories;
    private Stack<String[]> currentLists;
    private Stack<String[]> currentConvertedLists;
    private Stack<Integer> currentIndexes;
    private boolean filesDone=false;

    /**
     * Gets the next UnixFile from the iterator.  If filename conversions are necessary, please
     * use <code>getNextResult</code>.
     *
     * @see  #getNextResult()
     * @see  #getNextResult(FilesystemIteratorResult)
     */
    public UnixFile getNextUnixFile() throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "getNextUnixFile()", null);
        try {
            FilesystemIteratorResult result = getNextResult(null);
            if(result==null) return null;
            return result.getUnixFile();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the next result from the iterator.
     */
    public FilesystemIteratorResult getNextResult() throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "getNextResult()", null);
        try {
            return getNextResult(null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the next result from the iterator or <code>null</code> if the iterator has completed the iterator of the filesystem.
     * If an instance of <code>FilesystemIteratorResult</code> is provided the instance will be reused.  Otherwise a new instance
     * will be created and returned.
     */
    public FilesystemIteratorResult getNextResult(FilesystemIteratorResult result) throws IOException {
        Profiler.startProfile(Profiler.IO, FilesystemIterator.class, "getNextResult()", null);
        try {
            synchronized(this) {
                // Loop trying to get the file because some special character filenames throw exceptions
                while(true) {
                    if(filesDone) return null;

                    // Initialize the stacks, if needed
                    if(currentDirectories==null) {
                        (currentDirectories=new Stack<String>()).push("");
                        (currentLists=new Stack<String[]>()).push(new String[] {""});
                        if(convertPathsToASCII) (currentConvertedLists=new Stack<String[]>()).push(new String[] {""});
                        (currentIndexes=new Stack<Integer>()).push(Integer.valueOf(0));
                    }
                    String currentDirectory=null;
                    String[] currentList=null;
                    String[] currentConvertedList=null;
                    int currentIndex=-1;
                    try {
                        currentDirectory=currentDirectories.peek();
                        currentList=currentLists.peek();
                        if(convertPathsToASCII) currentConvertedList=currentConvertedLists.peek();
                        currentIndex=currentIndexes.peek().intValue();

                        // Undo the stack as far as needed
                        while(currentDirectory!=null && currentIndex>=currentList.length) {
                            currentDirectories.pop();
                            currentDirectory=currentDirectories.peek();
                            currentLists.pop();
                            currentList=currentLists.peek();
                            if(convertPathsToASCII) {
                                currentConvertedLists.pop();
                                currentConvertedList=currentConvertedLists.peek();
                            }
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

                        // Get the converted filename
                        String convertedFilename;
                        if(convertPathsToASCII) {
                            if(currentDirectory.equals("/")) convertedFilename="/"+currentConvertedList[currentIndex++];
                            else convertedFilename=currentDirectory+'/'+currentConvertedList[currentIndex++];
                        } else convertedFilename=null;

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
                                String[] list=isNoRecurse(filename) ? emptyStringArray : unixFile.list();
                                if(list==null) list = emptyStringArray;
                                else AutoSort.sortStatic(list);
                                currentLists.push(list);
                                if(convertPathsToASCII) {
                                    currentConvertedLists.push(convertNonASCII(list));
                                }
                                currentIndexes.push(Integer.valueOf(0));
                            }
                            // Return if not a skip file
                            if(!isSkip(filename)) {
                                if(result==null) return new FilesystemIteratorResult(unixFile, convertedFilename);
                                result.unixFile = unixFile;
                                result.convertedFilename = convertedFilename;
                                return result;
                            }
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
    
    /**
     * Gets the next results, reusing the <code>FilesystemIteratorResult</code> objects in the array or creating them if they do not exist.
     */
    public int getNextResults(FilesystemIteratorResult[] results, int batchSize) throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "getNextFiles(FilesystemIteratorResult[],int)", null);
        try {
            int c=0;
            while(c<batchSize) {
                FilesystemIteratorResult result=getNextResult(results[c]);
                if(result==null) break;
                results[c++]=result;
            }
            return c;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isNoRecurse(String filename) throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "isNoRecurse(String)", null);
        try {
            FilesystemIteratorRule rule = rules.get(filename);
            if(rule!=null) {
                return rule.isNoRecurse(filename);
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isSkip(String filename) throws IOException {
        Profiler.startProfile(Profiler.FAST, FilesystemIterator.class, "isSkip(String)", null);
        try {
            FilesystemIteratorRule rule = rules.get(filename);
            if(rule!=null) {
                return rule.isSkip(filename);
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Converts a list of filenames (usually representing an entire directory list) to filenames that do not include
     * any non-ASCII characters.  Each resulting filename will be unique in the list and be at an index corresponding
     * to the parameter array.  Any non-ASCII character is replaced with a <code>?</code> character.  If two or more
     * files result in the same filename, the second one processed will have a sequence appended, such as .2, .3, .4, ...
     */
    public static String[] convertNonASCII(String[] list) {
        Profiler.startProfile(Profiler.UNKNOWN, FilesystemIterator.class, "convertNonASCII(String[])", null);
        try {
            String[] results = new String[list.length];
            
            // First build a list of all filenames that already have ? in them
            Set<String> questionFiles = new HashSet<String>();
            for(String filename : list) {
                if(filename.indexOf('?')!=-1) questionFiles.add(filename);
            }
            
            // Second, add each filename to the result array, replacing non-ASCII with ? and appending sequences as necessary
            StringBuilder sb = new StringBuilder();
            for(int c=0 ; c<list.length; c++) {
                String filename = list[c];
                sb.setLength(0);
                boolean converted = false;
                for(int d=0 ; d<filename.length(); d++) {
                    char ch = filename.charAt(d);
                    if(ch<' ' || ch>'~') {
                        sb.append('?');
                        converted = true;
                    } else sb.append(ch);
                }
                if(converted) {
                    String convertedFilename = sb.toString();
                    if(questionFiles.contains(convertedFilename)) {
                        for(int d=2; d<=Integer.MAX_VALUE; d++) {
                            String tempFilename = convertedFilename+"."+d;
                            if(!questionFiles.contains(tempFilename)) {
                                convertedFilename = tempFilename;
                                break;
                            }
                            if(d==Integer.MAX_VALUE) throw new RuntimeException("Unable to generate sequenced filename");
                        }
                    }
                    results[c] = convertedFilename;
                    questionFiles.add(convertedFilename);
                } else {
                    results[c] = filename;
                }
            }
            return results;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}