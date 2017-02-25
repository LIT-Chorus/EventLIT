package com.cse110.eventlit.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private PrivateData privateData;

    public static class PrivateData {
        /**
         * List of orgids of the student orgs that the user is following.
         */
        private List<Integer> orgsFollowing;

        /**
         * Map of org ids and event ids to the user's RSVP status to each event.
         */
        private ArrayList<UserEventRSVP> eventsFollowing;

        /**
         * List of orgids of the student orgs that the user manages.
         */
        private List<Integer> orgsManaging;

        public PrivateData() {
            orgsFollowing = new ArrayList<>();
            eventsFollowing = new ArrayList<>();
            orgsManaging = new ArrayList<>();
        }

        public PrivateData(PrivateData data) {
            this(data.getOrgsFollowing(), data.getEventsFollowing(), data.getOrgsManaging());
        }

        public PrivateData(List<Integer> orgsFollowing, ArrayList<UserEventRSVP> eventsFollowing,
                           List<Integer> orgsManaging) {
            this.orgsFollowing = orgsFollowing;
            this.eventsFollowing = eventsFollowing;
            this.orgsManaging = orgsManaging;
        }

        public List<Integer> getOrgsFollowing() {
            return new ArrayList<>(orgsFollowing);
        }

        public ArrayList<UserEventRSVP> getEventsFollowing() {
            return new ArrayList<>(eventsFollowing);
        }

        public List<Integer> getOrgsManaging() {
            return new ArrayList<>(orgsManaging);
        }

        public void setOrgsFollowing(List<Integer> orgsFollowing) {
            this.orgsFollowing = new ArrayList<>(orgsFollowing);
        }

        public void setEventsFollowing(ArrayList<UserEventRSVP> eventsFollowing) {
            this.eventsFollowing = new ArrayList<>(eventsFollowing);
        }

        public void setOrgsManaging(List<Integer> orgsManaging) {
            this.orgsManaging = new ArrayList<>(orgsManaging);
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            JSONArray eventsFollowingJSON = new JSONArray();
            try {
                json.put("orgsFollowing", orgsFollowing);
                for (UserEventRSVP event : eventsFollowing) {
                    eventsFollowingJSON.put(event.toJSON());
                }
                json.put("eventsFollowing", eventsFollowingJSON);
                json.put("orgsManaging", orgsManaging);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        public String toString() {
            return toJSON().toString();
        }
    }

    public PrivateData extractPrivateData() {
        return privateData;
    }

    public void applyPrivateData(PrivateData data) {
        privateData = new PrivateData(data);
    }

    public User() {
        privateData = new PrivateData();
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
                List<Integer> orgsFollowing,
                ArrayList<UserEventRSVP> eventsFollowing,
                List<Integer> orgsManaging) {
        this(firstName, lastName, email);
        privateData = new PrivateData(orgsFollowing, eventsFollowing, orgsManaging);
    }

    /**
     * Add an org to the user's following list.
     * @param orgid Organization ID
     */
    public void addOrgFollowing(int orgid) {
        privateData.orgsFollowing.add(orgid);
    }

    /**
     * Add an org to the user's managing list.
     * @param orgid Organization ID
     */
    public void addOrgManaging(int orgid) {
        privateData.orgsManaging.add(orgid);
    }

    /**
     * Add an event to the user's event following list.
     * @param orgid Organization ID
     * @param eventid Event ID
     * @param status status as defined in Event.RSVPStatus
     */
    public void addEventFollowing(int orgid, String eventid, Event.RSVPStatus status) {
        privateData.eventsFollowing.add(new UserEventRSVP(orgid, eventid, status));
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * @return The JSON stringified form of this User's public information.
     */
    public String toString() {
        return toJSON().toString();
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
}