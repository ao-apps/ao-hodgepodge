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
package com.aoindustries.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * @author  AO Industries, Inc.
 */
public class ImageCanvas extends JComponent {

    final Image image;
    final Window window;

    private long nextupdate;
    private boolean resized=false;

    public ImageCanvas(Image image) {
        this.image=image;
        this.window = null;
    }

    public ImageCanvas(URL url) {
        this.image = Toolkit.getDefaultToolkit().getImage(url);
        this.window = null;
    }

    public ImageCanvas(URL url, Window window) {
        this.image = Toolkit.getDefaultToolkit().getImage(url);
        this.window=window;
    }

    @Override
    public Dimension getPreferredSize() {
        int iwidth=image.getWidth(this);
        int iheight=image.getHeight(this);
        if(iwidth>0&&iheight>0) return new Dimension(iwidth,iheight);
        return new Dimension(0,0);
    }

    @Override
    synchronized public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
        if ((flags & SOMEBITS) != 0) {
            long time = System.currentTimeMillis();
            if (time >= nextupdate) {
                repaint();
                nextupdate = time + 500;
            }
        } else if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
            if (!resized)
                resizeIt();
            repaint();
        } else if ((flags & (WIDTH | HEIGHT)) != 0) {
            int iwidth = image.getWidth(this);
            int iheight = image.getHeight(this);
            if (iwidth > 0 && iheight > 0)
                resizeIt();
        }
        return (flags & (ALLBITS | ABORT | ERROR)) == 0;
    }

    @Override
    public void paint(Graphics G) {
        Dimension D;
        int width,height;
        int iwidth,iheight;
        if(
            G!=null
            &&(width=(D=getSize()).width)>0
            &&(height=D.height)>0
            &&(iwidth=image.getWidth(this))>0
            &&(iheight=image.getHeight(this))>0
        ) {
            Color background;
            G.setColor(background=getBackground());
            int temp=iheight*width/iwidth;
            if(temp<=height) {
                int y1=(height-temp)/2;
                G.fillRect(0,0,width,y1);
                G.drawImage(image,0,y1,width,temp,background,this);
                G.fillRect(0,y1+temp,width,height-y1-temp);
            } else {
                temp=iwidth*height/iheight;
                int x1=(width-temp)/2;
                G.fillRect(0,0,x1,height);
                G.drawImage(image,x1,0,temp,height,background,this);
                G.fillRect(x1+temp,0,width-x1-temp,height);
            }
        }
    }

    private void resizeIt() {
        if(window!=null) {
            synchronized(window) {
                window.pack();
            }
        } else {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        getParent().invalidate();
                        getParent().validate();
                    }
                }
            );
    	}
        resized=true;
    }

    @Override
    public void update(Graphics G) {
        paint(G);
    }
}
