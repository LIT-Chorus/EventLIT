package com.cse110.eventlit;

import java.util.List;

/**
 * Created by vansh on 2/14/17.
 */

class UserPrivateData {
    public List<String> organizer_access;
    public List<EventFollowing> events_following;

    public UserPrivateData() {}

    public UserPrivateData(List<String> organizer_access, List<EventFollowing> events_following) {
        this.organizer_access = organizer_access;
        this.events_following = events_following;
    }
}