package com.cse110.eventlit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A utility class to get data related to student organizations
 */

class OrganizerUtils {
    // Get a reference to the Firebase Database
    private static DatabaseReference fbDB = FirebaseDatabase.getInstance().getReference();

    /**
     * Return an ArrayList of all student organizations on UCSD campus.
     * @return studentOrgs - an ArrayList of student organizations as strings
     */
    static ArrayList<String> getAllStudentOrganizations(){
        final ArrayList<String> studentOrgs = new ArrayList<>();
        final DatabaseReference organizations = fbDB.child("organizations");
        ValueEventListener postListener = new ValueEventListener() {
            // Get a snapshot of the database organizations document
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    String organization = shot.getValue().toString();
                    studentOrgs.add(organization);
                }
            }

            // Elegantly handle the error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrganizerUtils", "Could not retrieve student organizations");
            }
        };
        organizations.addValueEventListener(postListener);
        return studentOrgs;
    }
}
