package com.cse110.eventlit.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
    public String title;

    /**
     * Description of the event.
     */
    public String description;

    /**
     * String ID of the organization hosting the event.
     */
    public String orgid;

    /**
     * Start and end dates of the event, as milliseconds since the Unix epoch (Jan 1, 1970).
     * Use getStartDate() or getEndDate() for better representations of this data.
     */
    public long startDate;
    public long endDate;

    /**
     * Named location of the event.
     */
    public String location;

    /**
     * Category of the event.
     */
    public String category;

    /**
     * Maximum number of persons that the event can hold.
     */
    public int maxCapacity;

    /**
     * List of UIDs of attendees to the event.
     */
    public List<String> attendees;

    /**
     * Full constructor containing all event information.
     * @param title
     * @param description
     * @param orgid
     * @param startDate
     * @param endDate
     * @param location
     * @param category
     * @param maxCapacity
     */
    public Event(String title, String description, String orgid, Calendar startDate,
                 Calendar endDate, String location, String category, int maxCapacity) {
        this.title = title;
        this.description = description;
        this.orgid = orgid;
        this.startDate = startDate.getTimeInMillis();
        this.endDate = endDate.getTimeInMillis();
        this.location = location;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.attendees = new ArrayList<>();
    }

    /**
     * Constructor less the start and end dates. The dates are automatically initialized to the
     * current date and time.
     * @param title
     * @param description
     * @param orgid
     * @param location
     * @param category
     * @param maxCapacity
     */
    public Event(String title, String description, String orgid, String location, String category,
                 int maxCapacity) {
        this(title, description, orgid,
                new GregorianCalendar(), new GregorianCalendar(), location, category, maxCapacity);
    }

    /**
     * Constructor less dates, location and max capacity.
     * @param title
     * @param description
     * @param orgid
     * @param category
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

    /**
     * Sets the start date of this event. Months are 0-indexed: e.g. Month 0 is January.
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     */
    public void setStartDate(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        startDate = c.getTimeInMillis();
    }

    /**
     * Gets the start date of this event.
     * @return
     */
    public Calendar getStartDate() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(startDate);
        return c;
    }

    /**
     * Sets the end date of this event. Months are 0-indexed: e.g. Month 0 is January.
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     */
    public void setEndDate(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        endDate = c.getTimeInMillis();
    }

    /**
     * Gets the end date of this event.
     * @return
     */
    public Calendar getEndDate() {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(endDate);
        return c;
    }
}
