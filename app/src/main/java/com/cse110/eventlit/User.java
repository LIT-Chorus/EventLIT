package com.cse110.eventlit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vansh on 2/13/17.
 */

public class User {

    public List<OrgFollowing> orgs_following;
    public String firstName;
    public String lastName;
    public String email;

    public User() {
    }

    public User(String firstName,
                String lastName,
                String email)
    {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(List<String> organizer_access, List<OrgFollowing> orgs_following,

                String firstName,
                String lastName,
                String email)
    {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.orgs_following = orgs_following;
    }


}