package com.cse110.eventlit.db;

/**
 * Created by vansh on 2/24/17.
 */

public class UserEventRsvp {
    public String orgid;
    public String eventid;
    public Event.RSVPStatus rsvpStatus;

    public UserEventRsvp(String orgid, String eventid, Event.RSVPStatus rsvpStatus) {
        this.orgid = orgid;
        this.eventid = eventid;
        this.rsvpStatus = rsvpStatus;
    }
}
