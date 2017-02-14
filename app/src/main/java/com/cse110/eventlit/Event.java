package com.cse110.eventlit;

import java.util.Date;
import java.util.List;

/**
 * Created by vansh on 2/13/17.
 */

public class Event {

    public static final int C_FOOD = 0;
    public static final int C_SOCIAL = 1;
    public static final int C_GBM = 2;
    // ... add more category codes below

    public String title;
    public String orgid;
    public String description;
    public String location;
    public List<Integer> categoryCode;
    public int numRSVP;
    public Date date;

    public Event(String title,
                 String orgid,
                 String description,
                 String location,
                 List<Integer> categoryCode,
                 int numRSVP,
                 Date date)
    {
        this.title = title;
        this.orgid = orgid;
        this.description = description;
        this.location = location;
        this.categoryCode = categoryCode;
        this.numRSVP = numRSVP;
        this.date = date;
    }
}
