/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2016, 2020, 2021  AO Industries, Inc.
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
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author  AO Industries, Inc.
 */
public abstract class Recurring {

	/**
	 * Copy of values for internal use without temporary array copy.
	 */
	private static final DayOfWeek[] allDaysOfWeek = DayOfWeek.values();
	private static final Month[] allMonths = Month.values();

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
	 *     Example: "on Monday, Wednesday, Friday".
	 *     Example: "On mon, tue, Sat".
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
	 *     "every ### {days|weeks|months|years}" - recurring every unit days.
	 *     For "months", if the day is past the last day of the month, the last day of the month is used.
	 *   </li>
	 * </ul>
	 *
	 * @param  recurring  when null, returns null
	 *
	 * @throws IllegalArgumentException if unable to parse recurring
	 */
	public static Recurring parse(String recurring) throws IllegalArgumentException {
		if(recurring == null) return null;
		if("everyday".equalsIgnoreCase(recurring)) {
			return EVERYDAY;
		}
		if("weekdays".equalsIgnoreCase(recurring)) {
			return WEEKDAYS;
		}
		if(recurring.length()>=3 && recurring.substring(0, 3).equalsIgnoreCase("on ")) {
			EnumSet<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
			for(String dayStr : Strings.splitCommaSpace(recurring.substring(3))) {
				boolean found = false;
				for(DayOfWeek dow : allDaysOfWeek) {
					if(
						dow.getLongName().equalsIgnoreCase(dayStr)
						|| dow.getShortName().equalsIgnoreCase(dayStr)
					) {
						daysOfWeek.add(dow);
						found = true;
						break;
					}
				}
				if(!found) throw new IllegalArgumentException("Unexpected value for day of week: " + dayStr);
			}
			if(daysOfWeek.isEmpty()) throw new IllegalArgumentException("Must specify at least one day of the week for recurring on");
			return new DayOfWeekList(daysOfWeek);
		}
		if(recurring.length()>=3 && recurring.substring(0, 3).equalsIgnoreCase("in ")) {
			EnumSet<Month> months = EnumSet.noneOf(Month.class);
			for(String monthStr : Strings.splitCommaSpace(recurring.substring(3))) {
				boolean found = false;
				for(Month month : allMonths) {
					if(
						month.getLongName().equalsIgnoreCase(monthStr)
						|| month.getShortName().equalsIgnoreCase(monthStr)
					) {
						months.add(month);
						found = true;
						break;
					}
				}
				if(!found) throw new IllegalArgumentException("Unexpected value for month: " + monthStr);
			}
			if(months.isEmpty()) throw new IllegalArgumentException("Must specify at least one month for recurring in");
			return new MonthList(months);
		}
		if("weekly".equalsIgnoreCase(recurring)) {
			return WEEKLY;
		}
		if("monthly".equalsIgnoreCase(recurring)) {
			return MONTHLY;
		}
		if("yearly".equalsIgnoreCase(recurring)) {
			return YEARLY;
		}
		if(recurring.length()>=6 && recurring.substring(0, 6).equalsIgnoreCase("every ")) {
			int spacePos = recurring.indexOf(' ', 6);
			if(spacePos == -1) throw new IllegalArgumentException("Second space not found in \"every ### {days|weeks|months|years}\": " + recurring);
			int increment = Integer.parseInt(recurring.substring(6, spacePos));
			String fieldStr = recurring.substring(spacePos + 1);
			int field;
			if("days".equalsIgnoreCase(fieldStr) || "day".equalsIgnoreCase(fieldStr)) {
				field = Calendar.DAY_OF_MONTH;
			} else if("weeks".equalsIgnoreCase(fieldStr) || "week".equalsIgnoreCase(fieldStr)) {
				field = Calendar.WEEK_OF_YEAR;
			} else if("months".equalsIgnoreCase(fieldStr) || "month".equalsIgnoreCase(fieldStr)) {
				field = Calendar.MONTH;
			} else if("years".equalsIgnoreCase(fieldStr) || "year".equalsIgnoreCase(fieldStr)) {
				field = Calendar.YEAR;
			} else {
				throw new IllegalArgumentException("Unexpected value for field: " + fieldStr);
			}
			return new Every(increment, field);
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
	 * Gets an iterator over dates in the YYYY-MM-DD format.
	 * The iteration starts at the given date.
	 */
	public abstract Iterator<Calendar> getScheduleIterator(Calendar from);

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
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next day
					cal.add(Calendar.DAY_OF_MONTH, 1);
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
			if(dayOfWeek==Calendar.SATURDAY || dayOfWeek==Calendar.SUNDAY) {
				DayOfWeek fromDow = DayOfWeek.getByCalendarDayOfWeek(dayOfWeek);
				return "Day of week for \"" + attribute + "\" must be a weekday: " + fromDow.getLongName();
			}
			return null;
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					while(true) {
						int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
						if(dayOfWeek!=Calendar.SATURDAY && dayOfWeek!=Calendar.SUNDAY) {
							break;
						}
						// Move the calendar to the next day
						cal.add(Calendar.DAY_OF_MONTH, 1);
					}
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next day
					cal.add(Calendar.DAY_OF_MONTH, 1);
					return date;
				}
			};
		}
	};

	public static class DayOfWeekList extends Recurring {

		private final EnumSet<DayOfWeek> daysOfWeek;

		public DayOfWeekList(Collection<DayOfWeek> daysOfWeek) {
			if(daysOfWeek.isEmpty()) throw new IllegalArgumentException("At least one day of week required");
			this.daysOfWeek = EnumSet.copyOf(daysOfWeek);
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof DayOfWeekList)) return false;
			DayOfWeekList other = (DayOfWeekList)o;
			return daysOfWeek.equals(other.daysOfWeek);
		}

		@Override
		public int hashCode() {
			return daysOfWeek.hashCode();
		}

		@Override
		public String getRecurringDisplay() {
			StringBuilder sb = new StringBuilder("On ");
			boolean didOne = false;
			for(DayOfWeek dow : daysOfWeek) {
				if(didOne) sb.append(", ");
				else didOne = true;
				sb.append(dow.toString());
			}
			return sb.toString();
		}

		@Override
		public String checkScheduleFrom(Calendar from, String attribute) {
			// List of days must include the day of the week for the first day "on"
			DayOfWeek fromDow = DayOfWeek.getByCalendarDayOfWeek(from.get(Calendar.DAY_OF_WEEK));
			if(!daysOfWeek.contains(fromDow)) {
				return "Day of week for \"" + attribute + "\" not found in days of week list: " + fromDow.getLongName();
			}
			return null;
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					while(true) {
						DayOfWeek dow = DayOfWeek.getByCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
						if(daysOfWeek.contains(dow)) {
							break;
						}
						// Move the calendar to the next day
						cal.add(Calendar.DAY_OF_MONTH, 1);
					}
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next day
					cal.add(Calendar.DAY_OF_MONTH, 1);
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
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next week
					cal.add(Calendar.WEEK_OF_YEAR, 1);
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
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next month
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.MONTH, 1);
					int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
					return date;
				}
			};
		}
	};

	public static class MonthList extends Recurring {

		private final EnumSet<Month> months;

		public MonthList(Collection<Month> months) {
			if(months.isEmpty()) throw new IllegalArgumentException("At least one month required");
			this.months = EnumSet.copyOf(months);
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof MonthList)) return false;
			MonthList other = (MonthList)o;
			return months.equals(other.months);
		}

		@Override
		public int hashCode() {
			return months.hashCode();
		}

		@Override
		public String getRecurringDisplay() {
			StringBuilder sb = new StringBuilder("In ");
			boolean didOne = false;
			for(Month month : months) {
				if(didOne) sb.append(", ");
				else didOne = true;
				sb.append(month.toString());
			}
			return sb.toString();
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					while(true) {
						Month month = Month.getByCalendarMonth(cal.get(Calendar.MONTH));
						if(months.contains(month)) {
							break;
						}
						// Move the calendar to the next day
						cal.add(Calendar.MONTH, 1);
					}
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next month
					cal.add(Calendar.MONTH, 1);
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
			// Java 9: new Iterator<>
			return new Iterator<Calendar>() {
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
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next year
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.YEAR, 1);
					int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
					return date;
				}
			};
		}
	};

	public static class Every extends Recurring {

		private final int increment;
		private final int field;

		public Every(int increment, int field) {
			if(increment < 1) throw new IllegalArgumentException("Increment must be at least one");
			this.increment = increment;
			if(
				field != Calendar.DAY_OF_MONTH
				&& field != Calendar.WEEK_OF_YEAR
				&& field != Calendar.MONTH
				&& field != Calendar.YEAR
			) throw new IllegalArgumentException("Unexpected value for field: " + field);
			this.field = field;
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Every)) return false;
			Every other = (Every)o;
			return
				increment == other.increment
				&& field == other.field
			;
		}

		@Override
		public int hashCode() {
			return increment * 31 + field;
		}

		@Override
		public String getRecurringDisplay() {
			StringBuilder sb = new StringBuilder("Every ");
			sb.append(increment);
			switch(field) {
				case Calendar.DAY_OF_MONTH :
					sb.append(" days");
					break;
				case Calendar.WEEK_OF_YEAR :
					sb.append(" weeks");
					break;
				case Calendar.MONTH :
					sb.append(" months");
					break;
				case Calendar.YEAR :
					sb.append(" years");
					break;
				default :
					throw new AssertionError("Unexpected value for field: " + field);
			}
			return sb.toString();
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			switch(field) {
				case Calendar.DAY_OF_MONTH :
				case Calendar.WEEK_OF_YEAR :
				case Calendar.YEAR :
					// Java 9: new Iterator<>
					return new Iterator<Calendar>() {
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
							Calendar date = (Calendar)cal.clone();
							// Move the calendar to the next by field and increment
							cal.add(field, increment);
							return date;
						}
					};
				case Calendar.MONTH :
					// Java 9: new Iterator<>
					return new Iterator<Calendar>() {
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
							Calendar date = (Calendar)cal.clone();
							// Move the calendar to the next month
							cal.set(Calendar.DAY_OF_MONTH, 1);
							cal.add(Calendar.MONTH, increment);
							int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
							cal.set(Calendar.DAY_OF_MONTH, Math.min(daysInMonth, dayOfMonth));
							return date;
						}
					};
				default :
					throw new AssertionError("Unexpected value for field: " + field);
			}
		}
	}
}
