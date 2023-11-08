/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2021, 2022, 2023  AO Industries, Inc.
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

package com.aoapps.hodgepodge.schedule;

import java.util.Calendar;
import java.util.Optional;

public enum Month {
  JANUARY(
      "Januaries",
      "Januarys",
      "January",
      "Jan",
      Calendar.JANUARY),
  FEBRUARY(
      "Februaries",
      "Februarys",
      "February",
      "Feb",
      Calendar.FEBRUARY),
  MARCH(
      "Marches",
      null,
      "March",
      "Mar",
      Calendar.MARCH),
  APRIL(
      "Aprils",
      null,
      "April",
      "Apr",
      Calendar.APRIL),
  MAY(
      "Mays",
      null,
      "May",
      "May",
      Calendar.MAY),
  JUNE(
      "Junes",
      null,
      "June",
      "Jun",
      Calendar.JUNE),
  JULY(
      "Julys",
      "Julies",
      "July",
      "Jul",
      Calendar.JULY),
  AUGUST(
      "Augusts",
      null,
      "August",
      "Aug",
      Calendar.AUGUST),
  SEPTEMBER(
      "Septembers",
      null,
      "September",
      "Sep",
      Calendar.SEPTEMBER),
  OCTOBER(
      "Octobers",
      null,
      "October",
      "Oct",
      Calendar.OCTOBER),
  NOVEMBER(
      "Novembers",
      null,
      "November",
      "Nov",
      Calendar.NOVEMBER),
  DECEMBER(
      "Decembers",
      null,
      "December",
      "Dec",
      Calendar.DECEMBER);

  /**
   * Copy of values for internal use without temporary array copy.
   */
  @SuppressWarnings("PackageVisibleField")
  static Month[] values = values();

  /**
   * Gets the month from the Calendar value.
   */
  public static Month getByCalendarMonth(int calendarMonth) {
    switch (calendarMonth) {
      case Calendar.JANUARY:
        return JANUARY;
      case Calendar.FEBRUARY:
        return FEBRUARY;
      case Calendar.MARCH:
        return MARCH;
      case Calendar.APRIL:
        return APRIL;
      case Calendar.MAY:
        return MAY;
      case Calendar.JUNE:
        return JUNE;
      case Calendar.JULY:
        return JULY;
      case Calendar.AUGUST:
        return AUGUST;
      case Calendar.SEPTEMBER:
        return SEPTEMBER;
      case Calendar.OCTOBER:
        return OCTOBER;
      case Calendar.NOVEMBER:
        return NOVEMBER;
      case Calendar.DECEMBER:
        return DECEMBER;
      default:
        throw new AssertionError("Calendar and Month mismatch");
    }
  }

  private final String pluralName;
  private final String altPluralName;
  private final String longName;
  private final String shortName;
  private final int calendarMonth;

  private Month(String pluralName, String altPluralName, String longName, String shortName, int calendarMonth) {
    this.pluralName = pluralName;
    this.altPluralName = altPluralName;
    this.longName = longName;
    this.shortName = shortName;
    this.calendarMonth = calendarMonth;
  }

  @Override
  public String toString() {
    return longName;
  }

  public String getPluralName() {
    return pluralName;
  }

  public Optional<String> getAltPluralName() {
    return Optional.ofNullable(altPluralName);
  }

  public String getLongName() {
    return longName;
  }

  public String getShortName() {
    return shortName;
  }

  /**
   * Gets the month value used by the Calendar class.
   *
   * @see  Calendar
   */
  public int getCalendarMonth() {
    return calendarMonth;
  }
}
