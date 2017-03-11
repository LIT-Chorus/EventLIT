package com.cse110.eventlit.db;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

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


    private String eventid;

    /**
     * Start and end dates of the event, as milliseconds since the Unix epoch (Jan 1, 1970).
     */
    private long startDate;
    private long endDate;

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


    public static Comparator<Event> eventComparatorDate = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            if (o2.startDate > o1.startDate) {
                return -1;
            }
            else if (o2.startDate < o1.startDate) {
                return 1;
            }
            else {
                return 0;
            }
        }
    };


    /**
     * Full constructor containing all event information.
     */
    public Event(String title, String description, String orgid, String eventid, Calendar startDate,
                 Calendar endDate, String location, String category, int maxCapacity) {
        this.title = title;
        this.description = description;
        this.orgid = orgid;
        this.eventid = eventid;
        this.startDate = startDate.getTimeInMillis();
        this.endDate = endDate.getTimeInMillis();
        this.location = location;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.attendees = new ArrayList<>();
    }

    public Event(String title, String description, String orgid, String eventid, long startDate,
                 long endDate, String location, String category, int maxCapacity) {
        this.title = title;
        this.description = description;
        this.orgid = orgid;
        this.eventid = eventid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.attendees = new ArrayList<>();
    }

    /**
     * Constructor less the start and end dates. The dates are automatically initialized to the
     * current date and time.
     */
    public Event(String title, String description, String orgid, String eventid, String location, String category,
                 int maxCapacity) {
        this(title, description, orgid, eventid,
                new GregorianCalendar(), new GregorianCalendar(), location, category, maxCapacity);
    }

    /**
     * Constructor less dates, location and max capacity.
     */
    public Event(String title, String description, String orgid, String eventid, String category) {
        this(title, description, orgid, eventid, "Nowhere", category, 100);
    }

    /**
     * No-arg constructor, fills in fields with 'blank' information.
     */
    public Event() {
        this("Untitled", "No description.", "No org", null, "None");
    }

    public long getEndDate() {
        return endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of this event. Months are 0-indexed: e.g. Month 0 is January.
     */
    public void putStartTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        startDate = c.getTimeInMillis();
    }

    /**
     * Gets the start date of this event as a Calendar object.
     */
    public Calendar startTimeAsCalendar() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(startDate);
        return c;
    }

    /**
     * Formats the start time given the format string following
     * <a href="http://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat conventions</a>:
     * e.g. A format of "yyyy-MM-dd" will return "2017-03-01" for March 3rd, 2017.
     */
    public String formattedStartTime(String fmt) {
        return new SimpleDateFormat(fmt).format(startTimeAsCalendar().getTime());
    }

    public static long getEpochTime(String date, String fmt) {
        try {
            return new SimpleDateFormat(fmt).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Sets the end date of this event. Months are 0-indexed: e.g. Month 0 is January.
     */
    public void putEndTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        endDate = c.getTimeInMillis();
    }

    /**
     * Gets the end date of this event as a Calendar object.
     */
    public Calendar endTimeAsCalendar() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(endDate);
        return c;
    }

    /**
     * Formats the end time given the format string following
     * <a href="http://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat conventions</a>:
     * e.g. A format of "yyyy-MM-dd" will return "2017-03-01" for March 3rd, 2017.
     */
    public String formattedEndTime(String fmt) {
        return new SimpleDateFormat(fmt).format(endTimeAsCalendar().getTime());
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
     * Returns a JSON stringified version of the Event object.
     */
    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("orgid", orgid);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("location", location);
        map.put("category", category);
        map.put("maxCapacity", maxCapacity);
        map.put("attendees", new ArrayList<>(attendees));
        JSONObject json = new JSONObject(map);
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

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }


    @Override
    public boolean equals(Object event) {
        return eventid.equals(((Event)event).getEventid());
    }
}
