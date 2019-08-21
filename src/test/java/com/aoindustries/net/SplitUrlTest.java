/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2019  AO Industries, Inc.
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

import static com.aoindustries.encoding.TestXmlEncoder.testXmlEncoder;
import com.aoindustries.io.Encoder;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SplitUrlTest {

	public SplitUrlTest() {
	}

	@Test
	public void testToString() {
		String href="test";
		assertSame(href, new SplitUrl(href).toString());
	}

	@Test
	public void testEquals() {
		assertTrue(new SplitUrl("test").equals(new SplitUrl(new String("test"))));
		assertTrue(new SplitUrl("test").equals(new SplitUrl("test")));
		assertFalse(new SplitUrl("test").equals(new SplitUrl("test?")));
	}

	@Test
	public void testHashCode() {
		for(String url : new String[] {"test", "", "BLARG", "http://", "test?", "blarg?test", "blarg?test=sdf"}) {
			assertEquals(url.hashCode(), new SplitUrl(url).hashCode());
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Test Scheme">
	@Test
	public void testIsScheme() {
		assertTrue(new SplitUrl("htTP:").isScheme("http"));
		assertTrue(new SplitUrl("htTPs:").isScheme("https"));

		assertFalse(new SplitUrl("htTP:").isScheme("https"));
		assertFalse(new SplitUrl("htTPs:").isScheme("http"));

		assertFalse(new SplitUrl("htTP").isScheme("http"));
		assertFalse(new SplitUrl("htTPs").isScheme("https"));

		assertFalse(new SplitUrl("/path").isScheme("http"));
		assertFalse(new SplitUrl("./path").isScheme("https"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeEmptyScheme1() {
		new SplitUrl(":blarg").isScheme("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeEmptyScheme2() {
		new SplitUrl(":").isScheme("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSchemeInvalidScheme() {
		new SplitUrl("http:").isScheme("<<>>");
	}

	public void testIsSchemeInvalidSchemeTooLongToValidate() {
		assertFalse(
			"Scheme will not validate when longer than possible value, due to short-cut",
			new SplitUrl("http:").isScheme("<<.>>")
		);
	}

	@Test
	public void testGetScheme() {
		assertEquals("htTP", new SplitUrl("htTP:").getScheme());
		assertEquals("htTPs", new SplitUrl("htTPs:").getScheme());
		assertEquals("htTP", new SplitUrl("htTP:?").getScheme());
		assertEquals("htTP", new SplitUrl("htTP:?param?&param2=value#anc?h&or").getScheme());
		assertEquals("htTP", new SplitUrl("htTP:#fragment#").getScheme());
		assertEquals("htTP", new SplitUrl("htTP:#fragment?notParam").getScheme());
		
		
		assertNull(new SplitUrl("htTP").getScheme());
		assertNull(new SplitUrl("htTPs").getScheme());

		assertNull(new SplitUrl("/path").getScheme());
		assertNull(new SplitUrl("./path").getScheme());

		assertNull(new SplitUrl(":blarg").getScheme());
		assertNull(new SplitUrl(":").getScheme());
	}

	// TODO: Test more scheme methods like write/append

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test HierPart">
	@Test
	public void testGetPathEnd() {
		assertEquals(5, new SplitUrl("htTP:").getPathEnd());
		assertEquals(5, new SplitUrl("htTP:?").getPathEnd());
		assertEquals(5, new SplitUrl("htTP:?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(5, new SplitUrl("htTP:#fragment#").getPathEnd());
		assertEquals(5, new SplitUrl("htTP:#fragment?notParam").getPathEnd());

		assertEquals(2, new SplitUrl("./").getPathEnd());
		assertEquals(2, new SplitUrl("./?").getPathEnd());
		assertEquals(2, new SplitUrl("./?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(2, new SplitUrl("./#fragment#").getPathEnd());
		assertEquals(2, new SplitUrl("./#fragment?notParam").getPathEnd());

		assertEquals(0, new SplitUrl("").getPathEnd());
		assertEquals(0, new SplitUrl("?").getPathEnd());
		assertEquals(0, new SplitUrl("?param?&param2=value#anc?h&or").getPathEnd());
		assertEquals(0, new SplitUrl("#fragment#").getPathEnd());
		assertEquals(0, new SplitUrl("#fragment?notParam").getPathEnd());
	}

	@Test
	public void testGetHierPart() {
		assertEquals("", new SplitUrl("htTP:").getHierPart());
		assertEquals("", new SplitUrl("htTP:?").getHierPart());
		assertEquals("", new SplitUrl("htTP:?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("", new SplitUrl("htTP:#fragment#").getHierPart());
		assertEquals("", new SplitUrl("htTP:#fragment?notParam").getHierPart());

		assertSame("./", new SplitUrl("./").getHierPart());
		assertEquals("./", new SplitUrl("./?").getHierPart());
		assertEquals("./", new SplitUrl("./?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("./", new SplitUrl("./#fragment#").getHierPart());
		assertEquals("./", new SplitUrl("./#fragment?notParam").getHierPart());

		assertSame("", new SplitUrl("").getHierPart());
		assertEquals("", new SplitUrl("?").getHierPart());
		assertEquals("", new SplitUrl("?param?&param2=value#anc?h&or").getHierPart());
		assertEquals("", new SplitUrl("#fragment#").getHierPart());
		assertEquals("", new SplitUrl("#fragment?notParam").getHierPart());
	}

	private static String captureWriteHierPart(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeHierPart(out);
		return out.toString();
	}

	@Test
	public void testWriteHierPart() throws IOException {
		assertEquals("", captureWriteHierPart("htTP:"));
		assertEquals("", captureWriteHierPart("htTP:?"));
		assertEquals("", captureWriteHierPart("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteHierPart("htTP:#fragment#"));
		assertEquals("", captureWriteHierPart("htTP:#fragment?notParam"));

		assertEquals("./", captureWriteHierPart("./"));
		assertEquals("./", captureWriteHierPart("./?"));
		assertEquals("./", captureWriteHierPart("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureWriteHierPart("./#fragment#"));
		assertEquals("./", captureWriteHierPart("./#fragment?notParam"));

		assertEquals("", captureWriteHierPart(""));
		assertEquals("", captureWriteHierPart("?"));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteHierPart("#fragment#"));
		assertEquals("", captureWriteHierPart("#fragment?notParam"));
	}

	private static String captureWriteHierPart(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeHierPart(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteHierPartNullEncoder() throws IOException {
		assertEquals("&", captureWriteHierPart("htTP:&", null));
		assertEquals("&", captureWriteHierPart("htTP:&?", null));
		assertEquals("&", captureWriteHierPart("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("&", captureWriteHierPart("htTP:&#fragment#", null));
		assertEquals("&", captureWriteHierPart("htTP:&#fragment?notParam", null));

		assertEquals("./<>", captureWriteHierPart("./<>", null));
		assertEquals("./<>", captureWriteHierPart("./<>?", null));
		assertEquals("./<>", captureWriteHierPart("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("./<>", captureWriteHierPart("./<>#fragment#", null));
		assertEquals("./<>", captureWriteHierPart("./<>#fragment?notParam", null));

		assertEquals("", captureWriteHierPart("", null));
		assertEquals("", captureWriteHierPart("?", null));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteHierPart("#fragment#", null));
		assertEquals("", captureWriteHierPart("#fragment?notParam", null));
	}

	@Test
	public void testWriteHierPartXhtmlEncoder() throws IOException {
		assertEquals("&amp;", captureWriteHierPart("htTP:&", testXmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&?", testXmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&#fragment#", testXmlEncoder));
		assertEquals("&amp;", captureWriteHierPart("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>?", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>#fragment#", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureWriteHierPart("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureWriteHierPart("", testXmlEncoder));
		assertEquals("", captureWriteHierPart("?", testXmlEncoder));
		assertEquals("", captureWriteHierPart("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureWriteHierPart("#fragment#", testXmlEncoder));
		assertEquals("", captureWriteHierPart("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendHierPartOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendHierPart(out);
		return out.toString();
	}

	@Test
	public void testAppendHierPartOut() throws IOException {
		assertEquals("", captureAppendHierPartOut("htTP:"));
		assertEquals("", captureAppendHierPartOut("htTP:?"));
		assertEquals("", captureAppendHierPartOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartOut("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartOut("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartOut("./"));
		assertEquals("./", captureAppendHierPartOut("./?"));
		assertEquals("./", captureAppendHierPartOut("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartOut("./#fragment#"));
		assertEquals("./", captureAppendHierPartOut("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartOut(""));
		assertEquals("", captureAppendHierPartOut("?"));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartOut("#fragment#"));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam"));
	}

	private static String captureAppendHierPartOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendHierPart(out, encoder);
		return out.toString();
	}

	@Test
	public void testAppendHierPartOutNullEncoder() throws IOException {
		assertEquals("&", captureAppendHierPartOut("htTP:&", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&?", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&#fragment#", null));
		assertEquals("&", captureAppendHierPartOut("htTP:&#fragment?notParam", null));

		assertEquals("./<>", captureAppendHierPartOut("./<>", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>?", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>#fragment#", null));
		assertEquals("./<>", captureAppendHierPartOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendHierPartOut("", null));
		assertEquals("", captureAppendHierPartOut("?", null));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendHierPartOut("#fragment#", null));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendHierPartOutXhtmlEncoder() throws IOException {
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&", testXmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&?", testXmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&#fragment#", testXmlEncoder));
		assertEquals("&amp;", captureAppendHierPartOut("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>?", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>#fragment#", testXmlEncoder));
		assertEquals("./&lt;&gt;", captureAppendHierPartOut("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureAppendHierPartOut("", testXmlEncoder));
		assertEquals("", captureAppendHierPartOut("?", testXmlEncoder));
		assertEquals("", captureAppendHierPartOut("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureAppendHierPartOut("#fragment#", testXmlEncoder));
		assertEquals("", captureAppendHierPartOut("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendHierPartStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new SplitUrl(url).appendHierPart(sb);
		return sb.toString();
	}

	@Test
	public void testAppendHierPartStringBuilder() throws IOException {
		assertEquals("", captureAppendHierPartStringBuilder("htTP:"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:?"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartStringBuilder("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartStringBuilder("./"));
		assertEquals("./", captureAppendHierPartStringBuilder("./?"));
		assertEquals("./", captureAppendHierPartStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartStringBuilder("./#fragment#"));
		assertEquals("./", captureAppendHierPartStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartStringBuilder(""));
		assertEquals("", captureAppendHierPartStringBuilder("?"));
		assertEquals("", captureAppendHierPartStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuilder("#fragment#"));
		assertEquals("", captureAppendHierPartStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendHierPartStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new SplitUrl(url).appendHierPart(sb);
		return sb.toString();
	}

	@Test
	public void testAppendHierPartStringBuffer() throws IOException {
		assertEquals("", captureAppendHierPartStringBuffer("htTP:"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:?"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:#fragment#"));
		assertEquals("", captureAppendHierPartStringBuffer("htTP:#fragment?notParam"));

		assertEquals("./", captureAppendHierPartStringBuffer("./"));
		assertEquals("./", captureAppendHierPartStringBuffer("./?"));
		assertEquals("./", captureAppendHierPartStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("./", captureAppendHierPartStringBuffer("./#fragment#"));
		assertEquals("./", captureAppendHierPartStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendHierPartStringBuffer(""));
		assertEquals("", captureAppendHierPartStringBuffer("?"));
		assertEquals("", captureAppendHierPartStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendHierPartStringBuffer("#fragment#"));
		assertEquals("", captureAppendHierPartStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Query">
	@Test
	public void testGetQueryIndex() {
		assertEquals(-1, new SplitUrl("htTP:").getQueryIndex());
		assertEquals(5, new SplitUrl("htTP:?").getQueryIndex());
		assertEquals(5, new SplitUrl("htTP:?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new SplitUrl("htTP:#fragment#").getQueryIndex());
		assertEquals(-1, new SplitUrl("htTP:#fragment?notParam").getQueryIndex());

		assertEquals(-1, new SplitUrl("./").getQueryIndex());
		assertEquals(2, new SplitUrl("./?").getQueryIndex());
		assertEquals(2, new SplitUrl("./?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new SplitUrl("./#fragment#").getQueryIndex());
		assertEquals(-1, new SplitUrl("./#fragment?notParam").getQueryIndex());

		assertEquals(-1, new SplitUrl("").getQueryIndex());
		assertEquals(0, new SplitUrl("?").getQueryIndex());
		assertEquals(0, new SplitUrl("?param?&param2=value#anc?h&or").getQueryIndex());
		assertEquals(-1, new SplitUrl("#fragment#").getQueryIndex());
		assertEquals(-1, new SplitUrl("#fragment?notParam").getQueryIndex());
	}

	@Test
	public void testHasQuery() {
		assertFalse(new SplitUrl("htTP:").hasQuery());
		assertTrue(new SplitUrl("htTP:?").hasQuery());
		assertTrue(new SplitUrl("htTP:?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new SplitUrl("htTP:#fragment#").hasQuery());
		assertFalse(new SplitUrl("htTP:#fragment?notParam").hasQuery());

		assertFalse(new SplitUrl("./").hasQuery());
		assertTrue(new SplitUrl("./?").hasQuery());
		assertTrue(new SplitUrl("./?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new SplitUrl("./#fragment#").hasQuery());
		assertFalse(new SplitUrl("./#fragment?notParam").hasQuery());

		assertFalse(new SplitUrl("").hasQuery());
		assertTrue(new SplitUrl("?").hasQuery());
		assertTrue(new SplitUrl("?param?&param2=value#anc?h&or").hasQuery());
		assertFalse(new SplitUrl("#fragment#").hasQuery());
		assertFalse(new SplitUrl("#fragment?notParam").hasQuery());
	}

	@Test
	public void testGetQueryString() {
		assertNull(new SplitUrl("htTP:").getQueryString());
		assertEquals("", new SplitUrl("htTP:?").getQueryString());
		assertEquals("param?&param2=value", new SplitUrl("htTP:?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new SplitUrl("htTP:#fragment#").getQueryString());
		assertNull(new SplitUrl("htTP:#fragment?notParam").getQueryString());

		assertNull(new SplitUrl("./").getQueryString());
		assertEquals("", new SplitUrl("./?").getQueryString());
		assertEquals("param?&param2=value", new SplitUrl("./?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new SplitUrl("./#fragment#").getQueryString());
		assertNull(new SplitUrl("./#fragment?notParam").getQueryString());

		assertNull(new SplitUrl("").getQueryString());
		assertEquals("", new SplitUrl("?").getQueryString());
		assertEquals("param?&param2=value", new SplitUrl("?param?&param2=value#anc?h&or").getQueryString());
		assertNull(new SplitUrl("#fragment#").getQueryString());
		assertNull(new SplitUrl("#fragment?notParam").getQueryString());
	}

	private static String captureWriteQueryString(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeQueryString(out);
		return out.toString();
	}

	@Test
	public void testWriteQueryString() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:"));
		assertEquals("", captureWriteQueryString("htTP:?"));
		assertEquals("param?&param2=value", captureWriteQueryString("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("htTP:#fragment#"));
		assertEquals("", captureWriteQueryString("htTP:#fragment?notParam"));

		assertEquals("", captureWriteQueryString("./"));
		assertEquals("", captureWriteQueryString("./?"));
		assertEquals("param?&param2=value", captureWriteQueryString("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("./#fragment#"));
		assertEquals("", captureWriteQueryString("./#fragment?notParam"));

		assertEquals("", captureWriteQueryString(""));
		assertEquals("", captureWriteQueryString("?"));
		assertEquals("param?&param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or"));
		assertEquals("", captureWriteQueryString("#fragment#"));
		assertEquals("", captureWriteQueryString("#fragment?notParam"));
	}

	private static String captureWriteQueryString(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeQueryString(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteQueryStringNullEncoder() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:&", null));
		assertEquals("", captureWriteQueryString("htTP:&?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("htTP:&#fragment#", null));
		assertEquals("", captureWriteQueryString("htTP:&#fragment?notParam", null));

		assertEquals("", captureWriteQueryString("./<>", null));
		assertEquals("", captureWriteQueryString("./<>?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("./<>#fragment#", null));
		assertEquals("", captureWriteQueryString("./<>#fragment?notParam", null));

		assertEquals("", captureWriteQueryString("", null));
		assertEquals("", captureWriteQueryString("?", null));
		assertEquals("param?&param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureWriteQueryString("#fragment#", null));
		assertEquals("", captureWriteQueryString("#fragment?notParam", null));
	}

	@Test
	public void testWriteQueryStringXhtmlEncoder() throws IOException {
		assertEquals("", captureWriteQueryString("htTP:&", testXmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&#fragment#", testXmlEncoder));
		assertEquals("", captureWriteQueryString("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("", captureWriteQueryString("./<>", testXmlEncoder));
		assertEquals("", captureWriteQueryString("./<>?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureWriteQueryString("./<>#fragment#", testXmlEncoder));
		assertEquals("", captureWriteQueryString("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureWriteQueryString("", testXmlEncoder));
		assertEquals("", captureWriteQueryString("?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureWriteQueryString("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureWriteQueryString("#fragment#", testXmlEncoder));
		assertEquals("", captureWriteQueryString("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendQueryStringOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendQueryString(out);
		return out.toString();
	}

	@Test
	public void testAppendQueryStringOut() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:"));
		assertEquals("", captureAppendQueryStringOut("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringOut("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringOut("./"));
		assertEquals("", captureAppendQueryStringOut("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("./#fragment#"));
		assertEquals("", captureAppendQueryStringOut("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringOut(""));
		assertEquals("", captureAppendQueryStringOut("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringOut("#fragment#"));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam"));
	}

	private static String captureAppendQueryStringOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendQueryString(out, encoder);
		return out.toString();
	}

	@Test
	public void testAppendQueryStringOutNullEncoder() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:&", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment?notParam", null));

		assertEquals("", captureAppendQueryStringOut("./<>", null));
		assertEquals("", captureAppendQueryStringOut("./<>?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendQueryStringOut("", null));
		assertEquals("", captureAppendQueryStringOut("?", null));
		assertEquals("param?&param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or", null));
		assertEquals("", captureAppendQueryStringOut("#fragment#", null));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendQueryStringOutXhtmlEncoder() throws IOException {
		assertEquals("", captureAppendQueryStringOut("htTP:&", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment#", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("", captureAppendQueryStringOut("./<>", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment#", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureAppendQueryStringOut("", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("?", testXmlEncoder));
		assertEquals("param?&amp;param2=value", captureAppendQueryStringOut("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("#fragment#", testXmlEncoder));
		assertEquals("", captureAppendQueryStringOut("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendQueryStringStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new SplitUrl(url).appendQueryString(sb);
		return sb.toString();
	}

	@Test
	public void testAppendQueryStringStringBuilder() throws IOException {
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuilder("./"));
		assertEquals("", captureAppendQueryStringStringBuilder("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("./#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuilder(""));
		assertEquals("", captureAppendQueryStringStringBuilder("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuilder("#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendQueryStringStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new SplitUrl(url).appendQueryString(sb);
		return sb.toString();
	}

	@Test
	public void testAppendQueryStringStringBuffer() throws IOException {
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("htTP:#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuffer("./"));
		assertEquals("", captureAppendQueryStringStringBuffer("./?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("./#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendQueryStringStringBuffer(""));
		assertEquals("", captureAppendQueryStringStringBuffer("?"));
		assertEquals("param?&param2=value", captureAppendQueryStringStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("", captureAppendQueryStringStringBuffer("#fragment#"));
		assertEquals("", captureAppendQueryStringStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Fragment">
	@Test
	public void testGetFragmentIndex() {
		assertEquals(-1, new SplitUrl("htTP:").getFragmentIndex());
		assertEquals(-1, new SplitUrl("htTP:?").getFragmentIndex());
		assertEquals(25, new SplitUrl("htTP:?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(5, new SplitUrl("htTP:#fragment#").getFragmentIndex());
		assertEquals(5, new SplitUrl("htTP:#fragment?notParam").getFragmentIndex());

		assertEquals(-1, new SplitUrl("./").getFragmentIndex());
		assertEquals(-1, new SplitUrl("./?").getFragmentIndex());
		assertEquals(22, new SplitUrl("./?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(2, new SplitUrl("./#fragment#").getFragmentIndex());
		assertEquals(2, new SplitUrl("./#fragment?notParam").getFragmentIndex());

		assertEquals(-1, new SplitUrl("").getFragmentIndex());
		assertEquals(-1, new SplitUrl("?").getFragmentIndex());
		assertEquals(20, new SplitUrl("?param?&param2=value#anc?h&or").getFragmentIndex());
		assertEquals(0, new SplitUrl("#fragment#").getFragmentIndex());
		assertEquals(0, new SplitUrl("#fragment?notParam").getFragmentIndex());
	}

	@Test
	public void testHasFragment() {
		assertFalse(new SplitUrl("htTP:").hasFragment());
		assertFalse(new SplitUrl("htTP:?").hasFragment());
		assertTrue(new SplitUrl("htTP:?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new SplitUrl("htTP:#fragment#").hasFragment());
		assertTrue(new SplitUrl("htTP:#fragment?notParam").hasFragment());

		assertFalse(new SplitUrl("./").hasFragment());
		assertFalse(new SplitUrl("./?").hasFragment());
		assertTrue(new SplitUrl("./?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new SplitUrl("./#fragment#").hasFragment());
		assertTrue(new SplitUrl("./#fragment?notParam").hasFragment());

		assertFalse(new SplitUrl("").hasFragment());
		assertFalse(new SplitUrl("?").hasFragment());
		assertTrue(new SplitUrl("?param?&param2=value#anc?h&or").hasFragment());
		assertTrue(new SplitUrl("#fragment#").hasFragment());
		assertTrue(new SplitUrl("#fragment?notParam").hasFragment());
	}

	@Test
	public void testGetFragment() {
		assertNull(new SplitUrl("htTP:").getFragment());
		assertNull(new SplitUrl("htTP:?").getFragment());
		assertEquals("anc?h&or", new SplitUrl("htTP:?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new SplitUrl("htTP:#fragment#").getFragment());
		assertEquals("fragment?notParam", new SplitUrl("htTP:#fragment?notParam").getFragment());

		assertNull(new SplitUrl("./").getFragment());
		assertNull(new SplitUrl("./?").getFragment());
		assertEquals("anc?h&or", new SplitUrl("./?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new SplitUrl("./#fragment#").getFragment());
		assertEquals("fragment?notParam", new SplitUrl("./#fragment?notParam").getFragment());

		assertNull(new SplitUrl("").getFragment());
		assertNull(new SplitUrl("?").getFragment());
		assertEquals("anc?h&or", new SplitUrl("?param?&param2=value#anc?h&or").getFragment());
		assertEquals("fragment#", new SplitUrl("#fragment#").getFragment());
		assertEquals("fragment?notParam", new SplitUrl("#fragment?notParam").getFragment());
	}

	private static String captureWriteFragment(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeFragment(out);
		return out.toString();
	}

	@Test
	public void testWriteFragment() throws IOException {
		assertEquals("", captureWriteFragment("htTP:"));
		assertEquals("", captureWriteFragment("htTP:?"));
		assertEquals("anc?h&or", captureWriteFragment("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:#fragment?notParam"));

		assertEquals("", captureWriteFragment("./"));
		assertEquals("", captureWriteFragment("./?"));
		assertEquals("anc?h&or", captureWriteFragment("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("./#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("./#fragment?notParam"));

		assertEquals("", captureWriteFragment(""));
		assertEquals("", captureWriteFragment("?"));
		assertEquals("anc?h&or", captureWriteFragment("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureWriteFragment("#fragment#"));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam"));
	}

	private static String captureWriteFragment(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).writeFragment(out, encoder);
		return out.toString();
	}

	@Test
	public void testWriteFragmentNullEncoder() throws IOException {
		assertEquals("", captureWriteFragment("htTP:&", null));
		assertEquals("", captureWriteFragment("htTP:&?", null));
		assertEquals("anc?h&or", captureWriteFragment("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("htTP:&#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:&#fragment?notParam", null));

		assertEquals("", captureWriteFragment("./<>", null));
		assertEquals("", captureWriteFragment("./<>?", null));
		assertEquals("anc?h&or", captureWriteFragment("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("./<>#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("./<>#fragment?notParam", null));

		assertEquals("", captureWriteFragment("", null));
		assertEquals("", captureWriteFragment("?", null));
		assertEquals("anc?h&or", captureWriteFragment("?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureWriteFragment("#fragment#", null));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam", null));
	}

	@Test
	public void testWriteFragmentXhtmlEncoder() throws IOException {
		assertEquals("", captureWriteFragment("htTP:&", testXmlEncoder));
		assertEquals("", captureWriteFragment("htTP:&?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureWriteFragment("htTP:&#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("", captureWriteFragment("./<>", testXmlEncoder));
		assertEquals("", captureWriteFragment("./<>?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureWriteFragment("./<>#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureWriteFragment("", testXmlEncoder));
		assertEquals("", captureWriteFragment("?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureWriteFragment("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureWriteFragment("#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureWriteFragment("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendFragmentOut(String url) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendFragment(out);
		return out.toString();
	}

	@Test
	public void testAppendFragmentOut() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:"));
		assertEquals("", captureAppendFragmentOut("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentOut("./"));
		assertEquals("", captureAppendFragmentOut("./?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentOut(""));
		assertEquals("", captureAppendFragmentOut("?"));
		assertEquals("anc?h&or", captureAppendFragmentOut("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam"));
	}

	private static String captureAppendFragmentOut(String url, Encoder encoder) throws IOException {
		StringWriter out = new StringWriter(url.length());
		new SplitUrl(url).appendFragment(out, encoder);
		return out.toString();
	}

	@Test
	public void testAppendFragmentOutNullEncoder() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:&", null));
		assertEquals("", captureAppendFragmentOut("htTP:&?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("htTP:&?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:&#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:&#fragment?notParam", null));

		assertEquals("", captureAppendFragmentOut("./<>", null));
		assertEquals("", captureAppendFragmentOut("./<>?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("./<>?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("./<>#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./<>#fragment?notParam", null));

		assertEquals("", captureAppendFragmentOut("", null));
		assertEquals("", captureAppendFragmentOut("?", null));
		assertEquals("anc?h&or", captureAppendFragmentOut("?param?&param2=value#anc?h&or", null));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#", null));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam", null));
	}

	@Test
	public void testAppendFragmentOutXhtmlEncoder() throws IOException {
		assertEquals("", captureAppendFragmentOut("htTP:&", testXmlEncoder));
		assertEquals("", captureAppendFragmentOut("htTP:&?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("htTP:&?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("htTP:&#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("htTP:&#fragment?notParam", testXmlEncoder));

		assertEquals("", captureAppendFragmentOut("./<>", testXmlEncoder));
		assertEquals("", captureAppendFragmentOut("./<>?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("./<>?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("./<>#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("./<>#fragment?notParam", testXmlEncoder));

		assertEquals("", captureAppendFragmentOut("", testXmlEncoder));
		assertEquals("", captureAppendFragmentOut("?", testXmlEncoder));
		assertEquals("anc?h&amp;or", captureAppendFragmentOut("?param?&param2=value#anc?h&or", testXmlEncoder));
		assertEquals("fragment#", captureAppendFragmentOut("#fragment#", testXmlEncoder));
		assertEquals("fragment?notParam", captureAppendFragmentOut("#fragment?notParam", testXmlEncoder));
	}

	private static String captureAppendFragmentStringBuilder(String url) throws IOException {
		StringBuilder sb = new StringBuilder(url.length());
		new SplitUrl(url).appendFragment(sb);
		return sb.toString();
	}

	@Test
	public void testAppendFragmentStringBuilder() throws IOException {
		assertEquals("", captureAppendFragmentStringBuilder("htTP:"));
		assertEquals("", captureAppendFragmentStringBuilder("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuilder("./"));
		assertEquals("", captureAppendFragmentStringBuilder("./?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuilder(""));
		assertEquals("", captureAppendFragmentStringBuilder("?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuilder("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuilder("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuilder("#fragment?notParam"));
	}

	private static String captureAppendFragmentStringBuffer(String url) throws IOException {
		StringBuffer sb = new StringBuffer(url.length());
		new SplitUrl(url).appendFragment(sb);
		return sb.toString();
	}

	@Test
	public void testAppendFragmentStringBuffer() throws IOException {
		assertEquals("", captureAppendFragmentStringBuffer("htTP:"));
		assertEquals("", captureAppendFragmentStringBuffer("htTP:?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("htTP:?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("htTP:#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("htTP:#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuffer("./"));
		assertEquals("", captureAppendFragmentStringBuffer("./?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("./?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("./#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("./#fragment?notParam"));

		assertEquals("", captureAppendFragmentStringBuffer(""));
		assertEquals("", captureAppendFragmentStringBuffer("?"));
		assertEquals("anc?h&or", captureAppendFragmentStringBuffer("?param?&param2=value#anc?h&or"));
		assertEquals("fragment#", captureAppendFragmentStringBuffer("#fragment#"));
		assertEquals("fragment?notParam", captureAppendFragmentStringBuffer("#fragment?notParam"));
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Encode/Decode URI">
	private static void testEncodeURI(String message, String asciiUrl, String unicodeUrl) throws UnsupportedEncodingException {
		assertSame(message, asciiUrl, new SplitUrl(asciiUrl).encodeURI(UrlUtils.ENCODING.name()).toString());
		assertEquals(message, asciiUrl, new SplitUrl(unicodeUrl).encodeURI(UrlUtils.ENCODING.name()).toString());
	}

	@Test
	public void testEncodeURI() throws UnsupportedEncodingException {
		testEncodeURI(
			null,
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A",
			"http://localhost/かおり"
		);
		testEncodeURI(
			null,
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?",
			"http://localhost/かおり?"
		);
		testEncodeURI(
			null,
			"://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?param?&param2=value#anc?h&or",
			"://localhost/かおり?param?&param2=value#anc?h&or"
		);
		testEncodeURI(
			null,
			"//localhost/%E3%81%8B%E3%81%8A%E3%82%8A#fragment#",
			"//localhost/かおり#fragment#"
		);
		testEncodeURI(
			null,
			"/%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam",
			"/かおり#fragment?notParam"
		);
		testEncodeURI(
			null,
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%%%",
			"かおり BBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Plus (+) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A+BBB#fragment?notParam%%%",
			"かおり+BBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Encoded plus (%2B) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB#fragment?notParam%%%",
			"かおり%2BBBB#fragment?notParam%%%"
		);
		testEncodeURI(
			"Encoded slash (%2F) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2FBBB#fragment?notParam%%%",
			"かおり%2FBBB#fragment?notParam%%%"
		);
		testEncodeURI(
			null,
			"?",
			"?"
		);
		testEncodeURI(
			null,
			"#",
			"#"
		);
		testEncodeURI(
			null,
			"",
			""
		);
	}

	private static void testDecodeURI(String message, String unicodeUrl, String asciiUrl) throws UnsupportedEncodingException {
		assertSame(message, unicodeUrl, new SplitUrl(unicodeUrl).decodeURI(UrlUtils.ENCODING.name()).toString());
		assertEquals(message, unicodeUrl, new SplitUrl(asciiUrl).decodeURI(UrlUtils.ENCODING.name()).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeURIInvalidEncoded() throws UnsupportedEncodingException {
		testDecodeURI(
			null,
			"かおり BBB#fragment?notParam%%%",
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%%%"
		);
	}

	@Test
	public void testDecodeURI() throws UnsupportedEncodingException {
		testDecodeURI(
			null,
			"http://localhost/かおり",
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A"
		);
		testDecodeURI(
			null,
			"http://localhost/かおり?",
			"http://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?"
		);
		testDecodeURI(
			null,
			"://localhost/かおり?param?&param2=value#anc?h&or",
			"://localhost/%E3%81%8B%E3%81%8A%E3%82%8A?param?&param2=value#anc?h&or"
		);
		testDecodeURI(
			null,
			"//localhost/かおり#fragment#",
			"//localhost/%E3%81%8B%E3%81%8A%E3%82%8A#fragment#"
		);
		testDecodeURI(
			null,
			"/かおり#fragment?notParam",
			"/%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam"
		);
		testDecodeURI(
			null,
			"かおり BBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Plus (+) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり+BBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A+BBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Encoded plus (%2B) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり%2BBBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			"Encoded slash (%2F) in hier-part must be left intact to avoid ambiguity between encode/decode",
			"かおり%2FBBB#fragment?notParam%25%25%25",
			"%E3%81%8B%E3%81%8A%E3%82%8A%2FBBB#fragment?notParam%25%25%25"
		);
		testDecodeURI(
			null,
			"?",
			"?"
		);
		testDecodeURI(
			null,
			"#",
			"#"
		);
		testDecodeURI(
			null,
			"",
			""
		);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Test Query String Mutators">
	private static void testSetQueryStringSame(String url, String newQuery) {
		SplitUrl splitUrl = new SplitUrl(url);
		assertSame(splitUrl, splitUrl.setQueryString(newQuery));
	}

	@Test
	public void testSetQueryStringSame() {
		testSetQueryStringSame("", null);
		testSetQueryStringSame("?", "");
		testSetQueryStringSame("#", null);
		testSetQueryStringSame("?#", "");

		testSetQueryStringSame("htTP:", null);
		testSetQueryStringSame("htTP:?", "");
		testSetQueryStringSame("htTP:?param?&param2=value#anc?h&or", "param?&param2=value");
		testSetQueryStringSame("htTP:#fragment#", null);
		testSetQueryStringSame("htTP:#fragment?notParam", null);
	}

	private static void testSetQueryStringRemoveQuery(String expected, String url) {
		assertEquals(expected, new SplitUrl(url).setQueryString(null).toString());
	}

	@Test
	public void testSetQueryStringRemoveQuery() {
		testSetQueryStringRemoveQuery("", "?");
		testSetQueryStringRemoveQuery("#", "?#");

		testSetQueryStringRemoveQuery("htTP:", "htTP:?");
		testSetQueryStringRemoveQuery("htTP:#anc?h&or", "htTP:?param?&param2=value#anc?h&or");
	}

	private static void testSetQueryStringReplaced(String expected, String url, String newQuery) {
		assertEquals(expected, new SplitUrl(url).setQueryString(newQuery).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetQueryStringReplacedNotAllowFragment() {
		testSetQueryStringReplaced("?##", "#", "#");
	}

	@Test
	public void testSetQueryStringReplaced() {
		testSetQueryStringReplaced("?new", "", "new");
		testSetQueryStringReplaced("??", "?", "?");
		testSetQueryStringReplaced("?new&other?other#", "?#", "new&other?other");

		testSetQueryStringReplaced("htTP:?new", "htTP:", "new");
		testSetQueryStringReplaced("htTP:?new", "htTP:?", "new");
		testSetQueryStringReplaced("htTP:?new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new");
		testSetQueryStringReplaced("htTP:?new#fragment#", "htTP:#fragment#", "new");
		testSetQueryStringReplaced("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new");
	}

	private static void testAddQueryStringSame(String url, String newQuery) {
		SplitUrl splitUrl = new SplitUrl(url);
		assertSame(splitUrl, splitUrl.addQueryString(newQuery));
	}

	public void testAddQueryStringSame() {
		testAddQueryStringSame("", null);
		testAddQueryStringSame("?", null);
		testAddQueryStringSame("#", null);
		testAddQueryStringSame("?#", null);

		testAddQueryStringSame("htTP:", null);
		testAddQueryStringSame("htTP:?", null);
		testAddQueryStringSame("htTP:?param?&param2=value#anc?h&or", null);
		testAddQueryStringSame("htTP:#fragment#", null);
		testAddQueryStringSame("htTP:#fragment?notParam", null);
	}

	private static void testAddQueryString(String expected, String url, String newQuery) {
		assertEquals(expected, new SplitUrl(url).addQueryString(newQuery).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddQueryStringNotAllowFragment() {
		testAddQueryString("?##", "#", "#");
	}

	@Test
	public void testAddQueryString() {
		testAddQueryString("?new", "", "new");
		testAddQueryString("?&?", "?", "?");
		testAddQueryString("?&new&other?other#", "?#", "new&other?other");

		testAddQueryString("htTP:?new", "htTP:", "new");
		testAddQueryString("htTP:?&new", "htTP:?", "new");
		testAddQueryString("htTP:?param?&param2=value&new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new");
		testAddQueryString("htTP:?new#fragment#", "htTP:#fragment#", "new");
		testAddQueryString("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new");
	}

	private static void testAddEncodedParameterSame(String url, String encodedName, String encodedValue) {
		SplitUrl splitUrl = new SplitUrl(url);
		assertSame(splitUrl, splitUrl.addEncodedParameter(encodedName, encodedValue));
	}

	public void testAddEncodedParameterSame() {
		testAddEncodedParameterSame("", null, null);
		testAddEncodedParameterSame("?", null, null);
		testAddEncodedParameterSame("#", null, null);
		testAddEncodedParameterSame("?#", null, null);

		testAddEncodedParameterSame("htTP:", null, null);
		testAddEncodedParameterSame("htTP:?", null, null);
		testAddEncodedParameterSame("htTP:?param?&param2=value#anc?h&or", null, null);
		testAddEncodedParameterSame("htTP:#fragment#", null, null);
		testAddEncodedParameterSame("htTP:#fragment?notParam", null, null);
	}

	private static void testAddEncodedParameter(String expected, String url, String encodedName, String encodedValue) {
		assertEquals(expected, new SplitUrl(url).addEncodedParameter(encodedName, encodedValue).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterNameNotAllowFragment() {
		testAddEncodedParameter("?##", "#", "#", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterValueNotAllowFragment() {
		testAddEncodedParameter("?name=##", "#", "name", "#");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEncodedParameterNullNameWithNonNullValue() {
		testAddEncodedParameter("/test?=value", "/test", null, "value");
	}

	@Test
	public void testAddEncodedParameter() {
		testAddEncodedParameter("?new", "", "new", null);
		testAddEncodedParameter("?new=", "", "new", "");
		testAddEncodedParameter("?new=val", "", "new", "val");
		testAddEncodedParameter("?&?", "?", "?", null);
		testAddEncodedParameter("?&?=", "?", "?", "");
		testAddEncodedParameter("?&?=val", "?", "?", "val");
		testAddEncodedParameter("?plo&other?other&new#", "?plo&other?other#", "new", null);
		testAddEncodedParameter("?plo&other?other&new=#", "?plo&other?other#", "new", "");
		testAddEncodedParameter("?plo&other?other&new=val#", "?plo&other?other#", "new", "val");

		testAddEncodedParameter("htTP:?new", "htTP:", "new", null);
		testAddEncodedParameter("htTP:?new=", "htTP:", "new", "");
		testAddEncodedParameter("htTP:?new=val", "htTP:", "new", "val");
		testAddEncodedParameter("htTP:?&new", "htTP:?", "new", null);
		testAddEncodedParameter("htTP:?&new=", "htTP:?", "new", "");
		testAddEncodedParameter("htTP:?&new=val", "htTP:?", "new", "val");
		testAddEncodedParameter("htTP:?param?&param2=value&new#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", null);
		testAddEncodedParameter("htTP:?param?&param2=value&new=#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", "");
		testAddEncodedParameter("htTP:?param?&param2=value&new=val#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "new", "val");
		testAddEncodedParameter("htTP:?new#fragment#", "htTP:#fragment#", "new", null);
		testAddEncodedParameter("htTP:?new=#fragment#", "htTP:#fragment#", "new", "");
		testAddEncodedParameter("htTP:?new=val#fragment#", "htTP:#fragment#", "new", "val");
		testAddEncodedParameter("htTP:?new#fragment?notParam", "htTP:#fragment?notParam", "new", null);
		testAddEncodedParameter("htTP:?new=#fragment?notParam", "htTP:#fragment?notParam", "new", "");
		testAddEncodedParameter("htTP:?new=val#fragment?notParam", "htTP:#fragment?notParam", "new", "val");
	}

	private static void testAddParameterSame(String url, String name, String value) throws UnsupportedEncodingException {
		SplitUrl splitUrl = new SplitUrl(url);
		assertSame(splitUrl, splitUrl.addParameter(name, value, UrlUtils.ENCODING.name()));
	}

	public void testAddParameterSame() throws UnsupportedEncodingException {
		testAddParameterSame("", null, null);
		testAddParameterSame("?", null, null);
		testAddParameterSame("#", null, null);
		testAddParameterSame("?#", null, null);

		testAddParameterSame("htTP:", null, null);
		testAddParameterSame("htTP:?", null, null);
		testAddParameterSame("htTP:?param?&param2=value#anc?h&or", null, null);
		testAddParameterSame("htTP:#fragment#", null, null);
		testAddParameterSame("htTP:#fragment?notParam", null, null);
	}

	private static void testAddParameter(String expected, String url, String name, String value) throws UnsupportedEncodingException {
		assertEquals(expected, new SplitUrl(url).addParameter(name, value, UrlUtils.ENCODING.name()).toString());
	}

	public void testAddParameterNameEncodesFragment() throws UnsupportedEncodingException {
		testAddParameter("?%23#", "#", "#", null);
	}

	public void testAddParameterValueEncodesFragment() throws UnsupportedEncodingException {
		testAddParameter("?name=%23#", "#", "name", "#");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddParameterNullNameWithNonNullValue() throws UnsupportedEncodingException {
		testAddParameter("/test?=value", "/test", null, "value");
	}

	@Test
	public void testAddParameter() throws UnsupportedEncodingException {
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A", "", "かおり", null);
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A=", "", "かおり", "");
		testAddParameter("?%E3%81%8B%E3%81%8A%E3%82%8A=val", "", "かおり", "val");
		testAddParameter("?&%3F", "?", "?", null);
		testAddParameter("?&%3F=", "?", "?", "");
		testAddParameter("?&%3F=val", "?", "?", "val");
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%20BBB#", "?plo&other?other#", "かおり BBB", null);
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%2BBBB=#", "?plo&other?other#", "かおり+BBB", "");
		testAddParameter("?plo&other?other&%E3%81%8B%E3%81%8A%E3%82%8A%3DBBB=%E8%8A%B1#", "?plo&other?other#", "かおり=BBB", "花");

		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A", "htTP:", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=", "htTP:", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1", "htTP:", "かおり", "花");
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A", "htTP:?", "かおり", null);
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A=", "htTP:?", "かおり", "");
		testAddParameter("htTP:?&%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1", "htTP:?", "かおり", "花");
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", null);
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A=#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", "");
		testAddParameter("htTP:?param?&param2=value&%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#anc?h&or", "htTP:?param?&param2=value#anc?h&or", "かおり", "花");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A#fragment#", "htTP:#fragment#", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=#fragment#", "htTP:#fragment#", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#fragment#", "htTP:#fragment#", "かおり", "花");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A#fragment?notParam", "htTP:#fragment?notParam", "かおり", null);
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=#fragment?notParam", "htTP:#fragment?notParam", "かおり", "");
		testAddParameter("htTP:?%E3%81%8B%E3%81%8A%E3%82%8A=%E8%8A%B1#fragment?notParam", "htTP:#fragment?notParam", "かおり", "花");
	}
	// </editor-fold>
}
