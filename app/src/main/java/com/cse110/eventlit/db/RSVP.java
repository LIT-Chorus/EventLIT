package com.cse110.eventlit.db;

import org.json.JSONObject;

public class RSVP {

    /**
     * Enumeration of various RSVP statuses.
     */
    public enum Status {
        GOING,
        NOT_GOING,
        INTERESTED
    }

    public String orgid;
    public String eventid;
    public Status rsvpStatus;

    public RSVP() {}

    public RSVP(String orgid, String eventid, Status rsvpStatus) {
        this.orgid = orgid;
        this.eventid = eventid;
        this.rsvpStatus = rsvpStatus;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("orgid", orgid);
            json.put("eventid", eventid);
            json.put("rsvpStatus", rsvpStatus.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RSVP) {
            RSVP rsvpOther = (RSVP) other;
            return this.orgid == rsvpOther.orgid
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

    public Status getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(Status rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}
