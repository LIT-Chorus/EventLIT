package com.cse110.utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * References to the Database are here for convenience. May be statically called from
 * other util classes for consistency.
 * Created by: Sandeep
 */

public class DatabaseUtils {
    // Get a reference to the Firebase Database
    private static DatabaseReference fbDBRef = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getDBReference() {
        return fbDBRef;
    }

    public static DatabaseReference getOrganizationsDB(){
        return fbDBRef.child("organizations");
    }

    public static DatabaseReference getEventsDB(){
        return fbDBRef.child("events");
    }

    public static DatabaseReference getUsersDB(){
        return fbDBRef.child("users");
    }

    public static DatabaseReference getUserPrivateDataDB(){
        return fbDBRef.child("userPrivateData");
    }

}
