package com.aoindustries.io;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.File;
import java.io.IOException;

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
            File file = new File(fullPaths[c]);
            if(file.exists()) return existsRule;
        }
        return notExistsRule;
    }

    public boolean isIncluded(String filename) throws IOException {
        return getEffectiveRule(filename).isIncluded(filename);
    }
}
