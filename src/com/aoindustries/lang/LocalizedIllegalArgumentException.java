/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009  AO Industries, Inc.
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

import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.util.Locale;

/**
 * Extends <code>IllegalArgumentException</code> to provide exceptions with both JVM default locale and user locale error messages.
 *
 * @author  AO Industries, Inc.
 */
public class LocalizedIllegalArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    final private String localizedMessage;

    public LocalizedIllegalArgumentException(ApplicationResourcesAccessor accessor, Locale userLocale, String key) {
        super(accessor.getMessage(Locale.getDefault(), key));
        this.localizedMessage = accessor.getMessage(userLocale, key);
    }

    public LocalizedIllegalArgumentException(ApplicationResourcesAccessor accessor, Locale userLocale, String key, Object... args) {
        super(accessor.getMessage(Locale.getDefault(), key, args));
        this.localizedMessage = accessor.getMessage(userLocale, key, args);
    }

    public LocalizedIllegalArgumentException(Throwable cause, ApplicationResourcesAccessor accessor, Locale userLocale, String key) {
        super(accessor.getMessage(Locale.getDefault(), key), cause);
        this.localizedMessage = accessor.getMessage(userLocale, key);
    }

    public LocalizedIllegalArgumentException(Throwable cause, ApplicationResourcesAccessor accessor, Locale userLocale, String key, Object... args) {
        super(accessor.getMessage(Locale.getDefault(), key, args), cause);
        this.localizedMessage = accessor.getMessage(userLocale, key, args);
    }

    @Override
    public String getLocalizedMessage() {
        return localizedMessage;
    }
}
