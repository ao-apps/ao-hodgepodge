/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.io.buffer;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class SegmentedWriterTest extends BufferWriterTest {

    public SegmentedWriterTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SegmentedWriterTest.class);
        return suite;
    }

	public void testBenchmarkSimulate() throws IOException {
		benchmarkSimulate(
			new BufferWriterFactory() {
				@Override
				public String getName() {
					return SegmentedWriter.class.getName();
				}

				@Override
				public BufferWriter newBufferWriter() {
					return new SegmentedWriter(4 * 1024 * 1024);
				}
			}
		);
	}
}
