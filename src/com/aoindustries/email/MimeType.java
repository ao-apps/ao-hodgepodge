package com.aoindustries.email;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.*;

/**
 * Obtains MIME types for file names.
 *
 * @author  AO Industries, Inc.
 */
public class MimeType {

    public static final String DEFAULT_MIME_TYPE="unknown/unknown";

    private static final String[] types={
        "aif", "audio/x-aiff",
        "aifc", "audio/x-aiff",
        "aiff", "audio/x-aiff",
        "asc", "text/plain",
        "au", "audio/basic",
        "avi", "video/x-msvideo",
        "bin", "application/octet-stream",
        "bmp", "image/bmp",
        "c", "text/plain",
        "class", "application/octet-stream",
        "cpio", "application/x-cpio",
        "csh", "application/x-csh",
        "css", "text/css",
        "doc", "application/msword",
        "dvi", "application/x-dvi",
        "eps", "application/postscript",
        "exe", "application/octet-stream",
        "gif", "image/gif",
        "gtar", "application/x-gtar",
        "gz", "application/x-gzip",
        "h", "text/plain",
        "html", "text/html",
        "htm", "text/html",
        "jad", "text/vnd.sun.j2me.app-descriptor",
        "jar", "application/java-archive",
        "java", "text/plain",
        "jpeg", "image/jpeg",
        "jpe", "image/jpeg",
        "jpg", "image/jpeg",
        "js", "application/x-javascript",
        "latex", "application/x-latex",
        "log", "text/plain",
        "m3u", "audio/x-mpegurl",
        "man", "application/x-troff-man",
        "mid", "audio/midi",
        "midi", "audio/midi",
        "movie", "video/x-sgi-movie",
        "mov", "video/quicktime",
        "mpeg", "video/mpeg",
        "mpe", "video/mpeg",
        "mp2", "audio/mpeg",
        "mp3", "audio/mpeg",
        "mpga", "audio/mpeg",
        "mpg", "video/mpeg",
        "pbm", "image/x-portable-bitmap",
        "pdf", "application/pdf",
        "pid", "text/plain",
        "pgm", "image/x-portable-graymap",
        "png", "image/png",
        "pnm", "image/x-portable-anymap",
        "ppm", "image/x-portable-pixmap",
        "ppt", "application/vnd.ms-powerpoint",
        "properties", "text/plain",
        "ps", "application/postscript",
        "qt", "video/quicktime",
        "ra", "audio/x-realaudio",
        "ram", "audio/x-pn-realaudio",
        "rar", "application/x-rar-compressed",
        "ras", "image/x-cmu-raster",
        "rgb", "image/x-rgb",
        "rm", "audio/x-pn-realaudio",
        "rtf", "text/rtf",
        "rtx", "text/richtext",
        "sgml", "text/sgml",
        "sgm", "text/sgml",
        "sh", "application/x-sh",
        "sit", "application/x-stuffit",
        "snd", "audio/basic",
        "sql", "text/plain",
        "swf", "application/x-shockwave-flash",
        "tar", "application/x-tar",
        "tcl", "application/x-tcl",
        "tex", "application/x-tex",
        "texi", "application/x-texinfo",
        "texinfo", "application/x-texinfo",
        "tiff", "image/tiff",
        "tif", "image/tiff",
        "ts", "text/tab-separated-values",
        "txt", "text/plain",
        "vrml", "model/vrml",
        "wav", "audio/x-wav",
        "wmlc", "application/vnd.wap.wmlc",
        "wmlsc", "application/vnd.wap.wmlscriptc",
        "wmls", "text/vnd.wap.wmlscript",
        "wml", "text/vnd.wap.wml",
        "wrl", "model/vrml",
        "xbm", "image/x-xbitmap",
        "xls", "application/vnd.ms-excel",
        "xml", "text/xml",
        "xpm", "image/x-xpixmap",
        "xwd", "image/x-xwindowdump",
        "z", "application/x-compress",
        "zip", "application/zip"
    };
    private static Map<String,String> hash;

    private MimeType() {}
    
    public static String getMimeType(String filename) {
        int pos=filename.lastIndexOf('.');
        if(pos!=-1) {
            String extension=filename.substring(pos+1);
            synchronized(MimeType.class) {
                if(hash==null) {
                    hash=new HashMap<String,String>();
                    for(int c=0;c<types.length;c+=2) {
                        if(hash.put(types[c].toLowerCase(), types[c+1])!=null) System.err.println(MimeType.class.getName()+".getMimeType(String): Warning: extension found more than once: "+extension);
                    }
                }
                String type=hash.get(extension.toLowerCase());
                if(type!=null) return type;
            }
        }
        return DEFAULT_MIME_TYPE;
    }
}