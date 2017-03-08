package com.cse110.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.OrganizationsAdapter;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.OrganizationList;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sandeep on 2/23/17.
 */

public class OrganizationUtils {

    private static List<Organization> orgsList;
    private static OrganizationList orgsListListener;

    private static DatabaseReference orgsDB = DatabaseUtils.getOrganizationsDB();

    /**
     * Fetch a list of student organizations at UCSD, save it off in an ArrayAdapter,
     * Notify the ArrayAdapter of the change.
     */
    public static void getAllStudentOrganizations(final OrganizationsAdapter adapter,
                                                  final ArrayList<Organization> orgsList) {


        ValueEventListener postListener = new ValueEventListener() {
            // Get a snapshot of the database organizations document
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reindex and add new organziations
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    String orgKey = shot.getKey();
                    String orgName = shot.getValue().toString();
                    Organization org = new Organization(orgKey, orgName);

                    // Add the new org to the list and notify the adapter.
                    orgsList.add(org);
                    if (adapter != null) adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
//                for (int i = 0; i < orgsList.size(); i++){
//                    Log.w("Organization " + i + ":",  orgsList.get(i).toString());
//                }

                if (adapter != null) adapter.notifyDataSetChanged();
            }

            // Elegantly handle the error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("OrganizerUtils", "Could not retrieve student organizations");
            }
        };
        orgsDB.addListenerForSingleValueEvent(postListener);
    }

    /**
     * Add an organization to a list from it's id
     * <p>
     * NOTE: only package scope. Only to be used by other util methods
     * DO NOT MAKE PUBLIC
     *
     * @param orgid
     * @param orgs
     * @param signal
     */
    public static void addOrgFromId(final String orgid, final List<Organization> orgs, final CountDownLatch signal) {

        final DatabaseReference orgDB = orgsDB.child(orgid);


        orgDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String org_name = dataSnapshot.getValue().toString();

                orgs.add(new Organization(orgid, org_name));
                signal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Organization getOrgFromIdSynch(final String orgid, final OnCompleteListener<User> completeListener) {

        final WrappedTask<User> getOrgTask = new WrappedTask<>();

        OnCompleteListener<AuthResult> getOrgCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                }
            }
        }

        final DatabaseReference orgDB = orgsDB.child(orgid);
        final Organization[] org = new Organization[1];

        final CountDownLatch latch = new CountDownLatch(1);

        orgDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String org_name = dataSnapshot.getValue().toString();

                org[0] = new Organization(orgid, org_name);

                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latch.countDown();
            }

        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return org[0];
    }
}
