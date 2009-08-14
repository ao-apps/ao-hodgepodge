/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.table;

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class ObjectArrayType implements Type {

    private static final ObjectArrayType type=new ObjectArrayType();

    public static ObjectArrayType getInstance() {
        return type;
    }

    private ObjectArrayType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            if(O2==null) return 1;
            Object[] OA1=(Object[])O1;
            Object[] OA2=(Object[])O2;
            int len1=OA1.length;
            int len2=OA2.length;
            int totalLen=len1>=len2?len1:len2;

            for(int c=0;c<totalLen;c++) {
                Object T1=c<len1?OA1[c]:null;
                Object T2=c<len2?OA2[c]:null;

                if(T1==null) {
                    if(T2!=null) return -1;
                } else {
                    if(T2==null) return 1;
                    int diff=T1.toString().compareTo(T2.toString());
                    if(diff!=0) return diff;
                }
            }
            return 0;
        }
    }
    
    public Object getDisplay(Object O) {
        Object[] OA=(Object[])O;
        int len=OA.length;
        if(len==0) return "";
        else if(len==1) return OA[0];
        StringBuilder SB=new StringBuilder();
        for(int c=0;c<len;c++) {
            if(c>0) SB.append(", ");
            SB.append(OA[c].toString());
        }
        return SB.toString();
    }
    
    public Class getTypeClass() {
        return Object.class;
    }
}