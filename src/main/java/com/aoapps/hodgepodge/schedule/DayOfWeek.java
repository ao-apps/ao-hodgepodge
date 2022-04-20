/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2018, 2021, 2022  AO Industries, Inc.
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

public enum DayOfWeek {
  SUNDAY   ("Sunday",    "Sun", Calendar.SUNDAY),
  MONDAY   ("Monday",    "Mon", Calendar.MONDAY),
  TUESDAY  ("Tuesday",   "Tue", Calendar.TUESDAY),
  WEDNESDAY("Wednesday", "Wed", Calendar.WEDNESDAY),
  THURSDAY ("Thursday",  "Thu", Calendar.THURSDAY),
  FRIDAY   ("Friday",    "Fri", Calendar.FRIDAY),
  SATURDAY ("Saturday",  "Sat", Calendar.SATURDAY);

  /**
   * Copy of values for internal use without temporary array copy.
   */
  static DayOfWeek[] values = values();

  /**
   * Gets the day of the week from the Calendar value.
   */
  public static DayOfWeek getByCalendarDayOfWeek(int calendarDayOfWeek) {
    switch (calendarDayOfWeek) {
      case Calendar.SUNDAY    : return SUNDAY;
      case Calendar.MONDAY    : return MONDAY;
      case Calendar.TUESDAY   : return TUESDAY;
      case Calendar.WEDNESDAY : return WEDNESDAY;
      case Calendar.THURSDAY  : return THURSDAY;
      case Calendar.FRIDAY    : return FRIDAY;
      case Calendar.SATURDAY  : return SATURDAY;
      default : throw new AssertionError("Calendar and DayOfWeek mismatch, all fields set?: calendarDayOfWeek = " + calendarDayOfWeek);
    }
  }

  private final String longName;
  private final String shortName;
  private final int calendarDayOfWeek;

  private DayOfWeek(String longName, String shortName, int calendarDayOfWeek) {
    this.longName = longName;
    this.shortName = shortName;
    this.calendarDayOfWeek = calendarDayOfWeek;
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
   * Gets the day of week value used by the Calendar class.
   * 
   * @see  Calendar
   */
  public int getCalendarDayOfWeek() {
    return calendarDayOfWeek;
  }
}
