package com.cse110.utils;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.cse110.eventlit.OrganizationsAdapter;
import com.cse110.eventlit.db.Organization;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sandeep on 2/23/17.
 */

public class OrganizationUtils {

    private static DatabaseReference orgsDB = DatabaseUtils.getOrganizationsDB();

    private static ArrayList<Organization> orgsList = new ArrayList<>();
    private static OrganizationsListener orgsListener = new OrganizationsListener(orgsList);

    private static class OrganizationsListener implements ValueEventListener {
        private ArrayList<Organization> myList;

        public OrganizationsListener(ArrayList<Organization> myList) {
            this.myList = myList;
            orgsDB.addValueEventListener(this);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            GenericTypeIndicator<ArrayList<String>> listType = new GenericTypeIndicator<ArrayList<String>>() {};
            ArrayList<String> dbList = dataSnapshot.getValue(listType);
            ArrayList<Organization> orgs = new ArrayList<>();

            for (int i = 0; i < dbList.size(); i++) {
                orgs.add(new Organization(String.valueOf(i), dbList.get(i)));
            }

            // Replace list with new DB contents.
            myList.clear();
            myList.addAll(orgs);

            Log.d("OrganizationUtils", "Updated Organizations list");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("UserDataListener", "Something went wrong while listening to " + myList + "!");
        }
    }

    public static Task<ArrayList<Organization>> loadOrgs() {
        return getAllStudentOrganizations(null, null);
    }

    /**
     * Fetch a list of student organizations at UCSD, save it off in an ArrayAdapter,
     * Notify the ArrayAdapter of the change.
     */
    public static Task<ArrayList<Organization>> getAllStudentOrganizations(final OrganizationsAdapter adapter,
                                                                           final ArrayList<Organization> oldOrgs) {
        final WrappedTask<ArrayList<Organization>> wrappedOrgsTask = new WrappedTask<>();

        // Temp variable holding new organization list
        final ArrayList<Organization> newOrgs = new ArrayList<>();

        // If orgs loaded already, update the adapter and lists
        if (!orgsList.isEmpty()) {
            newOrgs.addAll(orgsList); // Add in all existing orgs

            // Fill in adapter and list, if passed in
            if (adapter != null) adapter.setmOrganizations(newOrgs);
            if (oldOrgs != null) {
                oldOrgs.clear();
                oldOrgs.addAll(newOrgs);
            }

            // Set result and notify listeners of completion
            wrappedOrgsTask.wrapResult(newOrgs);
            if (adapter != null) adapter.notifyDataSetChanged();

        } else {
            // If no orgs loaded yet, make a DB call
            orgsDB.addListenerForSingleValueEvent(new ValueEventListener() {
                // Get a snapshot of the database organizations document
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Load in List
                    GenericTypeIndicator<ArrayList<String>> listType = new GenericTypeIndicator<ArrayList<String>>(){};
                    ArrayList<String> dbList = dataSnapshot.getValue(listType);

                    // Clear old orgs before updating
                    if (oldOrgs != null) oldOrgs.clear();

                    // Add the orgs to the lists and notify the adapter.
                    for (int i = 0; i < dbList.size(); i++) {
                        Organization org = new Organization(String.valueOf(i), dbList.get(i));
                        newOrgs.add(new Organization(String.valueOf(i), dbList.get(i)));

                        if (oldOrgs != null) oldOrgs.add(org);
                        if (adapter != null) adapter.notifyItemChanged(adapter.getItemCount() - 1);
                    }

                    // Wrap the result and notify listeners of completion
                    wrappedOrgsTask.wrapResult(newOrgs);
                    if (adapter != null) adapter.notifyDataSetChanged();
                }

                // Elegantly handle the error
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("OrganizerUtils", "Could not retrieve student organizations");
                }
            });
        }

        return wrappedOrgsTask.unwrap();
    }

    /**
     * Fetch a list of student organizations at UCSD, save it off in an ArrayAdapter,
     * Notify the ArrayAdapter of the change.
     */
    public static ArrayList<Organization> getAllStudentOrganizations() {
        final ArrayList<Organization> orgs = new ArrayList<>();

        // Just fill adapter with current list if not empty
        if (!orgsList.isEmpty()) {
            orgs.addAll(orgsList);
        } else {
            return orgs;
        }

        return orgs;
    }

    /**
     * Get an Organization object from its
     *
     * @param orgid
     */
    public static Organization orgFromId(String orgid) {
        int index = Integer.parseInt(orgid);
        return orgsList.get(index);
    }
}
