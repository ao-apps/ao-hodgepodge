/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2021, 2022  AO Industries, Inc.
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

import java.util.ResourceBundle;

/**
 * A modifiable resource bundle allows the resources to be changed during
 * the execution of the application.  It also keeps track of the modification
 * and verification times for the keys to help keep multiple translations
 * in sync throughout the lifetime of an application.
 *
 * @author  AO Industries, Inc.
 */
public abstract class ModifiableResourceBundle extends ResourceBundle {

  protected ModifiableResourceBundle() {
    // Do nothing
  }

  /**
   * Removes a string.
   */
  public final void removeKey(String key) {
    if (!isModifiable()) {
      throw new AssertionError("ResourceBundle is not modifiable: " + this);
    }
    handleRemoveKey(key);
  }

  /**
   * This will only be called on modifiable bundles.
   *
   * @see #isModifiable()
   */
  protected abstract void handleRemoveKey(String key);

  /**
   * @see #setObject(java.lang.String, java.lang.Object, boolean)
   */
  public final void setString(String key, String value, boolean modified) {
    setObject(key, value, modified);
  }

  /**
   * @see #setObject(java.lang.String, java.lang.Object, boolean)
   */
  public final void setStringArray(String key, String[] value, boolean modified) {
    setObject(key, value, modified);
  }

  /**
   * Adds or updates the value associated with the provided key and sets
   * the verified time to the current time.  If <code>modified</code>
   * is <code>true</code>, the modified time will also be updated, which will
   * cause other locales to require verification.
   */
  public final void setObject(String key, Object value, boolean modified) {
    if (!isModifiable()) {
      throw new AssertionError("ResourceBundle is not modifiable: " + this);
    }
    handleSetObject(key, value, modified);
  }

  /**
   * Checks if this bundle is currently modifiable.
   */
  public abstract boolean isModifiable();

  /**
   * This will only be called on modifiable bundles.
   *
   * @see #isModifiable()
   * @see #setObject(java.lang.String, java.lang.Object, boolean)
   */
  protected abstract void handleSetObject(String key, Object value, boolean modified);
}
