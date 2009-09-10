/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util;

import com.aoindustries.encoding.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Wraps the resources with XHTML and scripts to allow the modification of the
 * resource bundle contents directly through the web interface.  Also adds an
 * indicator when the resource need to be verified.  Verification is required
 * when any other locale has a modified time greater than the verified time
 * of this locale.
 *
 * @author  AO Industries, Inc.
 */
abstract public class EditableResourceBundle extends ModifiablePropertiesResourceBundle implements Comparable<EditableResourceBundle> {

    private static final ThreadLocal<Boolean> canEditResources = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private static final ThreadLocal<Sequence> idGenerator = new ThreadLocal<Sequence>() {
        @Override
        protected Sequence initialValue() {
            return new UnsynchronizedSequence();
        }
    };

    /**
     * Every lookup during a request is logged when editing enabled.
     */
    private class Lookup implements Comparable<Lookup> {

        private final EditableResourceBundle bundle;
        private final String key;
        private final String value;
        private final MediaType mediaType;
        private final Boolean isBlockElement;
        private final boolean validated;

        private Lookup(
            EditableResourceBundle bundle,
            String key,
            String value,
            MediaType mediaType,
            Boolean isBlockElement,
            boolean validated
        ) {
            this.bundle = bundle;
            this.key = key;
            this.value = value;
            this.mediaType = mediaType;
            this.isBlockElement = isBlockElement;
            this.validated = validated;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(!(obj instanceof Lookup)) return false;
            final Lookup other = (Lookup) obj;
            return
                bundle==other.bundle
                && key.equals(other.key)
            ;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + bundle.hashCode();
            hash = 97 * hash + key.hashCode();
            return hash;
        }

        public int compareTo(Lookup o) {
            int diff = bundle.compareTo(o.bundle);
            if(diff!=0) return diff;
            diff = key.compareToIgnoreCase(o.key);
            if(diff!=0) return diff;
            return key.compareTo(o.key);
        }

        /**
         * @return the bundle
         */
        EditableResourceBundle getBundle() {
            return bundle;
        }

        /**
         * @return the key
         */
        String getKey() {
            return key;
        }

        /**
         * @return the value
         */
        String getValue() {
            return value;
        }

        MediaType getMediaType() {
            return mediaType;
        }

        Boolean isBlockElement() {
            return isBlockElement;
        }

        /**
         * @return the validated
         */
        boolean isValidated() {
            return validated;
        }
    }

    private static final ThreadLocal<SortedMap<Lookup,List<Long>>> requestLookups = new ThreadLocal<SortedMap<Lookup,List<Long>>>() {
        @Override
        protected SortedMap<Lookup,List<Long>> initialValue() {
            return new TreeMap<Lookup,List<Long>>();
        }
    };

    private static final ThreadLocal<String> setStringUrl = new ThreadLocal<String>();

    /**
     * Any page that allows the editing of resources must set this at the beginning of the request.
     * If not all users of the site are allowed to edit the content, must also clear this at the end of the request
     * by either calling this method with <code>false</code> or calling <code>printEditableResourceBundleLookups</code>.
     *
     * @param  setStringUrl Must be non-null when <code>canEditResources</code> is true.
     *
     * @see  #printEditableResourceBundleLookups(Appendable)
     */
    public static void resetRequest(boolean canEditResources, String setStringUrl) {
        if(canEditResources && setStringUrl==null) throw new IllegalArgumentException("setStringUrl is null when canEditResources is true");
        EditableResourceBundle.canEditResources.set(canEditResources);
        idGenerator.get().setNextSequenceValue(1);
        requestLookups.remove();
        EditableResourceBundle.setStringUrl.set(setStringUrl);
    }

    /**
     * Prints the resource bundle lookup editor.  This should be called at the end of a request,
     * just before the body tag is closed.
     *
     * TODO: Add language resources to properties files
     */
    public static void printEditableResourceBundleLookups(Appendable out) throws IOException {
        String setUrl = setStringUrl.get();
        SortedMap<Lookup,List<Long>> lookups = requestLookups.get();
        resetRequest(false, null);
        if(setUrl!=null && !lookups.isEmpty()) {
            out.append("<div style='position:fixed; bottom:0px; left:0px; width:100%; text-align:center'>\n");
            int invalidCount = 0;
            int missingCount = 0;
            for(Lookup lookup : lookups.keySet()) {
                if(lookup.getValue()==null) missingCount++;
                else if(!lookup.isValidated()) invalidCount++;
            }
            out.append("  <a href=\"#\" onclick=\"document.getElementById('EditableResourceBundleEditor').style.visibility=document.getElementById('EditableResourceBundleEditor').style.visibility=='visible' ? 'hidden' : 'visible'; return false;\" style=\"text-decoration:none; color:black\"><span style='border:1px solid black; opacity:.875; background-color:white'>")
                .append(Integer.toString(lookups.size())).append(lookups.size()==1 ? " Resource" : " Resources");
            if(missingCount>0) {
                out
                    .append(" | ")
                    .append(Integer.toString(missingCount))
                    .append(" Missing")
                ;
            }
            if(invalidCount>0) {
                out
                    .append(" | <span style='color:red; text-decoration:blink;'>")
                    .append(Integer.toString(invalidCount))
                    .append(" Invalidated</span>")
                ;
            }
            out.append("</span></a>\n"
                    + "</div>\n"
                    + "<div id=\"EditableResourceBundleEditor\" style=\"position:fixed; left:10%; right:10%; top:10%; bottom:10%; visibility:hidden; border-left:1px solid black; border-top:1px solid black; border-right:2px solid black; border-bottom:2px solid black; background-color:white\">\n"
                    + "  <div style=\"border-bottom:1px solid black; background-color:#c0c0c0; height:2em; overflow:hidden\">\n"
                    + "    <div style=\"float:right; border:2px outset black; margin:.3em\"><a href=\"#\" onclick=\"document.getElementById('EditableResourceBundleEditor').style.visibility='hidden'; return false;\" style=\"text-decoration:none; color:black; background-color:white; padding-left:2px; padding-right:2px;\">Close</a></div>\n"
                    + "    <div style=\"text-align:center; font-weight:bold; font-size:x-large\">Resource Editor</div>\n"
                    + "  </div>\n"
                    + "  <div style=\"position:absolute; left:0px; width:100%; top:2em; bottom:0px; overflow:auto\">\n"
                    + "    <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" style=\"width:100%\">\n"
                    + "      <tr>\n"
                    + "        <th>Bundle</th>\n"
                    + "        <th>Key</th>\n"
                    + "        <th>Media Type</th>\n"
                    + "        <th>Value</th>\n"
                    //+ "        <th>State</th>\n"
                    + "      </tr>\n");
            for(Lookup lookup : lookups.keySet()) {
                out.append("      <tr>\n"
                        + "        <td>");
                EncodingUtils.encodeHtml(lookup.getBundle().getClass().getName(), out);
                out.append("</td>\n"
                        + "        <td>");
                EncodingUtils.encodeHtml(lookup.getKey(), out);
                out.append("</td>\n"
                        + "        <td>");
                MediaType mediaType = lookup.getMediaType();
                out.append(mediaType.getMediaType());
                if(lookup.isBlockElement()!=null) out.append(" (").append(lookup.isBlockElement() ? "block" : "inline").append(')');
                out.append("</td>\n"
                        + "        <td></td>\n"
                        //+ "        <td>").append(lookup.getMediaType()==MediaType.XHTML ? lookup.isBlockElement()?"Yes":"No").append("</td>\n"
                        //+ "        <td>").append(lookup.getValue()==null?"Missing":lookup.isValidated()?"Up-to-date":"Invalidated").append("</td>\n"
                        + "      </tr>\n");
            }
            out.append("    </table>\n"
                    + "  </div>\n"
                    + "</div>\n");
        }
    }

    public EditableResourceBundle(File sourceFile) {
        super(sourceFile);
    }

    /**
     * The natural sorting is based on classname.
     */
    public int compareTo(EditableResourceBundle o) {
        return getClass().getName().compareTo(o.getClass().getName());
    }

    @Override
    public Object handleGetObject(String key) {
        if(key==null) throw new NullPointerException();
        Object object = super.handleGetObject(key);
        // Unmodifiable or editing disabled, return unaltered
        if(!isModifiable() || !canEditResources.get()) return object;

        // Must be a string
        String value = (String)object;

        // Determine if the value is validated.  The value is validated
        // when its validated time is greater than the modified time of
        // all translations
        boolean validated = false; //value!=null; // TODO

        long id = idGenerator.get().getNextSequenceValue();
        MediaType mediaType = getMediaType(key);
        Boolean isBlockElement = mediaType==MediaType.XHTML ? isBlockElement(key) : null;
        String modifiedValue;
        if(value==null) modifiedValue = null;
        else {
            // Perform optional type-specific modification
            switch(mediaType) {
                case XHTML :
                    if(isBlockElement) modifiedValue = "<div style=\"color:red\">"+value+"</div>";
                    else modifiedValue = "<span style=\"color:red\">"+value+"</span>";
                    break;
                case TEXT :
                case XHTML_PRE :
                    modifiedValue = "<<<"+value+">>>";
                    break;
                case URL :
                case XHTML_ATTRIBUTE :
                case JAVASCRIPT :
                    modifiedValue = null;
                    break;
                default :
                    throw new AssertionError("Unexpected type: "+mediaType);
            }
        }
        // Add to the log
        SortedMap<Lookup,List<Long>> lookups = requestLookups.get();
        Lookup lookup = new Lookup(this, key, value, mediaType, isBlockElement, validated);
        List<Long> ids = lookups.get(lookup);
        if(ids==null) lookups.put(lookup, ids=new ArrayList<Long>());
        ids.add(id);
        return modifiedValue==null ? value : modifiedValue;
    }
}
