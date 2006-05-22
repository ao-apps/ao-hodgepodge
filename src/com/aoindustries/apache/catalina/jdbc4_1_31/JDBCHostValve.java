package com.aoindustries.apache.catalina.jdbc4_1_31;

/*
 * This code is partially derived from org.apache.catalina.valves.ValveBase
 * and org.apache.catalina.core.StandardHostValve.
 * For this reason we make this code available to everybody in the aocode-public
 * package.  Ultimately, we would like to submit this code to Apache for inclusion
 * in their Tomcat distribution.
 *
 * By AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.ValveContext;
import org.apache.catalina.core.Constants;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

/**
 * Copied from StandardHostValve because StandardHostValve is final.
 *
 * @see  JDBCHostValve
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class JDBCHostValve extends ValveBase {

    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "com.aoindustries.apache.catalina.jdbc.JDBCHostValve/1.0";


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Select the appropriate child Context to process this request,
     * based on the specified request URI.  If no matching Context can
     * be found, return an appropriate HTTP error.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     * @param valveContext Valve context used to forward to the next Valve
     *
     * @exception IOException if an input/output error occurred
     * @exception ServletException if a servlet error occurred
     */
    public void invoke(Request request, Response response,
                       ValveContext valveContext)
        throws IOException, ServletException {

        // Validate the request and response object types
        if (!(request.getRequest() instanceof HttpServletRequest) ||
            !(response.getResponse() instanceof HttpServletResponse)) {
            return;     // NOTE - Not much else we can do generically
        }

        // Select the Context to be used for this Request
        JDBCHost host = (JDBCHost) getContainer();
        Context context = (Context) host.map(request, true);
        if (context == null) {
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 sm.getString("standardHost.noContext"));
            return;
        }

        // Bind the context CL to the current thread
        Thread.currentThread().setContextClassLoader
            (context.getLoader().getClassLoader());

        // Update the session last access time for our session (if any)
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String sessionId = hreq.getRequestedSessionId();
        Manager manager = context.getManager();
        JDBCManager jdbcMan = (manager!=null && manager instanceof JDBCManager) ? (JDBCManager)manager : null;
        try {
            if(manager != null && sessionId != null) {
                if(jdbcMan != null) jdbcMan.incrementRequestCounter(sessionId);
                Session session = manager.findSession(sessionId);
                if ((session != null) && session.isValid()) session.access();
            }

            // Ask this Context to process this request
            context.invoke(request, response);
        } finally {
            if (jdbcMan != null) {
                if(sessionId != null) jdbcMan.decrementRequestCounter(sessionId);
                jdbcMan.passivateNewSessions();
            }
        }
        Thread.currentThread().setContextClassLoader
            (JDBCHostValve.class.getClassLoader());
    }
}
