package com.cse110.eventlit.db;

/**
 * Created by sandeep on 2/21/17.
 * An immutable object tracking an organization.
 */

public class Organization {
    // Firebase id for the organization
    private String id;

    // Name of the organization
    private String name;

    /**
     * Constructor requiring an orgId linked with Firebase DB and the name
     * @param id - orgId on the Firebase DB
     * @param name - the organization's name
     */
    public Organization(String id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Getter method for the organization id private variable
     * @return id
     */
    public String getOrgId(){
        return id;
    }

    /**
     * Getter method for the name private variable
     * @return name
     */
    public String getName(){
        return name;
    }
}
