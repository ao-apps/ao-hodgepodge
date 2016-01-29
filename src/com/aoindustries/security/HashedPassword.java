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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A salted, hashed and key stretched password.
 * {@link https://crackstation.net/hashing-security.htm}
 *
 * @author  AO Industries, Inc.
 */
public class HashedPassword {
	
	/** From http://crackstation.net/hashing-security.htm */
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	/** The number of bytes in the random salt. */
	public static final int SALT_BYTES = 256 / 8;

	/** The number of bytes in the hash. */
	public static final int HASH_BYTES = 256 / 8;

	/**
	 * The recommended number of iterations for typical usage.
	 * <p>
	 * We may change this value between releases without notice.
	 * Only use this value for new password hashes.
	 * Always store the iterations with the salt and hash, and use the stored
	 * iterations when checking password matches.
	 * </p>
	 *
	 * @see  #hash(java.lang.String, byte[], int) 
	 */
	public static final int RECOMMENDED_ITERATIONS = 1000;

	private static final SecureRandom secureRandom = new SecureRandom();

	/**
	 * Generates a random salt of <code>SALT_BYTES</code> bytes in length.
	 * 
	 * @see  #hash(java.lang.String, byte[], int) 
	 */
	public static byte[] generateSalt() {
		byte[] salt = new byte[SALT_BYTES];
		secureRandom.nextBytes(salt);
		return salt;
	}

	/**
	 * Hash the given password
	 * 
	 * @see  #generateSalt()
	 * @see  #RECOMMENDED_ITERATIONS
	 */
	public static byte[] hash(String password, byte[] salt, int iterations) {
		try {
			// See http://crackstation.net/hashing-security.htm
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, HASH_BYTES * 8);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			byte[] hash = skf.generateSecret(spec).getEncoded();
			assert hash.length == HASH_BYTES;
			return hash;
		} catch(InvalidKeySpecException e) {
			throw new WrappedException(e);
		} catch(NoSuchAlgorithmException e) {
			throw new WrappedException(e);
		}
	}

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
