package com.aoindustries.io;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * A writer that controls advanced features of
 * VT/100 terminals, while silently reverting to standard
 * behavior where the functions are not supported.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class TerminalWriter extends PrintWriter {

    private static final byte ESC=(byte)0x1b;

    private static final byte[]
        CLEAR_SCREEN=new byte[] {ESC, (byte)'[', (byte)'H', ESC, (byte)'[', (byte)'J'},
        BOLD_ON=new byte[] {ESC, (byte)'[', (byte)'1', (byte)'m'},
        ATTRIBUTES_OFF=new byte[] {ESC, (byte)'[', (byte)'m'}
    ;

    private static final boolean supported=System.getProperty("os.name").toLowerCase().indexOf("linux")>=0;
    private boolean enabled=true;

    private final OutputStream out;

    public TerminalWriter(OutputStream out) {
	super(out);
        Profiler.startProfile(Profiler.INSTANTANEOUS, TerminalWriter.class, "<init>(OutputStream)", null);
        try {
            this.out=out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public TerminalWriter(OutputStream out, boolean autoFlush) {
	super(out, autoFlush);
        Profiler.startProfile(Profiler.INSTANTANEOUS, TerminalWriter.class, "<init>(OutputStream,boolean)", null);
        try {
            this.out=out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void attributesOff() throws IOException {
        Profiler.startProfile(Profiler.IO, TerminalWriter.class, "attributesOff()", null);
        try {
            if(supported && enabled) {
                flush();
                out.write(ATTRIBUTES_OFF);
                out.flush();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void boldOn() throws IOException {
        Profiler.startProfile(Profiler.IO, TerminalWriter.class, "boldOn()", null);
        try {
            if(supported && enabled) {
                flush();
                out.write(BOLD_ON);
                out.flush();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void clearScreen() throws IOException {
        Profiler.startProfile(Profiler.IO, TerminalWriter.class, "clearScreen()", null);
        try {
            if(supported && enabled) {
                flush();
                out.write(CLEAR_SCREEN);
                out.flush();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public boolean isEnabled() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, TerminalWriter.class, "isEnabled()", null);
        try {
            return enabled;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static boolean isSupported() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, TerminalWriter.class, "isSupported()", null);
        try {
            return supported;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void setEnabled(boolean enabled) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, TerminalWriter.class, "setEnabled(boolean)", null);
        try {
            this.enabled=enabled;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
}