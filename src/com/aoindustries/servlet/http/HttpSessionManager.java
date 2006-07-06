package com.aoindustries.servlet.http;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * Allows a session to be maintained between different hostnames, domains, or protocols by simply passing the
 * session ID as a URL parameter when switching hostnames, domains, or protocols.  Instead of directly
 * using <code>HttpServletRequest.getSession()</code>, call the static <code>getSession</code> method of this class.
 * When switch hostnames, domains, or protocols, add the URL parameter like this:
 * <pre>
 * // To switch to the shared secure server from a Java Servlet
 * HttpSession session=HttpSessionManager.getSession(request, response);
 * out.print("&lt;A href='https://secure.aoindustries.com/domain.com/.....?jsessionid=");
 * out.print(session.getId());
 * out.print("'&gt;Secure Server&lt;/A&gt;");
 *
 * // To switch to the normal server from a Java Servlet
 * HttpSession session=HttpSessionManager.getSession(request, response);
 * out.print("&lt;A href='http://domain.com/.....?jessionid=");
 * out.print(session.getId());
 * out.print("'&gt;Normal Server&lt;/A&gt;");
 *
 * // To switch to the shared secure server from a JSP file
 * &lt;A href='https://secure.aoindustries.com/domain.com/.....?jsessionid=&lt;%= HttpSessionManager.getSession(request, response).getId() %&gt;'&gt;Secure Server&lt;/A&gt;
 *
 * // To switch to the normal server from a JSP file
 * &lt;A href='http://domain.com/.....?jsessionid=&lt;%= HttpSessionManager.getSession(request, response).getId() %&gt;'&gt;Normal Server&lt;/A&gt;
 * </pre>
 * The use of the JSESSIONID cookie may be specific to the Tomcat
 * servlet container.  Code portability is not guaranteed.
 *
 * @author  AO Industries, Inc.
 */
final public class HttpSessionManager {

    /**
     * The cleanup interval in milliseconds.
     */
    public static final long CLEANUP_INTERVAL=(long)5*60*1000;

    /**
     * Because the servlet API no longer supports retrieving sessions by session ID,
     * a cache of used sessions is maintained.
     */
    private static final Map<String,HttpSession> sessions=new HashMap<String,HttpSession>();
    
    /**
     * The old sessions are cleaned-up about every 5 minutes.
     */
    private static long lastCleanup=-1;

    public static synchronized HttpSession getSession(HttpServletRequest req, HttpServletResponse resp) {
        Profiler.startProfile(Profiler.FAST, HttpSessionManager.class, "getSession(HttpServletRequest,HttpServletResponse)", null);
        try {
            // Cleanup expired sessions once every CLEANUP_INTERVAL
            long currentTime=System.currentTimeMillis();
            long timeSince=currentTime-lastCleanup;
            if(lastCleanup==-1 || timeSince<0 || timeSince>=CLEANUP_INTERVAL) {
                // A list of keys to remove is created to avoid updating the HashMap while reading its Iterator
                List<String> removeList=new ArrayList<String>();

                // Iterate through all sessions
                Iterator<String> keys=sessions.keySet().iterator();
                while(keys.hasNext()) {
                    String key=keys.next();
                    HttpSession session=sessions.get(key);
                    // Negative times indicate sessions never expire
                    int maxInactiveInterval=session.getMaxInactiveInterval();
                    if(maxInactiveInterval>=0) {
                        long sessionTimeSince=currentTime-session.getLastAccessedTime();
                        if(sessionTimeSince<0 || sessionTimeSince>=((long)maxInactiveInterval*1000)) removeList.add(key);
                    }
                }

                // Remove all keys that are in the removeList
                int size=removeList.size();
                for(int c=0;c<size;c++) sessions.remove(removeList.get(c));

                // Reset interval timer
                lastCleanup=currentTime;
            }
            
            // Find or create new session
            String jsessionid=req.getParameter("jsessionid");
            if(jsessionid!=null && (jsessionid=jsessionid.trim()).length()>0) {
                HttpSession session=sessions.get(jsessionid);
                if(session!=null) {
                    // The use of the JSESSIONID cookie may be specific to the Tomcat
                    // servlet container.  Code portability is not guaranteed.
                    resp.addCookie(new Cookie("JSESSIONID", jsessionid));
                    return session;
                }
            }
            HttpSession session=req.getSession();
            sessions.put(session.getId(), session);
            return session;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}