package com.aoindustries.swing;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author  AO Industries, Inc.
 */
public class Arrow extends JComponent {
    
    public static final int
        NONE=0,
        UP=1,
        DOWN=2,
        LEFT=3,
        RIGHT=4
    ;

    private int direction;
    
    public Arrow(int direction, Border border) {
        this.direction=direction;
        setPreferredSize(new Dimension(16, 16));
        setBorder(border);
    }
    
    public void paintComponent(Graphics G) {
        Dimension size=getSize();
        //G.draw3DRect(0, 0, size.width-1, size.height-1, direction!=NONE);
        int centerX=size.width/2-1;
        int centerY=size.height/2-1;
        Graphics2D G2=(Graphics2D)G;
        Object previousHint=G2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            if(direction==NONE) {
                //G.drawOval(centerX-3, centerY-3, 6, 6);
            } else if(direction==UP) {
                Polygon P=new Polygon();
                P.addPoint(centerX-2, centerY+2);
                P.addPoint(centerX, centerY-2);
                P.addPoint(centerX+2, centerY+2);
                G.drawPolygon(P);
            } else if(direction==DOWN) {
                Polygon P=new Polygon();
                P.addPoint(centerX-2, centerY-2);
                P.addPoint(centerX, centerY+2);
                P.addPoint(centerX+2, centerY-2);
                G.drawPolygon(P);
            } else if(direction==LEFT) {
                Polygon P=new Polygon();
                P.addPoint(centerX+2, centerY-2);
                P.addPoint(centerX-2, centerY);
                P.addPoint(centerX+2, centerY+2);
                G.drawPolygon(P);
            } else if(direction==RIGHT) {
                Polygon P=new Polygon();
                P.addPoint(centerX-2, centerY-2);
                P.addPoint(centerX+2, centerY);
                P.addPoint(centerX-2, centerY+2);
                G.drawPolygon(P);
            } else throw new RuntimeException("Unexpected value for direction: "+direction);
        } finally {
            G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, previousHint);
        }
    }
}
