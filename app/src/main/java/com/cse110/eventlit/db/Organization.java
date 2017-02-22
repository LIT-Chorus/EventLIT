package com.cse110.eventlit.db;

/**
 * Created by sandeep on 2/21/17.
 * An immutable object tracking an organization.
 */

public class Organization {
    private String id;
    private String name;

    public Organization(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getOrgId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
