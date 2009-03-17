package com.aoindustries.servlet.filter;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * PREs are automatically detected as long as they start with exact "&lt;pre" and end with exactly "&lt;/pre" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.
 *
 * TODO: Don't trim inside PRE tags.
 * 
 * @author  AO Industries, Inc.
 */
public class TrimFilterResponse extends HttpServletResponseWrapper {

    private HttpServletResponse wrapped;
    private TrimFilterWriter writer;
    private TrimFilterOutputStream outputStream;

    public TrimFilterResponse(HttpServletResponse response) {
        super(response);
    }
    
    @Override
    public void reset() {
        getResponse().reset();
        if(writer!=null) {
            writer.inTextArea = false;
        }
        if(outputStream!=null) {
            outputStream.inTextArea = false;
        }
    }

    @Override
    public void resetBuffer() {
        getResponse().resetBuffer();
        if(writer!=null) {
            writer.inTextArea = false;
        }
        if(outputStream!=null) {
            outputStream.inTextArea = false;
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(writer==null) writer = new TrimFilterWriter(getResponse().getWriter());
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(outputStream==null) outputStream = new TrimFilterOutputStream(getResponse().getOutputStream());
        return outputStream;
    }
}
