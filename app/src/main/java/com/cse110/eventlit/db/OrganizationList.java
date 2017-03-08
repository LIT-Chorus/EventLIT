package com.cse110.eventlit.db;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.cse110.utils.DatabaseUtils;
import com.cse110.utils.WrappedTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by vansh on 3/8/17.
 */


public class OrganizationList {
    public static List<Organization> orgsList;
    private static boolean listenerAdded = false;

    private static ValueEventListener valueEventListener = new ValueEventListener() {
        @Override

        public void onDataChange(DataSnapshot dataSnapshot) {

            synchronized (orgsList) {
                orgsList = null;
                for (DataSnapshot dat: dataSnapshot.getChildren()) {
                    orgsList.add(dat.getValue(Organization.class));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public OrganizationList() {

        if (listenerAdded) return;
        listenerAdded = true;

        final DatabaseReference orgDB  = DatabaseUtils.getOrganizationsDB();

        orgDB.addValueEventListener(valueEventListener);
    }

    public void setValue(List<Organization> o) {
        orgsList = o;
    }

    public List<Organization> getValue() {
        synchronized (orgsList) {
            return orgsList;
        }
    }


}
