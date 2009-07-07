package com.aoindustries.util.logging;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.ErrorPrinter;
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

    public String format(LogRecord record) {
        List<Object> extraInfo = new ArrayList<Object>(9); // At most 9 elements added below
        String loggerName = record.getLoggerName();
        if(loggerName!=null) extraInfo.add("record.loggerName="+loggerName);
        extraInfo.add("record.level="+record.getLevel());
        extraInfo.add("record.sequenceNumber="+record.getSequenceNumber());
        String sourceClassName = record.getSourceClassName();
        if(sourceClassName!=null) extraInfo.add("record.sourceClassName="+sourceClassName);
        String sourceMethodName = record.getSourceMethodName();
        if(sourceMethodName!=null) extraInfo.add("record.sourceMethodName="+sourceMethodName);
        String message = record.getMessage();
        if(message==null) message = "";
        extraInfo.add("record.message="+message);
        String formatted = formatMessage(record);
        if(formatted==null) formatted = "";
        if(!message.equals(formatted)) extraInfo.add("record.message.formatted="+formatted);
        extraInfo.add("record.threadID="+record.getThreadID());
        extraInfo.add("record.millis="+record.getMillis());
        Throwable thrown = record.getThrown();
        return ErrorPrinter.getStackTraces(
            thrown,
            extraInfo.toArray()
        );
    }
}
