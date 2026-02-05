/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2016, 2020, 2021, 2022, 2023, 2025, 2026  AO Industries, Inc.
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

import com.aoapps.lang.Strings;
import com.aoapps.lang.util.UnmodifiableCalendar;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author  AO Industries, Inc.
 */
public abstract class Recurring {

  /**
   * All days of week as enum.
   */
  private static final EnumSet<DayOfWeek> ALL_DAYS_OF_WEEK_ENUM = EnumSet.allOf(DayOfWeek.class);

  /**
   * The set of all weekdays.
   */
  private static final EnumSet<DayOfWeek> WEEKDAYS_ENUM = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

  /**
   * All months as enum.
   */
  private static final EnumSet<Month> ALL_MONTHS = EnumSet.allOf(Month.class);

  /**
   * Parses a human-readable representation of a recurring schedule.
   * All parsing case-insensitive.
   * Supported values are:
   * <ul>
   *   <li>"everyday" - Task must be done every day</li>
   *   <li>"weekdays" - Monday through Friday (no holidays scheduled - yet)</li>
   *   <li>
   *     "on day-of-week-list" - on a comma-separated list of days of the week.
   *     The day of the week for the "on" date must be in this list.
   *     Example: "on Mondays, Wednesdays, Fridays".
   *     Example: "on Monday, Wednesday, Friday".
   *     Example: "On mon, tue, Sat".
   *     Example: "on Mondays, Wednesday, Fri".
   *   </li>
   *   <li>"weekly" - recurring on the same day every week.</li>
   *   <li>
   *     "monthly" - recurring on the same day every month.
   *     If the day is past the last day of the month, the last day of the month is used.
   *   </li>
   *   <li>
   *     "yearly" - recurring on the same day every year.
   *     If the day is past the last day of the month for a given year, the
   *     last day of the month is used. (Only affects February 29th)
   *   </li>
   *   <li>
   *     "every (other|second|third|fourth|fifth|sixth|seventh|eighth|ninth|tenth|eleventh|twelfth|###) {days|weeks|months|years|day-of-week|month-of-year}" - recurring every unit days.
   *     For "months", if the day is past the last day of the month, the last day of the month is used.
   *     For "day-of-week", is named day such as "every other Friday" or "every 4 Mondays".
   *     For "month-of-year", is named month such as "every other March" or "every 3 Januaries".
   *   </li>
   * </ul>
   *
   * @param  recurring  when null, returns null
   *
   * @throws IllegalArgumentException if unable to parse recurring
   */
  public static Recurring parse(String recurring) throws IllegalArgumentException {
    if (recurring == null) {
      return null;
    }
    if ("everyday".equalsIgnoreCase(recurring)) {
      return EVERYDAY;
    }
    if ("weekdays".equalsIgnoreCase(recurring)) {
      return WEEKDAYS;
    }
    if (recurring.length() >= 3 && "on ".equalsIgnoreCase(recurring.substring(0, 3))) {
      EnumSet<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
      for (String dayStr : Strings.splitCommaSpace(recurring.substring(3))) {
        boolean found = false;
        for (DayOfWeek dow : DayOfWeek.values) {
          if (
              dow.getLongName().equalsIgnoreCase(dayStr)
                  || dow.getShortName().equalsIgnoreCase(dayStr)
                  || dow.getPluralName().equalsIgnoreCase(dayStr)
          ) {
            daysOfWeek.add(dow);
            found = true;
            break;
          }
        }
        if (!found) {
          throw new IllegalArgumentException("Unexpected value for day of week: " + dayStr);
        }
      }
      if (daysOfWeek.isEmpty()) {
        throw new IllegalArgumentException("Must specify at least one day of the week for recurring on");
      }
      if (ALL_DAYS_OF_WEEK_ENUM.equals(daysOfWeek)) {
        return EVERYDAY;
      }
      if (WEEKDAYS_ENUM.equals(daysOfWeek)) {
        return WEEKDAYS;
      }
      return new DayOfWeekList(daysOfWeek);
    }
    if (recurring.length() >= 3 && "in ".equalsIgnoreCase(recurring.substring(0, 3))) {
      EnumSet<Month> months = EnumSet.noneOf(Month.class);
      for (String monthStr : Strings.splitCommaSpace(recurring.substring(3))) {
        boolean found = false;
        for (Month month : Month.values) {
          if (
              month.getLongName().equalsIgnoreCase(monthStr)
                  || month.getShortName().equalsIgnoreCase(monthStr)
                  || month.getPluralName().equalsIgnoreCase(monthStr)
                  || monthStr.equalsIgnoreCase(month.getAltPluralName().orElse(null))
          ) {
            months.add(month);
            found = true;
            break;
          }
        }
        if (!found) {
          throw new IllegalArgumentException("Unexpected value for month: " + monthStr);
        }
      }
      if (months.isEmpty()) {
        throw new IllegalArgumentException("Must specify at least one month for recurring in");
      }
      if (ALL_MONTHS.equals(months)) {
        return MONTHLY;
      }
      return new MonthList(months);
    }
    if ("weekly".equalsIgnoreCase(recurring)) {
      return WEEKLY;
    }
    if ("monthly".equalsIgnoreCase(recurring)) {
      return MONTHLY;
    }
    if ("yearly".equalsIgnoreCase(recurring)) {
      return YEARLY;
    }
    if (recurring.length() >= 6 && "every ".equalsIgnoreCase(recurring.substring(0, 6))) {
      int spacePos = recurring.indexOf(' ', 6);
      if (spacePos == -1) {
        throw new IllegalArgumentException("Second space not found in \"every ### {days|weeks|months|years|day-of-week|month-of-year}\": " + recurring);
      }
      String incrementStr = recurring.substring(6, spacePos);
      int increment;
      if ("other".equalsIgnoreCase(incrementStr) || "second".equalsIgnoreCase(incrementStr)) {
        increment = 2;
      } else if ("third".equalsIgnoreCase(incrementStr)) {
        increment = 3;
      } else if ("fourth".equalsIgnoreCase(incrementStr)) {
        increment = 4;
      } else if ("fifth".equalsIgnoreCase(incrementStr)) {
        increment = 5;
      } else if ("sixth".equalsIgnoreCase(incrementStr)) {
        increment = 6;
      } else if ("seventh".equalsIgnoreCase(incrementStr)) {
        increment = 7;
      } else if ("eighth".equalsIgnoreCase(incrementStr)) {
        increment = 8;
      } else if ("ninth".equalsIgnoreCase(incrementStr)) {
        increment = 9;
      } else if ("tenth".equalsIgnoreCase(incrementStr)) {
        increment = 10;
      } else if ("eleventh".equalsIgnoreCase(incrementStr)) {
        increment = 11;
      } else if ("twelfth".equalsIgnoreCase(incrementStr)) {
        increment = 12;
      } else {
        increment = Integer.parseInt(incrementStr);
      }
      String fieldStr = recurring.substring(spacePos + 1);
      int field = -1;
      DayOfWeek dayOfWeek = null;
      Month month = null;
      if ("days".equalsIgnoreCase(fieldStr) || "day".equalsIgnoreCase(fieldStr)) {
        field = Calendar.DAY_OF_MONTH;
      } else if ("weeks".equalsIgnoreCase(fieldStr) || "week".equalsIgnoreCase(fieldStr)) {
        field = Calendar.WEEK_OF_YEAR;
      } else if ("months".equalsIgnoreCase(fieldStr) || "month".equalsIgnoreCase(fieldStr)) {
        field = Calendar.MONTH;
      } else if ("years".equalsIgnoreCase(fieldStr) || "year".equalsIgnoreCase(fieldStr)) {
        field = Calendar.YEAR;
      } else {
        for (DayOfWeek dow : DayOfWeek.values) {
          if (
              dow.getLongName().equalsIgnoreCase(fieldStr)
                  || dow.getShortName().equalsIgnoreCase(fieldStr)
                  || dow.getPluralName().equalsIgnoreCase(fieldStr)
          ) {
            dayOfWeek = dow;
            break;
          }
        }
        if (dayOfWeek == null) {
          for (Month m : Month.values) {
            if (
                m.getLongName().equalsIgnoreCase(fieldStr)
                    || m.getShortName().equalsIgnoreCase(fieldStr)
                    || m.getPluralName().equalsIgnoreCase(fieldStr)
                    || fieldStr.equalsIgnoreCase(m.getAltPluralName().orElse(null))
            ) {
              month = m;
              break;
            }
          }
          if (month == null) {
            throw new IllegalArgumentException("Unexpected value for field: " + fieldStr);
          }
        }
      }
      if (increment == 1) {
        if (field == Calendar.DAY_OF_MONTH) {
          return EVERYDAY;
        }
        if (field == Calendar.WEEK_OF_YEAR) {
          return WEEKLY;
        }
        if (field == Calendar.MONTH) {
          return MONTHLY;
        }
        if (field == Calendar.YEAR) {
          return YEARLY;
        }
        if (dayOfWeek != null) {
          return new DayOfWeekList(EnumSet.of(dayOfWeek));
        }
        if (month != null) {
          return new MonthList(EnumSet.of(month));
        }
      }
      if (increment == 12 && field == Calendar.MONTH) {
        return YEARLY;
      }
      if (field != -1) {
        assert dayOfWeek == null;
        assert month == null;
        return new Every(increment, field);
      } else if (dayOfWeek != null) {
        assert field == -1;
        assert month == null;
        return new EveryByDayOfWeek(increment, dayOfWeek);
      } else if (month != null) {
        assert field == -1;
        assert dayOfWeek == null;
        return new EveryByMonth(increment, month);
      } else {
        throw new AssertionError();
      }
    }
    throw new IllegalArgumentException("Unexpected value for recurring: " + recurring);
  }

  /**
   * Recurring schedules are equal when they have both the same type and schedule.
   */
  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();

  public abstract String getRecurringDisplay();

  /**
   * Checks if the schedule can start on the given day.
   * Returns null if OK or a string reason of why not OK.
   */
  public String checkScheduleFrom(Calendar from, String attribute) {
    return null;
  }

  /**
   * Checks if the schedule can start on the given day.
   * Returns null if OK or a string reason of why not OK.
   */
  public String checkScheduleFrom(LocalDate from, String attribute) {
    return null;
  }

  /**
   * Gets an iterator over dates in the YYYY-MM-DD format.
   * The iteration starts at the given date.
   */
  public abstract Iterator<Calendar> getScheduleIterator(Calendar from);

  /**
   * Gets an iterator over dates in the YYYY-MM-DD format.
   * The iteration starts at the given date.
   */
  public abstract Iterator<LocalDate> getScheduleIterator(LocalDate from);

  private static final int EVERYDAY_HASH_CODE = 0;
  public static final Recurring EVERYDAY = new Recurring() {

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
      return o == EVERYDAY;
    }

    @Override
    public int hashCode() {
      return EVERYDAY_HASH_CODE;
    }

    @Override
    public String getRecurringDisplay() {
      return "Everyday";
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next day
          cal.add(Calendar.DAY_OF_MONTH, 1);
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          final LocalDate date = cal;
          // Move the calendar to the next day
          cal = cal.plusDays(1);
          return date;
        }
      };
    }
  };

  private static final int WEEKDAYS_HASH_CODE = EVERYDAY_HASH_CODE + 1;
  public static final Recurring WEEKDAYS = new Recurring() {

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
      return o == WEEKDAYS;
    }

    @Override
    public int hashCode() {
      return WEEKDAYS_HASH_CODE;
    }

    @Override
    public String getRecurringDisplay() {
      return "Week Days";
    }

    @Override
    public String checkScheduleFrom(Calendar from, String attribute) {
      // The first day "on" must be a weekday
      int dayOfWeek = from.get(Calendar.DAY_OF_WEEK);
      if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
        DayOfWeek fromDow = DayOfWeek.getByCalendarDayOfWeek(dayOfWeek);
        return "Day of week for \"" + attribute + "\" must be a weekday: " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public String checkScheduleFrom(LocalDate from, String attribute) {
      // The first day "on" must be a weekday
      java.time.DayOfWeek dayOfWeek = from.getDayOfWeek();
      if (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY) {
        DayOfWeek fromDow = DayOfWeek.getByJavaTimeDayOfWeek(dayOfWeek);
        return "Day of week for \"" + attribute + "\" must be a weekday: " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          // Skip past weekends
          while (true) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
              break;
            }
            // Move the calendar to the next day
            cal.add(Calendar.DAY_OF_MONTH, 1);
          }
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next day
          cal.add(Calendar.DAY_OF_MONTH, 1);
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          // Skip past weekends
          while (true) {
            java.time.DayOfWeek dayOfWeek = cal.getDayOfWeek();
            if (dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY) {
              break;
            }
            // Move the calendar to the next day
            cal = cal.plusDays(1);
          }
          final LocalDate date = cal;
          // Move the calendar to the next day
          cal = cal.plusDays(1);
          return date;
        }
      };
    }
  };

  public static class DayOfWeekList extends Recurring {

    private final EnumSet<DayOfWeek> daysOfWeek;

    public DayOfWeekList(Collection<DayOfWeek> daysOfWeek) {
      if (daysOfWeek.isEmpty()) {
        throw new IllegalArgumentException("At least one day of week required");
      }
      this.daysOfWeek = EnumSet.copyOf(daysOfWeek);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DayOfWeekList)) {
        return false;
      }
      DayOfWeekList other = (DayOfWeekList) o;
      return daysOfWeek.equals(other.daysOfWeek);
    }

    @Override
    public int hashCode() {
      return daysOfWeek.hashCode();
    }

    /**
     * For testing only.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    EnumSet<DayOfWeek> getDaysOfWeek() {
      return daysOfWeek;
    }

    @Override
    public String getRecurringDisplay() {
      StringBuilder sb = new StringBuilder("On ");
      boolean didOne = false;
      for (DayOfWeek dow : daysOfWeek) {
        if (didOne) {
          sb.append(", ");
        } else {
          didOne = true;
        }
        sb.append(dow.toString());
      }
      return sb.toString();
    }

    @Override
    public String checkScheduleFrom(Calendar from, String attribute) {
      // List of days must include the day of the week for the first day "on"
      DayOfWeek fromDow = DayOfWeek.getByCalendarDayOfWeek(from.get(Calendar.DAY_OF_WEEK));
      if (!daysOfWeek.contains(fromDow)) {
        return "Day of week for \"" + attribute + "\" not found in days of week list (" + Strings.join(daysOfWeek, ", ")
            + "): " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public String checkScheduleFrom(LocalDate from, String attribute) {
      // List of days must include the day of the week for the first day "on"
      DayOfWeek fromDow = DayOfWeek.getByJavaTimeDayOfWeek(from.getDayOfWeek());
      if (!daysOfWeek.contains(fromDow)) {
        return "Day of week for \"" + attribute + "\" not found in days of week list (" + Strings.join(daysOfWeek, ", ")
            + "): " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          // Skip past days that are not selected
          while (true) {
            DayOfWeek dow = DayOfWeek.getByCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
            if (daysOfWeek.contains(dow)) {
              break;
            }
            // Move the calendar to the next day
            cal.add(Calendar.DAY_OF_MONTH, 1);
          }
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next day
          cal.add(Calendar.DAY_OF_MONTH, 1);
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          // Skip past days that are not selected
          while (true) {
            DayOfWeek dow = DayOfWeek.getByJavaTimeDayOfWeek(cal.getDayOfWeek());
            if (daysOfWeek.contains(dow)) {
              break;
            }
            // Move the calendar to the next day
            cal = cal.plusDays(1);
          }
          final LocalDate date = cal;
          // Move the calendar to the next day
          cal = cal.plusDays(1);
          return date;
        }
      };
    }
  }

  private static final int WEEKLY_HASH_CODE = WEEKDAYS_HASH_CODE + 1;
  public static final Recurring WEEKLY = new Recurring() {

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
      return o == WEEKLY;
    }

    @Override
    public int hashCode() {
      return WEEKLY_HASH_CODE;
    }

    @Override
    public String getRecurringDisplay() {
      return "Weekly";
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next week
          cal.add(Calendar.WEEK_OF_YEAR, 1);
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          final LocalDate date = cal;
          // Move the calendar to the next week
          cal = cal.plusWeeks(1);
          return date;
        }
      };
    }
  };

  private static final int MONTHLY_HASH_CODE = WEEKLY_HASH_CODE + 1;
  public static final Recurring MONTHLY = new Recurring() {

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
      return o == MONTHLY;
    }

    @Override
    public int hashCode() {
      return MONTHLY_HASH_CODE;
    }

    @Override
    public String getRecurringDisplay() {
      return "Monthly";
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next month
          cal.set(Calendar.DAY_OF_MONTH, 1);
          cal.add(Calendar.MONTH, 1);
          int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.getDayOfMonth();
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          final LocalDate date = cal;
          // Move the calendar to the next month
          cal = cal.withDayOfMonth(1);
          cal = cal.plusMonths(1);
          int daysInMonth = cal.lengthOfMonth();
          cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }
  };

  public static class MonthList extends Recurring {

    private final EnumSet<Month> months;

    public MonthList(Collection<Month> months) {
      if (months.isEmpty()) {
        throw new IllegalArgumentException("At least one month required");
      }
      this.months = EnumSet.copyOf(months);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof MonthList)) {
        return false;
      }
      MonthList other = (MonthList) o;
      return months.equals(other.months);
    }

    @Override
    public int hashCode() {
      return months.hashCode();
    }

    /**
     * For testing only.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    EnumSet<Month> getMonths() {
      return months;
    }

    @Override
    public String getRecurringDisplay() {
      StringBuilder sb = new StringBuilder("In ");
      boolean didOne = false;
      for (Month month : months) {
        if (didOne) {
          sb.append(", ");
        } else {
          didOne = true;
        }
        sb.append(month.toString());
      }
      return sb.toString();
    }

    @Override
    public String checkScheduleFrom(Calendar from, String attribute) {
      // List of months must include the month for the first day "on"
      Month fromMonth = Month.getByCalendarMonth(from.get(Calendar.MONTH));
      if (!months.contains(fromMonth)) {
        return "Month for \"" + attribute + "\" not found in months list (" + Strings.join(months, ", ") + "): "
            + fromMonth.getLongName();
      }
      return null;
    }

    @Override
    public String checkScheduleFrom(LocalDate from, String attribute) {
      // List of months must include the month for the first day "on"
      Month fromMonth = Month.getByJavaTimeMonth(from.getMonth());
      if (!months.contains(fromMonth)) {
        return "Month for \"" + attribute + "\" not found in months list (" + Strings.join(months, ", ") + "): "
            + fromMonth.getLongName();
      }
      return null;
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          // Skip past months that are not selected
          while (true) {
            Month month = Month.getByCalendarMonth(cal.get(Calendar.MONTH));
            if (months.contains(month)) {
              break;
            }
            // Move the calendar to the next month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 1);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          }
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next month
          cal.set(Calendar.DAY_OF_MONTH, 1);
          cal.add(Calendar.MONTH, 1);
          int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.getDayOfMonth();
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          // Skip past months that are not selected
          while (true) {
            Month month = Month.getByJavaTimeMonth(cal.getMonth());
            if (months.contains(month)) {
              break;
            }
            // Move the calendar to the next month
            cal = cal.withDayOfMonth(1);
            cal = cal.plusMonths(1);
            int daysInMonth = cal.lengthOfMonth();
            cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          }
          final LocalDate date = cal;
          // Move the calendar to the next month
          cal = cal.withDayOfMonth(1);
          cal = cal.plusMonths(1);
          int daysInMonth = cal.lengthOfMonth();
          cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }
  }

  private static final int YEARLY_HASH_CODE = MONTHLY_HASH_CODE + 1;
  public static final Recurring YEARLY = new Recurring() {

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
      return o == YEARLY;
    }

    @Override
    public int hashCode() {
      return YEARLY_HASH_CODE;
    }

    @Override
    public String getRecurringDisplay() {
      return "Yearly";
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next year
          cal.set(Calendar.DAY_OF_MONTH, 1);
          cal.add(Calendar.YEAR, 1);
          int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.getDayOfMonth();
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          final LocalDate date = cal;
          // Move the calendar to the next year
          cal = cal.withDayOfMonth(1);
          cal = cal.plusYears(1);
          int daysInMonth = cal.lengthOfMonth();
          cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          return date;
        }
      };
    }
  };

  public static class Every extends Recurring {

    private final int increment;
    private final int field;
    private final ChronoUnit chronoUnit;

    public Every(int increment, int field) {
      if (increment < 1) {
        throw new IllegalArgumentException("Increment must be at least one");
      }
      this.increment = increment;
      switch (field) {
        case Calendar.DAY_OF_MONTH:
          chronoUnit = ChronoUnit.DAYS;
          break;
        case Calendar.WEEK_OF_YEAR:
          chronoUnit = ChronoUnit.WEEKS;
          break;
        case Calendar.MONTH:
          chronoUnit = ChronoUnit.MONTHS;
          break;
        case Calendar.YEAR:
          chronoUnit = ChronoUnit.YEARS;
          break;
        default:
          throw new IllegalArgumentException("Unexpected value for field: " + field);
      }
      this.field = field;
    }

    public Every(int increment, ChronoUnit chronoUnit) {
      if (increment < 1) {
        throw new IllegalArgumentException("Increment must be at least one");
      }
      this.increment = increment;
      switch (chronoUnit) {
        case DAYS:
          field = Calendar.DAY_OF_MONTH;
          break;
        case WEEKS:
          field = Calendar.WEEK_OF_YEAR;
          break;
        case MONTHS:
          field = Calendar.MONTH;
          break;
        case YEARS:
          field = Calendar.YEAR;
          break;
        default:
          throw new IllegalArgumentException("Unexpected value for chronoUnit: " + chronoUnit);
      }
      this.chronoUnit = chronoUnit;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Every)) {
        return false;
      }
      Every other = (Every) o;
      return
          increment == other.increment
              && field == other.field;
    }

    @Override
    public int hashCode() {
      return increment * 31 + field;
    }

    /**
     * For testing only.
     */
    int getIncrement() {
      return increment;
    }

    /**
     * For testing only.
     */
    int getField() {
      return field;
    }

    @Override
    public String getRecurringDisplay() {
      StringBuilder sb = new StringBuilder("Every ");
      sb.append(increment);
      switch (field) {
        case Calendar.DAY_OF_MONTH:
          sb.append(" days");
          break;
        case Calendar.WEEK_OF_YEAR:
          sb.append(" weeks");
          break;
        case Calendar.MONTH:
          sb.append(" months");
          break;
        case Calendar.YEAR:
          sb.append(" years");
          break;
        default:
          throw new AssertionError("Unexpected value for field: " + field);
      }
      return sb.toString();
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      switch (field) {
        case Calendar.DAY_OF_MONTH:
        case Calendar.WEEK_OF_YEAR:
          return new Iterator<>() {
            private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

            @Override
            public boolean hasNext() {
              return true;
            }

            /**
             * Never-ending iterator - will never throw {@link NoSuchElementException}.
             */
            @Override
            public Calendar next() {
              final Calendar date = (Calendar) cal.clone();
              // Move the calendar to the next by field and increment
              cal.add(field, increment);
              return date;
            }
          };
        case Calendar.MONTH:
        case Calendar.YEAR:
          return new Iterator<>() {
            private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
            private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

            @Override
            public boolean hasNext() {
              return true;
            }

            /**
             * Never-ending iterator - will never throw {@link NoSuchElementException}.
             */
            @Override
            public Calendar next() {
              final Calendar date = (Calendar) cal.clone();
              // Move the calendar to the next month
              cal.set(Calendar.DAY_OF_MONTH, 1);
              cal.add(field, increment);
              int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
              cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
              return date;
            }
          };
        default:
          throw new AssertionError("Unexpected value for field: " + field);
      }
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      switch (chronoUnit) {
        case DAYS:
        case WEEKS:
          return new Iterator<>() {
            private LocalDate cal = from;

            @Override
            public boolean hasNext() {
              return true;
            }

            /**
             * Never-ending iterator - will never throw {@link NoSuchElementException}.
             */
            @Override
            public LocalDate next() {
              final LocalDate date = cal;
              // Move the calendar to the next by field and increment
              cal = cal.plus(increment, chronoUnit);
              return date;
            }
          };
        case MONTHS:
        case YEARS:
          return new Iterator<>() {
            private final int dayOfMonth = from.getDayOfMonth();
            private LocalDate cal = from;

            @Override
            public boolean hasNext() {
              return true;
            }

            /**
             * Never-ending iterator - will never throw {@link NoSuchElementException}.
             */
            @Override
            public LocalDate next() {
              final LocalDate date = cal;
              // Move the calendar to the next month
              cal = cal.withDayOfMonth(1);
              cal = cal.plus(increment, chronoUnit);
              int daysInMonth = cal.lengthOfMonth();
              cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
              return date;
            }
          };
        default:
          throw new AssertionError("Unexpected value for chronoUnit: " + chronoUnit);
      }
    }
  }

  public static class EveryByDayOfWeek extends Recurring {

    private final int increment;
    private final DayOfWeek dayOfWeek;

    public EveryByDayOfWeek(int increment, DayOfWeek dayOfWeek) {
      if (increment < 1) {
        throw new IllegalArgumentException("Increment must be at least one");
      }
      this.increment = increment;
      this.dayOfWeek = Objects.requireNonNull(dayOfWeek);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof EveryByDayOfWeek)) {
        return false;
      }
      EveryByDayOfWeek other = (EveryByDayOfWeek) o;
      return
          increment == other.increment
              && dayOfWeek == other.dayOfWeek;
    }

    @Override
    public int hashCode() {
      return increment * 31 + dayOfWeek.hashCode();
    }

    /**
     * For testing only.
     */
    int getIncrement() {
      return increment;
    }

    /**
     * For testing only.
     */
    DayOfWeek getDayOfWeek() {
      return dayOfWeek;
    }

    @Override
    public String getRecurringDisplay() {
      StringBuilder sb = new StringBuilder("Every ");
      sb.append(increment).append(' ').append(increment == 1 ? dayOfWeek.getLongName() : dayOfWeek.getPluralName());
      return sb.toString();
    }

    @Override
    public String checkScheduleFrom(Calendar from, String attribute) {
      // Day of week must match the first day "on"
      DayOfWeek fromDow = DayOfWeek.getByCalendarDayOfWeek(from.get(Calendar.DAY_OF_WEEK));
      if (dayOfWeek != fromDow) {
        return "Day of week for \"" + attribute + "\" is not \"" + dayOfWeek.getLongName() + "\": " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public String checkScheduleFrom(LocalDate from, String attribute) {
      // Day of week must match the first day "on"
      DayOfWeek fromDow = DayOfWeek.getByJavaTimeDayOfWeek(from.getDayOfWeek());
      if (dayOfWeek != fromDow) {
        return "Day of week for \"" + attribute + "\" is not \"" + dayOfWeek.getLongName() + "\": " + fromDow.getLongName();
      }
      return null;
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          // Skip past days that are not selected
          while (true) {
            if (dayOfWeek == DayOfWeek.getByCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK))) {
              break;
            }
            // Move the calendar to the next day
            cal.add(Calendar.DAY_OF_MONTH, 1);
          }
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next increment week
          cal.add(Calendar.WEEK_OF_YEAR, increment);
          assert dayOfWeek == DayOfWeek.getByCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          // Skip past days that are not selected
          while (true) {
            if (dayOfWeek == DayOfWeek.getByJavaTimeDayOfWeek(cal.getDayOfWeek())) {
              break;
            }
            // Move the calendar to the next day
            cal = cal.plusDays(1);
          }
          final LocalDate date = cal;
          // Move the calendar to the next increment week
          cal = cal.plusWeeks(increment);
          assert dayOfWeek == DayOfWeek.getByJavaTimeDayOfWeek(cal.getDayOfWeek());
          return date;
        }
      };
    }
  }

  public static class EveryByMonth extends Recurring {

    private final int increment;
    private final Month month;

    public EveryByMonth(int increment, Month month) {
      if (increment < 1) {
        throw new IllegalArgumentException("Increment must be at least one");
      }
      this.increment = increment;
      this.month = Objects.requireNonNull(month);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof EveryByMonth)) {
        return false;
      }
      EveryByMonth other = (EveryByMonth) o;
      return
          increment == other.increment
              && month == other.month;
    }

    @Override
    public int hashCode() {
      return increment * 31 + month.hashCode();
    }

    /**
     * For testing only.
     */
    int getIncrement() {
      return increment;
    }

    /**
     * For testing only.
     */
    Month getMonth() {
      return month;
    }

    @Override
    public String getRecurringDisplay() {
      StringBuilder sb = new StringBuilder("Every ");
      sb.append(increment).append(' ').append(increment == 1 ? month.getLongName() : month.getPluralName());
      return sb.toString();
    }

    @Override
    public String checkScheduleFrom(Calendar from, String attribute) {
      // Month must match the first day "on"
      Month fromMonth = Month.getByCalendarMonth(from.get(Calendar.MONTH));
      if (month != fromMonth) {
        return "Month for \"" + attribute + "\" is not \"" + month.getLongName() + "\": " + fromMonth.getLongName();
      }
      return null;
    }

    @Override
    public String checkScheduleFrom(LocalDate from, String attribute) {
      // Month must match the first day "on"
      Month fromMonth = Month.getByJavaTimeMonth(from.getMonth());
      if (month != fromMonth) {
        return "Month for \"" + attribute + "\" is not \"" + month.getLongName() + "\": " + fromMonth.getLongName();
      }
      return null;
    }

    @Override
    public Iterator<Calendar> getScheduleIterator(final Calendar from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
        private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public Calendar next() {
          // Skip past months that are not selected
          while (true) {
            if (month == Month.getByCalendarMonth(cal.get(Calendar.MONTH))) {
              break;
            }
            // Move the calendar to the next month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 1);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          }
          final Calendar date = (Calendar) cal.clone();
          // Move the calendar to the next increment month
          cal.set(Calendar.DAY_OF_MONTH, 1);
          cal.add(Calendar.YEAR, increment);
          int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
          assert month == Month.getByCalendarMonth(cal.get(Calendar.MONTH));
          return date;
        }
      };
    }

    @Override
    public Iterator<LocalDate> getScheduleIterator(final LocalDate from) {
      return new Iterator<>() {
        private final int dayOfMonth = from.getDayOfMonth();
        private LocalDate cal = from;

        @Override
        public boolean hasNext() {
          return true;
        }

        /**
         * Never-ending iterator - will never throw {@link NoSuchElementException}.
         */
        @Override
        public LocalDate next() {
          // Skip past months that are not selected
          while (true) {
            if (month == Month.getByJavaTimeMonth(cal.getMonth())) {
              break;
            }
            // Move the calendar to the next month
            cal = cal.withDayOfMonth(1);
            cal = cal.plusMonths(1);
            int daysInMonth = cal.lengthOfMonth();
            cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          }
          final LocalDate date = cal;
          // Move the calendar to the next increment month
          cal = cal.withDayOfMonth(1);
          cal = cal.plusYears(increment);
          int daysInMonth = cal.lengthOfMonth();
          cal = cal.withDayOfMonth(Math.min(daysInMonth, dayOfMonth));
          assert month == Month.getByJavaTimeMonth(cal.getMonth());
          return date;
        }
      };
    }
  }
}
