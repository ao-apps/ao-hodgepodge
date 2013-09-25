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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import junit.framework.TestCase;

/**
 * @author  AO Industries, Inc.
 */
abstract public class BufferWriterTest extends TestCase {

    public BufferWriterTest(String testName) {
        super(testName);
    }

	public static interface BufferWriterFactory {
		String getName();
		BufferWriter newBufferWriter();
	}

	public static void benchmarkSimulate(BufferWriterFactory factory) throws IOException {
		Writer out = new BufferedWriter(new FileWriter(new File("/dev/null")));
		try {
			final int loops = 1000;
			for(int i=1; i<=10; i++) {
				long startTime = System.nanoTime();
				for(int j=0; j<loops; j++) simulateCalls(factory, out);
				long endTime = System.nanoTime();
				System.out.println(factory.getName() + ": " + i + ": Simulated " + loops + " calls in " + BigDecimal.valueOf(endTime - startTime, 6)+" ms");
			}
		} finally {
			out.close();
		}
	}

	/**
	 * Performs the same set of calls that were performed in JSP request for:
	 *
	 * http://localhost:11156/essential-mining.com/purchase/domains.jsp?cartIndex=2&ui.lang=en&cookie%3AshoppingCart=jPAbu2Xc1JKVicbIGilVSW
	 */
	public static void simulateCalls(BufferWriterFactory factory, Writer out) throws IOException {
		BufferWriter writer1 = factory.newBufferWriter();
		writer1.write("Select Domains", 0, 14);
		writer1.flush();
		writer1.close();
		BufferResult result1 = writer1.getResult();
		BufferResult result2 = result1.trim();
		BufferWriter writer2 = factory.newBufferWriter();
		writer2.write("<!--2--><span id=\"EditableResourceBundleElement2\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(2, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(2);\">Select Domains</span>", 0, 322);
		writer2.flush();
		writer2.close();
		BufferResult result3 = writer2.getResult();
		BufferResult result4 = result3.trim();
		result2.toString();
		result4.toString();
		BufferWriter writer3 = factory.newBufferWriter();
		writer3.close();
		BufferResult result5 = writer3.getResult();
		BufferWriter writer4 = factory.newBufferWriter();
		writer4.close();
		BufferResult result6 = writer4.getResult();
		result2.toString();
		result4.toString();
		BufferWriter writer5 = factory.newBufferWriter();
		writer5.close();
		BufferResult result7 = writer5.getResult();
		BufferWriter writer6 = factory.newBufferWriter();
		writer6.close();
		BufferResult result8 = writer6.getResult();
		result2.toString();
		BufferWriter writer7 = factory.newBufferWriter();
		writer7.write("ESSENTIAL SUMMARIZER", 0, 20);
		writer7.flush();
		writer7.close();
		BufferResult result9 = writer7.getResult();
		BufferResult result10 = result9.trim();
		result10.writeTo(textInXhtmlAttributeEncoder, out);
		BufferWriter writer8 = factory.newBufferWriter();
		writer8.close();
		BufferResult result11 = writer8.getResult();
		result4.writeTo(textInXhtmlAttributeEncoder, out);
		BufferWriter writer9 = factory.newBufferWriter();
		writer9.write("contact@essential-mining.com", 0, 28);
		writer9.flush();
		writer9.close();
		BufferResult result12 = writer9.getResult();
		BufferResult result13 = result12.trim();
		result13.writeTo(textInXhtmlAttributeEncoder, out);
		BufferWriter writer10 = factory.newBufferWriter();
		writer10.close();
		BufferResult result14 = writer10.getResult();
		BufferWriter writer11 = factory.newBufferWriter();
		writer11.close();
		BufferResult result15 = writer11.getResult();
		BufferWriter writer12 = factory.newBufferWriter();
		writer12.close();
		BufferResult result16 = writer12.getResult();
		BufferWriter writer13 = factory.newBufferWriter();
		writer13.close();
		BufferResult result17 = writer13.getResult();
		BufferWriter writer14 = factory.newBufferWriter();
		writer14.close();
		BufferResult result18 = writer14.getResult();
		BufferWriter writer15 = factory.newBufferWriter();
		writer15.close();
		BufferResult result19 = writer15.getResult();
		BufferWriter writer16 = factory.newBufferWriter();
		writer16.close();
		BufferResult result20 = writer16.getResult();
		BufferWriter writer17 = factory.newBufferWriter();
		writer17.close();
		BufferResult result21 = writer17.getResult();
		BufferWriter writer18 = factory.newBufferWriter();
		writer18.close();
		BufferResult result22 = writer18.getResult();
		BufferWriter writer19 = factory.newBufferWriter();
		writer19.close();
		BufferResult result23 = writer19.getResult();
		BufferWriter writer20 = factory.newBufferWriter();
		writer20.close();
		BufferResult result24 = writer20.getResult();
		BufferWriter writer21 = factory.newBufferWriter();
		writer21.close();
		BufferResult result25 = writer21.getResult();
		BufferWriter writer22 = factory.newBufferWriter();
		writer22.close();
		BufferResult result26 = writer22.getResult();
		BufferWriter writer23 = factory.newBufferWriter();
		writer23.close();
		BufferResult result27 = writer23.getResult();
		BufferWriter writer24 = factory.newBufferWriter();
		writer24.close();
		BufferResult result28 = writer24.getResult();
		BufferWriter writer25 = factory.newBufferWriter();
		writer25.close();
		BufferResult result29 = writer25.getResult();
		BufferWriter writer26 = factory.newBufferWriter();
		writer26.close();
		BufferResult result30 = writer26.getResult();
		BufferWriter writer27 = factory.newBufferWriter();
		writer27.close();
		BufferResult result31 = writer27.getResult();
		BufferWriter writer28 = factory.newBufferWriter();
		writer28.close();
		BufferResult result32 = writer28.getResult();
		BufferWriter writer29 = factory.newBufferWriter();
		writer29.close();
		BufferResult result33 = writer29.getResult();
		BufferWriter writer30 = factory.newBufferWriter();
		writer30.close();
		BufferResult result34 = writer30.getResult();
		BufferWriter writer31 = factory.newBufferWriter();
		writer31.close();
		BufferResult result35 = writer31.getResult();
		result2.toString();
		result4.toString();
		result2.toString();
		result4.toString();
		BufferWriter writer32 = factory.newBufferWriter();
		writer32.close();
		BufferResult result36 = writer32.getResult();
		BufferWriter writer33 = factory.newBufferWriter();
		writer33.write("\n", 0, 1);
		writer33.write("\t\t\t\t", 0, 4);
		writer33.write("\n", 0, 1);
		writer33.write("\t\t\t\t\t", 0, 5);
		BufferWriter writer34 = factory.newBufferWriter();
		writer34.close();
		BufferResult result37 = writer34.getResult();
		writer33.write("\n", 0, 1);
		writer33.write("\t\t\t\t", 0, 4);
		writer33.write(new char[] {
			'\n','\n','\n','\n',' ',' ',' ',' ','\n',' ',' ',' ',' ',' ',' ',' ',' ','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','<','!','-','-','6','-','-','>','<','s','p','a','n',' ','i','d','=','\"','E','d'
			,'i','t','a','b','l','e','R','e','s','o','u','r','c','e','B','u','n','d','l','e','E','l','e','m','e','n','t','6','\"',' ','o','n','m','o','u','s','e','o','v','e','r','=','\"','i','f','(','t','y','p','e'
			,'o','f',' ','E','d','i','t','a','b','l','e','R','e','s','o','u','r','c','e','B','u','n','d','l','e','H','i','g','h','l','i','g','h','t','A','l','l',' ','=','=',' ','&','#','3','9',';','f','u','n','c'
			,'t','i','o','n','&','#','3','9',';',')',' ','E','d','i','t','a','b','l','e','R','e','s','o','u','r','c','e','B','u','n','d','l','e','H','i','g','h','l','i','g','h','t','A','l','l','(','6',',',' ','t'
			,'r','u','e',')',';','\"',' ','o','n','m','o','u','s','e','o','u','t','=','\"','i','f','(','t','y','p','e','o','f',' ','E','d','i','t','a','b','l','e','R','e','s','o','u','r','c','e','B','u','n','d','l'
			,'e','U','n','h','i','g','h','l','i','g','h','t','A','l','l',' ','=','=',' ','&','#','3','9',';','f','u','n','c','t','i','o','n','&','#','3','9',';',')',' ','E','d','i','t','a','b','l','e','R','e','s'
			,'o','u','r','c','e','B','u','n','d','l','e','U','n','h','i','g','h','l','i','g','h','t','A','l','l','(','6',')',';','\"','>','B','u','y',' ','E','s','s','e','n','t','i','a','l',' ','S','u','m','m','a'
			,'r','i','z','e','r',' ','-',' ','P','e','r','s','o','n','a','l',' ','E','d','i','t','i','o','n',' ','L','i','c','e','n','s','e','<','/','s','p','a','n','>','\n',' ',' ',' ',' ',' ',' ',' ',' ','\n',' '
			,' ',' ',' ',' ',' ',' ',' ','\n',' ',' ',' ',' ',' ',' ',' ',' ','\n',' ',' ',' ',' ','\n','\n'
		}, 0, 423);
		writer33.write("\n", 0, 1);
		writer33.write("\t\t\t", 0, 3);
		writer33.flush();
		writer33.close();
		BufferResult result38 = writer33.getResult();
		BufferResult result39 = result38.trim();
		result39.toString();
		BufferWriter writer35 = factory.newBufferWriter();
		writer35.close();
		BufferResult result40 = writer35.getResult();
		BufferResult result41 = result40.trim();
		BufferWriter writer36 = factory.newBufferWriter();
		writer36.close();
		BufferResult result42 = writer36.getResult();
		BufferResult result43 = result42.trim();
		result41.toString();
		result43.toString();
		BufferWriter writer37 = factory.newBufferWriter();
		writer37.write("<!--7--><span id=\"EditableResourceBundleElement7\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(7, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(7);\">home</span>", 0, 312);
		writer37.flush();
		writer37.close();
		BufferResult result44 = writer37.getResult();
		result44.writeTo(out);
		BufferWriter writer38 = factory.newBufferWriter();
		writer38.write("<!--8--><span id=\"EditableResourceBundleElement8\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(8, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(8);\">products</span>", 0, 316);
		writer38.flush();
		writer38.close();
		BufferResult result45 = writer38.getResult();
		result45.writeTo(out);
		BufferWriter writer39 = factory.newBufferWriter();
		writer39.write("<!--9--><span id=\"EditableResourceBundleElement9\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(9, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(9);\">download</span>", 0, 316);
		writer39.flush();
		writer39.close();
		BufferResult result46 = writer39.getResult();
		result46.writeTo(out);
		BufferWriter writer40 = factory.newBufferWriter();
		writer40.write("<!--10--><span id=\"EditableResourceBundleElement10\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(10, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(10);\">what's new?</span>", 0, 323);
		writer40.flush();
		writer40.close();
		BufferResult result47 = writer40.getResult();
		result47.writeTo(out);
		BufferWriter writer41 = factory.newBufferWriter();
		writer41.write("<!--11--><span id=\"EditableResourceBundleElement11\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(11, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(11);\">contact us</span>", 0, 322);
		writer41.flush();
		writer41.close();
		BufferResult result48 = writer41.getResult();
		result48.writeTo(out);
		result41.toString();
		BufferWriter writer42 = factory.newBufferWriter();
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer43 = factory.newBufferWriter();
		writer43.close();
		BufferResult result49 = writer43.getResult();
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer44 = factory.newBufferWriter();
		writer44.close();
		BufferResult result50 = writer44.getResult();
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer42.write("العربية", 0, 7);
		writer42.write("\n", 0, 1);
		writer42.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer42.flush();
		writer42.close();
		BufferResult result51 = writer42.getResult();
		result51.writeTo(out);
		BufferWriter writer45 = factory.newBufferWriter();
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer46 = factory.newBufferWriter();
		writer46.close();
		BufferResult result52 = writer46.getResult();
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer47 = factory.newBufferWriter();
		writer47.close();
		BufferResult result53 = writer47.getResult();
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer45.write("Deutsch", 0, 7);
		writer45.write("\n", 0, 1);
		writer45.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer45.flush();
		writer45.close();
		BufferResult result54 = writer45.getResult();
		result54.writeTo(out);
		BufferWriter writer48 = factory.newBufferWriter();
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer49 = factory.newBufferWriter();
		writer49.close();
		BufferResult result55 = writer49.getResult();
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer50 = factory.newBufferWriter();
		writer50.close();
		BufferResult result56 = writer50.getResult();
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer48.write("English", 0, 7);
		writer48.write("\n", 0, 1);
		writer48.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer48.flush();
		writer48.close();
		BufferResult result57 = writer48.getResult();
		result57.writeTo(out);
		BufferWriter writer51 = factory.newBufferWriter();
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer52 = factory.newBufferWriter();
		writer52.close();
		BufferResult result58 = writer52.getResult();
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer53 = factory.newBufferWriter();
		writer53.close();
		BufferResult result59 = writer53.getResult();
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer51.write("Español", 0, 7);
		writer51.write("\n", 0, 1);
		writer51.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer51.flush();
		writer51.close();
		BufferResult result60 = writer51.getResult();
		result60.writeTo(out);
		BufferWriter writer54 = factory.newBufferWriter();
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer55 = factory.newBufferWriter();
		writer55.close();
		BufferResult result61 = writer55.getResult();
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer56 = factory.newBufferWriter();
		writer56.close();
		BufferResult result62 = writer56.getResult();
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer54.write("Français", 0, 8);
		writer54.write("\n", 0, 1);
		writer54.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer54.flush();
		writer54.close();
		BufferResult result63 = writer54.getResult();
		result63.writeTo(out);
		BufferWriter writer57 = factory.newBufferWriter();
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer58 = factory.newBufferWriter();
		writer58.close();
		BufferResult result64 = writer58.getResult();
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer59 = factory.newBufferWriter();
		writer59.close();
		BufferResult result65 = writer59.getResult();
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer57.write("Italiano", 0, 8);
		writer57.write("\n", 0, 1);
		writer57.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer57.flush();
		writer57.close();
		BufferResult result66 = writer57.getResult();
		result66.writeTo(out);
		BufferWriter writer60 = factory.newBufferWriter();
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer61 = factory.newBufferWriter();
		writer61.close();
		BufferResult result67 = writer61.getResult();
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer62 = factory.newBufferWriter();
		writer62.close();
		BufferResult result68 = writer62.getResult();
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer60.write("日本語", 0, 3);
		writer60.write("\n", 0, 1);
		writer60.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer60.flush();
		writer60.close();
		BufferResult result69 = writer60.getResult();
		result69.writeTo(out);
		BufferWriter writer63 = factory.newBufferWriter();
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer64 = factory.newBufferWriter();
		writer64.close();
		BufferResult result70 = writer64.getResult();
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer65 = factory.newBufferWriter();
		writer65.close();
		BufferResult result71 = writer65.getResult();
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer63.write("Português", 0, 9);
		writer63.write("\n", 0, 1);
		writer63.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer63.flush();
		writer63.close();
		BufferResult result72 = writer63.getResult();
		result72.writeTo(out);
		BufferWriter writer66 = factory.newBufferWriter();
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		BufferWriter writer67 = factory.newBufferWriter();
		writer67.close();
		BufferResult result73 = writer67.getResult();
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 15);
		BufferWriter writer68 = factory.newBufferWriter();
		writer68.close();
		BufferResult result74 = writer68.getResult();
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 14);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t\t", 0, 13);
		writer66.write("中文", 0, 2);
		writer66.write("\n", 0, 1);
		writer66.write("\t\t\t\t\t\t\t\t\t\t\t\t", 0, 12);
		writer66.flush();
		writer66.close();
		BufferResult result75 = writer66.getResult();
		result75.writeTo(out);
		result43.toString();
		result41.toString();
		result43.toString();
		result39.toString();
		BufferWriter writer69 = factory.newBufferWriter();
		writer69.write("\n", 0, 1);
		writer69.write("\t\t\t\t\t\t\t", 0, 7);
		BufferWriter writer70 = factory.newBufferWriter();
		writer70.write("\n", 0, 1);
		writer70.write("\t\t\t\t\t\t\t\t", 0, 8);
		BufferWriter writer71 = factory.newBufferWriter();
		writer71.write("Logo", 0, 4);
		writer71.flush();
		writer71.close();
		BufferResult result76 = writer71.getResult();
		BufferResult result77 = result76.trim();
		writer70.write("\n", 0, 1);
		writer70.write("\t\t\t\t\t\t\t\t", 0, 8);
		BufferWriter writer72 = factory.newBufferWriter();
		writer72.write(49);
		writer72.write(54);
		writer72.write(48);
		writer72.flush();
		writer72.close();
		BufferResult result78 = writer72.getResult();
		BufferResult result79 = result78.trim();
		writer70.write("\n", 0, 1);
		writer70.write("\t\t\t\t\t\t\t\t", 0, 8);
		BufferWriter writer73 = factory.newBufferWriter();
		writer73.write(49);
		writer73.write(52);
		writer73.write(53);
		writer73.flush();
		writer73.close();
		BufferResult result80 = writer73.getResult();
		BufferResult result81 = result80.trim();
		writer70.write("\n", 0, 1);
		writer70.write("\t\t\t\t\t\t\t", 0, 7);
		writer70.flush();
		writer70.close();
		BufferResult result82 = writer70.getResult();
		writer69.write("<img src=\"", 0, 10);
		writer69.write("/essential-mining.com/images/emlogo_1.png", 0, 41);
		writer69.write("\" width=\"", 0, 9);
		result79.writeTo(textInXhtmlAttributeEncoder, out);
		writer69.write(new char[] {
			'1','6','0'
		}, 0, 3);
		writer69.write("\" height=\"", 0, 10);
		result81.writeTo(textInXhtmlAttributeEncoder, out);
		writer69.write(new char[] {
			'1','4','5'
		}, 0, 3);
		writer69.write("\" alt=\"", 0, 7);
		result77.writeTo(textInXhtmlAttributeEncoder, out);
		writer69.write(new char[] {
			'L','o','g','o'
		}, 0, 4);
		writer69.write(34);
		writer69.write(" />", 0, 3);
		writer69.write("\n", 0, 1);
		writer69.write("\t\t\t\t\t\t", 0, 6);
		writer69.flush();
		writer69.close();
		BufferResult result83 = writer69.getResult();
		result83.writeTo(out);
		result39.toString();
		BufferWriter writer74 = factory.newBufferWriter();
		writer74.close();
		BufferResult result84 = writer74.getResult();
		BufferWriter writer75 = factory.newBufferWriter();
		writer75.close();
		BufferResult result85 = writer75.getResult();
		BufferWriter writer76 = factory.newBufferWriter();
		writer76.write("\n", 0, 1);
		writer76.write("                                        ", 0, 40);
		BufferWriter writer77 = factory.newBufferWriter();
		writer77.write("/purchase/images/step_1_completed_start.png", 0, 43);
		writer77.flush();
		writer77.close();
		BufferResult result86 = writer77.getResult();
		BufferResult result87 = result86.trim();
		result87.toString();
		writer76.write("\n", 0, 1);
		writer76.write("                                    ", 0, 36);
		writer76.flush();
		writer76.close();
		BufferResult result88 = writer76.getResult();
		BufferWriter writer78 = factory.newBufferWriter();
		writer78.close();
		BufferResult result89 = writer78.getResult();
		BufferWriter writer79 = factory.newBufferWriter();
		writer79.close();
		BufferResult result90 = writer79.getResult();
		BufferWriter writer80 = factory.newBufferWriter();
		writer80.write("\n", 0, 1);
		writer80.write("                                        ", 0, 40);
		BufferWriter writer81 = factory.newBufferWriter();
		writer81.write("/purchase/images/step_4_inactive_end.png", 0, 40);
		writer81.flush();
		writer81.close();
		BufferResult result91 = writer81.getResult();
		BufferResult result92 = result91.trim();
		result92.toString();
		writer80.write("\n", 0, 1);
		writer80.write("                                    ", 0, 36);
		writer80.flush();
		writer80.close();
		BufferResult result93 = writer80.getResult();
		BufferWriter writer82 = factory.newBufferWriter();
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t<div>\n", 0, 10);
		writer82.write("\t\t\t\t\t", 0, 5);
		BufferWriter writer83 = factory.newBufferWriter();
		writer83.close();
		BufferResult result94 = writer83.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("hidden", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("cartIndex", 0, 9);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("2", 0, 1);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		BufferWriter writer84 = factory.newBufferWriter();
		writer84.close();
		BufferResult result95 = writer84.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("hidden", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("l", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("ar", 0, 2);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		BufferWriter writer85 = factory.newBufferWriter();
		writer85.close();
		BufferResult result96 = writer85.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("hidden", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("l", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("zh", 0, 2);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		BufferWriter writer86 = factory.newBufferWriter();
		writer86.close();
		BufferResult result97 = writer86.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("hidden", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("l", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("en", 0, 2);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		BufferWriter writer87 = factory.newBufferWriter();
		writer87.close();
		BufferResult result98 = writer87.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("hidden", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("l", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("fr", 0, 2);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t</div>\n", 0, 11);
		writer82.write("                ", 0, 16);
		writer82.write(new char[] {
			'\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n','\n'
		}, 0, 16);
		writer82.write("               \n", 0, 16);
		writer82.write("                <div style=\"text-align:center; margin-top:1em\">\n", 0, 64);
		writer82.write("                    ", 0, 20);
		BufferWriter writer88 = factory.newBufferWriter();
		writer88.write("<< Back", 0, 7);
		writer88.flush();
		writer88.close();
		BufferResult result99 = writer88.getResult();
		BufferResult result100 = result99.trim();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("submit", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("back", 0, 4);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		result100.writeTo(textInXhtmlAttributeEncoder, out);
		writer82.write("&lt;", 0, 4);
		writer82.write("&lt;", 0, 4);
		writer82.write(new char[] {
			'<','<',' ','B','a','c','k'
		}, 2, 5);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("                    &nbsp;\n", 0, 27);
		writer82.write("                    ", 0, 20);
		BufferWriter writer89 = factory.newBufferWriter();
		writer89.write("Next >>", 0, 7);
		writer89.flush();
		writer89.close();
		BufferResult result101 = writer89.getResult();
		BufferResult result102 = result101.trim();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("submit", 0, 6);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		result102.writeTo(textInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'N','e','x','t',' '
		}, 0, 5);
		writer82.write("&gt;", 0, 4);
		writer82.write("&gt;", 0, 4);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("                </div>               \n", 0, 38);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t", 0, 7);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                        <div style=\"font-weight:bold\">", 0, 54);
		writer82.write(new char[] {
			'A','r','a','b','i','c'
		}, 0, 6);
		writer82.write("</div>\n", 0, 7);
		writer82.write("                        <div style=\"margin-left:1em; margin-right:1em\">\n", 0, 72);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                    ", 0, 36);
		writer82.write("<!--86--><span id=\"EditableResourceBundleElement1558\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(1558, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(1558);\">No domains available.</span>", 0, 339);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                        </div>\n", 0, 31);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t", 0, 7);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                        <div style=\"font-weight:bold\">", 0, 54);
		writer82.write(new char[] {
			'C','h','i','n','e','s','e'
		}, 0, 7);
		writer82.write("</div>\n", 0, 7);
		writer82.write("                        <div style=\"margin-left:1em; margin-right:1em\">\n", 0, 72);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                    ", 0, 36);
		writer82.write("<!--86--><span id=\"EditableResourceBundleElement1560\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(1560, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(1560);\">No domains available.</span>", 0, 339);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                        </div>\n", 0, 31);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t", 0, 7);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                        <div style=\"font-weight:bold\">", 0, 54);
		writer82.write(new char[] {
			'E','n','g','l','i','s','h'
		}, 0, 7);
		writer82.write("</div>\n", 0, 7);
		writer82.write("                        <div style=\"margin-left:1em; margin-right:1em\">\n", 0, 72);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer90 = factory.newBufferWriter();
		writer90.write("\n", 0, 1);
		writer90.write("                                                ", 0, 48);
		BufferWriter writer91 = factory.newBufferWriter();
		writer91.write("\n", 0, 1);
		writer91.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer91.write("                                                    return true;\n", 0, 65);
		writer91.write("                                                ", 0, 48);
		writer91.flush();
		writer91.close();
		BufferResult result103 = writer91.getResult();
		BufferResult result104 = result103.trim();
		writer90.write("\n", 0, 1);
		writer90.write("                                            ", 0, 44);
		writer90.flush();
		writer90.close();
		BufferResult result105 = writer90.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enbk", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enbk", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result104.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enbk", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'B','a','n','k','i','n','g'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer92 = factory.newBufferWriter();
		writer92.write("\n", 0, 1);
		writer92.write("                                                ", 0, 48);
		BufferWriter writer93 = factory.newBufferWriter();
		writer93.write("\n", 0, 1);
		writer93.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer93.write("                                                    return true;\n", 0, 65);
		writer93.write("                                                ", 0, 48);
		writer93.flush();
		writer93.close();
		BufferResult result106 = writer93.getResult();
		BufferResult result107 = result106.trim();
		writer92.write("\n", 0, 1);
		writer92.write("                                            ", 0, 44);
		writer92.flush();
		writer92.close();
		BufferResult result108 = writer92.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enbc", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enbc", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result107.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enbc", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'B','u','s','i','n','e','s','s'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer94 = factory.newBufferWriter();
		writer94.write("\n", 0, 1);
		writer94.write("                                                ", 0, 48);
		BufferWriter writer95 = factory.newBufferWriter();
		writer95.write("\n", 0, 1);
		writer95.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer95.write("                                                    return true;\n", 0, 65);
		writer95.write("                                                ", 0, 48);
		writer95.flush();
		writer95.close();
		BufferResult result109 = writer95.getResult();
		BufferResult result110 = result109.trim();
		writer94.write("\n", 0, 1);
		writer94.write("                                            ", 0, 44);
		writer94.flush();
		writer94.close();
		BufferResult result111 = writer94.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_entr", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("entr", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result110.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("entr", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','m','m','e','r','c','e'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer96 = factory.newBufferWriter();
		writer96.write("\n", 0, 1);
		writer96.write("                                                ", 0, 48);
		BufferWriter writer97 = factory.newBufferWriter();
		writer97.write("\n", 0, 1);
		writer97.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer97.write("                                                    return true;\n", 0, 65);
		writer97.write("                                                ", 0, 48);
		writer97.flush();
		writer97.close();
		BufferResult result112 = writer97.getResult();
		BufferResult result113 = result112.trim();
		writer96.write("\n", 0, 1);
		writer96.write("                                            ", 0, 44);
		writer96.flush();
		writer96.close();
		BufferResult result114 = writer96.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enco", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enco", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result113.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enco", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','m','m','u','n','i','c','a','t','i','o','n'
		}, 0, 13);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer98 = factory.newBufferWriter();
		writer98.write("\n", 0, 1);
		writer98.write("                                                ", 0, 48);
		BufferWriter writer99 = factory.newBufferWriter();
		writer99.write("\n", 0, 1);
		writer99.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer99.write("                                                    return true;\n", 0, 65);
		writer99.write("                                                ", 0, 48);
		writer99.flush();
		writer99.close();
		BufferResult result115 = writer99.getResult();
		BufferResult result116 = result115.trim();
		writer98.write("\n", 0, 1);
		writer98.write("                                            ", 0, 44);
		writer98.flush();
		writer98.close();
		BufferResult result117 = writer98.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enec", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enec", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result116.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enec", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','c','o','n','o','m','y'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer100 = factory.newBufferWriter();
		writer100.write("\n", 0, 1);
		writer100.write("                                                ", 0, 48);
		BufferWriter writer101 = factory.newBufferWriter();
		writer101.write("\n", 0, 1);
		writer101.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer101.write("                                                    return true;\n", 0, 65);
		writer101.write("                                                ", 0, 48);
		writer101.flush();
		writer101.close();
		BufferResult result118 = writer101.getResult();
		BufferResult result119 = result118.trim();
		writer100.write("\n", 0, 1);
		writer100.write("                                            ", 0, 44);
		writer100.flush();
		writer100.close();
		BufferResult result120 = writer100.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_ence", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("ence", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result119.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("ence", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','d','u','c','a','t','i','o','n'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer102 = factory.newBufferWriter();
		writer102.write("\n", 0, 1);
		writer102.write("                                                ", 0, 48);
		BufferWriter writer103 = factory.newBufferWriter();
		writer103.write("\n", 0, 1);
		writer103.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer103.write("                                                    return true;\n", 0, 65);
		writer103.write("                                                ", 0, 48);
		writer103.flush();
		writer103.close();
		BufferResult result121 = writer103.getResult();
		BufferResult result122 = result121.trim();
		writer102.write("\n", 0, 1);
		writer102.write("                                            ", 0, 44);
		writer102.flush();
		writer102.close();
		BufferResult result123 = writer102.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enwc", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enwc", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result122.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enwc", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','m','p','l','o','y','m','e','n','t'
		}, 0, 10);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer104 = factory.newBufferWriter();
		writer104.write("\n", 0, 1);
		writer104.write("                                                ", 0, 48);
		BufferWriter writer105 = factory.newBufferWriter();
		writer105.write("\n", 0, 1);
		writer105.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer105.write("                                                    return true;\n", 0, 65);
		writer105.write("                                                ", 0, 48);
		writer105.flush();
		writer105.close();
		BufferResult result124 = writer105.getResult();
		BufferResult result125 = result124.trim();
		writer104.write("\n", 0, 1);
		writer104.write("                                            ", 0, 44);
		writer104.flush();
		writer104.close();
		BufferResult result126 = writer104.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enen", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enen", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result125.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enen", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','e','r','g','y'
		}, 0, 6);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer106 = factory.newBufferWriter();
		writer106.write("\n", 0, 1);
		writer106.write("                                                ", 0, 48);
		BufferWriter writer107 = factory.newBufferWriter();
		writer107.write("\n", 0, 1);
		writer107.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer107.write("                                                    return true;\n", 0, 65);
		writer107.write("                                                ", 0, 48);
		writer107.flush();
		writer107.close();
		BufferResult result127 = writer107.getResult();
		BufferResult result128 = result127.trim();
		writer106.write("\n", 0, 1);
		writer106.write("                                            ", 0, 44);
		writer106.flush();
		writer106.close();
		BufferResult result129 = writer106.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enbu", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enbu", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result128.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enbu", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','t','e','r','p','r','i','s','e'
		}, 0, 10);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer108 = factory.newBufferWriter();
		writer108.write("\n", 0, 1);
		writer108.write("                                                ", 0, 48);
		BufferWriter writer109 = factory.newBufferWriter();
		writer109.write("\n", 0, 1);
		writer109.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer109.write("                                                    return true;\n", 0, 65);
		writer109.write("                                                ", 0, 48);
		writer109.flush();
		writer109.close();
		BufferResult result130 = writer109.getResult();
		BufferResult result131 = result130.trim();
		writer108.write("\n", 0, 1);
		writer108.write("                                            ", 0, 44);
		writer108.flush();
		writer108.close();
		BufferResult result132 = writer108.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enev", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enev", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result131.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enev", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','v','i','r','o','n','m','e','n','t'
		}, 0, 11);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer110 = factory.newBufferWriter();
		writer110.write("\n", 0, 1);
		writer110.write("                                                ", 0, 48);
		BufferWriter writer111 = factory.newBufferWriter();
		writer111.write("\n", 0, 1);
		writer111.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer111.write("                                                    return true;\n", 0, 65);
		writer111.write("                                                ", 0, 48);
		writer111.flush();
		writer111.close();
		BufferResult result133 = writer111.getResult();
		BufferResult result134 = result133.trim();
		writer110.write("\n", 0, 1);
		writer110.write("                                            ", 0, 44);
		writer110.flush();
		writer110.close();
		BufferResult result135 = writer110.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_eneu", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("eneu", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result134.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("eneu", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','u','r','o','p','e','a','n',' ','u','n','i','o','n'
		}, 0, 14);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer112 = factory.newBufferWriter();
		writer112.write("\n", 0, 1);
		writer112.write("                                                ", 0, 48);
		BufferWriter writer113 = factory.newBufferWriter();
		writer113.write("\n", 0, 1);
		writer113.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer113.write("                                                    return true;\n", 0, 65);
		writer113.write("                                                ", 0, 48);
		writer113.flush();
		writer113.close();
		BufferResult result136 = writer113.getResult();
		BufferResult result137 = result136.trim();
		writer112.write("\n", 0, 1);
		writer112.write("                                            ", 0, 44);
		writer112.flush();
		writer112.close();
		BufferResult result138 = writer112.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enqf", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enqf", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result137.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enqf", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','a','m','i','l','y'
		}, 0, 6);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer114 = factory.newBufferWriter();
		writer114.write("\n", 0, 1);
		writer114.write("                                                ", 0, 48);
		BufferWriter writer115 = factory.newBufferWriter();
		writer115.write("\n", 0, 1);
		writer115.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer115.write("                                                    return true;\n", 0, 65);
		writer115.write("                                                ", 0, 48);
		writer115.flush();
		writer115.close();
		BufferResult result139 = writer115.getResult();
		BufferResult result140 = result139.trim();
		writer114.write("\n", 0, 1);
		writer114.write("                                            ", 0, 44);
		writer114.flush();
		writer114.close();
		BufferResult result141 = writer114.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enfi", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enfi", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result140.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enfi", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','i','n','a','n','c','e'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer116 = factory.newBufferWriter();
		writer116.write("\n", 0, 1);
		writer116.write("                                                ", 0, 48);
		BufferWriter writer117 = factory.newBufferWriter();
		writer117.write("\n", 0, 1);
		writer117.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer117.write("                                                    return true;\n", 0, 65);
		writer117.write("                                                ", 0, 48);
		writer117.flush();
		writer117.close();
		BufferResult result142 = writer117.getResult();
		BufferResult result143 = result142.trim();
		writer116.write("\n", 0, 1);
		writer116.write("                                            ", 0, 44);
		writer116.flush();
		writer116.close();
		BufferResult result144 = writer116.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enfo", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enfo", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result143.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enfo", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','o','o','d'
		}, 0, 4);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer118 = factory.newBufferWriter();
		writer118.write("\n", 0, 1);
		writer118.write("                                                ", 0, 48);
		BufferWriter writer119 = factory.newBufferWriter();
		writer119.write("\n", 0, 1);
		writer119.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer119.write("                                                    return true;\n", 0, 65);
		writer119.write("                                                ", 0, 48);
		writer119.flush();
		writer119.close();
		BufferResult result145 = writer119.getResult();
		BufferResult result146 = result145.trim();
		writer118.write("\n", 0, 1);
		writer118.write("                                            ", 0, 44);
		writer118.flush();
		writer118.close();
		BufferResult result147 = writer118.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enff", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enff", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result146.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enff", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','o','r','e','s','t','r','y',' ','a','n','d',' ','f','i','s','h','e','r','i','e','s'
		}, 0, 22);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer120 = factory.newBufferWriter();
		writer120.write("\n", 0, 1);
		writer120.write("                                                ", 0, 48);
		BufferWriter writer121 = factory.newBufferWriter();
		writer121.write("\n", 0, 1);
		writer121.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer121.write("                                                    return true;\n", 0, 65);
		writer121.write("                                                ", 0, 48);
		writer121.flush();
		writer121.close();
		BufferResult result148 = writer121.getResult();
		BufferResult result149 = result148.trim();
		writer120.write("\n", 0, 1);
		writer120.write("                                            ", 0, 44);
		writer120.flush();
		writer120.close();
		BufferResult result150 = writer120.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enid", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enid", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result149.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enid", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','d','u','s','t','r','y'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer122 = factory.newBufferWriter();
		writer122.write("\n", 0, 1);
		writer122.write("                                                ", 0, 48);
		BufferWriter writer123 = factory.newBufferWriter();
		writer123.write("\n", 0, 1);
		writer123.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer123.write("                                                    return true;\n", 0, 65);
		writer123.write("                                                ", 0, 48);
		writer123.flush();
		writer123.close();
		BufferResult result151 = writer123.getResult();
		BufferResult result152 = result151.trim();
		writer122.write("\n", 0, 1);
		writer122.write("                                            ", 0, 44);
		writer122.flush();
		writer122.close();
		BufferResult result153 = writer122.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enio", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enio", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result152.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enio", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','t','e','r','n','a','t','i','o','n','a','l',' ','o','r','g','a','n','i','s','a','t','i','o','n','s'
		}, 0, 27);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer124 = factory.newBufferWriter();
		writer124.write("\n", 0, 1);
		writer124.write("                                                ", 0, 48);
		BufferWriter writer125 = factory.newBufferWriter();
		writer125.write("\n", 0, 1);
		writer125.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer125.write("                                                    return true;\n", 0, 65);
		writer125.write("                                                ", 0, 48);
		writer125.flush();
		writer125.close();
		BufferResult result154 = writer125.getResult();
		BufferResult result155 = result154.trim();
		writer124.write("\n", 0, 1);
		writer124.write("                                            ", 0, 44);
		writer124.flush();
		writer124.close();
		BufferResult result156 = writer124.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enir", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enir", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result155.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enir", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','t','e','r','n','a','t','i','o','n','a','l',' ','r','e','l','a','t','i','o','n','s'
		}, 0, 23);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer126 = factory.newBufferWriter();
		writer126.write("\n", 0, 1);
		writer126.write("                                                ", 0, 48);
		BufferWriter writer127 = factory.newBufferWriter();
		writer127.write("\n", 0, 1);
		writer127.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer127.write("                                                    return true;\n", 0, 65);
		writer127.write("                                                ", 0, 48);
		writer127.flush();
		writer127.close();
		BufferResult result157 = writer127.getResult();
		BufferResult result158 = result157.trim();
		writer126.write("\n", 0, 1);
		writer126.write("                                            ", 0, 44);
		writer126.flush();
		writer126.close();
		BufferResult result159 = writer126.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enla", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enla", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result158.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enla", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'L','a','w'
		}, 0, 3);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer128 = factory.newBufferWriter();
		writer128.write("\n", 0, 1);
		writer128.write("                                                ", 0, 48);
		BufferWriter writer129 = factory.newBufferWriter();
		writer129.write("\n", 0, 1);
		writer129.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer129.write("                                                    return true;\n", 0, 65);
		writer129.write("                                                ", 0, 48);
		writer129.flush();
		writer129.close();
		BufferResult result160 = writer129.getResult();
		BufferResult result161 = result160.trim();
		writer128.write("\n", 0, 1);
		writer128.write("                                            ", 0, 44);
		writer128.flush();
		writer128.close();
		BufferResult result162 = writer128.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enle", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enle", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result161.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enle", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'L','e','g','a','l'
		}, 0, 5);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer130 = factory.newBufferWriter();
		writer130.write("\n", 0, 1);
		writer130.write("                                                ", 0, 48);
		BufferWriter writer131 = factory.newBufferWriter();
		writer131.write("\n", 0, 1);
		writer131.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer131.write("                                                    return true;\n", 0, 65);
		writer131.write("                                                ", 0, 48);
		writer131.flush();
		writer131.close();
		BufferResult result163 = writer131.getResult();
		BufferResult result164 = result163.trim();
		writer130.write("\n", 0, 1);
		writer130.write("                                            ", 0, 44);
		writer130.flush();
		writer130.close();
		BufferResult result165 = writer130.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enme", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enme", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result164.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enme", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'M','e','d','i','c','i','n','e'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer132 = factory.newBufferWriter();
		writer132.write("\n", 0, 1);
		writer132.write("                                                ", 0, 48);
		BufferWriter writer133 = factory.newBufferWriter();
		writer133.write("\n", 0, 1);
		writer133.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer133.write("                                                    return true;\n", 0, 65);
		writer133.write("                                                ", 0, 48);
		writer133.flush();
		writer133.close();
		BufferResult result166 = writer133.getResult();
		BufferResult result167 = result166.trim();
		writer132.write("\n", 0, 1);
		writer132.write("                                            ", 0, 44);
		writer132.flush();
		writer132.close();
		BufferResult result168 = writer132.getResult();
		simulateCalls2(
			factory,
			out,
			writer82,
			result167
		);
	}
	
	private static void simulateCalls2(
		BufferWriterFactory factory,
		Writer out,
		BufferWriter writer82,
		BufferResult result167
	) throws IOException {
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enpt", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enpt", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result167.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enpt", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'P','o','l','i','t','i','c','s'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer134 = factory.newBufferWriter();
		writer134.write("\n", 0, 1);
		writer134.write("                                                ", 0, 48);
		BufferWriter writer135 = factory.newBufferWriter();
		writer135.write("\n", 0, 1);
		writer135.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer135.write("                                                    return true;\n", 0, 65);
		writer135.write("                                                ", 0, 48);
		writer135.flush();
		writer135.close();
		BufferResult result169 = writer135.getResult();
		BufferResult result170 = result169.trim();
		writer134.write("\n", 0, 1);
		writer134.write("                                            ", 0, 44);
		writer134.flush();
		writer134.close();
		BufferResult result171 = writer134.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_ensc", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("ensc", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result170.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("ensc", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'S','c','i','e','n','c','e','s'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer136 = factory.newBufferWriter();
		writer136.write("\n", 0, 1);
		writer136.write("                                                ", 0, 48);
		BufferWriter writer137 = factory.newBufferWriter();
		writer137.write("\n", 0, 1);
		writer137.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer137.write("                                                    return true;\n", 0, 65);
		writer137.write("                                                ", 0, 48);
		writer137.flush();
		writer137.close();
		BufferResult result172 = writer137.getResult();
		BufferResult result173 = result172.trim();
		writer136.write("\n", 0, 1);
		writer136.write("                                            ", 0, 44);
		writer136.flush();
		writer136.close();
		BufferResult result174 = writer136.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_enpr", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("enpr", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result173.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("enpr", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'T','e','c','h','n','o','l','o','g','y',' ','a','n','d',' ','r','e','s','e','a','r','c','h'
		}, 0, 23);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer138 = factory.newBufferWriter();
		writer138.write("\n", 0, 1);
		writer138.write("                                                ", 0, 48);
		BufferWriter writer139 = factory.newBufferWriter();
		writer139.write("\n", 0, 1);
		writer139.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer139.write("                                                    return true;\n", 0, 65);
		writer139.write("                                                ", 0, 48);
		writer139.flush();
		writer139.close();
		BufferResult result175 = writer139.getResult();
		BufferResult result176 = result175.trim();
		writer138.write("\n", 0, 1);
		writer138.write("                                            ", 0, 44);
		writer138.flush();
		writer138.close();
		BufferResult result177 = writer138.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_entp", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("entp", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result176.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("entp", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'T','r','a','n','s','p','o','r','t'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                        </div>\n", 0, 31);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t", 0, 7);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                        <div style=\"font-weight:bold\">", 0, 54);
		writer82.write(new char[] {
			'F','r','e','n','c','h'
		}, 0, 6);
		writer82.write("</div>\n", 0, 7);
		writer82.write("                        <div style=\"margin-left:1em; margin-right:1em\">\n", 0, 72);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer140 = factory.newBufferWriter();
		writer140.write("\n", 0, 1);
		writer140.write("                                                ", 0, 48);
		BufferWriter writer141 = factory.newBufferWriter();
		writer141.write("\n", 0, 1);
		writer141.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer141.write("                                                    return true;\n", 0, 65);
		writer141.write("                                                ", 0, 48);
		writer141.flush();
		writer141.close();
		BufferResult result178 = writer141.getResult();
		BufferResult result179 = result178.trim();
		writer140.write("\n", 0, 1);
		writer140.write("                                            ", 0, 44);
		writer140.flush();
		writer140.close();
		BufferResult result180 = writer140.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frag", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frag", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result179.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frag", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'A','g','r','i','c','u','l','t','u','r','e'
		}, 0, 11);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer142 = factory.newBufferWriter();
		writer142.write("\n", 0, 1);
		writer142.write("                                                ", 0, 48);
		BufferWriter writer143 = factory.newBufferWriter();
		writer143.write("\n", 0, 1);
		writer143.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer143.write("                                                    return true;\n", 0, 65);
		writer143.write("                                                ", 0, 48);
		writer143.flush();
		writer143.close();
		BufferResult result181 = writer143.getResult();
		BufferResult result182 = result181.trim();
		writer142.write("\n", 0, 1);
		writer142.write("                                            ", 0, 44);
		writer142.flush();
		writer142.close();
		BufferResult result183 = writer142.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frbk", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frbk", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result182.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frbk", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'B','a','n','k','i','n','g'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer144 = factory.newBufferWriter();
		writer144.write("\n", 0, 1);
		writer144.write("                                                ", 0, 48);
		BufferWriter writer145 = factory.newBufferWriter();
		writer145.write("\n", 0, 1);
		writer145.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer145.write("                                                    return true;\n", 0, 65);
		writer145.write("                                                ", 0, 48);
		writer145.flush();
		writer145.close();
		BufferResult result184 = writer145.getResult();
		BufferResult result185 = result184.trim();
		writer144.write("\n", 0, 1);
		writer144.write("                                            ", 0, 44);
		writer144.flush();
		writer144.close();
		BufferResult result186 = writer144.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frci", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frci", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result185.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frci", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','i','t','i','e','s'
		}, 0, 6);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer146 = factory.newBufferWriter();
		writer146.write("\n", 0, 1);
		writer146.write("                                                ", 0, 48);
		BufferWriter writer147 = factory.newBufferWriter();
		writer147.write("\n", 0, 1);
		writer147.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer147.write("                                                    return true;\n", 0, 65);
		writer147.write("                                                ", 0, 48);
		writer147.flush();
		writer147.close();
		BufferResult result187 = writer147.getResult();
		BufferResult result188 = result187.trim();
		writer146.write("\n", 0, 1);
		writer146.write("                                            ", 0, 44);
		writer146.flush();
		writer146.close();
		BufferResult result189 = writer146.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frtr", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frtr", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result188.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frtr", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','m','m','e','r','c','e'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer148 = factory.newBufferWriter();
		writer148.write("\n", 0, 1);
		writer148.write("                                                ", 0, 48);
		BufferWriter writer149 = factory.newBufferWriter();
		writer149.write("\n", 0, 1);
		writer149.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer149.write("                                                    return true;\n", 0, 65);
		writer149.write("                                                ", 0, 48);
		writer149.flush();
		writer149.close();
		BufferResult result190 = writer149.getResult();
		BufferResult result191 = result190.trim();
		writer148.write("\n", 0, 1);
		writer148.write("                                            ", 0, 44);
		writer148.flush();
		writer148.close();
		BufferResult result192 = writer148.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frco", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frco", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result191.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frco", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','m','m','u','n','i','c','a','t','i','o','n'
		}, 0, 13);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer150 = factory.newBufferWriter();
		writer150.write("\n", 0, 1);
		writer150.write("                                                ", 0, 48);
		BufferWriter writer151 = factory.newBufferWriter();
		writer151.write("\n", 0, 1);
		writer151.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer151.write("                                                    return true;\n", 0, 65);
		writer151.write("                                                ", 0, 48);
		writer151.flush();
		writer151.close();
		BufferResult result193 = writer151.getResult();
		BufferResult result194 = result193.trim();
		writer150.write("\n", 0, 1);
		writer150.write("                                            ", 0, 44);
		writer150.flush();
		writer150.close();
		BufferResult result195 = writer150.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frcp", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frcp", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result194.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frcp", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','m','p','a','n','i','e','s'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer152 = factory.newBufferWriter();
		writer152.write("\n", 0, 1);
		writer152.write("                                                ", 0, 48);
		BufferWriter writer153 = factory.newBufferWriter();
		writer153.write("\n", 0, 1);
		writer153.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer153.write("                                                    return true;\n", 0, 65);
		writer153.write("                                                ", 0, 48);
		writer153.flush();
		writer153.close();
		BufferResult result196 = writer153.getResult();
		BufferResult result197 = result196.trim();
		writer152.write("\n", 0, 1);
		writer152.write("                                            ", 0, 44);
		writer152.flush();
		writer152.close();
		BufferResult result198 = writer152.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frcn", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frcn", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result197.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frcn", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'C','o','u','n','t','r','i','e','s'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer154 = factory.newBufferWriter();
		writer154.write("\n", 0, 1);
		writer154.write("                                                ", 0, 48);
		BufferWriter writer155 = factory.newBufferWriter();
		writer155.write("\n", 0, 1);
		writer155.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer155.write("                                                    return true;\n", 0, 65);
		writer155.write("                                                ", 0, 48);
		writer155.flush();
		writer155.close();
		BufferResult result199 = writer155.getResult();
		BufferResult result200 = result199.trim();
		writer154.write("\n", 0, 1);
		writer154.write("                                            ", 0, 44);
		writer154.flush();
		writer154.close();
		BufferResult result201 = writer154.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frec", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frec", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result200.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frec", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','c','o','n','o','m','y'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer156 = factory.newBufferWriter();
		writer156.write("\n", 0, 1);
		writer156.write("                                                ", 0, 48);
		BufferWriter writer157 = factory.newBufferWriter();
		writer157.write("\n", 0, 1);
		writer157.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer157.write("                                                    return true;\n", 0, 65);
		writer157.write("                                                ", 0, 48);
		writer157.flush();
		writer157.close();
		BufferResult result202 = writer157.getResult();
		BufferResult result203 = result202.trim();
		writer156.write("\n", 0, 1);
		writer156.write("                                            ", 0, 44);
		writer156.flush();
		writer156.close();
		BufferResult result204 = writer156.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frce", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frce", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result203.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frce", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','d','u','c','a','t','i','o','n'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer158 = factory.newBufferWriter();
		writer158.write("\n", 0, 1);
		writer158.write("                                                ", 0, 48);
		BufferWriter writer159 = factory.newBufferWriter();
		writer159.write("\n", 0, 1);
		writer159.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer159.write("                                                    return true;\n", 0, 65);
		writer159.write("                                                ", 0, 48);
		writer159.flush();
		writer159.close();
		BufferResult result205 = writer159.getResult();
		BufferResult result206 = result205.trim();
		writer158.write("\n", 0, 1);
		writer158.write("                                            ", 0, 44);
		writer158.flush();
		writer158.close();
		BufferResult result207 = writer158.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frel", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frel", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result206.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frel", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','m','p','l','o','y','m','e','n','t'
		}, 0, 10);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer160 = factory.newBufferWriter();
		writer160.write("\n", 0, 1);
		writer160.write("                                                ", 0, 48);
		BufferWriter writer161 = factory.newBufferWriter();
		writer161.write("\n", 0, 1);
		writer161.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer161.write("                                                    return true;\n", 0, 65);
		writer161.write("                                                ", 0, 48);
		writer161.flush();
		writer161.close();
		BufferResult result208 = writer161.getResult();
		BufferResult result209 = result208.trim();
		writer160.write("\n", 0, 1);
		writer160.write("                                            ", 0, 44);
		writer160.flush();
		writer160.close();
		BufferResult result210 = writer160.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_fren", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("fren", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result209.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("fren", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','e','r','g','y'
		}, 0, 6);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer162 = factory.newBufferWriter();
		writer162.write("\n", 0, 1);
		writer162.write("                                                ", 0, 48);
		BufferWriter writer163 = factory.newBufferWriter();
		writer163.write("\n", 0, 1);
		writer163.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer163.write("                                                    return true;\n", 0, 65);
		writer163.write("                                                ", 0, 48);
		writer163.flush();
		writer163.close();
		BufferResult result211 = writer163.getResult();
		BufferResult result212 = result211.trim();
		writer162.write("\n", 0, 1);
		writer162.write("                                            ", 0, 44);
		writer162.flush();
		writer162.close();
		BufferResult result213 = writer162.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frbu", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frbu", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result212.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frbu", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','t','e','r','p','r','i','s','e'
		}, 0, 10);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer164 = factory.newBufferWriter();
		writer164.write("\n", 0, 1);
		writer164.write("                                                ", 0, 48);
		BufferWriter writer165 = factory.newBufferWriter();
		writer165.write("\n", 0, 1);
		writer165.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer165.write("                                                    return true;\n", 0, 65);
		writer165.write("                                                ", 0, 48);
		writer165.flush();
		writer165.close();
		BufferResult result214 = writer165.getResult();
		BufferResult result215 = result214.trim();
		writer164.write("\n", 0, 1);
		writer164.write("                                            ", 0, 44);
		writer164.flush();
		writer164.close();
		BufferResult result216 = writer164.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frev", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frev", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result215.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frev", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'E','n','v','i','r','o','n','m','e','n','t'
		}, 0, 11);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer166 = factory.newBufferWriter();
		writer166.write("\n", 0, 1);
		writer166.write("                                                ", 0, 48);
		BufferWriter writer167 = factory.newBufferWriter();
		writer167.write("\n", 0, 1);
		writer167.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer167.write("                                                    return true;\n", 0, 65);
		writer167.write("                                                ", 0, 48);
		writer167.flush();
		writer167.close();
		BufferResult result217 = writer167.getResult();
		BufferResult result218 = result217.trim();
		writer166.write("\n", 0, 1);
		writer166.write("                                            ", 0, 44);
		writer166.flush();
		writer166.close();
		BufferResult result219 = writer166.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frfi", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frfi", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result218.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frfi", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','i','n','a','n','c','e'
		}, 0, 7);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer168 = factory.newBufferWriter();
		writer168.write("\n", 0, 1);
		writer168.write("                                                ", 0, 48);
		BufferWriter writer169 = factory.newBufferWriter();
		writer169.write("\n", 0, 1);
		writer169.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer169.write("                                                    return true;\n", 0, 65);
		writer169.write("                                                ", 0, 48);
		writer169.flush();
		writer169.close();
		BufferResult result220 = writer169.getResult();
		BufferResult result221 = result220.trim();
		writer168.write("\n", 0, 1);
		writer168.write("                                            ", 0, 44);
		writer168.flush();
		writer168.close();
		BufferResult result222 = writer168.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frfo", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frfo", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result221.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frfo", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'F','o','o','d'
		}, 0, 4);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer170 = factory.newBufferWriter();
		writer170.write("\n", 0, 1);
		writer170.write("                                                ", 0, 48);
		BufferWriter writer171 = factory.newBufferWriter();
		writer171.write("\n", 0, 1);
		writer171.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer171.write("                                                    return true;\n", 0, 65);
		writer171.write("                                                ", 0, 48);
		writer171.flush();
		writer171.close();
		BufferResult result223 = writer171.getResult();
		BufferResult result224 = result223.trim();
		writer170.write("\n", 0, 1);
		writer170.write("                                            ", 0, 44);
		writer170.flush();
		writer170.close();
		BufferResult result225 = writer170.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frge", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frge", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result224.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frge", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'G','e','o','g','r','a','p','h','y'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer172 = factory.newBufferWriter();
		writer172.write("\n", 0, 1);
		writer172.write("                                                ", 0, 48);
		BufferWriter writer173 = factory.newBufferWriter();
		writer173.write("\n", 0, 1);
		writer173.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer173.write("                                                    return true;\n", 0, 65);
		writer173.write("                                                ", 0, 48);
		writer173.flush();
		writer173.close();
		BufferResult result226 = writer173.getResult();
		BufferResult result227 = result226.trim();
		writer172.write("\n", 0, 1);
		writer172.write("                                            ", 0, 44);
		writer172.flush();
		writer172.close();
		BufferResult result228 = writer172.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frid", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frid", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result227.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frid", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','d','u','s','t','r','y'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer174 = factory.newBufferWriter();
		writer174.write("\n", 0, 1);
		writer174.write("                                                ", 0, 48);
		BufferWriter writer175 = factory.newBufferWriter();
		writer175.write("\n", 0, 1);
		writer175.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer175.write("                                                    return true;\n", 0, 65);
		writer175.write("                                                ", 0, 48);
		writer175.flush();
		writer175.close();
		BufferResult result229 = writer175.getResult();
		BufferResult result230 = result229.trim();
		writer174.write("\n", 0, 1);
		writer174.write("                                            ", 0, 44);
		writer174.flush();
		writer174.close();
		BufferResult result231 = writer174.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frin", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frin", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result230.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frin", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','s','t','i','t','u','t','i','o','n','s'
		}, 0, 12);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer176 = factory.newBufferWriter();
		writer176.write("\n", 0, 1);
		writer176.write("                                                ", 0, 48);
		BufferWriter writer177 = factory.newBufferWriter();
		writer177.write("\n", 0, 1);
		writer177.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer177.write("                                                    return true;\n", 0, 65);
		writer177.write("                                                ", 0, 48);
		writer177.flush();
		writer177.close();
		BufferResult result232 = writer177.getResult();
		BufferResult result233 = result232.trim();
		writer176.write("\n", 0, 1);
		writer176.write("                                            ", 0, 44);
		writer176.flush();
		writer176.close();
		BufferResult result234 = writer176.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frio", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frio", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result233.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frio", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','t','e','r','n','a','t','i','o','n','a','l',' ','o','r','g','a','n','i','s','a','t','i','o','n','s'
		}, 0, 27);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer178 = factory.newBufferWriter();
		writer178.write("\n", 0, 1);
		writer178.write("                                                ", 0, 48);
		BufferWriter writer179 = factory.newBufferWriter();
		writer179.write("\n", 0, 1);
		writer179.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer179.write("                                                    return true;\n", 0, 65);
		writer179.write("                                                ", 0, 48);
		writer179.flush();
		writer179.close();
		BufferResult result235 = writer179.getResult();
		BufferResult result236 = result235.trim();
		writer178.write("\n", 0, 1);
		writer178.write("                                            ", 0, 44);
		writer178.flush();
		writer178.close();
		BufferResult result237 = writer178.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frir", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frir", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result236.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frir", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'I','n','t','e','r','n','a','t','i','o','n','a','l',' ','r','e','l','a','t','i','o','n','s'
		}, 0, 23);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer180 = factory.newBufferWriter();
		writer180.write("\n", 0, 1);
		writer180.write("                                                ", 0, 48);
		BufferWriter writer181 = factory.newBufferWriter();
		writer181.write("\n", 0, 1);
		writer181.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer181.write("                                                    return true;\n", 0, 65);
		writer181.write("                                                ", 0, 48);
		writer181.flush();
		writer181.close();
		BufferResult result238 = writer181.getResult();
		BufferResult result239 = result238.trim();
		writer180.write("\n", 0, 1);
		writer180.write("                                            ", 0, 44);
		writer180.flush();
		writer180.close();
		BufferResult result240 = writer180.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frla", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frla", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result239.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frla", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'L','a','w'
		}, 0, 3);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer182 = factory.newBufferWriter();
		writer182.write("\n", 0, 1);
		writer182.write("                                                ", 0, 48);
		BufferWriter writer183 = factory.newBufferWriter();
		writer183.write("\n", 0, 1);
		writer183.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer183.write("                                                    return true;\n", 0, 65);
		writer183.write("                                                ", 0, 48);
		writer183.flush();
		writer183.close();
		BufferResult result241 = writer183.getResult();
		BufferResult result242 = result241.trim();
		writer182.write("\n", 0, 1);
		writer182.write("                                            ", 0, 44);
		writer182.flush();
		writer182.close();
		BufferResult result243 = writer182.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frle", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frle", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result242.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frle", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'L','e','g','a','l'
		}, 0, 5);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer184 = factory.newBufferWriter();
		writer184.write("\n", 0, 1);
		writer184.write("                                                ", 0, 48);
		BufferWriter writer185 = factory.newBufferWriter();
		writer185.write("\n", 0, 1);
		writer185.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer185.write("                                                    return true;\n", 0, 65);
		writer185.write("                                                ", 0, 48);
		writer185.flush();
		writer185.close();
		BufferResult result244 = writer185.getResult();
		BufferResult result245 = result244.trim();
		writer184.write("\n", 0, 1);
		writer184.write("                                            ", 0, 44);
		writer184.flush();
		writer184.close();
		BufferResult result246 = writer184.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frme", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frme", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result245.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frme", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'M','e','d','i','c','i','n','e'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		simulateCalls3(
			factory,
			out,
			writer82
		);
	}
	
	private static void simulateCalls3(
		BufferWriterFactory factory,
		Writer out,
		BufferWriter writer82
	) throws IOException {
		BufferWriter writer186 = factory.newBufferWriter();
		writer186.write("\n", 0, 1);
		writer186.write("                                                ", 0, 48);
		BufferWriter writer187 = factory.newBufferWriter();
		writer187.write("\n", 0, 1);
		writer187.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer187.write("                                                    return true;\n", 0, 65);
		writer187.write("                                                ", 0, 48);
		writer187.flush();
		writer187.close();
		BufferResult result247 = writer187.getResult();
		BufferResult result248 = result247.trim();
		writer186.write("\n", 0, 1);
		writer186.write("                                            ", 0, 44);
		writer186.flush();
		writer186.close();
		BufferResult result249 = writer186.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frpe", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frpe", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result248.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frpe", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'P','e','o','p','l','e'
		}, 0, 6);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer188 = factory.newBufferWriter();
		writer188.write("\n", 0, 1);
		writer188.write("                                                ", 0, 48);
		BufferWriter writer189 = factory.newBufferWriter();
		writer189.write("\n", 0, 1);
		writer189.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer189.write("                                                    return true;\n", 0, 65);
		writer189.write("                                                ", 0, 48);
		writer189.flush();
		writer189.close();
		BufferResult result250 = writer189.getResult();
		BufferResult result251 = result250.trim();
		writer188.write("\n", 0, 1);
		writer188.write("                                            ", 0, 44);
		writer188.flush();
		writer188.close();
		BufferResult result252 = writer188.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frpt", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frpt", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result251.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frpt", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'P','o','l','i','t','i','c','s'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer190 = factory.newBufferWriter();
		writer190.write("\n", 0, 1);
		writer190.write("                                                ", 0, 48);
		BufferWriter writer191 = factory.newBufferWriter();
		writer191.write("\n", 0, 1);
		writer191.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer191.write("                                                    return true;\n", 0, 65);
		writer191.write("                                                ", 0, 48);
		writer191.flush();
		writer191.close();
		BufferResult result253 = writer191.getResult();
		BufferResult result254 = result253.trim();
		writer190.write("\n", 0, 1);
		writer190.write("                                            ", 0, 44);
		writer190.flush();
		writer190.close();
		BufferResult result255 = writer190.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frsc", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frsc", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result254.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frsc", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'S','c','i','e','n','c','e','s'
		}, 0, 8);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer192 = factory.newBufferWriter();
		writer192.write("\n", 0, 1);
		writer192.write("                                                ", 0, 48);
		BufferWriter writer193 = factory.newBufferWriter();
		writer193.write("\n", 0, 1);
		writer193.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer193.write("                                                    return true;\n", 0, 65);
		writer193.write("                                                ", 0, 48);
		writer193.flush();
		writer193.close();
		BufferResult result256 = writer193.getResult();
		BufferResult result257 = result256.trim();
		writer192.write("\n", 0, 1);
		writer192.write("                                            ", 0, 44);
		writer192.flush();
		writer192.close();
		BufferResult result258 = writer192.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frpr", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frpr", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result257.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frpr", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'T','e','c','h','n','o','l','o','g','y',' ','a','n','d',' ','r','e','s','e','a','r','c','h'
		}, 0, 23);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t\t\t\t\t", 0, 10);
		writer82.write("\n", 0, 1);
		writer82.write("                                        <div>\n", 0, 46);
		writer82.write("\t                                        ", 0, 41);
		writer82.write("\n", 0, 1);
		writer82.write("                                            ", 0, 44);
		BufferWriter writer194 = factory.newBufferWriter();
		writer194.write("\n", 0, 1);
		writer194.write("                                                ", 0, 48);
		BufferWriter writer195 = factory.newBufferWriter();
		writer195.write("\n", 0, 1);
		writer195.write("                                                    updateCostCalculator(this.form);\n", 0, 85);
		writer195.write("                                                    return true;\n", 0, 65);
		writer195.write("                                                ", 0, 48);
		writer195.flush();
		writer195.close();
		BufferResult result259 = writer195.getResult();
		BufferResult result260 = result259.trim();
		writer194.write("\n", 0, 1);
		writer194.write("                                            ", 0, 44);
		writer194.flush();
		writer194.close();
		BufferResult result261 = writer194.getResult();
		writer82.write("<input", 0, 6);
		writer82.write(" id=\"", 0, 5);
		writer82.write("d_frtp", 0, 6);
		writer82.write(34);
		writer82.write(" type=\"", 0, 7);
		writer82.write("checkbox", 0, 8);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("d", 0, 1);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		writer82.write("frtp", 0, 4);
		writer82.write(34);
		writer82.write(" onclick=\"", 0, 10);
		result260.writeTo(javaScriptInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';'
		}, 53, 32);
		writer82.write("&#xA;", 0, 5);
		writer82.write(new char[] {
			'\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ','u','p','d','a','t','e','C','o','s','t','C','a','l','c','u','l','a','t','o','r','(','t','h','i','s','.','f','o','r','m',')',';','\n',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '
			,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','r','e','t','u','r','n',' ','t','r','u','e',';'
		}, 86, 64);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("<label for=\"d_", 0, 14);
		writer82.write("frtp", 0, 4);
		writer82.write(34);
		writer82.write(62);
		writer82.write(new char[] {
			'T','r','a','n','s','p','o','r','t'
		}, 0, 9);
		writer82.write("</label>\n", 0, 9);
		writer82.write("                                        </div>\n", 0, 47);
		writer82.write("                                    ", 0, 36);
		writer82.write("\n", 0, 1);
		writer82.write("                                ", 0, 32);
		writer82.write("\n", 0, 1);
		writer82.write("                            ", 0, 28);
		writer82.write("\n", 0, 1);
		writer82.write("                        </div>\n", 0, 31);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t\t", 0, 6);
		writer82.write("\n", 0, 1);
		writer82.write("\t\t\t\t\t", 0, 5);
		writer82.write("\n", 0, 1);
		writer82.write("                    ", 0, 20);
		writer82.write("\n", 0, 1);
		writer82.write("                ", 0, 16);
		writer82.write("\n", 0, 1);
		writer82.write("                <div style=\"text-align:center; margin-top:1em\">\n", 0, 64);
		writer82.write("                    ", 0, 20);
		BufferWriter writer196 = factory.newBufferWriter();
		writer196.write("<< Back", 0, 7);
		writer196.flush();
		writer196.close();
		BufferResult result262 = writer196.getResult();
		BufferResult result263 = result262.trim();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("submit", 0, 6);
		writer82.write(34);
		writer82.write(" name=\"", 0, 7);
		writer82.write("back", 0, 4);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		result263.writeTo(textInXhtmlAttributeEncoder, out);
		writer82.write("&lt;", 0, 4);
		writer82.write("&lt;", 0, 4);
		writer82.write(new char[] {
			'<','<',' ','B','a','c','k'
		}, 2, 5);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("                    &nbsp;\n", 0, 27);
		writer82.write("                    ", 0, 20);
		BufferWriter writer197 = factory.newBufferWriter();
		writer197.write("Next >>", 0, 7);
		writer197.flush();
		writer197.close();
		BufferResult result264 = writer197.getResult();
		BufferResult result265 = result264.trim();
		writer82.write("<input", 0, 6);
		writer82.write(" type=\"", 0, 7);
		writer82.write("submit", 0, 6);
		writer82.write(34);
		writer82.write(" value=\"", 0, 8);
		result265.writeTo(textInXhtmlAttributeEncoder, out);
		writer82.write(new char[] {
			'N','e','x','t',' '
		}, 0, 5);
		writer82.write("&gt;", 0, 4);
		writer82.write("&gt;", 0, 4);
		writer82.write(34);
		writer82.write(" />", 0, 3);
		writer82.write("\n", 0, 1);
		writer82.write("                </div>\n", 0, 23);
		writer82.write("            ", 0, 12);
		writer82.flush();
		writer82.close();
		BufferResult result266 = writer82.getResult();
		result266.writeTo(out);
		BufferWriter writer198 = factory.newBufferWriter();
		writer198.close();
		BufferResult result267 = writer198.getResult();
		BufferWriter writer199 = factory.newBufferWriter();
		writer199.close();
		BufferResult result268 = writer199.getResult();
		BufferWriter writer200 = factory.newBufferWriter();
		writer200.close();
		BufferResult result269 = writer200.getResult();
		BufferWriter writer201 = factory.newBufferWriter();
		writer201.write("\n", 0, 1);
		writer201.write("\t\t// Rounds a currency amount to the nearest penny\n", 0, 51);
		writer201.write("\t\tfunction roundCurrency(amount) {\n", 0, 35);
		writer201.write("\t\t\treturn Math.round(amount*100)/100;\n", 0, 38);
		writer201.write("\t\t}\n", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("\t\tvar cartIndex = parseInt(", 0, 27);
		writer201.write(34);
		writer201.write("2", 0, 1);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("\t\tvar productQuantity = parseInt(", 0, 33);
		writer201.write(34);
		writer201.write("1", 0, 1);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("\t\tvar productUnitPrice = parseFloat(", 0, 36);
		writer201.write(34);
		writer201.write("49.00", 0, 5);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\tvar productSalePrice = parseFloat(", 0, 37);
		writer201.write(34);
		writer201.write("49.00", 0, 5);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("\t\t", 0, 2);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t// The total for all cart items except the current\n", 0, 53);
		writer201.write("\t\tvar otherProductTotalUnitPrice = parseFloat(", 0, 46);
		writer201.write(34);
		writer201.write("5267.00", 0, 7);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\tvar otherProductTotalSalePrice = parseFloat(", 0, 47);
		writer201.write(34);
		writer201.write("4571.00", 0, 7);
		writer201.write(34);
		writer201.write(");\n", 0, 3);
		writer201.write("\t\t", 0, 2);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\n", 0, 3);
		writer201.write("        var languageUnitPrices = [\n", 0, 35);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("            parseFloat(", 0, 23);
		writer201.write(34);
		writer201.write("0.00", 0, 4);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("            parseFloat(", 0, 23);
		writer201.write(34);
		writer201.write("49.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("            parseFloat(", 0, 23);
		writer201.write(34);
		writer201.write("44.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("            parseFloat(", 0, 23);
		writer201.write(34);
		writer201.write("39.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("        ];\n", 0, 11);
		writer201.write("        function getLanguagesPrice(count) {\n", 0, 44);
		writer201.write("            var total = 0;\n", 0, 27);
		writer201.write("            for(var i=0; i < count; i++) {\n", 0, 43);
		writer201.write("                if(i < languageUnitPrices.length) {\n", 0, 52);
		writer201.write("                    total = total + languageUnitPrices[i];\n", 0, 59);
		writer201.write("                } else {\n", 0, 25);
		writer201.write("                    total = total + languageUnitPrices[languageUnitPrices.length-1] * (count - languageUnitPrices.length);\n", 0, 123);
		writer201.write("                    break;\n", 0, 27);
		writer201.write("                }\n", 0, 18);
		writer201.write("            }\n", 0, 14);
		writer201.write("            return roundCurrency(\n", 0, 34);
		writer201.write("\t\t\t\tproductQuantity * total\n", 0, 28);
		writer201.write("\t\t\t);\n", 0, 6);
		writer201.write("        }\n", 0, 10);
		writer201.write("\n", 0, 1);
		writer201.write("        function getDomainsPrice(count) {\n", 0, 42);
		writer201.write("            if(count<=parseInt(", 0, 31);
		writer201.write(34);
		writer201.write("1", 0, 1);
		writer201.write(34);
		writer201.write(")) return 0;\n", 0, 13);
		writer201.write("            return roundCurrency(\n", 0, 34);
		writer201.write("\t\t\t\tproductQuantity\n", 0, 20);
		writer201.write("\t\t\t\t* parseFloat(", 0, 17);
		writer201.write(34);
		writer201.write("10.00", 0, 5);
		writer201.write(34);
		writer201.write(")\n", 0, 2);
		writer201.write("\t\t\t\t* (count-parseInt(", 0, 22);
		writer201.write(34);
		writer201.write("1", 0, 1);
		writer201.write(34);
		writer201.write("))\n", 0, 3);
		writer201.write("\t\t\t);\n", 0, 6);
		writer201.write("        }\n", 0, 10);
		writer201.write("\n", 0, 1);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("            var languageSalePrices = [\n", 0, 39);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t\t0,\n", 0, 9);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t                parseFloat(", 0, 29);
		writer201.write(34);
		writer201.write("25.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t                parseFloat(", 0, 29);
		writer201.write(34);
		writer201.write("22.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t                parseFloat(", 0, 29);
		writer201.write(34);
		writer201.write("20.00", 0, 5);
		writer201.write(34);
		writer201.write("),\n", 0, 3);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\t", 0, 5);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("            ];\n", 0, 15);
		writer201.write("            function getLanguagesSalePrice(count) {\n", 0, 52);
		writer201.write("                var total = 0;\n", 0, 31);
		writer201.write("                for(var i=0; i < count; i++) {\n", 0, 47);
		writer201.write("                    if(i < languageSalePrices.length) {\n", 0, 56);
		writer201.write("                        total = total + languageSalePrices[i];\n", 0, 63);
		writer201.write("                    } else {\n", 0, 29);
		writer201.write("                        total = total + languageSalePrices[languageSalePrices.length-1] * (count - languageSalePrices.length);\n", 0, 127);
		writer201.write("                        break;\n", 0, 31);
		writer201.write("                    }\n", 0, 22);
		writer201.write("                }\n", 0, 18);
		writer201.write("                return roundCurrency(\n", 0, 38);
		writer201.write("\t\t\t\t\tproductQuantity * total\n", 0, 29);
		writer201.write("\t\t\t\t);\n", 0, 7);
		writer201.write("            }\n", 0, 14);
		writer201.write("\n", 0, 1);
		writer201.write("            function getDomainsSalePrice(count) {\n", 0, 50);
		writer201.write("                if(count<=parseInt(", 0, 35);
		writer201.write(34);
		writer201.write("1", 0, 1);
		writer201.write(34);
		writer201.write(")) return 0;\n", 0, 13);
		writer201.write("                return roundCurrency(\n", 0, 38);
		writer201.write("\t\t\t\t\tproductQuantity\n", 0, 21);
		writer201.write("\t\t\t\t\t* parseFloat(", 0, 18);
		writer201.write(34);
		writer201.write("10.00", 0, 5);
		writer201.write(34);
		writer201.write(")\n", 0, 2);
		writer201.write("\t\t\t\t\t* (count-parseInt(", 0, 23);
		writer201.write(34);
		writer201.write("1", 0, 1);
		writer201.write(34);
		writer201.write("))\n", 0, 3);
		writer201.write("\t\t\t\t);\n", 0, 7);
		writer201.write("            }\n", 0, 14);
		writer201.write("        ", 0, 8);
		writer201.write("\n", 0, 1);
		writer201.write("\n", 0, 1);
		writer201.write("\t\tfunction formatCurrency(amount) {\n", 0, 36);
		writer201.write("\t\t\tamount = roundCurrency(amount);\n", 0, 35);
		writer201.write("\t\t\tvar result = Math.floor(amount);\n", 0, 36);
		writer201.write("\t\t\tresult = result + \".\";\n", 0, 26);
		writer201.write("\t\t\tamount = (amount * 100) % 100;\n", 0, 34);
		writer201.write("\t\t\tif(amount<10) result = result + \"0\";\n", 0, 40);
		writer201.write("\t\t\tresult = result + amount;\n", 0, 29);
		writer201.write("\t\t\treturn result;\n", 0, 18);
		writer201.write("\t\t}\n", 0, 4);
		writer201.write("\n", 0, 1);
		writer201.write("        function updateCostCalculator(form) {\n", 0, 46);
		writer201.write("            var elems = form.elements;\n", 0, 39);
		writer201.write("\n", 0, 1);
		writer201.write("            var languageCount = 0;\n", 0, 35);
		writer201.write("            var languageCodes = new Array();\n", 0, 45);
		writer201.write("            for(var i=0; i < elems.length; i++) {\n", 0, 50);
		writer201.write("                var elem = elems[i];\n", 0, 37);
		writer201.write("                if(elem.name=='l') {\n", 0, 37);
		writer201.write("                    if(elem.type=='hidden' || elem.checked==true) {\n", 0, 68);
		writer201.write("                        languageCount++;\n", 0, 41);
		writer201.write("                        languageCodes.push(elem.value);\n", 0, 56);
		writer201.write("                    }\n", 0, 22);
		writer201.write("                }\n", 0, 18);
		writer201.write("            }\n", 0, 14);
		writer201.write("            document.getElementById(\"languageCount_\"+cartIndex).firstChild.nodeValue = languageCount.toString();\n", 0, 113);
		writer201.write("            var languagesPrice = getLanguagesPrice(languageCount);\n", 0, 67);
		writer201.write("            document.getElementById(\"languagesPrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(languagesPrice);\n", 0, 120);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("                var languagesSalePrice = getLanguagesSalePrice(languageCount);\n", 0, 79);
		writer201.write("                document.getElementById(\"languagesSalePrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(languagesSalePrice);\n", 0, 132);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\n", 0, 1);
		writer201.write("            var domainCount = 0;\n", 0, 33);
		writer201.write("            for(var i=0; i < elems.length; i++) {\n", 0, 50);
		writer201.write("                var elem = elems[i];\n", 0, 37);
		writer201.write("                if(elem.name=='d') {\n", 0, 37);
		writer201.write("                    if(elem.type=='hidden' || elem.checked==true) {\n", 0, 68);
		writer201.write("                        for(var j=0; j < languageCodes.length; j++) {\n", 0, 70);
		writer201.write("                            if(elem.value.indexOf(languageCodes[j])===0) {\n", 0, 75);
		writer201.write("                                domainCount++;\n", 0, 47);
		writer201.write("                                break;\n", 0, 39);
		writer201.write("                            }\n", 0, 30);
		writer201.write("                        }\n", 0, 26);
		writer201.write("                    }\n", 0, 22);
		writer201.write("                }\n", 0, 18);
		writer201.write("            }\n", 0, 14);
		writer201.write("            document.getElementById(\"domainCount_\"+cartIndex).firstChild.nodeValue = domainCount.toString();\n", 0, 109);
		writer201.write("            var domainsPrice = getDomainsPrice(domainCount);\n", 0, 61);
		writer201.write("            document.getElementById(\"domainsPrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(domainsPrice);\n", 0, 116);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("                var domainsSalePrice = getDomainsSalePrice(domainCount);\n", 0, 73);
		writer201.write("                document.getElementById(\"domainsSalePrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(domainsSalePrice);\n", 0, 128);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\tvar newTotalPrice =\n", 0, 23);
		writer201.write("\t\t\t\tproductQuantity * productUnitPrice\n", 0, 39);
		writer201.write("\t\t\t\t+ languagesPrice\n", 0, 21);
		writer201.write("\t\t\t\t+ domainsPrice\n", 0, 19);
		writer201.write("\t\t\t;\n", 0, 5);
		writer201.write("            document.getElementById(\"totalPrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(newTotalPrice);\n", 0, 115);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\tvar newTotalSalePrice =\n", 0, 28);
		writer201.write("\t\t\t\t\tproductQuantity * productSalePrice\n", 0, 40);
		writer201.write("\t\t\t\t\t+ languagesSalePrice\n", 0, 26);
		writer201.write("\t\t\t\t\t+ domainsSalePrice\n", 0, 24);
		writer201.write("\t\t\t\t;\n", 0, 6);
		writer201.write("                document.getElementById(\"totalSalePrice_\"+cartIndex).firstChild.nodeValue = formatCurrency(newTotalSalePrice);\n", 0, 127);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\t\n", 0, 5);
		writer201.write("\t\t\t// Update grand totals\n", 0, 26);
		writer201.write("\t\t\tdocument.getElementById(\"grandTotalPrice\").firstChild.nodeValue = formatCurrency(\n", 0, 85);
		writer201.write("\t\t\t\totherProductTotalUnitPrice + newTotalPrice\n", 0, 47);
		writer201.write("\t\t\t);\n", 0, 6);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("\t\t\t\tdocument.getElementById(\"grantTotalSalePrice\").firstChild.nodeValue = formatCurrency(\n", 0, 90);
		writer201.write("\t\t\t\t\totherProductTotalSalePrice + newTotalSalePrice\n", 0, 52);
		writer201.write("\t\t\t\t);\n", 0, 7);
		writer201.write("            ", 0, 12);
		writer201.write("\n", 0, 1);
		writer201.write("        }\n", 0, 10);
		writer201.write("    ", 0, 4);
		writer201.flush();
		writer201.close();
		BufferResult result270 = writer201.getResult();
		result270.writeTo(out);
		BufferWriter writer202 = factory.newBufferWriter();
		writer202.close();
		BufferResult result271 = writer202.getResult();
		BufferResult result272 = result271.trim();
		BufferWriter writer203 = factory.newBufferWriter();
		writer203.close();
		BufferResult result273 = writer203.getResult();
		BufferResult result274 = result273.trim();
		result272.toString();
		result274.toString();
		BufferWriter writer204 = factory.newBufferWriter();
		writer204.write("<!--97--><span id=\"EditableResourceBundleElement3020\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3020, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3020);\">home</span>", 0, 322);
		writer204.flush();
		writer204.close();
		BufferResult result275 = writer204.getResult();
		result275.writeTo(out);
		BufferWriter writer205 = factory.newBufferWriter();
		writer205.write("<!--98--><span id=\"EditableResourceBundleElement3021\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3021, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3021);\">what's new?</span>", 0, 329);
		writer205.flush();
		writer205.close();
		BufferResult result276 = writer205.getResult();
		result276.writeTo(out);
		BufferWriter writer206 = factory.newBufferWriter();
		writer206.write("<!--99--><span id=\"EditableResourceBundleElement3022\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3022, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3022);\">FAQ</span>", 0, 321);
		writer206.flush();
		writer206.close();
		BufferResult result277 = writer206.getResult();
		result277.writeTo(out);
		BufferWriter writer207 = factory.newBufferWriter();
		writer207.write(10);
		writer207.write(9);
		BufferWriter writer208 = factory.newBufferWriter();
		writer208.write("Twitter", 0, 7);
		writer208.flush();
		writer208.close();
		BufferResult result278 = writer208.getResult();
		BufferResult result279 = result278.trim();
		writer207.write(10);
		writer207.write(9);
		BufferWriter writer209 = factory.newBufferWriter();
		writer209.write("\n", 0, 1);
		writer209.write("        ", 0, 8);
		BufferWriter writer210 = factory.newBufferWriter();
		writer210.write("Twitter", 0, 7);
		writer210.flush();
		writer210.close();
		BufferResult result280 = writer210.getResult();
		BufferResult result281 = result280.trim();
		writer209.write("\n", 0, 1);
		writer209.write("        ", 0, 8);
		BufferWriter writer211 = factory.newBufferWriter();
		writer211.write(51);
		writer211.write(50);
		writer211.flush();
		writer211.close();
		BufferResult result282 = writer211.getResult();
		BufferResult result283 = result282.trim();
		writer209.write("\n", 0, 1);
		writer209.write("        ", 0, 8);
		BufferWriter writer212 = factory.newBufferWriter();
		writer212.write(51);
		writer212.write(50);
		writer212.flush();
		writer212.close();
		BufferResult result284 = writer212.getResult();
		BufferResult result285 = result284.trim();
		writer209.write("\n", 0, 1);
		writer209.write("    ", 0, 4);
		writer209.flush();
		writer209.close();
		BufferResult result286 = writer209.getResult();
		writer207.write("<img src=\"", 0, 10);
		writer207.write("/essential-mining.com/images/share/twitter.gif", 0, 46);
		writer207.write("\" width=\"", 0, 9);
		result283.writeTo(textInXhtmlAttributeEncoder, out);
		writer207.write(new char[] {
			'3','2'
		}, 0, 2);
		writer207.write("\" height=\"", 0, 10);
		result285.writeTo(textInXhtmlAttributeEncoder, out);
		writer207.write(new char[] {
			'3','2'
		}, 0, 2);
		writer207.write("\" alt=\"", 0, 7);
		result281.writeTo(textInXhtmlAttributeEncoder, out);
		writer207.write(new char[] {
			'T','w','i','t','t','e','r'
		}, 0, 7);
		writer207.write(34);
		writer207.write(" />", 0, 3);
		writer207.write(10);
		writer207.flush();
		writer207.close();
		BufferResult result287 = writer207.getResult();
		result279.writeTo(textInXhtmlAttributeEncoder, out);
		result287.writeTo(out);
		BufferWriter writer213 = factory.newBufferWriter();
		writer213.write(10);
		writer213.write(9);
		BufferWriter writer214 = factory.newBufferWriter();
		writer214.write(" Facebook ", 0, 10);
		writer214.flush();
		writer214.close();
		BufferResult result288 = writer214.getResult();
		BufferResult result289 = result288.trim();
		writer213.write("\n", 0, 1);
		writer213.write("    ", 0, 4);
		BufferWriter writer215 = factory.newBufferWriter();
		writer215.write("\n", 0, 1);
		writer215.write("        ", 0, 8);
		BufferWriter writer216 = factory.newBufferWriter();
		writer216.write("Facebook", 0, 8);
		writer216.flush();
		writer216.close();
		BufferResult result290 = writer216.getResult();
		BufferResult result291 = result290.trim();
		writer215.write("\n", 0, 1);
		writer215.write("        ", 0, 8);
		BufferWriter writer217 = factory.newBufferWriter();
		writer217.write(51);
		writer217.write(50);
		writer217.flush();
		writer217.close();
		BufferResult result292 = writer217.getResult();
		BufferResult result293 = result292.trim();
		writer215.write("\n", 0, 1);
		writer215.write("        ", 0, 8);
		BufferWriter writer218 = factory.newBufferWriter();
		writer218.write(51);
		writer218.write(50);
		writer218.flush();
		writer218.close();
		BufferResult result294 = writer218.getResult();
		BufferResult result295 = result294.trim();
		writer215.write("    \n", 0, 5);
		writer215.write("    ", 0, 4);
		writer215.flush();
		writer215.close();
		BufferResult result296 = writer215.getResult();
		writer213.write("<img src=\"", 0, 10);
		writer213.write("/essential-mining.com/images/share/facebook.gif", 0, 47);
		writer213.write("\" width=\"", 0, 9);
		result293.writeTo(textInXhtmlAttributeEncoder, out);
		writer213.write(new char[] {
			'3','2'
		}, 0, 2);
		writer213.write("\" height=\"", 0, 10);
		result295.writeTo(textInXhtmlAttributeEncoder, out);
		writer213.write(new char[] {
			'3','2'
		}, 0, 2);
		writer213.write("\" alt=\"", 0, 7);
		result291.writeTo(textInXhtmlAttributeEncoder, out);
		writer213.write(new char[] {
			'F','a','c','e','b','o','o','k'
		}, 0, 8);
		writer213.write(34);
		writer213.write(" />", 0, 3);
		writer213.write(10);
		writer213.flush();
		writer213.close();
		BufferResult result297 = writer213.getResult();
		result289.writeTo(textInXhtmlAttributeEncoder, out);
		result297.writeTo(out);
		BufferWriter writer219 = factory.newBufferWriter();
		writer219.write(10);
		writer219.write(9);
		BufferWriter writer220 = factory.newBufferWriter();
		writer220.write(" LinkedIn", 0, 9);
		writer220.flush();
		writer220.close();
		BufferResult result298 = writer220.getResult();
		BufferResult result299 = result298.trim();
		writer219.write(10);
		writer219.write(9);
		BufferWriter writer221 = factory.newBufferWriter();
		writer221.write("\n", 0, 1);
		writer221.write("        ", 0, 8);
		BufferWriter writer222 = factory.newBufferWriter();
		writer222.write("LinkedIn", 0, 8);
		writer222.flush();
		writer222.close();
		BufferResult result300 = writer222.getResult();
		BufferResult result301 = result300.trim();
		writer221.write("\n", 0, 1);
		writer221.write("        ", 0, 8);
		BufferWriter writer223 = factory.newBufferWriter();
		writer223.write(51);
		writer223.write(50);
		writer223.flush();
		writer223.close();
		BufferResult result302 = writer223.getResult();
		BufferResult result303 = result302.trim();
		writer221.write("\n", 0, 1);
		writer221.write("        ", 0, 8);
		BufferWriter writer224 = factory.newBufferWriter();
		writer224.write(51);
		writer224.write(50);
		writer224.flush();
		writer224.close();
		BufferResult result304 = writer224.getResult();
		BufferResult result305 = result304.trim();
		writer221.write("\n", 0, 1);
		writer221.write("    ", 0, 4);
		writer221.flush();
		writer221.close();
		BufferResult result306 = writer221.getResult();
		writer219.write("<img src=\"", 0, 10);
		writer219.write("/essential-mining.com/images/share/linkedin.gif", 0, 47);
		writer219.write("\" width=\"", 0, 9);
		result303.writeTo(textInXhtmlAttributeEncoder, out);
		writer219.write(new char[] {
			'3','2'
		}, 0, 2);
		writer219.write("\" height=\"", 0, 10);
		result305.writeTo(textInXhtmlAttributeEncoder, out);
		writer219.write(new char[] {
			'3','2'
		}, 0, 2);
		writer219.write("\" alt=\"", 0, 7);
		result301.writeTo(textInXhtmlAttributeEncoder, out);
		writer219.write(new char[] {
			'L','i','n','k','e','d','I','n'
		}, 0, 8);
		writer219.write(34);
		writer219.write(" />", 0, 3);
		writer219.write(10);
		writer219.flush();
		writer219.close();
		BufferResult result307 = writer219.getResult();
		result299.writeTo(textInXhtmlAttributeEncoder, out);
		result307.writeTo(out);
		BufferWriter writer225 = factory.newBufferWriter();
		writer225.write("\n", 0, 1);
		writer225.write("    ", 0, 4);
		BufferWriter writer226 = factory.newBufferWriter();
		writer226.write("MySpace", 0, 7);
		writer226.flush();
		writer226.close();
		BufferResult result308 = writer226.getResult();
		BufferResult result309 = result308.trim();
		writer225.write(10);
		writer225.write(9);
		BufferWriter writer227 = factory.newBufferWriter();
		writer227.write("\n", 0, 1);
		writer227.write("        ", 0, 8);
		BufferWriter writer228 = factory.newBufferWriter();
		writer228.write("MySpace", 0, 7);
		writer228.flush();
		writer228.close();
		BufferResult result310 = writer228.getResult();
		BufferResult result311 = result310.trim();
		writer227.write("\n", 0, 1);
		writer227.write("        ", 0, 8);
		BufferWriter writer229 = factory.newBufferWriter();
		writer229.write(51);
		writer229.write(50);
		writer229.flush();
		writer229.close();
		BufferResult result312 = writer229.getResult();
		BufferResult result313 = result312.trim();
		writer227.write("\n", 0, 1);
		writer227.write("        ", 0, 8);
		BufferWriter writer230 = factory.newBufferWriter();
		writer230.write(51);
		writer230.write(50);
		writer230.flush();
		writer230.close();
		BufferResult result314 = writer230.getResult();
		BufferResult result315 = result314.trim();
		writer227.write("\n", 0, 1);
		writer227.write("    ", 0, 4);
		writer227.flush();
		writer227.close();
		BufferResult result316 = writer227.getResult();
		writer225.write("<img src=\"", 0, 10);
		writer225.write("/essential-mining.com/images/share/myspace.gif", 0, 46);
		writer225.write("\" width=\"", 0, 9);
		result313.writeTo(textInXhtmlAttributeEncoder, out);
		writer225.write(new char[] {
			'3','2'
		}, 0, 2);
		writer225.write("\" height=\"", 0, 10);
		result315.writeTo(textInXhtmlAttributeEncoder, out);
		writer225.write(new char[] {
			'3','2'
		}, 0, 2);
		writer225.write("\" alt=\"", 0, 7);
		result311.writeTo(textInXhtmlAttributeEncoder, out);
		writer225.write(new char[] {
			'M','y','S','p','a','c','e'
		}, 0, 7);
		writer225.write(34);
		writer225.write(" />", 0, 3);
		writer225.write(10);
		writer225.flush();
		writer225.close();
		BufferResult result317 = writer225.getResult();
		result309.writeTo(textInXhtmlAttributeEncoder, out);
		result317.writeTo(out);
		BufferWriter writer231 = factory.newBufferWriter();
		writer231.write("\n", 0, 1);
		writer231.write("    ", 0, 4);
		BufferWriter writer232 = factory.newBufferWriter();
		writer232.write("Digg", 0, 4);
		writer232.flush();
		writer232.close();
		BufferResult result318 = writer232.getResult();
		BufferResult result319 = result318.trim();
		writer231.write(10);
		writer231.write(9);
		BufferWriter writer233 = factory.newBufferWriter();
		writer233.write("\n", 0, 1);
		writer233.write("        ", 0, 8);
		BufferWriter writer234 = factory.newBufferWriter();
		writer234.write("Digg", 0, 4);
		writer234.flush();
		writer234.close();
		BufferResult result320 = writer234.getResult();
		BufferResult result321 = result320.trim();
		writer233.write("\n", 0, 1);
		writer233.write("        ", 0, 8);
		BufferWriter writer235 = factory.newBufferWriter();
		writer235.write(51);
		writer235.write(50);
		writer235.flush();
		writer235.close();
		BufferResult result322 = writer235.getResult();
		BufferResult result323 = result322.trim();
		writer233.write("\n", 0, 1);
		writer233.write("        ", 0, 8);
		BufferWriter writer236 = factory.newBufferWriter();
		writer236.write(51);
		writer236.write(50);
		writer236.flush();
		writer236.close();
		BufferResult result324 = writer236.getResult();
		BufferResult result325 = result324.trim();
		writer233.write("\n", 0, 1);
		writer233.write("    ", 0, 4);
		writer233.flush();
		writer233.close();
		BufferResult result326 = writer233.getResult();
		writer231.write("<img src=\"", 0, 10);
		writer231.write("/essential-mining.com/images/share/digg.gif", 0, 43);
		writer231.write("\" width=\"", 0, 9);
		result323.writeTo(textInXhtmlAttributeEncoder, out);
		writer231.write(new char[] {
			'3','2'
		}, 0, 2);
		writer231.write("\" height=\"", 0, 10);
		result325.writeTo(textInXhtmlAttributeEncoder, out);
		writer231.write(new char[] {
			'3','2'
		}, 0, 2);
		writer231.write("\" alt=\"", 0, 7);
		result321.writeTo(textInXhtmlAttributeEncoder, out);
		writer231.write(new char[] {
			'D','i','g','g'
		}, 0, 4);
		writer231.write(34);
		writer231.write(" />", 0, 3);
		writer231.write(10);
		writer231.flush();
		writer231.close();
		BufferResult result327 = writer231.getResult();
		result319.writeTo(textInXhtmlAttributeEncoder, out);
		result327.writeTo(out);
		BufferWriter writer237 = factory.newBufferWriter();
		writer237.write("<!--100--><span id=\"EditableResourceBundleElement3363\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3363, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3363);\">essential summarizer</span>", 0, 339);
		writer237.flush();
		writer237.close();
		BufferResult result328 = writer237.getResult();
		result328.writeTo(out);
		BufferWriter writer238 = factory.newBufferWriter();
		writer238.write("<!--101--><span id=\"EditableResourceBundleElement3364\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3364, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3364);\">online edition</span>", 0, 333);
		writer238.flush();
		writer238.close();
		BufferResult result329 = writer238.getResult();
		result329.writeTo(out);
		BufferWriter writer239 = factory.newBufferWriter();
		writer239.write("<!--102--><span id=\"EditableResourceBundleElement3365\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3365, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3365);\">personal edition</span>", 0, 335);
		writer239.flush();
		writer239.close();
		BufferResult result330 = writer239.getResult();
		result330.writeTo(out);
		BufferWriter writer240 = factory.newBufferWriter();
		writer240.write("<!--103--><span id=\"EditableResourceBundleElement3366\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3366, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3366);\">enterprise edition</span>", 0, 337);
		writer240.flush();
		writer240.close();
		BufferResult result331 = writer240.getResult();
		result331.writeTo(out);
		BufferWriter writer241 = factory.newBufferWriter();
		writer241.write("<!--104--><span id=\"EditableResourceBundleElement3367\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3367, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3367);\">developer tools</span>", 0, 334);
		writer241.flush();
		writer241.close();
		BufferResult result332 = writer241.getResult();
		result332.writeTo(out);
		BufferWriter writer242 = factory.newBufferWriter();
		writer242.write("<!--105--><span id=\"EditableResourceBundleElement3368\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3368, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3368);\">company</span>", 0, 326);
		writer242.flush();
		writer242.close();
		BufferResult result333 = writer242.getResult();
		result333.writeTo(out);
		BufferWriter writer243 = factory.newBufferWriter();
		writer243.write("<!--106--><span id=\"EditableResourceBundleElement3369\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3369, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3369);\">contact us</span>", 0, 329);
		writer243.flush();
		writer243.close();
		BufferResult result334 = writer243.getResult();
		result334.writeTo(out);
		BufferWriter writer244 = factory.newBufferWriter();
		writer244.write("<!--107--><span id=\"EditableResourceBundleElement3370\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3370, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3370);\">in the news</span>", 0, 330);
		writer244.flush();
		writer244.close();
		BufferResult result335 = writer244.getResult();
		result335.writeTo(out);
		BufferWriter writer245 = factory.newBufferWriter();
		writer245.write("<!--108--><span id=\"EditableResourceBundleElement3371\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3371, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3371);\">references</span>", 0, 329);
		writer245.flush();
		writer245.close();
		BufferResult result336 = writer245.getResult();
		result336.writeTo(out);
		BufferWriter writer246 = factory.newBufferWriter();
		writer246.write("\n", 0, 1);
		writer246.write("                                ", 0, 32);
		BufferWriter writer247 = factory.newBufferWriter();
		writer247.write("mailto:contact@essential-mining.com", 0, 35);
		writer247.flush();
		writer247.close();
		BufferResult result337 = writer247.getResult();
		BufferResult result338 = result337.trim();
		result338.toString();
		writer246.write("\n", 0, 1);
		writer246.write("                                ", 0, 32);
		writer246.write("<!--113--><span id=\"EditableResourceBundleElement3376\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3376, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3376);\">contact@essential-mining.com</span>", 0, 347);
		writer246.write("\n", 0, 1);
		writer246.write("                            ", 0, 28);
		writer246.flush();
		writer246.close();
		BufferResult result339 = writer246.getResult();
		result339.writeTo(out);
		result272.toString();
		BufferWriter writer248 = factory.newBufferWriter();
		writer248.write("<!--115--><span id=\"EditableResourceBundleElement3378\" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(3378, true);\" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(3378);\">Terms of Use and Privacy</span>", 0, 343);
		writer248.flush();
		writer248.close();
		BufferResult result340 = writer248.getResult();
		result340.writeTo(out);
		result274.toString();
		BufferWriter writer249 = factory.newBufferWriter();
		writer249.write("\n", 0, 1);
		writer249.write("\t\t\tvar _gaq = _gaq || [];\n", 0, 26);
		writer249.write("\t\t\t_gaq.push(['_setAccount', 'UA-16755233-1']);\n", 0, 48);
		writer249.write("\t\t\t_gaq.push(['_trackPageview']);\n", 0, 34);
		writer249.write("\n", 0, 1);
		writer249.write("\t\t\t(function() {\n", 0, 17);
		writer249.write("\t\t\t\tvar ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n", 0, 93);
		writer249.write("\t\t\t\tga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n", 0, 118);
		writer249.write("\t\t\t\tvar s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n", 0, 90);
		writer249.write("\t\t\t})();\n", 0, 9);
		writer249.write("\t\t", 0, 2);
		writer249.flush();
		writer249.close();
		BufferResult result341 = writer249.getResult();
		result341.writeTo(out);
		result272.toString();
		result274.toString();
	}
}
