package com.aoindustries.io;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.EncodingUtils;
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable generic connection pooling with dynamic flaming tiger feature.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOPool<C,E extends Exception> extends Thread {

    public static final int DEFAULT_DELAY_TIME = 1 * 60 * 1000;
    public static final int DEFAULT_MAX_IDLE_TIME = 10 * 60 * 1000;

    public static final long UNLIMITED_MAX_CONNECTION_AGE=-1;
    public static final long DEFAULT_MAX_CONNECTION_AGE= 30L * 60 * 1000;

    public static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final int DEFAULT_SOCKET_SO_LINGER = 15;

    final private Class<C> connectionClass;
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
    private final C[] connections;

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
    protected final Logger logger;

    /** Lock for wait/notify */
    private final Object connectionLock = new Object();

    /**
     * The RefreshConnection thread polls every connection in the connection pool. If it
     * detects a connection is idle for more than the pre-defined MAX_IDLE_TIME, it closes 
     * the connection.
     */
    private boolean runMore = true;

    private int maxConcurrency=0;

    protected AOPool(Class<C> connectionClass, String name, int numConnections, long maxConnectionAge, Logger logger) {
        this(connectionClass, DEFAULT_DELAY_TIME, DEFAULT_MAX_IDLE_TIME, name, numConnections, maxConnectionAge, logger);
    }

    @SuppressWarnings("unchecked")
    protected AOPool(Class<C> connectionClass, int delayTime, int maxIdleTime, String name, int numConnections, long maxConnectionAge, Logger logger) {
    	super(name+"&delayTime="+delayTime+"&maxIdleTime="+maxIdleTime+"&size="+numConnections+"&maxConnectionAge="+(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":Long.toString(maxConnectionAge)));
        this.connectionClass = connectionClass;
        this.delayTime = delayTime;
        this.maxIdleTime = maxIdleTime;
        this.startTime = System.currentTimeMillis();
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(true);
        this.numConnections = numConnections;
        this.maxConnectionAge=maxConnectionAge;
        if(logger==null) throw new IllegalArgumentException("logger is null");
        this.logger=logger;
        connections = (C[])Array.newInstance(connectionClass, numConnections);
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

    protected abstract void close(C conn) throws E;

    /**
     * Shuts down the pool.
     * TODO: Add mechanism to allow clean shutdown, don't just close immediately.
     */
    final public void close() throws E {
        runMore = false;
        for (int c = 0; c < numConnections; c++) {
            C conn = connections[c];
            if (conn != null) {
                connections[c] = null;
                close(conn);
            }
        }
    }

    /**
     * Gets the number of connections that are currently busy.
     */
    final public int getConcurrency() {
        int total=0;
        for(int c=0;c<numConnections;c++) if(busyConnections[c]) total++;
        return total;
    }

    /**
     * Gets the number of connections currently connected.
     */
    final public int getConnectionCount() {
        int total=0;
        for(int c=0;c<numConnections;c++) if(connections[c]!=null) total++;
        return total;
    }

    /**
     * Gets a connection, warning of a connection is already used by this thread.
     *
     * @see  #getConnection(int)
     */
    public C getConnection() throws E {
        return getConnection(1);
    }

    /**
     * Gets a connection to the database.  Multiple <code>Connection</code>s to the database
     * may exist at any moment. It checks the <code>Connection</code> pool for a not busy
     * <code>Connection</code> sequentially. If found and the <code>Connection</code> is
     * a valid one, it returns that <code>Connection</code> object, otherwise creates a new
     * <code>connection</code>, adds it to the pool and also returns the <code>Connection</code>
     * object. If all the connections in the pool are busy, it waits until a connection becomes
     * available.
     *
     * @return     a <code>Connection</code> object
     * @exception  SQLException if unable to create the <code>Connection</code>
     * @exception  IOException if unable to access the <code>aoserv</code> file using
     *             <code>AOServConfiguration</code>
     */
    public C getConnection(int maxConnections) throws E {
        Thread thisThread=Thread.currentThread();
        while (true) {
            synchronized (connectionLock) {
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
                    logger.logp(
                        Level.WARNING,
                        AOPool.class.getName(),
                        "getConnection",
                        null,
                        new Throwable("Warning: Thread allocated more than one connection.  The stack trace at allocation time is included.", allocateStackTrace)
                    );
                }
                for (int c = 0; c < numConnections; c++) {
                    if (!busyConnections[c]) {
                        long currentTime=System.currentTimeMillis();
                        startTimes[c] = currentTime;
                        C connection = connections[c];
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
                        allocateStackTraces[c]=new Throwable("StackTrace at getConnection(" + maxConnections + ") for Thread named \"" + thisThread.getName() + "\"");

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
                    logger.logp(Level.WARNING, AOPool.class.getName(), "getConnection", null, err);
                }
            }
        }
    }

    /**
     * Creates a new connection.
     */
    protected abstract C getConnectionObject() throws E;

    /**
     * Gets the total number of connects for the entire pool.
     */
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

    protected abstract boolean isClosed(C conn) throws E;

    /**
     * Prints additional connection pool details.
     */
    protected abstract void printConnectionStats(Appendable out) throws IOException;

    /**
     * Prints complete statistics about connection pool use.
     */
    public final void printStatisticsHTML(Appendable out) throws IOException, E {
        out.append("<table style='border:1px;' cellspacing='0' cellpadding='2'>\n");
        printConnectionStats(out);
        out.append("  <tr><td>Max Connection Pool Size:</td><td>").append(Integer.toString(numConnections)).append("</td></tr>\n"
                + "  <tr><td>Max Connection Age:</td><td>");
        EncodingUtils.encodeHtml(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":StringUtility.getDecimalTimeLengthString(maxConnectionAge), out);
        out.append("</td></tr>\n"
                + "</table>\n"
                + "<br /><br />\n"
                + "<table style='border:1px;' cellspacing='0' cellpadding='2'>\n"
                + "  <tr><th colspan='11'><span style='font-size:large;'>Connections</span></th></tr>\n"
                + "  <tr>\n"
                + "    <th>Connection #</th>\n"
                + "    <th>Is Connected</th>\n"
                + "    <th>Conn Age</th>\n"
                + "    <th>Conn Count</th>\n"
                + "    <th>Use Count</th>\n"
                + "    <th>Total Time</th>\n"
                + "    <th>% of Time</th>\n"
                + "    <th>State</th>\n"
                + "    <th>State Time</th>\n"
                + "    <th>Ave Trans Time</th>\n"
                + "    <th>Stack Trace</th>\n"
                + "  </tr>\n");
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
                    out.append("  <tr>\n"
                            + "    <td>").append(Integer.toString(c+1)).append("</td>\n"
                            + "    <td>").append(isConnected?"Yes":"No").append("</td>\n"
                            + "    <td>");
                    if(isConnected) EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(System.currentTimeMillis()-createTimes[c]), out);
                    else out.append("&#160;");
                    out.append("    <td>").append(Long.toString(connCount)).append("</td>\n"
                            + "    <td>").append(Long.toString(useCount)).append("</td>\n"
                            + "    <td>");
                    EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(totalTime), out);
                    out.append("</td>\n"
                            + "    <td>").append(Float.toString(totalTime*100/(float)timeLen)).append("%</td>\n"
                            + "    <td>").append(isBusy?"In Use":isConnected?"Idle":"Closed").append("</td>\n"
                            + "    <td>");
                    EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(stateTime), out);
                    out.append("</td>\n"
                            + "    <td>").append(Long.toString(totalTime*1000/useCount)).append("&#181;s</td>\n"
                            + "    <td>");
                    Throwable T = allocateStackTraces[c];
                    if(T == null) out.append("&#160;");
                    else {
                        out.append("      <a href='#' onclick='var elem = document.getElementById(\"stack_").append(Integer.toString(c)).append("\").style; elem.visibility=(elem.visibility==\"visible\" ? \"hidden\" : \"visible\"); return false;'>Stack Trace</a>\n"
                                + "      <span id='stack_").append(Integer.toString(c)).append("' style='text-align:left; white-space:nowrap; position:absolute; visibility: hidden; z-index:").append(Integer.toString(c+1)).append("'>\n"
                                + "        <pre style='text-align:left; background-color:white; border: 2px solid; border-color: black;'>\n");
                        ErrorPrinter.printStackTraces(T, out);
                        out.append("        </pre>\n"
                                + "      </span>\n");
                    }
                    out.append("</td>\n"
                            + "  </tr>\n");

                    // Update totals
                    if(isConnected) totalConnected++;
                    totalConnects+=connCount;
                    totalUses+=useCount;
                    totalTotalTime+=totalTime;
                    if(isBusy) totalBusy++;
                }
            }
            out.append("  <tr>\n"
                    + "    <td><b>Total</b></td>\n"
                    + "    <td>").append(Integer.toString(totalConnected)).append("</td>\n"
                    + "    <td>&#160;</td>\n"
                    + "    <td>").append(Long.toString(totalConnects)).append("</td>\n"
                    + "    <td>").append(Long.toString(totalUses)).append("</td>\n"
                    + "    <td>");
            EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(totalTotalTime), out);
            out.append("</td>\n"
                    + "    <td>").append(Float.toString(timeLen==0 ? 0 : (totalTotalTime*100/(float)timeLen))).append("%</td>\n"
                    + "    <td>").append(Integer.toString(totalBusy)).append("</td>\n"
                    + "    <td>");
            EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(timeLen), out);
            out.append("</td>\n"
                    + "    <td>").append(Long.toString(totalUses==0 ? 0 : (totalTotalTime*1000/totalUses))).append("&#181;s</td>\n"
                    + "    <td>&#160;</td>\n"
                    + "  </tr>\n");
        }
        out.append("</table>\n");
    }

    /**
     * Releases the database <code>Connection</code> to the <code>Connection</code> pool.
     * @param connection java.sql.Connection
     */
    final public void releaseConnection(C connection) throws E {
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

    protected abstract void resetConnection(C conn) throws E;

    @Override
    final public void run() {
        while (runMore) {
            try {
                while (true) {
                    sleep(delayTime);
                    long time = System.currentTimeMillis();
                    synchronized (connectionLock) {
                        C[] myConnections = this.connections;
                        int size = myConnections.length;
                        boolean[] busyConnection = busyConnections;
                        long[] releaseTime = releaseTimes;
                        int maxIdle = maxIdleTime;
                        synchronized (this) {
                            for (int c = 0; c < size; c++) {
                                if(
                                    myConnections[c]!=null
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
                                    if(!isClosed(myConnections[c])) close(myConnections[c]);
                                    myConnections[c] = null;
                                    releaseTimes[c] = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                }
            } catch (ThreadDeath TD) {
                throw TD;
            } catch (Throwable T) {
                logger.logp(Level.SEVERE, AOPool.class.getName(), "run", null, T);
            }
            try {
                sleep(delayTime);
            } catch (InterruptedException err) {
                logger.logp(Level.WARNING, AOPool.class.getName(), "run", null, err);
            }
        }
    }

    protected abstract void throwException(String message, Throwable allocateStackTrace) throws E;
    
    final public Logger getLogger() {
        return logger;
    }
}