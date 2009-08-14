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
 * A label that has all of the extra whitespace to the left and right removed.
 *
 * @author  AO Industries, Inc.
 */
public class NarrowLabel extends Label {

public NarrowLabel() {
	super();
}
public NarrowLabel(String S) {
	super(S);
}
public NarrowLabel(String S, int align) {
	super(S, align);
}
public Dimension getPreferredSize() {
	Dimension D = super.getPreferredSize();
	Font font = getFont();
	if (font != null) {
		FontMetrics FM = getFontMetrics(font);
		int width = FM.stringWidth(getText());
		return new Dimension(width, D.height);
	} else
		return D;
}
}
