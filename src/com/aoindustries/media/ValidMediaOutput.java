package com.aoindustries.media;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Indicates that at object can be trusted to generate output with only
 * valid characters for the provided type.
 *
 * @author  AO Industries, Inc.
 */
public interface ValidMediaOutput {

    /**
     * Gets the output type.
     */
    MediaType getValidMediaOutputType();
}
