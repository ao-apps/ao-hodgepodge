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
