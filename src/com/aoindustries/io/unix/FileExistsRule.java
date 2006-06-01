package com.aoindustries.io.unix;

import java.io.IOException;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * Conditionally uses one of two rules based on the existence of a file on the current server.
 * The <code>existsRule</code> is used if ANY one of the <code>fullPaths</code> exists.
 * The <code>notExistsRule</code> is used if NONE of the <code>fullPaths</code> exist.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class FileExistsRule extends FilesystemIteratorRule {

    private String[] fullPaths;
    private FilesystemIteratorRule existsRule;
    private FilesystemIteratorRule notExistsRule;
    
    public FileExistsRule(String[] fullPaths, FilesystemIteratorRule existsRule, FilesystemIteratorRule notExistsRule) {
        this.fullPaths = fullPaths;
        this.existsRule = existsRule;
        this.notExistsRule = notExistsRule;
    }

    public FilesystemIteratorRule getEffectiveRule(String filename) throws IOException {
        for(int c=0;c<fullPaths.length;c++) {
            UnixFile uf = new UnixFile(fullPaths[c]);
            if(uf.exists()) return existsRule;
        }
        return notExistsRule;
    }

    public boolean isNoRecurse(String filename) throws IOException {
        return getEffectiveRule(filename).isNoRecurse(filename);
    }

    public boolean isSkip(String filename) throws IOException {
        return getEffectiveRule(filename).isSkip(filename);
    }
}
