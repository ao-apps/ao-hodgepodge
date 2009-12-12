/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.apache.catalina.jdbc4_1_31;

/*
 * This code is partially derived from org.apache.catalina.Session and
 * org.apache.catalina.session.StandardSession.
 * For this reason we make this code available to everybody in the aocode-public
 * package.  Ultimately, we would like to submit this code to Apache for inclusion
 * in their Tomcat distribution.
 */
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.session.Constants;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.catalina.util.Enumerator;
import org.apache.catalina.util.StringManager;


/**
 * Implementation of the <code>Session</code> interface that stores its attributes immediately
 * to the underlying database.
 *
 * @author  AO Industries, Inc.
 */
public class JDBCSession implements HttpSession, Session {

    private static final int DEBUG_LEVEL=2;

    /**
     * Construct a new Session associated with the specified Manager.
     *
     * @param manager The manager with which this Session is associated
     */
    public JDBCSession(Manager manager) {
        super();
        this.manager = manager;
        if (manager instanceof ManagerBase)
            this.debug = ((ManagerBase) manager).getDebug();
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].init<Manager>");
    }

    /**
     * The collection of user data attributes associated with this Session.
     */
    private Map<String,Object> attributes = new HashMap<String,Object>();

    /**
     * The authentication type used to authenticate our cached Principal,
     * if any.  NOTE:  This value is not included in the serialized
     * version of this object.
     */
    private transient String authType = null;

    /**
     * The <code>java.lang.Method</code> for the
     * <code>fireContainerEvent()</code> method of the
     * <code>org.apache.catalina.core.StandardContext</code> method,
     * if our Context implementation is of this class.  This value is
     * computed dynamically the first time it is needed, or after
     * a session reload (since it is declared transient).
     */
    private transient Method containerEventMethod = null;

    /**
     * The method signature for the <code>fireContainerEvent</code> method.
     */
    private static final Class containerEventTypes[] = { String.class, Object.class };

    /**
     * The time this session was created, in milliseconds since midnight,
     * January 1, 1970 GMT.
     */
    private long creationTime = 0L;

    /**
     * The debugging detail level for this component.  NOTE:  This value
     * is not included in the serialized version of this object.
     */
    private transient int debug = 0;

    /**
     * We are currently processing a session expiration, so bypass
     * certain IllegalStateException tests.  NOTE:  This value is not
     * included in the serialized version of this object.
     */
    private transient boolean expiring = false;
    
    /**
     * The session identifier of this Session.
     */
    private String id = null;


    /**
     * Descriptive information describing this Session implementation.
     */
    private static final String info = "JDBCSession/1.0";


    /**
     * The last accessed time for this Session.
     */
    private long lastAccessedTime = creationTime;


    /**
     * The session event listeners for this Session.
     */
    private transient List<SessionListener> listeners = new ArrayList<SessionListener>();


    /**
     * The Manager with which this Session is associated.
     */
    private Manager manager = null;


    /**
     * The maximum time interval, in seconds, between client requests before
     * the servlet container may invalidate this session.  A negative time
     * indicates that the session should never time out.
     */
    private int maxInactiveInterval = -1;


    /**
     * Flag indicating whether this session is new or not.
     */
    private boolean isNew = false;


    /**
     * Flag indicating whether this session is valid or not.
     */
    private boolean isValid = false;


    /**
     * Internal notes associated with this session by Catalina components
     * and event listeners.  <b>IMPLEMENTATION NOTE:</b> This object is
     * <em>not</em> saved and restored across session serializations!
     */
    private transient Map<String,Object> notes = new HashMap<String,Object>();


    /**
     * The authenticated Principal associated with this session, if any.
     * <b>IMPLEMENTATION NOTE:</b>  This object is <i>not</i> saved and
     * restored across session serializations!
     */
    private transient Principal principal = null;


    /**
     * The string manager for this package.
     */
    private static StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * The property change support for this component.  NOTE:  This value
     * is not included in the serialized version of this object.
     */
    private transient PropertyChangeSupport support =
        new PropertyChangeSupport(this);


    void init(
        String id,
        int maxInactiveInterval,
        boolean valid,
        long creationTime,
        long lastAccessed,
        InputStream attributesIn
    ) throws IOException, ClassNotFoundException {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].init(String,int,boolean,long,long,InputStream)");
        Container container=manager==null ? null : manager.getContainer();
        Loader loader=container==null ? null : container.getLoader();
        ClassLoader classloader=loader==null ? null : loader.getClassLoader();
        ObjectInputStream ois;
        if(classloader!=null) ois = new CustomObjectInputStream(attributesIn, classloader);
        else ois = new ObjectInputStream(attributesIn);
        try {
            attributes.clear();
            while(ois.readBoolean()) {
                String key=ois.readUTF();
                Object value=ois.readObject();
                attributes.put(key, value);
            }
        } finally {
            ois.close();
        }
        
        this.containerEventMethod=null;
        this.creationTime=creationTime;
        this.id=id;
        this.lastAccessedTime=lastAccessed;
        this.listeners.clear();
        this.maxInactiveInterval=maxInactiveInterval;
        this.isNew=false;
        this.isValid=valid;
    }

    // ----------------------------------------------------- Session Properties

    /**
     * Return the authentication type used to authenticate our cached
     * Principal, if any.
     */
    public String getAuthType() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getAuthType()");

        return (this.authType);

    }


    /**
     * Set the authentication type used to authenticate our cached
     * Principal, if any.
     *
     * @param authType The new cached authentication type
     */
    public void setAuthType(String authType) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setAuthType(String)");

        String oldAuthType = this.authType;
        this.authType = authType;
        support.firePropertyChange("authType", oldAuthType, this.authType);

    }


    /**
     * Set the creation time for this session.  This method is called by the
     * Manager when an existing Session instance is reused.
     *
     * @param time The new creation time
     */
    public void setCreationTime(long time) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setCreationTime(long)");

        this.creationTime = time;
        this.lastAccessedTime = time;
    }


    /**
     * Return the session identifier for this session.
     */
    public String getId() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getId()");

        return (this.id);

    }


    /**
     * Set the session identifier for this session.
     *
     * @param id The new session identifier
     * @param conn the currently active Connection
     */
    void setId(String id, Connection conn) throws SQLException, IOException {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setId(String,Connection)");

        if ((this.id != null) && (manager != null)) {
            if(manager instanceof JDBCManager) ((JDBCManager)manager).remove(this, conn);
            else manager.remove(this);
        }

        this.id = id;

        if (manager != null) {
            if(manager instanceof JDBCManager) ((JDBCManager)manager).add(this, conn);
            else manager.add(this);
        }
        tellNew();
    }

    /**
     * Set the session identifier for this session.
     *
     * @param id The new session identifier
     */
    public void setId(String id) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setId(String)");

        if ((this.id != null) && (manager != null))
            manager.remove(this);

        this.id = id;

        if (manager != null)
            manager.add(this);
        tellNew();
    }

    /**
     * Inform the listener about the new session.
     *
     */
    public void tellNew() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].tellNew()");

        // Notify interested session event listeners
        fireSessionEvent(Session.SESSION_CREATED_EVENT, null);

        // Notify interested application event listeners
        Context context = (Context) manager.getContainer();
        Object listeners[] = context.getApplicationListeners();
        if (listeners != null) {
            HttpSessionEvent event =
                new HttpSessionEvent(getSession());
            for (int i = 0; i < listeners.length; i++) {
                if (!(listeners[i] instanceof HttpSessionListener))
                    continue;
                HttpSessionListener listener =
                    (HttpSessionListener) listeners[i];
                try {
                    fireContainerEvent(context,
                                       "beforeSessionCreated",
                                       listener);
                    listener.sessionCreated(event);
                    fireContainerEvent(context,
                                       "afterSessionCreated",
                                       listener);
                } catch (Throwable t) {
                    try {
                        fireContainerEvent(context,
                                           "afterSessionCreated",
                                           listener);
                    } catch (Exception e) {
                        ;
                    }
                    // FIXME - should we do anything besides log these?
                    log(sm.getString("standardSession.sessionEvent"), t);
                }
            }
        }

    }


    /**
     * Return descriptive information about this Session implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getInfo()");

        return (this.info);

    }


    /**
     * Return the last time the client sent a request associated with this
     * session, as the number of milliseconds since midnight, January 1, 1970
     * GMT.  Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access time.
     */
    public long getLastAccessedTime() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getLastAccessedTime()");

        return (this.lastAccessedTime);

    }


    /**
     * Return the Manager within which this Session is valid.
     */
    public Manager getManager() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getManager()");

        return (this.manager);

    }


    /**
     * Set the Manager within which this Session is valid.
     *
     * @param manager The new Manager
     */
    public void setManager(Manager manager) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setManager(Manager)");

        this.manager = manager;

    }


    /**
     * Return the maximum time interval, in seconds, between client requests
     * before the servlet container will invalidate the session.  A negative
     * time indicates that the session should never time out.
     */
    public int getMaxInactiveInterval() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getMaxInactiveInterval()");

        return (this.maxInactiveInterval);

    }


    /**
     * Set the maximum time interval, in seconds, between client requests
     * before the servlet container will invalidate the session.  A negative
     * time indicates that the session should never time out.
     *
     * @param interval The new maximum interval
     * @param updateDB when true will update the underlying database
     */
    void setMaxInactiveInterval(int interval, boolean updateDB) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setMaxInactiveInterval(int,boolean)");
        this.maxInactiveInterval = interval;
        if(updateDB && manager!=null && (manager instanceof JDBCManager)) {
            JDBCManager jdbcMan=(JDBCManager)manager;
            jdbcMan.setMaxInactiveInterval(id, interval);
        }
    }

    /**
     * Set the maximum time interval, in seconds, between client requests
     * before the servlet container will invalidate the session.  A negative
     * time indicates that the session should never time out.
     *
     * @param interval The new maximum interval
     */
    public void setMaxInactiveInterval(int interval) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setMaxInactiveInterval(int)");
        setMaxInactiveInterval(interval, true);
    }


    /**
     * Set the <code>isNew</code> flag for this session.
     *
     * @param isNew The new value for the <code>isNew</code> flag
     */
    public void setNew(boolean isNew) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setNew(boolean)");
        this.isNew = isNew;
    }


    /**
     * Return the authenticated Principal that is associated with this Session.
     * This provides an <code>Authenticator</code> with a means to cache a
     * previously authenticated Principal, and avoid potentially expensive
     * <code>Realm.authenticate()</code> calls on every request.  If there
     * is no current associated Principal, return <code>null</code>.
     */
    public Principal getPrincipal() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getPrincipal()");

        return (this.principal);

    }


    /**
     * Set the authenticated Principal that is associated with this Session.
     * This provides an <code>Authenticator</code> with a means to cache a
     * previously authenticated Principal, and avoid potentially expensive
     * <code>Realm.authenticate()</code> calls on every request.
     *
     * @param principal The new Principal, or <code>null</code> if none
     */
    public void setPrincipal(Principal principal) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setPrincipal(Principal)");

        Principal oldPrincipal = this.principal;
        this.principal = principal;
        support.firePropertyChange("principal", oldPrincipal, this.principal);

    }


    public HttpSession getSession() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getSession()");
        return this;
    }


    /**
     * Return the <code>isValid</code> flag for this session.
     */
    public boolean isValid() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].isValid()");

        return (this.isValid);

    }


    /**
     * Set the <code>isValid</code> flag for this session.
     *
     * @param isValid The new value for the <code>isValid</code> flag
     * @param updateDB when true will update the underlying database
     */
    void setValid(boolean isValid, boolean updateDB) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setValid(boolean,boolean)");
        this.isValid = isValid;
        if(updateDB && manager!=null && (manager instanceof JDBCManager)) {
            JDBCManager jdbcMan=(JDBCManager)manager;
            jdbcMan.setValid(id, isValid);
        }
    }

    /**
     * Set the <code>isValid</code> flag for this session.
     *
     * @param isValid The new value for the <code>isValid</code> flag
     */
    public void setValid(boolean isValid) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setValid(boolean)");
        setValid(isValid, true);
    }


    // ------------------------------------------------- Session Public Methods


    /**
     * Update the accessed time information for this session.  This method
     * should be called by the context when a request comes in for a particular
     * session, even if the application does not reference it.
     */
    public void access() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].access()");
        this.isNew = false;
        this.lastAccessedTime = System.currentTimeMillis();

        // Update the db
        if(manager!=null && manager instanceof JDBCManager) {
            JDBCManager jdbcMan=(JDBCManager)manager;
            try {
                jdbcMan.setLastAccessedTime(id, this.lastAccessedTime);
            } catch(SQLException err) {
                jdbcMan.reportError(err, null);
            }
        }
    }


    /**
     * Add a session event listener to this component.
     */
    public void addSessionListener(SessionListener listener) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].addSessionListener(SessionListener)");

        synchronized (listeners) {
            listeners.add(listener);
        }

    }


    /**
     * Perform the internal processing required to invalidate this session,
     * without triggering an exception if the session has already expired.
     */
    public void expire() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].expire()");
        expire(true);
    }


    /**
     * Perform the internal processing required to invalidate this session,
     * without triggering an exception if the session has already expired.
     *
     * @param notify Should we notify listeners about the demise of
     *  this session?
     */
    public void expire(boolean notify) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].expire(boolean)");

        // Mark this session as "being expired" if needed
        if (expiring)
            return;
        expiring = true;
        setValid(false);

        // Remove this session from our manager's active sessions
        if (manager != null)
            manager.remove(this);

        // Unbind any objects associated with this session
        String keys[] = keys();
        for (int i = 0; i < keys.length; i++)
            removeAttribute(keys[i], notify);

        // Notify interested session event listeners
        if (notify) {
            fireSessionEvent(Session.SESSION_DESTROYED_EVENT, null);
        }

        // Notify interested application event listeners
        // FIXME - Assumes we call listeners in reverse order
        Context context = (Context) manager.getContainer();
        Object listeners[] = context.getApplicationListeners();
        if (notify && (listeners != null)) {
            HttpSessionEvent event =
              new HttpSessionEvent(getSession());
            for (int i = 0; i < listeners.length; i++) {
                int j = (listeners.length - 1) - i;
                if (!(listeners[j] instanceof HttpSessionListener))
                    continue;
                HttpSessionListener listener =
                    (HttpSessionListener) listeners[j];
                try {
                    fireContainerEvent(context,
                                       "beforeSessionDestroyed",
                                       listener);
                    listener.sessionDestroyed(event);
                    fireContainerEvent(context,
                                       "afterSessionDestroyed",
                                       listener);
                } catch (Throwable t) {
                    try {
                        fireContainerEvent(context,
                                           "afterSessionDestroyed",
                                           listener);
                    } catch (Exception e) {
                        ;
                    }
                    // FIXME - should we do anything besides log these?
                    log(sm.getString("standardSession.sessionEvent"), t);
                }
            }
        }

        // We have completed expire of this session
        expiring = false;
    }


    /**
     * Perform the internal processing required to passivate
     * this session.
     */
    public void passivate() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].passivate()");

        // Notify ActivationListeners
        HttpSessionEvent event = null;
        String keys[] = keys();
        for (int i = 0; i < keys.length; i++) {
            Object attribute = getAttribute(keys[i]);
            if (attribute instanceof HttpSessionActivationListener) {
                if (event == null)
                    event = new HttpSessionEvent(this);
                try {
                    ((HttpSessionActivationListener)attribute).sessionWillPassivate(event);
                } catch (Throwable t) {
                    log(sm.getString("standardSession.attributeEvent"), t);
                }
            }
        }
    }


    /**
     * Perform internal processing required to activate this
     * session.
     */
    public void activate() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].activate()");
        // Notify ActivationListeners
        HttpSessionEvent event = null;
        String keys[] = keys();
        for (int i = 0; i < keys.length; i++) {
            Object attribute = getAttribute(keys[i]);
            if (attribute instanceof HttpSessionActivationListener) {
                if (event == null)
                    event = new HttpSessionEvent(this);
                try {
                    ((HttpSessionActivationListener)attribute).sessionDidActivate(event);
                } catch (Throwable t) {
                    log(sm.getString("standardSession.attributeEvent"), t);
                }
            }
        }
    }

    /**
     * Return the object bound with the specified name to the internal notes
     * for this session, or <code>null</code> if no such binding exists.
     *
     * @param name Name of the note to be returned
     */
    public Object getNote(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getNote(String)");

        synchronized (notes) {
            return notes.get(name);
        }

    }


    /**
     * Return an Iterator containing the String names of all notes bindings
     * that exist for this session.
     */
    public Iterator getNoteNames() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getNoteNames()");

        synchronized (notes) {
            return notes.keySet().iterator();
        }

    }


    /**
     * Release all object references, and initialize instance variables, in
     * preparation for reuse of this object.
     */
    public void recycle() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].recycle()");

        // Reset the instance variables associated with this Session
        //attributesBytes=null;
        attributes.clear();
        setAuthType(null);
        creationTime = 0L;
        expiring = false;
        id = null;
        lastAccessedTime = 0L;
        maxInactiveInterval = -1;
        notes.clear();
        setPrincipal(null);
        isNew = false;
        isValid = false;
        manager = null;
    }


    /**
     * Remove any object bound to the specified name in the internal notes
     * for this session.
     *
     * @param name Name of the note to be removed
     */
    public void removeNote(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].removeNote(String)");

        synchronized (notes) {
            notes.remove(name);
        }

    }


    /**
     * Remove a session event listener from this component.
     */
    public void removeSessionListener(SessionListener listener) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].removeSessionListener(SessionListener)");

        synchronized (listeners) {
            listeners.remove(listener);
        }

    }


    /**
     * Bind an object to a specified name in the internal notes associated
     * with this session, replacing any existing binding for this name.
     *
     * @param name Name to which the object should be bound
     * @param value Object to be bound to the specified name
     */
    public void setNote(String name, Object value) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setNote(String,Object)");

        synchronized (notes) {
            notes.put(name, value);
        }

    }


    /**
     * Return a string representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JDBCSession[");
        sb.append(id);
        sb.append("]");
        return (sb.toString());

    }


    // ------------------------------------------------- HttpSession Properties


    /**
     * Return the time when this session was created, in milliseconds since
     * midnight, January 1, 1970 GMT.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public long getCreationTime() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getCreationTime()");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.getCreationTime.ise"));

        return (this.creationTime);

    }


    /**
     * Return the ServletContext to which this session belongs.
     */
    public ServletContext getServletContext() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getServletContext()");

        if (manager == null)
            return (null);
        Context context = (Context) manager.getContainer();
        if (context == null)
            return (null);
        else
            return (context.getServletContext());

    }


    /**
     * Return the session context with which this session is associated.
     *
     * @deprecated As of Version 2.1, this method is deprecated and has no
     *  replacement.  It will be removed in a future version of the
     *  Java Servlet API.
     */
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getSessionContext()");
        return new JDBCSessionContext();
    }


    // ----------------------------------------------HttpSession Public Methods


    /**
     * Return the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound with that name.
     *
     * @param name Name of the attribute to be returned
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public Object getAttribute(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getAttribute(\""+name+"\")");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.getAttribute.ise"));

        synchronized (attributes) {
            //decodeAttributes();
            return attributes.get(name);
        }

    }


    /**
     * Return an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of the objects bound to this session.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public Enumeration getAttributeNames() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getAttributeNames()");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.getAttributeNames.ise"));

        synchronized (attributes) {
            //decodeAttributes();
            return (new Enumerator(attributes.keySet(), true));
        }

    }


    /**
     * Return the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound with that name.
     *
     * @param name Name of the value to be returned
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     *
     * @deprecated As of Version 2.2, this method is replaced by
     *  <code>getAttribute()</code>
     */
    public Object getValue(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getValue(String)");

        return (getAttribute(name));

    }


    /**
     * Return the set of names of objects bound to this session.  If there
     * are no such objects, a zero-length array is returned.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     *
     * @deprecated As of Version 2.2, this method is replaced by
     *  <code>getAttributeNames()</code>
     */
    public String[] getValueNames() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].getValueNames()");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.getValueNames.ise"));

        return (keys());

    }


    /**
     * Invalidates this session and unbinds any objects bound to it.
     *
     * @exception IllegalStateException if this method is called on
     *  an invalidated session
     */
    public void invalidate() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].invalidate()");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.invalidate.ise"));

        // Cause this session to expire
        expire();

    }

    /**
     * Return <code>true</code> if the client does not yet know about the
     * session, or if the client chooses not to join the session.  For
     * example, if the server used only cookie-based sessions, and the client
     * has disabled the use of cookies, then a session would be new on each
     * request.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public boolean isNew() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].isNew()");

        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.isNew.ise"));

        return (this.isNew);

    }


    /**
     * Bind an object to this session, using the specified name.  If an object
     * of the same name is already bound to this session, the object is
     * replaced.
     * <p>
     * After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>valueBound()</code> on the object.
     *
     * @param name Name to which the object is bound, cannot be null
     * @param value Object to be bound, cannot be null
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     *
     * @deprecated As of Version 2.2, this method is replaced by
     *  <code>setAttribute()</code>
     */
    public void putValue(String name, Object value) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].putValue(\""+name+"\","+value+")");

        setAttribute(name, value);

    }


    /**
     * Remove the object bound with the specified name from this session.  If
     * the session does not have an object bound with this name, this method
     * does nothing.
     * <p>
     * After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>valueUnbound()</code> on the object.
     *
     * @param name Name of the object to remove from this session.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public void removeAttribute(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].removeAttribute(\""+name+"\")");

        removeAttribute(name, true);

    }


    /**
     * Remove the object bound with the specified name from this session.  If
     * the session does not have an object bound with this name, this method
     * does nothing.
     * <p>
     * After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>valueUnbound()</code> on the object.
     *
     * @param name Name of the object to remove from this session.
     * @param notify Should we notify interested listeners that this
     *  attribute is being removed?
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public void removeAttribute(String name, boolean notify) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].removeAttribute(\""+name+"\","+notify+")");

        // Validate our current state
        if (!expiring && !isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.removeAttribute.ise"));

        // Remove this attribute from our collection
        Object value = null;
        boolean found = false;
        synchronized (attributes) {
            //decodeAttributes();
            found = attributes.containsKey(name);
            if (found) {
                value = attributes.get(name);
                attributes.remove(name);
            } else {
                return;
            }
        }

        if(!expiring && manager!=null && (manager instanceof JDBCManager)) {
            JDBCManager jdbcMan=(JDBCManager)manager;
            jdbcMan.updateAttributes(this);
        }
        // Do we need to do valueUnbound() and attributeRemoved() notification?
        if (!notify) {
            return;
        }

        // Call the valueUnbound() method if necessary
        HttpSessionBindingEvent event =
          new HttpSessionBindingEvent((HttpSession) this, name, value);
        if ((value != null) &&
            (value instanceof HttpSessionBindingListener)) {
            try {
                ((HttpSessionBindingListener) value).valueUnbound(event);
            } catch (Throwable t) {
                log(sm.getString("standardSession.bindingEvent"), t);
            }
        }

        // Notify interested application event listeners
        Context context = (Context) manager.getContainer();
        Object listeners[] = context.getApplicationListeners();
        if (listeners == null)
            return;
        for (int i = 0; i < listeners.length; i++) {
            if (!(listeners[i] instanceof HttpSessionAttributeListener))
                continue;
            HttpSessionAttributeListener listener =
                (HttpSessionAttributeListener) listeners[i];
            try {
                fireContainerEvent(context,
                                   "beforeSessionAttributeRemoved",
                                   listener);
                listener.attributeRemoved(event);
                fireContainerEvent(context,
                                   "afterSessionAttributeRemoved",
                                   listener);
            } catch (Throwable t) {
                try {
                    fireContainerEvent(context,
                                       "afterSessionAttributeRemoved",
                                       listener);
                } catch (Exception e) {
                    ;
                }
                // FIXME - should we do anything besides log these?
                log(sm.getString("standardSession.attributeEvent"), t);
            }
        }

    }


    /**
     * Remove the object bound with the specified name from this session.  If
     * the session does not have an object bound with this name, this method
     * does nothing.
     * <p>
     * After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>valueUnbound()</code> on the object.
     *
     * @param name Name of the object to remove from this session.
     *
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     *
     * @deprecated As of Version 2.2, this method is replaced by
     *  <code>removeAttribute()</code>
     */
    public void removeValue(String name) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].removeValue(\""+name+"\")");

        removeAttribute(name);

    }


    /**
     * Bind an object to this session, using the specified name.  If an object
     * of the same name is already bound to this session, the object is
     * replaced.
     * <p>
     * After this method executes, and if the object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>valueBound()</code> on the object.
     *
     * @param name Name to which the object is bound, cannot be null
     * @param value Object to be bound, cannot be null
     *
     * @exception IllegalArgumentException if an attempt is made to add a
     *  non-serializable object in an environment marked distributable.
     * @exception IllegalStateException if this method is called on an
     *  invalidated session
     */
    public void setAttribute(String name, Object value) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].setAttribute(\""+name+"\","+value+")");

        // Name cannot be null
        if (name == null)
            throw new IllegalArgumentException
                (sm.getString("standardSession.setAttribute.namenull"));

        // Null value is the same as removeAttribute()
        if (value == null) {
            removeAttribute(name);
            return;
        }

        // Validate our current state
        if (!isValid)
            throw new IllegalStateException
                (sm.getString("standardSession.setAttribute.ise"));
        if ((manager != null) && manager.getDistributable() &&
          !(value instanceof Serializable))
            throw new IllegalArgumentException
                (sm.getString("standardSession.setAttribute.iae"));

        // Construct an event with the new value
        HttpSessionBindingEvent event = new HttpSessionBindingEvent
                ((HttpSession) this, name, value);

        // Call the valueBound() method if necessary
        if (value instanceof HttpSessionBindingListener) {
            try {
                ((HttpSessionBindingListener) value).valueBound(event);
            } catch (Throwable t) {
                log(sm.getString("standardSession.bindingEvent"), t);
            }
        }

        // Replace or add this attribute
        Object unbound = null;
        synchronized (attributes) {
            //decodeAttributes();
            unbound = attributes.get(name);
            attributes.put(name, value);
        }
        
        // Update the database
        if(manager!=null && (manager instanceof JDBCManager)) {
            JDBCManager jdbcMan=(JDBCManager)manager;
            jdbcMan.updateAttributes(this);
        }

        // Call the valueUnbound() method if necessary
        if ((unbound != null) &&
                (unbound instanceof HttpSessionBindingListener)) {
            try {
                ((HttpSessionBindingListener) unbound).valueUnbound
                        (new HttpSessionBindingEvent((HttpSession) this, name));
            } catch (Throwable t) {
                log(sm.getString("standardSession.bindingEvent"), t); 
            }
        }

        // Replace the current event with one containing 
        // the old value if necesary
        if (unbound != null)
            event = new HttpSessionBindingEvent((HttpSession) this,
                                                name, unbound);

        // Notify interested application event listeners
        Context context = (Context) manager.getContainer();
        Object listeners[] = context.getApplicationListeners();
        if (listeners == null)
            return;
        for (int i = 0; i < listeners.length; i++) {
            if (!(listeners[i] instanceof HttpSessionAttributeListener))
                continue;
            HttpSessionAttributeListener listener =
                (HttpSessionAttributeListener) listeners[i];
            try {
                if (unbound != null) {
                    fireContainerEvent(context,
                                       "beforeSessionAttributeReplaced",
                                       listener);
                    listener.attributeReplaced(event);
                    fireContainerEvent(context,
                                       "afterSessionAttributeReplaced",
                                       listener);
                } else {
                    fireContainerEvent(context,
                                       "beforeSessionAttributeAdded",
                                       listener);
                    listener.attributeAdded(event);
                    fireContainerEvent(context,
                                       "afterSessionAttributeAdded",
                                       listener);
                }
            } catch (Throwable t) {
                try {
                    if (unbound != null) {
                        fireContainerEvent(context,
                                           "afterSessionAttributeReplaced",
                                           listener);
                    } else {
                        fireContainerEvent(context,
                                           "afterSessionAttributeAdded",
                                           listener);
                    }
                } catch (Exception e) {
                    ;
                }
                // FIXME - should we do anything besides log these?
                log(sm.getString("standardSession.attributeEvent"), t);
            }
        }

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Fire container events if the Context implementation is the
     * <code>org.apache.catalina.core.StandardContext</code>.
     *
     * @param context Context for which to fire events
     * @param type Event type
     * @param data Event data
     *
     * @exception Exception occurred during event firing
     */
    private void fireContainerEvent(Context context,
                                    String type, Object data)
        throws Exception {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].fireContainerEvent(Context,String,Object)");

        if (!"org.apache.catalina.core.StandardContext".equals
            (context.getClass().getName())) {
            return; // Container events are not supported
        }
        // NOTE:  Race condition is harmless, so do not synchronize
        if (containerEventMethod == null) {
            containerEventMethod =
                context.getClass().getMethod("fireContainerEvent",
                                             containerEventTypes);
        }
        Object containerEventParams[] = new Object[2];
        containerEventParams[0] = type;
        containerEventParams[1] = data;
        containerEventMethod.invoke(context, containerEventParams);

    }
                                      


    /**
     * Notify all session event listeners that a particular event has
     * occurred for this Session.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param data Event data
     */
    public void fireSessionEvent(String type, Object data) {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].fireSessionEvent(String,Object)");

        if (listeners.isEmpty()) return;
        SessionEvent event = new SessionEvent(this, type, data);
        SessionListener list[] = new SessionListener[0];
        synchronized (listeners) {
            list = (SessionListener[]) listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++)
            ((SessionListener) list[i]).sessionEvent(event);

    }


    /**
     * Return the names of all currently defined session attributes
     * as an array of Strings.  If there are no defined attributes, a
     * zero-length array is returned.
     */
    private String[] keys() {
        if(debug>=DEBUG_LEVEL) log("JDBCSession["+id+"].keys()");

        synchronized (attributes) {
            //decodeAttributes();
            Set<String> keySet = attributes.keySet();
            String[] keys = new String[keySet.size()];
            keySet.toArray(keys);
            return keys;
        }

    }


    /**
     * Log a message on the Logger associated with our Manager (if any).
     *
     * @param message Message to be logged
     */
    private void log(String message) {

        if ((manager != null) && (manager instanceof JDBCManager)) {
            ((JDBCManager) manager).log(message);
        } else {
            System.out.println("JDBCSession: " + message);
        }

    }


    /**
     * Log a message on the Logger associated with our Manager (if any).
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    private void log(String message, Throwable throwable) {

        if ((manager != null) && (manager instanceof JDBCManager)) {
            ((JDBCManager) manager).log(message, throwable);
        } else {
            System.out.println("JDBCSession: " + message);
            throwable.printStackTrace(System.out);
        }

    }


}


// -------------------------------------------------------------- Private Class


/**
 * This class is a dummy implementation of the <code>HttpSessionContext</code>
 * interface, to conform to the requirement that such an object be returned
 * when <code>HttpSession.getSessionContext()</code> is called.
 *
 * @author Craig R. McClanahan
 *
 * @deprecated As of Java Servlet API 2.1 with no replacement.  The
 *  interface will be removed in a future version of this API.
 */

final class JDBCSessionContext implements javax.servlet.http.HttpSessionContext {


    private static final HashMap dummy = new HashMap();

    /**
     * Return the session identifiers of all sessions defined
     * within this context.
     *
     * @deprecated As of Java Servlet API 2.1 with no replacement.
     *  This method must return an empty <code>Enumeration</code>
     *  and will be removed in a future version of the API.
     */
    public Enumeration getIds() {

        return (new Enumerator(dummy));

    }


    /**
     * Return the <code>HttpSession</code> associated with the
     * specified session identifier.
     *
     * @param id Session identifier for which to look up a session
     *
     * @deprecated As of Java Servlet API 2.1 with no replacement.
     *  This method must return null and will be removed in a
     *  future version of the API.
     */
    public HttpSession getSession(String id) {

        return (null);

    }
}
