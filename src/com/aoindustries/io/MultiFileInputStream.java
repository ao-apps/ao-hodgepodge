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

/**
 * A <code>MultiFileInputStream</code> reads multiple <code>File</code>s as
 * if they were one contiguous file.
 *
 * @author  AO Industries, Inc.
 */
public class MultiFileInputStream extends InputStream {

    private final File[] files;
    private int nextFile=0;
    private FileInputStream in=null;

    public MultiFileInputStream(File[] files) {
        this.files=files;
    }

    synchronized public int available() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) return 0;
            in=new FileInputStream(files[nextFile++]);
        }
        return in.available();
    }

    synchronized public void close() throws IOException {
        if(nextFile<files.length) nextFile=files.length;
        FileInputStream tempIn=in;
        if(tempIn!=null) {
            in=null;
            tempIn.close();
        }
    }

    synchronized public void mark(int readlimit) {
        try {
            if(in==null) {
                if(nextFile>=files.length) {
                    super.mark(readlimit);
                    return;
                }
                in=new FileInputStream(files[nextFile++]);
            }
            in.mark(readlimit);
        } catch(IOException err) {
            super.mark(readlimit);
        }
    }

    synchronized public boolean markSupported() {
        try {
            if(in==null) {
                if(nextFile>=files.length) return false;
                in=new FileInputStream(files[nextFile++]);
            }
            return in.markSupported();
        } catch(IOException err) {
            return false;
        }
    }

    synchronized public int read() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) return -1;
            in=new FileInputStream(files[nextFile++]);
        }
        int value=-1;
        while(value==-1) {
            value=in.read();
            if(value==-1) {
                FileInputStream tempIn=in;
                in=null;
                tempIn.close();
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
        }
        return value;
    }
    
    synchronized public int read(byte[] buff) throws IOException {
        if(buff.length>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            int count=-1;
            while(count==-1) {
                count=in.read(buff);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }
    
    synchronized public int read(byte[] buff, int off, int len) throws IOException {
        if(len>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            int count=-1;
            while(count==-1) {
                count=in.read(buff, off, len);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }

    synchronized public void reset() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) {
                super.reset();
                return;
            }
            in=new FileInputStream(files[nextFile++]);
        }
        in.reset();
    }

    synchronized public long skip(long n) throws IOException {
        if(n>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            long count=-1;
            while(count==-1) {
                count=in.skip(n);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }
    
    /**
     * Gets the total number of bytes in all the files that will be read.
     */
    public long length() {
        long total=0;
        for(int c=0;c<files.length;c++) total+=files[c].length();
        return total;
    }
}
