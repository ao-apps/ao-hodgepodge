package com.aoindustries.awt;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * A label that starts out <b>bold</b>
 *
 * @version  1.0
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
