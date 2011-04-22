/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011  AO Industries, Inc.
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
import com.aoindustries.util.WrappedException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes to a StringBuilder then switches to a temp file when the
 * threshold is reached.
 *
 * @author  AO Industries, Inc.
 */
public class AutoTempFileWriter extends Writer {

    private static final Logger logger = Logger.getLogger(AutoTempFileWriter.class.getName());

    private final int tempFileThreshold;

    private long length;

    private StringBuilder sb;

    private File tempFile;
    private Writer fileWriter;

    public AutoTempFileWriter(int initialCapacity, int tempFileThreshold) {
        if(tempFileThreshold<=initialCapacity) throw new IllegalArgumentException("tempFileThreshold must be > initialCapacity");
        this.tempFileThreshold = tempFileThreshold;
        length = 0;
        sb = new StringBuilder(initialCapacity);
    }

    private void switchIfNeeded(long newLength) throws IOException {
        if(sb!=null && newLength>=tempFileThreshold) {
            tempFile = File.createTempFile("AutoTempFileWriter", null);
            tempFile.deleteOnExit();
            if(logger.isLoggable(Level.FINE)) logger.fine("Switching to temp file: "+tempFile);
            fileWriter = new BufferedWriter(new FileWriter(tempFile));
            fileWriter.write(sb.toString());
            sb = null;
        }
    }

    @Override
    public void write(int c) throws IOException {
        long newLength = length+1;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append((char)c);
        else fileWriter.write(c);
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
        long newLength = length+cbuf.length;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(cbuf);
        else fileWriter.write(cbuf);
        length = newLength;
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
        long newLength = length+len;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(cbuf, off, len);
        else fileWriter.write(cbuf, off, len);
        length = newLength;
    }

    @Override
    public void write(String str) throws IOException {
        long newLength = length+str.length();
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(str);
        else fileWriter.write(str);
        length = newLength;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        long newLength = length+len;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(str, off, off+len);
        else fileWriter.write(str, off, len);
        length = newLength;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq) throws IOException {
        long newLength = length+csq.length();
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(csq);
        else fileWriter.append(csq);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq, int start, int end) throws IOException {
        long newLength = length+(end-start);
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(csq, start, end);
        else fileWriter.append(csq, start, end);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(char c) throws IOException {
        long newLength = length+1;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(c);
        else fileWriter.append(c);
        length = newLength;
        return this;
    }

    @Override
    public void flush() throws IOException {
        if(fileWriter!=null) fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
        if(fileWriter!=null) {
            fileWriter.close();
            fileWriter = null;
        }
    }

    /**
     * Gets the captured body as a string.  For larger amounts of data, especially when
     * in excess of <code>tempFileThreshold</code>, it is much more efficient to call
     * the <code>writeTo</code> method.
     *
     * @see  #tempFileThreshold
     * @see  #writeTo(java.io.Writer)
     */
    @Override
    public String toString() {
        if(sb!=null) {
            return sb.toString();
        } else {
            try {
                logger.info("Creating String from temp file - benefits of AutoTempFileWriter negated.");
                if(length>Integer.MAX_VALUE) throw new RuntimeException("Buffer too large to convert to String: length="+length);
                StringBuilder toStringResult = new StringBuilder((int)length);
                flush();
                Reader in = new FileReader(tempFile);
                try {
                    char[] buff = new char[BufferManager.BUFFER_SIZE];
                    int numChars;
                    while((numChars=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) toStringResult.append(buff, 0, numChars);
                    if(toStringResult.length()!=length) throw new AssertionError("toStringResult.length()!=length: "+toStringResult.length()+"!="+length);
                } finally {
                    in.close();
                }
                return toStringResult.toString();
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }
    }

    /**
     * Writes the captured body to the provided writer.
     */
    public void writeTo(Writer out) throws IOException {
        if(sb!=null) {
            out.write(sb.toString());
        } else {
            flush();
            Reader in = new FileReader(tempFile);
            try {
                long totalRead = 0;
                char[] buff = BufferManager.getChars();
                try {
                    int numChars;
                    while((numChars=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                        out.write(buff, 0, numChars);
                        totalRead += numChars;
                    }
                    if(totalRead!=length) throw new AssertionError("totalRead!=length: "+totalRead+"!="+length);
                } finally {
                    BufferManager.release(buff);
                }
            } finally {
                in.close();
            }
        }
    }

    /**
     * Deletes the internal buffers.  This object should not be used after this call.
     */
    public void delete() throws IOException {
        sb = null;
        close();
        if(tempFile!=null) {
            if(!tempFile.delete()) throw new IOException("Unable to delete: "+tempFile);
            tempFile = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }
}
