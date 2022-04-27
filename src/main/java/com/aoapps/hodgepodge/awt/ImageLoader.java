/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.awt;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.logging.Logger;

/**
 * Preloads an image for use in <code>Graphics.drawImage</code> calls.
 *
 * @author  AO Industries, Inc.
 */
public final class ImageLoader implements ImageConsumer {

  private int status;

  private final Image image;

  /**
   * @deprecated  logger is unused
   */
  @Deprecated
  public ImageLoader(Image image, Logger logger) {
    this.image = image;
  }

  public ImageLoader(Image image) {
    this.image = image;
  }

  @Override
  public synchronized void imageComplete(int status) {
    this.status |= status;
    notifyAll();
  }

  /**
   * Loads an image and returns when a frame is done, the image is done, an error occurs, or the image is aborted.
   */
  public void loadImage() throws InterruptedException {
    synchronized (this) {
      status = 0;
      image.getSource().startProduction(this);
      while ((status & (IMAGEABORTED | IMAGEERROR | SINGLEFRAMEDONE | STATICIMAGEDONE)) == 0) {
        wait();
      }
    }
  }

  @Override
  public void setColorModel(ColorModel mode) {
    // Do nothing
  }

  @Override
  public void setDimensions(int width, int height) {
    // Do nothing
  }

  @Override
  public void setHints(int flags) {
    // Do nothing
  }

  @Override
  public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scansize) {
    // Do nothing
  }

  @Override
  public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scansize) {
    // Do nothing
  }

  @Override
  @SuppressWarnings("UseOfObsoleteCollectionType") // Hashtable required by ImageConsumer
  public void setProperties(java.util.Hashtable<?, ?> properties) {
    // Do nothing
  }
}
