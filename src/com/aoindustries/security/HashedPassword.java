/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016  AO Industries, Inc.
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
package com.aoindustries.security;

import java.util.Arrays;

/**
 * A salted, hashed and key stretched password.
 * {@link https://crackstation.net/hashing-security.htm}
 *
 * @author  AO Industries, Inc.
 */
public class HashedPassword {
	
	private final byte[] passwordSalt;
	private final int passwordIterations;
	private final byte[] passwordHash;

	/**
	 * @param passwordSalt  The provided parameter is zeroed
	 * @param passwordIterations
	 * @param passwordHash  The provided parameter is zeroed 
	 */
    public HashedPassword(
		byte[] passwordSalt,
		int passwordIterations,
		byte[] passwordHash
    ) {
		this.passwordSalt = Arrays.copyOf(passwordSalt, passwordSalt.length);
		Arrays.fill(passwordSalt, (byte)0);
		this.passwordIterations = passwordIterations;
		this.passwordHash = Arrays.copyOf(passwordHash, passwordHash.length);
		Arrays.fill(passwordHash, (byte)0);
	}

	@Override
	public String toString() {
		return "*";
		/* Do not leak hash
		return
			'('
			+ StringUtility.convertToHex(passwordSalt)
			+ ", "
			+ passwordIterations
			+ ", "
			+ StringUtility.convertToHex(passwordHash)
			+ ')'
		;
		 */
	}
}
