/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2013, 2014, 2016, 2019, 2020, 2021, 2022, 2026  AO Industries, Inc.
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

package com.aoapps.hodgepodge.util;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.EmptyArrays;
import com.aoapps.lang.util.ErrorPrinter;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
public abstract class ShellInterpreter implements Runnable {

  private static long lastPid;

  private final long pid;

  /**
   * Assumes UNIX newlines always.
   */
  private static final char NL = '\n';

  protected final Reader in;
  protected final TerminalWriter out;
  protected final TerminalWriter err;

  /**
   * Reparses each argument, asserting matches the given arg.
   *
   * @return {@link true} if all assertions pass
   *
   * @throws AssertionError  When any assertion fails.
   */
  private static boolean reparseMatches(String[] rawArgs, String... args) throws AssertionError {
    ParseBuffer buffer = new ParseBuffer();
    assert rawArgs.length == args.length;
    for (int i = 0; i < rawArgs.length; i++) {
      String rawArg = rawArgs[i];
      String arg = args[i];
      try {
        ParseResult result = parse(new StringReader(rawArg), null, buffer);
        String[] reRawArgs = result.getRawArgs();
        String[] reArgs = result.getArgs();
        assert reRawArgs.length == 1 : "Must only be a single raw argument after reparse, got " + reRawArgs.length;
        assert reArgs.length == 1 : "Must only be a single argument after reparse, got " + reArgs.length;
        assert reRawArgs[0].equals(rawArg) : "Raw argument mismatch after reparse: expected \"" + rawArg + "\", got \"" + reRawArgs[0] + "\"";
        assert reArgs[0].equals(arg) : "Argument mismatch after reparse: expected \"" + arg + "\", got \"" + reArgs[0] + "\"";
        assert result.isAtEof() : "Not at end-of-file after reparse";
      } catch (IOException e) {
        throw new AssertionError("IOException should never occur from StringReader", e);
      }
    }
    return true;
  }

  /**
   * Performs checks and assertions that rawArgs is consistent with args.
   * Quick checks are always done, slower checks only when assertions are enabled.
   *
   * @param  strict        Perform additional checks for our specific documented rules, that don't apply if someone
   *                       else provides separate rawArgs and args.
   *
   * @param  allowReparse  Set to {@code false} when being called by
   *                       {@link ShellInterpreter#parse(java.io.Reader, java.io.Writer, com.aoapps.hodgepodge.util.ShellInterpreter.ParserCache)}
   *                       to avoid {@link StackOverflowError}.
   */
  @SuppressWarnings("StringEquality") // Intentional for additional checks in strict mode
  private static void checkRawArgsVersusArgs(boolean strict, boolean allowReparse, String[] rawArgs, String... args)
      throws IllegalArgumentException, AssertionError {
    if (rawArgs != null) {
      // When rawArgs is set, must have same number of elements as args
      if (rawArgs.length != args.length) {
        throw new IllegalArgumentException("Mismatched number of elements rawArgs.length=" + rawArgs.length
            + " and args.length=" + args.length);
      }
      if (strict) {
        if (rawArgs.length == 0 && rawArgs != EmptyArrays.EMPTY_STRING_ARRAY) {
          throw new IllegalArgumentException("EMPTY_STRING_ARRAY expected for rawArgs as String[0]");
        }
        if (args.length == 0 && args != EmptyArrays.EMPTY_STRING_ARRAY) {
          throw new IllegalArgumentException("EMPTY_STRING_ARRAY expected for args as String[0]");
        }
      }
      for (int i = 0; i < rawArgs.length; i++) {
        String rawArg = rawArgs[i];
        int rawArgLen = rawArg.length();
        String arg = args[i];
        int argLen = arg.length();
        // rawArgs should never have an empty string
        if (rawArg.isEmpty()) {
          throw new IllegalArgumentException("Element of rawArgs should never be an empty string, index=" + i);
        }
        // args never longer than rawArg
        if (argLen > rawArgLen) {
          throw new IllegalArgumentException(
              "Element of args should never be longer than the corresponding element in rawArgs: index=" + i
                  + ", rawArg=\"" + rawArg + "\", arg=\"" + arg + '"');
        }
      }
      if (rawArgs != args) {
        if (strict) {
          boolean hasDifferentValue = false;
          for (int i = 0; i < rawArgs.length; i++) {
            String rawArg = rawArgs[i];
            String arg = args[i];
            if (rawArg != arg) {
              assert !rawArg.equals(arg) : "Any value that is the same must be the same string identity";
              hasDifferentValue = true;
            }
          }
          if (!hasDifferentValue) {
            throw new IllegalArgumentException("When different arrays, must have at least one different value");
          }
        }
      }
      if (allowReparse) {
        // reparse of each rawArg must become arg
        assert reparseMatches(rawArgs, args);
      }
    }
  }

  /**
   * Performs checks and assertions that rawArgs is consistent with args.
   */
  public static void checkRawArgsVersusArgs(String[] rawArgs, String... args)
      throws IllegalArgumentException, AssertionError {
    checkRawArgsVersusArgs(false, true, rawArgs, args);
  }

  private final String[] rawArgs;

  private final String[] args;

  private boolean isInteractive;

  /**
   * If running as a separate thread, a handle to the thread
   * is kept here.
   */
  private Thread thread;

  private ShellInterpreter parent;

  private final List<ShellInterpreter> jobs = new ArrayList<>();

  protected String status = "Running";

  protected ShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err) {
    this(in, out, err, EmptyArrays.EMPTY_STRING_ARRAY, EmptyArrays.EMPTY_STRING_ARRAY);
  }

  protected ShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String[] rawArgs, String... args) {
    checkRawArgsVersusArgs(rawArgs, args);
    this.pid = getNextPid();
    this.in = in;
    this.out = out;
    this.err = err;

    // Process any command line arguments
    int skipped = 0;
    for (String arg : args) {
      if ("-i".equals(arg)) {
        isInteractive = true;
        skipped++;
      } else if ("--".equals(arg)) {
        skipped++;
        break;
      } else {
        break;
      }
    }
    if (skipped == 0) {
      this.rawArgs = rawArgs;
      this.args = args;
    } else {
      if (skipped == args.length) {
        // Shortcut for when all skipped
        this.rawArgs = (rawArgs == null) ? null : EmptyArrays.EMPTY_STRING_ARRAY;
        this.args = EmptyArrays.EMPTY_STRING_ARRAY;
      } else {
        // Remove the skipped arguments
        if (rawArgs != null) {
          this.rawArgs = new String[rawArgs.length - skipped];
          System.arraycopy(rawArgs, skipped, this.rawArgs, 0, rawArgs.length - skipped);
        } else {
          this.rawArgs = null;
        }
        this.args = new String[args.length - skipped];
        System.arraycopy(args, skipped, this.args, 0, args.length - skipped);
      }
    }
    out.setEnabled(isInteractive);
    err.setEnabled(isInteractive);
  }

  protected ShellInterpreter(Reader in, Writer out, Writer err) {
    this(
        in,
        (out instanceof TerminalWriter) ? (TerminalWriter) out : new TerminalWriter(out),
        (err instanceof TerminalWriter) ? (TerminalWriter) err : new TerminalWriter(err)
    );
  }

  protected ShellInterpreter(Reader in, Writer out, Writer err, String[] rawArgs, String... args) {
    this(
        in,
        (out instanceof TerminalWriter) ? (TerminalWriter) out : new TerminalWriter(out),
        (err instanceof TerminalWriter) ? (TerminalWriter) err : new TerminalWriter(err),
        rawArgs,
        args
    );
  }

  /**
   * Clears the screen.
   */
  public final void clear(String... args) throws IOException {
    out.clearScreen();
  }

  protected abstract String getName();

  private static long getNextPid() {
    synchronized (ShellInterpreter.class) {
      return ++lastPid;
    }
  }

  public final long getPid() {
    return pid;
  }

  /**
   * @deprecated  Please use {@link ShellInterpreter#getPid()} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final long getPID() {
    return getPid();
  }

  protected abstract String getPrompt() throws IOException, SQLException;

  /**
   * Processes one command and returns.
   *
   * @param  rawArgs  The raw args contain the same content as {@link ShellInterpreter#args}, but quoting is left
   *                  intact.  Will be {@code null} when the arguments were split outside, such as being from the
   *                  arguments passed to the Java application main method.
   *
   * @param  args  The args with all shell quoting decoded.
   */
  protected abstract boolean handleCommand(String[] rawArgs, String... args) throws IOException, SQLException;

  /**
   * Processes one command and returns.
   */
  @SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
  private boolean handleCommandImpl(String[] rawArgs, String... args) throws IOException, SQLException, Throwable {
    checkRawArgsVersusArgs(true, true, rawArgs, args);
    try {
      // Fork to background task
      if (args.length > 0 && "&".equals(args[args.length - 1])) {
        String[] newRawArgs;
        String[] newArgs;
        if (args.length == 1) {
          newRawArgs = (rawArgs == null) ? null : EmptyArrays.EMPTY_STRING_ARRAY;
          newArgs = EmptyArrays.EMPTY_STRING_ARRAY;
        } else {
          if (rawArgs != null) {
            newRawArgs = new String[rawArgs.length - 1];
            System.arraycopy(rawArgs, 0, newRawArgs, 0, rawArgs.length - 1);
          } else {
            newRawArgs = null;
          }
          newArgs = new String[args.length - 1];
          System.arraycopy(args, 0, newArgs, 0, args.length - 1);
        }
        ShellInterpreter shell = newShellInterpreter(in, out, err, newRawArgs, newArgs);
        shell.parent = this;
        synchronized (jobs) {
          jobs.add(shell);
          if (isInteractive) {
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
        return handleCommand(rawArgs, args);
      }
    } catch (ThreadDeath td) {
      throw td;
    } catch (Throwable t) {
      if (isInteractive) {
        ErrorPrinter.printStackTraces(t, err);
        err.flush();
        return true;
      } else {
        throw t;
      }
    }
  }

  protected final boolean isAlive() {
    Thread t = this.thread;
    return t != null && t.isAlive();
  }

  protected final boolean isInteractive() {
    return isInteractive;
  }

  public final void jobs(String... args) {
    // Print all jobs
    synchronized (jobs) {
      for (int c = 0; c < jobs.size(); c++) {
        ShellInterpreter job = jobs.get(c);
        if (job != null) {
          printJobLine(c + 1, job);
        }
      }
    }
    out.flush();
  }

  protected abstract ShellInterpreter newShellInterpreter(Reader in, TerminalWriter out, TerminalWriter err, String[] rawArgs, String... args);

  private void printFinishedJobs() {
    synchronized (jobs) {
      boolean changed = false;
      for (int c = 0; c < jobs.size(); c++) {
        ShellInterpreter shell = jobs.get(c);
        if (shell != null) {
          if (!shell.isAlive()) {
            shell.parent = null;
            if (isInteractive) {
              printJobLine(c + 1, shell);
              out.flush();
            }
            jobs.set(c, null);
            changed = true;
          }
        }
      }
      if (changed) {
        // Trim off any extra entries
        while (!jobs.isEmpty() && jobs.get(jobs.size() - 1) == null) {
          jobs.remove(jobs.size() - 1);
        }
      }
    }
  }

  private void printJobLine(int jobnum, ShellInterpreter shell) {
    out.print('[');
    String num = String.valueOf(jobnum);
    out.print(num);
    out.print("] ");
    String mystatus = shell.status;
    out.print(mystatus);
    int blanks = Math.max(1, 25 - num.length() - mystatus.length());
    for (int c = 0; c < blanks; c++) {
      out.print(' ');
    }
    for (int c = 0; c < shell.args.length; c++) {
      if (c > 0) {
        out.print(' ');
      }
      out.print(shell.args[c]);
    }
    out.println();
  }

  /**
   * If arguments were provided, executes that command.  Otherwise,
   * reads from <code>in</code> until end of file or <code>exit</code>.
   */
  @Override
  public final void run() {
    try {
      if (args.length > 0) {
        handleCommand(rawArgs, args);
      } else {
        runImpl();
      }
      status = "Done";
    } catch (IOException exception) {
      this.err.println(getName() + ": " + exception.getMessage());
      status = "IO Error: " + exception.getMessage();
      this.err.flush();
    } catch (SQLException exception) {
      this.err.println(getName() + ": " + exception.getMessage());
      status = "SQL Error: " + exception.getMessage();
      this.err.flush();
    } catch (ThreadDeath td) {
      throw td;
    } catch (Throwable t) {
      ErrorPrinter.printStackTraces(t, this.err);
      status = "Error: " + t.toString();
      this.err.flush();
    } finally {
      if (Thread.currentThread() == thread) {
        thread = null;
      }
    }
  }

  /**
   * A optional parser buffer to aid performance of repeated parsing.
   * This class is not thread safe and is expected to be used within a non-concurrent call to
   * {@link ShellInterpreter#parse(java.io.Reader, java.io.Writer, com.aoapps.hodgepodge.util.ShellInterpreter.ParserCache)}.
   */
  public static class ParseBuffer {

    /**
     * The arguments that have been read so far.
     */
    private final List<String> rawArguments = new ArrayList<>();

    /**
     * Elements are added only when first deviate from {@link ParseBuffer#rawArguments}.
     */
    private final List<String> arguments = new ArrayList<>();

    /**
     * The raw form of the argument that is being read.
     */
    private final StringBuilder rawArgument = new StringBuilder();

    /**
     * The decoded form of the argument that is being read.
     */
    private final StringBuilder argument = new StringBuilder();

    private void init() {
      rawArguments.clear();
      arguments.clear();
      rawArgument.setLength(0);
      argument.setLength(0);
    }

    /**
     * Adds an argument from the current buffers to the lists of arguments.
     * The buffers may be empty, in which case the argument will be an empty String.
     * Clears the buffers.
     */
    private void addArgument() {
      if (rawArgument.length() == 0) {
        throw new AssertionError("An argument can never be empty in raw form");
      }
      String rawArg = rawArgument.toString();
      rawArgument.setLength(0);
      if (argument.length() > rawArg.length()) {
        throw new AssertionError("Decoded length should always be equal to or less than raw length");
      }
      if (argument.length() == rawArg.length()) {
        // rawArg and arg match, only add if arguments if already non-empty
        assert argument.toString().equals(rawArg);
        if (!arguments.isEmpty()) {
          arguments.add(rawArg);
        }
      } else {
        // rawArg and arg are different, populate arguments if empty
        String arg = argument.toString();
        if (arguments.isEmpty()) {
          arguments.addAll(rawArguments);
        }
        arguments.add(arg);
      }
      argument.setLength(0);
      // Add to rawArguments last, since may have been used to populate arguments when first argument differs
      rawArguments.add(rawArg);
    }

    /**
     * Gets the result for the current state and clears buffers.
     * The individual argument buffers must be empty already.
     */
    private ParseResult getResult(boolean atEof) {
      if (rawArgument.length() != 0) {
        throw new IllegalStateException("rawArgument must be finished before trying to get result");
      }
      if (argument.length() != 0) {
        throw new IllegalStateException("argument must be finished before trying to get result");
      }
      if (rawArguments.isEmpty()) {
        // No arguments
        if (!arguments.isEmpty()) {
          throw new AssertionError();
        }
        return new ParseResult(
            EmptyArrays.EMPTY_STRING_ARRAY,
            EmptyArrays.EMPTY_STRING_ARRAY,
            atEof
        );
      } else {
        String[] rawArgs = rawArguments.toArray(String[]::new);
        rawArguments.clear();
        if (arguments.isEmpty()) {
          // All elements identical
          return new ParseResult(rawArgs, rawArgs, atEof);
        } else {
          // At least one argument is different in raw form
          String[] args = arguments.toArray(String[]::new);
          arguments.clear();
          assert args.length == rawArgs.length;
          return new ParseResult(rawArgs, args, atEof);
        }
      }
    }
  }

  /**
   * The result of a parsing pass.
   */
  public static class ParseResult {

    private final String[] rawArgs;

    private final String[] args;

    private final boolean atEof;

    private ParseResult(String[] rawArgs, String[] args, boolean atEof) {
      checkRawArgsVersusArgs(true, false, rawArgs, args);
      this.rawArgs = rawArgs;
      this.args = args;
      this.atEof = atEof;
    }

    /**
     * The raw arguments, with all original characters unmodified.
     * None of the elements can be an empty string, even if it represents an empty argument once decoded.
     *
     * @return  The array without any defensive copying.
     *
     *          <p>When an empty array, returns {@link EmptyArrays#EMPTY_STRING_ARRAY}.</p>
     *
     *          <p>When an argument is identical in raw and decoded forms, the element in both
     *          {@code rawArgs} and {@code args} will be the same {@link String} instance.</p>
     *
     *          <p>When all arguments are identical in both {@code rawArgs} and {@code args},
     *          the same {@code String[]} instance is returned.</p>
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField") // @return notes no defensive copy
    public String[] getRawArgs() {
      return rawArgs;
    }

    /**
     * The resulting arguments, with backslash escapes decoded.
     * Some of the elements can be an empty string representing an empty argument.
     *
     * @return  The array without any defensive copying.
     *
     *          <p>When an empty array, returns {@link EmptyArrays#EMPTY_STRING_ARRAY}.</p>
     *
     *          <p>When an argument is identical in raw and decoded forms, the element in both
     *          {@code rawArgs} and {@code args} will be the same {@link String} instance.</p>
     *
     *          <p>When all arguments are identical in both {@code rawArgs} and {@code args},
     *          the same {@code String[]} instance is returned.</p>
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField") // @return notes no defensive copy
    public String[] getArgs() {
      return args;
    }

    /**
     * Did the parser reach end of file on the input?  The parser stops at either end-of-file or the first
     * {@link ShellInterpreter#NL} that is not escaped or quoted.
     */
    public boolean isAtEof() {
      return atEof;
    }
  }

  /**
   * Parses a stream of characters into arguments.  Reads up to the first {@link ShellInterpreter#NL newline}
   * that is not backslash-escaped, or until end of file.
   *
   * @param  in         the reader to parse
   * @param  promptOut  the optional output to write prompts
   * @param  buffer     the optional buffer, may be {@code null}
   */
  // TODO: Support more JavaScript-like escapes?  If so, would it be in double-quotes only, or all except single quotes?
  public static ParseResult parse(Reader in, Writer promptOut, ParseBuffer buffer) throws IOException {
    if (buffer == null) {
      buffer = new ParseBuffer();
    } else {
      buffer.init();
    }
    boolean inArgument = false;
    int quoteChar = -1;
    // Read until end of file or exit command
    int chOrEof;
    while ((chOrEof = in.read()) != -1) {
      char ch = (char) chOrEof;
      // Skip windows '\r'
      if (ch == '\r') {
        continue;
      }
      if (ch == '\\') {
        // Process escapes
        do {
          chOrEof = in.read();
          if (chOrEof == -1) {
            throw new EOFException("unexpected EOF processing escape: \\");
          }
          ch = (char) chOrEof;
        } while (
            // Skip windows '\r'
            ch == '\r'
        );
        // Skip '\n' when it is escaped
        if (ch == NL) {
          if (promptOut != null) {
            promptOut.write("\\> ");
            promptOut.flush();
          }
        } else {
          if (!inArgument) {
            // Starting a new argument
            assert buffer.rawArgument.length() == 0;
            assert buffer.argument.length() == 0;
            inArgument = true;
          }
          buffer.rawArgument.append('\\').append(ch);
          // skip the escape only when followed by ' when using single quotes
          if (quoteChar == '\'' && ch != '\'') {
            buffer.argument.append('\\');
          }
          // use the second character for anything left
          buffer.argument.append(ch);
        }
      } else if (quoteChar != -1) {
        // Handle within quoted text
        assert quoteChar == '\'' || quoteChar == '"';
        assert inArgument;
        assert buffer.rawArgument.length() > 0;
        buffer.rawArgument.append(ch);
        if (ch == quoteChar) {
          // End quotes
          quoteChar = -1;
        } else {
          buffer.argument.append(ch);
          if (promptOut != null && ch == NL) {
            promptOut.write(quoteChar);
            promptOut.write("> ");
            promptOut.flush();
          }
        }
      } else {
        // Read as unquoted input
        if (ch == NL) {
          // End of line means end of command if not in a quote mode
          if (inArgument) {
            buffer.addArgument();
          }
          return buffer.getResult(false);
        } else if (ch <= ' ') {
          // Whitespace separates commands or is skipped
          if (inArgument) {
            buffer.addArgument();
            inArgument = false;
          }
        } else if (ch == '\'' || ch == '"') {
          // Beginning quote
          assert quoteChar == -1;
          assert !inArgument;
          assert buffer.rawArgument.length() == 0;
          assert buffer.argument.length() == 0;
          quoteChar = ch;
          inArgument = true;
          buffer.rawArgument.append(ch);
        } else {
          // Everything else is part of the argument or begins the next argument
          if (!inArgument) {
            // Starting a new argument
            assert buffer.rawArgument.length() == 0;
            assert buffer.argument.length() == 0;
            inArgument = true;
          }
          buffer.rawArgument.append(ch);
          buffer.argument.append(ch);
        }
      }
    }
    // If currently parsing quote, throw error
    if (quoteChar != -1) {
      throw new EOFException("unexpected EOF when processing quote: " + quoteChar);
    }

    // If commands have been parsed and not executed, run them now
    if (inArgument) {
      buffer.addArgument();
    }
    return buffer.getResult(true);
  }

  /**
   * Parses a stream of characters into arguments.  Reads up to the first {@link ShellInterpreter#NL newline}
   * that is not backslash-escaped, or until end of file.
   *
   * @param  in         the reader to parse
   * @param  promptOut  the optional output to write prompts
   */
  public static ParseResult parse(Reader in, Writer promptOut) throws IOException {
    return parse(in, promptOut, null);
  }

  /**
   * If arguments were provided, executes that command.  Otherwise,
   * reads from <code>in</code> until end of file or <code>exit</code>.
   */
  private void runImpl() throws IOException, SQLException, Throwable {
    if (args != null && args.length > 0) { // TODO: Why checked both here and in run()?
      handleCommandImpl(rawArgs, args);
    } else {
      ParseBuffer buffer = new ParseBuffer();
      ParseResult result;
      do {
        if (isInteractive) {
          out.print(getPrompt());
          out.flush();
        }
        result = parse(in, isInteractive ? out : null, buffer);
        if (!result.atEof && isInteractive) {
          printFinishedJobs();
        }
        if (result.args.length > 0) {
          boolean doMore = handleCommandImpl(result.rawArgs, result.args);
          if (!doMore) {
            break;
          }
        }
        if (!result.atEof && isInteractive) {
          out.print(getPrompt());
          out.flush();
        }
      } while (!result.atEof);
      if (status == null || "Running".equals(status)) {
        status = "Done";
      }
    }
    // Make the parent of all children the parent of this, and add children to the parents processes
    ShellInterpreter myparent = this.parent;
    synchronized (jobs) {
      for (ShellInterpreter shell : jobs) {
        shell.parent = myparent;
        if (myparent != null) {
          myparent.jobs.add(shell); // Should this synchronize myparent.jobs, too?
        }
      }
      jobs.clear();
    }
  }

  private void start() {
    synchronized (ShellInterpreter.class) {
      if (thread != null) {
        throw new IllegalThreadStateException("Already started");
      }
      (thread = new Thread(this, getClass().getName() + "?pid=" + pid + (args.length > 0 ? "&command=" + args[0] : ""))).start();
    }
  }
}
