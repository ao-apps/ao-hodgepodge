/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.io.unix.linux;

import com.aoindustries.io.unix.*;
import java.io.*;

/**
 * Access and modify all Linux specific file attributes, in addition to standard
 * Unix attributes.
 *
 * @author  AO Industries, Inc.
 */
public class LinuxFile extends UnixFile {

    /**
     * When deleted, its blocks are zeroed and written back to the disk.
     */
    public static final int SECURE_DELETION=1;

    /**
     * When deleted, its contents are saved.  This allows the user to ask for its
     * undeletion.
     */
    public static final int UNDELETABLE=2;

    /**
     * Automatically compress the file on disk.
     */
    public static final int COMPRESSED=4;

    /**
     * Changes are written synchronously on the disk.
     */
    public static final int SYNCHRONOUS_UPDATES=8;

    /**
     * File cannot be modified, deleted, renamed, or linked to.
     */
    public static final int IMMUTABLE=16;

    /**
     * File can only be opened in append mode for writing.
     */
    public static final int APPEND_ONLY=32;

    /**
     * Do not back up file.
     */
    public static final int NO_DUMP=64;

    /**
     * Do not update atime when accessed.
     */
    public static final int DO_NOT_UPDATE_ATIME=128;

    private int attributes;

    private static Process getattrProcess;
    private static PrintWriter getattrOut;
    private static BufferedReader getattrIn;

    public LinuxFile(String filename) {
	super(filename);
    }

    final public int getAttributes() throws IOException {
        //reloadIfNeeded();
        if(!getStat().exists()) throw new FileNotFoundException();
        return attributes;
    }

    /**
     * Only one process is created to handle all of the get attribute needs of this virtual machine.
     * Requests are synchronized in the Java and then handled by this process.  This increases
     * the performance of repeated stats by avoiding the creation of another Unix process.
     */
    public LinuxFile reload() throws IOException {
        // Reload the Linux attributes first
        synchronized(LinuxFile.class) {
            if(getattrProcess==null || getattrOut==null || getattrIn==null) {
                // Create the getattr process
                String[] cmd={"/usr/aoserv/bin/getattrs"};
                getattrProcess=Runtime.getRuntime().exec(cmd);
                getattrOut=new PrintWriter(getattrProcess.getOutputStream());
                getattrIn=new BufferedReader(new InputStreamReader(getattrProcess.getInputStream()));
            }

            getattrOut.println(path);
            getattrOut.flush();

            attributes=Integer.parseInt(getattrIn.readLine());
        }

        // Then reload the Unix attributes
        return this;
    }
}