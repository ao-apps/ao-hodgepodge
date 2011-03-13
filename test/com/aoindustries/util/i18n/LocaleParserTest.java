/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import java.io.IOException;
import java.util.Locale;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class LocaleParserTest extends TestCase {

    public LocaleParserTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LocaleParserTest.class);
    }

    public void testParseLocale() throws IOException {
        for(Locale locale : Locale.getAvailableLocales()) {
            Locale parsed = LocaleParser.parseLocale(locale.toString());
            // System.out.println(locale+"->"+parsed);
            assertEquals(locale, parsed);
        }
    }
}
