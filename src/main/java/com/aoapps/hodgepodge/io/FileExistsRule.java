/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.io;

import java.io.File;
import java.io.IOException;

/**
 * Conditionally uses one of two rules based on the existence of a file on the current server.
 * The <code>existsRule</code> is used if ANY one of the <code>fullPaths</code> exists.
 * The <code>notExistsRule</code> is used if NONE of the <code>fullPaths</code> exist.
 *
 * @author  AO Industries, Inc.
 */
public class FileExistsRule implements FilesystemIteratorRule {

	private final String[] fullPaths;
	private final FilesystemIteratorRule existsRule;
	private final FilesystemIteratorRule notExistsRule;

	public FileExistsRule(String[] fullPaths, FilesystemIteratorRule existsRule, FilesystemIteratorRule notExistsRule) {
		this.fullPaths = fullPaths;
		this.existsRule = existsRule;
		this.notExistsRule = notExistsRule;
	}

	public FilesystemIteratorRule getEffectiveRule(String filename) throws IOException {
		for (String fullPath : fullPaths) {
			File file = new File(fullPath);
			if(file.exists()) return existsRule;
		}
		return notExistsRule;
	}

	@Override
	public boolean isIncluded(String filename) throws IOException {
		return getEffectiveRule(filename).isIncluded(filename);
	}
}
