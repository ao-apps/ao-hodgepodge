package com.aoindustries.sql;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.io.ChainWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable connection pooling with dynamic flaming tiger feature.
 *
 * @version  1.0
*
 * @author  AO Industries, Inc.
 */
final public class AOConnectionPool extends AOPool {

    private static final boolean DEBUG_TIMING = false;

    private String driver;
    private String url;
    private String user;
    private String password;

    public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, Logger logger) {
    	super(AOConnectionPool.class.getName()+"?url=" + url+"&user="+user, numConnections, maxConnectionAge, logger);
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void close() throws SQLException {
        try {
            closeImp();
        } catch(Exception err) {
            if(err instanceof SQLException) throw (SQLException)err;
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            throw sqlErr;
        }
    }

    protected void close(Object O) throws SQLException {
        ((Connection)O).close();
    }

    /**
     * Gets a read/write connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return getConnection(Connection.TRANSACTION_READ_COMMITTED, false, 1);
    }

    /**
     * Gets a connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
     * @param readOnly
     * @return
     * @throws SQLException
     */
    public Connection getConnection(boolean readOnly) throws SQLException {
        return getConnection(Connection.TRANSACTION_READ_COMMITTED, readOnly, 1);
    }

    /**
     * Gets a connection to the database with a maximum connections of 1.
     * @param isolationLevel
     * @param readOnly
     * @return
     * @throws SQLException
     */
    public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
        return getConnection(isolationLevel, readOnly, 1);
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
        Connection conn=null;
        try {
            while(conn==null) {
                long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
                conn=(Connection)getConnectionImp(maxConnections);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: AOConnectionPool: getConnection: getConnectionImp in "+(endTime-startTime)+" ms");
                }
                try {
                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    boolean isReadOnly = conn.isReadOnly();
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: AOConnectionPool: getConnection: isReadOnly in "+(endTime-startTime)+" ms");
                    }
                    if(isReadOnly!=readOnly) {
                        if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                        conn.setReadOnly(readOnly);
                        if(DEBUG_TIMING) {
                            long endTime = System.currentTimeMillis();
                            System.err.println("DEBUG: AOConnectionPool: getConnection: setReadOnly("+readOnly+") in "+(endTime-startTime)+" ms");
                        }
                    }
                } catch(SQLException err) {
                    String message=err.getMessage();
                    // TODO: InterBase has a problem with setReadOnly(true), this is a workaround
                    if(message!=null && message.indexOf("[interclient] Invalid operation when transaction is in progress.")!=-1) {
                        conn.close();
                        releaseConnectionImp(conn);
                        conn=null;
                    } else throw err;
                }
            }
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            int currentIsolationLevel = conn.getTransactionIsolation();
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: AOConnectionPool: getConnection: getTransactionIsolation in "+(endTime-startTime)+" ms");
            }
            if(currentIsolationLevel!=isolationLevel) {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                conn.setTransactionIsolation(isolationLevel);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: AOConnectionPool: getConnection: setTransactionIsolation("+isolationLevel+") in "+(endTime-startTime)+" ms");
                }
            }
            return conn;
        } catch(SQLException err) {
            if(conn!=null) {
                try {
                    releaseConnectionImp(conn);
                } catch(Exception err2) {
                    // Will throw original error instead
                }
            }
            throw err;
        } catch(Exception err) {
            if(conn!=null) {
                try {
                    releaseConnectionImp(conn);
                } catch(Exception err2) {
                    // Will throw original error instead
                }
            }
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            throw sqlErr;
        }
    }

    protected Object getConnectionObject() throws SQLException {
        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url, user, password);
            if(conn.getClass().getName().startsWith("org.postgresql.")) {
                // getTransactionIsolation causes a round-trip to the database, this wrapper caches the value and avoids unnecessary sets
                // to eliminate unnecessary round-trips and improve performance over high-latency links.
                conn = new PostgresqlConnectionWrapper(conn);
            }
            return conn;
        } catch(SQLException err) {
            logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", err);
            throw err;
        } catch (ClassNotFoundException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
            throw sqlErr;
        } catch (InstantiationException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
            throw sqlErr;
        } catch (IllegalAccessException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
            throw sqlErr;
        }
    }

    protected boolean isClosed(Object O) throws SQLException {
        return ((Connection)O).isClosed();
    }

    protected void printConnectionStats(Appendable out) throws IOException {
        out.append("  <tr><th colspan='2'><span style='font-size:large;'>JDBC Driver</span></th></tr>\n"
                + "  <tr><td>Driver:</td><td>");
        EncodingUtils.encodeHtml(driver, out);
        out.append("</td></tr>\n"
                + "  <tr><td>URL:</td><td>");
        EncodingUtils.encodeHtml(url, out);
        out.append("</td></tr>\n"
                + "  <tr><td>User:</td><td>");
        EncodingUtils.encodeHtml(user, out);
        out.append("</td></tr>\n"
                + "  <tr><td>Password:</td><td>");
        int len=password.length();
        for(int c=0;c<len;c++) {
            out.append('*');
        }
        out.append("</td></tr>\n");
    }

    public void printStatisticsHTML(ChainWriter out) throws SQLException {
        try {
            printStatisticsHTMLImp(out);
        } catch(Exception err) {
            if(err instanceof SQLException) throw (SQLException)err;
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            throw sqlErr;
        }
    }

    public void releaseConnection(Connection connection) throws SQLException {
        try {
            releaseConnectionImp((Object)connection);
        } catch(Exception err) {
            if(err instanceof SQLException) throw (SQLException)err;
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            throw sqlErr;
        }
    }

    protected void resetConnection(Object O) throws SQLException {
        Connection connection=(Connection)O;

        // Dump all warnings to System.err and clear warnings
        SQLWarning warning=connection.getWarnings();
        if(warning!=null) {
            logger.logp(Level.WARNING, AOConnectionPool.class.getName(), "resetConnection", null, warning);
        }
        connection.clearWarnings();

        // Autocommit will always be turned on, regardless what a previous transaction might have done
        if(connection.getAutoCommit()==false) {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            connection.setAutoCommit(true);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: AOConnectionPool: resetConnection: setAutoCommit(true) in "+(endTime-startTime)+" ms");
            }
        }

        // Restore to default transaction level
        if(connection.getTransactionIsolation()!=Connection.TRANSACTION_READ_COMMITTED) {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: AOConnectionPool: resetConnection: setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED) in "+(endTime-startTime)+" ms");
            }
        }

        // Restore the connection to a read-only state
        if(!connection.isReadOnly()) {
            try {
                long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
                connection.setReadOnly(true);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: AOConnectionPool: resetConnection: setReadOnly(true) in "+(endTime-startTime)+" ms");
                }
            } catch(SQLException err) {
                String message=err.getMessage();
                // TODO: InterBase has a problem with setReadOnly(true), this is a workaround
                if(message!=null && message.indexOf("[interclient] Invalid operation when transaction is in progress.")!=-1) {
                    connection.close();
                } else throw err;
            }
        }
    }

    protected void throwException(String message, Throwable allocateStackTrace) throws IOException {
        IOException err=new IOException(message);
        err.initCause(allocateStackTrace);
        throw err;
    }

    @Override
    public String toString() {
        return "AOConnectionPool(url=\""+url+"\", user=\""+user+"\")";
    }
}
