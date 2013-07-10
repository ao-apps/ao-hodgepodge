/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013  AO Industries, Inc.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Reason implements Comparable<Reason> {

    /**
     * Merges a single reason, if not null.
     * Helper for the generation of cannot remove reasons.
     *
     * @return  the (possibly) new list.
     */
    public static List<Reason> addReason(List<Reason> reasons, Reason newReason) {
        if(newReason!=null) {
            int size = reasons.size();
            if(size==0) {
                reasons = new ArrayList<>();
                reasons.add(newReason);
            } else {
                boolean needAdd = true;
                for(int c=0; c<size; c++) {
                    Reason merged = reasons.get(c).merge(newReason);
                    if(merged!=null) {
                        reasons.set(c, merged);
                        needAdd = false;
                        break;
                    }
                }
                if(needAdd) reasons.add(newReason);
            }
        }
        return reasons;
    }

    /**
     * Combines two lists.
     * Helper for the generation of cannot remove reasons.
     *
     * @return  the (possibly) new list.
     */
    public static List<Reason> addReasons(List<Reason> reasons, List<Reason> newReasons) {
        for(Reason newReason : newReasons) reasons = addReason(reasons, newReason);
        return reasons;
    }

    /**
     * Adds a cannot remove reason if the provided collection is non-null and
     * non-empty.
     * Helper for the generation of cannot remove reasons.
     *
     * @return  the (possibly) new list.
     *
     * @see  Removable#getCannotRemoveReasons()
     */
    public static List<Reason> addUsedByReason(List<Reason> reasons, Collection<?> dependencies, String singularName, String pluralName) {
        // TODO: This should use application resources and keys passed-in instead of direct text
        if(dependencies!=null) {
            int count = dependencies.size();
            if(count>0) {
                reasons = addReason(
                    reasons,
                    new AggregateReason(
                        count,
                        "Used by ",
                        "Used by ",
                        ' ' + singularName + '.',
                        ' ' + pluralName + "."
                    )
                );
            }
        }
        return reasons;
    }

    @Override
    abstract public String toString();

    /**
     * Merges this reason with the provided reason, if possible.
     *
     * @return  the new reason or <code>null</code> if they cannot be merged.
     */
    abstract Reason merge(Reason other);
}
