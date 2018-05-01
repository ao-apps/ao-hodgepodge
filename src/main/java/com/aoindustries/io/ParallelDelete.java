/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2018  AO Industries, Inc.
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
package com.aoindustries.io;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * Our backup directories contain parallel directories with many hard links.
 * The performance of deleting more than one of the directories can be improved
 * by deleting from them in parallel.
 * </p>
 * <p>
 * Also performs the task with three threads:
 * <pre>
 *     Iterate filesystem -> Delete entries -> Verbose Output
 *     (Calling Thread)      (New Thread)      (New Thread)
 * </pre>
 * </p>
 * <p>
 * Verifying this is, in fact, true.  This is measured with a copy of the
 * backups from one of our managed servers.  The system RAM was limited to 128
 * MB to better simulate backup server hardware.  ext3 benchmarks on Maxtor 250
 * GB 7200 RPM SATA.  reiserfs benchmarks on WD 80 GB 7200 IDE.
 * <pre>
 *                       +---------------------+---------------------+
 *                       |         ext3        |      reiserfs       |
 * +-----------+---------+----------+----------+----------+----------+
 * | # Deleted |         | parallel |  rm -rf  | parallel |  rm -rf  |
 * +-----------+---------+----------+----------+----------+----------+
 * |      1/13 |         |     TODO |     TODO |     TODO |     TODO |
 * |      2/13 |         |     TODO |     TODO |     TODO |     TODO |
 * |      3/13 |         |     TODO |     TODO |     TODO |     TODO |
 * |      4/13 |         |     TODO |     TODO |     TODO |     TODO |
 * |      8/13 |         |     TODO |     TODO |     TODO |     TODO |
 * +-----------+---------+----------+----------+----------+----------+
 * |           | User    |    61.99 |     2.61 |    63.29 |     3.00 |
 * |     13/13 | System  |    89.90 |    48.01 |   180.69 |   113.26 |
 * |           | Elapsed | 10:38:53 | 10:23.79 |  8:26.71 | 33:13.52 |
 * |           | % CPU   |      23% |       8% |      48% |       5% |
 * +-----------+---------+----------+----------+----------+----------+
 * </pre>
 * </p>
 * TODO: Once benchmarks finished for other # Deleted, adjust threshold between
 *       rm and parallel in FailoverFileReplicationManager
 * 
 * TODO: Should it use a provided ExecutorService instead of making own Threads?
 * 
 * TODO: Concurrent deletes would be possible.  Is there any advantage?
 *
 * @author  AO Industries, Inc.
 */
public class ParallelDelete {

	/**
	 * The size of the delete queue.
	 */
	private static final int DELETE_QUEUE_SIZE = 5000;

	/**
	 * The size of the verbose output queue.
	 */
	private static final int VERBOSE_QUEUE_SIZE = 1000;

	/**
	 * Make no instances.
	 */
	private ParallelDelete() {}

	/**
	 * Deletes multiple directories in parallel (but not concurrently).
	 */
	public static void main(String[] args) {
		if(args.length==0) {
			System.err.println("Usage: "+ParallelDelete.class.getName()+" [-n] [-v] [--] path {path}");
			System.err.println("\t-n\tPerform dry run, do not modify the filesystem");
			System.err.println("\t-v\tWrite the full path to standard error as each file is removed");
			System.err.println("\t--\tEnd options, all additional arguments will be interpreted as paths");

			System.exit(1);
		} else {
			List<File> directories = new ArrayList<File>(args.length);
			PrintStream verboseOutput = null;
			boolean dryRun = false;
			boolean optionsEnded = false;
			for(String arg : args) {
				if(!optionsEnded && arg.equals("-v")) verboseOutput = System.err;
				else if(!optionsEnded && arg.equals("-n")) dryRun = true;
				else if(!optionsEnded && arg.equals("--")) optionsEnded = true;
				else directories.add(new File(arg));
			}
			try {
				parallelDelete(directories, verboseOutput, dryRun);
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
	public static void parallelDelete(List<File> directories, final PrintStream verboseOutput, final boolean dryRun) throws IOException {
		final int numDirectories = directories.size();

		// The set of next files is kept in key order so that it can scale with O(n*log(n)) for larger numbers of directories
		// as opposed to O(n^2) for a list.  This is similar to the fix for AWStats logresolvemerge provided by Dan Armstrong
		// a couple of years ago.
		final Map<String,List<FilesystemIterator>> nextFiles = new TreeMap<String,List<FilesystemIterator>>(
			new Comparator<String>() {
				@Override
				public int compare(String S1, String S2) {
					// Make sure directories are sorted after their directory contents
					int diff = S1.compareTo(S2);
					if(diff==0) return 0;
					if(S2.startsWith(S1)) return 1;
					if(S1.startsWith(S2)) return -1;
					return diff;
				}
			}
		);
		{
			final Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
			for(File directory : directories) {
				if(!directory.exists()) throw new IOException("Directory not found: "+directory.getPath());
				if(!directory.isDirectory()) throw new IOException("Not a directory: "+directory.getPath());
				String path = directory.getCanonicalPath();
				FilesystemIterator iterator = new FilesystemIterator(
					Collections.singletonMap(path, FilesystemIteratorRule.OK),
					prefixRules,
					path,
					false,
					true
				);
				File nextFile = iterator.getNextFile();
				if(nextFile!=null) {
					String relPath = getRelativePath(nextFile, iterator);
					List<FilesystemIterator> list = nextFiles.get(relPath);
					if(list==null) nextFiles.put(relPath, list = new ArrayList<FilesystemIterator>(numDirectories));
					list.add(iterator);
				}
			}
		}

		final BlockingQueue<File> verboseQueue;
		final boolean[] verboseThreadRun;
		Thread verboseThread;
		if(verboseOutput==null) {
			verboseQueue = null;
			verboseThreadRun = null;
			verboseThread = null;
		} else {
			verboseQueue = new ArrayBlockingQueue<File>(VERBOSE_QUEUE_SIZE);
			verboseThreadRun = new boolean[] {true};
			verboseThread = new Thread("ParallelDelete - Verbose Thread") {
				@Override
				public void run() {
					while(true) {
						synchronized(verboseThreadRun) {
							if(!verboseThreadRun[0] && verboseQueue.isEmpty()) break;
						}
						try {
							verboseOutput.println(verboseQueue.take().getPath());
							if(verboseQueue.isEmpty()) verboseOutput.flush();
						} catch(InterruptedException err) {
							// Normal during thread shutdown
						}
					}
				}
			};

			verboseThread.start();
		}
		try {
			final BlockingQueue<File> deleteQueue = new ArrayBlockingQueue<File>(DELETE_QUEUE_SIZE);
			final boolean[] deleteThreadRun = new boolean[] {true};
			final IOException[] deleteException = new IOException[1];
			Thread deleteThread = new Thread("ParallelDelete - Delete Thread") {
				@Override
				public void run() {
					while(true) {
						synchronized(deleteThreadRun) {
							if(!deleteThreadRun[0] && deleteQueue.isEmpty()) break;
						}
						try {
							File deleteme = deleteQueue.take();
							boolean doDelete;
							synchronized(deleteException) {
								doDelete = deleteException[0]==null;
							}
							if(doDelete) {
								try {
									if(verboseQueue!=null) {
										boolean done = false;
										while(!done) {
											try {
												verboseQueue.put(deleteme);
												done = true;
											} catch(InterruptedException err) {
												// Normal during thread shutdown
											}
										}
									}
									if(!dryRun) FileUtils.delete(deleteme);
								} catch(IOException err) {
									synchronized(deleteException) {
										deleteException[0] = err;
									}
								}
							}
						} catch(InterruptedException err) {
							// Normal during thread shutdown
						}
					}
				}
			};
			deleteThread.start();
			try {
				// Main loop, continue until nextFiles is empty
				final StringBuilder SB = new StringBuilder();
				while(true) {
					synchronized(deleteException) {
						if(deleteException[0]!=null) break;
					}
					Iterator<String> iter = nextFiles.keySet().iterator();
					if(!iter.hasNext()) break;
					String relPath = iter.next();
					for(FilesystemIterator iterator : nextFiles.remove(relPath)) {
						SB.setLength(0);
						SB.append(iterator.getStartPath());
						SB.append(relPath);
						String fullPath = SB.toString();
						try {
							deleteQueue.put(new File(fullPath));
						} catch(InterruptedException err) {
							IOException ioErr = new InterruptedIOException();
							ioErr.initCause(err);
							throw ioErr;
						}
						// Get the next file
						File nextFile = iterator.getNextFile();
						if(nextFile!=null) {
							String newRelPath = getRelativePath(nextFile, iterator);
							List<FilesystemIterator> list = nextFiles.get(newRelPath);
							if(list==null) nextFiles.put(newRelPath, list = new ArrayList<FilesystemIterator>(numDirectories));
							list.add(iterator);
						}
					}
				}
			} finally {
				// Wait for delete queue to be empty
				synchronized(deleteThreadRun) {
					deleteThreadRun[0] = false;
				}
				deleteThread.interrupt();
				try {
					deleteThread.join();
				} catch(InterruptedException err) {
					IOException ioErr = new InterruptedIOException();
					ioErr.initCause(err);
					throw ioErr;
				}
				// Throw any exception that caused this to stop
				synchronized(deleteException) {
					if(deleteException[0]!=null) throw deleteException[0];
				}
			}
		} finally {
			// Wait for verbose queue to be empty
			if(verboseThread!=null) {
				synchronized(verboseThreadRun) {
					verboseThreadRun[0] = false;
				}
				verboseThread.interrupt();
				try {
					verboseThread.join();
				} catch(InterruptedException err) {
					IOException ioErr = new InterruptedIOException();
					ioErr.initCause(err);
					throw ioErr;
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
