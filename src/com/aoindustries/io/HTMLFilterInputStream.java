/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTMLFilterInputStream extends FilterInputStream {

    private static final int BUFF_LEN=5;

    private final int[] buff=new int[BUFF_LEN];
	
    private int buffused=0;
	
    public static final int TITLE_START=256;
    public static final int TITLE_END=257;
	
    public HTMLFilterInputStream(InputStream in) {
	super(in);
    }

    public static void main(String[] S) {
        if(S.length==0) {
            System.err.println("usage: "+HTMLFilterInputStream.class.getName()+" filename [filename] [...]");
            System.exit(1);
        } else {
            for(int c=0;c<S.length;c++) {
                try {
                    InputStream in=new HTMLFilterInputStream(new FileInputStream(S[c]));
                    OutputStream out=System.out;
                    int ch;
                    while((ch=in.read())!=-1) out.write(ch);
                    out.flush();
                } catch(IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }

    @Override
    synchronized public int read() throws IOException {
	if(buffused>0) {
	    int returnme=buff[0];
	    System.arraycopy(buff,1,buff,0,--buffused);
	    return returnme;
	}
	int ch=in.read();
	if(ch<=' ') return ch;
	while(ch=='<') {
	    // Skip HTML tags, except search for <title> and </title>
	    if((ch=in.read())=='T'||ch=='t') {
		if(
                    ((ch=in.read())=='I'||ch=='i')
		    &&((ch=in.read())=='T'||ch=='t')
		    &&((ch=in.read())=='L'||ch=='l')
		    &&((ch=in.read())=='E'||ch=='e')
		    &&(ch=in.read())=='>'
                ) return TITLE_START;
	    } else if(
                ch=='/'
                &&((ch=in.read())=='T'||ch=='t')
                &&((ch=in.read())=='I'||ch=='i')
                &&((ch=in.read())=='T'||ch=='t')
                &&((ch=in.read())=='L'||ch=='l')
                &&((ch=in.read())=='E'||ch=='e')
                &&(ch=in.read())=='>'
            ) return TITLE_END;
	    while(ch!='>'&&ch!=-1) ch=in.read();
	    if(ch!=-1) ch=in.read();
	}
	// convert &quot; to ", &#160; to space, and &amp; to &
	if(ch=='&') {
            if((buff[0]=in.read())=='q'||buff[0]=='Q') {
		if((buff[1]=in.read())=='u'||buff[1]=='U') {
		    if((buff[2]=in.read())=='o'||buff[2]=='O') {
			if((buff[3]=in.read())=='t'||buff[3]=='T') {
			    if((buff[4]=in.read())==';') return '"';
			    else buffused=5;
			} else buffused=4;
		    } else buffused=3;
		} else buffused=2;
	    } else if(buff[0]=='n'||buff[0]=='N') {
		if((buff[1]=in.read())=='b'||buff[1]=='B') {
		    if((buff[2]=in.read())=='s'||buff[2]=='S') {
			if((buff[3]=in.read())=='p'||buff[3]=='P') {
			    if((buff[4]=in.read())==';') return ' ';
			    else buffused=5;
			} else buffused=4;
		    } else buffused=3;
		} else buffused=2;
	    } else if(buff[0]=='a'||buff[0]=='A') {
		if((buff[1]=in.read())=='m'||buff[1]=='M') {
		    if((buff[2]=in.read())=='p'||buff[2]=='P') {
			if((buff[3]=in.read())==';') return '&';
			else buffused=4;
		    } else buffused=3;
		} else buffused=2;
	    } else buffused=1;
	}
	return ch;
    }
}
