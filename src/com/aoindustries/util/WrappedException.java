package com.aoindustries.util;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedException extends RuntimeException {

    private final Object[] extraInfo;

    public WrappedException() {
        super();
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>()", null);
        try {
            this.extraInfo=null;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public WrappedException(String message) {
        super(message);
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>(String)", null);
        try {
            this.extraInfo=null;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public WrappedException(Throwable cause) {
        super(cause);
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>(Throwable)", null);
        try {
            this.extraInfo=null;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public WrappedException(Throwable cause, Object[] extraInfo) {
        super(cause);
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>(Throwable,Object[])", null);
        try {
            this.extraInfo=extraInfo;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public WrappedException(String message, Throwable cause) {
        super(message, cause);
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>(String,Throwable)", null);
        try {
            this.extraInfo=null;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public WrappedException(String message, Throwable cause, Object[] extraInfo) {
        super(message, cause);
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "<init>(String,Throwable,Object[])", null);
        try {
            this.extraInfo=extraInfo;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public Object[] getExtraInfo() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, WrappedException.class, "getExtraInfo()", null);
        try {
            return extraInfo;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
}