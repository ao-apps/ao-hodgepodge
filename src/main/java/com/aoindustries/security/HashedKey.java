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

import com.aoindustries.util.WrappedException;
import com.aoindustries.util.persistent.PersistentCollections;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * A hashed random key.
 *
 * @author  AO Industries, Inc.
 */
public class HashedKey implements Comparable<HashedKey> {

	public static final String ALGORITHM = "SHA-256";

	/** The number of bytes in the SHA-256 hash. */
	public static final int HASH_BYTES = 256 / 8;
	static {
		assert HASH_BYTES >= 4 : "Hash must be at least 32-bits for hashCode implementation";
	}

	private static final SecureRandom secureRandom = new SecureRandom();

	/**
	 * Generates a random plaintext key of <code>HASH_BYTES</code> bytes in length.
	 *
	 * @see  #hashKey(byte[])
	 */
	public static byte[] generateKey() {
		byte[] key = new byte[HASH_BYTES];
		secureRandom.nextBytes(key);
		return key;
	}

	/**
	 * SHA-256 hashes the given key.
	 * 
	 * @see  #generateKey()
	 */
	public static byte[] hash(byte[] key) {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			md.update(key);
			return md.digest();
		} catch(NoSuchAlgorithmException e) {
			throw new WrappedException(e);
		}
	}

	private final byte[] hash;

	/**
	 * @param hash  The provided parameter is zeroed 
	 */
    public HashedKey(
		byte[] hash
    ) {
		if(hash.length != HASH_BYTES) {
			Arrays.fill(hash, (byte)0);
			throw new IllegalArgumentException("Hash wrong length: " + hash.length);
		}
		this.hash = Arrays.copyOf(hash, hash.length);
		Arrays.fill(hash, (byte)0);
	}

	@Override
	public String toString() {
		return "*";
		/* Do not leak hash
		return StringUtility.convertToHex(hash);
		 */
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof HashedKey)) return false;
		HashedKey other = (HashedKey)obj;
		// TODO: constant time equals here?
		//return HashedPassword.slowEquals(hash, other.hash);
		return Arrays.equals(hash, other.hash);
	}

	/**
	 * The hash code is just the first 32 bits of the hash.
	 */
	@Override
	public int hashCode() {
		return PersistentCollections.bufferToInt(hash);
	}

	@Override
	public int compareTo(HashedKey other) {
		// TODO: constant time compare here?
		byte[] h1 = hash;
		byte[] h2 = other.hash;
		for(int i=0; i<HASH_BYTES; i++) {
			byte b1 = h1[i];
			byte b2 = h2[i];
			if(b1 < b2) return -1;
			if(b1 > b2) return 1;
			// Java 8: int diff = Byte.compare(h1[i], h2[i]);
			// Java 8: if(diff != 0) return 0;
		}
		return 0;
	}
}