package com.aoindustries.apache.catalina.jdbc4_1_31;

/*
 * This code is partially derived from org.apache.catalina.session.ManagerBase and
 * org.apache.catalina.session.StandardManager.  For this reason we make this code
 * available to everybody in the aocode-public package.  Ultimately, we would like
 * to submit this code to Apache for inclusion in their Tomcat distribution.
 *
 * By AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.sql.AOConnectionPool;
import com.aoindustries.sql.WrappedSQLException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Logger;
import org.apache.catalina.Session;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.util.LifecycleSupport;

/**
 * Stores all session information directly and immediately to a JDBC database.
 *
 * TODO: Periodic cleanup of database, at somewhat random intervals
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class JDBCManager extends ManagerBase implements Lifecycle, Runnable {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JDBCManager.class.getName());

    private static final int DEBUG_LEVEL=2;

    // Configuration values
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;
    private int jdbcMaxConnections=32;
    private String tableName="jdbc_sessions";
    private String idColumnName="id";
    private String maxInactiveIntervalColumnName="max_inactive_interval";
    private String validColumnName="valid";
    private String creationTimeColumnName="creation_time";
    private String lastAccessedColumnName="last_accessed";
    private String attributesColumnName="attributes";
    private int checkInterval=60;

    // Getters and setters for configuration values
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        String oldJdbcDriver=this.jdbcDriver;
        this.jdbcDriver = jdbcDriver;
        support.firePropertyChange(
            "jdbcDriver",
            oldJdbcDriver,
            this.jdbcDriver
        );
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        String oldJdbcUrl=this.jdbcUrl;
        this.jdbcUrl = jdbcUrl;
        support.firePropertyChange(
            "jdbcUrl",
            oldJdbcUrl,
            this.jdbcUrl
        );
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public void setJdbcUsername(String jdbcUsername) {
        String oldJdbcUser=this.jdbcUsername;
        this.jdbcUsername = jdbcUsername;
        support.firePropertyChange(
            "jdbcUsername",
            oldJdbcUser,
            this.jdbcUsername
        );
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        String oldJdbcPassword=this.jdbcPassword;
        this.jdbcPassword = jdbcPassword;
        support.firePropertyChange(
            "jdbcPassword",
            oldJdbcPassword,
            this.jdbcPassword
        );
    }

    public int getJdbcMaxConnections() {
        return jdbcMaxConnections;
    }

    public void setJdbcMaxConnections(int jdbcMaxConnections) {
        int oldJdbcMaxConnections=this.jdbcMaxConnections;
        this.jdbcMaxConnections = jdbcMaxConnections;
        support.firePropertyChange(
            "jdbcMaxConnections",
            Integer.valueOf(oldJdbcMaxConnections),
            Integer.valueOf(this.jdbcMaxConnections)
        );
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        String oldTableName=this.tableName;
        this.tableName = tableName;
        support.firePropertyChange(
            "tableName",
            oldTableName,
            this.tableName
        );
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public void setIdColumnName(String idColumnName) {
        String oldIdColumnName=this.idColumnName;
        this.idColumnName = idColumnName;
        support.firePropertyChange(
            "idColumnName",
            oldIdColumnName,
            this.idColumnName
        );
    }

    public String getMaxInactiveIntervalColumnName() {
        return maxInactiveIntervalColumnName;
    }

    public void setMaxInactiveIntervalColumnName(String maxInactiveIntervalColumnName) {
        String oldMaxInactiveIntervalColumnName=this.maxInactiveIntervalColumnName;
        this.maxInactiveIntervalColumnName = maxInactiveIntervalColumnName;
        support.firePropertyChange(
            "maxInactiveIntervalColumnName",
            oldMaxInactiveIntervalColumnName,
            this.maxInactiveIntervalColumnName
        );
    }

    public String getValidColumnName() {
        return validColumnName;
    }

    public void setValidColumnName(String validColumnName) {
        String oldValidColumnName=this.validColumnName;
        this.validColumnName = validColumnName;
        support.firePropertyChange(
            "validColumnName",
            oldValidColumnName,
            this.validColumnName
        );
    }

    public String getCreationTimeColumnName() {
        return creationTimeColumnName;
    }

    public void setCreationTimeColumnName(String creationTimeColumnName) {
        String oldCreationTimeColumnName=this.creationTimeColumnName;
        this.creationTimeColumnName = creationTimeColumnName;
        support.firePropertyChange(
            "creationTimeColumnName",
            oldCreationTimeColumnName,
            this.creationTimeColumnName
        );
    }

    public String getLastAccessedColumnName() {
        return lastAccessedColumnName;
    }

    public void setLastAccessedColumnName(String lastAccessedColumnName) {
        String oldLastAccessedColumnName=this.lastAccessedColumnName;
        this.lastAccessedColumnName = lastAccessedColumnName;
        support.firePropertyChange(
            "lastAccessedColumnName",
            oldLastAccessedColumnName,
            this.lastAccessedColumnName
        );
    }

    public String getAttributesColumnName() {
        return attributesColumnName;
    }

    public void setAttributesColumnName(String attributesColumnName) {
        String oldAttributesColumnName=this.attributesColumnName;
        this.attributesColumnName = attributesColumnName;
        support.firePropertyChange(
            "attributesColumnName",
            oldAttributesColumnName,
            this.attributesColumnName
        );
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        int oldCheckInterval=this.checkInterval;
        this.checkInterval = checkInterval;
        support.firePropertyChange(
            "checkInterval",
            Integer.valueOf(oldCheckInterval),
            Integer.valueOf(this.checkInterval)
        );
    }

    private final Object poolLock=new Object();
    private AOConnectionPool pool;
    protected AOConnectionPool getPool() {
        synchronized(poolLock) {
            if(pool==null) {
                pool=new AOConnectionPool(
                    jdbcDriver,
                    jdbcUrl,
                    jdbcUsername,
                    jdbcPassword,
                    jdbcMaxConnections,
                    AOPool.DEFAULT_MAX_CONNECTION_AGE,
                    logger
                );
            }
            return pool;
        }
    }

    public void reportWarning(Throwable T, Object[] extraInfo) {
        Logger myLogger = null;
        if (container != null) myLogger = container.getLogger();
        if (myLogger != null) {
            myLogger.log(getName() + "[" + container.getName() + "]: AOConnectionPool", T, Logger.WARNING);
        } else {
            String containerName = null;
            if (container != null) containerName = container.getName();
            System.out.println(getName() + "[" + containerName + "]: AOConnectionPool");
            T.printStackTrace(System.out);
        }
    }

    public void reportError(Throwable T, Object[] extraInfo) {
        Logger logger = null;
        if (container != null) logger = container.getLogger();
        if (logger != null) {
            logger.log(getName() + "[" + container.getName() + "]: AOConnectionPool", T, Logger.ERROR);
        } else {
            String containerName = null;
            if (container != null) containerName = container.getName();
            System.out.println(getName() + "[" + containerName + "]: AOConnectionPool");
            T.printStackTrace(System.out);
        }
    }

    /**
     * The number of requests per session ID is tracked.
     * The key is the requested session ID and the value is an <code>int[]</code> of the current count.
     */
    protected final Map<String,int[]> sessionRequestCounters=new HashMap<String,int[]>();

    /**
     * Each session is cached when its sessionRequestCounter indicates at least one
     * request is currently using the request.
     */
    protected final Map<String,JDBCSession> loadedSessions=new HashMap<String,JDBCSession>();
    
    /**
     * Any new sessions will be track here so that the sessions will be passivated
     * at the end of the request
     */
    protected final Map<Thread,List<JDBCSession>> newSessions=new HashMap<Thread,List<JDBCSession>>();

    public String getName() {
        return "JDBCManager";
    }

    public String getInfo() {
        return "JDBCManager/1.0";
    }

    /**
     * Has this component been started yet?
     */
    private boolean started = false;


    /**
     * The background thread.
     */
    private Thread thread = null;


    /**
     * The background thread completion semaphore.
     */
    private boolean threadDone = false;


    /**
     * Name to register for the background thread.
     */
    private String threadName = "JDBCManager";

    private int expiredSessions=0;

    /**
     * Number of sessions that expired.
     */
    public int getExpiredSessions() {
        return expiredSessions;
    }

    public void setExpiredSessions(int expiredSessions) {
        this.expiredSessions = expiredSessions;
    }

    /**
     * Converts the attributes of the session to a <code>byte[]</code>.
     */
    protected byte[] getAttributeBytes(Session session) throws IOException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.getAttributeBytes()");
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        ObjectOutputStream oout=new ObjectOutputStream(bout);
        HttpSession httpSession=session.getSession();
        Enumeration keys=httpSession.getAttributeNames();
        while(keys.hasMoreElements()) {
            String key=(String)keys.nextElement();
            Object value=httpSession.getAttribute(key);
            if(value!=null) {
                oout.writeBoolean(true);
                oout.writeUTF(key);
                oout.writeObject(value);
            }
        }
        oout.writeBoolean(false);
        oout.close();
        return bout.toByteArray();
    }

    private void insertSession(Connection conn, Session session) throws SQLException, IOException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.insertSession(Connection,Session)");
        PreparedStatement pstmt=conn.prepareStatement(
            "insert into\n"
            + tableName + "\n"
            + "(\n"
            + "  " + idColumnName + ",\n"
            + "  " + maxInactiveIntervalColumnName + ",\n"
            + "  " + validColumnName + ",\n"
            + "  " + creationTimeColumnName + ",\n"
            + "  " + lastAccessedColumnName + ",\n"
            + "  " + attributesColumnName + "\n"
            + ") values(\n"
            + "  ?,\n"
            + "  ?,\n"
            + "  ?,\n"
            + "  ?,\n"
            + "  ?,\n"
            + "  ?\n"
            + ")"
        );
        try {
            pstmt.setString(1, session.getId());
            pstmt.setInt(2, session.getMaxInactiveInterval());
            pstmt.setBoolean(3, session.isValid());
            pstmt.setTimestamp(4, new Timestamp(session.getCreationTime()));
            pstmt.setTimestamp(5, new Timestamp(session.getLastAccessedTime()));
            pstmt.setBytes(6, getAttributeBytes(session));
            pstmt.executeUpdate();
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    public void add(Session session) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.add(Session)");
        try {
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                insertSession(conn, session);
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(IOException err) {
            reportError(err, null);
        } catch(SQLException err) {
            reportError(err, null);
        }
    }

    void add(Session session, Connection conn) throws SQLException, IOException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.add(Session,Connection)");
        insertSession(conn, session);
    }
    
    public Session createSession() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.createSession()");
        try {
            // Create a Session instance
            JDBCSession session = (JDBCSession)createEmptySession();

            // Initialize the properties of the new session and return it
            session.setNew(true);
            session.setValid(true, false);
            session.setCreationTime(System.currentTimeMillis());
            session.setMaxInactiveInterval(this.maxInactiveInterval, false);
            String jvmRoute = getJvmRoute();

            AOConnectionPool pool=getPool();
            
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                String sessionId=null;

                PreparedStatement pstmt=conn.prepareStatement(
                    "select " + idColumnName + " from " + tableName + " where " + idColumnName + "=?"
                );
                try {
                    while(sessionId==null) {
                        String newSessionId=generateSessionId();
                        // @todo Move appending of jvmRoute generateSessionId()???
                        if (jvmRoute != null) {
                            newSessionId += '.' + jvmRoute;
                        }
                        pstmt.setString(1, newSessionId);
                        ResultSet results=pstmt.executeQuery();
                        try {
                            if(results.next()) duplicates++;
                            else sessionId=newSessionId;
                        } finally {
                            results.close();
                        }
                    }
                } finally {
                    pstmt.close();
                }

                // The setId calls manager.add, which inserts the row
                session.setId(sessionId, conn);
                sessionCounter++;

                // Commit to db
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
            
            // Add to the active sessions, but also keep track of the sessions created by this Thread
            synchronized(loadedSessions) {
                loadedSessions.put(session.getId(), session);
            }
            synchronized(newSessions) {
                Thread currentThread=Thread.currentThread();
                List<JDBCSession> newSessionList=newSessions.get(currentThread);
                if(newSessionList==null) newSessions.put(currentThread, newSessionList=new ArrayList<JDBCSession>());
                newSessionList.add(session);
            }

            // Return the new session
            return session;
        } catch(IOException err) {
            reportError(err, null);
            return null;
        } catch(SQLException err) {
            reportError(err, null);
            return null;
        }
    }
    
    public Session createEmptySession() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.createEmptySession()");
        return new JDBCSession(this);
    }

    /**
     * Loads a JDBCSession from the database
     */
    private JDBCSession loadSession(String id) throws SQLException, IOException, ClassNotFoundException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.loadSession(String)");
        AOConnectionPool pool=getPool();
        Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, true);
        try {
            PreparedStatement pstmt=conn.prepareStatement(
                "select\n"
                + "  " + idColumnName + ",\n"
                + "  " + maxInactiveIntervalColumnName + ",\n"
                + "  " + validColumnName + ",\n"
                + "  " + creationTimeColumnName + ",\n"
                + "  " + lastAccessedColumnName + ",\n"
                + "  " + attributesColumnName + "\n"
                + "from\n"
                + "  " + tableName + "\n"
                + "where\n"
                + "  " + idColumnName + "=?"
            );
            try {
                pstmt.setString(1, id);
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        JDBCSession session=(JDBCSession)createEmptySession();
                        session.init(
                            results.getString(1),
                            results.getInt(2),
                            results.getBoolean(3),
                            results.getTimestamp(4).getTime(),
                            results.getTimestamp(5).getTime(),
                            results.getBinaryStream(6)
                        );
                        return session;
                    }
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(SQLException err) {
            if(!conn.isClosed()) {
                conn.close();
            }
            throw err;
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public Session findSession(String id) throws IllegalStateException, IOException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.findSession(String)");
        // Check the loaded sessions
        JDBCSession session;
        synchronized(loadedSessions) {
            session=loadedSessions.get(id);
        }

        // Check the new sessions created by this thread
        if(session==null) {
            synchronized(newSessions) {
                List<JDBCSession> newSessionList = newSessions.get(Thread.currentThread());
                if(newSessionList!=null) {
                    for(JDBCSession newSession : newSessionList) {
                        if(newSession.getId().equals(id)) {
                            session=newSession;
                            break;
                        }
                    }
                }
            }
        }

        // Check the database
        if(session==null) {
            try {
                session=loadSession(id);
                if(session!=null) {
                    boolean isActive;
                    synchronized(sessionRequestCounters) {
                        int[] count=sessionRequestCounters.get(id);
                        isActive=count!=null && count[0]>=1;
                    }
                    if(isActive) {
                        // Add to the loaded sessions is not there
                        synchronized(loadedSessions) {
                            if(!loadedSessions.containsKey(id)) loadedSessions.put(id, session);
                        }
                    }
                    session.activate();
                }
            } catch(SQLException err) {
                reportError(err, null);
                IOException ioErr=new IOException("SQLException");
                ioErr.initCause(err);
                throw ioErr;
            } catch(ClassNotFoundException err) {
                reportError(err, null);
                IOException ioErr=new IOException("ClassNotFoundException");
                ioErr.initCause(err);
                throw ioErr;
            } catch(IOException err) {
                reportError(err, null);
                throw err;
            }
        }
        return session;
    }

    public Session[] findSessions() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.findSessions()");
        Session[] results = null;
        synchronized (loadedSessions) {
            Collection<JDBCSession> sessions = loadedSessions.values();
            results = new Session[sessions.size()];
            sessions.toArray(results);
        }
        return (results);
    }
    
    public void remove(Session session) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.remove(Session)");
        String id=session.getId();

        // Remove from DB
        try {
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                removeSession(id, conn);
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(SQLException err) {
            reportError(err, null);
        }

        // Remove from active sessions
        synchronized(loadedSessions) {
            loadedSessions.remove(id);
        }
    }

    void remove(Session session, Connection conn) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.remove(Session,Connection)");
        String id=session.getId();

        // Remove from DB
        try {
            removeSession(id, conn);
        } catch(SQLException err) {
            reportError(err, null);
        }

        // Remove from active sessions
        synchronized(loadedSessions) {
            loadedSessions.remove(id);
        }
    }

    private void removeSession(String id, Connection conn) throws SQLException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.removeSession(String,Connection)");
        PreparedStatement pstmt=conn.prepareStatement("delete from "+tableName+" where "+idColumnName+"=?");
        try {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    public int getActiveSessions() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.getActiveSession()");
        synchronized(loadedSessions) {
            return loadedSessions.size();
        }
    }

    public String listSessionIds() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.listSessionIds()");
        StringBuilder sb=new StringBuilder();
        synchronized(loadedSessions) {
            Iterator keys=loadedSessions.keySet().iterator();
            while( keys.hasNext() ) {
                sb.append(keys.next()).append(" ");
            }
        }
        return sb.toString();
    }
    
    public String getSessionAttribute(String sessionId, String key) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.getSessionAttribute(String,String)");
        try {
            Session s=findSession(sessionId);
            if( s==null ) {
                log("Session not found " + sessionId);
                return null;
            }
            Object o=s.getSession().getAttribute(key);
            if( o==null ) return null;
            return o.toString();
        } catch(IOException err) {
            reportError(err, null);
            return null;
        }
    }
    
    public void expireSession(String sessionId) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.expireSession(String)");
        try {
            Session s=findSession(sessionId);
            if( s==null ) {
                log("Session not found " + sessionId);
                return;
            }
            s.expire();
        } catch(IOException err) {
            reportError(err, null);
        }
    }
    
    public String getLastAccessedTime(String sessionId) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.getLastAccessedTime(String)");
        try {
            // Check the cache first
            Session activeSession;
            synchronized(loadedSessions) {
                activeSession=(Session)loadedSessions.get(sessionId);
            }
            if(activeSession!=null) return new Date(activeSession.getLastAccessedTime()).toString();

            // Query the database
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, true);
            try {
                PreparedStatement pstmt=conn.prepareStatement("select "+lastAccessedColumnName+" from "+tableName+" where "+idColumnName+"=?");
                try {
                    pstmt.setString(1, sessionId);
                    ResultSet results=pstmt.executeQuery();
                    try {
                        if(results.next()) return new Date(results.getTimestamp(1).getTime()).toString();
                        reportWarning(new IllegalArgumentException("Session not found " + sessionId), null);
                        return "";
                    } finally {
                        results.close();
                    }
                } catch(SQLException err) {
                    throw new WrappedSQLException(err, pstmt);
                } finally {
                    pstmt.close();
                }
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(SQLException err) {
            reportError(err, null);
            return "";
        }
    }
    
    void setLastAccessedTime(String sessionId, long lastAccessed) throws SQLException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.setLastAccessedTime(String,long)");
        // Update the underlying database
        AOConnectionPool pool=getPool();
        Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
        try {
            PreparedStatement pstmt=conn.prepareStatement("update "+tableName+" set "+lastAccessedColumnName+"=? where "+idColumnName+"=?");
            try {
                pstmt.setTimestamp(1, new Timestamp(lastAccessed));
                pstmt.setString(2, sessionId);
                pstmt.executeUpdate();
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
            conn.commit();
        } catch(SQLException err) {
            if(!conn.isClosed()) {
                conn.rollback();
                conn.close();
            }
            throw err;
        } finally {
            pool.releaseConnection(conn);
        }
    }

    /**
     * All setAttribute calls on the sessions are committed to the database immediately so
     * unloading of any sessions is not necessary.
     * This method does nothing.
     */
    public void unload() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.unload()");
    }
    
    /**
     * Sessions are loaded on an as-needed basis, not all at once.
     * This method does nothing.
     */
    public void load() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.load()");
    }

    /**
     * Increments the request counter for the provided sessionId, and updates the
     * access time for the provided sessionId.
     */
    void incrementRequestCounter(String sessionId) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.incrementRequestCounter() - method starting");
        int newCount;
        synchronized(sessionRequestCounters) {
            int[] count=sessionRequestCounters.get(sessionId);
            if(count==null) sessionRequestCounters.put(sessionId, new int[] {newCount=1});
            else newCount=++count[0];
        }
        if(debug>=DEBUG_LEVEL) log("JDBCManager.incrementRequestCounter() - newCount="+newCount);
    }

    /**
     * Decrement the request counter for the provided sessionId.  If this
     * is the last request for the provided session, remove the cached
     * session.
     */
    void decrementRequestCounter(String sessionId) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.decrementRequestCounter() - method starting");
        boolean removeCache=true;
        synchronized(sessionRequestCounters) {
            int[] count=sessionRequestCounters.get(sessionId);
            if(count!=null) {
                int remaining=--count[0];
                if(remaining>0) {
                    // Leave the cache for the remaining requests
                    removeCache=false;
                } else {
                    // Last request ending, remove the counter
                    sessionRequestCounters.remove(sessionId);
                }
            }
        }
        if(removeCache) {
            Session session;
            synchronized(loadedSessions) {
                session=(Session)loadedSessions.get(sessionId);
            }
            if(session!=null && (session instanceof JDBCSession)) {
                JDBCSession jdbcSession=(JDBCSession)session;
                jdbcSession.passivate();
                if(debug>=DEBUG_LEVEL) log("JDBCManager.decrementRequestCounter() - removing from loadedSessions");
                synchronized(loadedSessions) {
                    loadedSessions.remove(sessionId);
                }
            }
        }
        if(debug>=DEBUG_LEVEL) log("JDBCManager.decrementRequestCounter() - method ending");
    }

    /**
     * Passivates new sessions (sessions created by this thread but not tracked by request counters).
     */
    void passivateNewSessions() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.passivateNewSessions() - method starting");
        List passivateList;
        synchronized(newSessions) {
            passivateList=(List)newSessions.remove(Thread.currentThread());
        }
        if(passivateList!=null) {
            for(int c=0;c<passivateList.size();c++) {
                JDBCSession session=(JDBCSession)passivateList.get(c);
                String sessionId=session.getId();
                session.passivate();
                if(debug>=DEBUG_LEVEL) log("JDBCManager.passivateNewSessions() - removing from loadedSessions");
                synchronized(loadedSessions) {
                    loadedSessions.remove(sessionId);
                }
            }
        }
        if(debug>=DEBUG_LEVEL) log("JDBCManager.passivateNewSessions() - method ending");
    }

    public void log(String message) {
        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log(getName() + "[" + container.getName() + "]: "
                       + message);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println(getName() + "[" + containerName
                               + "]: " + message);
        }
    }

    public void log(String message, Throwable throwable) {
        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log(getName() + "[" + container.getName() + "] "
                       + message, throwable);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println(getName() + "[" + containerName
                               + "]: " + message);
            throwable.printStackTrace(System.out);
        }
    }

    /**
     * @see JDBCSession#setMaxInactiveInterval
     */
    void setMaxInactiveInterval(String sessionId, int interval) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.setMaxInactiveInterval(String,int)");
        try {
            // Update the underlying database
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                PreparedStatement pstmt=conn.prepareStatement("update "+tableName+" set "+maxInactiveIntervalColumnName+"=? where "+idColumnName+"=?");
                try {
                    pstmt.setInt(1, interval);
                    pstmt.setString(2, sessionId);
                    pstmt.executeUpdate();
                } catch(SQLException err) {
                    throw new WrappedSQLException(err, pstmt);
                } finally {
                    pstmt.close();
                }
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(SQLException err) {
            reportError(err, null);
        }
    }

    /**
     * @see JDBCSession#setValid
     */
    void setValid(String sessionId, boolean isValid) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.setValid(String,boolean)");
        try {
            // Update the underlying database
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                PreparedStatement pstmt=conn.prepareStatement("update "+tableName+" set "+validColumnName+"=? where "+idColumnName+"=?");
                try {
                    pstmt.setBoolean(1, isValid);
                    pstmt.setString(2, sessionId);
                    pstmt.executeUpdate();
                } catch(SQLException err) {
                    throw new WrappedSQLException(err, pstmt);
                } finally {
                    pstmt.close();
                }
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(SQLException err) {
            reportError(err, null);
        }
    }

    /**
     * @see JDBCSession#removeAttribute(String,boolean)
     * @see JDBCSession#setAttribute(String,Object)
     */
    void updateAttributes(Session session) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.updateAttributes(Session)");
        try {
            byte[] bytes=getAttributeBytes(session);

            // Update the underlying database
            AOConnectionPool pool=getPool();
            Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
            try {
                PreparedStatement pstmt=conn.prepareStatement("update "+tableName+" set "+attributesColumnName+"=? where "+idColumnName+"=?");
                try {
                    pstmt.setBytes(1, bytes);
                    pstmt.setString(2, session.getId());
                    pstmt.executeUpdate();
                } catch(SQLException err) {
                    throw new WrappedSQLException(err, pstmt);
                } finally {
                    pstmt.close();
                }
                conn.commit();
            } catch(SQLException err) {
                if(!conn.isClosed()) {
                    conn.rollback();
                    conn.close();
                }
                throw err;
            } finally {
                pool.releaseConnection(conn);
            }
        } catch(IOException err) {
            reportError(err, null);
        } catch(SQLException err) {
            reportError(err, null);
        }
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.addLifecycleListener(LifecycleListener)");

        lifecycle.addLifecycleListener(listener);

    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    public LifecycleListener[] findLifecycleListeners() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.findLifecycleListeners()");

        return lifecycle.findLifecycleListeners();

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removeLifecycleListener(LifecycleListener listener) {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.removeLifecycleListener(LifecycleListener)");

        lifecycle.removeLifecycleListener(listener);

    }

    
    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.start()");

        if (debug >= 1)
            log("Starting");

        // Validate and update our current component state
        if (started)
            throw new LifecycleException
                (sm.getString("standardManager.alreadyStarted"));
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

        // Force initialization of the random number generator
        if (debug >= 1)
            log("Force random number initialization starting");
        String dummy = generateSessionId();
        if (debug >= 1)
            log("Force random number initialization completed");

        // Load unloaded sessions, if any
        try {
            load();
        } catch (Throwable t) {
            log(sm.getString("standardManager.managerLoad"), t);
        }

        // Start the background reaper thread
        threadStart();

    }

    
    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.stop()");

        if (debug >= 1)
            log("Stopping");

        // Validate and update our current component state
        if (!started)
            throw new LifecycleException
                (sm.getString("standardManager.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        // Stop the background reaper thread
        threadStop();

        // Write out sessions
        //try {
            unload();
        //} catch (IOException e) {
        //    log(sm.getString("standardManager.managerUnload"), e);
        //}

        // Expire all active sessions
        /*
        Session sessions[] = findSessions();
        for (int i = 0; i < sessions.length; i++) {
            StandardSession session = (StandardSession) sessions[i];
            if (!session.isValid())
                continue;
            try {
                session.expire();
            } catch (Throwable t) {
                ;
            }
        }*/

        // Require a new random number generator if we are restarted
        this.random = null;

    }

    /**
     * Invalidate all sessions that have expired.
     */
    private void processExpires() throws SQLException {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.processExpires() - method starting");

        long timeNow = System.currentTimeMillis();
        
        AOConnectionPool pool=getPool();

        Connection conn=pool.getConnection(Connection.TRANSACTION_READ_COMMITTED, false);
        try {
            List<String> expiringList=new ArrayList<String>();
            PreparedStatement pstmt=conn.prepareStatement(
                "select " + idColumnName + ", " + maxInactiveIntervalColumnName + ", " + lastAccessedColumnName + " from " + tableName
            );
            try {
                ResultSet results=pstmt.executeQuery();
                try {
                    while(results.next()) {
                        String id = results.getString(1);
                        int maxInactiveInterval = results.getInt(2);
                        long lastAccessed = results.getTimestamp(3).getTime();
                        if (maxInactiveInterval >= 0) {
                            int timeIdle = // Truncate, do not round up
                                (int) ((timeNow - lastAccessed) / 1000L);
                            if (timeIdle >= maxInactiveInterval) expiringList.add(id);
                        }
                    }
                } finally {
                    results.close();
                }
            } finally {
                pstmt.close();
            }

            if(debug>=DEBUG_LEVEL) log("JDBCManager.processExpires() - found " + expiringList.size() + " sessions to expire");

            // Expire and remove the expired sessions
            for(String id : expiringList) {
                expiredSessions++;
                try {
                    JDBCSession loadedSession;
                    synchronized(loadedSessions) {
                        loadedSession=loadedSessions.get(id);
                    }
                    if(loadedSession!=null) {
                        // Remove using normal expire method
                        if(debug>=DEBUG_LEVEL) log("JDBCManager.processExpires() - expiring loaded session: "+id);
                        loadedSession.expire();
                        synchronized(loadedSessions) {
                            loadedSessions.remove(id);
                        }
                    } else {
                        // Remove from database directly
                        if(debug>=DEBUG_LEVEL) log("JDBCManager.processExpires() - removing session from database: "+id);
                        removeSession(id, conn);
                    }
                } catch (Throwable t) {
                    log(sm.getString("standardManager.expireException"), t);
                }
            }

            // Commit to db
            conn.commit();
        } catch(SQLException err) {
            if(!conn.isClosed()) {
                conn.rollback();
                conn.close();
            }
            throw err;
        } finally {
            pool.releaseConnection(conn);
        }
        if(debug>=DEBUG_LEVEL) log("JDBCManager.processExpires() - method ending");
    }

    /**
     * Sleep for the duration specified by the <code>checkInterval</code>
     * property.
     */
    private void threadSleep() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.threadSleep()");

        try {
            Thread.sleep(checkInterval * 1000L);
        } catch (InterruptedException e) {
            ;
        }

    }

    /**
     * Start the background thread that will periodically check for
     * session timeouts.
     */
    private void threadStart() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.threadStart()");

        if (thread != null)
            return;

        threadDone = false;
        threadName = "JDBCManager[" + container.getName() + "]";
        thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.setContextClassLoader(container.getLoader().getClassLoader());
        thread.start();

    }

    /**
     * Stop the background thread that is periodically checking for
     * session timeouts.
     */
    private void threadStop() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.threadStop()");

        if (thread == null)
            return;

        threadDone = true;
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            ;
        }

        thread = null;

    }


    // ------------------------------------------------------ Background Thread


    /**
     * The background thread that checks for session timeouts and shutdown.
     */
    public void run() {
        if(debug>=DEBUG_LEVEL) log("JDBCManager.run() - method starting");

        // Loop until the termination semaphore is set
        while (!threadDone) {
            threadSleep();
            try {
                processExpires();
            } catch(SQLException err) {
                reportError(err, null);
            }
        }

        if(debug>=DEBUG_LEVEL) log("JDBCManager.run() - method ending");
    }
}
