package com.cse110.eventlit;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;

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
    private static DatabaseReference fbDBRef = FirebaseDatabase.getInstance().getReference();

    /**
     * Return an ArrayList of all student organizations on UCSD campus.
     * @return studentOrgs - an ArrayList of student organizations as strings
     */
    static ArrayList<String> getAllStudentOrganizations(final ArrayAdapter<String> adapter){
        final ArrayList<String> studentOrgs = new ArrayList<>();
        final DatabaseReference organizations = fbDBRef.child("organizations");
        ValueEventListener postListener = new ValueEventListener() {
            // Get a snapshot of the database organizations document
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Remove all existing orgs
                adapter.clear();

                // Reindex and add new organziations
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    String organization = shot.getValue().toString();
                    adapter.add(organization);
                }
                for (int i = 0; i < adapter.getCount(); i++){
                    Log.w("Org " + i + ":", adapter.getItem(i));
                }
                adapter.notifyDataSetChanged();
            }

            // Elegantly handle the error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrganizerUtils", "Could not retrieve student organizations");
            }
        };
        organizations.addListenerForSingleValueEvent(postListener);
        return studentOrgs;
    }
}
