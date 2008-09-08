package com.aoindustries.table;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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