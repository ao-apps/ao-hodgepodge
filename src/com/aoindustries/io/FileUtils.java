/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012  AO Industries, Inc.
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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;

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
        final int contentLen = contents.length;
        {
            final long length = file.length();
            if(length>Integer.MAX_VALUE) return false;
            // Be careful about file.length() returning zero on error - always read file for zero case - no shortcut.
            if(contentLen>0 && length!=contentLen) return false;
        }
        final InputStream in = new FileInputStream(file);
        try {
            final byte[] buff = BufferManager.getBytes();
            try {
                int readPos = 0;
                while(readPos<contentLen) {
                    int bytesRemaining = contentLen - readPos;
                    int bytesRead = in.read(buff, 0, bytesRemaining > BufferManager.BUFFER_SIZE ? BufferManager.BUFFER_SIZE : bytesRemaining);
                    if(bytesRead==-1) return false; // End of file
                    int i=0;
                    while(i<bytesRead) {
                        if(buff[i++]!=contents[readPos++]) return false;
                    }
                }
                // Next read must be end of file - otherwise file content longer than contents.
                if(in.read()!=-1) return false;
            } finally {
                BufferManager.release(buff);
            }
        } finally {
            in.close();
        }
        /*
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
        }*/
        return true;
    }

    /**
     * Compares the contents of two files, not supporting directories.
     */
    public static boolean contentEquals(File file1, File file2) throws IOException {
        long len1 = file1.length();
        long len2 = file2.length();
        // Read file when zero length
        if(len1!=0 && len2!=0 && len1!=len2) return false;
        final InputStream in1 = new BufferedInputStream(new FileInputStream(file1));
        try {
            final InputStream in2 = new BufferedInputStream(new FileInputStream(file2));
            try {
                while(true) {
                    int b1 = in1.read();
                    int b2 = in2.read();
                    if(b1!=b2) return false;
                    if(b1==-1) break;
                }
                return true;
            } finally {
                in2.close();
            }
        } finally {
            in1.close();
        }
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

    /**
     * Copies a stream to a newly created temporary file.
     */
    public static File copyToTempFile(InputStream in, String prefix, String suffix) throws IOException {
        return copyToTempFile(in, prefix, suffix, null);
    }

    /**
     * Copies a stream to a newly created temporary file.
     */
    public static File copyToTempFile(InputStream in, String prefix, String suffix, File directory) throws IOException {
        File tmpFile = File.createTempFile("cache_", null);
        boolean successful = false;
        try {
            OutputStream out = new FileOutputStream(tmpFile);
            try {
                IoUtils.copy(in, out);
            } finally {
                out.close();
            }
            successful = true;
            return tmpFile;
        } finally {
            if(!successful) tmpFile.delete();
        }
    }

    /**
     * Makes a directory.  The directory must not already exist.
     *
     * @return  The directory itself.
     *
     * @exception  IOException  if mkdir fails.
     */
    public static File mkdir(File directory) throws IOException {
        if(!directory.mkdir()) throw new IOException("Unable to create directory: "+directory.getPath());
        return directory;
    }

    /**
     * Ensures that the file is a directory.
     *
     * @return  The directory itself.
     *
     * @exception  IOException  if not a directory
     */
    public static File checkIsDirectory(File directory) throws IOException {
        if(!directory.isDirectory()) throw new IOException("Not a directory: " + directory.getPath());
        return directory;
    }

    /**
     * Copies one file over another, possibly creating if needed.
     *
     * @return  the number of bytes copied
     */
    public static long copy(File from, File to) throws IOException {
        InputStream in = new FileInputStream(from);
        try {
            long bytes;
            long modified = from.lastModified();
            OutputStream out = new FileOutputStream(to);
            try {
                bytes = IoUtils.copy(in, out);
            } finally {
                out.close();
            }
            if(modified!=0) to.setLastModified(modified);
            return bytes;
        } finally {
            in.close();
        }
    }

    /**
     * Recursively copies source to destination.  Destination must not exist.
     */
    public static void copyRecursive(File from, File to) throws IOException {
        copyRecursive(from, to, null);
    }

    /**
     * Recursively copies source to destination.  Destination must not exist.
     */
    public static void copyRecursive(File from, File to, FileFilter fileFilter) throws IOException {
        if(fileFilter==null || fileFilter.accept(from)) {
            if(from.isDirectory()) {
                if(to.exists()) throw new IOException("Directory exists: "+to);
                long modified = from.lastModified();
                mkdir(to);
                String[] list = from.list();
                if(list!=null) {
                    for(String child : list) {
                        copyRecursive(
                            new File(from, child),
                            new File(to, child),
                            fileFilter
                        );
                    }
                }
                if(modified!=0) to.setLastModified(modified);
            } else if(from.isFile()) {
                if(to.exists()) throw new IOException("File exists: "+to);
                copy(from, to);
            } else {
                throw new IOException("Neither directory not file: "+to);
            }
        }
    }

    /**
     * Gets a File for a URL, retrieving the contents into a temporary file if needed.
     * Assumes URL is UTF-8 encoded.
     *
     * @param  deleteOnExit  when <code>true</code>, any newly created temp file will be flagged for delete of exit
     */
    public static File getFile(URL url, boolean deleteOnExit) throws IOException {
        if("file".equalsIgnoreCase(url.getProtocol())) {
            String path = url.getFile();
            if(path.length()>0) {
                File file = new File(URLDecoder.decode(path, "UTF-8").replace('/', File.separatorChar));
                if(file.exists() && file.isFile()) return file;
            }
        }
        File file = File.createTempFile("url", null);
        boolean successful = false;
        try {
            if(deleteOnExit) file.deleteOnExit();
            OutputStream out = new FileOutputStream(file);
            try {
                InputStream in = url.openStream();
                try {
                    IoUtils.copy(in, out);
                } finally {
                    in.close();
                }
            } finally {
                out.close();
            }
            successful = true;
            return file;
        } finally {
            if(!successful) file.delete();
        }
    }
}
