/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.awt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Overrides <code>BorderLayout</code> to support multiple
 * components in all of the four borders.  Only one component
 * is allowed in the Center.  Components are added from North
 * to South and West to East.
 *
 * @author  AO Industries, Inc.
 */
public class MultiBorderLayout extends BorderLayout {

	private static final long serialVersionUID = 1L;

	protected List<Component>
		northComponents,
		westComponents,
		eastComponents,
		southComponents
	;

	protected Component center;

	public MultiBorderLayout() {
		// Do nothing
	}

	public MultiBorderLayout(int hgap, int vgap) {
		super(hgap, vgap);
	}

	@Override
	public void addLayoutComponent(Component component, Object name) {
		synchronized (component.getTreeLock()) {
			if (name == null) name = CENTER;
			if(CENTER.equals(name)) {
				center = component;
			} else if(NORTH.equals(name)) {
				if (northComponents == null) northComponents = new ArrayList<>();
				northComponents.add(component);
			} else if(SOUTH.equals(name)) {
				if (southComponents == null) southComponents = new ArrayList<>();
				southComponents.add(component);
			} else if(EAST.equals(name)) {
				if (eastComponents == null) eastComponents = new ArrayList<>();
				eastComponents.add(component);
			} else if(WEST.equals(name)) {
				if (westComponents == null) westComponents = new ArrayList<>();
				westComponents.add(component);
			} else {
				throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
			}
		}
	}

	/**
	 * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>.
	 */
	@Override
	@Deprecated
	public void addLayoutComponent(String name, Component component) {
		addLayoutComponent(component, name);
	}

	protected Dimension getLayoutSize(Container target, boolean minimum) {
		final int hgap = getHgap();
		final int vgap = getVgap();
		int width = 0;
		int height = 0;
		synchronized (target.getTreeLock()) {
			// Handle the west
			int size = westComponents == null ? 0 : westComponents.size();
			for (int c = 0; c < size; c++) {
				Component component = westComponents.get(c);
				if (component.isVisible()) {
					Dimension d = minimum ? component.getMinimumSize() : component.getPreferredSize();
					width += d.width + hgap;
					if (d.height > height) height = d.height;
				}
			}
			// Handle the east
			size = eastComponents == null ? 0 : eastComponents.size();
			for (int c = 0; c < size; c++) {
				Component component = eastComponents.get(c);
				if (component.isVisible()) {
					Dimension d = minimum ? component.getMinimumSize() : component.getPreferredSize();
					width += d.width + hgap;
					if (d.height > height) height = d.height;
				}
			}
			// Handle the center
			if (center != null && center.isVisible()) {
				Dimension d = minimum ? center.getMinimumSize() : center.getPreferredSize();
				width += d.width;
				if (d.height > height) height = d.height;
			}
			// Handle the north
			size = northComponents == null ? 0 : northComponents.size();
			for (int c = 0; c < size; c++) {
				Component component = northComponents.get(c);
				if (component.isVisible()) {
					Dimension d = minimum ? component.getMinimumSize() : component.getPreferredSize();
					height += d.height + vgap;
					if (d.width > width) width = d.width;
				}
			}
			// Handle the south
			size = southComponents == null ? 0 : southComponents.size();
			for (int c = 0; c < size; c++) {
				Component component = southComponents.get(c);
				if (component.isVisible()) {
					Dimension d = minimum ? component.getMinimumSize() : component.getPreferredSize();
					height += d.height + vgap;
					if (d.width > width) width = d.width;
				}
			}
			Insets insets = target.getInsets();
			return new Dimension(insets.left + width + insets.right, insets.top + height + insets.bottom);
		}
	}

	@Override
	public void layoutContainer(Container target) {
		int hgap = getHgap();
		int vgap = getVgap();
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			Dimension d = target.getSize();
			int top = insets.top;
			int bottom = d.height - insets.bottom;
			int left = insets.left;
			int right = d.width - insets.right;
			// Reshape the North Components
			if(northComponents != null) {
				int size = northComponents.size();
				for (int c = 0; c < size; c++) {
					Component component = northComponents.get(c);
					if (component.isVisible()) {
						d = component.getSize();
						component.setSize(right - left, d.height);
						d = component.getPreferredSize();
						component.setBounds(left, top, right - left, d.height);
						top += d.height + vgap;
					}
				}
			}
			// Reshape the South Components
			if(southComponents != null) {
				for (int c = (southComponents.size() - 1); c >= 0; c--) {
					Component component = southComponents.get(c);
					if (component.isVisible()) {
						d = component.getSize();
						component.setSize(right - left, d.height);
						d = component.getPreferredSize();
						component.setBounds(left, bottom - d.height, right - left, d.height);
						bottom -= d.height + vgap;
					}
				}
			}
			// Reshape the West Components
			if(westComponents != null) {
				int size = westComponents.size();
				for (int c = 0; c < size; c++) {
					Component component = westComponents.get(c);
					if (component.isVisible()) {
						d = component.getSize();
						component.setSize(d.width, bottom - top);
						d = component.getPreferredSize();
						component.setBounds(left, top, d.width, bottom - top);
						left += d.width + hgap;
					}
				}
			}
			// Reshape the East Components
			if(eastComponents != null) {
				for (int c = (eastComponents.size() - 1); c >= 0; c--) {
					Component component = eastComponents.get(c);
					if (component.isVisible()) {
						d = component.getSize();
						component.setSize(d.width, bottom - top);
						d = component.getPreferredSize();
						component.setBounds(right - d.width, top, d.width, bottom - top);
						right -= d.width + hgap;
					}
				}
			}
			// Reshape the Center Component
			if (center != null && center.isVisible()) {
				center.setBounds(left, top, right - left, bottom - top);
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(final Container target) {
		return getLayoutSize(target, true);
	}

	@Override
	public Dimension preferredLayoutSize(final Container target) {
		return getLayoutSize(target, false);
	}

	public static boolean remove(Object o, List<Component> components) {
		int size = components.size();
		for(int c = 0; c < size; c++) {
			if(components.get(c) == o) {
				components.remove(c);
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeLayoutComponent(Component component) {
		synchronized (component.getTreeLock()) {
			if (component == center) {
				center = null;
			} else if (northComponents!=null && remove(component, northComponents)) {
				// Removed from northComponents
			} else if (westComponents!=null && remove(component, westComponents)) {
				// Removed from westComponents
			} else if (eastComponents!=null && remove(component, eastComponents)) {
				// Removed from eastComponents
			} else if (southComponents!=null) {
				remove(component, southComponents);
			}
		}
	}
}
