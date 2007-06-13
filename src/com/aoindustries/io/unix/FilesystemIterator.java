package com.aoindustries.io.unix;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.sort.AutoSort;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
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
    private final Map<String,FilesystemIteratorRule> prefixRules;
    private final boolean convertPathsToASCII;
    private final String startPath;

    /**
     * @deprecated  Please provide prefixRules, too.
     *
     * @see  #FilesystemIterator(Map,Map)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules) {
        this(rules, null, false, "/");
    }

    /**
     * Constructs an iterator without any filename conversions and starting at "/".
     *
     * @see  #FilesystemIterator(Map,boolean,String)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules) {
        this(rules, prefixRules, false, "/");
    }

    /**
     * @deprecated  Please provide prefixRules, too.
     *
     * @see  #FilesystemIterator(Map,Map,boolean)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, boolean convertPathsToASCII) {
        this(rules, null, convertPathsToASCII, "/");
    }

    /**
     * Constructs a filesystem iterator with the provided rules and conversion, starting at "/"
     *
     * @see  #FilesystemIterator(Map,Map,boolean,String)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules, boolean convertPathsToASCII) {
        this(rules, prefixRules, convertPathsToASCII, "/");
    }

    /**
     * @deprecated  Please provide prefixRules, too.
     *
     * @see  #FilesystemIterator(Map,Map,boolean,String)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, boolean convertPathsToASCII, String startPath) {
        this(rules, null, convertPathsToASCII, startPath);
    }

    /**
     * Constructs a filesystem iterator with the provided rules and conversion.
     *
     * @param  rules  the rules that will be applied during iteration
     * @param  convertPathsToASCII  if <code>true</code> any non-ASCII characters in paths will be converted to <code>?</code> characters.
     *                              should two or more files end up with the same path after conversion, the second one will be appended with
     *                              a sequence number, such as .2, .3, .4, ...
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules, boolean convertPathsToASCII, String startPath) {
        this.rules=rules;
        this.prefixRules=prefixRules;
        currentDirectories=null;
        currentConvertedDirectories=null;
        currentLists=null;
        currentConvertedLists=null;
        currentIndexes=null;
        filesDone=false;
        this.convertPathsToASCII = convertPathsToASCII;
        this.startPath = startPath;
    }

    private Stack<String> currentDirectories;
    private Stack<String> currentConvertedDirectories;
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
        FilesystemIteratorResult result = getNextResult(null);
        if(result==null) return null;
        return result.getUnixFile();
    }

    /**
     * Gets the next result from the iterator.
     */
    public FilesystemIteratorResult getNextResult() throws IOException {
        return getNextResult(null);
    }

    /**
     * Gets the next result from the iterator or <code>null</code> if the iterator has completed the iterator of the filesystem.
     * If an instance of <code>FilesystemIteratorResult</code> is provided the instance will be reused.  Otherwise a new instance
     * will be created and returned.
     */
    public FilesystemIteratorResult getNextResult(FilesystemIteratorResult result) throws IOException {
        synchronized(this) {
            // Loop trying to get the file because some special character filenames throw exceptions
            while(true) {
                if(filesDone) return null;

                // Initialize the stacks, if needed
                if(currentDirectories==null) {
                    if(startPath.equals("/")) {
                        // Starting at root will include the starting directory itself
                        (currentDirectories=new Stack<String>()).push("");
                        (currentLists=new Stack<String[]>()).push(new String[] {""});
                        if(convertPathsToASCII) {
                            (currentConvertedDirectories=new Stack<String>()).push("");
                            (currentConvertedLists=new Stack<String[]>()).push(new String[] {""});
                        }
                    } else {
                        // Starting at non root will include the starting directory itself
                        File startPathFile = new File(startPath);
                        String parent = startPathFile.getParent();
                        String name = startPathFile.getName();
                        (currentDirectories=new Stack<String>()).push(parent);
                        (currentLists=new Stack<String[]>()).push(new String[] {name});
                        if(convertPathsToASCII) {
                            (currentConvertedDirectories=new Stack<String>()).push(parent);
                            (currentConvertedLists=new Stack<String[]>()).push(new String[] {name});
                        }
                    }
                    (currentIndexes=new Stack<Integer>()).push(Integer.valueOf(0));
                }
                String currentDirectory=null;
                String currentConvertedDirectory=null;
                String[] currentList=null;
                String[] currentConvertedList=null;
                int currentIndex=-1;
                try {
                    currentDirectory=currentDirectories.peek();
                    currentList=currentLists.peek();
                    if(convertPathsToASCII) {
                        currentConvertedDirectory=currentConvertedDirectories.peek();
                        currentConvertedList=currentConvertedLists.peek();
                    }
                    currentIndex=currentIndexes.peek().intValue();

                    // Undo the stack as far as needed
                    while(currentDirectory!=null && currentIndex>=currentList.length) {
                        currentDirectories.pop();
                        currentDirectory=currentDirectories.peek();
                        currentLists.pop();
                        currentList=currentLists.peek();
                        if(convertPathsToASCII) {
                            currentConvertedDirectories.pop();
                            currentConvertedDirectory=currentConvertedDirectories.peek();
                            currentConvertedLists.pop();
                            currentConvertedList=currentConvertedLists.peek();
                        }
                        currentIndexes.pop();
                        currentIndex=currentIndexes.peek().intValue();
                    }
                } catch(EmptyStackException err) {
                    currentDirectory=null;
                    currentConvertedDirectory=null;
                }
                if(currentDirectory==null) {
                    filesDone=true;
                    return null;
                } else {
                    // Get the current filename
                    String filename;
                    if(currentDirectory.equals("/")) filename="/"+currentList[currentIndex];
                    else filename=currentDirectory+'/'+currentList[currentIndex];

                    // Get the converted filename
                    String convertedFilename;
                    if(convertPathsToASCII) {
                        if(currentConvertedDirectory.equals("/")) convertedFilename="/"+currentConvertedList[currentIndex];
                        else convertedFilename=currentConvertedDirectory+'/'+currentConvertedList[currentIndex];
                    } else convertedFilename=null;

                    // Increment index to point to the next file
                    currentIndexes.pop();
                    currentIndexes.push(Integer.valueOf(currentIndex+1));

                    try {
                        // Recurse for directories
                        UnixFile unixFile=new UnixFile(filename);
                        Stat stat = unixFile.getStat();
                        long statMode=stat.getRawMode();
                        if(
                            !UnixFile.isSymLink(statMode)
                            && UnixFile.isDirectory(statMode)
                            && !isSkip(filename)
                        ) {
                            // Push on stacks for next level
                            currentDirectories.push(filename);
                            if(convertPathsToASCII) currentConvertedDirectories.push(convertedFilename);
                            String[] list=isNoRecurse(filename) ? emptyStringArray : unixFile.list();
                            if(list==null) list = emptyStringArray;
                            else AutoSort.sortStatic(list);
                            currentLists.push(list);
                            if(convertPathsToASCII) currentConvertedLists.push(convertNonASCII(list));
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
    }

    public int getNextUnixFiles(UnixFile[] ufs, int batchSize) throws IOException {
        int c=0;
        while(c<batchSize) {
            UnixFile uf=getNextUnixFile();
            if(uf==null) break;
            ufs[c++]=uf;
        }
        return c;
    }
    
    /**
     * Gets the next results, reusing the <code>FilesystemIteratorResult</code> objects in the array or creating them if they do not exist.
     */
    public int getNextResults(FilesystemIteratorResult[] results, int batchSize) throws IOException {
        int c=0;
        while(c<batchSize) {
            FilesystemIteratorResult result=getNextResult(results[c]);
            if(result==null) break;
            results[c++]=result;
        }
        return c;
    }

    /**
     * Gets the rule that best suits the provided filename.
     *
     * If an exact match in rules is found, that is used.
     * Next, the longest prefix match in prefixRules is used.
     */
    private FilesystemIteratorRule getBestRule(String filename) {
        FilesystemIteratorRule rule = rules.get(filename);
        if(rule==null && prefixRules!=null) {
            String longestPrefix = null;
            for(Map.Entry<String,FilesystemIteratorRule> entry : prefixRules.entrySet()) {
                String prefix = entry.getKey();
                if((longestPrefix==null || prefix.length()>longestPrefix.length()) && filename.startsWith(prefix)) {
                    longestPrefix = prefix;
                    rule = entry.getValue();
                }
            }
        }
        return null;
    }

    public boolean isNoRecurse(String filename) throws IOException {
        FilesystemIteratorRule rule = getBestRule(filename);
        if(rule!=null) {
            return rule.isNoRecurse(filename);
        }
        return false;
    }

    public boolean isSkip(String filename) throws IOException {
        FilesystemIteratorRule rule = getBestRule(filename);
        if(rule!=null) {
            return rule.isSkip(filename);
        }
        return false;
    }
    
    /**
     * Converts a list of filenames (usually representing an entire directory list) to filenames that do not include
     * any non-ASCII characters.  Each resulting filename will be unique in the list and be at an index corresponding
     * to the parameter array.  Any non-ASCII character is replaced with a <code>?</code> character.  If two or more
     * files result in the same filename, the second one processed will have a sequence appended, such as .2, .3, .4, ...
     */
    public static String[] convertNonASCII(String[] list) {
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
    }

    /*
    public static void main(String[] args) {
        try {
            String path;
            if(args.length==0) path="/";
            else path=args[0];
            Map<String,FilesystemIteratorRule> rules = Collections.emptyMap();
            FilesystemIterator iter =  new FilesystemIterator(rules, false, path);
            UnixFile uf;
            while((uf=iter.getNextUnixFile())!=null) System.out.println(uf.getFilename());
        } catch(IOException err) {
            err.printStackTrace();
        }
    }
     */
}