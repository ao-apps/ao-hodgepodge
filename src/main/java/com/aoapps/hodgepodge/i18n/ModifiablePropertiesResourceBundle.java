/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.i18n;

import com.aoapps.hodgepodge.util.CommentCaptureInputStream;
import com.aoapps.hodgepodge.util.DiffableProperties;
import com.aoapps.hodgepodge.util.SkipCommentsFilterOutputStream;
import com.aoapps.lang.LocalizedIllegalStateException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.FileUtils;
import com.aoapps.lang.io.LocalizedIOException;
import com.aoapps.tempfiles.TempFile;
import com.aoapps.tempfiles.TempFileContext;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * or locale.  For instance, class <code>com.aoapps.hodgepodge.swing.ApplicationResources_ja</code>
 * would load its properties from <code>com/aoapps/hodgepodge/swing/ApplicationResources_ja.properties</code>.
 * </p>
 * <p>
 * Idea: Occasionally check sourceFile and reload properties if externally modified.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class ModifiablePropertiesResourceBundle extends ModifiableResourceBundle {

	private static final Logger logger = Logger.getLogger(ModifiablePropertiesResourceBundle.class.getName());

	private static final Resources RESOURCES = Resources.getResources(ModifiablePropertiesResourceBundle.class, ResourceBundle::getBundle);

	private static final Charset propertiesCharset = StandardCharsets.ISO_8859_1;

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
	private final Map<String, String> valueMap = new ConcurrentHashMap<>();

	/**
	 * All validated queries are performed on this concurrent map.
	 */
	private final Map<String, Long> validatedMap = new ConcurrentHashMap<>();

	/**
	 * All modified queries are performed on this concurrent map.
	 */
	private final Map<String, Long> modifiedMap = new ConcurrentHashMap<>();

	/**
	 * The properties file is only used for updates.
	 */
	private final Properties properties = new Properties();

	/**
	 * @param sourceFiles The source file(s).  If multiple source files are provided,
	 *                    only one may exist and be both readable and writable.  If more than
	 *                    one possible source file exists, will throw an IllegalStateException.
	 */
	public ModifiablePropertiesResourceBundle(File... sourceFiles) {
		File goodSourceFile = null;
		if(sourceFiles!=null) {
			for(File file : sourceFiles) {
				try {
					if(file.canRead() && file.canWrite()) {
						if(goodSourceFile != null) {
							throw new LocalizedIllegalStateException(
								RESOURCES,
								"init.moreThanOneSourceFile",
								goodSourceFile,
								file
							);
						}
						goodSourceFile = file;
					}
				} catch(SecurityException err) {
					// OK when sandboxed, goodSourceFile remains null
					logger.log(
						Level.WARNING,
						RESOURCES.getMessage("init.securityException", file),
						err
					);
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
					RESOURCES.getMessage("init.ioException", goodSourceFile),
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
			String resourceName = clazz.getName().replace('.', '/') + ".properties";
			InputStream in = getClass().getResourceAsStream("/" + resourceName);
			if(in == null) {
				// Try ClassLoader for when modules enabled
				ClassLoader classloader = Thread.currentThread().getContextClassLoader();
				in = (classloader != null)
					? classloader.getResourceAsStream(resourceName)
					: ClassLoader.getSystemResourceAsStream(resourceName);
			}
			if(in == null) throw new UncheckedIOException(new LocalizedIOException(RESOURCES, "init.resourceNotFound", resourceName));
			try {
				try {
					properties.load(in);
				} finally {
					in.close();
				}
			} catch(IOException err) {
				throw new UncheckedIOException(err);
			}
		}
		// Populate the concurrent maps while skipping the validated and modified entries
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
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

	// See sun.util.ResourceBundleEnumeration
	@Override
	public Enumeration<String> getKeys() {
		ResourceBundle myParent= this.parent;
		Set<String> set = valueMap.keySet();
		Enumeration<String> enumeration = (myParent != null) ? myParent.getKeys() : null;
		return new Enumeration<String>() {

			private final Iterator<String> iterator = set.iterator();
			private String next = null;

			@Override
			public boolean hasMoreElements() {
				if (next == null) {
					if (iterator.hasNext()) {
						next = iterator.next();
					} else if (enumeration != null) {
						while (next == null && enumeration.hasMoreElements()) {
							next = enumeration.nextElement();
							if (set.contains(next)) {
								next = null;
							}
						}
					}
				}
				return next != null;
			}

			@Override
			public String nextElement() {
				if (hasMoreElements()) {
					String result = next;
					next = null;
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}
		};
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

	public static final Comparator<Object> PROPERTIES_KEY_COMPARATOR = new Comparator<Object>() {

		private final Collator collator = Collator.getInstance(Locale.ROOT);

		@Override
		public int compare(Object o1, Object o2) {
			String s1 = (String)o1;
			String base1;
			int sub1;
			if(s1.endsWith(MODIFIED_SUFFIX)) {
				base1 = s1.substring(0, s1.length() - MODIFIED_SUFFIX.length());
				sub1 = 1;
			} else if(s1.endsWith(VALIDATED_SUFFIX)) {
				base1 = s1.substring(0, s1.length() - VALIDATED_SUFFIX.length());
				sub1 = 2;
			} else {
				base1 = s1;
				sub1 = 0;
			}
			String s2 = (String)o2;
			String base2;
			int sub2;
			if(s2.endsWith(MODIFIED_SUFFIX)) {
				base2 = s2.substring(0, s2.length() - MODIFIED_SUFFIX.length());
				sub2 = 1;
			} else if(s2.endsWith(VALIDATED_SUFFIX)) {
				base2 = s2.substring(0, s2.length() - VALIDATED_SUFFIX.length());
				sub2 = 2;
			} else {
				base2 = s2;
				sub2 = 0;
			}
			int diff = collator.compare(base1, base2);
			if(diff != 0) return diff;
			return Integer.compare(sub1, sub2);
		}
	};

	/**
	 * Saves the properties file in ascending key order.  All accesses must
	 * already hold a lock on the properties object.
	 */
	private void saveProperties() {
		assert Thread.holdsLock(properties);
		try {
			// Create a properties instance that sorts the output by keys (case-insensitive)
			@SuppressWarnings("deprecation")
			com.aoapps.collections.SortedProperties writer = new com.aoapps.collections.SortedProperties() {
				@Override
				public Comparator<Object> getKeyComparator() {
					return PROPERTIES_KEY_COMPARATOR;
				}
			};
			writer.putAll(properties);
			// Generate new file
			byte[] newContent;
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				// Write any comments from when file was read
				if(sourceFileComments != null) {
					for(String line : sourceFileComments) {
						out.write(line.getBytes(propertiesCharset));
						out.write('\n');
					}
				}
				// Java 9: Support UTF-8 properties files via reader/writer
				writer.store(
					// Wrap to skip any comments generated by Properties code
					new SkipCommentsFilterOutputStream(out),
					null
				);
				newContent = DiffableProperties.formatProperties(out.toString(propertiesCharset.name())).getBytes(propertiesCharset);
			}
			if(!sourceFile.exists() || !FileUtils.contentEquals(sourceFile, newContent)) {
				try (
					TempFileContext tempFileContext = new TempFileContext(sourceFile.getParentFile());
					TempFile tempFile = tempFileContext.createTempFile(sourceFile.getName())
				) {
					try (OutputStream out = new FileOutputStream(tempFile.getFile())) {
						out.write(newContent);
					}
					FileUtils.renameAllowNonAtomic(tempFile.getFile(), sourceFile);
				}
			}
		} catch(IOException err) {
			throw new UncheckedIOException(err);
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
