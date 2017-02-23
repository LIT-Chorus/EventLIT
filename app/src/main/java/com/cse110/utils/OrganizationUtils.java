package com.cse110.utils;

import android.util.Log;

import com.cse110.eventlit.OrganizationsAdapter;
import com.cse110.eventlit.db.Organization;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sandeep on 2/23/17.
 */

public class OrganizationUtils {
    private static DatabaseReference orgsDB = DatabaseUtils.getOrganizationsDB();

    /**
     * Fetch a list of student organizations at UCSD, save it off in an ArrayAdapter,
     * Notify the ArrayAdapter of the change.
     *
     */
    public static void getAllStudentOrganizations(final OrganizationsAdapter adapter,
                                                  final ArrayList<Organization> orgsList){
        final DatabaseReference organizations = orgsDB.child("organizations");
        ValueEventListener postListener = new ValueEventListener() {
            // Get a snapshot of the database organizations document
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reindex and add new organziations
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    String orgKey = shot.getKey();
                    String orgName = shot.getValue().toString();
                    Organization org = new Organization(orgKey, orgName);

                    // Add the new org to the list and notify the adapter.
                    orgsList.add(org);
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
                for (int i = 0; i < orgsList.size(); i++){
                    Log.w("Organization " + i + ":",  orgsList.get(i).toString());
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
    }


}
