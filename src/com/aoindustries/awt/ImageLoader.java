package com.aoindustries.awt;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Preloads an image for use in <code>Graphics.drawImage</code> calls.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class ImageLoader implements ImageConsumer {

    private int status=0;

    private Image image;
    private Logger logger;

    public ImageLoader(Image image, Logger logger) {
        this.image=image;
        this.logger=logger;
    }

    synchronized public void imageComplete(int status) {
        this.status|=status;
        notify();
    }

    /**
     * Loads an image and returns when a frame is done, the image is done, an error occurs, or the image is aborted.
     */
    public void loadImage() {
        status=0;
        image.getSource().startProduction(this);
        while((status&(IMAGEABORTED|IMAGEERROR|SINGLEFRAMEDONE|STATICIMAGEDONE))==0) {
            try {
            synchronized(this) {
                wait();
            }
            } catch(InterruptedException err) {
                logger.log(Level.WARNING, null, err);
            }
        }
    }

    public void setColorModel(ColorModel mode) {}
    public void setDimensions(int width, int height) {}
    public void setHints(int flags) {}
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scansize) {}
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scansize) {}
    public void setProperties(Hashtable properties) {}
}
