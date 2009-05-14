package com.aoindustries.servlet.http;
/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class ServletUtilTest extends TestCase {

    public ServletUtilTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ServletUtilTest.class);
        return suite;
    }

    public void testGetAbsolutePath() throws Exception {
        assertEquals("/test/", ServletUtil.getAbsolutePath("/test/page.jsp", "./"));
        assertEquals("/test/other.jsp", ServletUtil.getAbsolutePath("/test/subdir/page.jsp", "/test/other.jsp"));
        assertEquals("/test/other.jsp", ServletUtil.getAbsolutePath("/test/subdir/page.jsp", "../other.jsp"));
        assertEquals("/test/other.jsp", ServletUtil.getAbsolutePath("/test/subdir/page.jsp", "./.././other.jsp"));
        assertEquals("/test/subdir/other.jsp", ServletUtil.getAbsolutePath("/test/page.jsp", "subdir/other.jsp"));
        assertEquals("/test/other.jsp", ServletUtil.getAbsolutePath("/test/page.jsp", "other.jsp"));
        assertEquals("/other.jsp", ServletUtil.getAbsolutePath("/page.jsp", "other.jsp"));
    }
}
