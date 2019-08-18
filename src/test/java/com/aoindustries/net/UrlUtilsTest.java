/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.net;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UrlUtilsTest {

	public UrlUtilsTest() {
	}

	@Test
	public void testEncodeURI() {
		assertEquals(
			"https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?%E8%8A%B1=true",
			UrlUtils.encodeURI("https://aointernet.net/shared/ホスティング.do?花=true")
		);
		assertEquals(
			"https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?param=%E8%8A%B1",
			UrlUtils.encodeURI("https://aointernet.net/shared/ホスティング.do?param=花")
		);
		assertEquals(
			"Checking not double-encoding after #",
			"https://search.maven.org/#search%7Cgav%7C1%7Cg:%22@com.aoindustries%22%20AND%20a:%22@aocode-public%22",
			UrlUtils.encodeURI("https://search.maven.org/#search|gav|1|g:%22@com.aoindustries%22%20AND%20a:%22@aocode-public%22")
		);
	}

	@Test
	public void testDecodeURI() {
		assertEquals(
			"https://aointernet.net/shared/ホスティング.do?花=true",
			UrlUtils.decodeURI("https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?%E8%8A%B1=true")
		);
		assertEquals(
			"https://aointernet.net/shared/ホスティング.do?param=花",
			UrlUtils.decodeURI("https://aointernet.net/shared/%E3%83%9B%E3%82%B9%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0.do?param=%E8%8A%B1")
		);
		assertEquals(
			"Checking not double-encoding after #",
			"https://search.maven.org/#search|gav|1|g:\"@com.aoindustries\" AND a:\"@aocode-public\"",
			UrlUtils.decodeURI("https://search.maven.org/#search%7Cgav%7C1%7Cg:%22@com.aoindustries%22%20AND%20a:%22@aocode-public%22")
		);
	}
}
