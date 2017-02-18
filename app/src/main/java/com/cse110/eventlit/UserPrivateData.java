package com.cse110.eventlit;

import com.cse110.eventlit.db.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vansh on 2/14/17.
 */

class UserPrivateData {
    public List<String> organizer_access;
    public Map<String, Event.RSVPStatus> events_following = new HashMap<>();
    public ArrayList<String> orgs_managing = new ArrayList<String>();

    public UserPrivateData() {}

    public UserPrivateData(List<String> organizer_access, Map<String, Event.RSVPStatus> events_following, ArrayList<String> orgs_managing) {
        this.organizer_access = organizer_access;
        this.events_following = events_following;
        this.orgs_managing = orgs_managing;
        this.events_following = events_following;

    }
}