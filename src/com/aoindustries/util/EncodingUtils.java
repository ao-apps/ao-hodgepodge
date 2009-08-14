/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util;

import java.io.IOException;

/**
 * Provides encoding and escaping for various type of data.
 *
 * @author  AO Industries, Inc.
 */
public final class EncodingUtils {

    private EncodingUtils() {
    }

    private static final String EOL = System.getProperty("line.separator");
    private static final String BR_EOL = "<br />"+EOL;

    // <editor-fold defaultstate="collapsed" desc="XML Attributes">
    /**
     * Escapes for use in a XML attribute and writes to the provided <code>Appendable</code>.
     * Any characters less than 0x1f that are not \t, \r, or \n are completely filtered.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     *
     * @see  #encodeXml(CharSequence, Appendable)
     */
    public static void encodeXmlAttribute(CharSequence S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '<':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&lt;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&gt;");
                        break;
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#39;");
                        break;
                    case '"':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&quot;");
                        break;
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    case '\r':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#xD;");
                        break;
                    case '\n':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#xA;");
                        break;
                    default:
                        if(ch<' ') {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, c);
                                toPrint=0;
                            }
                            // skip the character
                        } else {
                            toPrint++;
                        }
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, len);
            }
        }
    }

    /**
     * @see #encodeXmlAttribute(CharSequence, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeXmlAttribute(CharSequence S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length());
        encodeXmlAttribute(S, result);
        return result.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="(X)HTML">
    /**
     * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
     * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt;, tabs to &amp;#x9;, and spaces to &amp;#160;
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void encodeHtml(CharSequence S, Appendable out) throws IOException {
        encodeHtml(S, true, true, out);
    }

    /**
     * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
     * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void encodeHtml(CharSequence S, int start, int end, Appendable out) throws IOException {
        encodeHtml(S, start, end, true, true, out);
    }

    /**
     * @see #encodeHtml(CharSequence, Appendable)
     */
    public static String encodeHtml(CharSequence S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length());
        encodeHtml(S, result);
        return result.toString();
    }

    /**
     * @see #encodeHtml(CharSequence, Appendable)
     */
    public static void encodeHtml(char ch, Appendable out) throws IOException {
        encodeHtml(ch, true, true, out);
    }

    /**
     * @see #encodeHtml(java.lang.CharSequence, int, int, boolean, boolean, java.lang.Appendable)
     */
    public static void encodeHtml(CharSequence S, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
        if(S!=null) encodeHtml(S, 0, S.length(), make_br, make_nbsp, out);
    }

    /**
     * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
     * Optionally, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
     * Any characters less than 0x1f that are not \t, \r, or \n are completely filtered.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     * @param make_br  will write &lt;br /&gt; tags for every newline character
     * @param make_nbsp  will write &amp;#160; for a space when another space follows
     */
    public static void encodeHtml(CharSequence S, int start, int end, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
        if (S != null) {
            int toPrint = 0;
            for (int c = start; c < end; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    // Standard XML escapes
                    case '<':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&lt;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&gt;");
                        break;
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
                    // Special (X)HTML options
                    case ' ':
                        if(make_nbsp) {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, c);
                                toPrint=0;
                            }
                            out.append("&#160;");
                        } else {
                            toPrint++;
                        }
                        break;
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    case '\r':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        // skip '\r'
                        break;
                    case '\n':
                        if(make_br) {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, c);
                                toPrint=0;
                            }
                            out.append("<br />\n");
                        } else {
                            toPrint++;
                        }
                        break;
                    default:
                        if(ch<' ') {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, c);
                                toPrint=0;
                            }
                            // skip the character
                        } else {
                            toPrint++;
                        }
                }
            }
            if(toPrint>0) out.append(S, end-toPrint, end);
        }
    }

    /**
     * @see #encodeHtml(CharSequence, boolean, boolean, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeHtml(CharSequence S, boolean make_br, boolean make_nbsp) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length());
        encodeHtml(S, make_br, make_nbsp, result);
        return result.toString();
    }

    /**
     * @see #encodeHtml(CharSequence, boolean, boolean, Appendable)
     */
    public static void encodeHtml(char ch, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
        switch(ch) {
            // Standard XML escapes
            case '<':
                out.append("&lt;");
                break;
            case '>':
                out.append("&gt;");
                break;
            case '&':
                out.append("&amp;");
                break;
            // Special (X)HTML options
            case ' ':
                if(make_nbsp) {
                    out.append("&#160;");
                } else {
                    out.append(' ');
                }
                break;
            case '\t':
                out.append("&#x9;");
                break;
            case '\r':
                // skip '\r'
                break;
            case '\n':
                if(make_br) {
                    out.append(BR_EOL);
                } else {
                    out.append('\n');
                }
                break;
            default:
                if(ch<' ') {
                    // skip the character
                } else {
                    out.append(ch);
                }
        }
    }
    // </editor-fold>
}
