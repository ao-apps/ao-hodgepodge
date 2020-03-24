/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2013, 2014, 2016, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.util;

import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.EmptyArrays;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>AOSH</code> is a Bourne-shell-like command interpreter
 * to control the <code>AOServ Client</code> utilities.
 *
 * @author  AO Industries, Inc.
 */
abstract public class ShellInterpreter implements Runnable {

	private static long lastPID=0;

	private final long pid;

	protected final Reader in;
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

	private final List<ShellInterpreter> jobs=new ArrayList<>();

	protected String status="Running";

	public ShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err) {
		this(in, out, err, EmptyArrays.EMPTY_STRING_ARRAY);
	}

	public ShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String ... args) {
		this.pid=getNextPID();
		this.in=in;
		this.out=out;
		this.err=err;

		// Process any command line arguments
		int skipped=0;
		for (String arg : args) {
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
			if(skipped==args.length) {
				this.args = EmptyArrays.EMPTY_STRING_ARRAY;
			} else {
				this.args = new String[args.length-skipped];
				System.arraycopy(args, skipped, this.args, 0, args.length-skipped);
			}
		}
		out.setEnabled(isInteractive);
		err.setEnabled(isInteractive);
	}

	public ShellInterpreter(Reader in, Writer out, Writer err) {
		this(
			in,
			(out instanceof TerminalWriter) ? (TerminalWriter)out : new TerminalWriter(out),
			(err instanceof TerminalWriter) ? (TerminalWriter)err : new TerminalWriter(err)
		);
	}

	public ShellInterpreter(Reader in, Writer out, Writer err, String[] args) {
		this(
			in,
			(out instanceof TerminalWriter) ? (TerminalWriter)out : new TerminalWriter(out),
			(err instanceof TerminalWriter) ? (TerminalWriter)err : new TerminalWriter(err),
			args
		);
	}

	/**
	 * Clears the screen.
	 */
	final public void clear(String[] args) throws IOException {
		out.clearScreen();
	}

	abstract protected String getName();

	private static long getNextPID() {
		synchronized(ShellInterpreter.class) {
			return ++lastPID;
		}
	}

	final public long getPID() {
		return pid;
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
		try {
			// Fork to background task
			if(args.length>0 && "&".equals(args[args.length-1])) {
				String[] newArgs;
				if(args.length==1) {
					newArgs = EmptyArrays.EMPTY_STRING_ARRAY;
				} else {
					newArgs = new String[args.length-1];
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
	}

	/**
	 * Processes one command and returns.
	 */
	private boolean handleCommandImpl(List<String> arguments) throws IOException, SQLException, Throwable {
		String[] myargs=new String[arguments.size()];
		arguments.toArray(myargs);
		return handleCommandImpl(myargs);
	}

	final protected boolean isAlive() {
		Thread t=this.thread;
		return t!=null && t.isAlive();
	}

	final protected boolean isInteractive() {
		return isInteractive;
	}

	final public void jobs(String[] args) {
		// Print all jobs
		synchronized(jobs) {
			for(int c=0;c<jobs.size();c++) {
				ShellInterpreter job=jobs.get(c);
				if(job!=null) printJobLine(c+1, job);
			}
		}
		out.flush();
	}

	protected abstract ShellInterpreter newShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String[] args);

	private void printFinishedJobs() {
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
	}

	private void printJobLine(int jobnum, ShellInterpreter shell) {
		out.print('[');
		String num=String.valueOf(jobnum);
		out.print(num);
		out.print("] ");
		String mystatus=shell.status;
		out.print(mystatus);
		int blanks=Math.max(1, 25-num.length()-mystatus.length());
		for(int c=0;c<blanks;c++) out.print(' ');
		for(int c=0;c<shell.args.length;c++) {
			if(c>0) out.print(' ');
			out.print(shell.args[c]);
		}
		out.println();
	}

	/**
	 * If arguments were provided, executes that command.  Otherwise,
	 * reads from <code>in</code> until end of file or <code>exit</code>.
	 */
	@Override
	final public void run() {
		try {
			if(args.length>0) handleCommand(args);
			else runImpl();
			status="Done";
		} catch(IOException exception) {
			this.err.println(getName()+": "+exception.getMessage());
			status="IO Error: "+exception.getMessage();
			this.err.flush();
		} catch(SQLException exception) {
			this.err.println(getName()+": "+exception.getMessage());
			status="SQL Error: "+exception.getMessage();
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
	}

	/**
	 * If arguments were provided, executes that command.  Otherwise,
	 * reads from <code>in</code> until end of file or <code>exit</code>.
	 */
	private void runImpl() throws IOException, SQLException, Throwable {
		if(args!=null && args.length>0) {
			handleCommandImpl(args);
		} else {
			// The arguments that have been read so far
			List<String> arguments=new ArrayList<>();

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
		ShellInterpreter myparent=this.parent;
		synchronized(jobs) {
			for (ShellInterpreter shell : jobs) {
				shell.parent=myparent;
				if(myparent!=null) myparent.jobs.add(shell); // Should this synchronize myparent.jobs, too?
			}
			jobs.clear();
		}
	}

	private void start() {
		synchronized(ShellInterpreter.class) {
			if(thread!=null) throw new IllegalThreadStateException("Already started");
			(thread=new Thread(this, getClass().getName()+"?pid="+pid+(args.length>0?"&command="+args[0]:""))).start();
		}
	}
}
