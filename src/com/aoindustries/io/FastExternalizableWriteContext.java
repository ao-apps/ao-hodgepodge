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

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities to write FastExternalizable, Externalizable, and Serializable objects.
 *
 * When multiple objects are being written, this avoids the repetative writing of classnames and serialVersionUIDs.
 *
 * @author  AO Industries, Inc.
 */
public class FastExternalizableWriteContext {

    private static final ThreadLocal<FastExternalizableWriteContext> threadContext = new ThreadLocal<FastExternalizableWriteContext>();

    /**
     * Sets-up or reuses the thread context, then calls the runnable.
     * For highest performance, this should be called as high as possible in the serialization process.
     * The same context must be established for writing.
     *
     * Because the context is associated with the thread, a thread should not write
     * to different objects streams at the same time within this runnable.  It should
     * also not hand-off the work to another thread (or ExecutorService).
     *
     * @see  #call(com.aoindustries.io.FastReadObjectRunnable)
     */
    public static void call(FastWriteObjectRunnable runnable) throws IOException {
        FastExternalizableWriteContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            runnable.run(context);
        } else {
            context = new FastExternalizableWriteContext();
            threadContext.set(context);
            try {
                runnable.run(context);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls writeObject.
     *
     * @see #writeObject
     *
     * @see  #readObjectInContext
     */
    public static void writeObjectInContext(ObjectOutput out, Object obj) throws IOException {
        FastExternalizableWriteContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            context.writeObject(out, obj);
        } else {
            context = new FastExternalizableWriteContext();
            threadContext.set(context);
            try {
                context.writeObject(out, obj);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls writeFastObject.
     *
     * @see #writeFastObject
     *
     * @see  #readFastObjectInContext
     */
    public static void writeFastObjectInContext(ObjectOutput out, FastExternalizable obj) throws IOException {
        FastExternalizableWriteContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            context.writeFastObject(out, obj);
        } else {
            context = new FastExternalizableWriteContext();
            threadContext.set(context);
            try {
                context.writeFastObject(out, obj);
            } finally {
                threadContext.remove();
            }
        }
    }

    /**
     * Resolves or creates the context, then calls writeFastUTF.
     *
     * For maximum performance, an array is used to compare strings by identity first, thus
     * already interned strings will benefit the most.
     *
     * @see #writeFastUTF
     *
     * @see  #readFastUTFInContext
     */
    public static void writeFastUTFInContext(ObjectOutput out, String value) throws IOException {
        FastExternalizableWriteContext context = threadContext.get();
        if(context!=null) {
            // Reuse existing
            context.writeFastUTF(out, value);
        } else {
            context = new FastExternalizableWriteContext();
            threadContext.set(context);
            try {
                context.writeFastUTF(out, value);
            } finally {
                threadContext.remove();
            }
        }
    }

    static final int
        // The object is null
        NULL = 0,
        // The object uses standard serialization
        STANDARD = NULL+1,
        // The object is of a previously unseen class
        FAST_NEW = STANDARD+1,
        // The object is the same class as the previous object
        FAST_SAME = FAST_NEW+1,
        // The object is of a class that has already been seen, and uses the next two bytes as its class ID - (255 - FAST_SEEN_CLASS_INT)
        FAST_SEEN_SHORT = FAST_SAME+1,
        // The object is of a class that has already been seen, and uses the next four bytes as its class ID
        FAST_SEEN_INT = FAST_SEEN_SHORT+1
        // The remaining values are for direct already seen classes between 0 <= ID < (255-FAST_SEEN_CLASS_INT)
    ;

    private static final int MAP_ARRAY_LENGTH = 20; // TODO: Benchmark what is best value

    /**
     * A mapping of classes to generated class IDs.
     */
    private Map<Class<?>,Integer> classesMap;
    private final Class<?>[] classesArray = new Class<?>[MAP_ARRAY_LENGTH];
    private int nextClassId = 0;
    private Class<?> lastClass = null;

    /**
     * A mapping of fast string IDs.
     */
    private Map<String,Integer> stringsMap;
    private final String[] stringsArray = new String[MAP_ARRAY_LENGTH];
    private int nextStringId = 0;
    private String lastString = null;

    private FastExternalizableWriteContext() {
    }

    /**
     * Writes the provided object in the most efficient manner possible, with no object graph tracking (if possible).
     *
     * If the object is null, writes a single byte of <code>NULL</code>.
     *
     * If the object is not FastSerializable, writes <code>STANDARD</code> and then uses standard Java serialization.
     *
     * Otherwise, calls writeFastObject(FastSerializable).
     *
     * This allows individual objects to switch between FastExternalizable and standard serialization without calling
     * code needing to know the difference.
     *
     * @see  #readObject
     */
    public void writeObject(ObjectOutput out, Object obj) throws IOException {
        if(obj==null) {
            out.write(NULL);
        } else if(!(obj instanceof FastExternalizable)) {
            out.write(STANDARD);
            out.writeObject(obj);
        } else {
            writeFastObject(out, (FastExternalizable)obj);
        }
    }

    /**
     * Writes a fast externalizable object to the provided stream, supporting null values.
     *
     * @see  #readFastObject
     */
    public void writeFastObject(ObjectOutput out, FastExternalizable obj) throws IOException {
        if(obj==null) {
            out.write(NULL);
        } else {
            Class<?> clazz = obj.getClass();
            if(clazz==lastClass) {
                out.write(FAST_SAME);
            } else {
                int classId;
                for(
                    classId=nextClassId < MAP_ARRAY_LENGTH ? nextClassId-1 : MAP_ARRAY_LENGTH-1;
                    classId>=0;
                    classId--
                ) {
                    if(classesArray[classId]==clazz) break;
                }
                if(classId==-1 && classesMap!=null) {
                    Integer classIdObj = classesMap.get(clazz);
                    if(classIdObj!=null) classId = classIdObj;
                }
                if(classId==-1) {
                    if(nextClassId<MAP_ARRAY_LENGTH) {
                        classesArray[nextClassId] = clazz;
                    } else {
                        if(classesMap==null) classesMap = new HashMap<Class<?>,Integer>();
                        classesMap.put(clazz, nextClassId);
                    }
                    nextClassId++;
                    out.write(FAST_NEW);
                    out.writeUTF(clazz.getName());
                    out.writeLong(obj.getSerialVersionUID());
                } else {
                    if(classId < (255-FAST_SEEN_INT)) { // 0 - 250
                        int code = classId + (FAST_SEEN_INT + 1);
                        assert code>FAST_SEEN_INT;
                        assert code<=255;
                        out.write(code);
                    } else if(classId <= (65536 + (255-FAST_SEEN_INT))) { // 251-65786
                        out.write(FAST_SEEN_SHORT);
                        int offset = classId - (255-FAST_SEEN_INT);
                        assert offset>=0;
                        assert offset<=65535;
                        out.writeShort(offset);
                    } else {
                        out.write(FAST_SEEN_INT); // 65787-Integer.MAX_VALUE, no offset
                        assert classId > (65536 + (255-FAST_SEEN_INT));
                        out.writeInt(classId);
                    }
                }
                lastClass = clazz;
            }
            obj.writeExternal(out);
        }
    }

    /**
     * Writes a string to the output, not writing any duplicates.
     * Supports nulls.
     */
    public void writeFastUTF(ObjectOutput out, String value) throws IOException {
        if(value==null) {
            out.write(NULL);
        } else {
            if(value==lastString) {
                out.write(FAST_SAME);
            } else {
                int stringId;
                for(
                    stringId=nextStringId < MAP_ARRAY_LENGTH ? nextStringId-1 : MAP_ARRAY_LENGTH-1;
                    stringId>=0;
                    stringId--
                ) {
                    if(stringsArray[stringId]==value) break;
                }
                if(stringId==-1 && stringsMap!=null) {
                    Integer stringIdObj = stringsMap.get(value);
                    if(stringIdObj!=null) stringId = stringIdObj;
                }
                if(stringId==-1) {
                    if(nextStringId<MAP_ARRAY_LENGTH) {
                        stringsArray[nextStringId] = value;
                    } else {
                        if(stringsMap==null) stringsMap = new HashMap<String,Integer>();
                        stringsMap.put(value, nextStringId);
                    }
                    nextStringId++;
                    out.write(FAST_NEW);
                    out.writeUTF(value);
                } else {
                    if(stringId < (255-FAST_SEEN_INT)) { // 0 - 250
                        int code = stringId + (FAST_SEEN_INT + 1);
                        assert code>FAST_SEEN_INT;
                        assert code<=255;
                        out.write(code);
                    } else if(stringId <= (65536 + (255-FAST_SEEN_INT))) { // 251-65786
                        //try {
                            out.write(FAST_SEEN_SHORT);
                            int offset = stringId - (255-FAST_SEEN_INT);
                            assert offset>=0;
                            assert offset<=65535;
                            out.writeShort(offset);
                        //} catch(AssertionError exc) {
                        //    throw exc;
                        //}
                    } else {
                        out.write(FAST_SEEN_INT); // 65787-Integer.MAX_VALUE, no offset
                        assert stringId > (65536 + (255-FAST_SEEN_INT));
                        out.writeInt(stringId);
                    }
                }
                lastString = value;
            }
        }
    }
}
