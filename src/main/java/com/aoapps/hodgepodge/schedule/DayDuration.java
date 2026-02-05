/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015, 2021, 2022, 2026  AO Industries, Inc.
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

import java.time.LocalDate;
import java.util.Calendar;

/**
 * Represents a number of days.
 */
public class DayDuration {

  public enum Unit {
    DAYS {
      @Override
      void toString(int count, StringBuilder sb) {
        sb.append(count).append(count == 1 ? " day" : " days");
      }

      @Override
      void offset(int count, Calendar cal) {
        cal.add(Calendar.DATE, count);
      }

      @Override
      LocalDate plus(int count, LocalDate date) {
        return date.plusDays(count);
      }
    },
    WEEKS {
      @Override
      void toString(int count, StringBuilder sb) {
        sb.append(count).append(count == 1 ? " week" : " weeks");
      }

      @Override
      void offset(int count, Calendar cal) {
        cal.add(Calendar.WEEK_OF_YEAR, count);
      }

      @Override
      LocalDate plus(int count, LocalDate date) {
        return date.plusWeeks(count);
      }
    },
    MONTHS {
      @Override
      void toString(int count, StringBuilder sb) {
        sb.append(count).append(count == 1 ? " month" : " months");
      }

      @Override
      void offset(int count, Calendar cal) {
        cal.add(Calendar.MONTH, count);
      }

      @Override
      LocalDate plus(int count, LocalDate date) {
        return date.plusMonths(count);
      }
    },
    YEARS {
      @Override
      void toString(int count, StringBuilder sb) {
        sb.append(count).append(count == 1 ? " year" : " years");
      }

      @Override
      void offset(int count, Calendar cal) {
        cal.add(Calendar.YEAR, count);
      }

      @Override
      LocalDate plus(int count, LocalDate date) {
        return date.plusYears(count);
      }
    };

    /**
     * Gets the unit for the given textual representation.
     *
     * @see  DayDuration#valueOf(java.lang.String)
     */
    static Unit valueOfUnit(String unit) throws IllegalArgumentException {
      if (
          "day".equalsIgnoreCase(unit)
              || "days".equalsIgnoreCase(unit)
      ) {
        return DAYS;
      } else if (
          "week".equalsIgnoreCase(unit)
              || "weeks".equalsIgnoreCase(unit)
      ) {
        return WEEKS;
      } else if (
          "month".equalsIgnoreCase(unit)
              || "months".equalsIgnoreCase(unit)
      ) {
        return MONTHS;
      } else if (
          "year".equalsIgnoreCase(unit)
              || "years".equalsIgnoreCase(unit)
      ) {
        return YEARS;
      } else {
        throw new IllegalArgumentException("Unknown duration unit: " + unit);
      }
    }

    abstract void toString(int count, StringBuilder sb);

    abstract void offset(int count, Calendar cal);

    abstract LocalDate plus(int count, LocalDate date);
  }

  /**
   * Some commonly used constant durations.
   */
  public static final DayDuration ZERO_DAYS = new DayDuration(0, Unit.DAYS);

  /**
   * Parses a duration compatible with results of toString.
   * Supports:
   * <ol>
   *   <li># day</li>
   *   <li># days</li>
   *   <li># week</li>
   *   <li># weeks</li>
   *   <li># month</li>
   *   <li># months</li>
   *   <li># year</li>
   *   <li># years</li>
   * </ol>
   *
   * @see  DayDuration#toString()
   */
  public static DayDuration valueOf(String duration) {
    int spacePos = duration.indexOf(' ');
    if (spacePos == -1) {
      throw new IllegalArgumentException("Space not found in duration: " + duration);
    }
    return getInstance(
        Integer.parseInt(duration.substring(0, spacePos)),
        Unit.valueOfUnit(duration.substring(spacePos + 1))
    );
  }

  public static DayDuration getInstance(int count, Unit unit) {
    // Shortcuts for constants
    if (count == 0 && unit == Unit.DAYS) {
      return ZERO_DAYS;
    }
    // Create new object
    return new DayDuration(count, unit);
  }

  private final int count;
  private final Unit unit;

  protected DayDuration(int count, Unit unit) {
    this.count = count;
    this.unit = unit;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    toString(sb);
    return sb.toString();
  }

  public void toString(StringBuilder sb) {
    unit.toString(count, sb);
  }

  public int getCount() {
    return count;
  }

  public Unit getUnit() {
    return unit;
  }

  /**
   * Offsets the given calendar by the unit and count.
   * The passed-in Calendar is modified.
   */
  public void offset(Calendar cal) {
    unit.offset(count, cal);
  }

  /**
   * Offsets the given date by the unit and count.
   */
  public LocalDate plus(LocalDate date) {
    return unit.plus(count, date);
  }
}
