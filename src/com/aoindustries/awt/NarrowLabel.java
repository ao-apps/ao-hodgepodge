package com.aoindustries.awt;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * A label that has all of the extra whitespace to the left and right removed.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class NarrowLabel extends Label {

public NarrowLabel() {
	super();
}
public NarrowLabel(String S) {
	super(S);
}
public NarrowLabel(String S, int align) {
	super(S, align);
}
public Dimension getPreferredSize() {
	Dimension D = super.getPreferredSize();
	Font font = getFont();
	if (font != null) {
		FontMetrics FM = getFontMetrics(font);
		int width = FM.stringWidth(getText());
		return new Dimension(width, D.height);
	} else
		return D;
}
}
