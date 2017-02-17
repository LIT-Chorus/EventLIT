package com.cse110.eventlit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vansh on 2/13/17.
 */

public class User {
    public List<String> orgs_following;

    public String firstName;
    public String lastName;
    public String email;

    public User() {
        orgs_following = new ArrayList<>();
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

    public User(List<String> organizer_access,

                String firstName,
                String lastName,
                String email)
    {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


}