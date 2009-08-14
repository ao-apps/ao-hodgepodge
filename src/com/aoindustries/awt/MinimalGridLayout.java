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
 * @author  AO Industries, Inc.
 */
public class MinimalGridLayout extends GridLayout{

/**
 * MinimalGridLayout constructor comment.
 */
public MinimalGridLayout() {
	super();
}
/**
 * MinimalGridLayout constructor comment.
 * @param rows int
 * @param cols int
 */
public MinimalGridLayout(int rows, int cols) {
	super(rows, cols);
}
/**
 * MinimalGridLayout constructor comment.
 * @param rows int
 * @param cols int
 * @param hgap int
 * @param vgap int
 */
public MinimalGridLayout(int rows, int cols, int hgap, int vgap) {
	super(rows, cols, hgap, vgap);
}
public void layoutContainer(Container parent) {
	synchronized (parent.getTreeLock()) {
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		int nrows = getRows();
		int ncols = getColumns();
		int hgap = getHgap();
		int vgap = getVgap();

		if (ncomponents == 0) {
			return;
		}
		if (nrows > 0) {
			ncols = (ncomponents + nrows - 1) / nrows;
		} else {
			nrows = (ncomponents + ncols - 1) / ncols;
		}

		int[] widths = new int[ncols];
		int[] heights = new int[nrows];
		int currComp = 0;
		//calculate the minimal dimensions of each row and column
		for (int y=0;y<nrows;y++) {
			for (int x=0;x<ncols;x++) {
				if (currComp<ncomponents) {
					Dimension d = parent.getComponent(currComp).getPreferredSize();
					int currWidth = d.width;
					int currHeight = d.height;
					
					if (currWidth > widths[x]) widths[x]=currWidth;
					if (currHeight > heights[y]) heights[y]=currHeight;
					currComp++;
				}
			}
		}
		// normalize (stretch) those minimal values to fill container
		int minWidth = 0;
		for (int x=0;x<widths.length;x++) {
			minWidth += widths[x];
		}
		
		int minHeight = 0;
		for (int y=0;y<heights.length;y++) {
			minHeight += heights[y];
		}

		Dimension d = parent.getSize();
		int pWidth = d.width - insets.left - insets.right - (hgap*(widths.length-1));
		int pHeight = d.height - insets.top - insets.bottom - (vgap*(heights.length-1));
		
		for (int x=0;x<widths.length;x++) {
			widths[x]=widths[x]*pWidth/minWidth;
		}
		for (int y=0;y<heights.length;y++) {
			heights[y]=heights[y]*pHeight/minHeight;
		}

		// set the bounds for the components
		int x = insets.left;
		
		for (int c = 0; c < ncols ; c++) {
			int y = insets.top;
		    for (int r = 0; r < nrows ; r++) {
				int i = r * ncols + c;
				if (i < ncomponents) {
				    parent.getComponent(i).setBounds(x, y, widths[c], heights[r]);
				}
				y += heights[r] + vgap;
		    }
		    x += widths[c] + hgap;
		    
		}
		
	}
}
/**
 * Determines the minimum size of the container argument using this 
 * grid layout. 
 * <p>
 * The minimum width of a grid layout is the largest minimum width 
 * of any of the widths in the container times the number of columns, 
 * plus the horizontal padding times the number of columns plus one, 
 * plus the left and right insets of the target container. 
 * <p>
 * The minimum height of a grid layout is the largest minimum height 
 * of any of the heights in the container times the number of rows, 
 * plus the vertical padding times the number of rows plus one, plus 
 * the top and bottom insets of the target container. 
 *  
 * @param       parent   the container in which to do the layout.
 * @return      the minimum dimensions needed to lay out the 
 *                      subcomponents of the specified container.
 * @see         java.awt.GridLayout#preferredLayoutSize
 * @see         java.awt.Container#doLayout
 */

public Dimension minimumLayoutSize(Container parent) {
	synchronized (parent.getTreeLock()) {
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		int nrows = getRows();
		int ncols = getColumns();
		int hgap = getHgap();
		int vgap = getVgap();

		if (nrows > 0) {
			ncols = (ncomponents + nrows - 1) / nrows;
		} else {
			nrows = (ncomponents + ncols - 1) / ncols;
		}
		int w = 0;
		int h = 0;
		for (int i = 0; i < ncomponents; i++) {
			Component comp = parent.getComponent(i);
			Dimension d = comp.getMinimumSize();
			if (w < d.width) {
				w = d.width;
			}
			if (h < d.height) {
				h = d.height;
			}
		}
		return new Dimension(
			insets.left + insets.right + ncols * w + (ncols - 1) * hgap,
			insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
	}
}
/** 
 * Determines the preferred size of the container argument using 
 * this grid layout. 
 * <p>
 * The preferred width of a grid layout is the largest preferred 
 * width of any of the widths in the container times the number of 
 * columns, plus the horizontal padding times the number of columns 
 * plus one, plus the left and right insets of the target container. 
 * <p>
 * The preferred height of a grid layout is the largest preferred 
 * height of any of the heights in the container times the number of 
 * rows, plus the vertical padding times the number of rows plus one, 
 * plus the top and bottom insets of the target container. 
 * 
 * @param     parent  the container in which to do the layout.
 * @return    the preferred dimensions to lay out the 
 *                      subcomponents of the specified container.
 * @see       java.awt.GridLayout#minimumLayoutSize 
 * @see       java.awt.Container#getPreferredSize()
 */
public Dimension preferredLayoutSize(Container parent) {
	synchronized (parent.getTreeLock()) {
		Insets insets = parent.getInsets();
		int ncomponents = parent.getComponentCount();
		int nrows = getRows();
		int ncols = getColumns();
		int hgap = getHgap();
		int vgap = getVgap();

		if (nrows > 0) {
			ncols = (ncomponents + nrows - 1) / nrows;
		} else {
			nrows = (ncomponents + ncols - 1) / ncols;
		}

		int[] widths = new int[ncols];
		int[] heights = new int[nrows];
		int currComp = 0;
		
		for (int y=0;y<nrows;y++) {
			
			for (int x=0;x<ncols;x++) {
				
				if (currComp<ncomponents) {
					Dimension d = parent.getComponent(currComp).getPreferredSize();
					int currWidth = d.width;
					int currHeight = d.height;
					if (currWidth > widths[x]) widths[x]=currWidth;
					if (currHeight > heights[y]) heights[y]=currHeight;
					currComp++;
				}
			}
		}

		int totalWidth = insets.left + insets.right + (ncols - 1) * hgap;
		int totalHeight = insets.top + insets.bottom + (nrows - 1) * vgap;

		for (int x = 0;x<ncols;x++) {
			totalWidth += widths[x];
		}
		
		for (int y = 0;y<nrows;y++) {
			totalHeight += heights[y];
		}
			
		return new Dimension(totalWidth, totalHeight);
	}	
}
}
