/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2023  AO Industries, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import com.aoapps.lang.util.CalendarUtils;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Iterator;
import org.junit.Test;

public class RecurringTest {

  @Test
  public void testParseNull() {
    assertNull(Recurring.parse(null));
  }

  @Test
  public void testParseEveryday() {
    assertSame(Recurring.EVERYDAY, Recurring.parse("everyday"));
    assertSame(Recurring.EVERYDAY, Recurring.parse("everyDay"));
  }

  @Test
  public void testParseWeekdays() {
    assertSame(Recurring.WEEKDAYS, Recurring.parse("weekdays"));
    assertSame(Recurring.WEEKDAYS, Recurring.parse("wEekdays"));
  }

  private static void checkParseDayOfWeekList(String recurring, DayOfWeek... expectedDaysOfWeek) {
    Recurring r = Recurring.parse(recurring);
    assertSame(Recurring.DayOfWeekList.class, r.getClass());
    Recurring.DayOfWeekList daysOfWeekList = (Recurring.DayOfWeekList) r;
    assertEquals(EnumSet.of(expectedDaysOfWeek[0], expectedDaysOfWeek), daysOfWeekList.getDaysOfWeek());
  }

  @Test
  @SuppressWarnings("ThrowableResultIgnored")
  public void testParseOn() {
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("on "));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("on blarg"));
    assertSame(Recurring.EVERYDAY, Recurring.parse("on sun, mon, tue, wed, Thursday, Fridays, sat"));
    assertSame(Recurring.WEEKDAYS, Recurring.parse("on mon, tue, wed, Thursday, Fridays"));
    assertSame(Recurring.WEEKDAYS, Recurring.parse("on fri, mon, tue, wed, Thursday, Fridays"));
    checkParseDayOfWeekList("On Mondays", DayOfWeek.MONDAY);
    checkParseDayOfWeekList("On Monday, Tuesday, Mon, tue, Fridays", DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.TUESDAY);
  }

  private static void checkParseMonthList(String recurring, Month... expectedMonths) {
    Recurring r = Recurring.parse(recurring);
    assertSame(Recurring.MonthList.class, r.getClass());
    Recurring.MonthList monthList = (Recurring.MonthList) r;
    assertEquals(EnumSet.of(expectedMonths[0], expectedMonths), monthList.getMonths());
  }

  @Test
  @SuppressWarnings("ThrowableResultIgnored")
  public void testParseIn() {
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("in "));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("in blarg"));
    assertSame(Recurring.MONTHLY, Recurring.parse("in januaries, february, marches, apr, may, jun, jul, aug, sep, oct, nov, dec"));
    checkParseMonthList("in march", Month.MARCH);
    checkParseMonthList("in march, apr, decembers", Month.DECEMBER, Month.MARCH, Month.APRIL);
  }

  @Test
  public void testParseWeekly() {
    assertSame(Recurring.WEEKLY, Recurring.parse("weekly"));
    assertSame(Recurring.WEEKLY, Recurring.parse("WEEKLY"));
  }

  @Test
  public void testParseMonthly() {
    assertSame(Recurring.MONTHLY, Recurring.parse("monthly"));
    assertSame(Recurring.MONTHLY, Recurring.parse("MONthly"));
  }

  @Test
  public void testParseYearly() {
    assertSame(Recurring.YEARLY, Recurring.parse("yearly"));
    assertSame(Recurring.YEARLY, Recurring.parse("YEarLY"));
  }

  private static void checkParseEvery(String recurring, int expectedIncrement, int expectedField) {
    Recurring r = Recurring.parse(recurring);
    assertSame(Recurring.Every.class, r.getClass());
    Recurring.Every every = (Recurring.Every) r;
    assertEquals(expectedIncrement, every.getIncrement());
    assertEquals(expectedField, every.getField());
  }

  private static void checkParseEveryByDayOfWeek(String recurring, int expectedIncrement, DayOfWeek expectedDayOfWeek) {
    Recurring r = Recurring.parse(recurring);
    assertSame(Recurring.EveryByDayOfWeek.class, r.getClass());
    Recurring.EveryByDayOfWeek every = (Recurring.EveryByDayOfWeek) r;
    assertEquals(expectedIncrement, every.getIncrement());
    assertEquals(expectedDayOfWeek, every.getDayOfWeek());
  }

  private static void checkParseEveryByMonth(String recurring, int expectedIncrement, Month expectedMonth) {
    Recurring r = Recurring.parse(recurring);
    assertSame(Recurring.EveryByMonth.class, r.getClass());
    Recurring.EveryByMonth every = (Recurring.EveryByMonth) r;
    assertEquals(expectedIncrement, every.getIncrement());
    assertEquals(expectedMonth, every.getMonth());
  }

  @Test
  @SuppressWarnings("ThrowableResultIgnored")
  public void testParseEvery() {
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every "));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every 1"));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every 1 "));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every blarg "));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every 1 blarg"));
    assertThrows(IllegalArgumentException.class, () -> Recurring.parse("every blarg week"));
    assertSame(Recurring.EVERYDAY, Recurring.parse("every 1 day"));
    assertSame(Recurring.EVERYDAY, Recurring.parse("Every 1 Days"));
    assertSame(Recurring.WEEKLY, Recurring.parse("every 1 week"));
    assertSame(Recurring.MONTHLY, Recurring.parse("every 1 months"));
    assertSame(Recurring.YEARLY, Recurring.parse("every 1 year"));
    checkParseDayOfWeekList("every 1 monday", DayOfWeek.MONDAY);
    checkParseDayOfWeekList("every 1 tuesdays", DayOfWeek.TUESDAY);
    checkParseMonthList("every 1 january", Month.JANUARY);
    checkParseMonthList("every 1 februaries", Month.FEBRUARY);
    assertSame(Recurring.YEARLY, Recurring.parse("every 12 Months"));
    assertSame(Recurring.YEARLY, Recurring.parse("every 12 month"));
    assertSame(Recurring.YEARLY, Recurring.parse("every twelfth month"));
    assertSame(Recurring.YEARLY, Recurring.parse("every twelfth MONTHS"));
    checkParseEvery("every 2 day", 2, Calendar.DAY_OF_MONTH);
    checkParseEvery("every other day", 2, Calendar.DAY_OF_MONTH);
    checkParseEvery("every SEcond days", 2, Calendar.DAY_OF_MONTH);
    checkParseEvery("every 3 weeks", 3, Calendar.WEEK_OF_YEAR);
    checkParseEvery("every THIRD week", 3, Calendar.WEEK_OF_YEAR);
    checkParseEvery("every eleventh month", 11, Calendar.MONTH);
    checkParseEvery("every 6 months", 6, Calendar.MONTH);
    checkParseEvery("every twelfth year", 12, Calendar.YEAR);
    checkParseEvery("every 100 years", 100, Calendar.YEAR);
    checkParseEveryByDayOfWeek("every 2 mondays", 2, DayOfWeek.MONDAY);
    checkParseEveryByDayOfWeek("every eighth friday", 8, DayOfWeek.FRIDAY);
    checkParseEveryByDayOfWeek("every seventh sundayS", 7, DayOfWeek.SUNDAY);
    checkParseEveryByMonth("every 45 januarys", 45, Month.JANUARY);
    checkParseEveryByMonth("every fourth januaries", 4, Month.JANUARY);
    checkParseEveryByMonth("every fifth marches", 5, Month.MARCH);
  }

  @Test
  public void testCheckScheduleFromEveryday() {
    Recurring r = Recurring.EVERYDAY;
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleEveryday() {
    Iterator<Calendar> schedule = Recurring.EVERYDAY.getScheduleIterator(CalendarUtils.parseDate("2023-11-07"));
    assertEquals(CalendarUtils.parseDate("2023-11-07"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-08"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromWeekdays() {
    Recurring r = Recurring.WEEKDAYS;
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-11"), "test"));
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-12"), "test"));
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-13"), "test"));
  }

  @Test
  public void testScheduleWeekdays() {
    Iterator<Calendar> schedule = Recurring.WEEKDAYS.getScheduleIterator(CalendarUtils.parseDate("2023-11-11"));
    assertEquals(CalendarUtils.parseDate("2023-11-13"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-14"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-15"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-16"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-17"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-20"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-21"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromDayOfWeekList() {
    Recurring r = new Recurring.DayOfWeekList(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-04"), "test"));
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-06"), "test"));
  }

  @Test
  public void testScheduleDayOfWeekList() {
    Iterator<Calendar> schedule = new Recurring.DayOfWeekList(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        .getScheduleIterator(CalendarUtils.parseDate("2023-11-04"));
    assertEquals(CalendarUtils.parseDate("2023-11-06"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-10"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-13"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-17"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromWeekly() {
    Recurring r = Recurring.WEEKLY;
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleWeekly() {
    Iterator<Calendar> schedule = Recurring.WEEKLY.getScheduleIterator(CalendarUtils.parseDate("2023-11-07"));
    assertEquals(CalendarUtils.parseDate("2023-11-07"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-14"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-21"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-12-05"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromMonthly() {
    Recurring r = Recurring.MONTHLY;
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleMonthly() {
    Iterator<Calendar> schedule = Recurring.MONTHLY.getScheduleIterator(CalendarUtils.parseDate("2023-01-31"));
    assertEquals(CalendarUtils.parseDate("2023-01-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-03-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-04-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-05-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-06-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-07-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-08-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-09-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-10-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-12-31"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromMonthList() {
    Recurring r = new Recurring.MonthList(EnumSet.of(Month.JANUARY, Month.FEBRUARY));
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2022-12-31"), "test"));
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-01-31"), "test"));
  }

  @Test
  public void testScheduleMonthList() {
    Iterator<Calendar> schedule = new Recurring.MonthList(EnumSet.of(Month.JANUARY, Month.FEBRUARY))
        .getScheduleIterator(CalendarUtils.parseDate("2022-12-31"));
    assertEquals(CalendarUtils.parseDate("2023-01-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2024-01-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2024-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2025-01-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2025-02-28"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromYearly() {
    Recurring r = Recurring.YEARLY;
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleYearly() {
    Iterator<Calendar> schedule = Recurring.YEARLY.getScheduleIterator(CalendarUtils.parseDate("2024-02-29"));
    assertEquals(CalendarUtils.parseDate("2024-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2025-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2026-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2027-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2028-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2029-02-28"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryFieldDay() {
    Recurring r = new Recurring.Every(3, Calendar.DAY_OF_MONTH);
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleEveryFieldDay() {
    Iterator<Calendar> schedule = new Recurring.Every(3, Calendar.DAY_OF_MONTH)
        .getScheduleIterator(CalendarUtils.parseDate("2023-11-07"));
    assertEquals(CalendarUtils.parseDate("2023-11-07"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-10"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-13"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-16"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryFieldWeek() {
    Recurring r = new Recurring.Every(3, Calendar.WEEK_OF_YEAR);
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleEveryFieldWeek() {
    Iterator<Calendar> schedule = new Recurring.Every(3, Calendar.WEEK_OF_YEAR)
        .getScheduleIterator(CalendarUtils.parseDate("2023-11-07"));
    assertEquals(CalendarUtils.parseDate("2023-11-07"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-12-19"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2024-01-09"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryFieldMonth() {
    Recurring r = new Recurring.Every(11, Calendar.MONTH);
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleEveryFieldMonth() {
    Iterator<Calendar> schedule = new Recurring.Every(11, Calendar.MONTH)
        .getScheduleIterator(CalendarUtils.parseDate("2023-01-31"));
    assertEquals(CalendarUtils.parseDate("2023-01-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-12-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2024-11-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2025-10-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2026-09-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2027-08-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2028-07-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2029-06-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2030-05-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2031-04-30"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2032-03-31"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2033-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2034-01-31"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryFieldYear() {
    Recurring r = new Recurring.Every(2, Calendar.YEAR);
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-07"), "test"));
  }

  @Test
  public void testScheduleEveryFieldYear() {
    Iterator<Calendar> schedule = new Recurring.Every(2, Calendar.YEAR)
        .getScheduleIterator(CalendarUtils.parseDate("2024-02-29"));
    assertEquals(CalendarUtils.parseDate("2024-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2026-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2028-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2030-02-28"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryByDayOfWeek() {
    Recurring r = new Recurring.EveryByDayOfWeek(2, DayOfWeek.SUNDAY);
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-04"), "test"));
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2023-11-05"), "test"));
  }

  @Test
  public void testScheduleEveryByDayOfWeek() {
    Iterator<Calendar> schedule = new Recurring.EveryByDayOfWeek(2, DayOfWeek.SUNDAY)
        .getScheduleIterator(CalendarUtils.parseDate("2023-11-07"));
    assertEquals(CalendarUtils.parseDate("2023-11-12"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-11-26"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2023-12-10"), schedule.next());
  }

  @Test
  public void testCheckScheduleFromEveryByMonth() {
    Recurring r = new Recurring.EveryByMonth(2, Month.FEBRUARY);
    assertNotNull(r.checkScheduleFrom(CalendarUtils.parseDate("2024-01-31"), "test"));
    assertNull(r.checkScheduleFrom(CalendarUtils.parseDate("2024-02-29"), "test"));
  }

  @Test
  public void testScheduleEveryByMonth() {
    Iterator<Calendar> schedule = new Recurring.EveryByMonth(2, Month.FEBRUARY)
        .getScheduleIterator(CalendarUtils.parseDate("2024-01-31"));
    assertEquals(CalendarUtils.parseDate("2024-02-29"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2026-02-28"), schedule.next());
    assertEquals(CalendarUtils.parseDate("2028-02-29"), schedule.next());
  }
}
