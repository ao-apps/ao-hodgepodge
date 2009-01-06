package com.aoindustries.awt;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.io.*;
import java.util.*;

/**
 * Gets color integers provided color names.  Supports the standard Unix
 * colors as found in rgb.txt, hex colors starting with 0x, hex colors
 * starting with #, or hex colors on their own.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class RGBColor {

    /**
     * A hash of color names and values, all stored lowercase.  The data will be populated on the
     * first call to <code>getColor</code>.
     */
    private static Map<String,Integer> colors;

    /**
     * Make no instances.
     */
    private RGBColor() {}

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
    public synchronized static int getColor(String name) throws IOException, IllegalArgumentException {
	if (name == null) throw new NullPointerException("name is null");
	name = name.trim();
	if (name.length() == 0) throw new IllegalArgumentException("name is empty");

	// Load the colors if not already done
	if (colors == null) {
            // Load the colors
            colors = new HashMap<String,Integer>();

            // Try to read the RGB file
            BufferedReader in = new BufferedReader(new InputStreamReader(RGBColor.class.getResourceAsStream("rgb.txt")));
            try {
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
                                    Integer.valueOf(
                                        (Integer.parseInt(line.substring(0, 3).trim()) << 16)
                                        | (Integer.parseInt(line.substring(4, 7).trim()) << 8)
                                        | Integer.parseInt(line.substring(8, 11).trim())
                                    )
                                );
                            } catch (NumberFormatException err) {
                                IOException ioErr=new IOException("Unable to parse line: "+line);
                                ioErr.initCause(err);
                                throw ioErr;
                            }
                        }
                    }
                }
            } finally {
                in.close();
            }
	}

	// Look in the color hash first
	name = name.toLowerCase();
	Integer I = colors.get(name);
	if (I != null) return I.intValue();

	if (name.length() >= 1 && name.charAt(0) == '#') name = name.substring(1);
	else if (name.length() >= 2 && name.charAt(0) == '0' && name.charAt(1) == 'x') name = name.substring(2);

	if (name.length() != 6) throw new IllegalArgumentException("name should be 6 digits, name is " + name.length() + " digits");

	// Get the number
	int color = 0;
	for (int c = 0; c < 6; c++) {
            char ch = name.charAt(c);
            int value;
            if (ch >= '0' && ch < '9') value = ch - '0';
            else if (ch >= 'a' && ch <= 'f') value = ch - 'a' + 10;
            else throw new IllegalArgumentException("Invalid character in name: " + ch);
            color |= (value << ((5 - c) << 2));
	}

	return color;
    }
}