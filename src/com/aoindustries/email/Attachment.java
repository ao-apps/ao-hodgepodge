package com.aoindustries.email;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * An <code>Attachment</code> consists of a filename and a byte[] of the attached data.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public final class Attachment implements Serializable {

    private final String filename;

    private final byte[] file;

    public Attachment(String filename, byte[] file) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, Attachment.class, "<init>(String,byte[])", null);
        try {
            this.filename = filename;
            this.file = file;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public byte[] getFile() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, Attachment.class, "getFile()", null);
        try {
            return file;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public String getFilename() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, Attachment.class, "getFilename()", null);
        try {
            return filename;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the uuencoded version of this file
     */
    public byte[] uuencode() throws IOException {
        Profiler.startProfile(Profiler.FAST, Attachment.class, "uuencode()", null);
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            UUEncoder uuencode = new UUEncoder(in, out, 640, filename);
            uuencode.run();
            return out.toByteArray();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}