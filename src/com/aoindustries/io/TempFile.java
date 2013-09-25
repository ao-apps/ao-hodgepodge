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

/**
 * Holds a reference to a temp file.  Automatically deletes the file with
 * a finalizer, on JVM shutdown, or when <code>delete()</code> is explicitely
 * called.
 *
 * @author  AO Industries, Inc.
 */
public class TempFile {
	
	private volatile File tempFile;
	private final String path;
	
	public TempFile(String prefix) throws IOException {
		this(prefix, null, null);
	}

	public TempFile(String prefix, String suffix) throws IOException {
		this(prefix, suffix, null);
	}

	public TempFile(String prefix, String suffix, File directory) throws IOException {
		tempFile = File.createTempFile(prefix, suffix, directory);
		tempFile.deleteOnExit();
		path = tempFile.getPath();
	}

	@Override
	public String toString() {
		return path;
	}

	/**
     * Deletes the underlying temp file immediately.
	 * Subsequent calls will not delete the temp file, even if another file has the same path.
	 * If already deleted, has no effect.
     */
    public void delete() throws IOException {
		File f = tempFile;
		if(f!=null) {
            FileUtils.delete(f);
            tempFile = null;
        }
    }

	/*
	 * Deletes the underlying temp file on garbage collection.
	 */
    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }
	
	/**
	 * Gets the temp file.
	 *
	 * @exception  IllegalStateException  if already deleted
	 */
	public File getFile() throws IllegalStateException {
		File f = tempFile;
		if(f==null) throw new IllegalStateException();
		return f;
	}
}