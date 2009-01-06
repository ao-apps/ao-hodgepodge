package com.aoindustries.io;
/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import com.aoindustries.io.unix.UnixFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the filesystem iterator.
 *
 * @author  AO Industries, Inc.
 */
public class FilesystemIteratorTest extends TestCase {

    public FilesystemIteratorTest(String testName) {
        super(testName);
    }

    private UnixFile tempDir;

    @Override
    protected void setUp() throws Exception {
        tempDir = UnixFile.mktemp("/home/o/orion/tmp/FilesystemIteratorTest.", true);
        tempDir.delete();
        tempDir.mkdir(false, 0700);
        UnixFile tmp = new UnixFile(tempDir, "tmp", true).mkdir(false, 0755);
        UnixFile tmp_something = new UnixFile(tmp, "something", true);
        new FileOutputStream(tmp_something.getFile()).close();
        tmp_something.setMode(0644);
        UnixFile home = new UnixFile(tempDir, "home", true).mkdir(false, 0755);
        UnixFile home_a = new UnixFile(home, "a", true).mkdir(false, 0755);
        UnixFile home_a_aoadmin = new UnixFile(home_a, "aoadmin", true).mkdir(false, 0700);
        UnixFile home_a_aoadmin_something = new UnixFile(home_a_aoadmin, "something", true);
        new FileOutputStream(home_a_aoadmin_something.getFile()).close();
        home_a_aoadmin_something.setMode(0600);
        UnixFile home_a_aoadmin_something2 = new UnixFile(home_a_aoadmin, "something2", true);
        new FileOutputStream(home_a_aoadmin_something2.getFile()).close();
        home_a_aoadmin_something2.setMode(0644);
        UnixFile home_a_aoadmin_badlink = new UnixFile(home_a_aoadmin, "badlink", true);
        home_a_aoadmin_badlink.symLink("../aoadmin");
        UnixFile home_a_aoadmin_brokenlink = new UnixFile(home_a_aoadmin, "brokenlink", true);
        home_a_aoadmin_brokenlink.symLink("brokenlinknotarget");
    }

    @Override
    protected void tearDown() throws Exception {
        tempDir.deleteRecursive();
        tempDir = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FilesystemIteratorTest.class);
        return suite;
    }

    /**
     * Without any rules the iterator should not return anything (defaults to exclude all).
     */
    public void testIteratorNone() throws IOException {
        Map<String,FilesystemIteratorRule> rules = Collections.emptyMap();
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }
    
    public void testIterateAll() throws IOException {
        Map<String,FilesystemIteratorRule> rules = Collections.singletonMap(tempDir.getFilename(), FilesystemIteratorRule.OK);
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        expectedResults.add("/");
        expectedResults.add("/home");
        expectedResults.add("/home/o");
        expectedResults.add("/home/o/orion");
        expectedResults.add("/home/o/orion/tmp");
        expectedResults.add(tempDir.getFilename());
        expectedResults.add(tempDir.getFilename()+"/home");
        expectedResults.add(tempDir.getFilename()+"/home/a");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/badlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/brokenlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something2");
        expectedResults.add(tempDir.getFilename()+"/tmp");
        expectedResults.add(tempDir.getFilename()+"/tmp/something");
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }

    public void testIncludeDirectoryAndSkipContents() throws IOException {
        Map<String,FilesystemIteratorRule> rules = new HashMap<String,FilesystemIteratorRule>();
        rules.put(tempDir.getFilename(), FilesystemIteratorRule.OK);
        rules.put(tempDir.getFilename()+"/tmp/", FilesystemIteratorRule.SKIP);
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        expectedResults.add("/");
        expectedResults.add("/home");
        expectedResults.add("/home/o");
        expectedResults.add("/home/o/orion");
        expectedResults.add("/home/o/orion/tmp");
        expectedResults.add(tempDir.getFilename());
        expectedResults.add(tempDir.getFilename()+"/home");
        expectedResults.add(tempDir.getFilename()+"/home/a");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/badlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/brokenlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something2");
        expectedResults.add(tempDir.getFilename()+"/tmp");
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }

    public void testFileExistsRuleExists() throws IOException {
        Map<String,FilesystemIteratorRule> rules = new HashMap<String,FilesystemIteratorRule>();
        rules.put(tempDir.getFilename(), FilesystemIteratorRule.OK);
        rules.put(
            tempDir.getFilename()+"/home/a/aoadmin/something",
            new FileExistsRule(new String[] {tempDir.getFilename()+"/home/a/aoadmin/something2"}, FilesystemIteratorRule.SKIP, FilesystemIteratorRule.OK)
        );
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        expectedResults.add("/");
        expectedResults.add("/home");
        expectedResults.add("/home/o");
        expectedResults.add("/home/o/orion");
        expectedResults.add("/home/o/orion/tmp");
        expectedResults.add(tempDir.getFilename());
        expectedResults.add(tempDir.getFilename()+"/home");
        expectedResults.add(tempDir.getFilename()+"/home/a");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/badlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/brokenlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something2");
        expectedResults.add(tempDir.getFilename()+"/tmp");
        expectedResults.add(tempDir.getFilename()+"/tmp/something");
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }

    public void testFileExistsRuleNotExists() throws IOException {
        Map<String,FilesystemIteratorRule> rules = new HashMap<String,FilesystemIteratorRule>();
        rules.put(tempDir.getFilename(), FilesystemIteratorRule.OK);
        rules.put(
            tempDir.getFilename()+"/home/a/aoadmin/something",
            new FileExistsRule(new String[] {tempDir.getFilename()+"/home/a/aoadmin/somethingNotHere"}, FilesystemIteratorRule.SKIP, FilesystemIteratorRule.OK)
        );
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        expectedResults.add("/");
        expectedResults.add("/home");
        expectedResults.add("/home/o");
        expectedResults.add("/home/o/orion");
        expectedResults.add("/home/o/orion/tmp");
        expectedResults.add(tempDir.getFilename());
        expectedResults.add(tempDir.getFilename()+"/home");
        expectedResults.add(tempDir.getFilename()+"/home/a");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/badlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/brokenlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something2");
        expectedResults.add(tempDir.getFilename()+"/tmp");
        expectedResults.add(tempDir.getFilename()+"/tmp/something");
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }

    public void testFileExistsRuleBrokenLink() throws IOException {
        Map<String,FilesystemIteratorRule> rules = new HashMap<String,FilesystemIteratorRule>();
        rules.put(tempDir.getFilename(), FilesystemIteratorRule.OK);
        rules.put(
            tempDir.getFilename()+"/home/a/aoadmin/something",
            new FileExistsRule(new String[] {tempDir.getFilename()+"/home/a/aoadmin/brokenlink"}, FilesystemIteratorRule.SKIP, FilesystemIteratorRule.OK)
        );
        Map<String,FilesystemIteratorRule> prefixRules = Collections.emptyMap();
        List<String> expectedResults = new ArrayList<String>();
        expectedResults.add("/");
        expectedResults.add("/home");
        expectedResults.add("/home/o");
        expectedResults.add("/home/o/orion");
        expectedResults.add("/home/o/orion/tmp");
        expectedResults.add(tempDir.getFilename());
        expectedResults.add(tempDir.getFilename()+"/home");
        expectedResults.add(tempDir.getFilename()+"/home/a");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/badlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/brokenlink");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something");
        expectedResults.add(tempDir.getFilename()+"/home/a/aoadmin/something2");
        expectedResults.add(tempDir.getFilename()+"/tmp");
        expectedResults.add(tempDir.getFilename()+"/tmp/something");
        doTest(
            rules,
            prefixRules,
            expectedResults
        );
    }

    /**
     * Performs the test against expected results.
     */
    private void doTest(
        Map<String,FilesystemIteratorRule> rules,
        Map<String,FilesystemIteratorRule> prefixRules,
        List<String> expectedResults
    ) throws IOException {
        FilesystemIterator iterator = new FilesystemIterator(
            rules,
            prefixRules
        );
        List<String> results = new ArrayList<String>();
        File file;
        while((file=iterator.getNextFile())!=null) {
            results.add(file.getPath());
        }
        int widestExpected = 8;
        for(String S : expectedResults) {
            int len = S.length();
            if(len>widestExpected) widestExpected = len;
        }
        int widestActual = 6;
        for(String S : results) {
            int len = S.length();
            if(len>widestActual) widestActual = len;
        }
        int longerList = Math.max(expectedResults.size(), results.size());
        System.out.print("Expected");
        for(int d=8;d<widestExpected;d++) System.out.print(' ');
        System.out.print(' ');
        System.out.print("Actual");
        for(int d=6;d<widestActual;d++) System.out.print(' ');
        System.out.println();
        for(int c=0;c<longerList;c++) {
            String expected = c<expectedResults.size() ? expectedResults.get(c) : "";
            System.out.print(expected);
            for(int d=expected.length();d<widestExpected;d++) System.out.print(' ');
            System.out.print(' ');
            String actual = c<results.size() ? results.get(c) : "";
            System.out.print(actual);
            for(int d=actual.length();d<widestActual;d++) System.out.print(' ');
            System.out.println();
        }
        assertEquals("Results are not as expected", expectedResults, results);
    }
}
