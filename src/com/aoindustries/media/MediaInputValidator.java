package com.aoindustries.media;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Indicates that the object validates its input for the provided type.
 * If invalid characters are received it will throw an appropriate exception.
 *
 * @author  AO Industries, Inc.
 */
public interface MediaInputValidator {

    /**
     * Checks if this is validating the provided type.
     */
    boolean isValidatingMediaInputType(MediaType inputType);
}
