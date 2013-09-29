/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.text.MessageFormatFactory;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The lookup in a resource bundle may be delayed by encapsulating the lookup
 * and calling toString on this object.
 *
 * @author  AO Industries, Inc.
 */
public class BundleLookup {

	/**
	 * The different type of markups allowed for translation integration.
	 */
	public enum MarkupType {
		NONE,
		XHTML,
		TEXT,
		JAVASCRIPT
	}

	private final String baseName;
	private final Locale locale;
	private final String key;
	private final Object[] args;

    public BundleLookup(
		String baseName,
		Locale locale,
		String key
	) {
		this.baseName = baseName;
		this.locale = locale;
		this.key = key;
		this.args = null;
	}

    public BundleLookup(
		String baseName,
		Locale locale,
		String key,
		Object... args
	) {
		this.baseName = baseName;
		this.key = key;
		this.locale = locale;
		this.args = args;
	}

	/**
	 * Performs the lookup on <code>toString()</code>.
	 */
	@Override
	public String toString() {
        String string = null;
        try {
			string = ResourceBundle.getBundle(baseName, locale).getString(key);
        } catch(MissingResourceException err) {
            // string remains null
        }
        if(string==null) return "???"+locale.toString()+"."+key+"???";
        if(args==null || args.length==0) return string;
        return MessageFormatFactory.getMessageFormat(string, locale).format(args, new StringBuffer(string.length()<<1), null).toString();
	}

	/**
	 * Uses the prefix and suffix of wrapped, but a new result (after argument substitution)
	 */
	private static class WrappedBundleLookupResult implements BundleLookupResult {
	
		private final BundleLookupResult wrapped;
		private final String newResult;
		
		private WrappedBundleLookupResult(BundleLookupResult wrapped, String newResult) {
			this.wrapped = wrapped;
			this.newResult = newResult;
		}

		@Override
		public void appendPrefixTo(Appendable out) throws IOException {
			wrapped.appendPrefixTo(out);
		}

		@Override
		public void appendPrefixTo(MediaEncoder encoder, Appendable out) throws IOException {
			wrapped.appendPrefixTo(encoder, out);
		}

		@Override
		public String getResult() {
			return newResult;
		}

		@Override
		public void appendSuffixTo(Appendable out) throws IOException {
			wrapped.appendSuffixTo(out);
		}

		@Override
		public void appendSuffixTo(MediaEncoder encoder, Appendable out) throws IOException {
			wrapped.appendSuffixTo(encoder, out);
		}
	}

	/**
	 * Performs the lookup on <code>toString()</code>
	 * while allowing possible prefixes and suffixes for the given context type.
	 * This is used as a hook for in-context translation editors.
	 *
	 * @param  markupType  the type of prefix and suffix markup allowed
	 */
	public BundleLookupResult toString(MarkupType markupType) {
		String string = null;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
			if(bundle instanceof EditableResourceBundle) {
				EditableResourceBundle editableBundle = (EditableResourceBundle)bundle;
				BundleLookupResult result = editableBundle.getString(key, markupType);
				if(args==null || args.length==0) {
					return result;
				} else {
					String oldText = result.getResult();
					String newText = MessageFormatFactory.getMessageFormat(oldText, locale).format(args, new StringBuffer(oldText.length()<<1), null).toString();
					if(newText.equals(oldText)) return result;
					return new WrappedBundleLookupResult(
						result,
						newText
					);
				}
			} else {
				string = bundle.getString(key);
			}
		} catch(MissingResourceException err) {
			// string remains null
		}
		if(string==null) {
			return new StringBundleLookupResult("???"+locale.toString()+"."+key+"???");
		}
		if(args==null || args.length==0) {
			return new StringBundleLookupResult(string);
		}
		return new StringBundleLookupResult(
			MessageFormatFactory.getMessageFormat(string, locale).format(args, new StringBuffer(string.length()<<1), null).toString()
		);
	}

	public String getBaseName() {
		return baseName;
	}
	
	public String getKey() {
		return key;
	}
	
	public Locale getLocale() {
		return locale;
	}
}
