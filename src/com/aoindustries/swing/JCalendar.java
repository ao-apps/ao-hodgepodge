package com.aoindustries.swing;


/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * @author  AO Industries, Inc.
 */
public class JCalendar extends JPanel implements MouseListener {

    private static final String[] headers={"S", "M", "T", "W", "T", "F", "S"};

    private static final String[] months={"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private Arrow
        monthLeft,
        monthRight,
        yearLeft,
        yearRight
    ;

    private JLabel
        monthLabel,
        yearLabel
    ;

    private Calendar cal;

    private boolean selectWeek;

    private JLabel[][] grid=new JLabel[7][7];

    private List<CalendarListener> calendarListeners;

    public JCalendar() {
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        init(cal);
    }

    public JCalendar(long time) {
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(time);
        init(cal);
    }
    
    public JCalendar(Calendar cal) {
        init(cal);
    }
    
    private void init(Calendar cal) {
        setLayout(new BorderLayout());
        JPanel sliders=new JPanel(new BorderLayout());
        add(sliders, BorderLayout.NORTH);
        
        JPanel leftArrows=new JPanel(new GridLayout(2, 1));
        JPanel labels=new JPanel(new GridLayout(2, 1));
        JPanel rightArrows=new JPanel(new GridLayout(2, 1));
        sliders.add(leftArrows, BorderLayout.WEST);
        sliders.add(labels, BorderLayout.CENTER);
        sliders.add(rightArrows, BorderLayout.EAST);

        Border border=new BevelBorder(BevelBorder.RAISED);
        leftArrows.add(monthLeft=new Arrow(Arrow.LEFT, border));
        leftArrows.add(yearLeft=new Arrow(Arrow.LEFT, border));
        monthLeft.setPreferredSize(new Dimension(20, 20));
        yearLeft.setPreferredSize(new Dimension(20, 20));
        monthLeft.addMouseListener(this);
        yearLeft.addMouseListener(this);
        
        Border labelBorder=new BevelBorder(BevelBorder.LOWERED);
        labels.add(monthLabel=new JLabel("", SwingConstants.CENTER));
        labels.add(yearLabel=new JLabel("", SwingConstants.CENTER));
        monthLabel.setBorder(labelBorder);
        monthLabel.setOpaque(true);
        monthLabel.setBackground(Color.white);
        yearLabel.setBorder(labelBorder);
        yearLabel.setOpaque(true);
        yearLabel.setBackground(Color.white);
        
        rightArrows.add(monthRight=new Arrow(Arrow.RIGHT, border));
        rightArrows.add(yearRight=new Arrow(Arrow.RIGHT, border));
        monthRight.setPreferredSize(new Dimension(20, 20));
        yearRight.setPreferredSize(new Dimension(20, 20));
        monthRight.addMouseListener(this);
        yearRight.addMouseListener(this);

        JPanel gridPanel=new JPanel(new GridLayout(7, 7, 1, 1));
        for(int y=0;y<7;y++) {
            for(int x=0;x<7;x++) {
                JLabel label=new JLabel(y==0?headers[x]:"", SwingConstants.CENTER);
                if(y==0) label.setBackground(new Color(0xe0e0e0));
                else label.addMouseListener(this);
                label.setOpaque(true);
                label.setPreferredSize(new Dimension(32, 12));
                gridPanel.add(grid[x][y]=label);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        setDate(cal);
    }
    
    synchronized public void addCalendarListener(CalendarListener cl) {
        if(calendarListeners==null) calendarListeners=new ArrayList<CalendarListener>();
        calendarListeners.add(cl);
    }

    synchronized public void removeCalendarListener(CalendarListener cl) {
        if(calendarListeners!=null) {
            int size=calendarListeners.size();
            for(int c=0;c<size;c++) {
                if(calendarListeners.get(c)==cl) {
                    calendarListeners.remove(c);
                    break;
                }
            }
        }
    }

    public void setDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        this.cal=cal;
        int selYear=cal.get(Calendar.YEAR);
        int selMonth=cal.get(Calendar.MONTH);
        int selDay=cal.get(Calendar.DAY_OF_MONTH);
        int selWeekOfYear=cal.get(Calendar.WEEK_OF_YEAR);

        monthLabel.setText(months[selMonth]);
        yearLabel.setText(Integer.toString(selYear));

        Calendar today=Calendar.getInstance();
        int curYear=today.get(Calendar.YEAR);
        int curMonth=today.get(Calendar.MONTH);
        int curDay=today.get(Calendar.DAY_OF_MONTH);

        Calendar beginning=Calendar.getInstance();
        beginning.set(Calendar.YEAR, selYear);
        beginning.set(Calendar.MONTH, selMonth);
        beginning.set(Calendar.DAY_OF_MONTH, 1);
        while(beginning.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) beginning.add(Calendar.DATE, -1);

        for(int y=0;y<6;y++) {
            for(int calDayOfWeek=0;calDayOfWeek<7;calDayOfWeek++) {
                int calYear=beginning.get(Calendar.YEAR);
                int calMonth=beginning.get(Calendar.MONTH);
                int calDay=beginning.get(Calendar.DAY_OF_MONTH);
                int calWeekOfYear=beginning.get(Calendar.WEEK_OF_YEAR);

                JLabel label=grid[calDayOfWeek][y+1];
                label.setText(Integer.toString(beginning.get(Calendar.DAY_OF_MONTH)));
                label.setForeground(
                    (calYear==curYear && calMonth==curMonth && calDay==curDay)?Color.red
                    :calMonth==selMonth?Color.black:Color.lightGray
                );
                label.setBackground(
                    selectWeek?(
                        (calMonth==selMonth && calWeekOfYear==selWeekOfYear)?Color.YELLOW:Color.WHITE
                    ):(
                        (calMonth==selMonth && calDay==selDay)?Color.YELLOW:Color.WHITE
                    )
                );
                    
                beginning.add(Calendar.DATE, 1);
            }
        }
        
        // Notify the listeners
        synchronized(this) {
            if(calendarListeners!=null) {
                int size=calendarListeners.size();
                for(int c=0;c<size;c++) {
                    calendarListeners.get(c).calendarDaySelected(this, cal);
                }
            }
        }
    }
    
    public void setSelectWeek(boolean selectWeek) {
        this.selectWeek=selectWeek;
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
        Object src=e.getSource();

        if(src==monthLeft) {
            cal.roll(Calendar.MONTH, -1);
            setDate(cal);
        } else if(src==yearLeft) {
            cal.roll(Calendar.YEAR, -1);
            setDate(cal);
        } else if(src==monthRight) {
            cal.roll(Calendar.MONTH, 1);
            setDate(cal);
        } else if(src==yearRight) {
            cal.roll(Calendar.YEAR, 1);
            setDate(cal);
        } else {
            int selYear=cal.get(Calendar.YEAR);
            int selMonth=cal.get(Calendar.MONTH);

            Calendar beginning=Calendar.getInstance();
            beginning.set(Calendar.YEAR, selYear);
            beginning.set(Calendar.MONTH, selMonth);
            beginning.set(Calendar.DAY_OF_MONTH, 1);
            while(beginning.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) beginning.add(Calendar.DATE, -1);

        Loop:
            for(int y=0;y<6;y++) {
                for(int calDayOfWeek=0;calDayOfWeek<7;calDayOfWeek++) {
                    JLabel label=grid[calDayOfWeek][y+1];
                    if(label==src) {
                        int calYear=beginning.get(Calendar.YEAR);
                        int calMonth=beginning.get(Calendar.MONTH);
                        int calDay=beginning.get(Calendar.DAY_OF_MONTH);

                        Calendar newCal=Calendar.getInstance();
                        newCal.set(calYear, calMonth, calDay);
                        setDate(newCal);

                        break Loop;
                    }
                    beginning.add(Calendar.DATE, 1);
                }
            }
        }
    }
    
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }
    
    public Calendar getDate() {
        return cal;
    }
}
