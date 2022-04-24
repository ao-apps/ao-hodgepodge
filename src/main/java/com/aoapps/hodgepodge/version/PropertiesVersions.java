/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.version;

import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.PropertiesUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Loads version numbers from a properties file.
 * The properties file should have a three-component value for each product,
 * along with a single build.number property for the last component of the
 * version number.
 *
 * @author  AO Industries, Inc.
 */
public class PropertiesVersions {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, PropertiesVersions.class);

  private static Properties readProperties(InputStream in) throws IOException {
    Properties props = new Properties();
    props.load(in);
    return props;
  }

  private final Properties properties;

  /**
   * Loads properties from a module or classpath resource.
   *
   * @see  PropertiesUtils#loadFromResource(java.lang.Class, java.lang.String)
   */
  public PropertiesVersions(Class<?> clazz, String resource) throws IOException {
    this(PropertiesUtils.loadFromResource(clazz, resource));
  }

  /**
   * Loads properties from the provided inputstream.
   */
  public PropertiesVersions(InputStream in) throws IOException {
    this(readProperties(in));
  }

  /**
   * Uses the provided properties directly, no defensive copy is made.
   */
  public PropertiesVersions(Properties properties) {
    this.properties = properties;
  }

  /**
   * Gets the version number for the provided product.
   */
  public Version getVersion(String product) throws IllegalArgumentException {
    String three = properties.getProperty(product);
    if (three == null) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "getVersion.productNotFound", product);
    }
    return Version.valueOf(three + "." + getBuild());
  }

  /**
   * Gets the build number that is applied to all products.
   */
  public int getBuild() throws IllegalArgumentException {
    String build = properties.getProperty("build.number");
    if (build == null) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "getVersion.buildNotFound");
    }
    return Integer.parseInt(build);
  }
}
