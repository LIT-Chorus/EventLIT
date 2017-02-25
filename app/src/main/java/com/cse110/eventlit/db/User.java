package com.cse110.eventlit.db;

import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding User information.
 */
public class User {
    /**
     * The user's first name.
     */
    private String firstName;
    private String lastName;
    private String email;

    /**
     * List of orgids of the student orgs that the user is following.
     */
    private List<String> orgsFollowing;

    /**
     * Map of org ids and event ids to the user's RSVP status to each event.
     */
    private ArrayList<UserEventRSVP> eventsFollowing;

    /**
     * List of orgids of the student orgs that the user manages.
     */
    private List<String> orgsManaging;

    public User() {
        orgsFollowing = new ArrayList<>();
        eventsFollowing = new ArrayList<>();
        orgsManaging = new ArrayList<>();
    }

    /**
     * The metadata constructor that should be used for new users.
     */
    public User(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Constructor containing more information about the user, for instantiating User objects
     * for existing users.
     */
    public User(String firstName, String lastName, String email,
                List<String> orgsFollowing,
                ArrayList<UserEventRSVP> eventsFollowing,
                List<String> orgsManaging) {
        this(firstName, lastName, email);
        this.orgsFollowing = orgsFollowing;
        this.eventsFollowing = eventsFollowing;
        this.orgsManaging = orgsManaging;
    }

    /**
     * Loads data from the key-value map into this User.
     */
    public User(Map<String, Object> data) {
        this();
        set(data);
    }

    /**
     * Sets corresponding data fields with the map, overwriting whatever data existed in those
     * fields. Useful for loading from Firebase.
     */
    public void set(Map<String, Object> data) {
        for (String key : data.keySet()) {
            Object val = data.get(key);
            if (key.equals("firstName")) {
                firstName = (String) val;
            } else if (key.equals("lastName")) {
                lastName = (String) val;
            } else if (key.equals("email")) {
                email = (String) val;
            } else if (key.equals("orgsFollowing")) {
                orgsFollowing = new ArrayList<>((ArrayList<String>) val);
            } else if (key.equals("eventsFollowing")) {
                // Firebase does some funky stuff with the eventsFollowing list -- we have to
                // parse through a list of maps for the RSVP data
                eventsFollowing = new ArrayList<>();
                ArrayList<Map<String, Object>> following = (ArrayList<Map<String, Object>>) val;
                for (Map<String, Object> m : following) {
                    eventsFollowing.add(
                            new UserEventRSVP(
                                    "" + m.get("orgid"),
                                    "" + m.get("eventid"),
                                    Event.RSVPStatus.valueOf((String) m.get("rsvpStatus"))));
                }
            } else if (key.equals("orgsManaging")) {
                orgsManaging = new ArrayList<>((ArrayList<String>) val);
            }
        }
    }

    /**
     * Add an org to the user's following list.
     * @param orgid Organization ID
     */
    public void addOrgFollowing(String orgid) {
        orgsFollowing.add(orgid);
    }

    /**
     * Add an org to the user's managing list.
     * @param orgid Organization ID
     */
    public void addOrgManaging(String orgid) {
        orgsManaging.add(orgid);
    }

    /**
     * Add an event to the user's event following list.
     * @param orgid Organization ID
     * @param eventid Event ID
     * @param status status as defined in Event.RSVPStatus
     */
    public void addEventFollowing(String orgid, String eventid, Event.RSVPStatus status) {
        eventsFollowing.add(new UserEventRSVP(orgid, eventid, status));
    }

    /**
     * @return The public data (first name, last name, email) as a Map.
     */
    public Map<String, Object> getPublicDataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", email);
        return map;
    }

    /**
     * @return The private data (orgs following, events following, orgs managing) as a Map.
     */
    public Map<String, Object> getPrivateDataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("orgsFollowing", new ArrayList<>(orgsFollowing));
        map.put("eventsFollowing", new ArrayList<>(eventsFollowing));
        map.put("orgsManaging", new ArrayList<>(orgsManaging));
        return map;
    }

    /**
     * @return The JSON stringified form of this User's information,
     * including all data -- public or private.
     */
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(getPublicDataMap());
        map.putAll(getPrivateDataMap());
        return new JSONObject(map).toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getOrgsFollowing() {
        return new ArrayList<>(orgsFollowing);
    }

    public ArrayList<UserEventRSVP> getEventsFollowing() {
        return new ArrayList<>(eventsFollowing);
    }

    public List<String> getOrgsManaging() {
        return new ArrayList<>(orgsManaging);
    }
}