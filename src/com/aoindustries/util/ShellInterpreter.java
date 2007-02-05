package com.aoindustries.util;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * <code>AOSH</code> is a Bourne-shell-like command interpreter
 * to control the <code>AOServ Client</code> utilities.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class ShellInterpreter implements Runnable {

    private static long lastPID=0;

    /**
     * Used to indicate to arguments.
     */
    private static final String[] noArgs=new String[0];

    private final long pid;

    protected final InputStream in;
    protected final TerminalWriter out;
    protected final TerminalWriter err;
    private final String[] args;

    private boolean isInteractive=false;

    /**
     * If running as a separate thread, a handle to the thread
     * is kept here.
     */
    private Thread thread;

    private ShellInterpreter parent;

    private List<ShellInterpreter> jobs=new ArrayList<ShellInterpreter>();

    protected String status="Running";

    public ShellInterpreter(InputStream in, TerminalWriter out, TerminalWriter err) {
	this(in, out, err, noArgs);
        Profiler.startProfile(Profiler.INSTANTANEOUS, ShellInterpreter.class, "<init>(InputStream,TerminalWriter,TerminalWriter)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public ShellInterpreter(InputStream in, TerminalWriter out, TerminalWriter err, String[] args) {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "<init>(InputStream,TerminalWriter,TerminalWriter,String[])", null);
        try {
            this.pid=getNextPID();
            this.in=in;
            this.out=out;
            this.err=err;

            // Process any command line arguments
            int skipped=0;
            for(int c=0;c<args.length;c++) {
                String arg=args[c];
                if("-i".equals(arg)) {
                    isInteractive=true;
                    skipped++;
                } else if("--".equals(arg)) {
                    skipped++;
                    break;
                } else break;
            }
            if(skipped==0) {
                this.args=args;
            } else {
                if(skipped==args.length) this.args=noArgs;
                else {
                    this.args=new String[args.length-skipped];
                    System.arraycopy(args, skipped, this.args, 0, args.length-skipped);
                }
            }
            out.setEnabled(isInteractive);
            err.setEnabled(isInteractive);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public ShellInterpreter(InputStream in, OutputStream out, OutputStream err) {
	this(
            in,
            new TerminalWriter(out),
            new TerminalWriter(err)
	);
        Profiler.startProfile(Profiler.INSTANTANEOUS, ShellInterpreter.class, "<init>(InputStream,OutputStream,OutputStream)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public ShellInterpreter(InputStream in, OutputStream out, OutputStream err, String[] args) {
	this(
            in,
            new TerminalWriter(out),
            new TerminalWriter(err),
            args
	);
        Profiler.startProfile(Profiler.INSTANTANEOUS, ShellInterpreter.class, "<init>(InputStream,OutputStream,OutputStream,String[])", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    final public void clear(String[] args) {
        try {
            out.clearScreen();
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    abstract protected String getName();

    private static long getNextPID() {
        Profiler.startProfile(Profiler.FAST, ShellInterpreter.class, "getNextPID()", null);
        try {
	    synchronized(ShellInterpreter.class) {
		return ++lastPID;
	    }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public long getPID() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ShellInterpreter.class, "getPID()", null);
        try {
            return pid;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    abstract protected String getPrompt() throws IOException, SQLException;

    /**
     * Processes one command and returns.
     */
    abstract protected boolean handleCommand(String[] args) throws IOException, SQLException;

    /**
     * Processes one command and returns.
     */
    private boolean handleCommandImpl(String[] args) throws IOException, SQLException, Throwable {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "handleCommandImpl(String[])", null);
        try {
            try {
                // Fork to background task
                if(args.length>0 && "&".equals(args[args.length-1])) {
                    String[] newArgs;
                    if(args.length==1) newArgs=noArgs;
                    else {
                        newArgs=new String[args.length-1];
                        System.arraycopy(args, 0, newArgs, 0, args.length-1);
                    }
                    ShellInterpreter shell=newShellInterpreter(in, out, err, newArgs);
                    shell.parent=this;
                    synchronized(jobs) {
                        jobs.add(shell);
                        if(isInteractive) {
                            out.print('[');
                            out.print(jobs.size());
                            out.print("] ");
                            out.println(shell.pid);
                            out.flush();
                        }
                        shell.start();
                    }
                    return true;
                } else {
                    return handleCommand(args);
                }
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                if(isInteractive) {
                    ErrorPrinter.printStackTraces(T, err);
                    err.flush();
                    return true;
                } else throw T;
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Processes one command and returns.
     */
    private boolean handleCommandImpl(List<String> arguments) throws IOException, SQLException, Throwable {
        Profiler.startProfile(Profiler.FAST, ShellInterpreter.class, "handleCommandImpl(List<String>)", null);
        try {
            String[] args=new String[arguments.size()];
            arguments.toArray(args);
            return handleCommandImpl(args);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final protected boolean isAlive() {
        Profiler.startProfile(Profiler.FAST, ShellInterpreter.class, "isAlive()", null);
        try {
            Thread t=this.thread;
            return t!=null && t.isAlive();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final protected boolean isInteractive() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ShellInterpreter.class, "isInteractive()", null);
        try {
            return isInteractive;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    final public void jobs(String[] args) {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "jobs(String[])", null);
        try {
            // Print all jobs
            synchronized(jobs) {
                for(int c=0;c<jobs.size();c++) {
                    ShellInterpreter job=jobs.get(c);
                    if(job!=null) printJobLine(c+1, job);
                }
            }
            out.flush();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    protected abstract ShellInterpreter newShellInterpreter(InputStream in, TerminalWriter out, TerminalWriter err, String[] args);

    private void printFinishedJobs() {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "printFinishedJobs()", null);
        try {
            synchronized(jobs) {
                boolean changed=false;
                for(int c=0;c<jobs.size();c++) {
                    ShellInterpreter shell=jobs.get(c);
                    if(shell!=null) {
                        if(!shell.isAlive()) {
                            shell.parent=null;
                            if(isInteractive) {
                                printJobLine(c+1, shell);
                                out.flush();
                            }
                            jobs.set(c, null);
                            changed=true;
                        }
                    }
                }
                if(changed) {
                    // Trim off any extra entries
                    while(jobs.size()>0 && jobs.get(jobs.size()-1)==null) jobs.remove(jobs.size()-1);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private void printJobLine(int jobnum, ShellInterpreter shell) {
        Profiler.startProfile(Profiler.IO, ShellInterpreter.class, "printJobLine(int,ShellInterpreter)", null);
        try {
            out.print('[');
            String num=String.valueOf(jobnum);
            out.print(num);
            out.print("] ");
            String status=shell.status;
            out.print(status);
            int blanks=Math.max(1, 25-num.length()-status.length());
            for(int c=0;c<blanks;c++) out.print(' ');
            for(int c=0;c<shell.args.length;c++) {
                if(c>0) out.print(' ');
                out.print(shell.args[c]);
            }
            out.println();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * If arguments were provided, executes that command.  Otherwise,
     * reads from <code>in</code> until end of file or <code>exit</code>.
     */
    final public void run() {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "run()", null);
        try {
            try {
                if(args.length>0) handleCommand(args);
                else runImpl();
                status="Done";
            } catch(IOException err) {
                this.err.println(getName()+": "+err.getMessage());
                status="IO Error: "+err.getMessage();
                this.err.flush();
            } catch(SQLException err) {
                this.err.println(getName()+": "+err.getMessage());
                status="SQL Error: "+err.getMessage();
                this.err.flush();
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                ErrorPrinter.printStackTraces(T, this.err);
                status="Error: "+T.toString();
                this.err.flush();
            } finally {
                if(Thread.currentThread()==thread) thread=null;
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * If arguments were provided, executes that command.  Otherwise,
     * reads from <code>in</code> until end of file or <code>exit</code>.
     */
    private void runImpl() throws IOException, SQLException, Throwable {
        Profiler.startProfile(Profiler.UNKNOWN, ShellInterpreter.class, "runImpl()", null);
        try {
            if(args!=null && args.length>0) {
                handleCommandImpl(args);
            } else {
                // The arguments that have been read so far
                List<String> arguments=new ArrayList<String>();

                // The argument that is being read.
                StringBuilder argument=new StringBuilder();
                boolean hasArgument=false;
                int quoteChar=-1;

                if(isInteractive) {
                    out.print(getPrompt());
                    out.flush();
                }
                // Read until end of file or exit command
                int ch;
                while((ch=in.read())!=-1) {
                    // Skip windows '\r'
                    if(ch!='\r') {
                        if(ch=='\\') {
                            // Process escapes
                            ch=in.read();
                            if(ch==-1) throw new EOFException("unexpected EOF processing escape: \\");
                            if(ch=='\r') {
                                // Skip windows '\r'
                                ch=in.read();
                                if(ch==-1) throw new EOFException("unexpected EOF processing escape: \\");
                            }
                            // Skip '\n' when it is escaped
                            if(ch=='\n') {
                                if(isInteractive) {
                                    out.print("\\> ");
                                    out.flush();
                                }
                            } else {
                                // skip the escape only when followed by ' when using single quotes
                                if(quoteChar=='\'' && ch!='\'') {
                                    argument.append('\\').append((char)ch);
                                    hasArgument=true;
                                } else {
                                    // use the second character for anything left
                                    argument.append((char)ch);
                                    hasArgument=true;
                                }
                            }
                        } else if(quoteChar=='\'') {
                            // Handle reading single quote
                            if(ch=='\'') quoteChar=-1;
                            else {
                                argument.append((char)ch);
                                if(isInteractive && ch=='\n') {
                                    out.print("'> ");
                                    out.flush();
                                }
                            }
                        } else if(quoteChar=='"') {
                            // Handle reading double quote
                            if(ch=='"') quoteChar=-1;
                            else {
                                argument.append((char)ch);
                                if(isInteractive && ch=='\n') {
                                    out.print("\"> ");
                                    out.flush();
                                }
                            }
                        } else {
                            // Read as unquoted input
                            if(ch=='\n') {
                                // End of line means end of command if not in a quote mode
                                if(hasArgument) {
                                    arguments.add(argument.toString());
                                    argument.setLength(0);
                                    hasArgument=false;
                                }
                                if(isInteractive) printFinishedJobs();
                                if(arguments.size()>0) {
                                    boolean doMore=handleCommandImpl(arguments);
                                    arguments.clear();
                                    if(!doMore) break;
                                }
                                if(isInteractive) {
                                    out.print(getPrompt());
                                    out.flush();
                                }
                            } else if(ch<=' ') {
                                // Whitespace separates commands or is skipped
                                if(hasArgument) {
                                    arguments.add(argument.toString());
                                    argument.setLength(0);
                                    hasArgument=false;
                                }
                            } else if(ch=='\'' || ch=='"') {
                                // Beginning quote
                                quoteChar=ch;
                                hasArgument=true;
                            } else {
                                // Everything else is part of the argument are begins the next argument
                                argument.append((char)ch);
                                hasArgument=true;
                            }
                        }
                    }
                }
                // If currently parsing quote, throw error
                if(quoteChar!=-1) throw new EOFException("unexpected EOF when processing quote: "+quoteChar);

                // If commands have been parsed and not executed, run them now
                if(hasArgument) {
                    arguments.add(argument.toString());
                    argument.setLength(0);
                }
                if(arguments.size()>0) {
                    handleCommandImpl(arguments);
                    arguments.clear();
                }
                if(status==null || "Running".equals(status)) status="Done";
            }
            // Make the parent of all children the parent of this, and add children to the parents processes
            ShellInterpreter parent=this.parent;
            synchronized(jobs) {
                for(int c=0;c<jobs.size();c++) {
                    ShellInterpreter shell=jobs.get(c);
                    shell.parent=parent;
                    if(parent!=null) parent.jobs.add(shell);
                }
                jobs.clear();
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private void start() {
        Profiler.startProfile(Profiler.FAST, ShellInterpreter.class, "start()", null);
        try {
	    synchronized(ShellInterpreter.class) {
		if(thread!=null) throw new IllegalThreadStateException("Already started");
		(thread=new Thread(this, getClass().getName()+"?pid="+pid+(args.length>0?"&command="+args[0]:""))).start();
	    }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
