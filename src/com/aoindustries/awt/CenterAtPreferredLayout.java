package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * Layout manager for three components, the first is placed to
 * the left, the second in the center, and the third on the right.
 * The center component is laid out to its preferred size while
 * the outer two split the remaining space.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class CenterAtPreferredLayout implements LayoutManager {
public void addLayoutComponent(String place, Component component) {
}
public void layoutContainer(Container parent) {
	synchronized(parent.getTreeLock()) {
		Insets insets=parent.getInsets();
		int ncomponents=parent.getComponentCount();
		if(ncomponents==0) return;

		// Determine the available space
		Dimension D=parent.getSize();
		int availableWidth=D.width-(insets.left+insets.right);
		int height=D.height-(insets.top+insets.right);

		// Reshape the center component
		int centerWidth;
		int leftWidth;
		if(ncomponents>=2) {
			Component C=parent.getComponent(1);
			D=C.getPreferredSize();
			centerWidth=D.width;
			if(centerWidth>availableWidth) centerWidth=availableWidth;
			leftWidth=(availableWidth-centerWidth)>>>1;
			C.setBounds(insets.left+leftWidth, insets.top, centerWidth, height);
		} else {
			centerWidth=0;
			leftWidth=availableWidth>>>1;
		}
		// Reshape the left component
		if(ncomponents>=1) {
			parent.getComponent(0).setBounds(insets.left, insets.top, leftWidth, height);
			// Reshape the right component
			if(ncomponents>=3) parent.getComponent(2).setBounds(
				insets.left+leftWidth+centerWidth,
				insets.top,
				availableWidth-(leftWidth+centerWidth),
				height
			);
		}
	}
}
public Dimension minimumLayoutSize(Container parent) {
	synchronized (parent.getTreeLock()){
		int ncomponents=parent.getComponentCount();
		int outsideWidth=0;
		int centerWidth=0;
		int highest=0;
		if(ncomponents>=1) {
			Dimension D=parent.getComponent(0).getMinimumSize();
			if(D.width>0) outsideWidth=D.width;
			if(D.height>highest) highest=D.height;
			if(ncomponents>=2) {
				D=parent.getComponent(1).getMinimumSize();
				centerWidth=D.width;
				if(D.height>highest) highest=D.height;
				if(ncomponents>=3) {
					D=parent.getComponent(2).getMinimumSize();
					if(D.width>outsideWidth) outsideWidth=D.width;
					if(D.height>highest) highest=D.height;
				}
			}
		}
		Insets insets=parent.getInsets();
		return new Dimension(
			insets.left + outsideWidth*2 + centerWidth + insets.right,
			insets.top + highest + insets.bottom
		);
	}
}
public Dimension preferredLayoutSize(Container parent) {
	synchronized (parent.getTreeLock()){
		int ncomponents=parent.getComponentCount();
		int outsideWidth=0;
		int centerWidth=0;
		int highest=0;
		if(ncomponents>=1) {
			Dimension D=parent.getComponent(0).getPreferredSize();
			if(D.width>0) outsideWidth=D.width;
			if(D.height>highest) highest=D.height;
			if(ncomponents>=2) {
				D=parent.getComponent(1).getPreferredSize();
				centerWidth=D.width;
				if(D.height>highest) highest=D.height;
				if(ncomponents>=3) {
					D=parent.getComponent(2).getPreferredSize();
					if(D.width>outsideWidth) outsideWidth=D.width;
					if(D.height>highest) highest=D.height;
				}
			}
		}
		Insets insets=parent.getInsets();
		return new Dimension(
			insets.left + outsideWidth*2 + centerWidth + insets.right,
			insets.top + highest + insets.bottom
		);
	}
}
public void removeLayoutComponent(Component component) {
}
}
