package com.aoindustries.io;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Our backup directories contain parallel directories with many hard links.
 * The performance of deleting more than one of the directories can be improved
 * by deleting from them in parallel.
 * 
 * TODO: Verify this is, in fact, true.
 * 
 * TODO: Use this from the back-up clean-up code.
 *
 * @author  AO Industries, Inc.
 */
public class ParallelDelete {

    /**
     * Make no instances.
     */
    private ParallelDelete() {}
    
    /**
     * Deletes multiple directories in parallel (but not concurrently).
     */
    public static void main(String[] args) {
        if(args.length==0) {
            System.err.println("Usage: "+ParallelDelete.class.getName()+" [-v] path {path}");
            System.exit(1);
        } else {
            List<File> directories = new ArrayList<File>(args.length);
            PrintStream verboseOutput = null;
            for(String arg : args) {
                if(arg.equals("-v")) verboseOutput = System.err;
                else directories.add(new File(arg));
            }
            try {
                parallelDelete(directories, verboseOutput);
            } catch(IOException err) {
                err.printStackTrace(System.err);
                System.err.flush();
                System.exit(2);
            }
        }
    }

    /**
     * Recursively deletes all of the files in the provided directories.  Also
     * deletes the directories themselves.  It is assumed the directory contents
     * are not changing, and there are no safe guards to protect against this.
     * This implies that there is a race condition where the delete could
     * possibly follow a symbolic link and delete outside the intended directory
     * trees.
     */
    public static void parallelDelete(List<File> directories, PrintStream verboseOutput) throws IOException {
        final int numDirectories = directories.size();

        // The set of next files is kept in key order so that it can scale with O(n*log(n)) for larger numbers of directories
        // as opposed to O(n^2) for a list.  This is similar to the fix for AWStats logresolvemerge provided by Dan Armstrong
        // a couple of years ago.
        Map<String,SortedSet<FilesystemIterator>> nextFiles = new TreeMap<String,SortedSet<FilesystemIterator>>(
            new Comparator<String>() {
                public int compare(String S1, String S2) {
                    // Make sure directories are sorted after their directory contents
                    if(S1.equals(S2)) return 0;
                    if(S2.startsWith(S1)) return 1;
                    if(S1.startsWith(S2)) return -1;
                    return S1.compareTo(S2);
                }
            }
        );
        {
            final Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
            for(File directory : directories) {
                if(!directory.exists()) throw new IOException("Directory not found: "+directory.getPath());
                if(!directory.isDirectory()) throw new IOException("Not a directory: "+directory.getPath());
                String path = directory.getCanonicalPath();
                Map<String,FilesystemIteratorRule> rules = Collections.singletonMap(path, FilesystemIteratorRule.OK);
                FilesystemIterator iterator = new FilesystemIterator(rules, prefixRules, path, false);
                File nextFile = iterator.getNextFile();
                if(nextFile!=null) {
                    String relPath = getRelativePath(nextFile, iterator);
                    SortedSet<FilesystemIterator> list = nextFiles.get(relPath);
                    if(list==null) nextFiles.put(relPath, list = new TreeSet<FilesystemIterator>());
                    list.add(iterator);
                }
            }
        }

        // Main loop, continue until nextFiles is empty
        StringBuilder SB = new StringBuilder();
        while(true) {
            Iterator<String> iter = nextFiles.keySet().iterator();
            if(!iter.hasNext()) break;
            String relPath = iter.next();
            for(FilesystemIterator iterator : nextFiles.remove(relPath)) {
                SB.setLength(0);
                SB.append(iterator.getStartPath());
                SB.append(relPath);
                String fullPath = SB.toString();
                if(verboseOutput!=null) {
                    verboseOutput.print(fullPath);
                    verboseOutput.flush();
                }
                File deleteme = new File(fullPath);
                if(!deleteme.delete()) throw new IOException("Unable to delete: "+fullPath);
                // Get the next file
                File nextFile = iterator.getNextFile();
                if(nextFile!=null) {
                    String newRelPath = getRelativePath(nextFile, iterator);
                    SortedSet<FilesystemIterator> list = nextFiles.get(newRelPath);
                    if(list==null) nextFiles.put(newRelPath, list = new TreeSet<FilesystemIterator>());
                    list.add(iterator);
                }
            }
        }
    }

    /**
     * Gets the relative path for the provided file from the provided iterator.
     */
    private static String getRelativePath(File file, FilesystemIterator iterator) throws IOException {
        String path = file.getPath();
        String prefix = iterator.getStartPath();
        if(!path.startsWith(prefix)) throw new IOException("path doesn't start with prefix: path=\""+path+"\", prefix=\""+prefix+"\"");
        return path.substring(prefix.length());
    }
}
