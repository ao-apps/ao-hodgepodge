package com.aoindustries.io;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.ErrorHandler;
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.StandardErrorHandler;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Reusable generic connection pooling with dynamic flaming tiger feature.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOPool extends Thread {

    public static final int DEFAULT_DELAY_TIME = 1 * 60 * 1000;
    public static final int DEFAULT_MAX_IDLE_TIME = 10 * 60 * 1000;

    public static final long UNLIMITED_MAX_CONNECTION_AGE=-1;
    public static final long DEFAULT_MAX_CONNECTION_AGE= 30L * 60 * 1000;

    public static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final int DEFAULT_SOCKET_SO_LINGER = 15;

    final private int delayTime;
    final private int maxIdleTime;
    private long startTime;
    private int numConnections;
    private long maxConnectionAge;

    /**
     * Instances of <code>Object</code> to reuse i.e. the <code>Object</code>
     * pool.
     *
     * @see  #getConnectionObject
     */
    private final Object[] connections;

    /**
     * The time each connection was created
     */
    private final long[] createTimes;

    /**
     * Flags used to keep track of which connections are busy
     */
    private final boolean[] busyConnections;

    /**
     * Total time using each connection
     */
    private final long[] totalTimes;

    /**
     * The time getting each DB connection from the pool
     */
    private final long[] startTimes;

    /**
     * The time returning each DB connection to the pool
     */
    private final long[] releaseTimes;

    /**
     * Counts the number of times each connection is connected
     */
    private final long[] connectCount;

    /**
     * Counts the number of times each connection is used
     */
    private final long[] connectionUses;

    /**
     * Keeps track of every thread that has a connection allocated.  A warning with
     * stack trace is provided when a thread allocates more than one connection.
     */
    private final Thread[] threads;

    /**
     * Keeps track of the stack trace at checkout for each connection.
     */
    private final Throwable[] allocateStackTraces;

    /**
     * All warnings are sent here if available, otherwise will be written to <code>System.err</code>.
     */
    protected final ErrorHandler errorHandler;

    /** Lock for wait/notify */
    public Object connectionLock = new Object();

    /**
     * The RefreshConnection thread polls every connection in the connection pool. If it
     * detects a connection is idle for more than the pre-defined MAX_IDLE_TIME, it closes 
     * the connection.
     */
    private boolean runMore = true;

    private int maxConcurrency=0;

    /**
     * @deprecated  Please call AOPool(String,int,long,ErrorHandler)
     *
     * @see #AOPool(String,int,long,ErrorHandler)
     */
    protected AOPool(String name, int numConnections) {
        this(DEFAULT_DELAY_TIME, DEFAULT_MAX_IDLE_TIME, name, numConnections, DEFAULT_MAX_CONNECTION_AGE, new StandardErrorHandler());
    }

    /**
     * @deprecated  Please call AOPool(String,int,long,ErrorHandler)
     *
     * @see #AOPool(String,int,long,ErrorHandler)
     */
    protected AOPool(String name, int numConnections, long maxConnectionAge) {
        this(DEFAULT_DELAY_TIME, DEFAULT_MAX_IDLE_TIME, name, numConnections, maxConnectionAge, new StandardErrorHandler());
    }

    protected AOPool(String name, int numConnections, long maxConnectionAge, ErrorHandler errorHandler) {
        this(DEFAULT_DELAY_TIME, DEFAULT_MAX_IDLE_TIME, name, numConnections, maxConnectionAge, errorHandler);
    }

    /**
     * @deprecated  Please call AOPool(int,int,String,int,long,ErrorHandler)
     *
     * @see #AOPool(int,int,String,int,long,ErrorHandler)
     */
    protected AOPool(int delayTime, int maxIdleTime, String name, int numConnections) {
        this(delayTime, maxIdleTime, name, numConnections, DEFAULT_MAX_CONNECTION_AGE, new StandardErrorHandler());
    }

    /**
     * @deprecated  Please call AOPool(int,int,String,int,long,ErrorHandler)
     *
     * @see #AOPool(int,int,String,int,long,ErrorHandler)
     */
    protected AOPool(int delayTime, int maxIdleTime, String name, int numConnections, long maxConnectionAge) {
        this(delayTime, maxIdleTime, name, numConnections, maxConnectionAge, new StandardErrorHandler());
    }

    protected AOPool(int delayTime, int maxIdleTime, String name, int numConnections, long maxConnectionAge, ErrorHandler errorHandler) {
	super(name+"&delayTime="+delayTime+"&maxIdleTime="+maxIdleTime+"&size="+numConnections+"&maxConnectionAge="+(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":Long.toString(maxConnectionAge)));
        this.delayTime=delayTime;
        this.maxIdleTime=maxIdleTime;
        this.startTime=System.currentTimeMillis();
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(true);
        this.numConnections = numConnections;
        this.maxConnectionAge=maxConnectionAge;
        if(errorHandler==null) throw new AssertionError("errorHandler is null");
        this.errorHandler=errorHandler;
        connections = new Object[numConnections];
        createTimes = new long[numConnections];
        busyConnections = new boolean[numConnections];
        totalTimes = new long[numConnections];
        startTimes = new long[numConnections];
        releaseTimes = new long[numConnections];
        connectCount = new long[numConnections];
        connectionUses = new long[numConnections];
        threads = new Thread[numConnections];
        allocateStackTraces = new Throwable[numConnections];
        start();
    }

    protected abstract void close(Object O) throws Exception;

    final protected void closeImp() throws Exception {
        runMore = false;
        for (int c = 0; c < numConnections; c++) {
            Object conn = connections[c];
            if (conn != null) {
                connections[c] = null;
                close(conn);
            }
        }
    }

    final public int getConcurrency() {
        int total=0;
        for(int c=0;c<numConnections;c++) if(busyConnections[c]) total++;
        return total;
    }

    final public int getConnectionCount() {
        int total=0;
        for(int c=0;c<numConnections;c++) if(connections[c]!=null) total++;
        return total;
    }

    /**
     * Gets a connection to the database.  Multiple <code>Connection</code>s to the database
     * may exist at any moment. It checks the <code>Connection</code> pool for a not busy
     * <code>Connection</code> sequentially. If found and the <code>Connection</code> is
     * a valid one, it returns that <code>Connection</code> object, otherwise creates a new
     * <code>connection</code>, adds it to the pool and also returns the <code>Connection</code>
     * object. If all the connections in the pool are busy, it waits till a connection becomes
     * available.
     *
     * @return     a <code>Connection</code> object
     * @exception  SQLException if unable to create the <code>Connection</code>
     * @exception  IOException if unable to access the <code>aoserv</code> file using
     *             <code>AOServConfiguration</code>
     */
    final protected Object getConnectionImp(int maxConnections) throws Exception {
        Thread thisThread=Thread.currentThread();
        synchronized (connectionLock) {
            while (true) {
                // Warn if this thread already has a conneciton
                Throwable allocateStackTrace=null;
                int useCount=0;
                for (int c = 0; c < numConnections; c++) {
                    if(threads[c]==thisThread) {
                        useCount++;
                        if(allocateStackTrace==null) allocateStackTrace=allocateStackTraces[c];
                    }
                }
                if(useCount>=maxConnections) {
                    if(useCount>=(numConnections/2)) throwException("Thread attempting to allocate more than half of the connection pool: "+thisThread.toString(), allocateStackTrace);
                    errorHandler.reportWarning(
                        new Throwable("Warning: Thread allocated more than one connection", allocateStackTrace),
                        new Object[] {
                            "useCount="+useCount,
                            "maxConnections="+maxConnections
                        }
                    );
                }
                for (int c = 0; c < numConnections; c++) {
                    if (!busyConnections[c]) {
                        long currentTime=System.currentTimeMillis();
                        startTimes[c] = currentTime;
                        Object connection = connections[c];
                        boolean doReset;
                        if (connection == null || isClosed(connection)) {
                            connection=connections[c]=getConnectionObject();
                            createTimes[c]=currentTime;
                            connectCount[c]++;
                            doReset=true;
                        } else doReset=false;
                        busyConnections[c] = true;
                        releaseTimes[c] = 0;
                        connectionUses[c]++;
                        threads[c]=thisThread;
                        allocateStackTraces[c]=new Throwable("StackTrace at getConnectionImp(" + maxConnections + ") for Thread named \"" + thisThread.getName() + "\"");

                        // Keep track of the maximum concurrency hit
                        if(maxConcurrency<numConnections) {
                            int concurrency=getConcurrency();
                            if(concurrency>maxConcurrency) maxConcurrency=concurrency;
                        }

                        if(doReset) resetConnection(connection);
                        return connection;
                    }
                }
                try {
                    connectionLock.wait();
                } catch (InterruptedException err) {
                    errorHandler.reportWarning(err, null);
                }
            }
        }
    }

    protected abstract Object getConnectionObject() throws Exception;

    final public long getConnects() {
        long total=0;
        for(int c=0;c<numConnections;c++) total+=connectCount[c];
        return total;
    }

    /**
     * Gets the maximum age for connections.
     */
    public long getMaxConnectionAge() {
        return maxConnectionAge;
    }

    final public int getMaxConcurrency() {
        return maxConcurrency;
    }

    final public int getPoolSize() {
        return numConnections;
    }

    final public long getTotalTime() {
        long total=0;
        for(int c=0;c<numConnections;c++) total+=totalTimes[c];
        return total;
    }

    final public long getTransactionCount() {
        long total=0;
        for(int c=0;c<numConnections;c++) total+=connectionUses[c];
        return total;
    }

    protected abstract boolean isClosed(Object O) throws Exception;

    protected abstract void printConnectionStats(ChainWriter out) throws IOException;

    /**
     * Prints complete statistics about connection pool use.
     */
    final protected void printStatisticsHTMLImp(ChainWriter out) throws Exception {
        out.print("<TABLE cellspacing=0 cellpadding=2 border=1>\n");
        printConnectionStats(out);
        out.print("  <TR><TD>Max Connection Pool Size:</TD><TD>").print(numConnections).print("</TD></TR>\n"
                + "  <TR><TD>Max Connection Age:</TD><TD>").print(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":StringUtility.getDecimalTimeLengthString(maxConnectionAge)).print("</TD></TR>\n"
                + "</TABLE>\n"
                + "<BR><BR>\n"
                + "<TABLE cellspacing=0 cellpadding=2 border=1>\n"
                + "  <TR><TH colspan=11><FONT size=+1>Connections</FONT></TH></TR>\n"
                + "  <TR>\n"
                + "    <TH>Connection #</TH>\n"
                + "    <TH>Is Connected</TH>\n"
                + "    <TH>Conn Age</TH>\n"
                + "    <TH>Conn Count</TH>\n"
                + "    <TH>Use Count</TH>\n"
                + "    <TH>Total Time</TH>\n"
                + "    <TH>% of Time</TH>\n"
                + "    <TH>State</TH>\n"
                + "    <TH>State Time</TH>\n"
                + "    <TH>Ave Trans Time</TH>\n"
                + "    <TH>Stack Trace</TH>\n"
                + "  </TR>\n");
        synchronized(connectionLock) {
            long time=System.currentTimeMillis();
            long timeLen=time-startTime;

            int totalConnected=0;
            long totalConnects=0;
            long totalUses=0;
            long totalTotalTime=0;
            int totalBusy=0;

            for(int c=0;c<numConnections;c++) {
                long connCount=connectCount[c];
                if(connCount>0) {
                    boolean isConnected=connections[c]!=null && !isClosed(connections[c]);
                    long useCount=connectionUses[c];
                    long totalTime=totalTimes[c];
                    boolean isBusy=busyConnections[c];
                    if(isBusy) totalTime+=time-startTimes[c];
                    long stateTime=isBusy?(time-startTimes[c]):(time-releaseTimes[c]);
                    out.print("  <TR>\n"
                            + "    <TD>").print(c+1).print("</TD>\n"
                            + "    <TD>").print(isConnected?"Yes":"No").print("</TD>\n"
                            + "    <TD>");
                    if(isConnected) out.print(StringUtility.getDecimalTimeLengthString(System.currentTimeMillis()-createTimes[c]));
                    else out.print("&nbsp;");
                    out.print("    <TD>").print(connCount).print("</TD>\n"
                            + "    <TD>").print(useCount).print("</TD>\n"
                            + "    <TD>").print(StringUtility.getDecimalTimeLengthString(totalTime)).print("</TD>\n"
                            + "    <TD>").print(totalTime*100/(float)timeLen).print("%</TD>\n"
                            + "    <TD>").print(isBusy?"In Use":isConnected?"Idle":"Closed").print("</TD>\n"
                            + "    <TD>").print(StringUtility.getDecimalTimeLengthString(stateTime)).print("</TD>\n"
                            + "    <TD>").print((totalTime*1000/useCount)).print("&micro;s</TD>\n"
                            + "    <TD>");
                    Throwable T = allocateStackTraces[c];
                    if(T == null) out.print("&nbsp;");
                    else {
                        out.print("      <A href='#' onClick='var elem = document.getElementById(\"stack_").print(c).print("\").style; elem.visibility=(elem.visibility==\"visible\" ? \"hidden\" : \"visible\"); return false;'>Stack Trace</A>\n"
                                + "      <SPAN width='100%' id='stack_").print(c).print("' style='align:left; white-space:nowrap; position:absolute; visibility: hidden; z-index:").print(c+1).print("'>\n"
                                + "        <PRE style='align:left; background-color:white; border: 2px solid; border-color: black;'>\n");
                        ErrorPrinter.printStackTraces(T, out.getPrintWriter());
                        out.print("        </PRE>\n"
                                + "      </SPAN>\n");
                    }
                    out.print("</TD>\n"
                            + "  </TR>\n");

                    // Update totals
                    if(isConnected) totalConnected++;
                    totalConnects+=connCount;
                    totalUses+=useCount;
                    totalTotalTime+=totalTime;
                    if(isBusy) totalBusy++;
                }
            }
            out.print("  <TR>\n"
                    + "    <TD><B>Total</B></TD>\n"
                    + "    <TD>").print(totalConnected).print("</TD>\n"
                    + "    <TD>&nbsp;</TD>\n"
                    + "    <TD>").print(totalConnects).print("</TD>\n"
                    + "    <TD>").print(totalUses).print("</TD>\n"
                    + "    <TD>").print(StringUtility.getDecimalTimeLengthString(totalTotalTime)).print("</TD>\n"
                    + "    <TD>").print(timeLen==0 ? 0 : (totalTotalTime*100/(float)timeLen)).print("%</TD>\n"
                    + "    <TD>").print(totalBusy).print("</TD>\n"
                    + "    <TD>").print(StringUtility.getDecimalTimeLengthString(timeLen)).print("</TD>\n"
                    + "    <TD>").print(totalUses==0 ? 0 : (totalTotalTime*1000/totalUses)).print("&micro;s</TD>\n"
                    + "    <TD>&nbsp;</TD>\n"
                    + "  </TD>\n");
        }
        out.print("</TABLE>\n");
    }

    /**
     * Releases the database <code>Connection</code> to the <code>Connection</code> pool.
     * @param connection java.sql.Connection
     */
    final protected void releaseConnectionImp(Object connection) throws Exception {
        // Reset connections as they are released
        if(!isClosed(connection)) resetConnection(connection);

        // Find the connection index and determine if it should be closed now
        int index=-1;
        boolean closeConnection=false;
        synchronized(connectionLock) {
            for (int c = 0; c < numConnections; c++) {
                if (connection == connections[c]) {
                    index=c;
                    if(maxConnectionAge!=UNLIMITED_MAX_CONNECTION_AGE) {
                        long age=System.currentTimeMillis()-createTimes[c];
                        if(age<0 || age>=maxConnectionAge) closeConnection=true;
                    }
                    break;
                }
            }
        }

        // Close the connection if needed
        if(closeConnection) close(connection);

        // Unallocate the connection from the pool
        if(index!=-1) {
            synchronized (connectionLock) {
                long currentTime=System.currentTimeMillis();
                busyConnections[index] = false;
                releaseTimes[index] = currentTime;
                totalTimes[index] += currentTime - startTimes[index];
                threads[index] = null;
                allocateStackTraces[index] = null;
                connectionLock.notify();
            }
        }
    }

    protected abstract void resetConnection(Object O) throws Exception;

    final public void run() {
        while (runMore) {
            try {
                while (true) {
                    sleep(delayTime);
                    long time = System.currentTimeMillis();
                    synchronized (connectionLock) {
                        Object[] connections = this.connections;
                        int size = connections.length;
                        boolean[] busyConnection = busyConnections;
                        long[] releaseTime = releaseTimes;
                        int maxIdle = maxIdleTime;
                        synchronized (this) {
                            for (int c = 0; c < size; c++) {
                                if(
                                    connections[c]!=null
                                    && !busyConnection[c]
                                    && (
                                        (time-releaseTime[c]) > maxIdle
                                        || (
                                            maxConnectionAge!=UNLIMITED_MAX_CONNECTION_AGE
                                            && (
                                                createTimes[c] > time
                                                || (time-createTimes[c]) >= maxConnectionAge
                                            )
                                        )
                                    )
                                ) {
                                    if(!isClosed(connections[c])) close(connections[c]);
                                    connections[c] = null;
                                    releaseTimes[c] = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                }
            } catch(SQLException err) {
                errorHandler.reportError(err, null);
            } catch (ThreadDeath TD) {
                throw TD;
            } catch (Throwable T) {
                errorHandler.reportError(T, null);
            }
            try {
                sleep(delayTime);
            } catch (InterruptedException err) {
                errorHandler.reportWarning(err, null);
            }
        }
    }

    protected abstract void throwException(String message, Throwable allocateStackTrace) throws Exception;
    
    final public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}