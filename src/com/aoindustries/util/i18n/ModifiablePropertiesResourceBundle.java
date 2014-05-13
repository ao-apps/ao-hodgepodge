/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import com.aoindustries.io.FileUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.util.ResourceBundleEnumeration;

/**
 * <p>
 * Wraps the resources with XHTML and scripts to allow the modification of the
 * resource bundle contents directly through the web interface.  Also adds an
 * indicator when the resource need to be verified.  Verification is required
 * when any other locale has a modified time greater than the verified time
 * of this locale.
 * </p>
 * <p>
 * The properties file should have the same name as this class, including any language
 * or locale.  For instance, class <code>com.aoindustries.swing.ApplicationResources_ja</code>
 * would load its properties from <code>com/aoindustries/swing/ApplicationResources_ja.properties</code>.
 * </p>
 * <p>
 * Idea: Occasionally check sourceFile and reload properties if externally modified.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class ModifiablePropertiesResourceBundle extends ModifiableResourceBundle {

    private static final Logger logger = Logger.getLogger(ModifiablePropertiesResourceBundle.class.getName());

    private static final Charset propertiesCharset = Charset.forName("ISO-8859-1");
    private static final String EOL = System.getProperty("line.separator");

    private static final String VALIDATED_SUFFIX = ".ModifiableResourceBundle.validated";
    private static final String MODIFIED_SUFFIX = ".ModifiableResourceBundle.modified";

	/**
	 * Checks if a key is used for tracking status.
	 */
	public static boolean isTrackingKey(String key) {
		return
			key.endsWith(VALIDATED_SUFFIX)
			|| key.endsWith(MODIFIED_SUFFIX)
		;
	}

	/**
     * <p>
     * If this sourceFile exists, the properties will be read from that file,
     * otherwise it will fall back to <code>Class.getResourceAsStream</code>.
     * This allows direct manipulation of the source tree during development,
     * while maintaining compatibility with the standard <code>PropertyResourceBundle</code>
     * implementation.
     * </p>
     * <p>
     * If this sourceFile does not exist, the bundle will not be modifiable and
     * any attempt to modify it will result in a <code>RuntimeException</code>.
     * </p>
     */
    private final File sourceFile;

    /**
     * Any comments from the original source file, if available.  Each does not
     * contain an end of line character.
     */
    private final List<String> sourceFileComments;

    private final boolean isModifiable;

    /**
     * All queries are performed on the concurrent map.
     */
    private final Map<String,String> valueMap = new ConcurrentHashMap<String,String>();

    /**
     * All validated queries are performed on this concurrent map.
     */
    private final Map<String,Long> validatedMap = new ConcurrentHashMap<String,Long>();

    /**
     * All modified queries are performed on this concurrent map.
     */
    private final Map<String,Long> modifiedMap = new ConcurrentHashMap<String,Long>();

    /**
     * The properties file is only used for updates.
     */
    private final Properties properties = new Properties();

    /**
     * Captures comments from any lines that begin with #.  This class is here because it is probably
     * too simple to be generally useful, as it assumes ISO-8859-1 encoding like used
     * by Properties.store.
     */
    static class CommentCaptureInputStream extends InputStream {
        private final InputStream in;
        CommentCaptureInputStream(InputStream in) {
            this.in = in;
        }

        private boolean lastCharNewline = true;
        private boolean isCommentLine = false;
        private StringBuilder currentComment = new StringBuilder();

        private List<String> comments = new ArrayList<String>();

        /**
         * Adds buffered comment to comments if non-empty.
         */
        private void addComment() {
            if(currentComment.length()>0) {
                comments.add(currentComment.toString());
                currentComment.setLength(0);
            }
        }

        @Override
        public int read() throws IOException {
            int ch = in.read();
            if(ch==-1) {
                // Handle EOL as newline terminator
                lastCharNewline = true;
                isCommentLine = false;
                addComment();
            } else {
                if(lastCharNewline) isCommentLine = ch=='#';
                lastCharNewline = ch=='\n';
                if(lastCharNewline) addComment();
                if(isCommentLine && ch!='\n' && ch!='\r') {
                    // This int->char conversion by cast only words because ISO-8859-1 encoding
                    currentComment.append((char)ch);
                }
            }
            return ch;
        }

        @Override
        public void close() throws IOException {
            addComment();
            super.close();
        }

        List<String> getComments() {
            return comments;
        }
    }

    /**
     * @param sourceFile  The source file(s).  If multiple source files are provided,
     *                    only one may exist and be both readable and writable.  If more than
     *                    one possible source file exists, will throw an IllegalStateException.
     */
    public ModifiablePropertiesResourceBundle(File... sourceFiles) {
        File goodSourceFile = null;
        if(sourceFiles!=null) {
            for(File file : sourceFiles) {
                try {
                    if(file.canRead() && file.canWrite()) {
                        if(goodSourceFile!=null) throw new IllegalStateException(ApplicationResources.accessor.getMessage("ModifiablePropertiesResourceBundle.init.moreThanOneSourceFile", goodSourceFile, file));
                        goodSourceFile = file;
                    }
                } catch(SecurityException e) {
                    // OK when sandboxed, goodSourceFile remains null
                }
            }
        }
        this.sourceFile = goodSourceFile;

        // Try to load from the sourceFile
        List<String> mySourceFileComments = null;
        boolean myIsModifiable = false;
        boolean loaded = false;
        if(goodSourceFile!=null) {
            try {
                CommentCaptureInputStream in = new CommentCaptureInputStream(new BufferedInputStream(new FileInputStream(goodSourceFile)));
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
                mySourceFileComments = in.getComments();
            } catch(IOException err) {
                logger.log(
                    Level.WARNING,
                    ApplicationResources.accessor.getMessage("ModifiablePropertiesResourceBundle.init.ioException", goodSourceFile),
                    err
                );
            }
            loaded = true;
            myIsModifiable = true;
        }
        this.sourceFileComments = mySourceFileComments;
        this.isModifiable = myIsModifiable;
        // Load from resources if sourceFile inaccessible
        if(!loaded) {
            Class<?> clazz = getClass();
            String resourceName = '/'+clazz.getName().replace('.', '/')+".properties";
            InputStream in = getClass().getResourceAsStream(resourceName);
            if(in==null) throw new RuntimeException(ApplicationResources.accessor.getMessage("ModifiablePropertiesResourceBundle.init.resourceNotFound", resourceName));
            try {
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
            } catch(IOException err) {
                throw new RuntimeException(err);
            }
        }
        // Populate the concurrent maps while skipping the validated and modified entries
        for(Map.Entry<Object,Object> entry : properties.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if(key.endsWith(VALIDATED_SUFFIX)) {
                validatedMap.put(key.substring(0, key.length()-VALIDATED_SUFFIX.length()), Long.parseLong(value));
            } else if(key.endsWith(MODIFIED_SUFFIX)) {
                modifiedMap.put(key.substring(0, key.length()-MODIFIED_SUFFIX.length()), Long.parseLong(value));
            } else {
                valueMap.put(key, value);
            }
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        if(key==null) throw new NullPointerException();
        return valueMap.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        ResourceBundle myParent= this.parent;
        return new ResourceBundleEnumeration(valueMap.keySet(), (myParent != null) ? myParent.getKeys() : null);
    }

    @Override
    protected Set<String> handleKeySet() {
        return valueMap.keySet();
    }

    public Set<String> keySetNoParents() {
        return valueMap.keySet();
    }

    @Override
    public boolean isModifiable() {
        return isModifiable;
    }

    /**
     * Makes sure the key is allowed, throws <code>IllegalArgumentException</code> when not allowed.
     */
    private static void checkKey(String key) throws IllegalArgumentException {
        if(key.endsWith(VALIDATED_SUFFIX)) throw new IllegalArgumentException("Key may not end with "+VALIDATED_SUFFIX+": "+key);
        if(key.endsWith(MODIFIED_SUFFIX)) throw new IllegalArgumentException("Key may not end with "+MODIFIED_SUFFIX+": "+key);
    }

    /**
     * Skips any lines that begin with #.  This class is here because it is probably
     * too simple to be generally useful, as it assumes ISO-8859-1 encoding like used
     * by Properties.store.
     */
    static class SkipCommentsFilterOutputStream extends FilterOutputStream {
        SkipCommentsFilterOutputStream(OutputStream out) {
            super(out);
        }

        private boolean lastCharNewline = true;
        private boolean isCommentLine = false;

        @Override
        public void write(int ch) throws IOException {
            if(lastCharNewline) isCommentLine = ch=='#';
            lastCharNewline = ch=='\n';
            if(!isCommentLine) out.write(ch);
        }
    }

    /**
     * Saves the properties file in ascending key order.  All accesses must
     * already hold a lock on the properties object.
     */
    private void saveProperties() {
        assert Thread.holdsLock(properties);
        try {
            // Create a properties instance that sorts the output by keys (case-insensitive)
            Properties writer = new Properties() {
                private static final long serialVersionUID = 6953022173340009928L;
                @Override
                public Enumeration<Object> keys() {
                    SortedSet<Object> sortedSet = new TreeSet<Object>(Collator.getInstance(Locale.ENGLISH));
                    Enumeration<Object> e = super.keys();
                    while(e.hasMoreElements()) sortedSet.add(e.nextElement());
                    return Collections.enumeration(sortedSet);
                }
            };
            writer.putAll(properties);
            File tmpFile = File.createTempFile("ApplicationResources", null, sourceFile.getParentFile());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile));
            try {
                // Write any comments from when file was read
                if(sourceFileComments!=null) {
                    for(String line : sourceFileComments) {
                        out.write(line.getBytes(propertiesCharset));
                        out.write(EOL.getBytes(propertiesCharset));
                    }
                }
                // Wrap to skip any comments generated by Properties code
                out = new SkipCommentsFilterOutputStream(out);
                writer.store(out, null);
            } finally {
                out.close();
            }
			FileUtils.renameAllowNonAtomic(tmpFile, sourceFile);
        } catch(IOException err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    protected void handleRemoveKey(String key) {
        checkKey(key);
        // Updates are serialized
        synchronized(properties) {
            properties.remove(key);
            properties.remove(key + VALIDATED_SUFFIX);
            properties.remove(key + MODIFIED_SUFFIX);
            saveProperties();
            valueMap.remove(key);
            validatedMap.remove(key);
            modifiedMap.remove(key);
        }
    }

	@Override
    protected void handleSetObject(String key, Object value, boolean modified) {
        checkKey(key);
        // Updates are serialized
        synchronized(properties) {
            Long currentTimeLong = System.currentTimeMillis();
            String currentTimeString = currentTimeLong.toString();
            properties.setProperty(key, (String)value);
            properties.setProperty(key+VALIDATED_SUFFIX, currentTimeString);
            if(modified) properties.setProperty(key+MODIFIED_SUFFIX, currentTimeString);
            saveProperties();
            valueMap.put(key, (String)value);
            validatedMap.put(key, currentTimeLong);
            if(modified) modifiedMap.put(key, currentTimeLong);
        }
    }

    /**
     * Provides direct read access to the value.
     */
    protected String getValue(String key) {
        return valueMap.get(key);
    }

    /**
     * Provides direct read access to the validated times.
     */
    public Long getValidatedTime(String key) {
        return validatedMap.get(key);
    }

    /**
     * Provides direct read access to the modified times.
     */
    public Long getModifiedTime(String key) {
        return modifiedMap.get(key);
    }
}
