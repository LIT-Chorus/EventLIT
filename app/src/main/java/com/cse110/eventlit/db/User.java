package com.cse110.eventlit.db;

import android.support.annotation.NonNull;

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

    public List<RSVP> eventsFollowing;
    public List<Integer> orgsFollowing;
    public List<Integer> orgsManaging;

    public User() {
        eventsFollowing = new ArrayList<>();
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
                @NonNull List<Integer> orgsFollowing,
                @NonNull List<RSVP> eventsFollowing,
                @NonNull List<Integer> orgsManaging)
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
    public void addOrgFollowing(int orgid) {
        orgsFollowing.add(orgid);
    }

    /**
     * Add an org to the user's managing list.
     * @param orgid Organization ID
     */
    public void addOrgManaging(int orgid) {
        orgsManaging.add(orgid);
    }

    /**
     * Add an event to the user's event following list.
     * @param orgid Organization ID
     * @param eventid Event ID
     * @param status status as defined in Event.RSVPStatus
     */
    public void addEventFollowing(int orgid, String eventid, RSVP.Status status) {
        eventsFollowing.add(new RSVP(orgid, eventid, status));
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
        this.eventsFollowing = new ArrayList<>(newData.eventsFollowing);
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
}