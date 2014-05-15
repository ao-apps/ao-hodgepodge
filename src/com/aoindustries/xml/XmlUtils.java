/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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
package com.aoindustries.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utilities that help when working with XML.
 *
 * @author  AO Industries, Inc.
 */
public final class XmlUtils {

    /**
     * Make no instances.
     */
    private XmlUtils() {
    }

	/**
	 * Fetches and parses an XML DOM from a URL.
	 */
	public static Document parseXml(URL url) throws IOException, ParserConfigurationException, SAXException {
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		try {
			return parseXml(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Parses an XML DOM from an input stream.
	 */
	public static Document parseXml(InputStream in) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(in);
	}

	/**
	 * Iterates over a NodeList.
	 */
	public static Iterable<Node> iterableNodes(final NodeList nodeList) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {
					int index = 0;

					@Override
					public boolean hasNext() {
						return index < nodeList.getLength();
					}

					@Override
					public Node next() {
						if(hasNext()) {
							return nodeList.item(index++);
						} else {
							throw new NoSuchElementException();
						}
					}

					@Override
					public void remove() throws UnsupportedOperationException {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Iterates over a NodeList, only returning Elements.
	 */
	public static Iterable<Element> iterableElements(final NodeList nodeList) {
		return new Iterable<Element>() {
			@Override
			public Iterator<Element> iterator() {
				return new Iterator<Element>() {
					int index = 0;

					@Override
					public boolean hasNext() {
						// Skip past any non-elements
						while(
							index < nodeList.getLength()
							&& !(nodeList.item(index) instanceof Element)
						) {
							index++;
						}
						return index < nodeList.getLength();
					}

					@Override
					public Element next() {
						if(hasNext()) {
							return (Element)nodeList.item(index++);
						} else {
							throw new NoSuchElementException();
						}
					}

					@Override
					public void remove() throws UnsupportedOperationException {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Iterates the children of the given element,
	 * returning only returning child elements of the given name.
	 */
	public static Iterable<Element> iterableChildElementsByTagName(final Element element, final String childTagName) {
		return new Iterable<Element>() {
			@Override
			public Iterator<Element> iterator() {
				return new Iterator<Element>() {
					NodeList children = element.getChildNodes();
					int index = 0;

					@Override
					public boolean hasNext() {
						// Skip past any non-elements
						while(index < children.getLength()) {
							Node child = children.item(index);
							if(child instanceof Element) {
								Element childElem = (Element)child;
								if(childTagName.equals(childElem.getTagName())) {
									break;
								}
							}
							index++;
						}
						return index < children.getLength();
					}

					@Override
					public Element next() {
						if(hasNext()) {
							return (Element)children.item(index++);
						} else {
							throw new NoSuchElementException();
						}
					}

					@Override
					public void remove() throws UnsupportedOperationException {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}