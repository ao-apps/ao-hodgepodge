/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util;

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
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

    private static final String VALIDATED_SUFFIX = ".ModifiableResourceBundle.validated";
    private static final String MODIFIED_SUFFIX = ".ModifiableResourceBundle.modified";
    private static final String MEDIATYPE_SUFFIX = ".ModifiableResourceBundle.mediaType";
    private static final String ISBLOCKELEMENT_SUFFIX = ".ModifiableResourceBundle.isBlockElement";

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
     * All type queries are performed on this concurrent map.
     */
    private final Map<String,MediaType> typeMap = new ConcurrentHashMap<String,MediaType>();

    /**
     * All isBlockElement queries are performed on this concurrent map.
     */
    private final Map<String,Boolean> isBlockElementMap = new ConcurrentHashMap<String,Boolean>();

    /**
     * The properties file is only used for updates.
     */
    private final Properties properties = new Properties();

    /**
     * @param sourceFile  The source file or <code>null</code> for none.
     */
    public ModifiablePropertiesResourceBundle(File sourceFile) {
        this.sourceFile = sourceFile;

        // Try to load from the sourceFile
        boolean myIsModifiable = false;
        boolean loaded = false;
        if(sourceFile!=null && sourceFile.canRead() && sourceFile.canWrite()) {
            try {
                InputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
            } catch(IOException err) {
                logger.log(Level.WARNING, "Unable to load properties from "+sourceFile.getAbsolutePath()+", defaulting to getResourceAsStream", err);
            }
            loaded = true;
            myIsModifiable = true;
        }
        this.isModifiable = myIsModifiable;
        // Load from resources if sourceFile inaccessible
        if(!loaded) {
            Class clazz = getClass();
            String resourceName = '/'+clazz.getName().replace('.', '/')+".properties";
            InputStream in = getClass().getResourceAsStream(resourceName);
            if(in==null) throw new RuntimeException("Resource not found: "+resourceName);
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
            if(key.endsWith(MEDIATYPE_SUFFIX)) {
                try {
                    typeMap.put(key.substring(0, key.length()-MEDIATYPE_SUFFIX.length()), MediaType.getMediaType(Locale.getDefault(), value));
                } catch(MediaException err) {
                    throw new RuntimeException(err);
                }
            } else if(key.endsWith(ISBLOCKELEMENT_SUFFIX)) {
                isBlockElementMap.put(key.substring(0, key.length()-ISBLOCKELEMENT_SUFFIX.length()), Boolean.parseBoolean(value));
            } else if(key.endsWith(VALIDATED_SUFFIX)) {
                validatedMap.put(key.substring(0, key.length()-VALIDATED_SUFFIX.length()), Long.parseLong(value));
            } else if(key.endsWith(MODIFIED_SUFFIX)) {
                modifiedMap.put(key.substring(0, key.length()-MODIFIED_SUFFIX.length()), Long.parseLong(value));
            } else {
                valueMap.put(key, value);
            }
        }
    }

    protected Object handleGetObject(String key) {
        if(key==null) throw new NullPointerException();
        return valueMap.get(key);
    }

    public Enumeration<String> getKeys() {
        ResourceBundle myParent= this.parent;
        return new ResourceBundleEnumeration(valueMap.keySet(), (myParent != null) ? myParent.getKeys() : null);
    }

    @Override
    protected Set<String> handleKeySet() {
        return valueMap.keySet();
    }

    public boolean isModifiable() {
        return isModifiable;
    }

    protected void handleSetObject(String key, Object value, boolean modified) {
        if(key.endsWith(VALIDATED_SUFFIX)) throw new RuntimeException("Key may not end with "+VALIDATED_SUFFIX+": "+key);
        if(key.endsWith(MODIFIED_SUFFIX)) throw new RuntimeException("Key may not end with "+MODIFIED_SUFFIX+": "+key);
        if(key.endsWith(MEDIATYPE_SUFFIX)) throw new RuntimeException("Key may not end with "+MEDIATYPE_SUFFIX+": "+key);
        if(key.endsWith(ISBLOCKELEMENT_SUFFIX)) throw new RuntimeException("Key may not end with "+ISBLOCKELEMENT_SUFFIX+": "+key);
        // Updates are serialized
        synchronized(properties) {
            String currentTime = Long.toString(System.currentTimeMillis());
            properties.setProperty(key, (String)value);
            properties.setProperty(key+VALIDATED_SUFFIX, currentTime);
            if(modified) properties.setProperty(key+MODIFIED_SUFFIX, currentTime);
            try {
                File tmpFile = File.createTempFile("ApplicationResources", null, sourceFile.getParentFile());
                OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile));
                try {
                    properties.store(out, null);
                } finally {
                    out.close();
                }
                if(!tmpFile.renameTo(sourceFile)) throw new IOException("Unable to rename \""+tmpFile+"\" to \""+sourceFile+'"');
            } catch(IOException err) {
                throw new RuntimeException(err);
            }
            valueMap.put(key, (String)value);
        }
    }

    protected MediaType handleGetMediaType(String key) {
        if(key==null) throw new NullPointerException();
        return typeMap.get(key);
    }

    protected Boolean handleIsBlockElement(String key) {
        if(key==null) throw new NullPointerException();
        return isBlockElementMap.get(key);
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
    protected Long getValidatedTime(String key) {
        return validatedMap.get(key);
    }

    /**
     * Provides direct read access to the modified times.
     */
    protected Long getModifiedTime(String key) {
        return modifiedMap.get(key);
    }
}
