/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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

import com.aoindustries.util.BufferManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File utilities.
 */
final public class FileUtils {

    /**
     * Make no instances.
     */
    private FileUtils() {}

    /**
     * Recursively deletes the provided file, being careful to not follow symbolic links (but there are still unavoidable race conditions).
     */
    public static void delete(File file) throws IOException {
        if(
            file.isDirectory()
            // Don't recursively travel across symbolic links (still a race condition here, though)
            && file.getCanonicalPath().equals(file.getAbsolutePath())
        ) {
            File afile[] = file.listFiles();
            if(afile != null) for(File f : afile) delete(f);
        }
        if(!file.delete()) throw new IOException("Unable to delete: " + file);
    }

    /**
     * Compares the contents of a file to the provided array.
     */
    public static boolean contentEquals(File file, byte[] contents) throws IOException {
        long length = file.length();
        if(length!=contents.length) return false;
        int buffSize = length<BufferManager.BUFFER_SIZE ? (int)length : BufferManager.BUFFER_SIZE;
        if(buffSize < 64) buffSize=64;
        InputStream in = new BufferedInputStream(new FileInputStream(file), buffSize);
        try {
            for(int c=0; c<contents.length; c++) {
                int b1 = in.read();
                int b2 = contents[c] & 0xff;
                if(b1!=b2) return false;
            }
        } finally {
            in.close();
        }
        return true;
    }

    /**
     * Creates a temporary directory.
     */
    public static File createTempDirectory(String prefix, String suffix) throws IOException {
        return createTempDirectory(prefix, suffix, null);
    }

    /**
     * Creates a temporary directory.
     */
    public static File createTempDirectory(String prefix, String suffix, File directory) throws IOException {
        while(true) {
            File tempFile = File.createTempFile(prefix, suffix, directory);
            if(!tempFile.delete()) throw new IOException(tempFile.getPath());
            // Check result of mkdir to catch race condition
            if(tempFile.mkdir()) return tempFile;
        }
    }
}
