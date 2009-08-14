/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
 * Layouts out starting from top to bottom in multiple columns.
 * The first column is as wide as possible, each of the remaining
 * columns is as wide as its widest component and are laid out to
 * be center-aligned.
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
