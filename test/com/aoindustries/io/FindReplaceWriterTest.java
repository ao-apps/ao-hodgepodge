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
package com.aoindustries.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class FindReplaceWriterTest extends TestCase {

    public FindReplaceWriterTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FindReplaceWriterTest.class);
        return suite;
    }

	private static void doTest(String unix, String ... dos) throws IOException {
		StringWriter buffer = new StringWriter(unix.length());
		try {
			FindReplaceWriter writer = new FindReplaceWriter(buffer, "\r\n", NativeToUnixWriter.UNIX_EOL);
			try {
				for(String s : dos) writer.write(s);
			} finally {
				writer.close();
			}
		} finally {
			buffer.close();
		}
		assertEquals(unix, buffer.toString());
	}

	public void testDosToUnixConversion() throws IOException {
		doTest(
			"",
			""
		);
		doTest(
			"Test line without end of line character.",
			"Test line without end of line character."
		);
		doTest(
			"Test line with end of line character.\n",
			"Test line with end of line character.\r\n"
		);
		doTest(
			"Test multiline\nwith end of line characters.\n",
			"Test multiline\r\nwith end of line characters.\r\n"
		);
		doTest(
			"Test multiline\nwith split write.\n",
			"Test multiline\r", "\nwith split write.\r\n"
		);
		doTest(
			"Test multiline\nwith split write at end.\n",
			"Test multiline\r", "\nwith split write at end.\r", "\n"
		);
    }
}
