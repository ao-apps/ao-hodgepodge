/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to read FastExternalizable, Externalizable, and Serializable objects.
 *
 * When multiple objects are being written, this avoids the repetative writing of classnames and serialVersionUIDs.
 *
 * @author  AO Industries, Inc.
 */
public class FastExternalizableReadContext {

    private static final ThreadLocal<FastExternalizableReadContext> threadContext = new ThreadLocal<FastExternalizableReadContext>();

    /**
     * Sets-up or reuses the thread context, then calls the runnable.
     * For highest performance, this should be called as high as possible in the serialization process.
     * The same context must be established for writing.
     *
     * Because the context is associated with the thread, a thread should not write
     * to different objects streams at the same time within this runnable.  It should
     * also not hand-off the work to another thread (or ExecutorService).
     *
     * @see  #call(com.aoindustries.io.FastWriteObjectRunnable)
     */
    public static void call(FastReadObjectRunnable runnable) throws IOException, ClassNotFoundException {
        FastExternalizableReadContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            runnable.run(context);
        } else {
            context = new FastExternalizableReadContext();
            threadContext.set(context);
            try {
                runnable.run(context);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls readObject.
     *
     * @see #readObject
     *
     * @see  #writeObjectInContext
     */
    public static Object readObjectInContext(ObjectInput in) throws IOException, ClassNotFoundException {
        FastExternalizableReadContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            return context.readObject(in);
        } else {
            context = new FastExternalizableReadContext();
            threadContext.set(context);
            try {
                return context.readObject(in);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls readFastObject.
     *
     * @see #readFastObject
     *
     * @see  #writeFastObjectInContext
     */
    public static FastExternalizable readFastObjectInContext(ObjectInput in) throws IOException, ClassNotFoundException {
        FastExternalizableReadContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            return context.readFastObject(in);
        } else {
            context = new FastExternalizableReadContext();
            threadContext.set(context);
            try {
                return context.readFastObject(in);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls readFastUTF.
     *
     * @see #readFastUTF
     *
     * @see  #writeFastUTFInContext
     */
    public static String readFastUTFInContext(ObjectInput in) throws IOException, ClassNotFoundException {
        FastExternalizableReadContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            return context.readFastUTF(in);
        } else {
            context = new FastExternalizableReadContext();
            threadContext.set(context);
            try {
                return context.readFastUTF(in);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * A mapping of generated IDs to classes.
     */
    private final List<Class<?>> classesById = new ArrayList<Class<?>>();
    private final List<Long> serialVersionUIDsById = new ArrayList<Long>();
    private int nextClassId = 0;

    private Class<?> lastClass = null;
    private long lastSerialVersionUID = 0;

    /**
     * A mapping of generated IDs to strings.
     */
    private final List<String> stringsById = new ArrayList<String>();
    private int nextStringId = 0;

    private String lastString = null;

    private FastExternalizableReadContext() {
    }

    /**
     * Reads a possibly-fast externalizable object from the stream.
     *
     * @see  #writeObject
     */
    public Object readObject(ObjectInput in) throws IOException, ClassNotFoundException {
        int code = in.read();
        switch(code) {
            case FastExternalizableWriteContext.NULL :
                return null;
            case FastExternalizableWriteContext.STANDARD :
                return in.readObject();
            case -1 :
                throw new EOFException();
            default :
                return readFastObject(in, code);
        }
    }

    /**
     * Reads a fast serializable object from the stream.
     *
     * @see  #writeFastObject(java.io.ObjectOutput, com.aoindustries.io.FastExternalizable)
     */
    public FastExternalizable readFastObject(ObjectInput in) throws IOException, ClassNotFoundException {
        int code = in.read();
        switch(code) {
            case FastExternalizableWriteContext.NULL :
                return null;
            case FastExternalizableWriteContext.STANDARD :
                // This is OK, perhaps we just recently changed this object to now be a fast class
                return (FastExternalizable)in.readObject();
            case -1 :
                throw new EOFException();
            default :
                return readFastObject(in, code);
        }
    }

    /**
     * Reads a fast serializable object from the stream.
     *
     * @see  #writeFastObject(java.io.ObjectOutput, com.aoindustries.io.FastExternalizable)
     */
    private FastExternalizable readFastObject(ObjectInput in, int code) throws IOException, ClassNotFoundException {
        assert code>=FastExternalizableWriteContext.FAST_NEW;
        // Resolve class (as lastClass) by code
        switch(code) {
            case FastExternalizableWriteContext.FAST_SAME :
            {
                if(lastClass==null) throw new StreamCorruptedException("lastClass is null");
                break;
            }
            case FastExternalizableWriteContext.FAST_NEW :
            {
                classesById.add(lastClass = Class.forName(in.readUTF()));
                serialVersionUIDsById.add(lastSerialVersionUID = in.readLong());
                nextClassId++;
                break;
            }
            case FastExternalizableWriteContext.FAST_SEEN_SHORT :
            {
                int offset = in.readShort() & 0xffff;
                int classId = offset + (255-FastExternalizableWriteContext.FAST_SEEN_INT);
                if(classId>=nextClassId) throw new StreamCorruptedException("Class ID not already in steam: "+classId);
                lastClass = classesById.get(classId);
                lastSerialVersionUID = serialVersionUIDsById.get(classId);
                break;
            }
            case FastExternalizableWriteContext.FAST_SEEN_INT :
            {
                int classId = in.readInt();
                if(classId>=nextClassId) throw new StreamCorruptedException("Class ID not already in steam: "+classId);
                lastClass = classesById.get(classId);
                lastSerialVersionUID = serialVersionUIDsById.get(classId);
                break;
            }
            default :
            {
                assert code > FastExternalizableWriteContext.FAST_SEEN_INT;
                int classId = code - (FastExternalizableWriteContext.FAST_SEEN_INT + 1);
                if(classId>=nextClassId) throw new StreamCorruptedException("Class ID not already in steam: "+classId);
                lastClass = classesById.get(classId);
                lastSerialVersionUID = serialVersionUIDsById.get(classId);
            }
        }
        try {
            FastExternalizable obj = (FastExternalizable)lastClass.newInstance();
            long actualSerialVersionUID = obj.getSerialVersionUID();
            if(lastSerialVersionUID!=actualSerialVersionUID) throw new InvalidClassException(lastClass.getName(), "Mismatched serialVersionUID: expected "+lastSerialVersionUID+", got "+actualSerialVersionUID);
            obj.readExternal(in);
            return obj;
        } catch(InstantiationException exc) {
            InvalidClassException newExc = new InvalidClassException("InstantiationException");
            newExc.initCause(exc);
            throw newExc;
        } catch(IllegalAccessException exc) {
            InvalidClassException newExc = new InvalidClassException("IllegalAccessException");
            newExc.initCause(exc);
            throw newExc;
        }
    }

    /**
     * Reads a fast serialized String from the stream.
     *
     * @see  #writeFastUTF(java.io.ObjectOutput, com.aoindustries.io.FastExternalizable)
     */
    public String readFastUTF(ObjectInput in) throws IOException, ClassNotFoundException {
        int code = in.read();
        if(code==FastExternalizableWriteContext.NULL) return null;
        if(code==FastExternalizableWriteContext.STANDARD) throw new IOException("Unexpected code: "+code);
        if(code==-1) throw new EOFException();
        // Resolve string by code
        switch(code) {
            case FastExternalizableWriteContext.FAST_SAME :
            {
                if(lastString==null) throw new StreamCorruptedException("lastString is null");
                return lastString;
            }
            case FastExternalizableWriteContext.FAST_NEW :
            {
                lastString = in.readUTF();
                stringsById.add(lastString);
                nextStringId++;
                return lastString;
            }
            case FastExternalizableWriteContext.FAST_SEEN_SHORT :
            {
                int offset = in.readShort() & 0xffff;
                int stringId = offset + (255-FastExternalizableWriteContext.FAST_SEEN_INT);
                if(stringId>=nextStringId) throw new StreamCorruptedException("String ID not already in steam: "+stringId);
                return lastString = stringsById.get(stringId);
            }
            case FastExternalizableWriteContext.FAST_SEEN_INT :
            {
                int stringId = in.readInt();
                if(stringId>=nextStringId) throw new StreamCorruptedException("String ID not already in steam: "+stringId);
                return lastString = stringsById.get(stringId);
            }
            default :
            {
                assert code > FastExternalizableWriteContext.FAST_SEEN_INT;
                int stringId = code - (FastExternalizableWriteContext.FAST_SEEN_INT + 1);
                if(stringId>=nextStringId) throw new StreamCorruptedException("String ID not already in steam: "+stringId);
                return lastString = stringsById.get(stringId);
            }
        }
    }
}
