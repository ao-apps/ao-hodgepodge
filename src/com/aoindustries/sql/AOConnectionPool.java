package com.aoindustries.sql;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

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

    /**
     * @deprecated  Please call AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     *
     * @see #AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     */
    public AOConnectionPool(String driver, String url, String user, String password, int numConnections) {
        this(driver, url, user, password, numConnections, DEFAULT_MAX_CONNECTION_AGE, new StandardErrorHandler());
    }

    /**
     * @deprecated  Please call AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     *
     * @see #AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     */
    public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge) {
        this(driver, url, user, password, numConnections, maxConnectionAge, new StandardErrorHandler());
    }

    public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, ErrorHandler errorHandler) {
	super(AOConnectionPool.class.getName()+"?url=" + url+"&user="+user, numConnections, maxConnectionAge, errorHandler);
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
     */
    public Connection getConnection() throws SQLException {
        return getConnection(Connection.TRANSACTION_READ_COMMITTED, false, 1);
    }

    /**
     * Gets a connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
     */
    public Connection getConnection(boolean readOnly) throws SQLException {
        return getConnection(Connection.TRANSACTION_READ_COMMITTED, readOnly, 1);
    }

    /**
     * Gets a connection to the database with a maximum connections of 1.
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
            errorHandler.reportError(
                err, new Object[] {"url="+url, "user="+user, "password=XXXXXXXX"}
            );
            throw err;
        } catch (ClassNotFoundException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            errorHandler.reportError(
                sqlErr, new Object[] {"url="+url, "user="+user, "password=XXXXXXXX"}
            );
            throw sqlErr;
        } catch (InstantiationException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            errorHandler.reportError(
                sqlErr, new Object[] {"url="+url, "user="+user, "password=XXXXXXXX"}
            );
            throw sqlErr;
        } catch (IllegalAccessException err) {
            SQLException sqlErr=new SQLException();
            sqlErr.initCause(err);
            errorHandler.reportError(
                sqlErr, new Object[] {"url="+url, "user="+user, "password=XXXXXXXX"}
            );
            throw sqlErr;
        }
    }

    protected boolean isClosed(Object O) throws SQLException {
        return ((Connection)O).isClosed();
    }

    protected void printConnectionStats(ChainWriter out) {
        out.print("  <TR><TH colspan='2'><FONT size=+1>JDBC Driver</FONT></TH></TR>\n"
                + "  <TR><TD>Driver:</TD><TD>").print(driver).print("</TD></TR>\n"
                + "  <TR><TD>URL:</TD><TD>").print(url).print("</TD></TR>\n"
                + "  <TR><TD>User:</TD><TD>").print(user).print("</TD></TR>\n"
                + "  <TR><TD>Password:</TD><TD>");
        int len=password.length();
        for(int c=0;c<len;c++) out.print('*');
        out.print("</TD></TR>\n");
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
        if(warning!=null) errorHandler.reportWarning(warning, null);
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

    public String toString() {
        return "AOConnectionPool(url=\""+url+"\", user=\""+user+"\")";
    }
}
