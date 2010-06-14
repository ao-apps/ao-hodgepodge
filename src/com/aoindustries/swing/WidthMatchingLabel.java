/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * @author  AO Industries, Inc.
 */
public class WidthMatchingLabel extends JLabel {

    private final Object component;

    public WidthMatchingLabel(Object component) {
        super();
        this.component=component;
    }

    public WidthMatchingLabel(String text, Object component) {
        super(text);
        this.component=component;
    }

    public WidthMatchingLabel(String text, int horizontalAlignment, Object component) {
        super(text, horizontalAlignment);
        this.component=component;
    }

    public WidthMatchingLabel(String text, Icon icon, int horizontalAlignment, Object component) {
        super(text, icon, horizontalAlignment);
        this.component=component;
    }

    public WidthMatchingLabel(Icon icon, Object component) {
        super(icon);
        this.component=component;
    }

    public WidthMatchingLabel(Icon icon, int horizontalAlignment, Object component) {
        super(icon, horizontalAlignment);
        this.component=component;
    }
    
    @Override
    public Dimension getPreferredSize() {
        int totalWidth;
        if(component instanceof Component) totalWidth=((Component)component).getPreferredSize().width;
        else if(component instanceof Component[]) {
            Component[] comps=(Component[])component;
            totalWidth=0;
            for(int c=0;c<comps.length;c++) {
                totalWidth+=comps[c].getPreferredSize().width;
            }
        } else throw new IllegalArgumentException("Unknown class for component, must be Component or Component[], is "+component.getClass().getName());
        return new Dimension(
            totalWidth,
            super.getPreferredSize().height
        );
    }
}
