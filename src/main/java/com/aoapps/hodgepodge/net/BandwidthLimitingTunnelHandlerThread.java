/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.hodgepodge.net;

import com.aoapps.hodgepodge.io.BitRateOutputStream;
import com.aoapps.hodgepodge.io.BitRateProvider;
import com.aoapps.lang.util.BufferManager;
import com.aoapps.lang.util.ErrorPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.SocketException;

/**
 * Handles one direction of bandwidth for one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnelHandler
 * @see  BandwidthLimitingTunnel
 *
 * @author  AO Industries, Inc.
 */
public abstract class BandwidthLimitingTunnelHandlerThread implements Runnable, BitRateProvider {

	/**
	 * The number of seconds between verbose bits/second output.
	 */
	private static final long VERBOSE_REPORT_INTERVAL = 10L * 1000;

	private final boolean verbose;
	private final Long bandwidth;
	private final Socket listenSocket;
	private final Socket connectSocket;
	private volatile Thread thread;

	protected BandwidthLimitingTunnelHandlerThread(
		boolean verbose,
		Long bandwidth,
		Socket listenSocket,
		Socket connectSocket
	) {
		this.verbose = verbose;
		this.bandwidth = bandwidth;
		this.listenSocket = listenSocket;
		this.connectSocket = connectSocket;
	}

	public void start() {
		if(thread!=null) throw new IllegalStateException();
		(this.thread = new Thread(this)).start();
	}

	@Override
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void run() {
		try {
			long totalBytes = 0;
			long startTime = verbose ? System.currentTimeMillis() : 0;
			byte[] buff = BufferManager.getBytes();
			try {
				OutputStream out = getOutputStream(listenSocket, connectSocket);
				if(bandwidth != null) out = new BitRateOutputStream(out, this);
				try {
					try (InputStream in = getInputStream(listenSocket, connectSocket)) {
						long blockStartTime = verbose ? System.currentTimeMillis() : 0;
						long blockByteCount = 0;
						int ret;
						while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
							out.write(buff, 0, ret);
							totalBytes+=ret;
							if(verbose) {
								blockByteCount+=ret;
								long currentTime = System.currentTimeMillis();
								long blockTime = currentTime - blockStartTime;
								if(blockTime < 0) {
									// System time updated
									blockStartTime = currentTime;
									blockByteCount = 0;
								} else if(blockTime >= VERBOSE_REPORT_INTERVAL) {
									System.out.println(getDirection()+" "+totalBytes+" bytes sent in "+BigDecimal.valueOf(currentTime-startTime, 3)+" seconds, "+(blockByteCount * 8000 / blockTime)+" bits/second");
									blockStartTime = currentTime;
									blockByteCount = 0;
								}
							}
						}
					} catch(SocketException err) {
						// Normal socket closure
						if(!"Socket closed".equals(err.getMessage())) throw err;
					}
				} finally {
					out.close();
				}
			} finally {
				BufferManager.release(buff, false);
			}
			if(verbose) {
				long endTime = System.currentTimeMillis();
				System.out.println(getDirection()+" Connection closing: "+totalBytes+" bytes sent in "+BigDecimal.valueOf(endTime-startTime, 3)+" seconds, "+(totalBytes * 8000 / (endTime - startTime))+" bits/second average");
			}
		} catch(IOException err) {
			ErrorPrinter.printStackTraces(err, System.err);
		}
	}

	@Override
	public int getBlockSize() {
		return BufferManager.BUFFER_SIZE;
	}

	@Override
	public Long getBitRate() {
		return bandwidth;
	}

	protected abstract String getDirection();

	protected abstract OutputStream getOutputStream(Socket listenSocket, Socket connectSocket) throws IOException;

	protected abstract InputStream getInputStream(Socket listenSocket, Socket connectSocket) throws IOException;
}
