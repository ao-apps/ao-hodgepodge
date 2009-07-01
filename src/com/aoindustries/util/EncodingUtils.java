package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;

/**
 * Provides encoding and escaping for various type of data.
 *
 * @author  AO Industries, Inc.
 */
public final class EncodingUtils {
    private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static char getHex(int value) {
        return hexChars[value & 15];
    }

    /**
     * Escapes for use in a XML attribute and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     *
     * @see  #encodeXml(java.lang.String, Appendable)
     */
    public static void encodeXmlAttribute(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
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
                    case '\r':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#xD;");
                        break;
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    case '\n':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#xA;");
                        break;
                    /*case ' ':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("#x20;");
                        break;*/
                    case '"':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&quot;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#39;");
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, len);
            }
        }
    }

    /**
     * @see #encodeXmlAttribute(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeXmlAttribute(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeXmlAttribute(S, result);
        return result.toString();
    }

    /**
     * Escapes for use in a XML body and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     *
     * @see  #encodeXmlAttribute(java.lang.String, Appendable)
     */
    public static void encodeXml(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
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
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, len);
            }
        }
    }

    /**
     * @see #encodeXml(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeXml(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeXml(S, result);
        return result.toString();
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Appendable</code>.
     * It turns newlines into &lt;br /&gt; and extra spaces to &amp;#160;
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void encodeHtml(String S, Appendable out) throws IOException {
        encodeHtml(S, true, true, out);
    }

    /**
     * @see #encodeHtml(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeHtml(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeHtml(S, result);
        return result.toString();
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     * @param make_nbsp  will write &amp;#160; for a space when another space follows
     */
    public static void encodeHtml(String S, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case ' ':
                        if(make_nbsp && c<(len-1) && S.charAt(c+1)==' ') {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, c);
                                toPrint=0;
                            }
                            out.append("&#160;");
                        } else {
                            toPrint++;
                        }
                        break;
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
                    case '"':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&quot;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, c);
                            toPrint=0;
                        }
                        out.append("&#39;");
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
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, len);
            }
        }
    }

    /**
     * @see #encodeHtml(java.lang.String, boolean, boolean, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeHtml(String S, boolean make_br, boolean make_nbsp) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeHtml(S, make_br, make_nbsp, result);
        return result.toString();
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string in
     * a XML CDATA area.  The string should be in double quotes, the quotes are not provided by
     * this call.
     *
     * Writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.
     */
    public static void encodeJavaScriptString(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if (ch == '"') out.append("\\\"");
                else if (ch == '\'') out.append("\\'");
                else if (ch == '\\') out.append("\\\\");
                else if (ch == '\b') out.append("\\b");
                else if (ch == '\f') out.append("\\f");
                else if (ch == '\r') out.append("\\r");
                else if (ch == '\n') out.append("\\n");
                else if (ch == '\t') out.append("\\t");
                // Also escape any < > & and ] to avoid early end of XHTML CDATA sections
                // and to not allow data to interfere with the XML parser.
                else if (ch == '<') out.append("\\u003c");
                else if (ch == '>') out.append("\\u003e");
                else if (ch == '&') out.append("\\u0026");
                else if (ch == ']') out.append("\\u005d");
                else if (ch<' ') {
                    out.append("\\u00");
                    int chInt = ch;
                    out.append(getHex(chInt>>>4));
                    out.append(getHex(chInt));
                } else {
                    out.append(ch);
                }
            }
        }
    }

    /**
     * @see #encodeJavaScriptString(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String encodeJavaScriptString(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeJavaScriptString(S, result);
        return result.toString();
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string in
     * an HTML attribute.  The string should be in JavaScript quotes, the quotes
     * are not provided by this call.
     *
     * Writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.
     */
    public static void encodeJavaScriptStringInXmlAttribute(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if (ch == '"') out.append("\\&quot;");
                else if (ch == '\'') out.append("\\&#39;");
                else if (ch == '\\') out.append("\\\\");
                else if (ch == '\b') out.append("\\b");
                else if (ch == '\f') out.append("\\f");
                else if (ch == '\r') out.append("\\r");
                else if (ch == '\n') out.append("\\n");
                else if (ch == '\t') out.append("\\t");
                else if (ch == ' ') out.append("\\u0020");
                // Also escape any < > & and ] to avoid early end of XHTML CDATA sections
                // and to not allow data to interfere with the XML parser.
                else if (ch == '<') out.append("\\u003c");
                else if (ch == '>') out.append("\\u003e");
                else if (ch == '&') out.append("\\u0026");
                else if (ch == ']') out.append("\\u005d");
                else if (ch<' ') {
                    out.append("\\u00");
                    int chInt = ch;
                    out.append(getHex(chInt>>>4));
                    out.append(getHex(chInt));
                } else {
                    out.append(ch);
                }
            }
        }
    }

    /**
     * @see #encodeJavaScriptStringInXmlAttribute(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped to be a JavaScript string in a XML attribute.
     */
    public static String encodeJavaScriptStringInXmlAttribute(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        encodeJavaScriptStringInXmlAttribute(S, result);
        return result.toString();
    }
}
