package com.aoindustries.awt;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
