/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ImagesTest {

	private static final Logger logger = Logger.getLogger(ImagesTest.class.getName());

	public ImagesTest() {
	}

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
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void hello() {
		logger.log(
			Level.INFO,
			"Got image: {0} x {1}",
			new Object[] {
				image.getWidth(),
				image.getHeight()
			}
		);
		logger.log(
			Level.INFO,
			"Got findme: {0} x {1}",
			new Object[] {
				findme.getWidth(),
				findme.getHeight()
			}
		);
		long startNanos = System.nanoTime();
		final int repeat = 10;
		for(int i=0; i<repeat; i++) {
			testRepeat();
		}
		long endNanos = System.nanoTime();
		logger.log(Level.INFO, "Total time: {0} ms", BigDecimal.valueOf((endNanos - startNanos)/repeat, 6));
	}

	private void testRepeat() {
		Point foundAt = Images.findImage(image, findme, 0);
		assertEquals(new Point(687, 524), foundAt);
	}
}
