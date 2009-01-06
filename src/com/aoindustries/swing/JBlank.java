package com.aoindustries.swing;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class JBlank extends JComponent {

	private int width, height;

/**
 * Constructs an empty DefaultComboBoxModel object.
 */
public JBlank() {
	this(1, 1);
}
/**
 * Constructs an empty DefaultComboBoxModel object.
 */
public JBlank(int width, int height) {
	this.width=width;
	this.height=height;
}
public Dimension getPreferredSize() {
	return new Dimension(width,height);
}
}
