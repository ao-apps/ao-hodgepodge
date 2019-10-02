/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Matches simple wildcard patterns.  A wildcard pattern is any combination of '*'
 * and textual values.  For example, some patterns would include:
 * <ul>
 *   <li>""                     Match none</li>
 *   <li>"*"                    Match all</li>
 *   <li>"prefix*"              Prefix match</li>
 *   <li>"*suffix"              Suffix match</li>
 *   <li>"prefix*suffix"        Prefix and suffix match</li>
 *   <li>"*infix*"              Infix match</li>
 *   <li>"prefix*infix*"        Prefix and infix match</li>
 *   <li>"*infix*suffix"        Infix and suffix match</li>
 *   <li>"prefix*infix*suffix"  Prefix, infix, and suffix match</li>
 *   <li>"exact_value"          Exact match</li>
 * </ul>
 * <p>
 * Any consecutive sequence of '*' are combined into a single '*'.
 * </p>
 * <p>
 * All matchers are thread-safe.
 * </p>
 * <p>
 * {@link WildcardPatternMatcher} are measured typically 2 to 100 times as fast as {@link Pattern},
 * and have been measured up to 2000 times as fast in contrived scenarios (suffix match on long strings),
 * and should never be slower in their limited use-case domain.
 * </p>
 * <p>
 * TODO: Support "**" as an escape for literal '*' in matching?  No longer collapse adjacent '*'?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class WildcardPatternMatcher {

	private static final WildcardPatternMatcher matchNone = new WildcardPatternMatcher() {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean isMatch(String paramName) {
			return false;
		}
	};

	/**
	 * Gets the match none matcher.
	 */
	public static WildcardPatternMatcher matchNone() {
		return matchNone;
	}

	private static final WildcardPatternMatcher matchAll = new WildcardPatternMatcher() {
		@Override
		public boolean isMatch(String paramName) {
			return true;
		}
	};

	/**
	 * Gets the match all matcher.
	 */
	public static WildcardPatternMatcher matchAll() {
		return matchAll;
	}

	/**
	 * Gets the matcher for the comma and/or space separated patterns.
	 * <p>
	 * Any null or empty pattern matches none.
	 * </p>
	 * <p>
	 * TODO: New optional flag to limit the matchers to disable the infix matching.
	 * The prefix and suffix matches will always be fast O(n), but the infix matching
	 * can be O(n^2).  This may be useful with public facing search forms, for example.
	 * </p>
	 */
	public static WildcardPatternMatcher compile(String patterns) {
		if(patterns == null || patterns.isEmpty()) {
			return matchNone;
		} else {
			List<String> list = StringUtility.splitStringCommaSpace(patterns);
			// Match none shortcut
			if(list.isEmpty()) return matchNone;

			// Parse into a series of individual matchers
			final List<WildcardPatternMatcher> matchers = new ArrayList<>(list.size());
			for(String pattern : list) {
				int end = pattern.length();
				if(end > 0) {
					// Beginning wildcards
					int pos = 0;
					final boolean startsWildcard;
					if(pattern.charAt(0) == '*') {
						startsWildcard = true;
						pos++;
						// Skip consecutive beginning
						while(pos < end && pattern.charAt(pos) == '*') pos++;
						if(pos >= end) {
							// Is any number of '*' only, matchAll shortcut
							return matchAll;
						}
					} else {
						startsWildcard = false;
					}
					// Ending wildcards
					final boolean endsWildcard;
					if(pattern.charAt(end - 1) == '*') {
						endsWildcard = true;
						end--;
						// Skip consecutive ending
						while(end > pos && pattern.charAt(end - 1) == '*') end--;
						assert end > pos;
					} else {
						endsWildcard = false;
					}
					// Split the remaining pattern on any internal '*'
					final List<String> sequences = new ArrayList<>();
					while(pos < end) {
						assert pattern.charAt(pos) != '*';
						int starPos = pattern.indexOf('*', pos + 1);
						if(starPos == -1 || starPos >= end) {
							// Not more '*' found
							sequences.add(pattern.substring(pos, end));
							pos = end;
						} else {
							// Found
							sequences.add(pattern.substring(pos, starPos));
							pos = starPos + 1;
							// Skip consecutive
							while(pos < end && pattern.charAt(pos) == '*') pos++;
						}
					}
					int seqCount = sequences.size();
					assert seqCount >= 1;
					if(seqCount == 1) {
						final String sequence = sequences.get(0);
						if(startsWildcard) {
							if(endsWildcard) {
								// *infix*
								matchers.add(
									new WildcardPatternMatcher() {
										@Override
										public boolean isMatch(String paramName) {
											return paramName.contains(sequence);
										}
									}
								);
							} else {
								// *suffix
								matchers.add(
									new WildcardPatternMatcher() {
										@Override
										public boolean isMatch(String paramName) {
											return paramName.endsWith(sequence);
										}
									}
								);
							}
						} else {
							if(endsWildcard) {
								// prefix*
								matchers.add(
									new WildcardPatternMatcher() {
										@Override
										public boolean isMatch(String paramName) {
											return paramName.startsWith(sequence);
										}
									}
								);
							} else {
								// exact
								matchers.add(
									new WildcardPatternMatcher() {
										@Override
										public boolean isMatch(String paramName) {
											return paramName.equals(sequence);
										}
									}
								);
							}
						}
					} else {
						matchers.add(
							new WildcardPatternMatcher() {
								@Override
								public boolean isMatch(String paramName) {
									int index = 0;
									int indexEnd = sequences.size();
									int pos = 0;
									int end = paramName.length();
									// Handle non-wildcard start
									if(!startsWildcard) {
										String prefix = sequences.get(0);
										if(!paramName.startsWith(prefix)) {
											return false;
										}
										index++;
										pos += prefix.length();
									}
									// Handle non-wildcard end
									if(!endsWildcard) {
										indexEnd--;
										String suffix = sequences.get(indexEnd);
										if(!paramName.endsWith(suffix)) {
											return false;
										}
										end -= suffix.length();
									}
									// Check if overlapping prefix and suffix matches
									if(end < pos) return false;
									// Handle any remaining infixes
									while(index < indexEnd) {
										String sequence = sequences.get(index++);
										int sequenceLen = sequence.length();
										assert sequenceLen > 0;
										int foundAt = paramName.indexOf(sequence, pos);
										if(foundAt == -1 || foundAt > (end - sequenceLen)) {
											return false;
										}
										pos += sequenceLen;
									}
									return true;
								}
							}
						);
					}
				}
			}
			if(matchers.isEmpty()) {
				return matchNone;
			}
			if(matchers.size() == 1) return matchers.get(0);
			return new WildcardPatternMatcher() {
				@Override
				public boolean isMatch(String paramName) {
					for(WildcardPatternMatcher matcher : matchers) {
						if(matcher.isMatch(paramName)) return true;
					}
					return false;
				}
			};
		}
	}

	private WildcardPatternMatcher() {
	}

	/**
	 * Checks if this is empty (has no patterns).
	 * Any empty matcher does not match anything.
	 */
	public boolean isEmpty() {
		return false;
	}

	// TODO: Rename "matches", deprecate old with "default" method in Java 1.8?
	abstract public boolean isMatch(String paramName);
}
