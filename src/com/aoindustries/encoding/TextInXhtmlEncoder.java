/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.encoding;

import java.io.IOException;
import java.io.Writer;

/**
 * Encodes arbitrary data into XHTML.  Minimal conversion is performed, just
 * encoding of necessary values and throwing an IOException when any character
 * is found that cannot be converted to XHTML entities.
 * 
 * @author  AO Industries, Inc.
 */
public class TextInXhtmlEncoder extends MediaEncoder {

    public class EncodingState {
        boolean previousWasSpace = false;
    }

    // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
    /**
     * Encodes a single character and returns its String representation
     * or null if no modification is necessary.
     *
     * @see XhtmlMediaValidator#checkCharacter(int)
     */
    private static String getEscapedCharacter(int c, boolean makeBr, boolean makeNbsp, EncodingState encodingState) throws IOException {
        boolean oldPreviousWasSpace;
        if(encodingState==null) {
            if(makeNbsp) throw new IllegalArgumentException("encodingState is null while makeNbsp is true");
            oldPreviousWasSpace = false;
        } else {
            oldPreviousWasSpace = encodingState.previousWasSpace;
            encodingState.previousWasSpace = c==' ';
        }
        switch(c) {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '&': return "&amp;";
            case '\r':
                if(makeBr) return "";
                return null;
            case '\n':
                if(makeBr) return "<br />\n";
                return null;
            case ' ':
                if(makeNbsp && oldPreviousWasSpace) return "&#160;";
                return null;
            default:
                XhtmlValidator.checkCharacter(c);
                return null;
        }
    }

    public static void encodeTextInXhtml(CharSequence S, Appendable out) throws IOException {
        encodeTextInXhtml(S, out, false, false, null);
    }

    public static void encodeTextInXhtml(CharSequence S, Appendable out, boolean makeBr, boolean makeNbsp, EncodingState encodingState) throws IOException {
        if(S==null) S = "null";
        encodeTextInXhtml(S, 0, S.length(), out, makeBr, makeNbsp, encodingState);
    }

    public static void encodeTextInXhtml(CharSequence S, int start, int end, Appendable out) throws IOException {
        encodeTextInXhtml(S, start, end, out, false, false, null);
    }

    public static void encodeTextInXhtml(CharSequence S, int start, int end, Appendable out, boolean makeBr, boolean makeNbsp, EncodingState encodingState) throws IOException {
        if(S==null) S = "null";
        int toPrint = 0;
        for (int c = start; c < end; c++) {
            String escaped = getEscapedCharacter(S.charAt(c), makeBr, makeNbsp, encodingState);
            if(escaped!=null) {
                if(toPrint>0) {
                    out.append(S, c-toPrint, c);
                    toPrint=0;
                }
                out.append(escaped);
            } else {
                toPrint++;
            }
        }
        if(toPrint>0) out.append(S, end-toPrint, end);
    }

    public static void encodeTextInXhtml(char[] cbuf, int start, int len, Writer out) throws IOException {
        encodeTextInXhtml(cbuf, start, len, out, false, false, null);
    }

    public static void encodeTextInXhtml(char[] cbuf, int start, int len, Writer out, boolean makeBr, boolean makeNbsp, EncodingState encodingState) throws IOException {
        int end = start+len;
        int toPrint = 0;
        for (int c = start; c < end; c++) {
            String escaped = getEscapedCharacter(cbuf[c], makeBr, makeNbsp, encodingState);
            if(escaped!=null) {
                if(toPrint>0) {
                    out.write(cbuf, c-toPrint, toPrint);
                    toPrint=0;
                }
                out.append(escaped);
            } else {
                toPrint++;
            }
        }
        if(toPrint>0) out.write(cbuf, end-toPrint, toPrint);
    }

    public static void encodeTextInXhtml(char ch, Appendable out) throws IOException {
        encodeTextInXhtml(ch, out, false, false, null);
    }

    public static void encodeTextInXhtml(char ch, Appendable out, boolean makeBr, boolean makeNbsp, EncodingState encodingState) throws IOException {
        String escaped = getEscapedCharacter(ch, makeBr, makeNbsp, encodingState);
        if(escaped!=null) out.append(escaped);
        else out.append(ch);
    }
    // </editor-fold>

    private boolean makeBr = false;
    private boolean makeNbsp = false;
    private final EncodingState encodingState = new EncodingState();

    protected TextInXhtmlEncoder(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.TEXT
            || inputType==MediaType.JAVASCRIPT  // No validation required
        ;
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    /**
     * @return the makeBr
     */
    public boolean isMakeBr() {
        return makeBr;
    }

    /**
     * @param makeBr the makeBr to set
     */
    public void setMakeBr(boolean makeBr) {
        this.makeBr = makeBr;
    }

    /**
     * @return the makeNbsp
     */
    public boolean isMakeNbsp() {
        return makeNbsp;
    }

    /**
     * @param makeNbsp the makeNbsp to set
     */
    public void setMakeNbsp(boolean makeNbsp) {
        this.makeNbsp = makeNbsp;
    }

    /**
     * Will accept values higher than <code>Character.MAX_VALUE</code>.
     * The wrapped out may reject them, however.
     */
    @Override
    public void write(int c) throws IOException {
        String escaped = getEscapedCharacter(c, makeBr, makeNbsp, encodingState);
        if(escaped!=null) out.write(escaped);
        else out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeTextInXhtml(cbuf, off, len, out, makeBr, makeNbsp, encodingState);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        encodeTextInXhtml(str, off, off+len, out, makeBr, makeNbsp, encodingState);
    }

    @Override
    public TextInXhtmlEncoder append(CharSequence csq) throws IOException {
        encodeTextInXhtml(csq, out, makeBr, makeNbsp, encodingState);
        return this;
    }

    @Override
    public TextInXhtmlEncoder append(CharSequence csq, int start, int end) throws IOException {
        encodeTextInXhtml(csq, start, end, out, makeBr, makeNbsp, encodingState);
        return this;
    }

    @Override
    public TextInXhtmlEncoder append(char c) throws IOException {
        encodeTextInXhtml(c, out, makeBr, makeNbsp, encodingState);
        return this;
    }
}
