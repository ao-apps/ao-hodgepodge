/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.awt;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Preloads an image for use in <code>Graphics.drawImage</code> calls.
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
