/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a set of temporary files.  When a context is completed, all
 * temporary files created within that context may be deleted by calling delete
 * on its list.
 * <p>
 * For example, a context might be the lifecycle of serving one HTTP request.
 * Once the request has been completed, any temporary files created should be
 * cleaned-up immediately instead of waiting an indeterminate amount of time
 * for 
 * </p>
 * 
 * @see  TempFile
 *
 * @author  AO Industries, Inc.
 */
public class TempFileList {

	private final List<WeakReference<TempFile>> tempFiles = new ArrayList<WeakReference<TempFile>>();

	private final String prefix;
	private final String suffix;
	private final File directory;

	public TempFileList(String prefix) throws IOException {
		this(prefix, null, null);
	}

	public TempFileList(String prefix, String suffix) throws IOException {
		this(prefix, suffix, null);
	}

	public TempFileList(String prefix, String suffix, File directory) throws IOException {
		this.prefix = prefix;
		this.suffix = suffix;
		this.directory = directory;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(
			directory==null ? 0 : (directory.getPath().length() + 1)
			+ prefix.length()
			+ 1
			+ suffix.length()
		);
		if(directory!=null) {
			sb.append(directory.getPath()).append(File.separatorChar);
		}
		sb.append(prefix).append('*').append(suffix);
		return sb.toString();
	}

	/**
	 * Creates a new temp file while adding it to the list of files that will
	 * be explicitely deleted when this list is deleted.
	 */
	public TempFile createTempFile() throws IOException {
		TempFile tempFile = new TempFile(prefix, suffix, directory);
		tempFiles.add(new WeakReference<TempFile>(tempFile));
		return tempFile;
	}

	/**
     * Deletes all of the underlying temp files immediately.
	 *
	 * This list may still be used for additional temp files.
	 *
	 * @see  TempFile#delete()
     */
    public void delete() throws IOException {
		synchronized(tempFiles) {
			for(WeakReference<TempFile> tempFileRef : tempFiles) {
				TempFile tempFile = tempFileRef.get();
				if(tempFile!=null) tempFile.delete();
			}
			tempFiles.clear();
		}
    }
}