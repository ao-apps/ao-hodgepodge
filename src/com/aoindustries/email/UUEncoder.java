package com.aoindustries.email;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This source is Copyright 1997 by Matthew Hixson &lt;hixson@frozenwave.com&gt; and Mark Spadoni &lt;speed@linux.dpilink.com&gt;
 * It may be distributed under the Gnu Public License (GPL).
 * You may use it freely, so long as credit is given to Matthew Hixson and Mark Spadoni,
 * and our email addresses are included somewhere within the documentation.
 */
public class UUEncoder extends Thread {

    static final int BUFFER_SIZE = (45 * 128);

    DataInputStream data_in;
    DataOutputStream data_out;
    int permission; // Unix file mode
    String filename; // name for the file after it becomes uudecoded
    byte[] buffer;
    byte[] output_buffer;
    int absolute;

    public UUEncoder(InputStream i, OutputStream o, int p, String name) throws IOException {
	data_in = new DataInputStream(i);
	data_out = new DataOutputStream(o);
	permission = p;
	filename = name;
	absolute = 0;
	buffer = new byte[BUFFER_SIZE];
	output_buffer = new byte[BUFFER_SIZE];
    }

    @Override
    public void run() {
	try {
            int a = 0;
            int b = 0;
            int av = 0;
            int n = 0;
            int i = 0; // loop until the end of the file is reached
            int j = 0;
            data_out.writeBytes("begin " + permission + " " + filename + "\n");
            while (true) {
                av = data_in.available();
                if (av >= buffer.length) {
                    n = data_in.read(buffer, 0, buffer.length);
                } else {
                    n = av;
                    data_in.readFully(buffer, 0, n);
                }
                for (a = 45, b = 0; a < n; a += 45) {
                    // this does a chunk of 46k input bytes  
                    output_buffer[absolute] = (byte) ((45 & 077) + 32);
                    absolute++;
                    for (; b < a; b += 3) {
                        // this does a chunk of 45 input bytes            
                        write_uuencoded(b);
                    }
                    output_buffer[absolute] = (byte) 10; // add a newline byte
                    absolute++;
                    data_out.flush(); //this may not be neccessary, but its still polite to flush
                }

                // here we need to write the leftovers to the output_buffer        
                a -= 45; //at the end of the previous loop a was incremented so decrement it here
                n -= a; // this tells us how much is left over after reading "a" bytes
                output_buffer[absolute] = (n != 0) ? ((byte) ((n & 077) + 32)) : ((byte) 96);
                absolute++;
                j = b + n;
                for (; b < j; b += 3) {
                    write_uuencoded(b);
                }
                output_buffer[absolute] = (byte) 10; // add a newline byte
                absolute++;
                if (n <= 0) {
                    break;
                }
            } // end while(true)

            // now get whatever was left over in the buffer, if anything
            data_out.write(output_buffer, 0, absolute);
            data_out.writeBytes("end\n");
            data_out.flush();
            data_in.close();
            data_out.close();
	} catch (IOException stan) {
            System.err.println("IOE caught: " + stan);
	}
    }

    private void write_uuencoded(int i) throws IOException {
	//it might be faster to make these four bytes a four
	//element array, dunno...    
	byte c1 = 0;
	byte c2 = 0;
	byte c3 = 0;
	byte c4 = 0;
	c1 = (byte) (buffer[i] >> 2);
	c2 = (byte) ((buffer[i] << 4) & 060 | (buffer[i + 1] >> 4) & 017);
	c3 = (byte) ((buffer[i + 1] << 2) & 074 | (buffer[i + 2] >> 6) & 03);
	c4 = (byte) (buffer[i + 2] & 077);

	// if the next addition of four bytes is going to
	// be longer than the buffer has room for
	// then go ahead and write the buffer to disk,
	// from 0 to the current index
	if ((absolute + 4) >= output_buffer.length) {
            data_out.write(output_buffer, 0, absolute);
            data_out.flush();
            // start a new buffer
            output_buffer[0] = (c1 != 0) ? ((byte) ((c1 & 077) + 32)) : ((byte) 96);
            output_buffer[1] = (c2 != 0) ? ((byte) ((c2 & 077) + 32)) : ((byte) 96);
            output_buffer[2] = (c3 != 0) ? ((byte) ((c3 & 077) + 32)) : ((byte) 96);
            output_buffer[3] = (c4 != 0) ? ((byte) ((c4 & 077) + 32)) : ((byte) 96);
            absolute = 4;
	} else {
            // we won't run into the end of the buffer on this call, so keep copying, baby!
            // Its shagadelic, yeah!
            output_buffer[absolute] = (c1 != 0) ? ((byte) ((c1 & 077) + 32)) : ((byte) 96);
            output_buffer[absolute + 1] = (c2 != 0) ? ((byte) ((c2 & 077) + 32)) : ((byte) 96);
            output_buffer[absolute + 2] = (c3 != 0) ? ((byte) ((c3 & 077) + 32)) : ((byte) 96);
            output_buffer[absolute + 3] = (c4 != 0) ? ((byte) ((c4 & 077) + 32)) : ((byte) 96);
            absolute += 4;
	}
    }
}