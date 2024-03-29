/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.io;

import com.aoapps.lang.AutoCloseables;
import com.aoapps.lang.io.AoByteArrayInputStream;
import com.aoapps.lang.io.AoByteArrayOutputStream;
import com.aoapps.tempfiles.TempFileContext;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * A <code>FileList</code> is a List that stores its objects in
 * a fixed-record-size file.
 *
 * @author  AO Industries, Inc.
 */
public class FileList<T extends FileListObject> extends AbstractList<T> implements RandomAccess, Closeable {

  private final String filenamePrefix;
  private final String filenameExtension;
  private final TempFileContext tempFileContext;
  private final FixedRecordFile frf;
  private final FileListObjectFactory<T> objectFactory;

  private final AoByteArrayInputStream inBuffer;
  private final DataInputStream dataInBuffer;
  private final AoByteArrayOutputStream outBuffer;
  private final DataOutputStream dataOutBuffer;

  public FileList(
      String filenamePrefix,
      String filenameExtension,
      int objectLength,
      FileListObjectFactory<T> objectFactory
  ) throws IOException {
    this.filenamePrefix = filenamePrefix;
    this.filenameExtension = filenameExtension;
    this.tempFileContext = new TempFileContext();
    this.frf = new FixedRecordFile(
        tempFileContext.createTempFile(
            filenamePrefix + '_',
            filenameExtension == null ? null : ("." + filenameExtension)
        ).getFile(),
        "rw",
        objectLength + 1
    );
    this.objectFactory = objectFactory;

    this.inBuffer = new AoByteArrayInputStream(new byte[objectLength + 1]);
    this.dataInBuffer = new DataInputStream(inBuffer);
    this.outBuffer = new AoByteArrayOutputStream(objectLength + 1);
    this.dataOutBuffer = new DataOutputStream(outBuffer);
  }

  @Override
  public void clear() {
    try {
      frf.removeAllRecords();
      modCount++;
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  @Override
  public T get(int index) {
    try {
      frf.seekToExistingRecord(index);
      inBuffer.fillFrom(frf);
      if (dataInBuffer.readBoolean()) {
        T obj = objectFactory.createInstance();
        obj.readRecord(dataInBuffer);
        return obj;
      } else {
        return null;
      }
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  public void swap(int index1, int index2) {
    try {
      frf.swap(index1, index2);
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  @Override
  public int size() {
    try {
      return frf.getRecordCount();
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  public int getRecordLength() {
    return frf.getRecordLength();
  }

  @Override
  public T set(int index, T element) {
    try {
      // Read old object
      frf.seekToExistingRecord(index);
      inBuffer.fillFrom(frf);
      T old;
      if (dataInBuffer.readBoolean()) {
        old = objectFactory.createInstance();
        old.readRecord(dataInBuffer);
      } else {
        old = null;
      }

      // Write new object
      frf.seekToExistingRecord(index);
      outBuffer.reset();
      if (element == null) {
        dataOutBuffer.writeBoolean(false);
      } else {
        T newObj = element;
        dataOutBuffer.writeBoolean(true);
        newObj.writeRecord(dataOutBuffer);
      }
      int recordSize = outBuffer.size();
      if (recordSize > frf.getRecordLength()) {
        throw new IOException("Record length exceeded: outBuffer.size()=" + recordSize + ", frf.getRecordLength()=" + frf.getRecordLength());
      }
      outBuffer.writeTo(frf);

      // Return old object
      return old;
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  @Override
  public void add(int index, T element) {
    try {
      // Write to buffer
      outBuffer.reset();
      if (element == null) {
        dataOutBuffer.writeBoolean(false);
      } else {
        T newObj = element;
        dataOutBuffer.writeBoolean(true);
        newObj.writeRecord(dataOutBuffer);
      }
      int recordSize = outBuffer.size();
      if (recordSize > frf.getRecordLength()) {
        throw new IOException("Record length exceeded: outBuffer.size()=" + recordSize + ", frf.getRecordLength()=" + frf.getRecordLength());
      }

      // Seeks to beginning of the new record
      frf.addRecord(index);

      // Write new object
      outBuffer.writeTo(frf);

      modCount++;
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf, err);
    }
  }

  @Override
  public boolean addAll(Collection<? extends T> collection) {
    return addAll(size(), collection);
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> collection) {
    try {
      FileList<? extends T> otherFileList;
      if (
          (collection instanceof FileList)
              && (otherFileList = (FileList<? extends T>) collection).frf.getRecordLength() == frf.getRecordLength()
      ) {
        // Do direct disk copies
        boolean changed = false;
        int otherSize = otherFileList.size();
        if (otherSize > 0) {
          frf.addRecords(index, otherSize);
          FixedRecordFile.copyRecords(otherFileList.frf, 0, frf, index, otherSize);
          changed = true;
        }
        if (changed) {
          modCount++;
        }
        return changed;
      } else {
        // Do block allocate then write
        boolean changed = false;
        int otherSize = collection.size();
        if (otherSize > 0) {
          frf.addRecords(index, otherSize);
          Iterator<? extends T> records = collection.iterator();
          int count = 0;
          while (records.hasNext()) {
            // Write to buffer
            outBuffer.reset();
            T o = records.next();
            if (o == null) {
              dataOutBuffer.writeBoolean(false);
            } else {
              dataOutBuffer.writeBoolean(true);
              o.writeRecord(dataOutBuffer);
            }
            int recordSize = outBuffer.size();
            if (recordSize > frf.getRecordLength()) {
              throw new IOException("Record length exceeded: outBuffer.size()=" + recordSize + ", frf.getRecordLength()=" + frf.getRecordLength());
            }

            // Write to disk
            frf.seekToExistingRecord(index + count);
            outBuffer.writeTo(frf);
            count++;
          }
          if (count != otherSize) {
            throw new IOException("count != otherSize");
          }
          changed = true;
        }
        if (changed) {
          modCount++;
        }
        return changed;
      }
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf + ", index=" + index, err);
    }
  }

  @Override
  public T remove(int index) {
    try {
      // Read the old object
      frf.seekToExistingRecord(index);
      inBuffer.fillFrom(frf);
      T old;
      if (dataInBuffer.readBoolean()) {
        old = objectFactory.createInstance();
        old.readRecord(dataInBuffer);
      } else {
        old = null;
      }

      frf.removeRecord(index);

      modCount++;

      // Return the old object
      return old;
    } catch (IOException err) {
      throw new UncheckedIOException("frf=" + frf + ", index=" + index, err);
    }
  }

  public String getFilenamePrefix() {
    return filenamePrefix;
  }

  public String getFilenameExtension() {
    return filenameExtension;
  }

  public FileListObjectFactory<T> getObjectFactory() {
    return objectFactory;
  }

  @Override
  public void close() throws IOException {
    AutoCloseables.closeAndThrow(IOException.class, IOException::new, frf, tempFileContext);
  }

  /**
   * @deprecated  Please use {@link TempFileContext}
   *              as {@link File#deleteOnExit()} is prone to memory leaks in long-running applications.
   */
  @Deprecated
  public static File getTempFile(String prefix, String extension) throws IOException {
    if (extension == null) {
      extension = "tmp";
    }
    /* Now just using standard Java temporary files to avoid dependency on new ao-io-posix project.
    try {
      // First try to use Unix file because it creates the files with 600 permissions.
      File f=PosixFile.mktemp(System.getProperty("java.io.tmpdir")+'/'+prefix+'_'+extension+'.', true).getFile();
      return f;
    } catch (SecurityException err) {
      // This is OK if now allowed to load libraries
    } catch (UnsatisfiedLinkError err) {
      // This is OK if the library is not supported on this platform
    }
     */
    File f = File.createTempFile(prefix + '_', '.' + extension);
    f.deleteOnExit();
    return f;
  }
}
