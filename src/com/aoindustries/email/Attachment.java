/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * An <code>Attachment</code> consists of a filename and a byte[] of the attached data.
 *
 * @author  AO Industries, Inc.
 */
public final class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

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