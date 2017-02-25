package com.cse110.eventlit.db;

import org.json.JSONObject;

public class Rsvp {
    private String orgid;
    private String eventid;
    private Event.RSVPStatus rsvpStatus;

    public Rsvp(String orgid, String eventid, Event.RSVPStatus rsvpStatus) {
        this.orgid = orgid;
        this.eventid = eventid;
        this.rsvpStatus = rsvpStatus;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("orgid", orgid);
            json.put("eventid", eventid);
            json.put("rsvpStatus", rsvpStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Rsvp) {
            Rsvp rsvpOther = (Rsvp) other;
            return this.orgid.equals(rsvpOther.orgid)
                    && this.eventid.equals(rsvpOther.eventid)
                    && this.rsvpStatus == rsvpOther.rsvpStatus;
        }
        return false;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public Event.RSVPStatus getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(Event.RSVPStatus rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}
