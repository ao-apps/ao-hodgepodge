/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.lang;

import com.aoindustries.util.AoArrays;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.io.Serializable;

/**
 * Extends <code>IllegalArgumentException</code> to provide exceptions in user locale error messages.
 *
 * @author  AO Industries, Inc.
 */
public class LocalizedIllegalArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = -4941714282849295887L;

    private final ApplicationResourcesAccessor accessor;
    private final String key;
    private final Serializable[] args;

    public LocalizedIllegalArgumentException(ApplicationResourcesAccessor accessor, String key) {
        super(accessor.getMessage(key));
        this.accessor = accessor;
        this.key = key;
        this.args = AoArrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public LocalizedIllegalArgumentException(ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(accessor.getMessage(key, (Object[])args));
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    public LocalizedIllegalArgumentException(Throwable cause, ApplicationResourcesAccessor accessor, String key) {
        super(accessor.getMessage(key), cause);
        this.accessor = accessor;
        this.key = key;
        this.args = AoArrays.EMPTY_SERIALIZABLE_ARRAY;
    }

    public LocalizedIllegalArgumentException(Throwable cause, ApplicationResourcesAccessor accessor, String key, Serializable... args) {
        super(accessor.getMessage(key, (Object[])args), cause);
        this.accessor = accessor;
        this.key = key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return accessor.getMessage(key, (Object[])args);
    }
}