/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
package com.aoindustries.awt.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Gets and caches image size information.
 * Only updates the value when the file modified time or length changes.
 *
 * @author  AO Industries, Inc.
 */
final public class ImageSizeCache {

	static class CacheEntry {
		long lastModified;
		long length;
		Dimension size;
	}

	private static final Map<String,CacheEntry> sizeCache = new HashMap<>();

	public static Dimension getImageSize(File imageFile) throws IOException {
		// Locate the CacheValue
		final CacheEntry entry;
		synchronized(sizeCache) {
			String canonicalPath = imageFile.getCanonicalPath();
			CacheEntry ce = sizeCache.get(canonicalPath);
			if(ce==null) sizeCache.put(canonicalPath, ce = new CacheEntry());
			entry = ce;
		}
		// Synchronize on the cache entry itself.  Allows concurrency for different images
		synchronized(entry) {
			long lastModified = imageFile.lastModified();
			long length = imageFile.length();
			if(
				entry.size == null
				|| lastModified != entry.lastModified
				|| length != entry.length
			) {
				BufferedImage img = ImageIO.read(imageFile);
				if(img==null) throw new IOException("Unable to read image: " + imageFile);
				entry.lastModified = lastModified;
				entry.length = length;
				entry.size = new Dimension(img.getWidth(), img.getHeight());
			}
			return new Dimension(entry.size); // Safe copy
		}
	}

	/**
	 * Make no instances.
	 */
	private ImageSizeCache() {
	}
}
