package com.cse110.utils;

import android.provider.ContactsContract;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.UserEventRsvp;
import com.cse110.eventlit.db.UserPrivateData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sandeep on 2/23/17.
 */

public class UserUtils {
    // TODO create methods to modify the User database

    public static final void addOrgFromId(String orgid, final List<Organization> orgs) {

        DatabaseReference orgdb = DatabaseUtils.getOrganizationsDB();

        orgdb = orgdb.child(orgid);

        orgdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orgs.add(dataSnapshot.getValue(Organization.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static final void getOrgsManaging(String uid, final List<Organization> orgsManaging) {

        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserPrivateData data = dataSnapshot.getValue(UserPrivateData.class);

                for (String orgid: data.org_ids_managing) {
                    addOrgFromId(orgid, orgsManaging);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void getOrgsFollowing(String uid, final List<Organization> orgsFollowing) {
        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserPrivateData data = dataSnapshot.getValue(UserPrivateData.class);

                for (String orgid: data.org_ids_following) {
                    addOrgFromId(orgid, orgsFollowing);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void addEventFromIds(UserEventRsvp rsvp, final List<Event> eventsFollowing) {
        DatabaseReference e_db = DatabaseUtils.getEventsDB();

        e_db = e_db.child(rsvp.orgid).child(rsvp.eventid);

        e_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsFollowing.add(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void getEventsFollowing(String uid, final List<Event> eventsFollowing) {

        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserPrivateData data = dataSnapshot.getValue(UserPrivateData.class);

                for (UserEventRsvp rsvp: data.event_ids_following) {
                    addEventFromIds(rsvp, eventsFollowing);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
