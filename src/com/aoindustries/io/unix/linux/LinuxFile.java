package com.aoindustries.io.unix.linux;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.unix.*;
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * Access and modify all Linux specific file attributes, in addition to standard
 * Unix attributes.
 *
 * @version  1.0
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, LinuxFile.class, "<init>(String)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    final public int getAttributes() throws IOException {
        Profiler.startProfile(Profiler.FAST, LinuxFile.class, "getAttributes()", null);
        try {
            //reloadIfNeeded();
            if(!getStat().exists()) throw new FileNotFoundException();
            return attributes;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Only one process is created to handle all of the get attribute needs of this virtual machine.
     * Requests are synchronized in the Java and then handled by this process.  This increases
     * the performance of repeated stats by avoiding the creation of another Unix process.
     */
    public LinuxFile reload() throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxFile.class, "reload()", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}