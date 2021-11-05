/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2021  AO Industries, Inc.
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
package com.aoapps.hodgepodge.awt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Gets color integers provided color names.  Supports the standard Unix
 * colors as found in rgb.txt, hex colors starting with 0x, hex colors
 * starting with #, or hex colors on their own.
 *
 * @author  AO Industries, Inc.
 */
public abstract class RGBColor {

	/** Make no instances. */
	private RGBColor() {throw new AssertionError();}

	/**
	 * A hash of color names and values, all stored lowercase.  The data will be populated on the
	 * first call to <code>getColor</code>.
	 */
	private static Map<String, Integer> colors;

	/**
	 * Gets color integers provided color names.  Supports the standard Unix
	 * colors as found in rgb.txt, hex colors starting with 0x, hex colors
	 * starting with #, or hex colors on their own.
	 *
	 * @param  name  the name of the color or a hex value
	 *
	 * @return  an <code>int</code> in 8 bit RGB format
	 *
	 * @exception  NullPointerException      if <code>name</code> is <code>null</code>
	 * @exception  IllegalArgumentException  if unable to determine the color
	 */
	public static synchronized int getColor(String name) throws IOException, IllegalArgumentException {
		if (name == null) throw new NullPointerException("name is null");
		name = name.trim();
		if (name.length() == 0) throw new IllegalArgumentException("name is empty");

		// Load the colors if not already done
		if (colors == null) {
			// Load the colors
			colors = new HashMap<>();
			try (BufferedReader in = new BufferedReader(new InputStreamReader(RGBColor.class.getResourceAsStream("rgb.txt")))) {
				String line;
				while ((line = in.readLine()) != null) {
					int len = line.length();
					if (len > 13) {
						// Ignore comments
						int ch = line.charAt(0);
						if (ch != '!' && ch != '#') {
							try {
								colors.put(
									line.substring(13).trim(),
									(Integer.parseInt(line.substring(0, 3).trim()) << 16)
									| (Integer.parseInt(line.substring(4, 7).trim()) << 8)
									| Integer.parseInt(line.substring(8, 11).trim())
								);
							} catch (NumberFormatException err) {
								throw new IOException("Unable to parse line: "+line, err);
							}
						}
					}
				}
			}
		}

		// Look in the color hash first
		name = name.toLowerCase(Locale.ROOT);
		Integer cached = colors.get(name);
		if (cached != null) return cached;

		int start = 0;
		if (name.length() >= 1 && name.charAt(0) == '#') start = 1;
		else if (name.length() >= 2 && name.charAt(0) == '0' && name.charAt(1) == 'x') start = 2;

		int nameLen = name.length() - start;
		if (nameLen != 6) throw new IllegalArgumentException("name should be 6 digits, name is " + nameLen + " digits");

		// Get the number
		int color = 0;
		for(
			int i = start, end=start+6;
			i < end;
			i++
		) {
			char ch = name.charAt(i);
			int value;
			if (ch >= '0' && ch < '9') value = ch - '0';
			else if (ch >= 'a' && ch <= 'f') value = ch - 'a' + 10;
			else throw new IllegalArgumentException("Invalid character in name: " + ch);
			color |= (value << ((5 - i) << 2));
		}

		return color;
	}
}
