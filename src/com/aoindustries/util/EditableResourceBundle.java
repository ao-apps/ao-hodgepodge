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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final ThreadLocal<Sequence> elementIdGenerator = new ThreadLocal<Sequence>() {
        @Override
        protected Sequence initialValue() {
            return new UnsynchronizedSequence();
        }
    };

    private static final ThreadLocal<Sequence> lookupIdGenerator = new ThreadLocal<Sequence>() {
        @Override
        protected Sequence initialValue() {
            return new UnsynchronizedSequence();
        }
    };

    /**
     * Every lookup during a request is logged when editing enabled.
     */
    static class LookupKey implements Comparable<LookupKey> {

        final EditableResourceBundle bundle;
        final String key;
        final MediaType mediaType;
        final Boolean isBlockElement;

        LookupKey(
            EditableResourceBundle bundle,
            String key,
            MediaType mediaType,
            Boolean isBlockElement
        ) {
            this.bundle = bundle;
            this.key = key;
            this.mediaType = mediaType;
            this.isBlockElement = isBlockElement;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(!(obj instanceof LookupKey)) return false;
            final LookupKey other = (LookupKey) obj;
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

        public int compareTo(LookupKey o) {
            int diff = bundle.compareTo(o.bundle);
            if(diff!=0) return diff;
            diff = key.compareToIgnoreCase(o.key);
            if(diff!=0) return diff;
            return key.compareTo(o.key);
        }
    }

    static class LookupValue {
        final long id = lookupIdGenerator.get().getNextSequenceValue();
        final List<Long> ids = new ArrayList<Long>();
        int missingCount = 0;
        int invalidatedCount = 0;
        LookupValue() {}
    }

    private static final ThreadLocal<Map<LookupKey,LookupValue>> requestLookups = new ThreadLocal<Map<LookupKey,LookupValue>>() {
        @Override
        protected Map<LookupKey,LookupValue> initialValue() {
            return new HashMap<LookupKey,LookupValue>();
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
        elementIdGenerator.get().setNextSequenceValue(1);
        lookupIdGenerator.get().setNextSequenceValue(1);
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
        final Map<LookupKey,LookupValue> lookups = requestLookups.get();
        resetRequest(false, null);
        if(!lookups.isEmpty()) {
            // Sort by lookupValue.id to present the information in the same order as first seen in the request
            List<LookupKey> lookupKeys = new ArrayList<LookupKey>(lookups.keySet());
            Collections.sort(
                lookupKeys,
                new Comparator<LookupKey>() {
                    public int compare(LookupKey key1, LookupKey key2) {
                        return Long.valueOf(lookups.get(key1).id).compareTo(Long.valueOf(lookups.get(key2).id));
                    }
                }
            );
            if(setUrl!=null) {
                out.append("<div style='position:fixed; bottom:0px; left:0px; width:100%; text-align:center'>\n");
                int invalidatedCount = 0;
                int missingCount = 0;
                for(LookupValue lookupValue : lookups.values()) {
                    missingCount += lookupValue.missingCount;
                    invalidatedCount += lookupValue.invalidatedCount;
                }
                out.append("  <a href=\"#\" onclick=\"if(EditableResourceBundleEditorSetVisibility) EditableResourceBundleEditorSetVisibility(document.getElementById('EditableResourceBundleEditor').style.visibility=='visible' ? 'hidden' : 'visible'); return false;\" style=\"text-decoration:none; color:black\"><span style='border:1px solid black; background-color:white'>")
                    .append(Integer.toString(lookups.size())).append(lookups.size()==1 ? " Resource" : " Resources");
                if(missingCount>0) {
                    out
                        .append(" | ")
                        .append(Integer.toString(missingCount))
                        .append(" Missing")
                    ;
                }
                if(invalidatedCount>0) {
                    out
                        .append(" | <span style='color:red; text-decoration:blink;'>")
                        .append(Integer.toString(invalidatedCount))
                        .append(" Invalidated</span>")
                    ;
                }
                out.append("</span></a>\n"
                        + "</div>\n"
                        + "<div id=\"EditableResourceBundleEditor\" style=\"position:fixed; left:50px; width:640px; top:50px; height:480px; visibility:hidden; border-left:1px solid black; border-top:1px solid black; border-right:2px solid black; border-bottom:2px solid black; background-color:white\">\n"
                        + "  <div id=\"EditableResourceBundleEditorHeader\" style=\"border-bottom:1px solid black; background-color:#c0c0c0; height:2em; overflow:hidden\">\n"
                        + "    <div style=\"float:right; border:2px outset black; margin:.3em\"><a href=\"#\" onclick=\"if(EditableResourceBundleEditorSetVisibility) EditableResourceBundleEditorSetVisibility('hidden'); return false;\" style=\"text-decoration:none; color:black; background-color:white; padding-left:2px; padding-right:2px;\">✕</a></div>\n"
                        + "    <script type='text/javascript'>\n"
                        + "      // <![CDATA[\n"
                        + "      function EditableResourceBundleEditorSetCookie(c_name,value,expiredays) {\n"
                        + "        var exdate=new Date();\n"
                        + "        exdate.setDate(exdate.getDate()+expiredays);\n"
                        + "        document.cookie=c_name+\"=\"+escape(value)+((expiredays==null)?\"\":\"; expires=\"+exdate.toGMTString())+\"; path=/\";\n"
                        + "      }\n"
                        + "\n"
                        + "      // From http://www.w3schools.com/JS/js_cookies.asp\n"
                        + "      function EditableResourceBundleEditorGetCookie(c_name) {\n"
                        + "        if (document.cookie.length>0) {\n"
                        + "          c_start=document.cookie.indexOf(c_name + \"=\");\n"
                        + "          if (c_start!=-1) {\n"
                        + "            c_start=c_start + c_name.length+1;\n"
                        + "            c_end=document.cookie.indexOf(\";\",c_start);\n"
                        + "            if (c_end==-1) c_end=document.cookie.length;\n"
                        + "              return unescape(document.cookie.substring(c_start,c_end));\n"
                        + "            }\n"
                        + "        }\n"
                        + "        return \"\";\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorSetVisibility(visibility) {\n"
                        + "        document.getElementById('EditableResourceBundleEditor').style.visibility=visibility;\n"
                        + "        EditableResourceBundleEditorSetCookie(\"EditableResourceBundleEditorVisibility\", visibility, 31);\n"
                        + "      }\n"
                        + "\n"
                        + "      var EditableResourceBundleEditorSelectedRow = null;\n"
                        + "      function EditableResourceBundleEditorSelectedRowOnClick(row, originalBackground) {\n"
                        + "        row.EditableResourceBundleEditorSelectedRowOriginalBackground=originalBackground;\n"
                        + "        if(EditableResourceBundleEditorSelectedRow!=row) {\n"
                        + "          if(EditableResourceBundleEditorSelectedRow!=null) {\n" // && EditableResourceBundleEditorSelectedRow.style.backgroundColor!='yellow'
                        + "            EditableResourceBundleEditorSelectedRow.style.backgroundColor=EditableResourceBundleEditorSelectedRow.EditableResourceBundleEditorSelectedRowOriginalBackground;\n"
                        + "          }\n"
                        + "          EditableResourceBundleEditorSelectedRow=row;\n"
                        + "        }\n"
                        + "        row.style.backgroundColor=\"red\";\n"
                        + "      }\n"
                        + "\n"
                        + "      var EditableResourceBundleEditorDragElem=null;\n"
                        + "      var EditableResourceBundleEditorResizeElem=null;\n"
                        + "      var EditableResourceBundleEditorDownScreenX;\n"
                        + "      var EditableResourceBundleEditorDownScreenY;\n"
                        + "      var EditableResourceBundleEditorDownElemX;\n"
                        + "      var EditableResourceBundleEditorDownElemY;\n"
                        + "      var EditableResourceBundleEditorDownElemWidth;\n"
                        + "      var EditableResourceBundleEditorDownElemHeight;\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorDragMouseMove(event) {\n"
                        + "        if(EditableResourceBundleEditorDragElem!=null) {\n"
                        + "          var editorStyle=document.getElementById('EditableResourceBundleEditor').style;"
                        + "          editorStyle.left=(EditableResourceBundleEditorDownElemX+event.screenX-EditableResourceBundleEditorDownScreenX)+'px';\n"
                        + "          editorStyle.top=(EditableResourceBundleEditorDownElemY+event.screenY-EditableResourceBundleEditorDownScreenY)+'px';\n"
                        + "          EditableResourceBundleEditorSetCookie(\"EditableResourceBundleEditorLeft\", editorStyle.left, 31);\n"
                        + "          EditableResourceBundleEditorSetCookie(\"EditableResourceBundleEditorTop\", editorStyle.top, 31);\n"
                        + "          event.preventDefault();\n"
                        + "          return false;\n"
                        + "        }\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorDragMouseUp(event) {\n"
                        + "        if(EditableResourceBundleEditorDragElem!=null) EditableResourceBundleEditorDragElem.style.cursor='auto';\n"
                        + "        EditableResourceBundleEditorDragElem=null;\n"
                        + "        document.removeEventListener('mousemove', EditableResourceBundleEditorDragMouseMove, true);\n"
                        + "        document.removeEventListener('mouseup', EditableResourceBundleEditorDragMouseUp, true);\n"
                        + "        document.getElementById('EditableResourceBundleEditorHeader').style.backgroundColor='#c0c0c0';\n"
                        + "        event.preventDefault();\n"
                        + "        return false;\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorDragMouseDown(elem, event) {\n"
                        + "        EditableResourceBundleEditorDragElem=elem;\n"
                        + "        EditableResourceBundleEditorDownScreenX=event.screenX;\n"
                        + "        EditableResourceBundleEditorDownScreenY=event.screenY;\n"
                        + "        EditableResourceBundleEditorDownElemX=parseInt(document.getElementById('EditableResourceBundleEditor').style.left);\n"
                        + "        EditableResourceBundleEditorDownElemY=parseInt(document.getElementById('EditableResourceBundleEditor').style.top);\n"
                        + "        document.addEventListener('mousemove', EditableResourceBundleEditorDragMouseMove, true);\n"
                        + "        document.addEventListener('mouseup', EditableResourceBundleEditorDragMouseUp, true);\n"
                        + "        elem.style.cursor='move';\n"
                        + "        document.getElementById('EditableResourceBundleEditorHeader').style.backgroundColor='red';\n"
                        + "        event.preventDefault();\n"
                        + "        return false;\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorResizeMouseMove(event) {\n"
                        + "        if(EditableResourceBundleEditorResizeElem!=null) {\n"
                        + "          var editorStyle=document.getElementById('EditableResourceBundleEditor').style;"
                        + "          editorStyle.width=Math.max(100, EditableResourceBundleEditorDownElemWidth+event.screenX-EditableResourceBundleEditorDownScreenX)+'px';\n"
                        + "          editorStyle.height=Math.max(100, EditableResourceBundleEditorDownElemHeight+event.screenY-EditableResourceBundleEditorDownScreenY)+'px';\n"
                        + "          EditableResourceBundleEditorSetCookie(\"EditableResourceBundleEditorWidth\", editorStyle.width, 31);\n"
                        + "          EditableResourceBundleEditorSetCookie(\"EditableResourceBundleEditorHeight\", editorStyle.height, 31);\n"
                        + "          event.preventDefault();\n"
                        + "          return false;\n"
                        + "        }\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorResizeMouseUp(event) {\n"
                        + "        EditableResourceBundleEditorResizeElem=null;\n"
                        + "        document.removeEventListener('mousemove', EditableResourceBundleEditorResizeMouseMove, true);\n"
                        + "        document.removeEventListener('mouseup', EditableResourceBundleEditorResizeMouseUp, true);\n"
                        + "        document.getElementById('EditableResourceBundleEditor').style.borderColor='black black black black';\n"
                        + "        event.preventDefault();\n"
                        + "        return false;\n"
                        + "      }\n"
                        + "\n"
                        + "      function EditableResourceBundleEditorResizeMouseDown(elem, event) {\n"
                        + "        EditableResourceBundleEditorResizeElem=elem;\n"
                        + "        EditableResourceBundleEditorDownScreenX=event.screenX;\n"
                        + "        EditableResourceBundleEditorDownScreenY=event.screenY;\n"
                        + "        EditableResourceBundleEditorDownElemWidth=parseInt(document.getElementById('EditableResourceBundleEditor').style.width);\n"
                        + "        EditableResourceBundleEditorDownElemHeight=parseInt(document.getElementById('EditableResourceBundleEditor').style.height);\n"
                        + "        document.addEventListener('mousemove', EditableResourceBundleEditorResizeMouseMove, true);\n"
                        + "        document.addEventListener('mouseup', EditableResourceBundleEditorResizeMouseUp, true);\n"
                        + "        document.getElementById('EditableResourceBundleEditor').style.borderColor='red red red red';\n"
                        + "        event.preventDefault();\n"
                        + "        return false;\n"
                        + "      }\n"
                        + "      // ]]>\n"
                        + "    </script>\n"
                        + "    <div"
                            + " style=\"text-align:center; font-weight:bold; font-size:x-large\""
                            + " onmousedown=\"return EditableResourceBundleEditorDragMouseDown(this, event);\""
                            + ">Resource Editor</div>\n"
                        + "  </div>\n"
                        + "  <div id=\"EditableResourceBundleEditorScroller\" style=\"position:absolute; left:0px; width:100%; top:2em; bottom:0px; overflow:auto\">\n"
                        + "    <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" style=\"width:100%\">\n"
                        + "      <tr style=\"background-color:#e0e0e0\">\n"
                        + "        <th></th>\n"
                        + "        <th>Bundle</th>\n"
                        + "        <th>Key</th>\n"
                        + "        <th>Media Type</th>\n"
                        + "        <th>Value</th>\n"
                        //+ "        <th>State</th>\n"
                        + "      </tr>\n");
                int i = 0;
                for(LookupKey lookupKey : lookupKeys) {
                    LookupValue lookupValue = lookups.get(lookupKey);
                    List<Long> ids = lookupValue.ids;
                    i++;
                    String id = ids.get(0).toString();
                    out.append("      <tr"
                            + " id=\"EditableResourceBundleEditorRow").append(id).append("\""
                            + " style=\"background-color:").append((i&1)==1 ? "white" : "#e0e0e0").append('"'
                            + " onclick=\"EditableResourceBundleEditorSelectedRowOnClick(this, '").append((i&1)==1 ? "white" : "#e0e0e0").append("');\""
                            + " onmouseover=\"if(typeof EditableResourceBundleHighlightAll == 'function') EditableResourceBundleHighlightAll(").append(id).append(", false);\""
                            + " onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == 'function') EditableResourceBundleUnhighlightAll(").append(ids.get(0).toString()).append(");\""
                            + ">\n"
                            + "        <td style=\"text-align:right\">").append(Long.toString(lookupValue.id)).append("</td>\n"
                            + "        <td>");
                    EncodingUtils.encodeHtml(lookupKey.bundle.getClass().getName(), out);
                    out.append("</td>\n"
                            + "        <td>");
                    EncodingUtils.encodeHtml(lookupKey.key, out);
                    out.append("</td>\n"
                            + "        <td>");
                    MediaType mediaType = lookupKey.mediaType;
                    out.append(mediaType.getMediaType());
                    if(lookupKey.isBlockElement!=null) out.append(" (").append(lookupKey.isBlockElement ? "block" : "inline").append(')');
                    out.append("</td>\n"
                            + "        <td></td>\n"
                            //+ "        <td>").append(lookup.getMediaType()==MediaType.XHTML ? lookup.isBlockElement()?"Yes":"No").append("</td>\n"
                            //+ "        <td>").append(lookup.getValue()==null?"Missing":lookup.isValidated()?"Up-to-date":"Invalidated").append("</td>\n"
                            + "      </tr>\n");
                }
                out.append("    </table>\n"
                        + "  </div>\n"
                        + "  <div"
                            + " style=\"position:absolute; right:-4px; width:19px; bottom:-4px; height:19px; overflow:hidden; cursor:nw-resize\""
                            + " onmousedown=\"return EditableResourceBundleEditorResizeMouseDown(this, event);\""
                        + "></div>\n"
                        + "</div>\n");
            }
            // Highlight and editor functions
            out.append("<script type='text/javascript'>\n"
                    + "  // <![CDATA[\n"
                    + "\n"
                    + "  // Restore the editor to its previous position\n"
                    + "  var EditableResourceBundleEditorStyle=document.getElementById(\"EditableResourceBundleEditor\").style;\n"
                    + "  var EditableResourceBundleEditorWidth = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorWidth\");\n"
                    + "  if(EditableResourceBundleEditorWidth!=\"\") EditableResourceBundleEditorStyle.width=EditableResourceBundleEditorWidth;\n"
                    + "  var EditableResourceBundleEditorHeight = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorHeight\");\n"
                    + "  if(EditableResourceBundleEditorHeight!=\"\") EditableResourceBundleEditorStyle.height=EditableResourceBundleEditorHeight;\n"
                    + "  var EditableResourceBundleEditorTop = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorTop\");\n"
                    + "  if(EditableResourceBundleEditorTop!=\"\" && (parseInt(EditableResourceBundleEditorTop)+parseInt(EditableResourceBundleEditorStyle.height))<=window.innerHeight) EditableResourceBundleEditorStyle.top=EditableResourceBundleEditorTop;\n"
                    + "  var EditableResourceBundleEditorLeft = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorLeft\");\n"
                    + "  if(EditableResourceBundleEditorLeft!=\"\" && (parseInt(EditableResourceBundleEditorLeft)+parseInt(EditableResourceBundleEditorStyle.width))<=window.innerWidth) EditableResourceBundleEditorStyle.left=EditableResourceBundleEditorLeft;\n"
                    + "  var EditableResourceBundleEditorVisibility = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorVisibility\");\n"
                    + "  if(EditableResourceBundleEditorVisibility!=\"\") EditableResourceBundleEditorStyle.visibility=EditableResourceBundleEditorVisibility;\n"
                    + "\n"
                    + "  var EditableResourceBundleElementSets=[");
            boolean didOne1 = false;
            for(LookupKey lookupKey : lookupKeys) {
                LookupValue lookupValue = lookups.get(lookupKey);
                if(didOne1) out.append(',');
                else didOne1 = true;
                out.append("\n    [");
                boolean didOne2 = false;
                for(Long id : lookupValue.ids) {
                    if(didOne2) out.append(',');
                    else didOne2 = true;
                    out.append(id.toString());
                }
                out.append(']');
            }
            out.append("\n  ];\n"
                    + "  function EditableResourceBundleSetAllBackgrounds(id, background, scrollEditor) {\n"
                    + "    for(var c=0; c<EditableResourceBundleElementSets.length; c++) {\n"
                    + "      var ids = EditableResourceBundleElementSets[c];\n"
                    + "      for(var d=0; d<ids.length; d++) {\n"
                    + "        if(id==ids[d]) {\n"
                    + "          var elem=document.getElementById(\"EditableResourceBundleEditorRow\"+ids[0]);\n"
                    + "          if(elem!=null) {\n"
                    + "            elem.style.backgroundColor=elem==(!scrollEditor && EditableResourceBundleEditorSelectedRow) ? \"red\" : background!=\"transparent\" ? background : elem==EditableResourceBundleEditorSelectedRow ? \"red\" : (c&1)==0 ? \"white\" : \"#e0e0e0\";\n"
                    + "            if(scrollEditor) {\n"
                    + "              var scroller=document.getElementById(\"EditableResourceBundleEditorScroller\");\n"
                    + "              scroller.scrollTop=elem.offsetTop-(scroller.clientHeight-elem.offsetHeight)/2;\n" // Centered
                    + "            }\n"
                    + "          }\n"
                    + "          for(var e=0; e<ids.length; e++) {\n"
                    + "            elem=document.getElementById(\"EditableResourceBundleElement\"+ids[e]);\n"
                    + "            if(elem!=null) elem.style.backgroundColor=background;\n"
                    + "          }\n"
                    + "          return;\n"
                    + "        }\n"
                    + "      }\n"
                    + "    }\n"
                    + "  }\n"
                    + "  function EditableResourceBundleHighlightAll(id, scrollEditor) {\n"
                    + "    EditableResourceBundleSetAllBackgrounds(id, \"yellow\", scrollEditor);\n"
                    + "  }\n"
                    + "  function EditableResourceBundleUnhighlightAll(id) {\n"
                    + "    EditableResourceBundleSetAllBackgrounds(id, \"transparent\", false);\n"
                    + "  }\n"
                    + "  // ]]>\n"
                    + "</script>\n"
            );
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
        boolean invalidated = true; //value!=null; // TODO

        long elementId = elementIdGenerator.get().getNextSequenceValue();
        MediaType mediaType = getMediaType(key);
        Boolean isBlockElement = mediaType==MediaType.XHTML ? isBlockElement(key) : null;

        // Add to the log
        Map<LookupKey,LookupValue> lookups = requestLookups.get();
        LookupKey lookupKey = new LookupKey(this, key, mediaType, isBlockElement);
        LookupValue lookupValue = lookups.get(lookupKey);
        if(lookupValue==null) lookups.put(lookupKey, lookupValue = new LookupValue());
        lookupValue.ids.add(elementId);
        if(value==null) lookupValue.missingCount++;
        if(invalidated) lookupValue.invalidatedCount++;

        // Modify and return the value
        String modifiedValue;
        if(value==null) modifiedValue = null;
        else {
            // Perform optional type-specific modification
            switch(mediaType) {
                case XHTML :
                    StringBuilder SB = new StringBuilder();
                    SB
                        .append("<!--").append(lookupValue.id).append("-->")
                        .append(isBlockElement ? "<div" : "<span")
                        .append(" id=\"EditableResourceBundleElement").append(elementId).append("\"")
                    ;
                    if(invalidated) SB.append(" style=\"color:red\"");
                    SB
                        .append(" onmouseover=\"if(typeof EditableResourceBundleHighlightAll == 'function') EditableResourceBundleHighlightAll(").append(elementId).append(", true);\"")
                        .append(" onmouseout=\"if(typeof EditableResourceBundleUnhighlightAll == 'function') EditableResourceBundleUnhighlightAll(").append(elementId).append(");\"")
                        .append('>')
                        .append(value)
                        .append(isBlockElement ? "</div>" : "</span>")
                    ;
                    modifiedValue = SB.toString();
                    break;
                case TEXT :
                case XHTML_PRE :
                    // <#< and >#> used to cause XHTML parse errors if text value not properly escaped
                    modifiedValue = invalidated ? value : ("<"+lookupValue.id+"<"+value+">"+lookupValue.id+">");
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
        return modifiedValue==null ? value : modifiedValue;
    }
}
