/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import com.aoindustries.encoding.MediaEncoder;
import java.io.IOException;

/**
 * A result with a string and no special prefix or suffix.
 *
 * @author  AO Industries, Inc.
 */
public class StringBundleLookupResult implements BundleLookupResult {

	private final String result;

    public StringBundleLookupResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return result;
	}

	@Override
	public void appendPrefixTo(Appendable out) throws IOException {
		// No prefix
	}

	@Override
	public void appendPrefixTo(MediaEncoder encoder, Appendable out) throws IOException {
		// No prefix
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public void appendSuffixTo(Appendable out) throws IOException {
		// No suffix
	}

	@Override
	public void appendSuffixTo(MediaEncoder encoder, Appendable out) throws IOException {
		// No suffix
	}
}
