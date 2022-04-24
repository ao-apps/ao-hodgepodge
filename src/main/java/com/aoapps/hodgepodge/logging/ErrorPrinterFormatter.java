/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.logging;

import com.aoapps.lang.util.ErrorPrinter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Uses <code>ErrorPrinter</code> to format log messages.
 *
 * @see  ErrorPrinter
 *
 * @author  AO Industries, Inc.
 */
public class ErrorPrinterFormatter extends Formatter {

  /**
   * Public constructor required so can be specified in <code>logging.properties</code>.
   */
  public ErrorPrinterFormatter() {
    // Do nothing
  }

  public void format(LogRecord rec, Appendable out) {
    List<Object> extraInfo = new ArrayList<>();
    String loggerName = rec.getLoggerName();
    if (loggerName != null) {
      extraInfo.add("rec.loggerName=" + loggerName);
    }
    extraInfo.add("rec.level=" + rec.getLevel());
    extraInfo.add("rec.sequenceNumber=" + rec.getSequenceNumber());
    String sourceClassName = rec.getSourceClassName();
    if (sourceClassName != null) {
      extraInfo.add("rec.sourceClassName=" + sourceClassName);
    }
    String sourceMethodName = rec.getSourceMethodName();
    if (sourceMethodName != null) {
      extraInfo.add("rec.sourceMethodName=" + sourceMethodName);
    }
    String message = rec.getMessage();
    if (message == null) {
      message = "";
    }
    extraInfo.add("rec.message=" + message);
    String formatted = formatMessage(rec);
    if (formatted == null) {
      formatted = "";
    }
    if (!message.equals(formatted)) {
      extraInfo.add("rec.message.formatted=" + formatted);
    }
    extraInfo.add("rec.threadID=" + rec.getThreadID());
    extraInfo.add("rec.millis=" + rec.getMillis());
    Throwable thrown = rec.getThrown();
    ErrorPrinter.printStackTraces(
        thrown,
        out,
        extraInfo.toArray()
    );
  }

  @Override
  public String format(LogRecord rec) {
    StringBuilder buffer = new StringBuilder(1024);
    format(rec, buffer);
    return buffer.toString();
  }
}
