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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wraps the resources with XHTML and scripts to allow the modification of the
 * resource bundle contents directly through the web interface.  Also adds an
 * indicator when the resource need to be verified.  Verification is required
 * when any other locale has a modified time greater than the verified time
 * of this locale.
 *
 * @author  AO Industries, Inc.
 */
abstract public class EditableResourceBundle extends ModifiablePropertiesResourceBundle {

    private static final ThreadLocal<Boolean> canEditResources = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    /**
     * Any page that allows the editing of resources must set this at the beginning of the request.
     * If not all users of the site are allowed to edit the content, must also clear this at the end of the request.
     */
    public static void setCanEditResources(boolean canEditResources) {
        EditableResourceBundle.canEditResources.set(canEditResources);
    }

    /**
     * Every lookup during a request is logged when editing enabled.
     * TODO: Keep map of lookup to ids because some lookups performed repeatedly with same values.
     * TODO: Interface should only ask once and update all as needed.
     */
    public class Lookup {
        /**
         * Matches the value used in the span id
         */
        private final long id;
        private final EditableResourceBundle bundle;
        private final String key;
        private final String value;
        private final MediaType mediaType;
        private final Boolean isBlockElement;
        private final boolean validated;
        private final String modifiedValue;

        private Lookup(
            long id,
            EditableResourceBundle bundle,
            String key,
            String value,
            MediaType mediaType,
            Boolean isBlockElement,
            boolean validated,
            String modifiedValue
        ) {
            this.id = id;
            this.bundle = bundle;
            this.key = key;
            this.value = value;
            this.mediaType = mediaType;
            this.isBlockElement = isBlockElement;
            this.validated = validated;
            this.modifiedValue = modifiedValue;
        }

        /**
         * @return the id
         */
        public long getId() {
            return id;
        }

        /**
         * @return the bundle
         */
        public EditableResourceBundle getBundle() {
            return bundle;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public Boolean isBlockElement() {
            return isBlockElement;
        }

        /**
         * @return the validated
         */
        public boolean isValidated() {
            return validated;
        }

        /**
         * @return the modifiedValue
         */
        public String getModifiedValue() {
            return modifiedValue;
        }
    }

    private static final ThreadLocal<List<Lookup>> requestLookups = new ThreadLocal<List<Lookup>>() {
        @Override
        protected List<Lookup> initialValue() {
            return new ArrayList<Lookup>();
        }
    };

    /**
     * Gets all the lookups for the current request and clears canEditResources.
     */
    public static List<Lookup> getAndResetRequestLookups() {
        setCanEditResources(false);
        List<Lookup> lookups = requestLookups.get();
        requestLookups.remove();
        return lookups;
    }

    private static final ThreadLocal<String> setStringUrl = new ThreadLocal<String>();

    // TODO: Add language resources to properties files
    public static void printEditableResourceBundleLookups(Appendable out) throws IOException {
        List<EditableResourceBundle.Lookup> lookups = EditableResourceBundle.getAndResetRequestLookups();
        if(!lookups.isEmpty()) {
            out.append("    <div style='position:fixed; bottom:0px; left:0px; width:100%; text-align:center'>\n");
            int invalidCount = 0;
            for(EditableResourceBundle.Lookup lookup : lookups) {
                if(!lookup.isValidated()) invalidCount++;
            }
            out.append("<span style='border:1px solid black; opacity:.625; background-color:white'>");
            out.append(Integer.toString(lookups.size()));
            out.append(lookups.size()==1 ? " Resource" : " Resources");
            out.append(": ");
            if(invalidCount>0) {
                out.append("<span style='color:red; text-decoration:blink;'>Validate ");
                out.append(Integer.toString(invalidCount));
                out.append(invalidCount==1 ? " Resource" : " Resources");
                out.append("</span>");
            } else {
                out.append("Edit Resources");
            }
            out.append("</span>\n"
                    + "    </div>\n");
                    /*
                    + "      <table>\n"
                    + "        <tr>\n"
                    + "          <th>TODO</th>\n"
                    + "        </tr>\n"
                    + "      </table>\n"*/
        }
    }

    private static final AtomicLong idGenerator = new AtomicLong(1);

    public EditableResourceBundle(File sourceFile) {
        super(sourceFile);
    }

    @Override
    public Object handleGetObject(String key) {
        if(key==null) throw new NullPointerException();
        String value = (String)super.handleGetObject(key);
        if(value==null) return value;
        // Unmodifiable or editing disabled, return unaltered
        if(!isModifiable() || !canEditResources.get()) return value;
        // Determine if the value is validated
        boolean validated = false; // TODO
        long id = idGenerator.getAndIncrement();
        MediaType mediaType = getMediaType(key);
        Boolean isBlockElement = null;
        String modifiedValue;
        switch(mediaType) {
            case XHTML :
                if(isBlockElement = isBlockElement(key)) modifiedValue = "<div style=\"color:red\">"+value+"</div>";
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
        // Add to the log
        requestLookups.get().add(new Lookup(id, this, key, value, mediaType, isBlockElement, validated, modifiedValue));
        return modifiedValue==null ? value : modifiedValue;
    }
}
