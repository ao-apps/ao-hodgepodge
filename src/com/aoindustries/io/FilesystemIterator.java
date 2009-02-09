package com.aoindustries.io;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.sort.AutoSort;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/**
 * Iterates through all of the files in a filesystem.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIterator implements Comparable<FilesystemIterator> {

    private static final String[] emptyStringArray = new String[0];

    private final Map<String,FilesystemIteratorRule> rules;
    private final Map<String,FilesystemIteratorRule> prefixRules;
    private final String startPath;
    private final boolean isPreorder;

    /**
     * Constructs an iterator without any filename conversions and starting at all roots.
     * Performs a preorder traversal - directory before directory contents.
     *
     * @see  #FilesystemIterator(Map,Map,String,boolean)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules) {
        this(rules, prefixRules, "", true);
    }

    /**
     * Constructs an iterator without any filename conversions and starting at all roots.
     *
     * @see  #FilesystemIterator(Map,Map,String,boolean)
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules, boolean isPreorder) {
        this(rules, prefixRules, "", isPreorder);
    }

    /**
     * Constructs a filesystem iterator with the provided rules and conversion.
     * Performs a preorder traversal - directory before directory contents.
     *
     * @param  rules  the rules that will be applied during iteration
     * @param  startPath  if "", all roots will be used, otherwise starts at the provided path
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules, String startPath) {
        this(rules, prefixRules, startPath, true);
    }

    /**
     * Constructs a filesystem iterator with the provided rules and conversion.
     *
     * @param  rules  the rules that will be applied during iteration
     * @param  startPath  if "", all roots will be used, otherwise starts at the provided path
     */
    public FilesystemIterator(Map<String,FilesystemIteratorRule> rules, Map<String,FilesystemIteratorRule> prefixRules, String startPath, boolean isPreorder) {
        this.rules=rules;
        this.prefixRules=prefixRules;
        currentDirectories=null;
        currentLists=null;
        currentIndexes=null;
        filesDone=false;
        this.startPath = startPath;
        this.isPreorder = isPreorder;
    }

    private Stack<String> currentDirectories;
    private Stack<String[]> currentLists;
    private Stack<Integer> currentIndexes;
    private boolean filesDone=false;

    /**
     * Gets the next file from the iterator or <code>null</code> if the iterator has completed the iteration of the filesystem.
     */
    public File getNextFile() throws IOException {
        synchronized(this) {
            // Loop trying to get the file because files may be removed during the loop
            while(true) {
                if(filesDone) return null;

                // Initialize the stacks, if needed
                if(currentDirectories==null) {
                    if(startPath.length()==0) {
                        // Starting at root will include the starting directory itself
                        (currentDirectories=new Stack<String>()).push("");
                        (currentLists=new Stack<String[]>()).push(getFilesystemRoots());
                    } else {
                        if(isFilesystemRoot(startPath)) {
                            // Starting from a root, has no parent
                            (currentDirectories=new Stack<String>()).push("");
                            (currentLists=new Stack<String[]>()).push(new String[] {startPath});
                        } else {
                            // Starting at non root will include the starting directory itself
                            File startPathFile = new File(startPath);
                            String parent = startPathFile.getParent();
                            String name = startPathFile.getName();
                            (currentDirectories=new Stack<String>()).push(parent);
                            (currentLists=new Stack<String[]>()).push(new String[] {name});
                        }
                    }
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
                        currentLists.pop();
                        currentIndexes.pop();
                        String oldCurrentDirectory = currentDirectory;
                        currentDirectory=currentDirectories.peek();
                        currentList=currentLists.peek();
                        currentIndex=currentIndexes.peek().intValue();
                        if(!isPreorder) return new File(oldCurrentDirectory); // This is performed last because the EmptyStackException caused by peek indicates end of traversal
                    }
                } catch(EmptyStackException err) {
                    currentDirectory=null;
                }
                if(currentDirectory==null) {
                    filesDone=true;
                    return null;
                } else {
                    // Get the current filename
                    final String filename;
                    if(currentDirectory.length()==0) filename=currentList[currentIndex];
                    else if(currentDirectory.endsWith(File.separator)) filename=currentDirectory+currentList[currentIndex];
                    else filename=currentDirectory+File.separatorChar+currentList[currentIndex];

                    // Increment index to point to the next file
                    currentIndexes.pop();
                    currentIndexes.push(Integer.valueOf(currentIndex+1));

                    try {
                        final File file=new File(filename);
                        if(file.isDirectory()) {
                            // Directories
                            final String filenamePlusSlash;
                            if(filename.endsWith(File.separator)) filenamePlusSlash = filename;
                            else filenamePlusSlash = filename+File.separatorChar;

                            final boolean includeDirectory;
                            final boolean recurse;
                            // If the settings for the directory indicate include
                            if(isIncluded(filename)) {
                                // Directory is included, optimized recurse follows
                                includeDirectory = true;
                                FilesystemIteratorRule rule = rules.get(filenamePlusSlash);
                                if(rule==null) {
                                    recurse = true;
                                } else {
                                    if(rule.isIncluded(filenamePlusSlash)) {
                                        recurse = true;
                                    } else {
                                        // This is the shortcut to not list directory when flagged as "/proc/"-style skip and
                                        // there are no overriding children
                                        recurse = hasIncludedChild(filenamePlusSlash);
                                    }
                                }
                            } else {
                                // Force include if there are any backup-enabled settings that are a child of this
                                if(hasIncludedChild(filenamePlusSlash)) {
                                    includeDirectory = true;
                                    recurse = true;
                                } else {
                                    includeDirectory = false;
                                    recurse = false;
                                }
                            }
                            // Push on stacks for next level
                            if(includeDirectory) {
                                String[] list;
                                if(recurse) {
                                    // Skip anything that is not canonical, this avoids symbolic link targets
                                    if(file.getCanonicalPath().equals(filename)) {
                                        list = file.list();
                                        if(list==null) list = emptyStringArray;
                                        else AutoSort.sortStatic(list);
                                    } else {
                                        //System.err.println("Skipping non-canonical directory listing: "+filename);
                                        list = emptyStringArray;
                                    }
                                } else {
                                    list = emptyStringArray;
                                }
                                // No need to push onto the stack if the children are empty?
                                if(list.length>0) {
                                    currentDirectories.push(filename);
                                    currentLists.push(list);
                                    currentIndexes.push(Integer.valueOf(0));
                                    if(isPreorder) return file;
                                } else {
                                    // If empty directory both preorder and postorder return directory immediately
                                    // and bypass using the stack.
                                    return file;
                                }
                            } else {
                                if(recurse) throw new AssertionError("recurse=true and includeDirectory=false");
                            }
                        } else {
                            // Non-directories
                            if(isIncluded(filename)) return file;
                        }
                    } catch(FileNotFoundException err) {
                        // Normal if the file was deleted while accessing
                    }
                }
            }
        }
    }

    /**
     * Gets the next files, up to batchSize.
     * @return the number of files in the array, zero (0) indicates iteration has completed
     * @throws java.io.IOException
     */
    public int getNextFiles(final File[] files, final int batchSize) throws IOException {
        int c=0;
        while(c<batchSize) {
            File file=getNextFile();
            if(file==null) break;
            files[c++]=file;
        }
        return c;
    }

    /**
     * Gets the filesystem roots.  It will only include the root if it has
     * at least one backup-enabled rule.  An empty rule ("") will implicitely
     * allow all roots.
     */
    protected String[] getFilesystemRoots() throws IOException {
        File[] fileRoots=File.listRoots();
        List<String> tempRoots=new ArrayList<String>(fileRoots.length);
        for(int c=0;c<fileRoots.length;c++) {
            String root=fileRoots[c].getPath();
            // Only add if this root is used for at least one backup setting
            FilesystemIteratorRule defaultRule = rules.get("");
            if(
                (defaultRule!=null && defaultRule.isIncluded(root))
                || hasIncludedChild(root)
            ) tempRoots.add(root);
        }
        return tempRoots.toArray(new String[tempRoots.size()]);
    }

    /**
     * Determines if a path is a possible filesystem root
     */
    protected boolean isFilesystemRoot(String filename) throws IOException {
        String[] roots=getFilesystemRoots();
        int len=roots.length;
        for(int c=0;c<roots.length;c++) {
            if(roots[c].equals(filename)) return true;
        }
        return false;
    }

    /**
     * Gets the rule that best suits the provided filename.  The rule is the longer
     * rule between the regular rules and the prefix rules.
     * 
     * The regular rules are scanned first by looking through the filename and then
     * all parents up to the root for the first match.  These use Map lookups in the
     * set of rules so this should still perform well when there are many rules.
     * For example, when searching for the rule for /home/u/username/tmp/, this
     * will search:
     * <ol>
     *   <li>/home/u/username/tmp/</li>
     *   <li>/home/u/username/tmp</li>
     *   <li>/home/u/username/</li>
     *   <li>/home/u/username</li>
     *   <li>/home/u/</li>
     *   <li>/home/u</li>
     *   <li>/home/</li>
     *   <li>/home</li>
     *   <li>/</li>
     *   <li></li>
     * </ol>
     *
     * Next, the entire list of prefix rules is searched, with the longest rule
     * being used if it is a longer match than that found in the regular rules.
     */
    private FilesystemIteratorRule getBestRule(final String filename) {
        String longestPrefix = null;
        FilesystemIteratorRule rule = null;
        // First search the path and all of its parents for the first regular rule
        String path = filename;
        while(true) {
            // Check the current path for an exact match
            //System.out.println("DEBUG: Checking "+path);
            rule = rules.get(path);
            if(rule!=null) {
                longestPrefix = path;
                break;
            }

            // If done, break the loop
            int pathLen = path.length();
            if(pathLen==0) break;

            int lastSlashPos = path.lastIndexOf(File.separatorChar);
            if(lastSlashPos==-1) {
                path = "";
            } else if(lastSlashPos==(pathLen-1)) {
                // If ends with a slash, remove that slash
                path = path.substring(0, lastSlashPos);
            } else {
                // Otherwise, remove and leave the last slash
                path = path.substring(0, lastSlashPos+1);
            }
        }
        if(prefixRules!=null) {
            for(Map.Entry<String,FilesystemIteratorRule> entry : prefixRules.entrySet()) {
                String prefix = entry.getKey();
                if(
                    (longestPrefix==null || prefix.length()>longestPrefix.length())
                    && filename.startsWith(prefix)
                ) {
                    //System.err.println("DEBUG: FilesystemIterator: getBestRule: filename="+filename+", prefix="+prefix+", longestPrefix="+longestPrefix);
                    longestPrefix = prefix;
                    rule = entry.getValue();
                }
            }
        }
        return rule;
    }

    private boolean isIncluded(String filename) throws IOException {
        FilesystemIteratorRule rule = getBestRule(filename);
        if(rule!=null) {
            return rule.isIncluded(filename);
        }
        return false;
    }

    private boolean hasIncludedChild(String filenamePlusSlash) throws IOException {
        // Iterate through all rules, looking for the first one that starts with the current filename+File.separatorChar and has backup enabled
        for(Map.Entry<String,FilesystemIteratorRule> entry : rules.entrySet()) {
            String path = entry.getKey();
            if(path.startsWith(filenamePlusSlash)) {
                FilesystemIteratorRule rule = entry.getValue();
                if(rule.isIncluded(filenamePlusSlash)) return true;
            }
        }
        for(Map.Entry<String,FilesystemIteratorRule> entry : prefixRules.entrySet()) {
            String path = entry.getKey();
            FilesystemIteratorRule rule = entry.getValue();
            if(path.startsWith(filenamePlusSlash) && rule.isIncluded(filenamePlusSlash)) return true;
        }
        return false;
    }

    /**
     * Converts a list of filenames (usually representing an entire directory list) to filenames that do not include
     * any non-ASCII characters.  Each resulting filename will be unique in the list and be at an index corresponding
     * to the parameter array.  Any non-ASCII character is replaced with a <code>?</code> character.  If two or more
     * files result in the same filename, the second one processed will have a sequence appended, such as .2, .3, .4, ...
     * 
     * @deprecated
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

    static class FilenameIterator implements Iterator<String> {

        private final FilesystemIterator filesystemIterator;

        File next;

        FilenameIterator(FilesystemIterator filesystemIterator) throws IOException {
            this.filesystemIterator = filesystemIterator;
            next = filesystemIterator.getNextFile();
        }

        public boolean hasNext() {
            return next!=null;
        }

        public String next() {
            try {
                if(next==null) throw new NoSuchElementException();
                String retVal = next.getPath();
                next = filesystemIterator.getNextFile();
                return retVal;
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    /**
     * Gets an iterator of filenames.
     */
    public Iterator<String> getFilenameIterator() throws IOException {
        return new FilenameIterator(this);
    }
    
    /**
     * Gets the start path for this iterator.
     */
    public String getStartPath() {
        return startPath;
    }
    
    /**
     * Gets the preorder flag for this iterator.
     */
    public boolean isPreorder() {
        return isPreorder;
    }
    
    public int compareTo(FilesystemIterator other) {
        return startPath.compareTo(other.startPath);
    }
}