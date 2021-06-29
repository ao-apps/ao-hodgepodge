/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2021  AO Industries, Inc.
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
module com.aoapps.hodgepodge {
	exports com.aoapps.hodgepodge.awt;
	exports com.aoapps.hodgepodge.awt.image;
	exports com.aoapps.hodgepodge.cache;
	exports com.aoapps.hodgepodge.email;
	exports com.aoapps.hodgepodge.graph;
	exports com.aoapps.hodgepodge.i18n;
	exports com.aoapps.hodgepodge.io;
	//exports com.aoapps.hodgepodge.io.posix.linux.kernel;
	exports com.aoapps.hodgepodge.io.stream;
	exports com.aoapps.hodgepodge.logging;
	exports com.aoapps.hodgepodge.md5;
	exports com.aoapps.hodgepodge.net;
	exports com.aoapps.hodgepodge.rmi;
	exports com.aoapps.hodgepodge.schedule;
	exports com.aoapps.hodgepodge.sort;
	exports com.aoapps.hodgepodge.swing;
	exports com.aoapps.hodgepodge.swing.table;
	exports com.aoapps.hodgepodge.table;
	exports com.aoapps.hodgepodge.tree;
	exports com.aoapps.hodgepodge.util;
	exports com.aoapps.hodgepodge.version;
	exports com.aoapps.hodgepodge.ws;
	// Direct
	requires com.aoapps.collections; // <groupId>com.aoapps</groupId><artifactId>ao-collections</artifactId>
	requires com.aoapps.lang; // <groupId>com.aoapps</groupId><artifactId>ao-lang</artifactId>
	requires com.aoapps.tempfiles; // <groupId>com.aoapps</groupId><artifactId>ao-tempfiles</artifactId>
	// Java SE
	requires java.desktop;
	requires java.logging;
	requires java.rmi;
	requires java.sql;
}
