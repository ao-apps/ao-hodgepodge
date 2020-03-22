/* 
 * $Header: /var/cvs/aocode-public/src/com/aoindustries/md5/MD5OutputStream.java,v 1.2 2013/07/10 20:30:28 orion Exp $
 *
 * MD5OutputStream, a subclass of FilterOutputStream implementing MD5
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
 * $Log: MD5OutputStream.java,v $
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
 * Revision 1.3  2002/03/24 15:56:57  orion
 * Minor optimizations to the MD5 libraries
 *
 * Revision 1.2  2002/03/23 00:50:46  orion
 * Everything except file backup
 *
 * Revision 1.1  2002/01/20 10:26:47  orion
 * Added MD5 functions
 *
 * Revision 1.3  1996/12/12 10:46:28  santtu
 * Changed GPL to LGPL
 *
 * Revision 1.2  1996/04/15 07:28:09  santtu
 * Added GPL statemets, and RSA derivate stametemetsnnts.
 *
 * Revision 1.1  1996/01/09 10:21:07  santtu
 * Initial revision
 *
 *
 */
package com.aoindustries.md5;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * MD5OutputStream is a subclass of FilterOutputStream adding MD5
 * hashing of the read output.
 *
 * @version	$Revision: 1.2 $
 * @author	Santeri Paavolainen &lt;santtu@cs.hut.fi&gt;
 */

public class MD5OutputStream extends FilterOutputStream {

	/**
	 * MD5 context
	 */
	final private MD5 md5;

	/**
	 * Creates MD5OutputStream
	 * @param out	The output stream
	 */

	public MD5OutputStream (OutputStream out) {
		super(out);

		md5 = new MD5();
	}

	/**
	 * Writes a byte. 
	 */
	@Override
	public void write (int b) throws IOException {
		out.write(b);
		md5.Update((byte) b);
	}

	/**
	 * Writes a sub array of bytes.
	 */
	@Override
	public void write (byte b[], int off, int len) throws IOException {
		out.write(b, off, len);
		md5.Update(b, off, len);
	}

	/**
	 * Returns array of bytes representing hash of the stream as finalized
	 * for the current state.
	 * @see MD5#Final()
	 */
	public byte[] hash () {
		return md5.Final();
	}
}
