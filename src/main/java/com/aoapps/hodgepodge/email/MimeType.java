/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.email;

import com.aoapps.lang.io.ContentType;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Obtains MIME types for file names.
 *
 * @deprecated  This simple hard-coded list of MIME types is not maintained well.
 *              It is strongly recommended to use a different, well-supported API.
 *
 * @author  AO Industries, Inc.
 */
@Deprecated
public final class MimeType {

	/** Make no instances. */
	private MimeType() {throw new AssertionError();}

	public static final String DEFAULT_MIME_TYPE="unknown/unknown";

	// Related to LocaleFilter.java
	// Related to NoSessionFilter.java
	// Related to SessionResponseWrapper.java
	// Related to LastModifiedServlet.java
	// Related to ao-mime-mappings/…/web-fragment.xml
	// Related to ContentType.java
	// Is MimeType.java
	private static final String[] types={
		"aif", "audio/x-aiff",
		"aifc", "audio/x-aiff",
		"aiff", "audio/x-aiff",
		"asc", ContentType.TEXT,
		"au", "audio/basic",
		"avi", "video/x-msvideo",
		"bin", "application/octet-stream",
		"bmp", "image/bmp",
		"c", ContentType.TEXT,
		"class", "application/octet-stream",
		"cpio", "application/x-cpio",
		"csh", "application/x-csh",
		"css", ContentType.CSS,
		"doc", "application/msword",
		"dvi", "application/x-dvi",
		"eps", "application/postscript",
		"exe", "application/octet-stream",
		"gif", ContentType.GIF,
		"gtar", "application/x-gtar",
		"gz", "application/x-gzip",
		"h", ContentType.TEXT,
		"html", ContentType.HTML,
		"htm", ContentType.HTML,
		"jad", "text/vnd.sun.j2me.app-descriptor",
		"jar", "application/java-archive",
		"java", ContentType.TEXT,
		"jpeg", ContentType.JPEG,
		"jpe", ContentType.JPEG,
		"jpg", ContentType.JPEG,
		"js", "application/x-javascript",
		"latex", "application/x-latex",
		"log", ContentType.TEXT,
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
		"pid", ContentType.TEXT,
		"pgm", "image/x-portable-graymap",
		"png", ContentType.PNG,
		"pnm", "image/x-portable-anymap",
		"ppm", "image/x-portable-pixmap",
		"ppt", "application/vnd.ms-powerpoint",
		"properties", ContentType.TEXT,
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
		"sql", ContentType.TEXT,
		"swf", "application/x-shockwave-flash",
		"tar", "application/x-tar",
		"tcl", "application/x-tcl",
		"tex", "application/x-tex",
		"texi", "application/x-texinfo",
		"texinfo", "application/x-texinfo",
		"tiff", "image/tiff",
		"tif", "image/tiff",
		"ts", "text/tab-separated-values",
		"txt", ContentType.TEXT,
		"vrml", "model/vrml",
		"wav", "audio/x-wav",
		"wmlc", "application/vnd.wap.wmlc",
		"wmlsc", "application/vnd.wap.wmlscriptc",
		"wmls", "text/vnd.wap.wmlscript",
		"wml", "text/vnd.wap.wml",
		"wrl", "model/vrml",
		"xbm", "image/x-xbitmap",
		"xls", "application/vnd.ms-excel",
		"xml", ContentType.XML_OLD,
		"xpm", "image/x-xpixmap",
		"xwd", "image/x-xwindowdump",
		"z", "application/x-compress",
		"zip", "application/zip",
		// Web development
		"less",    ContentType.TEXT, // See https://stackoverflow.com/a/45102599
		"sass",    ContentType.SASS, // See https://stackoverflow.com/a/40893366
		"scss",    ContentType.SCSS, // See https://stackoverflow.com/a/40893366
		"css.map", ContentType.JSON, // See https://stackoverflow.com/a/44184109
		"js.map",  ContentType.JSON, // See https://stackoverflow.com/a/44184109
	};
	private static final Map<String, String> hash = new HashMap<>();
	static {
		for(int c=0;c<types.length;c+=2) {
			String extension = types[c].toLowerCase(Locale.ROOT);
			if(hash.put(extension, types[c+1])!=null) throw new AssertionError(MimeType.class.getName()+": extension found more than once: "+extension);
		}
	}

	public static String getMimeType(String filename) {
		int dotPos = filename.lastIndexOf('.');
		if(dotPos != -1) {
			String type = hash.get(filename.substring(dotPos + 1).toLowerCase(Locale.ROOT));
			if(type != null) return type;
			// Check for double-dot extension
			dotPos = filename.lastIndexOf('.', dotPos - 1);
			if(dotPos != -1) {
				type = hash.get(filename.substring(dotPos + 1).toLowerCase(Locale.ROOT));
				if(type != null) return type;
			}
		}
		return DEFAULT_MIME_TYPE;
	}
}
