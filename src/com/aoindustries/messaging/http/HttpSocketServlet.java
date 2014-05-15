/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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
package com.aoindustries.messaging.http;

import com.aoindustries.io.AoByteArrayOutputStream;
import com.aoindustries.messaging.AbstractSocket;
import com.aoindustries.messaging.AbstractSocketContext;
import com.aoindustries.messaging.Message;
import com.aoindustries.messaging.MessageType;
import com.aoindustries.messaging.Socket;
import com.aoindustries.messaging.tcp.TcpSocket;
import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Server component for bi-directional messaging over HTTP.
 * This is a synchronous implementation compatible with older environments.
 */
public class HttpSocketServlet extends HttpServlet {

	//private static final Logger logger = Logger.getLogger(HttpSocketServlet.class.getName());

	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = true;

	public static class ServletSocket extends AbstractSocket {
		
		private final String serverName;

		ServletSocket(
			ServletSocketContext socketContext,
			Identifier id,
			long connectTime,
			SocketAddress remoteSocketAddress,
			String serverName
		) {
			super(
				socketContext,
				id,
				connectTime,
				remoteSocketAddress
			);
			this.serverName = serverName;
		}

		/**
		 * All requests for this socket must use the same server name.
		 */
		public String getServerName() {
			return serverName;
		}

		@Override
		public String getProtocol() {
			return HttpSocket.PROTOCOL;
		}

		// Expose to this package
		@Override
		protected Future<?> callOnMessages(List<? extends Message> messages) throws IllegalStateException {
			return super.callOnMessages(messages);
		}

		// Expose to this package
		@Override
		protected Future<?> callOnError(Exception exc) throws IllegalStateException {
			return super.callOnError(exc);
		}

		@Override
		protected void startImpl(Callback<? super Socket> onStart, Callback<? super Exception> onError) {
			// Nothing to do
			if(onStart!=null) onStart.call(this);
		}

		@Override
		protected void sendMessagesImpl(Collection<? extends Message> messages) {
			// TODO
		}
	}

	public static class ServletSocketContext extends AbstractSocketContext<ServletSocket> {
		// Expose to this package
		@Override
		protected Identifier newIdentifier() {
			return super.newIdentifier(); //To change body of generated methods, choose Tools | Templates.
		}

		// Expose to this package
		@Override
		protected void addSocket(ServletSocket newSocket) {
			super.addSocket(newSocket);
		}
	}

	protected final ServletSocketContext socketContext = new ServletSocketContext();

	public HttpSocketServlet() {
	}

	/**
	 * Last modified times must never be used.
	 */
	@Override
	final protected long getLastModified(HttpServletRequest request) {
		return -1;
	}

	/**
	 * Get requests must never be used.
	 */
	@Override
	final protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if("connect".equals(action)) {
			long connectTime = System.currentTimeMillis();
			Identifier id = socketContext.newIdentifier();
			// Build the response
			AoByteArrayOutputStream bout = new AoByteArrayOutputStream();
			try {
				DataOutputStream out = new DataOutputStream(bout);
				try {
					out.writeBytes("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
						+ "<connection id=\"");
					out.writeBytes(id.toString());
					out.writeBytes("\"/>");
				} finally {
					out.close();
				}
			} finally {
				bout.close();
			}
			response.setContentType("application/xml");
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(bout.size());
			OutputStream out = response.getOutputStream();
			try {
				out.write(bout.getInternalByteArray(), 0, bout.size());
			} finally {
				out.close();
			}
			// Determine the port
			int remotePort = request.getRemotePort();
			if(remotePort < 0) remotePort = 0; // < 0 when unknown such as old AJP13 protocol
			if(DEBUG) System.err.println("DEBUG: HttpSocketServlet: doPost: remotePort="+remotePort);
			ServletSocket servletSocket = new ServletSocket(
				socketContext,
				id,
				connectTime,
				new InetSocketAddress(
					request.getRemoteAddr(),
					remotePort
				),
				request.getServerName()
			);
			socketContext.addSocket(servletSocket);
		} else if("messages".equals(action)) {
			Identifier id = Identifier.valueOf(request.getParameter("id"));
			if(DEBUG) System.err.println("DEBUG: HttpSocketServlet: doPost: id="+id);
			ServletSocket socket = socketContext.getSocket(id);
			if(socket==null) {
				if(DEBUG) System.err.println("DEBUG: HttpSocketServlet: doPost: socket not found");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Socket id not found");
			} else {
				try {
					int size = Integer.parseInt(request.getParameter("l"));
					if(DEBUG) System.err.println("DEBUG: HttpSocketServlet: doPost: size="+size);
					List<Message> messages = new ArrayList<Message>(size);
					for(int i=0; i<size; i++) {
						// Get the type
						MessageType type = MessageType.getFromTypeChar(request.getParameter("t"+i).charAt(0));
						// Get the message string
						String encodedMessage = request.getParameter("m"+i);
						// Decode and add
						messages.add(type.decode(encodedMessage));
					}
					if(!messages.isEmpty()) socket.callOnMessages(Collections.unmodifiableList(messages));
					// TODO: Wait for any return messages
				} catch(Exception e) {
					socket.callOnError(e);
					if(e instanceof ServletException) throw (ServletException)e;
					if(e instanceof IOException) throw (IOException)e;
					if(e instanceof RuntimeException) throw (RuntimeException)e;
					throw new ServletException(e);
				}
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected action: " + action);
		}
	}

	/**
	 * When the servlet is destroyed, all active sockets are also closed.
	 */
	@Override
	public void destroy() {
		socketContext.close();
	}
}
