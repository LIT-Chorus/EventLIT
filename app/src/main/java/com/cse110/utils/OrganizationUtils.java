package com.cse110.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.OrganizationsAdapter;
import com.cse110.eventlit.db.Organization;
import com.google.android.gms.tasks.OnCompleteListener;
import com.cse110.eventlit.db.OrganizationList;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    public static void addOrgFromId(final String orgid, final List<Organization> orgs,
                                    final CountDownLatch signal) {

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

    public static void getOrgFromIdAsync(final String orgid, final OnCompleteListener<Organization> onComplete) {

        final WrappedTask<User> getOrgTask = new WrappedTask<>();

        final DatabaseReference orgDB = orgsDB.child(orgid);
        final Organization[] org = new Organization[1];

        orgDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String org_name = dataSnapshot.getValue().toString();
                    org[0] = new Organization(orgid, org_name);
                }
                else {
                    org[0] = new Organization(orgid, "Unknown");
                }

                WrappedTask<Organization> resultTask = new WrappedTask<Organization>();
                resultTask.wrapResult(org[0]);
                onComplete.onComplete(resultTask.unwrap());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
