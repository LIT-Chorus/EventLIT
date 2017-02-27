package com.cse110.eventlit.db;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sandeep on 2/21/17.
 * An immutable object tracking an organization.
 */

public class Organization {
    // Firebase id for the organization
    private int id;

    // Name of the organization
    private String name;

    /**
     * Constructor requiring an orgId linked with Firebase DB and the name
     * @param id - orgId on the Firebase DB
     * @param name - the organization's name
     */
    public Organization(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Getter method for the organization id private variable
     * @return id
     */
    public int getOrgId(){
        return id;
    }

    /**
     * Getter method for the name private variable
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the organization as a JSON string.
     * @return
     */
    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("name", name);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return json.toString();
    }
}
