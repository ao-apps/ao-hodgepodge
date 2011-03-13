/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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

import java.io.*;
import java.util.*;

/**
 * A <code>MultiFileOutputStream</code> writes to multiple <code>File</code>s as
 * if they were one contiguous file.
 *
 * @author  AO Industries, Inc.
 */
public class MultiFileOutputStream extends OutputStream {

    public static final long DEFAULT_FILE_SIZE=(long)1024*1024*1024;

    private final File parent;
    private final String prefix;
    private final String suffix;
    private final long fileSize;

    private List<File> files=new ArrayList<File>();
    private FileOutputStream out=null;
    private long bytesOut=0;

    public MultiFileOutputStream(File parent, String prefix, String suffix) {
        this(parent, prefix, suffix, DEFAULT_FILE_SIZE);
    }

    public MultiFileOutputStream(File parent, String prefix, String suffix, long fileSize) {
        this.parent=parent;
        this.prefix=prefix;
        this.suffix=suffix;
        this.fileSize=fileSize;
    }

    synchronized public void close() throws IOException {
        FileOutputStream tempOut=out;
        if(tempOut!=null) {
            out=null;
            tempOut.flush();
            tempOut.close();
        }
    }
    
    synchronized public void flush() throws IOException {
        if(out!=null) out.flush();
    }
    
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
    
    synchronized public void write(byte[] b, int off, int len) throws IOException {
        while(off<len) {
            if(out==null) makeNewFile();
            int blockLen=len;
            long newBytesOut=bytesOut+blockLen;
            if(newBytesOut>fileSize) {
                blockLen=(int)(fileSize-newBytesOut);
            }
            out.write(b, off, blockLen);
            off+=blockLen;
            len-=blockLen;
            bytesOut+=blockLen;
            if(bytesOut>=fileSize) {
                FileOutputStream tempOut=out;
                out=null;
                tempOut.flush();
                tempOut.close();
            }
        }
    }

    synchronized public void write(int b) throws IOException {
        out.write(b);
        bytesOut+=1;
        if(bytesOut>=fileSize) {
            FileOutputStream tempOut=out;
            out=null;
            tempOut.flush();
            tempOut.close();
        }
    }

    /**
     * All accesses are already synchronized.
     */
    private void makeNewFile() throws IOException {
        String filename=prefix+(files.size()+1)+suffix;
        File file=new File(parent, filename);
        out=new FileOutputStream(file);
        bytesOut=0;
        files.add(file);
    }
    
    public File[] getFiles() {
        File[] FA=new File[files.size()];
        files.toArray(FA);
        return FA;
    }
}
