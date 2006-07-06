package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class Blank extends Canvas {
	public int width;
	public int height;
public Blank(int width,int height) {
	this.width=width;
	this.height=height;
}
public Blank(int width, int height, Color color) {
	this(width, height);
	setBackground(color);
}
public Dimension getPreferredSize() {
	return new Dimension(width,height);
}
}
