package com.aoindustries.net;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * Overrides the standard FtpClient to not close the FTP connection when closing the data input
 * stream during a GET operation.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FtpClient extends sun.net.ftp.FtpClient {

    /**
     * FtpClient constructor comment.
     */
    public FtpClient() {
        super();
        Profiler.startProfile(Profiler.INSTANTANEOUS, FtpClient.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * FtpClient constructor comment.
     * @param arg1 java.lang.String
     * @exception java.io.IOException The exception description.
     */
    public FtpClient(String arg1) throws java.io.IOException {
        super(arg1);
        Profiler.startProfile(Profiler.INSTANTANEOUS, FtpClient.class, "<init>(String)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * FtpClient constructor comment.
     * @param arg1 java.lang.String
     * @param arg2 int
     * @exception java.io.IOException The exception description.
     */
    public FtpClient(String arg1, int arg2) throws java.io.IOException {
        super(arg1, arg2);
        Profiler.startProfile(Profiler.INSTANTANEOUS, FtpClient.class, "<init>(String,int)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * Closes the FTP server connection.  This should be used instead of closeServer.
     */
    public void close() throws IOException {
        Profiler.startProfile(Profiler.IO, FtpClient.class, "close()", null);
        try {
            super.closeServer();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Overridden to do nothing so that closing the data input stream during a get
     * operation leaves the connection open.
     */
    public void closeServer() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FtpClient.class, "closeServer()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }
}