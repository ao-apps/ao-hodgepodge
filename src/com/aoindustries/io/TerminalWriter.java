package com.aoindustries.io;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        this.out=out;
    }

    public TerminalWriter(OutputStream out, boolean autoFlush) {
	super(out, autoFlush);
        this.out=out;
    }

    public void attributesOff() throws IOException {
        if(supported && enabled) {
            flush();
            out.write(ATTRIBUTES_OFF);
            out.flush();
        }
    }

    public void boldOn() throws IOException {
        if(supported && enabled) {
            flush();
            out.write(BOLD_ON);
            out.flush();
        }
    }

    public void clearScreen() throws IOException {
        if(supported && enabled) {
            flush();
            out.write(CLEAR_SCREEN);
            out.flush();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static boolean isSupported() {
        return supported;
    }

    public void setEnabled(boolean enabled) {
        this.enabled=enabled;
    }
}