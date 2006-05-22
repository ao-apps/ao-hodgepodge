package com.aoindustries.awt;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * Matches the height of another panel while maintaining its own width
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class MatchHeightPanel extends Panel {

	private final Panel other;
public MatchHeightPanel(Panel other, LayoutManager layout) {
	super(layout);
	this.other=other;
}
public Dimension getPreferredSize() {
	return new Dimension(
		super.getPreferredSize().width,
		other.getPreferredSize().height
	);
}
}
