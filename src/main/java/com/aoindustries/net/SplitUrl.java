/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2019  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.io.Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Extremely minimal representation of a URL, optimized for altering the path,
 * query, or fragment for URL rewriting.
 * <p>
 * This only deals with three parts of the URL:
 * </p>
 * <ol>
 *   <li>base - everything before the first '?' or '#' (exclusive).  This may
 *       include scheme, hier-part (host, port, path, and such), which this
 *       class is not concerned with.
 *   </li>
 *   <li>query - everything after the first '?' (exclusive) and the fragment '#' (exclusive)</li>
 *   <li>fragment - everything after the first '#' (exclusive)</li>
 * </p>
 * <p>
 * Instances of this class are immutable and thus thread-safe.  Mutating
 * operations return a new instance.
 * </p>
 *
 * @see UriComponent
 * @see UrlUtils
 *
 * @author  AO Industries, Inc.
 */
public class SplitUrl {

	private final String url;

	/**
	 * The index of the {@code '?'} marking the query or {@code -1} when there is no query.
	 */
	private final int queryIndex;

	/**
	 * The index of the {@code '#'} marking the fragment or {@code -1} when there is no fragment.
	 */
	private final int fragmentIndex;

	public SplitUrl(String url) {
		this.url = url;
		int urlLen = url.length();
		// Find first of '?' or '#'
		int pathEnd = UrlUtils.getPathEnd(url);
		if(pathEnd >= urlLen) {
			queryIndex = -1;
			fragmentIndex = -1;
		} else if(url.charAt(pathEnd) == '?') {
			queryIndex = pathEnd;
			fragmentIndex = url.indexOf('#', pathEnd + 1);
		} else {
			assert url.charAt(pathEnd) == '#';
			queryIndex = -1;
			fragmentIndex = pathEnd;
		}
	}

	private SplitUrl(String url, int queryIndex, int fragmentIndex) {
		this.url = url;
		this.queryIndex = queryIndex;
		this.fragmentIndex = fragmentIndex;
		assert equals(new SplitUrl(url)) : "Split after mutations must be equal to splitting in public constructor";
	}

	/**
	 * Gets the full URL that has been split.
	 */
	@Override
	public String toString() {
		return url;
	}

	/**
	 * Compares the {@link #url URL} directly.  No encoding or decoding
	 * is performed.  This does not compare URLs semantically.
	 */
	@Override
	final public boolean equals(Object obj) {
		if(!(obj instanceof SplitUrl)) return false;
		SplitUrl other = (SplitUrl)obj;
		if(this == other) {
			return true;
		} else if(url.equals(other.url)) {
			assert queryIndex == other.queryIndex : "url equal with queryIndex mismatch: url = " + url + ", this.queryIndex = " + this.queryIndex + ", other.queryIndex = " + other.queryIndex;
			assert fragmentIndex == other.fragmentIndex : "url equal with fragmentIndex mismatch: url = " + url + ", this.fragmentIndex = " + this.fragmentIndex + ", other.fragmentIndex = " + other.fragmentIndex;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The hash code is the same as the hash code of the url.
	 *
	 * @see  #toString()
	 * @see  String#hashCode()
	 */
	@Override
	final public int hashCode() {
		return url.hashCode();
	}

	/**
	 * @see  UrlUtils#isScheme(java.lang.String, java.lang.String)
	 */
	public boolean isScheme(String scheme) {
		return UrlUtils.isScheme(url, scheme);
	}

	/**
	 * @see  UrlUtils#hasScheme(java.lang.String)
	 */
	public boolean hasScheme() {
		return UrlUtils.hasScheme(url);
	}

	/**
	 * @see  UrlUtils#getScheme(java.lang.String)
	 */
	public String getScheme() {
		return UrlUtils.getScheme(url);
	}

	/**
	 * Gets the path end within this URL.
	 *
	 * @return  the index of the first '?' or '#' (exclusive), or the length of the URL when neither found.
	 */
	public int getPathEnd() {
		if(queryIndex != -1) return queryIndex;
		else if(fragmentIndex != -1) return fragmentIndex;
		else return url.length();
	}

	/**
	 * Checks if the path ends with the given value.
	 *
	 * @see String#regionMatches(int, java.lang.String, int, int)
	 */
	public boolean pathEndsWith(String suffix) {
		int suffixLen = suffix.length();
		int pathStart = getPathEnd() - suffixLen;
		return pathStart >= 0 && url.regionMatches(pathStart, suffix, 0, suffixLen);
	}

	/**
	 * Checks if the path ends with the given value, case-insensitive.
	 *
	 * @see String#regionMatches(boolean, int, java.lang.String, int, int)
	 */
	public boolean pathEndsWithIgnoreCase(String suffix) {
		int suffixLen = suffix.length();
		int pathStart = getPathEnd() - suffixLen;
		return pathStart >= 0 && url.regionMatches(true, pathStart, suffix, 0, suffixLen);
	}

	/**
	 * Gets the base - everything before the first '?' or '#' (exclusive).  This may
	 * include scheme, hier-part (host, port, path, and such), which this
	 * class is not concerned with.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeBase(…)</code>
	 * and <code>appendBase(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 */
	public String getBase() {
		if(queryIndex != -1) return url.substring(0, queryIndex);
		else if(fragmentIndex != -1) return url.substring(0, fragmentIndex);
		else return url;
	}

	/**
	 * Writes the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 */
	public void writeBase(Writer out) throws IOException {
		if(queryIndex != -1) out.write(url, 0, queryIndex);
		else if(fragmentIndex != -1) out.write(url, 0, fragmentIndex);
		else out.write(url);
	}

	/**
	 * Writes the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 */
	public void writeBase(Writer out, Encoder encoder) throws IOException {
		if(encoder == null) {
			writeBase(out);
		} else {
			if(queryIndex != -1) encoder.write(url, 0, queryIndex, out);
			else if(fragmentIndex != -1) encoder.write(url, 0, fragmentIndex, out);
			else encoder.write(url, out);
		}
	}

	/**
	 * Appends the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendBase(A out) throws IOException {
		if(queryIndex != -1) out.append(url, 0, queryIndex);
		else if(fragmentIndex != -1) out.append(url, 0, fragmentIndex);
		else out.append(url);
		return out;
	}

	/**
	 * Appends the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendBase(A out, Encoder encoder) throws IOException {
		if(encoder == null) {
			appendBase(out);
		} else {
			if(queryIndex != -1) encoder.append(url, 0, queryIndex, out);
			else if(fragmentIndex != -1) encoder.append(url, 0, fragmentIndex, out);
			else encoder.append(url, out);
		}
		return out;
	}

	/**
	 * Appends the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendBase(StringBuilder sb) {
		if(queryIndex != -1) return sb.append(url, 0, queryIndex);
		else if(fragmentIndex != -1) return sb.append(url, 0, fragmentIndex);
		else return sb.append(url);
	}

	/**
	 * Appends the part of the URL up to the first '?' or '#' (exclusive), or the full URL when neither found.
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendBase(StringBuffer sb) {
		if(queryIndex != -1) return sb.append(url, 0, queryIndex);
		else if(fragmentIndex != -1) return sb.append(url, 0, fragmentIndex);
		else return sb.append(url);
	}

	/**
	 * Gets the index of the query marker ('?').
	 *
	 * @return  the index of the '?' marking the query string or {@code -1} when there is no query string.
	 */
	public int getQueryIndex() {
		return queryIndex;
	}

	/**
	 * Checks if this has a query.
	 */
	public boolean hasQuery() {
		return queryIndex != -1;
	}

	/**
	 * Gets the query string.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeQueryString(…)</code>
	 * and <code>appendQuery(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the query string (not including the '?') or {@code null} when there is no query.
	 */
	public String getQueryString() {
		if(queryIndex == -1) return null;
		int queryStart = queryIndex + 1;
		if(fragmentIndex == -1) {
			return url.substring(queryStart);
		} else {
			return url.substring(queryStart, fragmentIndex);
		}
	}

	/**
	 * Writes the query string (not including the '?').
	 */
	public void writeQueryString(Writer out) throws IOException {
		if(queryIndex != -1) {
			int queryStart = queryIndex + 1;
			out.write(url,
				queryStart,
				(fragmentIndex == -1 ? url.length() : fragmentIndex) - queryStart
			);
		}
	}

	/**
	 * Writes the query string (not including the '?').
	 */
	public void writeQueryString(Writer out, Encoder encoder) throws IOException {
		if(queryIndex != -1) {
			if(encoder == null) {
				writeQueryString(out);
			} else {
				int queryStart = queryIndex + 1;
				encoder.write(url,
					queryStart,
					(fragmentIndex == -1 ? url.length() : fragmentIndex) - queryStart,
					out
				);
			}
		}
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendQueryString(A out) throws IOException {
		if(queryIndex != -1) {
			out.append(url,
				queryIndex+ 1,
				fragmentIndex == -1 ? url.length() : fragmentIndex
			);
		}
		return out;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendQueryString(A out, Encoder encoder) throws IOException {
		if(queryIndex != -1) {
			if(encoder == null) {
				appendQueryString(out);
			} else {
				encoder.append(url,
					queryIndex + 1,
					fragmentIndex == -1 ? url.length() : fragmentIndex,
					out
				);
			}
		}
		return out;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendQueryString(StringBuilder sb) {
		if(queryIndex != -1) {
			sb.append(url,
				queryIndex + 1,
				fragmentIndex == -1 ? url.length() : fragmentIndex
			);
		}
		return sb;
	}

	/**
	 * Appends the query string (not including the '?').
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendQueryString(StringBuffer sb) {
		if(queryIndex != -1) {
			sb.append(url,
				queryIndex + 1,
				fragmentIndex == -1 ? url.length() : fragmentIndex
			);
		}
		return sb;
	}

	/**
	 * Gets the index of the fragment marker ('#').
	 *
	 * @return  the index of the '#' marking the fragment or {@code -1} when there is no fragment.
	 */
	public int getFragmentIndex() {
		return fragmentIndex;
	}

	/**
	 * Checks if this has an fragment.
	 */
	public boolean hasFragment() {
		return fragmentIndex != -1;
	}

	/**
	 * Gets the fragment.
	 * <p>
	 * This method may involve string manipulation, favor the <code>writeFragment(…)</code>
	 * and <code>appendFragment(…)</code> methods when appropriate.
	 * </p>
	 *
	 * @return  the fragment (not including the '#') or {@code null} when there is no fragment.
	 */
	public String getFragment() {
		return (fragmentIndex == -1) ? null : url.substring(fragmentIndex + 1);
	}

	/**
	 * Writes the fragment (not including the '#').
	 */
	public void writeFragment(Writer out) throws IOException {
		if(fragmentIndex != -1) {
			int fragmentStart = fragmentIndex + 1;
			out.write(url, fragmentStart, url.length() - fragmentStart);
		}
	}

	/**
	 * Writes the fragment (not including the '#').
	 */
	public void writeFragment(Writer out, Encoder encoder) throws IOException {
		if(fragmentIndex != -1) {
			if(encoder == null) {
				SplitUrl.this.writeFragment(out);
			} else {
				int fragmentStart = fragmentIndex + 1;
				encoder.write(url, fragmentStart, url.length() - fragmentStart, out);
			}
		}
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendFragment(A out) throws IOException {
		if(fragmentIndex != -1) {
			out.append(url, fragmentIndex + 1, url.length());
		}
		return out;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link Appendable} {@code out}
	 */
	public <A extends Appendable> A appendFragment(A out, Encoder encoder) throws IOException {
		if(fragmentIndex != -1) {
			if(encoder == null) {
				SplitUrl.this.appendFragment(out);
			} else {
				encoder.append(url, fragmentIndex + 1, url.length(), out);
			}
		}
		return out;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link StringBuilder} {@code sb}
	 */
	public StringBuilder appendFragment(StringBuilder sb) {
		if(fragmentIndex != -1) {
			sb.append(url, fragmentIndex + 1, url.length());
		}
		return sb;
	}

	/**
	 * Appends the fragment (not including the '#').
	 *
	 * @return  The {@link StringBuffer} {@code sb}
	 */
	public StringBuffer appendFragment(StringBuffer sb) {
		if(fragmentIndex != -1) {
			sb.append(url, fragmentIndex + 1, url.length());
		}
		return sb;
	}

	/**
	 * @return  The new split URL or {@code this} when unmodified.
	 *
	 * @see  UrlUtils#encodeURI(java.lang.String, java.lang.String)
	 */
	public SplitUrl encodeURI(String documentEncoding) throws UnsupportedEncodingException {
		String newUrl = UrlUtils.encodeURI(url, documentEncoding);
		return (newUrl == url) ? this : new SplitUrl(newUrl);
	}

	/**
	 * @return  The new split URL or {@code this} when unmodified.
	 *
	 * @see  UrlUtils#decodeURI(java.lang.String, java.lang.String)
	 */
	public SplitUrl decodeURI(String documentEncoding) throws UnsupportedEncodingException {
		String newUrl = UrlUtils.decodeURI(url, documentEncoding);
		return (newUrl == url) ? this : new SplitUrl(newUrl);
	}

	/**
	 * Replaces the base.
	 *
	 * @param base  The base may not contain the query marker '?' or fragment marker '#'
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 */
	public SplitUrl setBase(String base) {
		int baseLen = base.length();
		int pathEnd = getPathEnd();
		// Look for not changed
		if(
			baseLen == pathEnd
			&& url.startsWith(base)
		) {
			// Not changed
			return this;
		} else {
			if(queryIndex == -1) {
				if(fragmentIndex == -1) {
					// Base only
					return new SplitUrl(base);
				} else {
					// Fragment only
					int urlLen = url.length();
					int newUrlLen = baseLen + (urlLen - fragmentIndex);
					StringBuilder newUrl = new StringBuilder(newUrlLen);
					newUrl.append(base).append(url, fragmentIndex, urlLen);
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						-1,
						baseLen
					);
				}
			} else {
				int urlLen = url.length();
				int newUrlLen = baseLen + (urlLen - queryIndex);
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				newUrl.append(base).append(url, queryIndex, urlLen);
				assert newUrl.length() == newUrlLen;
				if(fragmentIndex == -1) {
					// Query only
					return new SplitUrl(
						newUrl.toString(),
						baseLen,
						-1
					);
				} else {
					// Query and fragment
					return new SplitUrl(
						newUrl.toString(),
						baseLen,
						fragmentIndex + (baseLen - pathEnd)
					);
				}
			}
		}
	}

	/**
	 * Replaces the query string.
	 *
	 * @param query  The query (not including the first '?') - it is added without additional encoding.
	 *               The query is removed when the query is {@code null}.
	 *               The query may not contain the fragment marker '#'
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 */
	public SplitUrl setQueryString(String query) {
		if(query == null) {
			// Removing query
			if(queryIndex == -1) {
				// Already has no query
				return this;
			} else {
				// Remove the existing query
				if(fragmentIndex == -1) {
					return new SplitUrl(
						url.substring(0, queryIndex),
						-1,
						-1
					);
				} else {
					int urlLen = url.length();
					int newUrlLen = queryIndex + (urlLen - fragmentIndex);
					StringBuilder newUrl = new StringBuilder(newUrlLen);
					newUrl.append(url, 0, queryIndex).append(url, fragmentIndex, urlLen);
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						-1,
						queryIndex
					);
				}
			}
		} else {
			if(query.indexOf('#') != -1) throw new IllegalArgumentException("query string may not contain fragment marker (#): " + query);
			// Setting query
			if(queryIndex == -1) {
				// Add query
				int urlLen = url.length();
				int queryLen = query.length();
				int newUrlLen = urlLen + 1 + queryLen;
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				if(fragmentIndex == -1) {
					newUrl.append(url).append('?').append(query);
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						urlLen,
						-1
					);
				} else {
					newUrl.append(url, 0, fragmentIndex).append('?').append(query).append(url, fragmentIndex, urlLen);
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						fragmentIndex,
						fragmentIndex + 1 + queryLen
					);
				}
			} else {
				// Replace query
				int urlLen = url.length();
				int queryLen = query.length();
				int queryStart = queryIndex + 1;
				int currentQueryLen = (fragmentIndex == -1 ? urlLen : fragmentIndex) - queryStart;
				if(
					currentQueryLen == queryLen
					&& url.regionMatches(queryStart, query, 0, queryLen)
				) {
					// Already has this query
					return this;
				}
				int newUrlLen = urlLen - currentQueryLen + queryLen;
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				newUrl.append(url, 0, queryStart).append(query);
				if(fragmentIndex == -1) {
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						queryIndex,
						-1
					);
				} else {
					newUrl.append(url, fragmentIndex, urlLen);
					assert newUrl.length() == newUrlLen;
					return new SplitUrl(
						newUrl.toString(),
						queryIndex,
						fragmentIndex - currentQueryLen + queryLen
					);
				}
			}
		}
	}

	/**
	 * Adds a query string.
	 *
	 * @param query  The query (not including the first '?' / '&') - it is added without additional encoding.
	 *               Nothing is added when the query is {@code null}.
	 *               The query may not contain the fragment marker '#'
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 */
	public SplitUrl addQueryString(String query) {
		if(query == null) {
			return this;
		} else {
			if(query.indexOf('#') != -1) throw new IllegalArgumentException("query string may not contain fragment marker (#): " + query);
			int urlLen = url.length();
			int queryLen = query.length();
			int newUrlLen = urlLen + 1 + queryLen;
			StringBuilder newUrl = new StringBuilder(newUrlLen);
			int newQueryIndex;
			int newFragmentIndex;
			if(queryIndex == -1) {
				if(fragmentIndex == -1) {
					// First parameter to end
					newUrl.append(url).append('?').append(query);
					newQueryIndex = url.length();
					newFragmentIndex = -1;
				} else {
					// First parameter before fragment
					newUrl.append(url, 0, fragmentIndex).append('?').append(query).append(url, fragmentIndex, urlLen);
					newQueryIndex = fragmentIndex;
					newFragmentIndex = fragmentIndex + 1 + queryLen;
				}
			} else {
				newQueryIndex = queryIndex;
				if(fragmentIndex == -1) {
					// Additional parameter to end
					newUrl.append(url).append('&').append(query);
					newFragmentIndex = -1;
				} else {
					// Additional parameter before fragment
					newUrl.append(url, 0, fragmentIndex).append('&').append(query).append(url, fragmentIndex, urlLen);
					newFragmentIndex = fragmentIndex + 1 + queryLen;
				}
			}
			assert newUrl.length() == newUrlLen;
			return new SplitUrl(
				newUrl.toString(),
				newQueryIndex,
				newFragmentIndex
			);
		}
	}

	/**
	 * Adds an already-encoded parameter.
	 *
	 * @param encodedName  The parameter name - it is added without additional encoding.
	 *                     Nothing is added when the name is {@code null}.
	 *                     The name may not contain the fragment marker '#'
	 * @param encodedValue  The parameter value - it is added without additional encoding.
	 *                      When {@code null}, the parameter is added without any '='.
	 *                      Must be {@code null} when {@code name} is {@code null}.
	 *                      The value may not contain the fragment marker '#'
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 */
	public SplitUrl addEncodedParameter(String encodedName, String encodedValue) {
		if(encodedName == null) {
			if(encodedValue != null) throw new IllegalArgumentException("non-null value provided with null name: " + encodedValue);
			return this;
		} else {
			if(encodedValue == null) {
				return addQueryString(encodedName);
			} else {
				if(encodedName.indexOf('#') != -1) throw new IllegalArgumentException("name may not contain fragment marker (#): " + encodedName);
				if(encodedValue.indexOf('#') != -1) throw new IllegalArgumentException("value may not contain fragment marker (#): " + encodedValue);
				int urlLen = url.length();
				int nameLen = encodedName.length();
				int valueLen = encodedValue.length();
				int newUrlLen = urlLen + 1 + nameLen + 1 + valueLen;
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				int newQueryIndex;
				int newFragmentIndex;
				if(queryIndex == -1) {
					if(fragmentIndex == -1) {
						// First parameter to end
						newUrl.append(url).append('?').append(encodedName).append('=').append(encodedValue);
						newQueryIndex = url.length();
						newFragmentIndex = -1;
					} else {
						// First parameter before fragment
						newUrl.append(url, 0, fragmentIndex).append('?').append(encodedName).append('=').append(encodedValue).append(url, fragmentIndex, urlLen);
						newQueryIndex = fragmentIndex;
						newFragmentIndex = fragmentIndex + 1 + nameLen + 1 + valueLen;
					}
				} else {
					newQueryIndex = queryIndex;
					if(fragmentIndex == -1) {
						// Additional parameter to end
						newUrl.append(url).append('&').append(encodedName).append('=').append(encodedValue);
						newFragmentIndex = -1;
					} else {
						// Additional parameter before fragment
						newUrl.append(url, 0, fragmentIndex).append('&').append(encodedName).append('=').append(encodedValue).append(url, fragmentIndex, urlLen);
						newFragmentIndex = fragmentIndex + 1 + nameLen + 1 + valueLen;
					}
				}
				assert newUrl.length() == newUrlLen;
				return new SplitUrl(
					newUrl.toString(),
					newQueryIndex,
					newFragmentIndex
				);
			}
		}
	}

	/**
	 * Encodes and adds a parameter in a given encoding.
	 *
	 * @param name  The parameter name.
	 *              Nothing is added when the name is {@code null}.
	 * @param value  The parameter value.
	 *               When {@code null}, the parameter is added without any '='.
	 *               Must be {@code null} when {@code name} is {@code null}.
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 *
	 * @see  UrlUtils#encodeURIComponent(java.lang.String, java.lang.String)
	 */
	public SplitUrl addParameter(String name, String value, String documentEncoding) throws UnsupportedEncodingException {
		return addEncodedParameter(
			UrlUtils.encodeURIComponent(name, documentEncoding),
			UrlUtils.encodeURIComponent(value, documentEncoding)
		);
	}

	/**
	 * Adds all of the parameters in a given encoding.
	 *
	 * @param params  The parameters to add.
	 *                Nothing is added when {@code null} or empty.
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 *
	 * @see  HttpParametersUtils#addParams(java.lang.String, com.aoindustries.net.HttpParameters, java.lang.String)
	 */
	public SplitUrl addParameters(HttpParameters params, String documentEncoding) throws UnsupportedEncodingException {
		if(params == null) {
			return this;
		} else {
			String newUrl = HttpParametersUtils.addParams(url, params, documentEncoding);
			return (newUrl == url) ? this : new SplitUrl(newUrl);
		}
	}

	/**
	 * Replaces the fragment.
	 *
	 * @param encodedFragment  The fragment (not including the '#') - it is added without additional encoding.
	 *                         Removes fragment when {@code null}.
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 */
	public SplitUrl setEncodedFragment(String encodedFragment) {
		if(encodedFragment == null) {
			// Removing fragment
			if(fragmentIndex == -1) {
				// Already has no fragment
				return this;
			} else {
				// Remove the existing fragment
				return new SplitUrl(
					url.substring(0, fragmentIndex),
					queryIndex,
					-1
				);
			}
		} else {
			// Setting fragment
			if(fragmentIndex == -1) {
				// Add fragment
				int urlLen = url.length();
				int newUrlLen = urlLen + 1 + encodedFragment.length();
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				newUrl.append(url).append('#').append(encodedFragment);
				assert newUrl.length() == newUrlLen;
				return new SplitUrl(
					newUrl.toString(),
					queryIndex,
					urlLen
				);
			} else {
				// Replace fragment
				int urlLen = url.length();
				int fragmentLen = encodedFragment.length();
				int fragmentStart = fragmentIndex + 1;
				int currentFragmentLen = urlLen - fragmentStart;
				if(
					currentFragmentLen == fragmentLen
					&& url.regionMatches(fragmentStart, encodedFragment, 0, fragmentLen)
				) {
					// Already has this fragment
					return this;
				}
				int newUrlLen = urlLen - currentFragmentLen + fragmentLen;
				StringBuilder newUrl = new StringBuilder(newUrlLen);
				newUrl.append(url, 0, fragmentStart).append(encodedFragment);
				assert newUrl.length() == newUrlLen;
				return new SplitUrl(
					newUrl.toString(),
					queryIndex,
					fragmentIndex - currentFragmentLen + fragmentLen
				);
			}
		}
	}

	/**
	 * Replaces the fragment in the default encoding {@link UrlUtils#ENCODING}.
	 * <p>
	 * TODO: Implement specification of <a href="https://dev.w3.org/html5/spec-LC/urls.html#url-manipulation-and-creation">fragment-escape</a>.
	 * </p>
	 *
	 * @param fragment  The fragment (not including the '#') or {@code null} for no fragment.
	 *
	 * @return  The new split URL or {@code this} when unmodified.
	 *
	 * @deprecated  This is an incomplete implementation - recommend using {@code org.xbib.net.URL}
	 *              or {@code org.apache.http.client.utils.URIBuilder}
	 */
	@Deprecated
	public SplitUrl setFragment(String fragment) {
		return setEncodedFragment(UrlUtils.encodeURIComponent(fragment));
	}
}
