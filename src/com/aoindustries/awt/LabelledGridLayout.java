/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Used to layout components in a grid.  Each element in the
 * grid contains two components.  The borders between these
 * two components are vertically aligned.
 *
 * @author  AO Industries, Inc.
 */
public class LabelledGridLayout implements LayoutManager {

    public final int rows, columns;
    
    public final int hgap, vgap, cgap;
    
    public final boolean stretchComponents;

    public LabelledGridLayout(int rows, int columns) {
        this(rows, columns, 0, 0, 0, true);
    }

    /**
     * @param  rows     the number of rows
     * @param  columns  the number of columns, each column is two components wide
     * @param  hgap     the space between columns
     * @param  vgap     the space between rows
     * @param  cgap     the space between the two components in a column
     */
    public LabelledGridLayout(int rows, int columns, int hgap, int vgap, int cgap) {
        this.rows=rows;
        this.columns=columns;
        this.hgap=hgap;
        this.vgap=vgap;
        this.cgap=cgap;
        this.stretchComponents = true;
    }

    /**
     * @param  rows     the number of rows
     * @param  columns  the number of columns, each column is two components wide
     * @param  hgap     the space between columns
     * @param  vgap     the space between rows
     * @param  cgap     the space between the two components in a column
     */
    public LabelledGridLayout(int rows, int columns, int hgap, int vgap, int cgap, boolean stretchComponents) {
        this.rows=rows;
        this.columns=columns;
        this.hgap=hgap;
        this.vgap=vgap;
        this.cgap=cgap;
        this.stretchComponents = stretchComponents;
    }

	@Override
    public void addLayoutComponent(String place, Component component) {
    }

    private Dimension getLayoutSize(Container parent, boolean isMinimum) {
        synchronized (parent.getTreeLock()){
            Insets insets=parent.getInsets();
            int ncomponents=parent.getComponentCount();
            int nrows=rows;
            int ncols=columns;
            if(nrows>0) {
                ncols=((ncomponents/2)+nrows-1)/nrows;
            } else {
                nrows=((ncomponents/2)+ncols-1)/ncols;
            }
            int[] widths=new int[ncols*2];
            int totalHeight=0;
            int highest=0;
            int pos=0;
            Loop:
                for(int y=0;y<nrows;y++) {
                    if(!stretchComponents) highest=0;
                    for(int x=0;x<(ncols*2);x++) {
                        if(pos>=ncomponents) break Loop;
                        Component component=parent.getComponent(pos++);
                        Dimension d=isMinimum?component.getMinimumSize():component.getPreferredSize();
                        if(d.width>widths[x]) widths[x]=d.width;
                        if(d.height>highest) highest=d.height;
                    }
                    if(!stretchComponents) totalHeight+=highest;
                }
                // Find the widest sum of label and component
                int widest=0;
                for(int x=0;x<ncols;x++) {
                    int width=widths[x*2]+widths[x*2+1];
                    if(width>widest) widest=width;
                }
                return new Dimension(
                    insets.left + ncols*widest + insets.right + hgap*(columns-1) + cgap*columns,
                    insets.top + (stretchComponents?nrows*highest:totalHeight) + insets.bottom + vgap*(rows-1)
                );
        }
    }

    /**
     * Fix the layout when stretchComponenets==false
     * and the number of components is not evenly divisible
     * by the number of cols or rows.
     */
	@Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets=parent.getInsets();
            int ncomponents=parent.getComponentCount();
            if(ncomponents==0) return;
            
            int nrows=rows;
            int ncols=columns;
            if(nrows>0) {
                ncols=((ncomponents/2)+nrows-1)/nrows;
            } else {
                nrows=((ncomponents/2)+ncols-1)/ncols;
            }
            
            // Determine the desired widths for the labels
            int[] labelWidths=new int[ncols];
            int pos=0;
        Loop1:
            for(int y=0;y<nrows;y++) {
                for(int x=0;x<ncols;x++) {
                    if(pos>=ncomponents) break Loop1;
                    Component component=parent.getComponent(pos);
                    pos+=2;
                    Dimension d=component.getPreferredSize();
                    if(d.width>labelWidths[x]) labelWidths[x]=d.width;
                }
            }
            Dimension parentSize=parent.getSize();
            int width=parentSize.width-(insets.left+insets.right);
            int height=parentSize.height-(insets.top+insets.bottom);
            int lasty=0;
            pos=0;
        Loop:
            for(int y=0;y<nrows;y++) {
                int yend=0;
                int cellHeight=0;
                if (stretchComponents) {
                    yend=((y+1)*(height+vgap))/nrows-vgap;
                    cellHeight=yend-lasty;
                }
                int lastx=0;
                for(int x=0;x<ncols;x++) {
                    if(pos>=ncomponents) break Loop;
                    int xend=((x+1)*(width+hgap))/ncols-hgap;
                    int cellWidth=xend-lastx;
                    // Split this width into two areas, left for label and right for component
                    int availableWidth=labelWidths[x];
                    if(availableWidth>cellWidth) availableWidth=cellWidth;
                    // Reshape the label to fit
                    Component label=parent.getComponent(pos++);
                    Dimension labelD=label.getPreferredSize();
                    Component component=parent.getComponent(pos++);
                    Dimension componentD=component.getPreferredSize();
                    if (!stretchComponents) {
                        cellHeight = Math.max(labelD.height, componentD.height);
                        if((lasty+cellHeight)>yend) yend=lasty+cellHeight;
                        // yend = lasty+cellHeight;
                    }

                    //int actualWidth=labelD.width;
                    //if(actualWidth>availableWidth) actualWidth=availableWidth;
                    label.setBounds(
                        lastx+insets.left,
                        lasty+insets.top,
                        availableWidth,
                        cellHeight
                    );
                    // Split out the right area
                    if(pos>ncomponents) break Loop;
                    availableWidth=cellWidth-cgap-labelWidths[x];
                    if(availableWidth<0) availableWidth=0;
                    // Reshape the component
                    int actualWidth=componentD.width;
                    if(actualWidth>availableWidth) actualWidth=availableWidth;
                    component.setBounds(
                        lastx+labelWidths[x]+cgap+insets.left,
                        lasty+insets.top,
                        actualWidth,
                        cellHeight /*Klay--8/22/01*/
                    );
                    // Get ready for next iteration
                    lastx=xend+hgap;
                }
                lasty=yend+vgap;
            }
        }
    }

	@Override
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent, true);
    }

	@Override
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent, false);
    }

	@Override
    public void removeLayoutComponent(Component component) {
    }
}
