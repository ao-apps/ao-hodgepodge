package com.aoindustries.awt;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class GroupLabel extends Label {

	private final LabelGroup group;
public GroupLabel(String text, int alignment, LabelGroup group) {
	super(text, alignment);
	this.group=group;
	group.addLabel(this);
}
public GroupLabel(String text, LabelGroup group) {
	super(text);
	this.group=group;
	group.addLabel(this);
}
public Dimension getActualPreferredSize() {
	return super.getPreferredSize();
}
public Dimension getPreferredSize() {
	return new Dimension(
		group.getWidth(),
		super.getPreferredSize().height
	);
}
}
