/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.i18n;

import com.aoapps.lang.i18n.LocaleComparator;
import com.aoapps.lang.io.ContentType;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.util.AtomicSequence;
import com.aoapps.lang.util.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Wraps the resources with XHTML and scripts to allow the modification of the
 * resource bundle contents directly through the web interface.  Also adds an
 * indicator when the resource needs to be verified.  Verification is required
 * when any other locale has a modified time greater than the verified time
 * of this locale.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Implement as a ResourceBundleControlProvider, which would then load all editable resource bundles via
//       ServiceLoader, instead of having to declare a subclass of EditableResourceBundle for every bundle and language.
//       This, in turn, would mean a project could be used directly as its ApplicationResources*properties files, but
//       would also be activated as editable only in a development mode.  This, without depending directly on
//       ao-hodgepodge or having to split into a separate *-i18n sub-project.  When going this route, consider the
//       implications of ResourceBundleControlProvider having to be a "Java Extension Mechanism" - will it work in a
//       servlet environment without modification of the Tomcat libraries?
//       https://docs.oracle.com/javase/8/docs/api/java/util/spi/ResourceBundleControlProvider.html
//       https://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.Control.html
//       https://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.html
//       https://docs.oracle.com/javase/8/docs/technotes/guides/extensions/index.html
//

// TODO: In a future major version update, fail when properties source file is unavailable.  This will only be practical
//       after all uses are from *-i18n sub-projects that are not deployed.
public abstract class EditableResourceBundle extends ModifiablePropertiesResourceBundle implements Comparable<EditableResourceBundle> {

  public static final String VISIBILITY_COOKIE_NAME = "EditableResourceBundleEditorVisibility";

  /**
   * Value used for empty strings, to avoid ambiguity.  Other submitted empty will be removed.
   */
  public static final String EMPTY_DISPLAY = "[BLANK]";

  /**
   * Settings for the current thread.  This object is {@linkplain I18nThreadLocalRunnable copied during invocation of subtasks}
   * and therefore has a thread-safe implementation.
   */
  public static class ThreadSettings {

    public enum Mode {
      DISABLED,
      LOOKUP,
      NOSCRIPT,
      MARKUP
    }

    // Never changed
    private final String setValueUrl;

    // May be changed
    private final Mode mode;
    private final boolean modifyAllText;

    private final Sequence elementIdGenerator;

    private final Sequence lookupIdGenerator;

    /**
     * Use of LookupValue must be synchronized on the LookupValue instance.
     */
    private final ConcurrentMap<LookupKey, LookupValue> requestLookups;

    /**
     * The lookup context that will be active when markup is enabled.
     */
    private final BundleLookupThreadContext lookupContext;

    private ThreadSettings(
        String setValueUrl,
        Mode mode,
        boolean modifyAllText,
        Sequence elementIdGenerator,
        Sequence lookupIdGenerator,
        ConcurrentMap<LookupKey, LookupValue> requestLookups,
        BundleLookupThreadContext lookupContext
    ) {
      if (setValueUrl != null) {
        // Strip any anchor
        int anchorPos = setValueUrl.lastIndexOf('#');
        if (anchorPos != -1) {
          setValueUrl = setValueUrl.substring(0, anchorPos);
        }
      }
      this.setValueUrl = setValueUrl;
      this.mode = mode;
      this.modifyAllText = modifyAllText;
      this.elementIdGenerator = elementIdGenerator;
      this.lookupIdGenerator = lookupIdGenerator;
      this.requestLookups = requestLookups;
      this.lookupContext = lookupContext;
    }

    public ThreadSettings(
        String setValueUrl,
        Mode mode,
        boolean modifyAllText
    ) {
      this(
          setValueUrl,
          mode,
          modifyAllText,
          new AtomicSequence(),
          new AtomicSequence(),
          new ConcurrentHashMap<>(),
          new BundleLookupThreadContext()
      );
    }

    /**
     * Creates new settings in a disable state.
     */
    public ThreadSettings() {
      this(null, Mode.DISABLED, false);
    }

    /**
     * Creates a new settings instance that shares the underlying sequences, map, and lookup context, but with a
     * different configuration.
     *
     * <p>These new settings are not automatically associated with the current thread.  That must be done via
     * {@link #setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)}.</p>
     *
     * @return  When configuration unchanged, returns {@code this}, otherwise a new instance with shared underlying
     *          sequences, map, and lookup context.
     */
    public ThreadSettings setSettings(Mode mode, boolean modifyAllText) {
      if (
          mode == this.mode
              && modifyAllText == this.modifyAllText
      ) {
        return this;
      } else {
        return new ThreadSettings(
            setValueUrl,
            mode,
            modifyAllText,
            elementIdGenerator,
            lookupIdGenerator,
            requestLookups,
            lookupContext
        );
      }
    }

    public Mode getMode() {
      return mode;
    }

    /**
     * Sets the mode.
     *
     * <p>These new settings are not automatically associated with the current thread.  That must be done via
     * {@link #setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)}.</p>
     *
     * @return  When configuration unchanged, returns {@code this}, otherwise a new instance with shared underlying
     *          sequences, map, and lookup context.
     *
     * @see  #setSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings.Mode, boolean)
     */
    public ThreadSettings setMode(Mode mode) {
      return setSettings(mode, modifyAllText);
    }

    public boolean getModifyAllText() {
      return modifyAllText;
    }

    /**
     * Sets the modify all text flag.
     *
     * <p>These new settings are not automatically associated with the current thread.  That must be done via
     * {@link #setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)}.</p>
     *
     * @return  When configuration unchanged, returns {@code this}, otherwise a new instance with shared underlying
     *          sequences, map, and lookup context.
     *
     * @see  #setSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings.Mode, boolean)
     */
    public ThreadSettings setModifyAllText(boolean modifyAllText) {
      return setSettings(mode, modifyAllText);
    }
  }

  /**
   * The settings for the current thread.
   *
   * @see  I18nThreadLocalRunnable
   */
  static final ThreadLocal<ThreadSettings> currentThreadSettings = ThreadLocal.withInitial(ThreadSettings::new);

  /**
   * Gets the current settings for the current thread, initialized to {@link ThreadSettings#ThreadSettings()}
   * when not yet set.
   */
  public static ThreadSettings getThreadSettings() {
    return currentThreadSettings.get();
  }

  /**
   * Resets the settings for the current thread.
   * Also removes {@link BundleLookupThreadContext} for the current thread.
   */
  @SuppressWarnings("deprecation")
  public static void resetThreadSettings() {
    currentThreadSettings.remove();
    BundleLookupThreadContext.removeThreadContext();
  }

  /**
   * Removes the settings for the current thread.
   * Also removes {@link BundleLookupThreadContext} for the current thread.
   *
   * @deprecated  Please use {@link #resetThreadSettings()} instead.
   */
  @Deprecated
  public static void removeThreadSettings() {
    resetThreadSettings();
  }

  /**
   * Sets the settings for the current thread.
   * Also configures {@link BundleLookupThreadContext} for the current thread.
   *
   * @param  threadSettings  When {@code null}, is equivalent to {@link #resetThreadSettings()}.
   *
   * @see  #printEditableResourceBundleLookups(com.aoapps.lang.io.Encoder, com.aoapps.lang.io.Encoder, java.lang.Appendable, boolean, int, boolean)
   */
  @SuppressWarnings("deprecation")
  public static void setThreadSettings(ThreadSettings threadSettings) {
    if (threadSettings == null) {
      resetThreadSettings();
    } else {
      currentThreadSettings.set(threadSettings);
      if (threadSettings.getMode() == ThreadSettings.Mode.DISABLED) {
        BundleLookupThreadContext.removeThreadContext();
      } else {
        BundleLookupThreadContext.setThreadContext(threadSettings.lookupContext);
      }
    }
  }

  /**
   * Every lookup during a request is logged when editing enabled.
   */
  private static class LookupKey {

    private final EditableResourceBundleSet bundleSet;
    private final String key;

    private LookupKey(
        EditableResourceBundleSet bundleSet,
        String key
    ) {
      this.bundleSet = bundleSet;
      this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof LookupKey)) {
        return false;
      }
      final LookupKey other = (LookupKey) obj;
      return
          bundleSet == other.bundleSet
              && key.equals(other.key);
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 97 * hash + bundleSet.hashCode();
      hash = 97 * hash + key.hashCode();
      return hash;
    }
  }

  private static class LookupLocaleValue {
    private final boolean missing;
    private final boolean invalidated;

    private LookupLocaleValue(boolean missing, boolean invalidated) {
      //assert !(missing && invalidated) : "May not be invalidated when missing";
      this.missing = missing;
      this.invalidated = invalidated;
    }
  }

  private static class LookupValue {

    private final long id;

    /**
     * Access must be synchronized on this LookupValue instance.
     */
    private final List<Long> elementIds = new ArrayList<>();

    /**
     * The set of locales that were queried.
     * Access must be synchronized on this LookupValue instance.
     */
    private final Map<Locale, LookupLocaleValue> locales = new HashMap<>();

    private LookupValue(long id) {
      this.id = id;
    }
  }

  /**
   * Any page that allows the editing of resources must set this at the beginning of the request.
   * If not all users of the site are allowed to edit the content, must also clear this at the end of the request
   * by either calling this method with <code>false</code> or calling <code>printEditableResourceBundleLookups</code>.
   *
   * <p>Also resets the {@linkplain BundleLookupThreadContext thread lookup context}.</p>
   *
   * @param  setValueUrl  Must be non-null when {@code canEditResources} is {@code true}.
   *
   * @see  #printEditableResourceBundleLookups(com.aoapps.lang.io.Encoder, com.aoapps.lang.io.Encoder, java.lang.Appendable, boolean, int, boolean)
   *
   * @deprecated  Please use {@link #setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)}
   *              or {@link #resetThreadSettings()}.
   */
  @Deprecated
  public static void resetRequest(boolean canEditResources, String setValueUrl, boolean modifyAllText) {
    //System.out.println("DEBUG: EditableResourceBundle: resetRequest: thread.id="+Thread.currentThread().getId()+", canEditResources="+canEditResources+", setValueUrl="+setValueUrl);
    if (canEditResources && setValueUrl == null) {
      throw new IllegalArgumentException("setValueUrl is null when canEditResources is true");
    }
    setThreadSettings(
        new ThreadSettings(
            setValueUrl,
            canEditResources ? ThreadSettings.Mode.MARKUP : ThreadSettings.Mode.DISABLED,
            modifyAllText
        )
    );
  }

  private static String convertEmpty(String value) {
    if (value == null) {
      return null;
    }
    if (value.isEmpty()) {
      return EMPTY_DISPLAY;
    }
    return value;
  }

  /**
   * Prints the resource bundle lookup editor.  This should be called at the end of a request,
   * just before the body tag is closed.
   *
   * <p>Also clears the {@linkplain BundleLookupThreadContext thread lookup context}.</p>
   *
   * <p>TODO: Add language resources to properties files (but do not make it an editable properties file to avoid possible infinite recursion?)
   * TODO: Decouple from ao-hodgepodge and use ao-fluent-html</p>
   */
  public static void printEditableResourceBundleLookups(
      Encoder textInJavascriptEncoder,
      Encoder textInXhtmlEncoder,
      Appendable out,
      boolean isXhtml,
      int editorRows,
      boolean verticalButtons
  ) throws IOException {
    final ThreadSettings threadSettings = getThreadSettings();
    // Disable on current thread
    resetThreadSettings();
    // Get a copy, in case is still being altered by other threads
    final Map<LookupKey, LookupValue> lookups = new HashMap<>(threadSettings.requestLookups);
    if (!lookups.isEmpty()) {
      // Sort by lookupValue.id to present the information in the same order as first seen in the request
      List<LookupKey> lookupKeys = new ArrayList<>(lookups.keySet());
      Collections.sort(
          lookupKeys,
          (key1, key2) -> Long.compare(lookups.get(key1).id, lookups.get(key2).id)
      );
      String setValueUrl = threadSettings.setValueUrl;
      if (setValueUrl != null) {
        // Get the set of all locales
        SortedSet<Locale> allLocales = new TreeSet<>(LocaleComparator.getInstance());
        for (LookupKey lookupKey : lookupKeys) {
          allLocales.addAll(lookupKey.bundleSet.getLocales());
        }

        out.append("<div style='position:fixed; bottom:0px; left:50%; width:300px; margin-left:-150px; text-align:center'>\n");
        int invalidatedCount = 0;
        int missingCount = 0;
        for (LookupValue lookupValue : lookups.values()) {
          synchronized (lookupValue) {
            for (LookupLocaleValue localeValue : lookupValue.locales.values()) {
              if (localeValue.missing) {
                missingCount++;
              } else if (localeValue.invalidated) {
                invalidatedCount++;
              }
            }
          }
        }
        out.append("  <a href=\"#\" onclick=\"if (EditableResourceBundleEditorSetVisibility) "
            + "EditableResourceBundleEditorSetVisibility(document.getElementById('EditableResourceBundleEditor').style"
            + ".visibility == 'visible' ? 'hidden' : 'visible'); return false;\" style=\"text-decoration:none; "
            + "color:black\"><span style='border:1px solid black; background-color:white'>")
            .append(Integer.toString(lookups.size())).append(lookups.size() == 1 ? " Resource" : " Resources");
        if (missingCount > 0) {
          out
              .append(" | <span style='color:red'>")
              .append(Integer.toString(missingCount))
              .append(" Missing</span>");
        }
        if (invalidatedCount > 0) {
          out
              .append(" | <span style='color:blue'>")
              .append(Integer.toString(invalidatedCount))
              .append(" Invalidated</span>");
        }
        out.append("</span></a>\n"
            + "</div>\n"
            + "<div id=\"EditableResourceBundleEditor\" style=\"position:fixed; left:50px; width:640px; top:50px;"
            + " height:480px; visibility:hidden; border-left:1px solid black; border-top:1px solid black;"
            + " border-right:2px solid black; border-bottom:2px solid black; background-color:white; overflow:hidden\">\n"
            + "  <div style=\"border-top:1px solid black; background-color:#c0c0c0; position:absolute; left:0px; width:100%; bottom:0px; height:")
            .append(Integer.toString(allLocales.size() * editorRows)).append("em; overflow:hidden\">\n");
        int i = 0;
        for (Locale locale : allLocales) {
          String toString = locale.toString();
          out.append("    <div style=\"position:absolute; left:0px; width:6em; top:")
              .append(Integer.toString(i * editorRows)).append("em; height:")
              .append(Integer.toString(editorRows)).append("em\">\n"
              // Vertical centering uses Method 1 from http://phrogz.net/CSS/vertical-align/index.html
              + "      <div style=\"position:absolute; top:50%; height:1em; margin-top:-.5em; padding-left:4px; padding-right:2px\">\n"
              + "        ").append(toString.length() == 0 ? "Default" : toString).append("\n"
              + "      </div>\n"
              + "    </div>\n"
              + "    <div style=\"position:absolute; left:6em; right:").append(verticalButtons ? "10em" : "14em")
              .append("; top:").append(Integer.toString(i * editorRows)).append("em; height:")
              .append(Integer.toString(editorRows)).append("em\">\n"
              + "      <textarea disabled=\"disabled\" id=\"EditableResourceBundleEditorTextArea")
              .append(Integer.toString(i + 1)).append("\" name=\"EditableResourceBundleEditorTextArea")
              .append(Integer.toString(i + 1)).append("\" cols=\"40\" rows=\"")
              .append(Integer.toString(editorRows)).append("\" style=\"width:100%; height:100%\"></textarea>\n"
              + "    </div>\n"
              + "    <div style=\"position:absolute; width:").append(verticalButtons ? "10em" : "14em")
              .append("; right:0px; top:").append(Integer.toString(i * editorRows)).append("em; height:")
              .append(Integer.toString(editorRows)).append("em\">\n");
          if (verticalButtons) {
            out.append("      <div style=\"position:absolute; left:0px; width:100%; top:30%; height:1.2em; margin-top:-.6em; text-align:center\">\n"
                + "        <input disabled=\"disabled\" id=\"EditableResourceBundleEditorValidateButton")
                .append(Integer.toString(i + 1)).append("\" name=\"EditableResourceBundleEditorValidateButton")
                .append(Integer.toString(i + 1)).append("\" type=\"button\" value=\"Validate\" onclick=\"return EditableResourceBundleEditorModifyOnClick(")
                .append(Integer.toString(i)).append(", false);\" />\n"
                + "      </div>\n"
                + "      <div style=\"position:absolute; left:0px; width:100%; top:70%; height:1.2em; margin-top:-.6em; text-align:center\">\n"
                + "        <input disabled=\"disabled\" id=\"EditableResourceBundleEditorModifyButton")
                .append(Integer.toString(i + 1)).append("\" name=\"EditableResourceBundleEditorModifyButton")
                .append(Integer.toString(i + 1)).append("\" type=\"button\" value=\"Modify\" onclick=\"return EditableResourceBundleEditorModifyOnClick(")
                .append(Integer.toString(i)).append(", true);\" />\n"
                  + "      </div>\n");
          } else {
            out.append("      <div style=\"position:absolute; left:0px; width:100%; top:50%; height:1.2em; margin-top:-.6em; text-align:center\">\n"
                + "        <input disabled=\"disabled\" id=\"EditableResourceBundleEditorValidateButton")
                .append(Integer.toString(i + 1)).append("\" name=\"EditableResourceBundleEditorValidateButton")
                .append(Integer.toString(i + 1)).append("\" type=\"button\" value=\"Validate\" onclick=\"return EditableResourceBundleEditorModifyOnClick(")
                .append(Integer.toString(i)).append(", false);\" />\n"
                + "        <input disabled=\"disabled\" id=\"EditableResourceBundleEditorModifyButton")
                .append(Integer.toString(i + 1)).append("\" name=\"EditableResourceBundleEditorModifyButton")
                .append(Integer.toString(i + 1)).append("\" type=\"button\" value=\"Modify\" onclick=\"return EditableResourceBundleEditorModifyOnClick(")
                .append(Integer.toString(i)).append(", true);\" />\n"
                  + "      </div>\n");
          }
          out.append("    </div>\n");
          i++;
        }
        out.append("  </div>\n"
            + "  <div id=\"EditableResourceBundleEditorHeader\" style=\"border-bottom:1px solid black;"
            + " background-color:#c0c0c0; position:absolute; left:0px; width:100%; top:0px; height:2em; overflow:hidden\">\n"
            + "    <div style=\"float:right; border:2px outset black; margin:.3em\"><a href=\"#\" onclick=\"if"
            + " (EditableResourceBundleEditorSetVisibility) EditableResourceBundleEditorSetVisibility('hidden');"
            + " return false;\" style=\"text-decoration:none; color:black; background-color:white; padding-left:2px;"
            + " padding-right:2px;\">✕</a></div>\n"
            + "    <script type=\"" + ContentType.JAVASCRIPT + "\">");
        if (isXhtml) {
          out.append("//<![CDATA[");
        }
        out.append("\n"
            + "      function EditableResourceBundleEditorSetCookie(c_name,value,expiredays) {\n"
            + "        var exdate=new Date();\n"
            + "        exdate.setDate(exdate.getDate()+expiredays);\n"
            + "        document.cookie=c_name+\"=\"+escape(value)+((expiredays == null)?\"\":\"; expires=\"+exdate.toGMTString())+\"; path=/\";\n"
            + "      }\n"
            + "\n"
            + "      // From http://www.w3schools.com/JS/js_cookies.asp\n"
            + "      function EditableResourceBundleEditorGetCookie(c_name) {\n"
            + "        if (document.cookie.length>0) {\n"
            + "          c_start=document.cookie.indexOf(c_name + \"=\");\n"
            + "          if (c_start != -1) {\n"
            + "            c_start=c_start + c_name.length+1;\n"
            + "            c_end=document.cookie.indexOf(\";\",c_start);\n"
            + "            if (c_end == -1) c_end=document.cookie.length;\n"
            + "            return unescape(document.cookie.substring(c_start,c_end));\n"
            + "          }\n"
            + "        }\n"
            + "        return \"\";\n"
            + "      }\n"
            + "\n"
            + "      function EditableResourceBundleEditorSetVisibility(visibility) {\n"
            + "        document.getElementById('EditableResourceBundleEditor').style.visibility=visibility;\n"
            + "        EditableResourceBundleEditorSetCookie(\"" + VISIBILITY_COOKIE_NAME + "\", visibility, 31);\n"
            + "      }\n"
            + "\n"
            + "      var EditableResourceBundleEditorRowValues=[");
        boolean didOne1 = false;
        for (LookupKey lookupKey : lookupKeys) {
          EditableResourceBundleSet bundleSet = lookupKey.bundleSet;
          if (didOne1) {
            out.append(',');
          } else {
            didOne1 = true;
          }
          out.append("\n        [");
          boolean didOne2 = false;
          for (Locale locale : allLocales) {
            if (didOne2) {
              out.append(',');
            } else {
              didOne2 = true;
            }
            if (bundleSet.getLocales().contains(locale)) {
              // Value allowed
              out.append('"');
              String value = convertEmpty(bundleSet.getResourceBundle(locale).getValue(lookupKey.key));
              textInJavascriptEncoder.append(value, out);
              out.append('"');
            } else {
              // null means not allowed
              out.append("null");
            }
          }
          out.append(']');
        }
        out.append("\n  ];\n"
            + "\n"
            + "      var EditableResourceBundleEditorRowBaseNames=[");
        didOne1 = false;
        for (LookupKey lookupKey : lookupKeys) {
          if (didOne1) {
            out.append(',');
          } else {
            didOne1 = true;
          }
          out.append("\n        \"");
          textInJavascriptEncoder.append(lookupKey.bundleSet.getBaseName(), out);
          out.append('"');
        }
        out.append("\n  ];\n"
            + "\n"
            + "      var EditableResourceBundleEditorLocales=[");
        didOne1 = false;
        for (Locale locale : allLocales) {
          if (didOne1) {
            out.append(',');
          } else {
            didOne1 = true;
          }
          out.append("\n        \"");
          textInJavascriptEncoder.append(locale.toString(), out);
          out.append('"');
        }
        out.append("\n  ];\n"
            + "\n"
            + "      var EditableResourceBundleEditorRowKeys=[");
        didOne1 = false;
        for (LookupKey lookupKey : lookupKeys) {
          if (didOne1) {
            out.append(',');
          } else {
            didOne1 = true;
          }
          out.append("\n        \"");
          textInJavascriptEncoder.append(lookupKey.key, out);
          out.append('"');
        }
        out.append("\n  ];\n"
            + "\n"
            + "      var EditableResourceBundleEditorSelectedRow = null;\n"
            + "      var EditableResourceBundleEditorSelectedIndex = -1;\n"
            + "      function EditableResourceBundleEditorSelectedRowOnClick(index, row, originalBackground) {\n"
            + "        row.EditableResourceBundleEditorSelectedRowOriginalBackground=originalBackground;\n"
            + "        if (EditableResourceBundleEditorSelectedRow != row) {\n"
            + "          if (EditableResourceBundleEditorSelectedRow != null) {\n" // && EditableResourceBundleEditorSelectedRow.style.backgroundColor != 'yellow'
            + "            EditableResourceBundleEditorSelectedRow.style.backgroundColor=EditableResourceBundleEditorSelectedRow.EditableResourceBundleEditorSelectedRowOriginalBackground;\n"
            + "          }\n"
            + "          EditableResourceBundleEditorSelectedRow=row;\n"
            + "          EditableResourceBundleEditorSelectedIndex=index;\n"
            + "          var rowValues=EditableResourceBundleEditorRowValues[index];\n"
            + "          for (var c=0; c<").append(Integer.toString(allLocales.size())).append("; c++) {\n"
            + "            var value=rowValues[c];\n"
            + "            var textArea=document.getElementById(\"EditableResourceBundleEditorTextArea\"+(c+1));\n"
            + "            var validateButton=document.getElementById(\"EditableResourceBundleEditorValidateButton\"+(c+1));\n"
            + "            var modifyButton=document.getElementById(\"EditableResourceBundleEditorModifyButton\"+(c+1));\n"
            + "            if (value == null) {\n"
            + "              textArea.disabled=true;\n"
            + "              validateButton.disabled=true;\n"
            + "              modifyButton.disabled=true;\n"
            + "              textArea.value=\"\";\n"
            + "            } else {\n"
            + "              textArea.value=value;\n"
            + "              textArea.disabled=false;\n"
            + "              validateButton.disabled=false;\n"
            + "              modifyButton.disabled=false;\n"
            + "            }\n"
            + "          }\n"
            + "        }\n"
            + "        row.style.backgroundColor=\"red\";\n"
            + "      }\n"
            + "\n"
            + "      function EditableResourceBundleEditorUpdateElements(rowIndex, value) {\n"
            + "        var elementIds = EditableResourceBundleElementIds[rowIndex];\n"
            + "        for (var e=0; e<elementIds.length; e++) {\n"
            + "          elem=document.getElementById(\"EditableResourceBundleElement\"+elementIds[e]);\n"
            + "          if (elem != null) {\n"
            //+ "            elem.firstChild.nodeValue=value;\n"
            + "            elem.innerHTML=value;\n"
            // From http://www.webdeveloper.com/forum/showthread.php?t=71464
            //+ "            var parser = new DOMParser();\n"
            //+ "            var doc=parser.parseFromString('<div xmlns=\"http://www.w3.org/1999/xhtml\">' + value + '<\\/div>', 'application/xhtml+xml');\n"
            //+ "            var root=doc.documentElement;\n"
            //+ "            for (var i=0; i<root.childNodes.length; ++i) {\n"
            //+ "              elem.appendChild(document.importNode(root.childNodes[i], true));\n"
            //+ "            }\n"
            + "          }\n"
            + "        }\n"
            + "      }\n"
            + "\n"
            + "      function EditableResourceBundleEditorModifyOnClick(localeIndex, modified) {\n"
            + "        if (EditableResourceBundleEditorSelectedIndex != null) {\n"
            + "          var textArea=document.getElementById(\"EditableResourceBundleEditorTextArea\"+(localeIndex+1));\n"
            + "          var value=textArea.value;\n"
            // Update server
            + "          var request=new XMLHttpRequest();\n"
            + "          var url=\"")
            .append(setValueUrl)
            .append(setValueUrl.indexOf('?') == -1 ? '?' : '&')
            .append("baseName=\"+encodeURIComponent(EditableResourceBundleEditorRowBaseNames[EditableResourceBundleEditorSelectedIndex])"
                + "+\"&locale=\"+encodeURIComponent(EditableResourceBundleEditorLocales[localeIndex])+\"&key=\"+encodeURIComponent("
                + "EditableResourceBundleEditorRowKeys[EditableResourceBundleEditorSelectedIndex])+\"&value=\"+encodeURIComponent(value)"
                + "+\"&modified=\"+modified;\n"
                //+ "          window.alert(url);\n"
                + "          request.open('GET', url, false);\n"
                + "          request.send(null);\n"
                + "          if (request.status != 200) {\n"
                + "            window.alert(\"Update failed: \"+request.status+\" from \"+url);\n"
                + "          } else {\n"
                // Updated local data
                + "            EditableResourceBundleEditorRowValues[EditableResourceBundleEditorSelectedIndex][localeIndex]=value;\n"
                // Updated in editor row
                + "            var rowLocaleElem=document.getElementById(\"EditableResourceBundleEditorRow\"+(EditableResourceBundleEditorSelectedIndex+1)+\"Locale\"+(localeIndex+1));\n"
                + "            if (rowLocaleElem == null) window.alert(\"rowLocaleElem is null\");\n"
                + "            else {\n"
                + "              var rowValue=(value.length>30) ? value.substring(0, 30)+\"\\u2026\" : value;\n"
                + "              if (rowLocaleElem.firstChild == null) rowLocaleElem.appendChild(document.createTextNode(rowValue));\n"
                + "              else rowLocaleElem.firstChild.nodeValue=rowValue;\n"
                + "              if (!modified) rowLocaleElem.style.backgroundColor=\"white\";\n"
                + "            }\n"
                // Update background colors in editor row for modify
                + "            if (modified) {\n"
                + "              for (var c=0;c<").append(Integer.toString(allLocales.size())).append(";c++) {\n"
            + "                rowLocaleElem=document.getElementById(\"EditableResourceBundleEditorRow\"+(EditableResourceBundleEditorSelectedIndex+1)+\"Locale\"+(c+1));\n"
            + "                if (rowLocaleElem != null) rowLocaleElem.style.backgroundColor=c == localeIndex ? \"#c0ffc0\" : \"#c0c0ff\";\n"
            + "              }\n"
            + "            }\n"
            + "            EditableResourceBundleEditorUpdateElements(EditableResourceBundleEditorSelectedIndex, value);\n"
            + "          }\n"
            + "        }\n"
            + "        return false;\n"
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
            + "        if (EditableResourceBundleEditorDragElem != null) {\n"
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
            + "        if (EditableResourceBundleEditorDragElem != null) EditableResourceBundleEditorDragElem.style.cursor='auto';\n"
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
            + "        document.getElementById('EditableResourceBundleEditorHeader').style.backgroundColor=\"red\";\n"
            + "        event.preventDefault();\n"
            + "        return false;\n"
            + "      }\n"
            + "\n"
            + "      function EditableResourceBundleEditorResizeMouseMove(event) {\n"
            + "        if (EditableResourceBundleEditorResizeElem != null) {\n"
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
            + "    ");
        if (isXhtml) {
          out.append("//]]>");
        }
        out.append("</script>\n"
            + "    <div"
            + " style=\"text-align:center; font-weight:bold; font-size:larger\""
            + " onmousedown=\"return EditableResourceBundleEditorDragMouseDown(this, event);\""
            + ">Resource Editor</div>\n"
            + "  </div>\n"
            + "  <div id=\"EditableResourceBundleEditorScroller\" style=\"position:absolute; left:0px; width:100%; top:2em; bottom:")
            .append(Integer.toString(allLocales.size() * editorRows)).append("em; overflow:auto\">\n"
              + "    <table style=\"width:100%; border-collapse: collapse; border:1px solid black\">\n" // Not HTML 5 compatible: cellspacing=\"0\" cellpadding=\"2\"
              + "      <tr style=\"background-color:#e0e0e0\">\n"
              + "        <th style=\"border:1px solid black\"></th>\n"
              + "        <th style=\"border:1px solid black\">Key</th>\n");
        for (Locale locale : allLocales) {
          String toString = locale.toString();
          out.append("        <th style=\"border:1px solid black\">").append(toString.length() == 0 ? "Default" : toString).append("</th>\n");
        }
        out.append("        <th style=\"border:1px solid black\">Bundle Set</th>\n"
            + "      </tr>\n");
        i = 0;
        for (LookupKey lookupKey : lookupKeys) {
          EditableResourceBundleSet bundleSet = lookupKey.bundleSet;
          LookupValue lookupValue = lookups.get(lookupKey);
          synchronized (lookupValue) {
            List<Long> elementIds = lookupValue.elementIds;
            final String key = lookupKey.key;
            i++;
            String lookupId = Long.toString(lookupValue.id);
            out.append("      <tr"
                + " id=\"EditableResourceBundleEditorRow").append(lookupId).append("\""
                + " style=\"background-color:").append((i & 1) == 1 ? "white" : "#e0e0e0").append('"');
            if (!elementIds.isEmpty()) {
              out.append(" onmouseover=\"if (typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(")
                  .append(elementIds.get(0).toString()).append(", false);\""
                  + " onmouseout=\"if (typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(")
                  .append(elementIds.get(0).toString()).append(");\"");
            }
            out.append(">\n"
                + "        <td onclick=\"EditableResourceBundleEditorSelectedRowOnClick(")
                .append(Integer.toString(i - 1)).append(", document.getElementById('EditableResourceBundleEditorRow")
                .append(lookupId).append("'), '").append((i & 1) == 1 ? "white" : "#e0e0e0")
                .append("');\" style=\"text-align:right; border:1px solid black\">")
                .append(Long.toString(lookupValue.id))
                .append("</td>\n"
                + "        <td onclick=\"EditableResourceBundleEditorSelectedRowOnClick(")
                .append(Integer.toString(i - 1)).append(", document.getElementById('EditableResourceBundleEditorRow")
                .append(lookupId).append("'), '").append((i & 1) == 1 ? "white" : "#e0e0e0")
                .append("');\" style=\"border:1px solid black\">");
            textInXhtmlEncoder.append(lookupKey.key, out);
            out.append("</td>\n");
            int localeIndex = 0;
            for (Locale locale : allLocales) {
              localeIndex++;
              if (bundleSet.getLocales().contains(locale)) {
                // Supported by this bundleSet
                LookupLocaleValue localeValue = lookupValue.locales.get(locale);

                // The border color represents the specific lookups performed.
                // Red: missing
                // Blue: invalidated
                // Black: validated
                String borderColor = null;
                if (localeValue != null) {
                  if (localeValue.missing) {
                    borderColor = "red";
                  } else if (localeValue.invalidated) {
                    borderColor = "blue";
                  } else {
                    borderColor = "black";
                  }
                }

                // The background color represents the value status.
                // Green: is most recently modified (only one unless somehow multiple modified at same exact moment)
                // Red: is missing
                // Blue: is invalidated
                // White: is validated
                String backgroundColor;
                EditableResourceBundle localeBundle = bundleSet.getResourceBundle(locale);
                String currentValue = convertEmpty(localeBundle.getValue(key));
                if (currentValue == null) {
                  backgroundColor = "#ffc0c0"; // Missing
                } else {
                  // Find the most recently updated item
                  Long newestModifiedTime = null;
                  for (Locale possLocale : bundleSet.getLocales()) {
                    EditableResourceBundle possBundle = bundleSet.getResourceBundle(possLocale);
                    Long possModifiedTime = possBundle.getModifiedTime(key);
                    if (possModifiedTime != null && (newestModifiedTime == null || possModifiedTime > newestModifiedTime)) {
                      newestModifiedTime = possModifiedTime;
                    }
                  }
                  Long modifiedTime = localeBundle.getModifiedTime(key);
                  if (modifiedTime != null && modifiedTime.equals(newestModifiedTime)) {
                    backgroundColor = "#c0ffc0"; // Newest modified time
                  } else {
                    Long validatedTime = localeBundle.getValidatedTime(key);
                    if (validatedTime == null) {
                      backgroundColor = newestModifiedTime == null ? "white" : "#c0c0ff"; // Not yet validated
                    } else if (newestModifiedTime == null) {
                      backgroundColor = "white"; // Nothing modified, assume OK
                    } else if (validatedTime < newestModifiedTime) {
                      backgroundColor = "#c0c0ff"; // Invalidated
                    } else {
                      backgroundColor = "white";
                    }
                  }
                }

                out.append("        <td id=\"EditableResourceBundleEditorRow").append(Integer.toString(i))
                    .append("Locale").append(Integer.toString(localeIndex)).append("\" style=\"white-space:pre; ");
                if (borderColor != null) {
                  out.append("border:2px solid ").append(borderColor).append("; ");
                } else {
                  out.append("border:1px solid black; ");
                }
                out.append("background-color:").append(backgroundColor)
                    .append("\" onclick=\"EditableResourceBundleEditorSelectedRowOnClick(")
                    .append(Integer.toString(i - 1))
                    .append(", document.getElementById('EditableResourceBundleEditorRow").append(lookupId)
                    .append("'), '").append((i & 1) == 1 ? "white" : "#e0e0e0")
                    .append("'); document.getElementById('EditableResourceBundleEditorTextArea")
                    .append(Integer.toString(localeIndex))
                    .append("').select(); document.getElementById('EditableResourceBundleEditorTextArea")
                    .append(Integer.toString(localeIndex)).append("').focus();\">");
                if (currentValue != null) {
                  if (currentValue.length() > 30) {
                    textInXhtmlEncoder.append(currentValue, 0, 30, out);
                    out.append("\u2026"); // Ellipsis
                  } else {
                    textInXhtmlEncoder.append(currentValue, out);
                  }
                }
                out.append("</td>\n");
              } else {
                // Not supported by this bundleSet
                out.append("        <td style=\"opacity:.5; background-color:#404040; border:1px solid black\"></td>\n");
              }
            }
            // Base Name
            out.append("        <td onclick=\"EditableResourceBundleEditorSelectedRowOnClick(")
                .append(Integer.toString(i - 1)).append(", document.getElementById('EditableResourceBundleEditorRow")
                .append(lookupId).append("'), '").append((i & 1) == 1 ? "white" : "#e0e0e0").append("');\">");
            textInXhtmlEncoder.append(bundleSet.getBaseName(), out);
            out.append("</td>\n"
                + "      </tr>\n");
          }
        }
        out.append("    </table>\n"
            + "  </div>\n"
            + "  <div"
            + " style=\"position:absolute; right:0px; width:20px; bottom:0px; height:20px; overflow:hidden; cursor:nw-resize\""
            + " onmousedown=\"return EditableResourceBundleEditorResizeMouseDown(this, event);\""
            + "></div>\n"
            + "</div>\n");
      }
      // Highlight and editor functions
      // TODO: Why are these functions still written when setValueUrl is null?
      //       Would they result in undefined errors?
      //       Either understand intent and test/fix, or don't write setValueUrl is null.
      out.append("<script type=\"" + ContentType.JAVASCRIPT + "\">");
      if (isXhtml) {
        out.append("//<![CDATA[");
      }
      out.append("\n"
          + "  // Restore the editor to its previous position\n"
          + "  var EditableResourceBundleEditorStyle=document.getElementById(\"EditableResourceBundleEditor\").style;\n"
          + "  var EditableResourceBundleEditorWidth = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorWidth\");\n"
          + "  if (EditableResourceBundleEditorWidth != \"\") EditableResourceBundleEditorStyle.width=EditableResourceBundleEditorWidth;\n"
          + "  var EditableResourceBundleEditorHeight = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorHeight\");\n"
          + "  if (EditableResourceBundleEditorHeight != \"\") EditableResourceBundleEditorStyle.height=EditableResourceBundleEditorHeight;\n"
          + "  var EditableResourceBundleEditorTop = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorTop\");\n"
          + "  if (EditableResourceBundleEditorTop != \"\" && parseInt(EditableResourceBundleEditorTop) >= 0 && "
          + "(parseInt(EditableResourceBundleEditorTop)+parseInt(EditableResourceBundleEditorStyle.height)) <= window.innerHeight) "
          + "EditableResourceBundleEditorStyle.top=EditableResourceBundleEditorTop;\n"
          + "  var EditableResourceBundleEditorLeft = EditableResourceBundleEditorGetCookie(\"EditableResourceBundleEditorLeft\");\n"
          + "  if (EditableResourceBundleEditorLeft != \"\" && parseInt(EditableResourceBundleEditorLeft) >= 0 && "
          + "(parseInt(EditableResourceBundleEditorLeft)+parseInt(EditableResourceBundleEditorStyle.width)) <= window.innerWidth) "
          + "EditableResourceBundleEditorStyle.left=EditableResourceBundleEditorLeft;\n"
          + "  var EditableResourceBundleEditorVisibility = EditableResourceBundleEditorGetCookie(\"" + VISIBILITY_COOKIE_NAME + "\");\n"
          + "  if (EditableResourceBundleEditorVisibility != \"\") EditableResourceBundleEditorStyle.visibility=EditableResourceBundleEditorVisibility;\n"
          + "\n"
          + "  var EditableResourceBundleLookupIds=[");
      boolean didOne1 = false;
      for (LookupKey lookupKey : lookupKeys) {
        LookupValue lookupValue = lookups.get(lookupKey);
        if (didOne1) {
          out.append(',');
        } else {
          didOne1 = true;
        }
        out.append("\n    ").append(Long.toString(lookupValue.id));
      }
      out.append("\n  ];\n"
          + "\n"
          + "  var EditableResourceBundleElementIds=[");
      didOne1 = false;
      for (LookupKey lookupKey : lookupKeys) {
        LookupValue lookupValue = lookups.get(lookupKey);
        if (didOne1) {
          out.append(',');
        } else {
          didOne1 = true;
        }
        out.append("\n    [");
        boolean didOne2 = false;
        synchronized (lookupValue) {
          for (Long id : lookupValue.elementIds) {
            if (didOne2) {
              out.append(',');
            } else {
              didOne2 = true;
            }
            out.append(id.toString());
          }
        }
        out.append(']');
      }
      out.append("\n  ];\n"
          + "\n"
          + "  var EditableResourceBundleDelayScrollElement = null;\n"
          + "  var EditableResourceBundleDelayScrollTimerId = null;\n"
          + "\n"
          + "  function EditableResourceBundleDelayDoScroll() {\n"
          + "    var scroller=document.getElementById(\"EditableResourceBundleEditorScroller\");\n"
          + "    scroller.scrollTop=EditableResourceBundleDelayScrollElement.offsetTop-(scroller.clientHeight-EditableResourceBundleDelayScrollElement.offsetHeight)/2;\n" // Centered vertically
          + "    clearTimeout(EditableResourceBundleDelayScrollTimerId);\n"
          + "  }\n"
          + "\n"
          + "  function EditableResourceBundleDelayScroll(elem) {\n"
          + "    EditableResourceBundleDelayScrollElement=elem;\n"
          + "    EditableResourceBundleDelayScrollTimerId=setTimeout(\"EditableResourceBundleDelayDoScroll()\", 250);\n"
          + "  }\n"
          + "\n"
          + "  function EditableResourceBundleCancelDelayScroll() {\n"
          + "    clearTimeout(EditableResourceBundleDelayScrollTimerId);\n"
          + "  }\n"
          + "\n"
          + "  function EditableResourceBundleSetAllBackgrounds(elementId, background, scrollEditor) {\n"
          + "    for (var c=0; c<EditableResourceBundleElementIds.length; c++) {\n"
          + "      var elementIds = EditableResourceBundleElementIds[c];\n"
          + "      for (var d=0; d<elementIds.length; d++) {\n"
          + "        if (elementId == elementIds[d]) {\n"
          + "          var elem=document.getElementById(\"EditableResourceBundleEditorRow\"+EditableResourceBundleLookupIds[c]);\n"
          + "          if (elem != null) {\n"
          + "            elem.style.backgroundColor=elem == (!scrollEditor && EditableResourceBundleEditorSelectedRow) "
          + "? \"red\" : background != \"transparent\" ? background : elem == EditableResourceBundleEditorSelectedRow "
          + "? \"red\" : (c&1) == 0 ? \"white\" : \"#e0e0e0\";\n"
          + "            if (scrollEditor) {\n"
          + "              EditableResourceBundleDelayScroll(elem);\n"
          + "            }\n"
          + "          }\n"
          + "          for (var e=0; e<elementIds.length; e++) {\n"
          + "            elem=document.getElementById(\"EditableResourceBundleElement\"+elementIds[e]);\n"
          + "            if (elem != null) {\n"
          + "              elem.style.backgroundColor=background;\n"
          + "            }\n"
          + "          }\n"
          + "          return;\n"
          + "        }\n"
          + "      }\n"
          + "    }\n"
          + "  }\n"
          + "\n"
          + "  function EditableResourceBundleHighlightAll(elementId, scrollEditor) {\n"
          + "    EditableResourceBundleSetAllBackgrounds(elementId, \"yellow\", scrollEditor);\n"
          + "  }\n"
          + "\n"
          + "  function EditableResourceBundleUnhighlightAll(elementId) {\n"
          + "    EditableResourceBundleCancelDelayScroll();\n"
          + "    EditableResourceBundleSetAllBackgrounds(elementId, \"transparent\", false);\n"
          + "  }\n");
      if (isXhtml) {
        out.append("//]]>");
      }
      out.append("</script>\n");
    }
  }

  /**
   * @deprecated  Please use {@link #printEditableResourceBundleLookups(com.aoapps.lang.io.Encoder, com.aoapps.lang.io.Encoder, java.lang.Appendable, boolean, int, boolean)}
   *              directly, providing whether is SGML or XHTML serialization with the {@code isXhtml} flag.
   *              This assumes XHTML serialization for backward-compatibility.
   */
  @Deprecated
  public static void printEditableResourceBundleLookups(
      Encoder textInJavascriptEncoder,
      Encoder textInXhtmlEncoder,
      Appendable out,
      int editorRows,
      boolean verticalButtons
  ) throws IOException {
    printEditableResourceBundleLookups(
        textInJavascriptEncoder,
        textInXhtmlEncoder,
        out,
        true,
        editorRows,
        verticalButtons
    );
  }

  private final Locale locale;
  private final EditableResourceBundleSet bundleSet;

  @SuppressWarnings("LeakingThisInConstructor")
  protected EditableResourceBundle(Locale locale, EditableResourceBundleSet bundleSet, File... sourceFiles) {
    super(sourceFiles);
    this.locale = locale;
    this.bundleSet = bundleSet;
    bundleSet.addBundle(this);
  }

  Locale getBundleLocale() {
    return locale;
  }

  EditableResourceBundleSet getBundleSet() {
    return bundleSet;
  }

  /**
   * The natural sorting is based on classname.
   */
  @Override
  public int compareTo(EditableResourceBundle o) {
    return getClass().getName().compareTo(o.getClass().getName());
  }

  /**
   * Gets an object.
   * If editing is enabled and the bundle is modifiable, adds to the thread
   * lookup context if present.
   *
   * @see  BundleLookupThreadContext
   */
  @Override
  @SuppressWarnings("RedundantStringConstructorCall")
  public Object handleGetObject(String key) {
    final ThreadSettings threadSettings;
    final ThreadSettings.Mode mode;
    Object object = super.handleGetObject(key);
    if (
        !isModifiable() // unmodifiable
            // markup disabled
            || (mode = (threadSettings = getThreadSettings()).getMode()) == ThreadSettings.Mode.DISABLED
            || mode == ThreadSettings.Mode.LOOKUP
    ) {
      return object;
    }

    // Must be a string (or null)
    String value = (String) object;

    // Determine if the value is validated.  The value is validated
    // when its validated time is greater than the modified time of
    // all translations

    // Find the most recently updated item
    Long newestModifiedTime = null;
    for (Locale possLocale : bundleSet.getLocales()) {
      EditableResourceBundle possBundle = bundleSet.getResourceBundle(possLocale);
      Long possModifiedTime = possBundle.getModifiedTime(key);
      if (possModifiedTime != null && (newestModifiedTime == null || possModifiedTime > newestModifiedTime)) {
        newestModifiedTime = possModifiedTime;
      }
    }
    boolean invalidated;
    Long validatedTime = bundleSet.getResourceBundle(locale).getValidatedTime(key);
    if (newestModifiedTime == null) {
      invalidated = false; // Nothing modified, assume OK
    } else {
      if (validatedTime == null) {
        invalidated = true; // Only invalidated when at least one other item
      } else {
        invalidated = validatedTime < newestModifiedTime;
      }
    }

    // Add to the log
    LookupKey lookupKey = new LookupKey(bundleSet, key);
    LookupValue lookupValue = threadSettings.requestLookups.get(lookupKey);
    if (lookupValue == null) {
      lookupValue = new LookupValue(threadSettings.lookupIdGenerator.getNextSequenceValue());
      LookupValue existing = threadSettings.requestLookups.putIfAbsent(lookupKey, lookupValue);
      if (existing != null) {
        lookupValue = existing;
      }
    }
    synchronized (lookupValue) {
      // Add this locale if not already set
      boolean missing = value == null;
      lookupValue.locales.computeIfAbsent(locale, l -> new LookupLocaleValue(missing, invalidated));
      //if (!lookupValue.locales.containsKey(locale)) {
      //  lookupValue.locales.put(
      //    locale,
      //    new LookupLocaleValue(value == null, invalidated)
      //  );
      //}
      if (!missing) {
        // Record the lookup in any thread context
        BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext();
        if (threadContext != null) {
          // Reserve an element ID, even though it may not be used depending on final context
          final long elementId = threadSettings.elementIdGenerator.getNextSequenceValue();
          lookupValue.elementIds.add(elementId);
          // If this string is already in the thread context, make a new instance to be unique by identity
          if (threadContext.getLookupMarkup(value) != null) {
            value = new String(value); // Need new string with different identity for unique threadContext lookups
          }
          // Find invalidated flag
          threadContext.addLookupMarkup(
              value,
              new EditableResourceBundleLookupMarkup(
                  lookupValue.id,
                  invalidated,
                  elementId,
                  mode == ThreadSettings.Mode.MARKUP,
                  threadSettings.modifyAllText
              )
          );
        }
      }
    }
    return value;
  }

  /**
   * XHTML: Text surrounded by &lt;span&gt;.
   *
   * <p>TEXT: &lt;#&lt; and &gt;#&gt; used to cause XHTML parse errors if text value not properly encoded</p>
   *
   * <p>JAVASCRIPT: Adds a comment before the string with the lookup id</p>
   */
  private static final class EditableResourceBundleLookupMarkup implements BundleLookupMarkup {

    private final long lookupId;
    private final boolean invalidated;
    private final long elementId;
    private final boolean allowScripts;
    private final boolean modifyAllText;

    private EditableResourceBundleLookupMarkup(
        long lookupId,
        boolean invalidated,
        long elementId,
        boolean allowScripts,
        boolean modifyAllText
    ) {
      this.lookupId = lookupId;
      this.invalidated = invalidated;
      this.elementId = elementId;
      this.allowScripts = allowScripts;
      this.modifyAllText = modifyAllText;
    }

    @Override
    public void appendPrefixTo(MarkupType markupType, Appendable out) throws IOException {
      switch (markupType) {
        case NONE:
          {
            // No markup
            break;
          }
        case XHTML:
          {
            //if (invalidated) {
            //  SB.append(" style=\"color:red\"");
            //}
            if (allowScripts) {
              String elementIdString = Long.toString(elementId);
              out
                  .append("<!--")
                  .append(Long.toString(lookupId))
                  .append("--><span id=\"EditableResourceBundleElement")
                  .append(elementIdString)
                  .append("\" onmouseover=\"if (typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(")
                  .append(elementIdString)
                  .append(", true);\"")
                  .append(" onmouseout=\"if (typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(")
                  .append(elementIdString)
                  .append(");\">");
              // TODO: invalidated/modifyAllText marker here, too?;
            } else {
              // No scripting version, similar to TEXT
              if (invalidated) {
                out
                    .append("&lt;&lt;&lt;")
                    .append(Long.toString(lookupId))
                    .append("&lt;");
              } else if (modifyAllText) {
                out
                    .append("&lt;")
                    .append(Long.toString(lookupId))
                    .append("&lt;");
              } else {
                // Comments only
                out
                    .append("<!--")
                    .append(Long.toString(lookupId))
                    .append("-->");
              }
            }
            break;
          }
        case TEXT:
          {
            if (invalidated) {
              out
                  .append("<<<")
                  .append(Long.toString(lookupId))
                  .append('<');
            } else if (modifyAllText) {
              out
                  .append('<')
                  .append(Long.toString(lookupId))
                  .append('<');
            } else {
              // No prefix
            }
            break;
          }
        case JAVASCRIPT:
        case MYSQL:
        case PSQL:
        case CSS:
          {
            out
                .append("/*")
                .append(Long.toString(lookupId))
                .append("*/");
            break;
          }
        case SH:
          {
            out
                .append("`#")
                .append(Long.toString(lookupId))
                .append('`');
            break;
          }
        default:
          throw new AssertionError();
      }
    }

    @Override
    public void appendPrefixTo(MarkupType markupType, Encoder encoder, Appendable out) throws IOException {
      if (encoder == null) {
        appendPrefixTo(markupType, out);
      } else {
        switch (markupType) {
          case NONE:
            {
              // No markup
              break;
            }
          case XHTML:
            {
              //if (invalidated) {
              //  SB.append(" style=\"color:red\"");
              //}
              if (allowScripts) {
                String elementIdString = Long.toString(elementId);
                encoder
                    .append("<!--", out)
                    .append(Long.toString(lookupId), out)
                    .append("--><span id=\"EditableResourceBundleElement", out)
                    .append(elementIdString, out)
                    .append("\" onmouseover=\"if (typeof EditableResourceBundleHighlightAll == &#39;function&#39;) EditableResourceBundleHighlightAll(", out)
                    .append(elementIdString, out)
                    .append(", true);\"", out)
                    .append(" onmouseout=\"if (typeof EditableResourceBundleUnhighlightAll == &#39;function&#39;) EditableResourceBundleUnhighlightAll(", out)
                    .append(elementIdString, out)
                    .append(");\">", out);
                // TODO: invalidated/modifyAllText marker here, too?
              } else {
                // No scripting version, similar to TEXT
                if (invalidated) {
                  encoder
                      .append("&lt;&lt;&lt;", out)
                      .append(Long.toString(lookupId), out)
                      .append("&lt;", out);
                } else if (modifyAllText) {
                  encoder
                      .append("&lt;", out)
                      .append(Long.toString(lookupId), out)
                      .append("&lt;", out);
                } else {
                  // Comments only
                  encoder
                      .append("<!--", out)
                      .append(Long.toString(lookupId), out)
                      .append("-->", out);
                }
              }
              break;
            }
          case TEXT:
            {
              if (invalidated) {
                encoder
                    .append("<<<", out)
                    .append(Long.toString(lookupId), out)
                    .append('<', out);
              } else if (modifyAllText) {
                encoder
                    .append('<', out)
                    .append(Long.toString(lookupId), out)
                    .append('<', out);
              } else {
                // No prefix
              }
              break;
            }
          case JAVASCRIPT:
          case MYSQL:
          case PSQL:
          case CSS:
            {
              encoder
                  .append("/*", out)
                  .append(Long.toString(lookupId), out)
                  .append("*/", out);
              break;
            }
          case SH:
            {
              encoder
                  .append("`#", out)
                  .append(Long.toString(lookupId), out)
                  .append('`', out);
              break;
            }
          default:
            throw new AssertionError();
        }
      }
    }

    @Override
    public void appendSuffixTo(MarkupType markupType, Appendable out) throws IOException {
      switch (markupType) {
        case NONE:
          {
            // No markup
            break;
          }
        case XHTML:
          {
            if (allowScripts) {
              // TODO: invalidated/modifyAllText marker here, too?
              out.append("</span>");
            } else {
              // No scripting version, similar to TEXT
              if (invalidated) {
                out
                    .append("&gt;")
                    .append(Long.toString(lookupId))
                    .append("&gt;&gt;&gt;");
              } else if (modifyAllText) {
                out
                    .append("&gt;")
                    .append(Long.toString(lookupId))
                    .append("&gt;");
              } else {
                // No suffix
              }
            }
            break;
          }
        case TEXT:
          {
            if (invalidated) {
              out
                  .append('>')
                  .append(Long.toString(lookupId))
                  .append(">>>");
            } else if (modifyAllText) {
              out
                  .append('>')
                  .append(Long.toString(lookupId))
                  .append('>');
            } else {
              // No suffix
            }
            break;
          }
        case JAVASCRIPT:
        case MYSQL:
        case PSQL:
        case CSS:
        case SH:
          {
            // No suffix
            break;
          }
        default:
          throw new AssertionError();
      }
    }

    @Override
    public void appendSuffixTo(MarkupType markupType, Encoder encoder, Appendable out) throws IOException {
      if (encoder == null) {
        appendSuffixTo(markupType, out);
      } else {
        switch (markupType) {
          case NONE:
            {
              // No markup
              break;
            }
          case XHTML:
            {
              if (allowScripts) {
                // TODO: invalidated/modifyAllText marker here, too?
                encoder.append("</span>", out);
              } else {
                // No scripting version, similar to TEXT
                if (invalidated) {
                  encoder
                      .append("&gt;", out)
                      .append(Long.toString(lookupId), out)
                      .append("&gt;&gt;&gt;", out);
                } else if (modifyAllText) {
                  encoder
                      .append("&gt;", out)
                      .append(Long.toString(lookupId), out)
                      .append("&gt;", out);
                } else {
                  // No suffix
                }
              }
              break;
            }
          case TEXT:
            {
              if (invalidated) {
                encoder
                    .append('>', out)
                    .append(Long.toString(lookupId), out)
                    .append(">>>", out);
              } else if (modifyAllText) {
                encoder
                    .append('>', out)
                    .append(Long.toString(lookupId), out)
                    .append('>', out);
              } else {
                // No suffix
              }
              break;
            }
          case JAVASCRIPT:
          case MYSQL:
          case PSQL:
          case CSS:
          case SH:
            {
              // No suffix
              break;
            }
          default:
            throw new AssertionError();
        }
      }
    }
  }
}
