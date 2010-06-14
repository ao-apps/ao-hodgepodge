/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.awt;

import java.awt.*;

/**
 * A label that starts out <b>bold</b>
 *
 * @author  AO Industries, Inc.
 */
public class BoldLabel extends Label {

	/** The default <code>Font</code> name */
	public static final String DEFAULT_FONT_NAME="SansSerif";

	/** The default <code>Font</code> size */
	public static final int DEFAULT_FONT_SIZE=11;

	/** The default <code>Font</code> to be used */
	public static final Font defaultFont=new Font(DEFAULT_FONT_NAME,Font.PLAIN,DEFAULT_FONT_SIZE);

	/** The default <code>Font</code> to be used whenever a <b>bold</b> font is needed */
	public static final Font defaultBoldFont=new Font(DEFAULT_FONT_NAME,Font.BOLD,DEFAULT_FONT_SIZE);

/**
 * Construct and make <b>bold</b>
 */
public BoldLabel() {
	super();
	init();
}
public BoldLabel(String S) {
	super(S);
	init();
}
public BoldLabel(String S, int align) {
	super(S, align);
	init();
}
protected void init() {
	setFont(defaultBoldFont);
}
}
