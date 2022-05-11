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

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a 255 + alpha IndexColorModel to best match a BufferedImage.
 *
 * @author  AO Industries, Inc.
 */
public class OptimalIndexColorModel extends IndexColorModel {

  static class ColorCount {

    int color;
    int count;

    ColorCount(int color) {
      this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
      return
          (obj instanceof ColorCount)
              && ((ColorCount) obj).color == color;
    }

    @Override
    public int hashCode() {
      return color;
    }
  }

  /**
   * Sorted by count descending.
   */
  static final class ColorCountCountComparator implements Comparator<ColorCount> {

    private static final ColorCountCountComparator instance = new ColorCountCountComparator();

    static ColorCountCountComparator getInstance() {
      return instance;
    }

    private ColorCountCountComparator() {
      // Do nothing
    }

    @Override
    public int compare(ColorCount o1, ColorCount o2) {
      return o1.count - o2.count;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof ColorCountCountComparator;
    }

    @Override
    public int hashCode() {
      return 1;
    }
  }

  /**
   * The minimum brightness for a color to be dropped.
   */
  private static final int DROP_THRESHOLD = 96;

  /**
   * The offset used to keep the dark colors in the image.
   */
  private static final int DROP_OFFSET = 1000;

  private OptimalIndexColorModel(int size, byte[] r, byte[] g, byte[] b, int trans) {
    super(8, size, r, g, b, trans);
  }

  public static OptimalIndexColorModel getOptimalIndexColorModel(BufferedImage image) {
    // Sorted by color
    Map<Integer, ColorCount> colorCounts = new HashMap<>();

    // Count the use of each color in the image
    int width = image.getWidth();
    int height = image.getHeight();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Integer color = image.getRGB(x, y);
        ColorCount colorCount = colorCounts.get(color);
        if (colorCount == null) {
          colorCounts.put(color, colorCount = new ColorCount(color));
        }
        colorCount.count++;
      }
    }

    // Add 1000 to the counts for those less than DROP_THRESHOLD brightness
    for (ColorCount colorCount : colorCounts.values()) {
      int color = colorCount.color;
      int brightness = Math.max(
          (color >>> 16) & 255,
          Math.max(
              (color >>> 8) & 255,
              color & 255
          )
      );
      if (brightness < DROP_THRESHOLD) {
        colorCount.count += DROP_OFFSET;
      }
    }

    // Sort the list based on number of times the pixels were used (most used on top)
    List<ColorCount> colorCountList = new ArrayList<>(colorCounts.values());
    Collections.sort(colorCountList, ColorCountCountComparator.getInstance());

    // Use at most the top 256 colors
    int size = colorCountList.size();
    int numColorsUsed = size > 256 ? 256 : size;
    byte[] r = new byte[numColorsUsed];
    byte[] g = new byte[numColorsUsed];
    byte[] b = new byte[numColorsUsed];

    int transparentIndex = -1;

    for (int c = 0; c < numColorsUsed; c++) {
      int color = colorCountList.get(c).color;
      /*byte red=*/ r[c] = (byte) ((color >>> 16) & 255);
      /*byte green=*/ g[c] = (byte) ((color >>> 8) & 255);
      /*byte blue=*/ b[c] = (byte) (color & 255);
      // TODO: transparency not distinguished properly:
      // if (transparent_index == -1 && red == -1 && green == -1 && blue == -1) {
      //   transparent_index = c;
      // }
    }
    return new OptimalIndexColorModel(numColorsUsed, r, g, b, transparentIndex);
  }
}
