/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2014, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.io;

import com.aoapps.lang.EmptyArrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Iterates through all of the files in a file system.
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIterator implements Comparable<FilesystemIterator> {

	private final Map<String, FilesystemIteratorRule> rules;
	private final Map<String, FilesystemIteratorRule> prefixRules;
	private final String startPath;
	private final boolean isPreorder;
	private final boolean isSorted;

	/**
	 * Constructs an iterator without any filename conversions and starting at all roots.
	 * Performs a preorder traversal - directory before directory contents.
	 * The directories are sorted.
	 *
	 * @see  #FilesystemIterator(Map,Map,String,boolean,boolean)
	 */
	public FilesystemIterator(Map<String, FilesystemIteratorRule> rules, Map<String, FilesystemIteratorRule> prefixRules) {
		this(rules, prefixRules, "", true, true);
	}

	/**
	 * Constructs an iterator without any filename conversions and starting at all roots.
	 *
	 * @see  #FilesystemIterator(Map,Map,String,boolean,boolean)
	 */
	public FilesystemIterator(Map<String, FilesystemIteratorRule> rules, Map<String, FilesystemIteratorRule> prefixRules, boolean isPreorder, boolean isSorted) {
		this(rules, prefixRules, "", isPreorder, isSorted);
	}

	/**
	 * Constructs a file system iterator with the provided rules and conversion.
	 * Performs a preorder traversal - directory before directory contents.
	 * The directories are sorted.
	 *
	 * @see  #FilesystemIterator(Map,Map,String,boolean,boolean)
	 */
	public FilesystemIterator(Map<String, FilesystemIteratorRule> rules, Map<String, FilesystemIteratorRule> prefixRules, String startPath) {
		this(rules, prefixRules, startPath, true, true);
	}

	/**
	 * Constructs a file system iterator with the provided rules and conversion.
	 *
	 * @param  rules  the rules that will be applied during iteration
	 * @param  startPath  if "", all roots will be used, otherwise starts at the provided path
	 */
	public FilesystemIterator(Map<String, FilesystemIteratorRule> rules, Map<String, FilesystemIteratorRule> prefixRules, String startPath, boolean isPreorder, boolean isSorted) {
		this.rules=rules;
		this.prefixRules=prefixRules;
		currentDirectories=null;
		currentLists=null;
		currentIndexes=null;
		filesDone=false;
		this.startPath = startPath;
		this.isPreorder = isPreorder;
		this.isSorted = isSorted;
	}

	private Stack<String> currentDirectories;
	private Stack<String[]> currentLists;
	private Stack<Integer> currentIndexes;
	private boolean filesDone=false;

	/**
	 * Gets the next file from the iterator or <code>null</code> if the iterator has completed the iteration of the file system.
	 * This method is internally synchronized and is thread-safe.
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
						(currentDirectories=new Stack<>()).push("");
						(currentLists=new Stack<>()).push(getFilesystemRoots());
					} else {
						if(isFilesystemRoot(startPath)) {
							// Starting from a root, has no parent
							(currentDirectories=new Stack<>()).push("");
							(currentLists=new Stack<>()).push(new String[] {startPath});
						} else {
							// Starting at non root will include the starting directory itself
							File startPathFile = new File(startPath);
							String parent = startPathFile.getParent();
							String name = startPathFile.getName();
							(currentDirectories=new Stack<>()).push(parent);
							(currentLists=new Stack<>()).push(new String[] {name});
						}
					}
					(currentIndexes=new Stack<>()).push(0);
				}
				String currentDirectory;
				String[] currentList=null;
				int currentIndex=-1;
				try {
					currentDirectory=currentDirectories.peek();
					currentList=currentLists.peek();
					currentIndex=currentIndexes.peek();

					// Undo the stack as far as needed
					while(currentDirectory!=null && currentIndex>=currentList.length) {
						currentDirectories.pop();
						currentLists.pop();
						currentIndexes.pop();
						String oldCurrentDirectory = currentDirectory;
						currentDirectory=currentDirectories.peek();
						currentList=currentLists.peek();
						currentIndex=currentIndexes.peek();
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
					currentIndexes.push(currentIndex+1);

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
										if(list==null) {
											list = EmptyArrays.EMPTY_STRING_ARRAY;
										} else if(isSorted && list.length>0) {
											Arrays.sort(list);
										}
									} else {
										//System.err.println("Skipping non-canonical directory listing: "+filename);
										list = EmptyArrays.EMPTY_STRING_ARRAY;
									}
								} else {
									list = EmptyArrays.EMPTY_STRING_ARRAY;
								}
								// No need to push onto the stack if the children are empty?
								if(list.length>0) {
									currentDirectories.push(filename);
									currentLists.push(list);
									currentIndexes.push(0);
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
	 * Gets the file system roots.  It will only include the root if it has
	 * at least one backup-enabled rule.  An empty rule ("") will implicitly
	 * allow all roots.
	 */
	protected String[] getFilesystemRoots() throws IOException {
		File[] fileRoots=File.listRoots();
		List<String> tempRoots=new ArrayList<>(fileRoots.length);
		for (File fileRoot : fileRoots) {
			String root = fileRoot.getPath();
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
	 * Determines if a path is a possible file system root
	 */
	protected boolean isFilesystemRoot(String filename) throws IOException {
		String[] roots=getFilesystemRoots();
		for(int c=0, len=roots.length;c<len;c++) {
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
			// TODO: If there are many more prefix rules than the length of this filename, it will at some threshold
			//       be faster to do a map lookup for each possible length of the string.
			//       Would also only need to look down to longestPrefix
			for(Map.Entry<String, FilesystemIteratorRule> entry : prefixRules.entrySet()) {
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
		for(Map.Entry<String, FilesystemIteratorRule> entry : rules.entrySet()) {
			String path = entry.getKey();
			if(path.startsWith(filenamePlusSlash)) {
				FilesystemIteratorRule rule = entry.getValue();
				if(rule.isIncluded(filenamePlusSlash)) return true;
			}
		}
		for(Map.Entry<String, FilesystemIteratorRule> entry : prefixRules.entrySet()) {
			String path = entry.getKey();
			FilesystemIteratorRule rule = entry.getValue();
			if(path.startsWith(filenamePlusSlash) && rule.isIncluded(filenamePlusSlash)) return true;
		}
		return false;
	}

	static class FilenameIterator implements Iterator<String> {

		private final FilesystemIterator filesystemIterator;

		private File next;

		FilenameIterator(FilesystemIterator filesystemIterator) throws IOException {
			this.filesystemIterator = filesystemIterator;
			next = filesystemIterator.getNextFile();
		}

		@Override
		public boolean hasNext() {
			return (next != null);
		}

		@Override
		public String next() throws NoSuchElementException {
			try {
				if(next == null) throw new NoSuchElementException();
				String retVal = next.getPath();
				next = filesystemIterator.getNextFile();
				return retVal;
			} catch(IOException err) {
				throw new UncheckedIOException(err);
			}
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

	/**
	 * Gets the sort flag for this iterator.
	 */
	public boolean isSorted() {
		return isSorted;
	}

	@Override
	public int compareTo(FilesystemIterator other) {
		return startPath.compareTo(other.startPath);
	}
}
