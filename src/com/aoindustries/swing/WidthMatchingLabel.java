package com.aoindustries.swing;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import javax.swing.*;

/**
 * @version  1.0
 *
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
