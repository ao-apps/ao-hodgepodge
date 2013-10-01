/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.io.buffer;

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.JavaScriptInXhtmlEncoder.javaScriptInXhtmlEncoder;
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaWriter;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.util.AtomicSequence;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.Writer;

/**
 * Logs all write calls in a way that can be put into Java source code.
 * This is used to capture real-world scenarios for unit testing.
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class LoggingResult implements BufferResult {

	private static final Sequence idSeq = new AtomicSequence(0);

	final long id = idSeq.getNextSequenceValue();
	private final BufferResult wrapped;
	private final Writer log;

	protected LoggingResult(BufferResult wrapped, Writer log) throws IOException {
		this.wrapped = wrapped;
		this.log = log;
    }

	public long getId() {
		return id;
	}

	/**
	 * Provides detailed logging for a media encoder.
	 */
	private void log(MediaEncoder encoder) throws IOException {
		if(encoder==null) log.write("null");
		else if(encoder==javaScriptInXhtmlAttributeEncoder) log.write("javaScriptInXhtmlAttributeEncoder");
		else if(encoder==javaScriptInXhtmlEncoder) log.write("javaScriptInXhtmlEncoder");
		else if(encoder==textInXhtmlAttributeEncoder) log.write("textInXhtmlAttributeEncoder");
		else log.write(encoder.getClass().getName());
	}

	/**
	 * Provides detailed logging for a writer.
	 */
	private void log(Writer writer) throws IOException {
		if(writer==null) {
			log.write("null");
		} else if(writer instanceof LoggingWriter) {
			LoggingWriter loggingWriter = (LoggingWriter)writer;
			log.write("writer[");
			log.write(Long.toString(loggingWriter.getId()));
			log.write(']');
		} else if(writer instanceof MediaWriter) {
			MediaWriter mediaWriter = (MediaWriter)writer;
			log.write("new MediaWriter(");
			log(mediaWriter.getEncoder());
			log.write(", ");
			log(mediaWriter.getOut());
			log.write(')');
		} else {
			String classname = writer.getClass().getName();
			if(classname.equals("org.apache.jasper.runtime.BodyContentImpl")) log.write("bodyContent");
			else if(classname.equals("org.apache.jasper.runtime.JspWriterImpl")) log.write("jspWriter");
			else log.write(classname);
		}
	}

	@Override
    public long getLength() throws IOException {
		log.write("result[");
		log.write(Long.toString(id));
		log.write("].getLength();\n");
		log.flush();
		return wrapped.getLength();
    }

	@Override
	public boolean isFastToString() {
		return wrapped.isFastToString();
	}

	@Override
    public String toString() {
		try {
			log.write("result[");
			log.write(Long.toString(id));
			log.write("].toString();\n");
			log.flush();
		} catch(IOException e) {
			throw new WrappedException(e);
		}
		return wrapped.toString();
    }

	@Override
    public void writeTo(Writer out) throws IOException {
		log.write("result[");
		log.write(Long.toString(id));
		log.write("].writeTo(");
		log(out);
		log.write(");\n");
		log.flush();
		wrapped.writeTo(out);
    }

	@Override
    public void writeTo(Writer out, long off, long len) throws IOException {
		log.write("result[");
		log.write(Long.toString(id));
		log.write("].writeTo(");
		log(out);
		log.write(", ");
		log.write(Long.toString(off));
		log.write(", ");
		log.write(Long.toString(len));
		log.write(");\n");
		log.flush();
		wrapped.writeTo(out, off, len);
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IOException {
		log.write("result[");
		log.write(Long.toString(id));
		log.write("].writeTo(");
		log(encoder);
		log.write(", ");
		log(out);
		log.write(");\n");
		log.flush();
		wrapped.writeTo(encoder, out);
	}

	@Override
    public void writeTo(MediaEncoder encoder, Writer out, long off, long len) throws IOException {
		log.write("result[");
		log.write(Long.toString(id));
		log.write("].writeTo(");
		log(encoder);
		log.write(", ");
		log(out);
		log.write(", ");
		log.write(Long.toString(off));
		log.write(", ");
		log.write(Long.toString(len));
		log.write(");\n");
		log.flush();
		wrapped.writeTo(encoder, out, off, len);
	}

	@Override
	public LoggingResult trim() throws IOException {
		LoggingResult result = new LoggingResult(wrapped.trim(), log);
		log.write("result[");
		log.write(Long.toString(result.id));
		log.write("] = result[");
		log.write(Long.toString(id));
		log.write("].trim();\n");
		log.flush();
		return result;
	}
}
