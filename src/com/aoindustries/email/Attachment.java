package com.aoindustries.email;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        this.filename = filename;
        this.file = file;
    }

    public byte[] getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Gets the uuencoded version of this file
     */
    public byte[] uuencode() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UUEncoder uuencode = new UUEncoder(in, out, 640, filename);
        uuencode.run();
        return out.toByteArray();
    }
}