/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014, 2016, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.awt.image;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImagesTest {

  private static final Logger logger = Logger.getLogger(ImagesTest.class.getName());

  private static BufferedImage image;
  private static BufferedImage findme;

  @BeforeClass
  public static void setUpClass() throws IOException {
    // Load saved screen shot
    InputStream in = ImagesTest.class.getResourceAsStream("ImagesTest-image.png");
    try {
      image = ImageIO.read(in);
    } finally {
      in.close();
    }
    // Load the close button
    in = ImagesTest.class.getResourceAsStream("ImagesTest-findme.png");
    try {
      findme = ImageIO.read(in);
    } finally {
      in.close();
    }
  }

  @Test
  public void testFindImage() {
    logger.log(
        Level.INFO,
        "Got image: {0} x {1}",
        new Object[]{
            image.getWidth(),
            image.getHeight()
        }
    );
    logger.log(
        Level.INFO,
        "Got findme: {0} x {1}",
        new Object[]{
            findme.getWidth(),
            findme.getHeight()
        }
    );
    long startNanos = System.nanoTime();
    final int repeat = 10;
    for (int i = 0; i < repeat; i++) {
      testRepeat();
    }
    long endNanos = System.nanoTime();
    logger.log(Level.INFO, "Average time: {0} ms", BigDecimal.valueOf((endNanos - startNanos) / repeat, 6));
  }

  private void testRepeat() {
    Point foundAt = Images.findImage(image, findme, 0);
    assertEquals(new Point(687, 524), foundAt);
  }
}
