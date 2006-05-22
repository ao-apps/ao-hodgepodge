package com.aoindustries.swing;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import java.awt.image.*;
import java.net.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class ImageCanvas extends JComponent {

  Image image;
  Window window;

  private long nextupdate;
  private boolean resized=false;
  public ImageCanvas(Image image) {
	this.image=image;
  }
  public ImageCanvas(URL url) {
	this(Toolkit.getDefaultToolkit().getImage(url));
  }
  public ImageCanvas(URL url,Window window) {
	this(url);
	this.window=window;
  }
  public Dimension getPreferredSize() {
	int iwidth=image.getWidth(this);
	int iheight=image.getHeight(this);
	if(iwidth>0&&iheight>0) return new Dimension(iwidth,iheight);
	return new Dimension(0,0);
  }
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
	  Color BG;
	  G.setColor(BG=getBackground());
	  int temp=iheight*width/iwidth;
	  if(temp<=height) {
	int y1=(height-temp)/2;
	G.fillRect(0,0,width,y1);
	G.drawImage(image,0,y1,width,temp,BG,this);
	G.fillRect(0,y1+temp,width,height-y1-temp);
	  } else {
	temp=iwidth*height/iheight;
	int x1=(width-temp)/2;
	G.fillRect(0,0,x1,height);
	G.drawImage(image,x1,0,temp,height,BG,this);
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
  public void update(Graphics G) {
	paint(G);
  }
}
