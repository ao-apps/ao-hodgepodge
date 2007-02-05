package com.aoindustries.sql;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "<init>(String,String,String,String,int)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * @deprecated  Please call AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     *
     * @see #AOConnectionPool(String,String,String,String,int,long,ErrorHandler)
     */
    public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge) {
        this(driver, url, user, password, numConnections, maxConnectionAge, new StandardErrorHandler());
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "<init>(String,String,String,String,int,long)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, ErrorHandler errorHandler) {
	super(AOConnectionPool.class.getName()+"?url=" + url+"&user="+user, numConnections, maxConnectionAge, errorHandler);
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "<init>(String,String,String,String,int,long,ErrorHandler)", null);
        try {
            this.driver = driver;
            this.url = url;
            this.user = user;
            this.password = password;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void close() throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "close()", null);
        try {
            try {
                closeImp();
            } catch(Exception err) {
                if(err instanceof SQLException) throw (SQLException)err;
                SQLException sqlErr=new SQLException();
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected void close(Object O) throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "close(Object)", null);
        try {
            ((Connection)O).close();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets a read/write connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
     */
    public Connection getConnection() throws SQLException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "getConnection()", null);
        try {
            return getConnection(Connection.TRANSACTION_READ_COMMITTED, false, 1);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets a connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
     */
    public Connection getConnection(boolean readOnly) throws SQLException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "getConnection(boolean)", null);
        try {
            return getConnection(Connection.TRANSACTION_READ_COMMITTED, readOnly, 1);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets a connection to the database with a maximum connections of 1.
     */
    public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "getConnection(int,boolean)", null);
        try {
	    return getConnection(isolationLevel, readOnly, 1);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "getConnection(int,boolean,int)", null);
        try {
            Connection conn=null;
            try {
                while(conn==null) {
                    conn=(Connection)getConnectionImp(maxConnections);
                    try {
                        if(conn.isReadOnly()!=readOnly) {
                            conn.setReadOnly(readOnly);
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
                if(conn.getTransactionIsolation()!=isolationLevel) conn.setTransactionIsolation(isolationLevel);
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected Object getConnectionObject() throws SQLException {
        Profiler.startProfile(Profiler.FAST, AOConnectionPool.class, "getConnectionObject()", null);
        try {
            try {
                Class.forName(driver).newInstance();
                return DriverManager.getConnection(url, user, password);
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected boolean isClosed(Object O) throws SQLException {
        Profiler.startProfile(Profiler.FAST, AOConnectionPool.class, "isClosed(Object)", null);
        try {
            return ((Connection)O).isClosed();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected void printConnectionStats(ChainWriter out) {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "printConnectionStats(ChainWriter)", null);
        try {
            out.print("  <TR><TH colspan=2><FONT size=+1>JDBC Driver</FONT></TH></TR>\n"
                    + "  <TR><TD>Driver:</TD><TD>").print(driver).print("</TD></TR>\n"
                    + "  <TR><TD>URL:</TD><TD>").print(url).print("</TD></TR>\n"
                    + "  <TR><TD>User:</TD><TD>").print(user).print("</TD></TR>\n"
                    + "  <TR><TD>Password:</TD><TD>");
            int len=password.length();
            for(int c=0;c<len;c++) out.print('*');
            out.print("</TD></TR>\n");
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void printStatisticsHTML(ChainWriter out) throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "printStatisticsHTML(ChainWriter)", null);
        try {
            try {
                printStatisticsHTMLImp(out);
            } catch(Exception err) {
                if(err instanceof SQLException) throw (SQLException)err;
                SQLException sqlErr=new SQLException();
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void releaseConnection(Connection connection) throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "releaseConnection(Connection)", null);
        try {
            try {
                releaseConnectionImp((Object)connection);
            } catch(Exception err) {
                if(err instanceof SQLException) throw (SQLException)err;
                SQLException sqlErr=new SQLException();
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected void resetConnection(Object O) throws SQLException {
        Profiler.startProfile(Profiler.IO, AOConnectionPool.class, "resetConnection(Object)", null);
        try {
            Connection connection=(Connection)O;

            // Dump all warnings to System.err and clear warnings
            SQLWarning warning=connection.getWarnings();
            if(warning!=null) errorHandler.reportWarning(warning, null);
            connection.clearWarnings();

            // Autocommit will always be turned on, regardless what a previous transaction might have done
            if(connection.getAutoCommit()==false) {
                connection.setAutoCommit(true);
            }

            // Restore to default transaction level
            if(connection.getTransactionIsolation()!=Connection.TRANSACTION_READ_COMMITTED) connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // Restore the connection to a read-only state
            if(!connection.isReadOnly()) {
                try {
                    connection.setReadOnly(true);
                } catch(SQLException err) {
                    String message=err.getMessage();
                    // TODO: InterBase has a problem with setReadOnly(true), this is a workaround
                    if(message!=null && message.indexOf("[interclient] Invalid operation when transaction is in progress.")!=-1) {
                        connection.close();
                    } else throw err;
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected void throwException(String message, Throwable allocateStackTrace) throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "throwException(String,Throwable)", null);
        try {
            IOException err=new IOException(message);
            err.initCause(allocateStackTrace);
            throw err;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public String toString() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AOConnectionPool.class, "toString()", null);
        try {
            return AOConnectionPool.class.getName()+"?url=" + url+"&user="+user;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
}
