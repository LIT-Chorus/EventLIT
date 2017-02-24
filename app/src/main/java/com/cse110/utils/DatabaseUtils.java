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

    static DatabaseReference getDBReference() {
        return fbDBRef;
    }

    static DatabaseReference getOrganizationsDB(){
        return fbDBRef.child("organizations");
    }

    static DatabaseReference getEventsDB(){
        return fbDBRef.child("events");
    }

    static DatabaseReference getUsersDB(){
        return fbDBRef.child("users");
    }

    static DatabaseReference getUserPrivateDataDB(){
        return fbDBRef.child("userPrivateData");
    }

}
