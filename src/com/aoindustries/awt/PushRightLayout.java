package com.aoindustries.awt;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * Layouts out starting from top to bottom in multiple columns.
 * The first column is as wide as possible, each of the remaining
 * columns is as wide as its widest component and are laid out to
 * be center-aligned.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class PushRightLayout implements LayoutManager {

	private final int columns;

public PushRightLayout(int columns) {
	this.columns=columns;
}
public void addLayoutComponent(String place, Component component) {
}
public void layoutContainer(Container parent) {
	synchronized(parent.getTreeLock()) {

		int ncomponents=parent.getComponentCount();
		if(ncomponents>=1) {
			Dimension size=parent.getSize();

			// Figure out widths
			int[] columnWidths=new int[columns];
			int rows=ncomponents/columns;
			int pos=0;
			for(int row=0;row<rows;row++) {
				pos++;
				for(int c=1;c<columns;c++) {
					Dimension D=parent.getComponent(pos++).getPreferredSize();
					if(D.width>columnWidths[c]) columnWidths[c]=D.width;
				}
			}
			// Column 0 is the remaining space
			Insets insets=parent.getInsets();
			int left=insets.left;
			int totalWidth=0;
			for(int c=1;c<columns;c++) totalWidth+=columnWidths[c];
			columnWidths[0]=size.width-left-totalWidth-insets.right;

			// Make a pass, top to bottom
			pos=0;
			int y=insets.top;
			for(int row=0;row<rows;row++) {
				// Figure out how tall to make this one
				int highest=0;
				for(int c=0;c<columns;c++) {
					int height=parent.getComponent(pos+c).getPreferredSize().height;
					if(height>highest) highest=height;
				}
				// Lay out the row
				int x=left;
				for(int c=0;c<columns;c++) {
					int width=columnWidths[c];
					// Take the full width for the first component
					if(c==0) parent.getComponent(pos++).setBounds(x, y, width, highest);
					// Center the remaining components
					else {
						Component C=parent.getComponent(pos++);
						int prefWidth=C.getPreferredSize().width;
						C.setBounds(x+(width-prefWidth)/2, y, prefWidth, highest);
					}
					x+=width;
				}
				y+=highest;
			}
		}
	}
}
public Dimension minimumLayoutSize(Container parent) {
	synchronized (parent.getTreeLock()){
		int totalHeight=0;
		int[] columnWidths=new int[columns];

		int ncomponents=parent.getComponentCount();
		if(ncomponents>=1) {
			int rows=ncomponents/columns;
			int pos=0;
			for(int row=0;row<rows;row++) {
				int highest=0;
				for(int c=0;c<columns;c++) {
					Dimension D=parent.getComponent(pos++).getMinimumSize();
					if(D.width>columnWidths[c]) columnWidths[c]=D.width;
					if(D.height>highest) highest=D.height;
				}
				totalHeight+=highest;
			}
		}

		int totalWidth=0;
		for(int c=0;c<columns;c++) totalWidth+=columnWidths[c];

		Insets insets=parent.getInsets();
		return new Dimension(
			insets.left + totalWidth + insets.right,
			insets.top + totalHeight + insets.bottom
		);
	}
}
public Dimension preferredLayoutSize(Container parent) {
	synchronized(parent.getTreeLock()) {
		int totalHeight=0;
		int[] columnWidths=new int[columns];

		int ncomponents=parent.getComponentCount();
		if(ncomponents>=1) {
			int rows=ncomponents/columns;
			int pos=0;
			for(int row=0;row<rows;row++) {
				int highest=0;
				for(int c=0;c<columns;c++) {
					Dimension D=parent.getComponent(pos++).getPreferredSize();
					if(D.width>columnWidths[c]) columnWidths[c]=D.width;
					if(D.height>highest) highest=D.height;
				}
				totalHeight+=highest;
			}
		}

		int totalWidth=0;
		for(int c=0;c<columns;c++) totalWidth+=columnWidths[c];

		Insets insets=parent.getInsets();
		return new Dimension(
			insets.left + totalWidth + insets.right,
			insets.top + totalHeight + insets.bottom
		);
	}
}
public void removeLayoutComponent(Component component) {
}
}
