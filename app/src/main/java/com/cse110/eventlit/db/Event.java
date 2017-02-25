package com.cse110.eventlit.db;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    /**
     * Enumeration of various RSVP statuses.
     */
    public enum RSVPStatus {
        GOING,
        NOT_GOING,
        MAYBE,
        INTERESTED
    }

    /**
     * Title of the event.
     */
    private String title;

    /**
     * Description of the event.
     */
    private String description;

    /**
     * String ID of the organization hosting the event.
     */
    private String orgid;

    /**
     * Start and end dates of the event, as milliseconds since the Unix epoch (Jan 1, 1970).
     */
    private long startTime;
    private long endTime;

    /**
     * Named location of the event. e.g. "La Jolla, CA"
     */
    private String location;

    /**
     * Category of the event.
     */
    private String category;

    /**
     * Maximum number of persons that the event can hold.
     */
    private int maxCapacity;

    /**
     * List of UIDs of attendees to the event.
     */
    private List<String> attendees;

    /**
     * Full constructor containing all event information.
     */
    public Event(String title, String description, String orgid, Calendar startDate,
                 Calendar endDate, String location, String category, int maxCapacity) {
        this.title = title;
        this.description = description;
        this.orgid = orgid;
        this.startTime = startDate.getTimeInMillis();
        this.endTime = endDate.getTimeInMillis();
        this.location = location;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.attendees = new ArrayList<>();
    }

    /**
     * Constructor less the start and end dates. The dates are automatically initialized to the
     * current date and time.
     */
    public Event(String title, String description, String orgid, String location, String category,
                 int maxCapacity) {
        this(title, description, orgid,
                new GregorianCalendar(), new GregorianCalendar(), location, category, maxCapacity);
    }

    /**
     * Constructor less dates, location and max capacity.
     */
    public Event(String title, String description, String orgid, String category) {
        this(title, description, orgid, "Nowhere", category, 100);
    }

    /**
     * No-arg constructor, fills in fields with 'blank' information.
     */
    public Event() {
        this("Untitled", "No description.", null, "None");
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start date of this event. Months are 0-indexed: e.g. Month 0 is January.
     */
    public void putStartTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        startTime = c.getTimeInMillis();
    }

    /**
     * Gets the start date of this event as a Calendar object.
     */
    public Calendar startTimeAsCalendar() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(startTime);
        return c;
    }

    /**
     * Sets the end date of this event. Months are 0-indexed: e.g. Month 0 is January.
     */
    public void putEndTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        endTime = c.getTimeInMillis();
    }

    /**
     * Gets the end date of this event as a Calendar object.
     */
    public Calendar endTimeAsCalendar() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(endTime);
        return c;
    }

    /**
     * Adds attendee by UID to the Event's attendee list.
     */
    public void addAttendee(String uid) {
        attendees.add(uid);
    }

    /**
     * Removes an attendee by UID from the Event's attendee list.
     */
    public void removeAttendee(String uid) {
        attendees.remove(uid);
    }


    /**
     * Returns a list of the Event's attendees.
     */
    public ArrayList<String> getAttendees() {
        ArrayList<String> copy = new ArrayList<>(attendees);
        return copy;
    }

    /**
     * Returns a key-value mapping of the Event object.
     * Pass this map to a Firebase setValue() method if you need to store the Event into the DB.
     */
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("orgid", orgid);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("location", location);
        map.put("category", category);
        map.put("maxCapacity", maxCapacity);
        map.put("attendees", new ArrayList<>(attendees));
        return map;
    }

    /**
     * Returns a JSON stringified version of the Event object.
     */
    @Override
    public String toString() {
        JSONObject json = new JSONObject(getMap());
        return json.toString();
    }

    // Plain self-explanatory getters and setters below.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
