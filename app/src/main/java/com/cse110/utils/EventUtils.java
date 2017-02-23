package com.cse110.utils;

import android.util.Log;

import com.cse110.eventlit.CardFragment;
import com.cse110.eventlit.db.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by sandeep on 2/23/17.
 */

public class EventUtils {

    private static DatabaseReference eventsDB = DatabaseUtils.getEventsDB();
    /**
     * Fetch a list of events and save it off in an ArrayAdapter. Notify the ArrayAdapter of the
     * change.
     */
    public static void getEventsByOrgId(final CardFragment.MyAdapter adapter,
                                        final ArrayList<Event> adapterArray,
                                        final String orgId){
        final DatabaseReference events = eventsDB.child(orgId);
        ValueEventListener eventListener = new ValueEventListener() {
            // Get a snapshot of events db
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    if (eventSnapshot.hasChild("orgid")){
                        String eventOrgId = eventSnapshot.child("orgid").getValue().toString();
                        if (eventOrgId.equals(orgId)){

                            // Get all fields in an event object
                            String title = eventSnapshot.child("title").getValue()
                                    .toString();
                            String category = eventSnapshot.child("category").getValue().toString();
                            String description = eventSnapshot.child("description").getValue()
                                    .toString();

                            Calendar startDate = new GregorianCalendar();
                            String startMilli = eventSnapshot.child("startDate").getValue()
                                    .toString();
                            startDate.setTimeInMillis(Long.parseLong(startMilli));

                            Calendar endDate = new GregorianCalendar();
                            String endMilli = eventSnapshot.child("endDate").getValue().toString();
                            endDate.setTimeInMillis(Long.parseLong(endMilli));

                            String location = eventSnapshot.child("location").getValue().toString();
                            int maxCapacity = Integer
                                    .parseInt(eventSnapshot.child("maxCapacity")
                                            .getValue().toString());

                            // Create an event object and add it to the adapter
                            Event event = new Event(title, description, orgId, startDate,
                                    endDate,location, category, maxCapacity);
                            adapterArray.add(event);
                            adapter.notifyItemChanged(adapter.getItemCount() - 1);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DatabaseUtils", "Could not retrieve events");
            }
        };
        events.addValueEventListener(eventListener);
    }

    /**
     * Fetch a list of all events regardless of organization.
     *
     * @param adapter - the adapter to notify once the ArrayList has been populated
     * @param eventList - an ArrayList of events to be populated
     */
    public static void getAllEvents(final CardFragment.MyAdapter adapter,
                                    final ArrayList<Event> eventList) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Enumerate through all the organizations
                for (DataSnapshot org : dataSnapshot.getChildren()){
                    // Makes call to other method to get events for the org
                    getEventsByOrgId(adapter, eventList, org.getKey());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DatabaseUtils", "Could not retrieve events");
            }
        };
        eventsDB.addValueEventListener(eventListener);
    }
}
