/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util.schedule;

import com.aoindustries.util.StringUtility;
import com.aoindustries.util.UnmodifiableCalendar;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

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
	 * @throws IllegalArgumentException if unable to parse recurring
	 */
	public static Recurring parse(String recurring) throws IllegalArgumentException {
		if("everyday".equalsIgnoreCase(recurring)) {
			return EVERYDAY;
		}
		if("weekdays".equalsIgnoreCase(recurring)) {
			return WEEKDAYS;
		}
		if(recurring.length()>=3 && recurring.substring(0, 3).equalsIgnoreCase("on ")) {
			EnumSet<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
			for(String dayStr : StringUtility.splitStringCommaSpace(recurring.substring(3))) {
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
			for(String monthStr : StringUtility.splitStringCommaSpace(recurring.substring(3))) {
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
	abstract public boolean equals(Object o);

	abstract public String getRecurringDisplay();

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
	abstract public Iterator<Calendar> getScheduleIterator(Calendar from);

	public static final Recurring EVERYDAY = new Recurring() {

		@Override
		public boolean equals(Object o) {
			return o == EVERYDAY;
		}

		@Override
		public String getRecurringDisplay() {
			return "Everyday";
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			return new Iterator<Calendar>() {
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

				@Override
				public Calendar next() {
					Calendar date = (Calendar)cal.clone();
					// Move the calendar to the next day
					cal.add(Calendar.DAY_OF_MONTH, 1);
					return date;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	};

	public static final Recurring WEEKDAYS = new Recurring() {

		@Override
		public boolean equals(Object o) {
			return o == WEEKDAYS;
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
			return new Iterator<Calendar>() {
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

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

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
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
			return new Iterator<Calendar>() {
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

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

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	public static final Recurring MONTHLY = new Recurring() {

		@Override
		public boolean equals(Object o) {
			return o == MONTHLY;
		}

		@Override
		public String getRecurringDisplay() {
			return "Monthly";
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			return new Iterator<Calendar>() {
				private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

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

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
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
			return new Iterator<Calendar>() {
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

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

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	public static final Recurring YEARLY = new Recurring() {

		@Override
		public boolean equals(Object o) {
			return o == YEARLY;
		}

		@Override
		public String getRecurringDisplay() {
			return "Yearly";
		}

		@Override
		public Iterator<Calendar> getScheduleIterator(final Calendar from) {
			return new Iterator<Calendar>() {
				private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
				private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

				@Override
				public boolean hasNext() {
					return true;
				}

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

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
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
					return new Iterator<Calendar>() {
						private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

						@Override
						public boolean hasNext() {
							return true;
						}

						@Override
						public Calendar next() {
							Calendar date = (Calendar)cal.clone();
							// Move the calendar to the next by field and increment
							cal.add(field, increment);
							return date;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				case Calendar.MONTH :
					return new Iterator<Calendar>() {
						private final int dayOfMonth = from.get(Calendar.DAY_OF_MONTH);
						private final Calendar cal = UnmodifiableCalendar.unwrapClone(from);

						@Override
						public boolean hasNext() {
							return true;
						}

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

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				default :
					throw new AssertionError("Unexpected value for field: " + field);
			}
		}
	}
}
