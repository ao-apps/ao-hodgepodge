/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2021, 2022  AO Industries, Inc.
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

public enum Month {
  JANUARY  ("January",   "Jan", Calendar.JANUARY),
  FEBRUARY ("February",  "Feb", Calendar.FEBRUARY),
  MARCH    ("March",     "Mar", Calendar.MARCH),
  APRIL    ("April",     "Apr", Calendar.APRIL),
  MAY      ("May",       "May", Calendar.MAY),
  JUNE     ("June",      "Jun", Calendar.JUNE),
  JULY     ("July",      "Jul", Calendar.JULY),
  AUGUST   ("August",    "Aug", Calendar.AUGUST),
  SEPTEMBER("September", "Sep", Calendar.SEPTEMBER),
  OCTOBER  ("October",   "Oct", Calendar.OCTOBER),
  NOVEMBER ("November",  "Nov", Calendar.NOVEMBER),
  DECEMBER ("December",  "Dec", Calendar.DECEMBER);

  /**
   * Copy of values for internal use without temporary array copy.
   */
  static Month[] values = values();

  /**
   * Gets the month from the Calendar value.
   */
  public static Month getByCalendarMonth(int calendarMonth) {
    switch (calendarMonth) {
      case Calendar.JANUARY   : return JANUARY;
      case Calendar.FEBRUARY  : return FEBRUARY;
      case Calendar.MARCH     : return MARCH;
      case Calendar.APRIL     : return APRIL;
      case Calendar.MAY       : return MAY;
      case Calendar.JUNE      : return JUNE;
      case Calendar.JULY      : return JULY;
      case Calendar.AUGUST    : return AUGUST;
      case Calendar.SEPTEMBER : return SEPTEMBER;
      case Calendar.OCTOBER   : return OCTOBER;
      case Calendar.NOVEMBER  : return NOVEMBER;
      case Calendar.DECEMBER  : return DECEMBER;
      default : throw new AssertionError("Calendar and Month mismatch");
    }
  }

  private final String longName;
  private final String shortName;
  private final int calendarMonth;

  private Month(String longName, String shortName, int calendarMonth) {
    this.longName = longName;
    this.shortName = shortName;
    this.calendarMonth = calendarMonth;
  }

  @Override
  public String toString() {
    return longName;
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
