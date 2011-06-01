/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.dao;

import com.aoindustries.util.i18n.ThreadLocale;
import java.text.Collator;

public final class SingleReason extends Reason {

    private final String reason;

    public SingleReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    SingleReason merge(Reason other) {
        if(!(other instanceof SingleReason)) return null;
        SingleReason otherSingleReason = (SingleReason)other;
        // Must have the same reason
        if(reason.equals(otherSingleReason.reason)) return this;
        return null;
    }

    @Override
    public int compareTo(Reason other) {
        if(other instanceof SingleReason) {
            SingleReason otherSingleReason = (SingleReason)other;
            return Collator.getInstance(ThreadLocale.get()).compare(reason, otherSingleReason.reason);
        } else {
            return -1; // Single reasons before aggregate
        }
    }
}
