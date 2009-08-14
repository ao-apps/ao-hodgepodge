/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.swing;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;

/**
 * @author  AO Industries, Inc.
 */
public class FixedHeightScrollPane extends JScrollPane {

    private int preferredHeight;
    private Component view;

    public FixedHeightScrollPane(int preferredHeight) {
        super();
        this.preferredHeight=preferredHeight;
    }

    public FixedHeightScrollPane(int preferredHeight, int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
        this.preferredHeight=preferredHeight;
    }

    public FixedHeightScrollPane(int preferredHeight, Component view) {
        super(view);
        this.preferredHeight=preferredHeight;
        this.view=view;
    }

    public FixedHeightScrollPane(int preferredHeight, Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        this.preferredHeight=preferredHeight;
        this.view=view;
    }

    private int maximum=0;

    @Override
    public Dimension getPreferredSize() {
        int newWidth=super.getPreferredSize().width;
        if(newWidth>maximum) maximum=newWidth;
        Dimension D=new Dimension(
            maximum,
            preferredHeight
        );
        return D;
    }
}
