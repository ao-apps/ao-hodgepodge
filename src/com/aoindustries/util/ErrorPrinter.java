package com.aoindustries.util;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.WrappedSQLException;
import java.io.CharArrayWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * Prints errors with more detail than a standard printStackTrace() call.  Is also able to
 * capture the error into a <code>String</code>.
 *
 * @author  AO Industries, Inc.
 */
public class ErrorPrinter {

    public static void printStackTraces(Throwable T) {
        printStackTraces(T, System.err, null);
    }

    public static void printStackTraces(Throwable T, Object[] extraInfo) {
        printStackTraces(T, System.err, extraInfo);
    }

    public static void printStackTraces(Throwable T, PrintStream out) {
        printStackTraces(T, out, null);
    }

    public static void printStackTraces(Throwable T, PrintStream out, Object[] extraInfo) {
        synchronized(out) {
            out.println();
            out.println("**************************");
            out.println("* BEGIN EXCEPTION REPORT *");
            out.println("**************************");
            out.println();
            out.println("    Time ");
            out.print("        ");
            out.println(new java.util.Date(System.currentTimeMillis()).toString());

            // Extra info
            if(extraInfo!=null && extraInfo.length>0) {
                out.println("    Extra Information");
                for(int c=0;c<extraInfo.length;c++) {
                    out.print("        ");
                    out.println(extraInfo[c]);
                }
            }

            // Threads
            out.println("    Threading");
            out.println("        Thread");
            out.print("            Name........: ");
            Thread thread=Thread.currentThread();
            out.println(thread.getName());
            out.print("            Class.......: ");
            out.println(thread.getClass().getName());
            out.print("            Priority....: ");
            out.println(thread.getPriority());
            try {
                ThreadGroup TG=thread.getThreadGroup();
                while(TG!=null) {
                    String name=TG.getName();
                    String classname=TG.getClass().getName();
                    int maxPriority=TG.getMaxPriority();
                    out.println("        ThreadGroup");
                    out.print("            Name........: "); out.println(name);
                    out.print("            Class.......: "); out.println(classname);
                    out.print("            Max Priority: "); out.println(maxPriority);
                    TG=TG.getParent();
                }
            } catch(SecurityException err) {
                out.println("Unable to print all Thread Groups: "+err.toString());
            }

            out.println("    Exceptions");
            printThrowables(T, out, 8);

            // End Report
            out.println();
            out.println("**************************");
            out.println("*  END EXCEPTION REPORT  *");
            out.println("**************************");

            // Flush output
            out.flush();
        }
    }

    private static void printThrowables(Throwable T, PrintStream out, int indent) {
        for(int c=0;c<indent;c++) out.print(' ');
        out.println(T.getClass().getName());
        printMessage(out, indent+4, "Message...........: ", T.getMessage());
        printMessage(out, indent+4, "Localized Message.: ", T.getLocalizedMessage());
        if(T instanceof SQLException) {
            SQLException sql=(SQLException)T;
            if(sql instanceof WrappedSQLException) printMessage(out, indent+4, "SQL Statement.....: ", ((WrappedSQLException)sql).getSqlString());
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.print("SQL Error Code....: ");
            out.println(sql.getErrorCode());
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.print("SQL State.........: ");
            out.println(sql.getSQLState());
        } else if(T instanceof WrappedException) {
            WrappedException wrapped=(WrappedException)T;
            Object[] wrappedInfo=wrapped.getExtraInfo();
            if(wrappedInfo!=null && wrappedInfo.length>0) {
                for(int c=0;c<(indent+4);c++) out.print(' ');
                out.println("Extra Information");
                for(int c=0;c<wrappedInfo.length;c++) {
                    for(int d=0;d<(indent+8);d++) out.print(' ');
                    out.println(wrappedInfo[c]);
                }
            }
        }
        for(int c=0;c<(indent+4);c++) out.print(' ');
        out.println("Stack Trace");
        StackTraceElement[] stack=T.getStackTrace();
        for(int c=0;c<stack.length;c++) {
            for(int d=0;d<(indent+8);d++) out.print(' ');
            out.print("at ");
            out.println(stack[c].toString());
        }
        Throwable cause=T.getCause();
        if(cause!=null) {
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.println("Caused By");
            printThrowables(cause, out, indent+8);
        }
        // Uses reflection avoid binding to JspException directly.
        try {
            Class<?> clazz=T.getClass();
            if(isSubclass(clazz, "javax.servlet.jsp.JspException")) {
                Method method=clazz.getMethod("getRootCause", new Class[0]);
                Throwable rootCause=(Throwable)method.invoke(T, new Object[0]);
                if(rootCause!=null) {
                    for(int c=0;c<(indent+4);c++) out.print(' ');
                    out.println("Caused By");
                    printThrowables(rootCause, out, indent+8);
                }
            }
        } catch(NoSuchMethodException err) {
            // OK, future versions of JspException might not have getRootCause
        } catch(IllegalAccessException err) {
            // OK, future versions of JspException could make it private
        } catch(InvocationTargetException err) {
            // Ignored because we are dealing with one exception at a time
            // Afterall, this is the exception handling code
        }
        // Uses reflection avoid binding to ServletException directly.
        try {
            Class<?> clazz=T.getClass();
            if(isSubclass(clazz, "javax.servlet.ServletException")) {
                Method method=clazz.getMethod("getRootCause", new Class[0]);
                Throwable rootCause=(Throwable)method.invoke(T, new Object[0]);
                if(rootCause!=null) {
                    for(int c=0;c<(indent+4);c++) out.print(' ');
                    out.println("Caused By");
                    printThrowables(rootCause, out, indent+8);
                }
            }
        } catch(NoSuchMethodException err) {
            // OK, future versions of ServletException might not have getRootCause
        } catch(IllegalAccessException err) {
            // OK, future versions of ServletException could make it private
        } catch(InvocationTargetException err) {
            // Ignored because we are dealing with one exception at a time
            // Afterall, this is the exception handling code
        }
        if(T instanceof SQLException) {
            if(T instanceof SQLWarning) {
                SQLWarning nextSQL=((SQLWarning)T).getNextWarning();
                if(nextSQL!=null) printThrowables(nextSQL, out, indent);
            } else {
                SQLException nextSQL=((SQLException)T).getNextException();
                if(nextSQL!=null) printThrowables(nextSQL, out, indent);
            }
        }
    }
    
    private static void printMessage(PrintStream out, int indent, String label, String message) {
        for(int c=0;c<indent;c++) out.print(' ');
        out.print(label);
        if(message==null) out.println("null");
        else {
            message=message.trim();
            int messageLen=message.length();
            for(int c=0;c<messageLen;c++) {
                char ch=message.charAt(c);
                if(ch=='\n') {
                    int lineIndent=indent+label.length();
                    out.println();
                    for(int d=0;d<lineIndent;d++) out.print(' ');
                } else if(ch!='\r') out.print(ch);
            }
            out.println();
        }
    }

    public static void printStackTraces(Throwable T, PrintWriter out) {
        printStackTraces(T, out, null);
    }

    public static void printStackTraces(Throwable T, PrintWriter out, Object[] extraInfo) {
        synchronized(out) {
            out.println();
            out.println("**************************");
            out.println("* BEGIN EXCEPTION REPORT *");
            out.println("**************************");
            out.println();
            out.println("    Time ");
            out.print("        ");
            out.println(new java.util.Date(System.currentTimeMillis()).toString());

            // Extra info
            if(extraInfo!=null && extraInfo.length>0) {
                out.println("    Extra Information");
                for(int c=0;c<extraInfo.length;c++) {
                    out.print("        ");
                    out.println(extraInfo[c]);
                }
            }

            // Threads
            out.println("    Threading");
            out.println("        Thread");
            out.print("            Name........: ");
            Thread thread=Thread.currentThread();
            out.println(thread.getName());
            out.print("            Class.......: ");
            out.println(thread.getClass().getName());
            out.print("            Priority....: ");
            out.println(thread.getPriority());
            try {
                ThreadGroup TG=thread.getThreadGroup();
                while(TG!=null) {
                    String name=TG.getName();
                    String classname=TG.getClass().getName();
                    int maxPriority=TG.getMaxPriority();
                    out.println("        ThreadGroup");
                    out.print("            Name........: "); out.println(name);
                    out.print("            Class.......: "); out.println(classname);
                    out.print("            Max Priority: "); out.println(maxPriority);
                    TG=TG.getParent();
                }
            } catch(SecurityException err) {
                out.println("Unable to print all Thread Groups: "+err.toString());
            }

            out.println("    Exceptions");
            printThrowables(T, out, 8);

            // End Report
            out.println();
            out.println("**************************");
            out.println("*  END EXCEPTION REPORT  *");
            out.println("**************************");

            // Flush output
            out.flush();
        }
    }

    private static void printThrowables(Throwable T, PrintWriter out, int indent) {
        for(int c=0;c<indent;c++) out.print(' ');
        out.println(T.getClass().getName());
        printMessage(out, indent+4, "Message...........: ", T.getMessage());
        printMessage(out, indent+4, "Localized Message.: ", T.getLocalizedMessage());
        if(T instanceof SQLException) {
            SQLException sql=(SQLException)T;
            if(sql instanceof WrappedSQLException) printMessage(out, indent+4, "SQL Statement.....: ", ((WrappedSQLException)sql).getSqlString());
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.print("SQL Error Code....: ");
            out.println(sql.getErrorCode());
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.print("SQL State.........: ");
            out.println(sql.getSQLState());
        } else if(T instanceof WrappedException) {
            WrappedException wrapped=(WrappedException)T;
            Object[] wrappedInfo=wrapped.getExtraInfo();
            if(wrappedInfo!=null && wrappedInfo.length>0) {
                for(int c=0;c<(indent+4);c++) out.print(' ');
                out.println("Extra Information");
                for(int c=0;c<wrappedInfo.length;c++) {
                    for(int d=0;d<(indent+8);d++) out.print(' ');
                    out.println(wrappedInfo[c]);
                }
            }
        }
        for(int c=0;c<(indent+4);c++) out.print(' ');
        out.println("Stack Trace");
        StackTraceElement[] stack=T.getStackTrace();
        for(int c=0;c<stack.length;c++) {
            for(int d=0;d<(indent+8);d++) out.print(' ');
            out.print("at ");
            out.println(stack[c].toString());
        }
        Throwable cause=T.getCause();
        if(cause!=null) {
            for(int c=0;c<(indent+4);c++) out.print(' ');
            out.println("Caused By");
            printThrowables(cause, out, indent+8);
        }
        // Uses reflection avoid binding to JspException directly.
        try {
            Class<?> clazz=T.getClass();
            if(isSubclass(clazz, "javax.servlet.jsp.JspException")) {
                Method method=clazz.getMethod("getRootCause", new Class[0]);
                Throwable rootCause=(Throwable)method.invoke(T, new Object[0]);
                if(rootCause!=null) {
                    for(int c=0;c<(indent+4);c++) out.print(' ');
                    out.println("Caused By");
                    printThrowables(rootCause, out, indent+8);
                }
            }
        } catch(NoSuchMethodException err) {
            // OK, future versions of JspException might not have getRootCause
        } catch(IllegalAccessException err) {
            // OK, future versions of JspException could make it private
        } catch(InvocationTargetException err) {
            // Ignored because we are dealing with one exception at a time
            // Afterall, this is the exception handling code
        }
        // Uses reflection avoid binding to ServletException directly.
        try {
            Class<?> clazz=T.getClass();
            if(isSubclass(clazz, "javax.servlet.ServletException")) {
                Method method=clazz.getMethod("getRootCause", new Class[0]);
                Throwable rootCause=(Throwable)method.invoke(T, new Object[0]);
                if(rootCause!=null) {
                    for(int c=0;c<(indent+4);c++) out.print(' ');
                    out.println("Caused By");
                    printThrowables(rootCause, out, indent+8);
                }
            }
        } catch(NoSuchMethodException err) {
            // OK, future versions of ServletException might not have getRootCause
        } catch(IllegalAccessException err) {
            // OK, future versions of ServletException could make it private
        } catch(InvocationTargetException err) {
            // Ignored because we are dealing with one exception at a time
            // Afterall, this is the exception handling code
        }
        if(T instanceof SQLException) {
            if(T instanceof SQLWarning) {
                SQLWarning nextSQL=((SQLWarning)T).getNextWarning();
                if(nextSQL!=null) printThrowables(nextSQL, out, indent);
            } else {
                SQLException nextSQL=((SQLException)T).getNextException();
                if(nextSQL!=null) printThrowables(nextSQL, out, indent);
            }
        }
    }

    private static void printMessage(PrintWriter out, int indent, String label, String message) {
        for(int c=0;c<indent;c++) out.print(' ');
        out.print(label);
        if(message==null) out.println("null");
        else {
            message=message.trim();
            int messageLen=message.length();
            for(int c=0;c<messageLen;c++) {
                char ch=message.charAt(c);
                if(ch=='\n') {
                    int lineIndent=indent+label.length();
                    out.println();
                    for(int d=0;d<lineIndent;d++) out.print(' ');
                } else if(ch!='\r') out.print(ch);
            }
            out.println();
        }
    }
    
    private static boolean isSubclass(Class clazz, String classname) {
        while(clazz!=null) {
            if(clazz.getName().equals(classname)) return true;
            clazz=clazz.getSuperclass();
        }
        return false;
    }

    public static String getStackTraces(Throwable T) {
        return getStackTraces(T, null);
    }

    /**
     * Gets the entire exception report as a <code>String</code>.  This is not
     * as efficient as directly writing the report due to the extra buffering.
     */
    public static String getStackTraces(Throwable T, Object[] extraInfo) {
        CharArrayWriter cout = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(cout);
        printStackTraces(T, pw, extraInfo);
        pw.flush();
        pw.close();
        return cout.toString();
    }
}
