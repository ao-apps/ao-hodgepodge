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
package com.aoindustries.messaging;

import com.aoindustries.awt.image.*;
import java.awt.Point;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import org.junit.Test;
import static org.junit.Assert.*;

public class ByteArrayMessageTest {

	public ByteArrayMessageTest() {
	}

	private static final Random random = new SecureRandom();

	@Test
	public void testStringEncodeAndDecode() throws IOException {
		int len = random.nextInt(10000);
		byte[] bytes = new byte[len + random.nextInt(10)];
		random.nextBytes(bytes);
		byte[] expected = Arrays.copyOf(bytes, len);

		ByteArrayMessage message = new ByteArrayMessage(bytes);
		
		// Encode to String
		String encodedString = message.encodeAsString();
		
		// Decode back to message
		ByteArrayMessage decoded = (ByteArrayMessage)MessageType.BYTE_ARRAY.decode(encodedString);

	}
}
