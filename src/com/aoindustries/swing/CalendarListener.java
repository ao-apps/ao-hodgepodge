package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public interface CalendarListener {

    void calendarDaySelected(JCalendar source, Calendar day);
}
