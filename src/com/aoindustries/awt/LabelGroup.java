package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.*;

/**
 * Stores a list of Labels that will all act as one for the preferred size
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class LabelGroup extends Vector<GroupLabel> {
public void addLabel(GroupLabel L) {
	addElement(L);
}
/**
 * The width of a label group is the width of its widest member.
 */
synchronized public int getWidth() {
	int widest=0;
	int size=size();
	for(int c=0;c<size;c++) {
		int width=((GroupLabel)elementAt(c)).getActualPreferredSize().width;
		if(width>widest) widest=width;
	}
	return widest;
}
}
