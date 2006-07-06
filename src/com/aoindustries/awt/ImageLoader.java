package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

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
    private ErrorHandler errorHandler;

    /**
     * @deprecated  Please use <code>ImageLoader(Image,ErrorHandler)
     */
    public ImageLoader(Image image) {
        this(image, new StandardErrorHandler());
    }
    
    public ImageLoader(Image image, ErrorHandler errorHandler) {
	this.image=image;
        this.errorHandler=errorHandler;
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
                errorHandler.reportWarning(err, null);
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
