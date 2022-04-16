/*
 * $Header: /var/cvs/aocode-public/src/com/aoindustries/md5/MD5InputStream.java,v 1.2 2013/07/10 20:30:28 orion Exp $
 *
 * MD5InputStream, a subclass of FilterInputStream implementing MD5
 * functionality on a stream.
 *
 * written Santeri Paavolainen, Helsinki Finland 1996
 * (c) Santeri Paavolainen, Helsinki Finland 1996
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * See http://www.cs.hut.fi/~santtu/java/ for more information on this
 * and the MD5 class.
 *
 * $Log: MD5InputStream.java,v $
 * Revision 1.2  2013/07/10 20:30:28  orion
 * Java 1.7 now.
 *
 * Revision 1.1  2006/05/22 00:51:15  orion
 * Current production version
 *
 * Revision 1.1  2005/07/06 15:08:19  orion
 * Adding to new CVS repository
 *
 * Revision 1.1.1.1  2002/07/16 05:12:24  orion
 * Imported sources
 *
 * Revision 1.4  2002/03/26 21:29:08  orion
 * Fixed backup bug, parameters now included in master_processes table
 *
 * Revision 1.3  2002/03/24 15:56:55  orion
 * Minor optimizations to the MD5 libraries
 *
 * Revision 1.2  2002/03/23 00:50:45  orion
 * Everything except file backup
 *
 * Revision 1.1  2002/01/20 10:26:47  orion
 * Added MD5 functions
 *
 * Revision 1.5  1996/12/12 10:46:44  santtu
 * Changed GPL to LGPL
 *
 * Revision 1.4  1996/12/12 10:31:02  santtu
 * Something.
 *
 * Revision 1.3  1996/04/15 07:28:09  santtu
 * Added GPL statemets, and RSA derivate stametemetsnnts.
 *
 * Revision 1.2  1996/01/09 10:20:44  santtu
 * Changed read() method to use offset too invoking md5.Update()
 *
 * Revision 1.1  1996/01/07 20:51:59  santtu
 * Initial revision
 *
 */

package com.aoapps.hodgepodge.md5;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MD5InputStream is a subclass of FilterInputStream adding MD5
 * hashing of the read input.
 *
 * @version	$Revision: 1.2 $
 * @author	Santeri Paavolainen &lt;santtu@cs.hut.fi&gt;
 */
public class MD5InputStream extends FilterInputStream {

	/**
	 * MD5 context
	 */
	private final MD5 md5;

	/**
	 * Creates a MD5InputStream
	 * @param in	The input stream
	 */
	public MD5InputStream (InputStream in) {
		super(in);

		md5 = new MD5();
	}

	/**
	 * Read a byte of data.
	 * @see java.io.FilterInputStream
	 */
	@Override
	public int read() throws IOException {
		int c = in.read();
		if (c == -1) return -1;

		md5.Update(c);

		return c;
	}

	/**
	 * Reads into an array of bytes.
	 */
	@Override
	public int read (byte[] bytes, int offset, int length) throws IOException {
		int	r;

		if ((r = in.read(bytes, offset, length)) == -1) return -1;

		md5.Update(bytes, offset, r);

		return r;
	}

	/**
	 * Returns array of bytes representing hash of the stream as
	 * finalized for the current state.
	 * @see MD5#Final()
	 */
	public byte [] hash () {
		return md5.Final();
	}
}