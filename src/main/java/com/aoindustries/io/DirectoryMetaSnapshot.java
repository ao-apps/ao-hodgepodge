/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Recursively (but not following symbolic links), gets a snapshot of the meta
 * data for a directory.  To see if any file has been modified, this meta data
 * may be compared to a snapshot of the same directory at a different time.  To
 * perform a quick comparison of two directory trees, a snapshot may be compared
 * to a snapshot of a different directory.
 *
 * Only watches the lengths and modified times for filesystem objects that Java
 * considers a file.
 * 
 * @see  File#isFile()  for what Java considers a file
 *
 * @author  AO Industries, Inc.
 */
final public class DirectoryMetaSnapshot {

	final public static class FileMetaSnapshot {

		private final long lastModified;
		private final long length;

		private FileMetaSnapshot(long lastModified, long length) {
			this.lastModified = lastModified;
			this.length = length;
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof FileMetaSnapshot)) return false;
			final FileMetaSnapshot other = (FileMetaSnapshot) obj;
			return
				lastModified == other.lastModified
				&& length == other.length
			;
		}

		@Override
		public int hashCode() {
			return
				(int)(lastModified >>> 32)
				^ (int)lastModified
				^ (int)(length >>> 32)
				^ (int)length
			;
		}

		public long getLastModified() {
			return lastModified;
		}

		public long getLength() {
			return length;
		}
	}

	final private SortedMap<String,FileMetaSnapshot> files;

	public DirectoryMetaSnapshot(String startPath) throws IOException {
		Map<String,FilesystemIteratorRule> noPrefixRules = Collections.emptyMap();
		FilesystemIterator iter = new FilesystemIterator(
			Collections.singletonMap(startPath, FilesystemIteratorRule.OK),
			noPrefixRules,
			startPath
		);
		final String expectedStart = startPath + File.separatorChar;
		SortedMap<String,FileMetaSnapshot> newFiles = new TreeMap<String,FileMetaSnapshot>();
		File file;
		while((file=iter.getNextFile())!=null) {
			if(file.isFile()) {
				String path = file.getPath();
				if(!path.startsWith(expectedStart)) throw new AssertionError("Unexpected start of path: " + path);
				newFiles.put(
					path.substring(expectedStart.length()),
					new FileMetaSnapshot(file.lastModified(), file.length())
				);
			}
		}
		this.files = Collections.unmodifiableSortedMap(newFiles);
	}

	/**
	 * Checks that all meta data is equal in the two directory tree snapshots.
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DirectoryMetaSnapshot)) return false;
		final DirectoryMetaSnapshot other = (DirectoryMetaSnapshot) obj;
		return files.equals(other.files);
	}

	@Override
	public int hashCode() {
		return files.hashCode();
	}

	public SortedMap<String, FileMetaSnapshot> getFiles() {
		return files;
	}
}
