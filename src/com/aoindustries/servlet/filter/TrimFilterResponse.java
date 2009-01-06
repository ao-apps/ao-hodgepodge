package com.aoindustries.servlet.filter;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
public class TrimFilterResponse implements HttpServletResponse {

    private HttpServletResponse wrapped;
    private TrimFilterWriter writer;
    private TrimFilterOutputStream outputStream;

    public TrimFilterResponse(HttpServletResponse wrapped) {
        this.wrapped = wrapped;
    }
    
    public Locale getLocale() {
        return wrapped.getLocale();
    }
    
    public void setLocale(Locale locale) {
        wrapped.setLocale(locale);
    }
    
    public void reset() {
        wrapped.reset();
        if(writer!=null) {
            writer.inTextArea = false;
        }
        if(outputStream!=null) {
            outputStream.inTextArea = false;
        }
    }
    
    public boolean isCommitted() {
        return wrapped.isCommitted();
    }
    
    public void resetBuffer() {
        wrapped.resetBuffer();
        if(writer!=null) {
            writer.inTextArea = false;
        }
        if(outputStream!=null) {
            outputStream.inTextArea = false;
        }
    }
    
    public void flushBuffer() throws IOException {
        wrapped.flushBuffer();
    }
    
    public int getBufferSize() {
        return wrapped.getBufferSize();
    }
    
    public void setBufferSize(int size) {
        wrapped.setBufferSize(size);
    }
    
    public void setContentType(String contentType) {
        wrapped.setContentType(contentType);
    }
    
    public void setContentLength(int length) {
        wrapped.setContentLength(length);
    }
    
    public PrintWriter getWriter() throws IOException {
        if(writer==null) writer = new TrimFilterWriter(wrapped.getWriter());
        return writer;
    }
    
    public synchronized ServletOutputStream getOutputStream() throws IOException {
        if(outputStream==null) outputStream = new TrimFilterOutputStream(wrapped.getOutputStream());
        return outputStream;
    }
    
    public String getCharacterEncoding() {
        return wrapped.getCharacterEncoding();
    }
    
    public void setStatus(int status) {
        wrapped.setStatus(status);
    }

    /**
     * @deprecated
     */
    public void setStatus(int status, String message) {
        wrapped.setStatus(status, message);
    }
    
    public void addIntHeader(String name, int value) {
        wrapped.addIntHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        wrapped.setIntHeader(name, value);
    }

    public void addHeader(String name, String value) {
        wrapped.addHeader(name, value);
    }

    public void setHeader(String name, String value) {
        wrapped.setHeader(name, value);
    }

    public void addDateHeader(String name, long value) {
        wrapped.addDateHeader(name, value);
    }

    public void setDateHeader(String name, long value) {
        wrapped.setDateHeader(name, value);
    }
    
    public void sendRedirect(String path) throws IOException {
        wrapped.sendRedirect(path);
    }
    
    public void sendError(int code) throws IOException {
        wrapped.sendError(code);
    }
    
    public void sendError(int code, String message) throws IOException {
        wrapped.sendError(code, message);
    }

    /**
     * @deprecated
     */
    public String encodeRedirectUrl(String path) {
        return wrapped.encodeRedirectUrl(path);
    }
    
    /**
     * @deprecated
     */
    public String encodeUrl(String path) {
        return wrapped.encodeUrl(path);
    }

    public String encodeRedirectURL(String path) {
        return wrapped.encodeRedirectURL(path);
    }

    public String encodeURL(String path) {
        return wrapped.encodeURL(path);
    }
    
    public boolean containsHeader(String name) {
        return wrapped.containsHeader(name);
    }
    
    public void addCookie(Cookie cookie) {
        wrapped.addCookie(cookie);
    }

    public void setCharacterEncoding(String string) {
        try {
            // Call through reflection just in case we are in an older servlet environment
            Method setCharacterEncodingMethod = wrapped.getClass().getMethod("setCharacterEncoding", String.class);
            setCharacterEncodingMethod.invoke(wrapped, string);
        } catch(NoSuchMethodException err) {
            throw new WrappedException(err);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    public String getContentType() {
        try {
            // Call through reflection just in case we are in an older servlet environment
            Method getContentTypeMethod = wrapped.getClass().getMethod("getContentType");
            return (String)getContentTypeMethod.invoke(wrapped);
        } catch(NoSuchMethodException err) {
            throw new WrappedException(err);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }
}
