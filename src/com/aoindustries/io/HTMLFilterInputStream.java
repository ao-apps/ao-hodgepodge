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

    synchronized public int read() throws IOException {
	if(buffused>0) {
	    int returnme=buff[0];
	    System.arraycopy(buff,1,buff,0,--buffused);
	    return returnme;
	}
	int ch=in.read();
	if(ch<=' ') return ch;
	while(ch=='<') {
	    // Skip HTML tags, except search for <TITLE> and </TITLE>
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
