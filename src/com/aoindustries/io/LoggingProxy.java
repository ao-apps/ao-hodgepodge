/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Listens on a socket, connects to another socket, and dumps all communication line-by-line to
 * a single log file.
 *
 * @author  AO Industries, Inc.
 */
public class LoggingProxy {

    private static final Charset CHARSET = Charset.forName("ISO-8859-1");

    private LoggingProxy() {
    }

    /**
     * Writes one line to the given file, synchronizes on the file object.
     */
    private static void log(File logFile, long connectionId, char separator, String line) {
        synchronized(logFile) {
            try {
                Writer out = new FileWriter(logFile, true);
                try {
                    out.write(Long.toString(connectionId));
                    out.write(separator);
                    out.write(' ');
                    out.write(line);
                    out.write('\n');
                } finally {
                    out.close();
                }
            } catch(IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public static void main(String[] args) {
        if(args.length==5) {
            try {
                final int listenPort = Integer.parseInt(args[1]);
                final int connectPort = Integer.parseInt(args[3]);
                long connectionId = 1;
                while(true) {
                    try {
                        InetAddress listenAddress = InetAddress.getByName(args[0]);
                        InetAddress connectAddress = InetAddress.getByName(args[2]);
                        File logFile = new File(args[4]);
                        ServerSocket ss = new ServerSocket(listenPort, 50, listenAddress);
                        try {
                            while(true) {
                                Socket socketIn = ss.accept();
                                new LoggingProxyThread(socketIn, connectionId++, connectAddress, connectPort, logFile).start();
                            }
                        } finally {
                            ss.close();
                        }
                    } catch(IOException e) {
                        e.printStackTrace(System.err);
                        try {
                            Thread.sleep(1000);
                        } catch(InterruptedException ie) {
                            ie.printStackTrace(System.err);
                        }
                    }
                }
            } catch(NumberFormatException e) {
                e.printStackTrace(System.err);
                System.exit(2);
            }
        } else {
            System.err.println("Usage: " + LoggingProxy.class.getName() + "listen_address listen_port connect_address connect_port log_file");
            System.exit(1);
        }
    }

    private static class LoggingProxyThread extends Thread {
        private final Socket socketIn;
        private final long connectionId;
        private final InetAddress connectAddress;
        private final int connectPort;
        private final File logFile;

        private LoggingProxyThread(Socket socketIn, long connectionId, InetAddress connectAddress, int connectPort, File logFile) {
            this.socketIn = socketIn;
            this.connectionId = connectionId;
            this.connectAddress = connectAddress;
            this.connectPort = connectPort;
            this.logFile = logFile;
        }

        @Override
        public void run() {
            try {
                try {
                    log(logFile, connectionId, ':', "Connection accepted from " + socketIn.getRemoteSocketAddress()+":"+socketIn.getPort());
                    log(logFile, connectionId, ':', "Connecting to " + connectAddress+":"+connectPort);
                    Socket socketOut = new Socket(connectAddress, connectPort);
                    try {
                        ReadLogWriteThread inThread = new ReadLogWriteThread(socketIn.getInputStream(), socketOut.getOutputStream(), connectionId, '<', logFile);
                        try {
                            inThread.start();
                            ReadLogWriteThread outThread = new ReadLogWriteThread(socketOut.getInputStream(), socketIn.getOutputStream(), connectionId, '>', logFile);
                            try {
                                outThread.start();
                            } finally {
                                try {
                                    inThread.join();
                                } catch(InterruptedException e) {
                                    e.printStackTrace(System.err);
                                }
                            }
                        } finally {
                            try {
                                inThread.join();
                            } catch(InterruptedException e) {
                                e.printStackTrace(System.err);
                            }
                        }
                    } finally {
                        socketOut.close();
                    }
                } finally {
                    socketIn.close();
                }
            } catch(IOException e) {
                log(logFile, connectionId, ':', "IOException: " + e.toString());
            }
        }
    }

    private static class ReadLogWriteThread extends Thread {
        private final InputStream in;
        private final OutputStream out;
        private final long connectionId;
        private final char separator;
        private final File logFile;

        ReadLogWriteThread(InputStream in, OutputStream out, long connectionId, char separator, File logFile) {
            this.in = in;
            this.out = out;
            this.connectionId = connectionId;
            this.separator = separator;
            this.logFile = logFile;
        }

        @Override
        public void run() {
            try {
                try {
                    try {
                        byte[] buff = new byte[4096];
                        int numBytes;
                        while((numBytes = in.read(buff, 0, 4096))!=-1) {
                            log(logFile, connectionId, separator, new String(buff, 0, numBytes, CHARSET));
                            out.write(buff, 0, numBytes);
                            out.flush();
                        }
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
            } catch(IOException e) {
                log(logFile, connectionId, separator, "IOException: " + e.toString());
            }
        }
    }
}
