package com.aoindustries.io;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.unix.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.util.*;

/**
 * A <code>FileList</code> is a List that stores its objects in
 * a fixed-record-size file.
 *
 * @author  AO Industries, Inc.
 */
public class FileList<T extends FileListObject> extends AbstractList<T> implements RandomAccess {

    final private String filenamePrefix;
    final private String filenameExtension;
    final private File file;
    final private FixedRecordFile frf;
    final private FileListObjectFactory<T> objectFactory;

    final private BetterByteArrayInputStream inBuffer;
    final private DataInputStream dataInBuffer;
    final private BetterByteArrayOutputStream outBuffer;
    final private DataOutputStream dataOutBuffer;

    public FileList(
        String filenamePrefix,
        String filenameExtension,
        int objectLength,
        FileListObjectFactory<T> objectFactory
    ) throws IOException {
        Profiler.startProfile(Profiler.IO, FileList.class, "<init>(String,String,int,FileListObjectFactory<T>)", null);
        try {
            this.filenamePrefix=filenamePrefix;
            this.filenameExtension=filenameExtension;
            this.file=getTempFile(filenamePrefix, filenameExtension);
            this.frf=new FixedRecordFile(file, "rw", objectLength+1);
            this.objectFactory=objectFactory;

            this.inBuffer=new BetterByteArrayInputStream(new byte[objectLength+1]);
            this.dataInBuffer=new DataInputStream(inBuffer);
            this.outBuffer=new BetterByteArrayOutputStream(objectLength+1);
            this.dataOutBuffer=new DataOutputStream(outBuffer);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void clear() {
        Profiler.startProfile(Profiler.IO, FileList.class, "clear()", null);
        try {
            try {
                frf.removeAllRecords();
                modCount++;
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public T get(int index) {
        Profiler.startProfile(Profiler.IO, FileList.class, "get(int)", null);
        try {
            try {
                frf.seekToExistingRecord(index);
                inBuffer.readFrom(frf);
                if(dataInBuffer.readBoolean()) {
                    T obj=objectFactory.createInstance();
                    obj.readRecord(dataInBuffer);
                    return obj;
                } else return null;
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    public void swap(int index1, int index2) {
        Profiler.startProfile(Profiler.IO, FileList.class, "swap(int,int)", null);
        try {
            try {
                frf.swap(index1, index2);
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int size() {
        Profiler.startProfile(Profiler.IO, FileList.class, "size()", null);
        try {
            try {
                return frf.getRecordCount();
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    public T set(int index, T element) {
        Profiler.startProfile(Profiler.IO, FileList.class, "set(int,T)", null);
        try {
            try {
                // Read old object
                frf.seekToExistingRecord(index);
                inBuffer.readFrom(frf);
                T old;
                if(dataInBuffer.readBoolean()) {
                    old=objectFactory.createInstance();
                    old.readRecord(dataInBuffer);
                } else old=null;

                // Write new object
                frf.seekToExistingRecord(index);
                outBuffer.reset();
                if(element==null) dataOutBuffer.writeBoolean(false);
                else {
                    T newObj=element;
                    dataOutBuffer.writeBoolean(true);
                    newObj.writeRecord(dataOutBuffer);
                }
                int recordSize=outBuffer.size();
                if(recordSize>frf.getRecordLength()) throw new IOException("Record length exceeded: outBuffer.size()="+recordSize+", frf.getRecordLength()="+frf.getRecordLength());
                outBuffer.writeTo(frf);

                // Return old object
                return old;
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void add(int index, T element) {
        Profiler.startProfile(Profiler.IO, FileList.class, "add(int,T)", null);
        try {
            try {
                // Write to buffer
                outBuffer.reset();
                if(element==null) dataOutBuffer.writeBoolean(false);
                else {
                    T newObj=element;
                    dataOutBuffer.writeBoolean(true);
                    newObj.writeRecord(dataOutBuffer);
                }
                int recordSize=outBuffer.size();
                if(recordSize>frf.getRecordLength()) throw new IOException("Record length exceeded: outBuffer.size()="+recordSize+", frf.getRecordLength()="+frf.getRecordLength());

                // Seeks to beginning of the new record
                frf.addRecord(index);

                // Write new object
                outBuffer.writeTo(frf);
                
                modCount++;
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public boolean addAll(Collection<? extends T> C) {
        Profiler.startProfile(Profiler.FAST, FileList.class, "addAll(Collection<? extends T>)", null);
        try {
            return addAll(size(), C);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean addAll(int index, Collection<? extends T> C) {
        Profiler.startProfile(Profiler.IO, FileList.class, "addAll(int,Collection<? extends T>)", null);
        try {
            try {
                FileList otherFL;
                if(
                    (C instanceof FileList)
                    && (otherFL=(FileList)C).frf.getRecordLength()==frf.getRecordLength()
                ) {
                    // Do direct disk copies
                    boolean changed=false;
                    int otherSize=otherFL.size();
                    if(otherSize>0) {
                        frf.addRecords(index, otherSize);
                        FixedRecordFile.copyRecords(otherFL.frf, 0, frf, index, otherSize);
                        changed=true;
                    }
                    if(changed) modCount++;
                    return changed;
                } else if(C instanceof List) {
                    // Do block allocate then write
                    boolean changed=false;
                    List<T> otherList=(List)C;
                    int otherSize=otherList.size();
                    if(otherSize>0) {
                        frf.addRecords(index, otherSize);
                        for(int c=0;c<otherSize;c++) {
                            // Write to buffer
                            outBuffer.reset();
                            T O=otherList.get(c);
                            if(O==null) dataOutBuffer.writeBoolean(false);
                            else {
                                dataOutBuffer.writeBoolean(true);
                                O.writeRecord(dataOutBuffer);
                            }
                            int recordSize=outBuffer.size();
                            if(recordSize>frf.getRecordLength()) throw new IOException("Record length exceeded: outBuffer.size()="+recordSize+", frf.getRecordLength()="+frf.getRecordLength());

                            // Write to disk
                            frf.seekToExistingRecord(index+c);
                            outBuffer.writeTo(frf);
                        }
                        changed=true;
                    }
                    if(changed) modCount++;
                    return changed;
                } else {
                    // Default to single adds
                    return super.addAll(index, C);
                }
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf, "index="+index});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public T remove(int index) {
        Profiler.startProfile(Profiler.IO, FileList.class, "remove(int)", null);
        try {
            try {
                // Read the old object
                frf.seekToExistingRecord(index);
                inBuffer.readFrom(frf);
                T old;
                if(dataInBuffer.readBoolean()) {
                    old=objectFactory.createInstance();
                    old.readRecord(dataInBuffer);
                } else old=null;

                frf.removeRecord(index);
                
                modCount++;

                // Return the old object
                return old;
            } catch(IOException err) {
                throw new WrappedException(err, new Object[] {"frf="+frf, "index="+index});
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public String getFilenamePrefix() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FileList.class, "getFilenamePrefix()", null);
        try {
            return filenamePrefix;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public String getFilenameExtension() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FileList.class, "getFilenameExtension()", null);
        try {
            return filenameExtension;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public FileListObjectFactory<T> getObjectFactory() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FileList.class, "getObjectFactory()", null);
        try {
            return objectFactory;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public void finalize() throws IOException {
        // finalize methods not profiled
        close0();
    }
    
    public void close() throws IOException {
        Profiler.startProfile(Profiler.IO, FileList.class, "close()", null);
        try {
            close0();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private void close0() throws IOException {
        // Not profiled because it is called by finalize
        frf.close();
        if(file.exists() && !file.delete()) throw new IOException("Unable to delete file: "+file.getPath());
    }
    
    public static File getTempFile(String prefix, String extension) throws IOException {
        Profiler.startProfile(Profiler.IO, FileList.class, "getTempFile()", null);
        try {
            if(extension==null) extension="tmp";
            try {
                // First try to use Unix file because it creates the files with 600 permissions.
                File f=UnixFile.mktemp(System.getProperty("java.io.tmpdir")+'/'+prefix+'_'+extension+'.').getFile();
                f.deleteOnExit();
                return f;
            } catch(SecurityException err) {
                // This is OK if now allowed to load libraries
            } catch(UnsatisfiedLinkError err) {
                // This is OK if the library is not supported on this platform
            }

            File f=File.createTempFile(prefix+'_', '.'+extension);
            f.deleteOnExit();
            return f;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
