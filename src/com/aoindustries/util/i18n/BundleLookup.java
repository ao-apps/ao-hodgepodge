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

import com.aoindustries.text.MessageFormatFactory;
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
