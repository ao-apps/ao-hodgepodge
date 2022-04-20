/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.LocalizedIllegalStateException;
import com.aoapps.lang.i18n.Resources;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>
 * Each thread has a markup context associated with it.  When set, bundle lookups
 * will be recorded with any markup-context appropriate prefixes and suffixes added.
 * This allows the use of a normal API while providing a mechanism for in-context
 * translation interfaces to better integrate with the underlying resource bundles.
 * </p>
 * <p>
 * Under concurrent programming, one context can
 * {@linkplain I18nThreadLocalRunnable end up being accessed concurrently by multiple threads},
 * thus BundleLookupThreadContext is a thread-safe implementation.
 * </p>
 * <p>
 * Bundle lookups are not guaranteed to be recorded, such as when in-context translation
 * is disabled (production mode).
 * </p>
 */
public final class BundleLookupThreadContext {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, BundleLookupThreadContext.class);

  /**
   * @see  I18nThreadLocalRunnable
   */
  static final ThreadLocal<BundleLookupThreadContext> threadContext = new ThreadLocal<>();

  /**
   * Gets the current context for the current thread or {@code null} if none set and none created.
   *
   * @deprecated  Please use {@link #getThreadContext()} directly, since this content is now added and removed based on the
   *              {@linkplain EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings) current thread settings}.
   *
   * @see  #getThreadContext()
   * @see  EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)
   */
  @Deprecated
  public static BundleLookupThreadContext getThreadContext(boolean createIfMissing) {
    BundleLookupThreadContext context = threadContext.get();
    if (createIfMissing && context == null) {
      context = new BundleLookupThreadContext();
      threadContext.set(context);
    }
    return context;
  }

  /**
   * Gets the current context for the current thread or {@code null} if none set.
   * This content is added and removed based on the
   * {@linkplain EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings) current thread settings}.
   *
   * @see  EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)
   */
  public static BundleLookupThreadContext getThreadContext() {
    return threadContext.get();
  }

  /**
   * Removes any current context.
   *
   * @deprecated  This should not be used directly and will become inaccessible in a future major version release,
   *              since this content is now added and removed based on the
   *              {@linkplain EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings) current thread settings}.
   *
   * @see  EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)
   */
  @Deprecated
  public static void removeThreadContext() {
    threadContext.remove();
  }

  /**
   * Sets the current context to the given value.
   * This may be used to restore a previous context when in-context translation is temporarily disabled.
   * This content is added and removed based on the
   * {@linkplain EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings) current thread settings}.
   *
   * @param  context  When {@code null}, is equivalent to {@link #removeThreadContext()}.
   *
   * @see  EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)
   */
  static void setThreadContext(BundleLookupThreadContext context) {
    if (context == null) {
      threadContext.remove();
    } else {
      threadContext.set(context);
    }
  }

  /**
   * Register a listener on {@link Resources}
   */
  static {
    Resources.addListener(
      (Resources _resources, Locale locale, String key, Object[] args, String resource, String result) -> {
        // Copy any lookup markup to the newly generated string
        BundleLookupThreadContext _threadContext = BundleLookupThreadContext.getThreadContext();
        if (_threadContext != null) {
          BundleLookupMarkup lookupMarkup = _threadContext.getLookupMarkup(resource);
          _threadContext.addLookupMarkup(
            result, // This string is already a new instance and therefore is already unique by identity
            lookupMarkup
          );
        }
      }
    );
  }

  private final IdentityHashMap<String, BundleLookupMarkup> lookupResults = new IdentityHashMap<>();

  BundleLookupThreadContext() {
    // Do nothing
  }

  /**
   * @throws IllegalStateException   if the string has already been added to this context (as matched by identity)
   */
  void addLookupMarkup(String lookupResult, BundleLookupMarkup lookupMarkup) throws IllegalStateException {
    assert lookupResult != null;
    synchronized (lookupResults) {
      if (lookupResults.put(lookupResult, lookupMarkup) != null) {
        throw new LocalizedIllegalStateException(RESOURCES, "addLookupMarkup.stringAlreadyAdded");
      }
    }
  }

  /**
   * Removes all lookups stored in this context.
   *
   * @deprecated  Lookups are no longer reset.  Instead, they are now added and removed based on the
   *              {@linkplain EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings) current thread settings}.
   *
   * @see  EditableResourceBundle#setThreadSettings(com.aoapps.util.i18n.EditableResourceBundle.ThreadSettings)
   */
  @Deprecated
  public void reset() {
    synchronized (lookupResults) {
      lookupResults.clear();
    }
  }

  /**
   * Gets the lookup markup for the given String or {@code null} if not found.
   * <p>
   * The string is looked-up by identity only: {@link String#equals(java.lang.Object)} is not called.
   * This is to give a more precise match to lookups.  Much care is taken care to support this string
   * identity lookup, including things like resource bundles, I/O buffers, and tag attribute manipulation.
   * </p>
   */
  public BundleLookupMarkup getLookupMarkup(String result) {
    if (result == null) {
      return null;
    }
    synchronized (lookupResults) {
      return lookupResults.get(result);
    }
  }
}
