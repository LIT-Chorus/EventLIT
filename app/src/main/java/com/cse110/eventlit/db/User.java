package com.cse110.eventlit.db;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    private HashMap<String, RSVP> eventsFollowing;
    private List<String> orgsFollowing;
    private List<String> orgsManaging;

    public User() {
        eventsFollowing = new HashMap<>();
        orgsFollowing = new ArrayList<>();
        orgsManaging = new ArrayList<>();
    }

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
                @NonNull List<String> orgsFollowing,
                @NonNull HashMap<String, RSVP> eventsFollowing,
                @NonNull List<String> orgsManaging)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        this.orgsFollowing = orgsFollowing;
        this.eventsFollowing = eventsFollowing;
        this.orgsManaging = orgsManaging;
    }

    /**
     * Copy constructor
     * @param otherUser
     */
    public User(User otherUser) {
        updateWith(otherUser);
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
     * Remove an org a user was following
     * @param orgid
     */
    public void removeOrgFollowing(String orgid) {
        orgsFollowing.remove(orgid);
    }

    /**
     * Add an event to the user's event following list.
     * @param orgid Organization ID
     * @param eventid Event ID
     * @param status status as defined in Event.RSVPStatus
     */
    public void addEventFollowing(String orgid, String eventid, RSVP.Status status) {
        eventsFollowing.put(eventid, new RSVP(orgid, eventid, status));
    }

    public void addEventFollowing(String eventid, RSVP rsvp) {
        eventsFollowing.put(eventid, rsvp);
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User otherUser = (User) other;
            return firstName.equals(otherUser.firstName) &&
                    lastName.equals(otherUser.lastName) &&
                    email.equals(otherUser.email);
        }
        return false;
    }

    /**
     * Updates this user's data to be equal to that of the one passed in.
     * @param newData
     * @return
     */
    public User updateWith(User newData) {
        this.firstName = newData.firstName;
        this.lastName = newData.lastName;
        this.email = newData.email;

        this.orgsFollowing = new ArrayList<>(newData.orgsFollowing);
        this.eventsFollowing = new HashMap<>(newData.eventsFollowing);
        this.orgsManaging = new ArrayList<>(newData.orgsManaging);

        return this;
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

    public Collection<RSVP> extractEventsFollowing() {
        return eventsFollowing.values();
    }

    public void setEventsFollowing(HashMap<String, RSVP> eventsFollowing) {
        this.eventsFollowing = new HashMap<>(eventsFollowing);
    }

    public HashMap<String, RSVP> getEventsFollowing() {
        return new HashMap<>(eventsFollowing);
    }

    public List<String> getOrgsFollowing() {
        return new ArrayList<>(orgsFollowing);
    }

    public void setOrgsFollowing(List<String> orgsFollowing) {
        this.orgsFollowing = new ArrayList<>(orgsFollowing);
    }

    public List<String> getOrgsManaging() {
        return new ArrayList<>(orgsManaging);
    }

    public void setOrgsManaging(List<String> orgsManaging) {
        this.orgsManaging = new ArrayList<>(orgsManaging);
    }
}