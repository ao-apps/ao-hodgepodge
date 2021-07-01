/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014, 2016, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.awt.image;

import com.aoapps.lang.function.FunctionE;
import com.aoapps.lang.io.IoUtils;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author  AO Industries, Inc.
 */
final public class Images {

	/**
	 * Gets an array big enough to hold the pixels for an image of the given size.
	 * The array is not populated.
	 */
	public static int[] getRGBArray(int width, int height) {
		return new int[width * height];
	}

	/**
	 * Gets an array big enough to hold the pixels for an image of the given size.
	 * The array is not populated.
	 */
	public static int[] getRGBArray(Dimension size) {
		return getRGBArray(size.width, size.height);
	}

	/**
	 * Gets an array big enough to hold the provided image pixels.
	 * The array is not populated.
	 */
	public static int[] getRGBArray(BufferedImage image) {
		return getRGBArray(image.getWidth(), image.getHeight());
	}

	/**
	 * Gets the RGB pixels for the given image into a new array.
	 */
	public static int[] getRGB(BufferedImage image) {
		int[] pixels = getRGBArray(image);
		getRGB(image, pixels);
		return pixels;
	}

	/**
	 * Gets the RGB pixels for the given image into the given array.
	 */
	public static void getRGB(BufferedImage image, int[] pixels) {
		int width = image.getWidth();
		int height = image.getHeight();
		image.getRGB(0, 0, width, height, pixels, 0, width);
	}

	/**
	 * Finds one image within another.
	 *
	 * @param  tolerance  The portion of red, green, and blue differences
	 *                    allowed before ignoring a certain location.  Zero implies
	 *                    an exact match.
	 *
	 * @return  The top-left point where the top left of the image is found or
	 *          <code>null</code> if not found within tolerance.
	 */
	public static Point findImage(BufferedImage image, BufferedImage findme, double tolerance) {
		final int imageWidth = image.getWidth();
		final int findmeWidth = findme.getWidth();
		if(imageWidth >= findmeWidth) {
			final int imageHeight = image.getHeight();
			final int findmeHeight = findme.getHeight();
			if(imageHeight >= findmeHeight) {
				return findImage(
					getRGB(image),
					imageWidth,
					imageHeight,
					getRGB(findme),
					findmeWidth,
					findmeHeight,
					tolerance
				);
			}
		}
		return null;
	}

	/**
	 * Finds one image within another.
	 *
	 * @param  tolerance  The portion of red, green, and blue differences
	 *                    allowed before ignoring a certain location.  Zero implies
	 *                    an exact match.
	 *
	 * @return  The top-left point where the top left of the image is found or
	 *          <code>null</code> if not found within tolerance.
	 */
	public static Point findImage(int[] imagePixels, int imageWidth, int imageHeight, int[] findmePixels, int findmeWidth, int findmeHeight, double tolerance) {
		final int searchWidth = imageWidth - findmeWidth;
		if(searchWidth >= 0) {
			final int searchHeight = imageHeight - findmeHeight;
			if(searchHeight >= 0) {
				// Each pixel can deviate by up to 255 for each primary color
				final double maxDeviation = (double)3 * (double)255 * (double)findmeWidth * (double)findmeHeight;
				final long maxMismatch = (long)(tolerance * maxDeviation);
				// Get pixels all at once
				for(int imageY = 0; imageY <= searchHeight; imageY++) {
NextLocation :
					for(int imageX = 0; imageX <= searchWidth; imageX++) {
						long totalMismatch = 0;
						int findMeIndex = 0;
						for(int findmeY=0; findmeY<findmeHeight; findmeY++) {
							for(int findmeX=0; findmeX<findmeWidth; findmeX++) {
								int imagePixel = imagePixels[(imageY + findmeY) * imageWidth + imageX + findmeX];
								int findmePixel = findmePixels[findMeIndex++];
								if(
									// Check for exact match
									imagePixel != findmePixel
									// If either is full alpha, consider a match
									&& (imagePixel & 0xff000000)!=0
									&& (findmePixel & 0xff000000)!=0
								) {
									totalMismatch +=
										// Red difference
										Math.abs(((imagePixel >>> 16) & 255) - ((findmePixel >>> 16) & 255))
										// Green difference
										+ Math.abs(((imagePixel >>> 8) & 255) - ((findmePixel >>> 8) & 255))
										// Blue difference
										+ Math.abs((imagePixel & 255) - (findmePixel & 255))
									;
									if(totalMismatch > maxMismatch) continue NextLocation;
								}
							}
						}
						return new Point(imageX, imageY);
					}
				}
			}
		}
		return null;
	};

	/**
	 * Loads an image from a resource using the default toolkit.
	 */
	public static Image getImageFromResources(FunctionE<? super String, ? extends InputStream, ? extends IOException> getResourceAsStream, String name) throws IOException {
		return getImageFromResources(getResourceAsStream, name, Toolkit.getDefaultToolkit());
	}

	/**
	 * Loads an image from a resource using the provided toolkit.
	 */
	public static Image getImageFromResources(FunctionE<? super String, ? extends InputStream, ? extends IOException> getResourceAsStream, String name, Toolkit toolkit) throws IOException {
		byte[] imageData;
		InputStream in = getResourceAsStream.apply(name);
		if(in == null) throw new IOException("Unable to find resource: " + name);
		try {
			imageData = IoUtils.readFully(in);
		} finally {
			in.close();
		}
		return toolkit.createImage(imageData);
	}

	private Images() {
	}
}
