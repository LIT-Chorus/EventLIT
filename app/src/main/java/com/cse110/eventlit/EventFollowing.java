package com.cse110.eventlit;

/**
 * Created by vansh on 2/14/17.
 */

class EventFollowing {
    public String eventid;
    public int attendance_code;

    public EventFollowing() {
        eventid = "";
        attendance_code = -1;
    }

    public EventFollowing(String eventid, int attendance_code) {
        this.eventid = eventid;
        this.attendance_code = attendance_code;
    }
}