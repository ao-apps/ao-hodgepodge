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
    
    @Override
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Arrow");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new Arrow(UP, BorderFactory.createEmptyBorder()));
        frame.getContentPane().add(new Arrow(DOWN, BorderFactory.createEmptyBorder()));
        frame.getContentPane().add(new Arrow(LEFT, BorderFactory.createEmptyBorder()));
        frame.getContentPane().add(new Arrow(RIGHT, BorderFactory.createEmptyBorder()));
        frame.pack();
        frame.setVisible(true);
    }
}
