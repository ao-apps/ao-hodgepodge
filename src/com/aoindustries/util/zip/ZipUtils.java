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
package com.aoindustries.util.zip;

import com.aoindustries.io.IoUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP file utilities.
 */
public class ZipUtils {

    /**
     * Gets the time for a ZipEntry, converting from GMT as stored in the ZIP
     * entry to make times correct between time zones.
     *
     * @see #setZipEntryTime(ZipEntry,long)
     */
    public static long getZipEntryTime(ZipEntry entry) {
        long time = entry.getTime();
        return time + TimeZone.getDefault().getOffset(time);
    }

    /**
     * Sets the time for a ZipEntry, converting to GMT while storing to the ZIP
     * entry to make times correct between time zones.  The actual time stored
     * may be rounded to the nearest two-second interval.
     *
     * @see #getZipEntryTime(ZipEntry)
     */
    public static void setZipEntryTime(ZipEntry entry, long time) {
        entry.setTime(time - TimeZone.getDefault().getOffset(time));
    }

    /**
     * Recursively packages a directory into a file.
     */
    public static void createZipFile(File sourceDirectory, File zipFile) throws IOException {
        OutputStream out = new FileOutputStream(zipFile);
        try {
            createZipFile(sourceDirectory, out);
        } finally {
            out.close();
        }
    }

    /**
     * Recursively packages a directory into an output stream.
     */
    public static void createZipFile(File sourceDirectory, OutputStream out) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(out);
        try {
            createZipFile(sourceDirectory, zipOut);
        } finally {
            zipOut.finish();
        }
    }

    /**
     * Recursively packages a directory into a ZIP output stream.
     */
    public static void createZipFile(File sourceDirectory, ZipOutputStream zipOut) throws IOException {
        File[] list = sourceDirectory.listFiles();
        if(list!=null) {
            for(File file : list) createZipFile(file, zipOut, "");
        }
    }

    /**
     * Recursively packages a directory into a ZIP output stream.
     */
    public static void createZipFile(File file, ZipOutputStream zipOut, String path) throws IOException {
        final String filename = file.getName();
        final String newPath = path.isEmpty() ? filename : (path+'/'+filename);
        if(file.isDirectory()) {
            // Add directory
            ZipEntry zipEntry = new ZipEntry(newPath + '/');
            setZipEntryTime(zipEntry, file.lastModified());
            zipOut.putNextEntry(zipEntry);
            zipOut.closeEntry();
            // Add all children
            File[] list = file.listFiles();
            if(list!=null) {
                for(File child : list) createZipFile(child, zipOut, filename);
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(newPath);
            setZipEntryTime(zipEntry, file.lastModified());
            zipOut.putNextEntry(zipEntry);
            try {
                InputStream in = new FileInputStream(file);
                try {
                    IoUtils.copy(in, zipOut);
                } finally {
                    in.close();
                }
            } finally {
                zipOut.closeEntry();
            }
        }
    }

    /**
     * Make no instances.
     */
    private ZipUtils() {
    }
}
