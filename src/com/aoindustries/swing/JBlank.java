package com.aoindustries.swing;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 * @author  AO Industries, Inc.
 */
public class JBlank extends JComponent {

    /**
     * The default preferred size is (1, 1)
     */
    public JBlank() {
        this(1, 1);
    }

    public JBlank(int width, int height) {
        this(new Dimension(width, height));
    }

    public JBlank(Dimension preferredSize) {
        setPreferredSize(preferredSize);
    }
}
