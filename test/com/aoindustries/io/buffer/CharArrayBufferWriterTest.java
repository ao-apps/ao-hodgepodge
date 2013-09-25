/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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
import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class CharArrayBufferWriterTest extends BufferWriterTest {

    public CharArrayBufferWriterTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CharArrayBufferWriterTest.class);
        return suite;
    }

    private static final char[][] strings = {
        "Editeur : Mining Essential".toCharArray(),
        "Langues : français, allemand, anglais, espagnol, portugais, italien, néerlandais, norvégien, suédois, arabe, hébreu, polonais, russe, turc, chinois, coréen, japonais, hindi, persan et grec.".toCharArray(),
        "Formats de documents : txt (plein texte), html (hypertext markup language), doc (microsoft word), rtf (rich text format), pdf (portable document format), docx (microsoft word 2010).".toCharArray(),
        "Version : 5.1".toCharArray(),
        "Navigateurs web : Internet Explorer, Firefox, Chrome, Safari, Opéra".toCharArray(),
        "Systèmes : Linux, Windows Server 2003, Windows Server 2008.".toCharArray(),
        "".toCharArray(),
        "L’API Java Essential Summarizer est destinée aux entreprises et aux développeurs. Elle peut être mise à disposition en vue de différentes intégrations de la fonctionnalité résumé automatique de texte.".toCharArray(),
        "Il en est de même pour le Web Service Essential Summarizer.".toCharArray()
    };

    private void doBenchmark() throws IOException {
        long startTime = System.nanoTime();
        CharArrayBufferWriter writer = new CharArrayBufferWriter(32, 4 * 1024 * 1024);
        try {
            for(int i=0; i<10000; i++) {
                for(char[] str : strings) writer.write(str);
            }
            long endTime = System.nanoTime();
            System.out.println("Wrote " + writer.getLength()+" characters in " + BigDecimal.valueOf(endTime - startTime, 6)+" ms");
        } finally {
            writer.close();
        }
    }

    public void testBenchmark() throws IOException {
        for(int i=0; i<10; i++) {
            doBenchmark();
        }
    }
	
	public void testBenchmarkSimulate() throws IOException {
		benchmarkSimulate(
			new BufferWriterFactory() {
				@Override
				public String getName() {
					return CharArrayBufferWriter.class.getName();
				}

				@Override
				public BufferWriter newBufferWriter() {
					return new CharArrayBufferWriter(32, 4 * 1024 * 1024);
				}
			}
		);
	}
}
