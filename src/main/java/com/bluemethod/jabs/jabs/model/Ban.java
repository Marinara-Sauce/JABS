package com.bluemethod.jabs.jabs.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class handles bans, and provides all the information needed for one
 */
public class Ban 
{
    //Is the ban a perma ban
    private boolean permaBan;

    //The date the ban expires on
    private String liftedDate;

    //Date formatter
    private final SimpleDateFormat SDF = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");

    /**
     * Initializes this ban as a perma ban
     * 
     * @param permaBan is the ban permanent
     */
    public Ban(boolean permaBan)
    {
        this.permaBan = true;
        this.liftedDate = null;
    }

    /**
     * Initializes the ban as a temp ban
     * 
     * @param daysActive days the ban is active
     * @param hoursActive hours the ban is active
     * @param minutesActive minutes the ban is active
     */
    public Ban(int daysActive, int hoursActive, int minutesActive)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        c.add(Calendar.DATE, daysActive);
        c.add(Calendar.HOUR, hoursActive);
        c.add(Calendar.MINUTE, minutesActive);

        SDF.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        this.liftedDate = SDF.format(c.getTime());
        this.permaBan = false;
    }

    public boolean banActive()
    {
        if (permaBan) return true;

        Date lifted;
        try {
            lifted = SDF.parse(liftedDate);
            return lifted.compareTo(new Date()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPermaBan()
    {
        return this.permaBan;
    }

    public void setIsPermaBan(boolean permaBan)
    {
        this.permaBan = permaBan;
    }

    public String getLiftedDate()
    {
        return this.liftedDate;
    }

    public void setLiftedDate(String liftedDate)
    {
        this.liftedDate = liftedDate;
    }
}
