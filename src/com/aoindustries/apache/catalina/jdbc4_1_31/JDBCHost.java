package com.aoindustries.apache.catalina.jdbc4_1_31;

/*
 * This code is partially derived from org.apache.catalina.core.StandardHost.
 * For this reason we make this code available to everybody in the aocode-public
 * package.  Ultimately, we would like to submit this code to Apache for inclusion
 * in their Tomcat distribution.
 *
 * By AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import org.apache.catalina.core.StandardHost;

/**
 * Overrides StandardHost in order to use the JDBCHostValve instead of StandardHostValve.
 *
 * @see  JDBCHostValve
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */

public class JDBCHost extends StandardHost {

    public JDBCHost() {
        super();
        pipeline.setBasic(new JDBCHostValve());
    }
}
