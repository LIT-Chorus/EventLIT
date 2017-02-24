package com.cse110.eventlit.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vansh on 2/14/17.
 */

public class UserPrivateData {

    public List<String> org_ids_following;


    // index 0: contains the org id of the event
    // index 1: contains the event id of the event
    public List<UserEventRsvp> event_ids_following;
    public List<String> org_ids_managing;
    private boolean notifications;


    public UserPrivateData() {
        org_ids_managing = new ArrayList<>();
        event_ids_following = new ArrayList<>();
        org_ids_following = new ArrayList<>();
        notifications = false;
    }

    public UserPrivateData(List<String> org_ids_managing, List<UserEventRsvp> event_ids_following, List<String> org_ids_following, boolean notifications) {
        this.event_ids_following = event_ids_following;
        this.org_ids_managing = org_ids_managing;
        this.org_ids_following = org_ids_following;
        this.notifications = notifications;
    }
}