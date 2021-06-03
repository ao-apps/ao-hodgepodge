/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import com.aoindustries.util.BufferManager;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A way to more easily manipulate files with fixed-record-size rows.
 *
 * @author  AO Industries, Inc.
 */
public class FixedRecordFile extends RandomAccessFile {

	final private int recordLength;

	final private byte[] buff1;
	final private byte[] buff2;

	public FixedRecordFile(
		String name,
		String mode,
		int recordLength
	) throws FileNotFoundException {
		super(name, mode);
		this.recordLength=recordLength;
		buff1=new byte[recordLength];
		buff2=new byte[recordLength];
	}

	public FixedRecordFile(
		File file,
		String mode,
		int recordLength
	) throws FileNotFoundException {
		super(file, mode);
		this.recordLength=recordLength;
		buff1=new byte[recordLength];
		buff2=new byte[recordLength];
	}

	public void seekToExistingRecord(int index) throws IndexOutOfBoundsException, IOException {
		if(index<0) throw new IndexOutOfBoundsException(index+"<0");
		long startPos=(long)index*recordLength;
		if(startPos>=length()) throw new IndexOutOfBoundsException(index+">="+getRecordCount());
		seek(startPos);
	}

	public void addRecord(int index) throws IndexOutOfBoundsException, IOException {
		addRecords(index, 1);
	}

	public void addRecords(int index, int numRecords) throws IndexOutOfBoundsException, IOException {
		if(numRecords<0) throw new IllegalArgumentException("numRecords<0: "+numRecords);

		if(numRecords>0) {
			if(index<0) throw new IndexOutOfBoundsException("index<0: "+index);
			long recordsStart=(long)index*recordLength;
			long recordsBytes=(long)numRecords*recordLength;
			long recordsEnd=recordsStart+recordsBytes;

			if(recordsStart>=length()) {
				// Add the record to the end
				setLength(recordsEnd);
			} else {
				// Insert the record if not at the end
				long moveLength=length()-recordsStart;
				setLength(length()+recordsBytes);
				copyBytes(this, recordsStart, this, recordsStart+recordsBytes, moveLength);
			}
			seek(recordsStart);
		}
	}

	public int getRecordCount() throws IOException {
		long size=length()/recordLength;
		return size>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)size;
	}

	public static void copyBytes(RandomAccessFile from, long fromIndex, RandomAccessFile to, long toIndex, long numBytes) throws IOException {
		if(numBytes<0) throw new IllegalArgumentException("numBytes<0: "+numBytes);

		if(numBytes>0) {
			byte[] buff=BufferManager.getBytes();
			try {
				if(fromIndex<toIndex) {
					// Perform the copy backward
					long readLocation=fromIndex+numBytes;
					while(readLocation>fromIndex) {
						long readEnd=readLocation;
						readLocation-=BufferManager.BUFFER_SIZE;
						if(readLocation<fromIndex) readLocation=fromIndex;
						from.seek(readLocation);
						int pos=0;
						int bytesLeft;
						while((bytesLeft=(int)(readEnd-readLocation-pos))>0) {
							int ret=from.read(buff, pos, bytesLeft);
							if(ret==-1) throw new EOFException();
							pos+=ret;
						}
						to.seek(toIndex+(readLocation-fromIndex));
						to.write(buff, 0, pos);
					}
				} else {
					// Perform the copy forward
					long numCopied=0;
					while(numCopied<numBytes) {
						long blockSizeLong=numBytes-numCopied;
						int blockSize=blockSizeLong>BufferManager.BUFFER_SIZE?BufferManager.BUFFER_SIZE:(int)blockSizeLong;
						from.seek(fromIndex+numCopied);
						int ret=from.read(buff, 0, blockSize);
						if(ret==-1) throw new EOFException();
						to.seek(toIndex+numCopied);
						to.write(buff, 0, ret);
						numCopied+=ret;
					}
				}
			} finally {
				BufferManager.release(buff, false);
			}
		}
	}

	public static void copyRecords(FixedRecordFile from, long fromIndex, FixedRecordFile to, long toIndex, long numRecords) throws IOException {
		if(numRecords<0) throw new IllegalArgumentException("numRecords<0: "+numRecords);

		int recordLength=from.recordLength;
		if(recordLength!=to.recordLength) throw new IllegalArgumentException("Files do not have the same record length: from.recordLength="+recordLength+", to.recordLength="+to.recordLength);

		copyBytes(from, fromIndex*(long)recordLength, to, toIndex*(long)recordLength, numRecords*(long)recordLength);
	}

	public void removeAllRecords() throws IOException {
		setLength(0);
	}

	public void removeRecord(int index) throws IOException {
		if(index<0) throw new IndexOutOfBoundsException(index+"<0");
		long startPos=(long)index*recordLength;
		if(startPos>=length()) throw new IndexOutOfBoundsException(index+">="+getRecordCount());

		// Shift objects if not at new end of list
		long newEnd=length()-recordLength;
		if(newEnd>startPos) copyBytes(this, startPos+recordLength, this, startPos, newEnd-startPos);

		// Truncate the file
		setLength(newEnd);
	}

	public int getRecordLength() {
		return recordLength;
	}

	public void swap(int index1, int index2) throws IOException {
		if(index1!=index2) {
			if(index1<0) throw new IndexOutOfBoundsException("index1<0: "+index1);
			long startPos1=(long)index1*recordLength;
			if(startPos1>=length()) throw new IndexOutOfBoundsException("index1>="+getRecordCount()+": "+index1);

			if(index2<0) throw new IndexOutOfBoundsException("index2<0: "+index2);
			long startPos2=(long)index2*recordLength;
			if(startPos2>=length()) throw new IndexOutOfBoundsException("index2>="+getRecordCount()+": "+index2);

			// Do the swap
			seek(startPos1);
			readFully(buff1);
			seek(startPos2);
			readFully(buff2);
			seek(startPos2);
			write(buff1);
			seek(startPos1);
			write(buff2);
		}
	}
}
