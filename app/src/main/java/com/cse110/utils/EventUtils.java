package com.cse110.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cse110.eventlit.CardFragment;
import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.RSVP;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Created by sandeep on 2/23/17.
 */

public class EventUtils {

    private static DatabaseReference eventsDB = DatabaseUtils.getEventsDB();

    private static Event getEventSnapshot(DataSnapshot eventSnapshot) {
        // Get all fields in an event object
        String orgId = eventSnapshot.child("orgid").getValue().toString();

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
        Event event = new Event(title, description, orgId, startDate,
                endDate,location, category, maxCapacity);
        return event;

    }

    /**
     * Fetch a list of events and save it off in an ArrayAdapter. Notify the ArrayAdapter of the
     * change.
     */
    public static void getEventsByOrgId(final CardFragment.MyAdapter adapter,
                                        final ArrayList<Event> adapterArray,
                                        final String orgId, final int popularity, final boolean notifyComplete){
        final DatabaseReference events = eventsDB.child(orgId);
        ValueEventListener eventListener = new ValueEventListener() {
            // Get a snapshot of events db
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    if (eventSnapshot.hasChild("orgid")){
                        String eventOrgId = eventSnapshot.child("orgid").getValue().toString();
                        if (eventOrgId.equals(orgId)){
                            // Create an event object and add it to the adapter
                            Event event = getEventSnapshot(eventSnapshot);
                            if (notifyComplete) {
                                adapterArray.add(event);
                                adapter.notifyItemChanged(adapter.getItemCount() - 1);
                            }
                        }
                    }
                }
                if (notifyComplete){
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DatabaseUtils", "Could not retrieve events");
            }
        };
        events.addValueEventListener(eventListener);
    }

    /**
     *
     * @param adapter
     * @param minPopularity -
     * @param eventList
     */
    public static void filterEventsByPopularity(final CardFragment.MyAdapter adapter,
                                                final int minPopularity,
                                                final ArrayList<Event> eventList) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Enumerate through all the organizations
                for (DataSnapshot org : dataSnapshot.getChildren()){
                    // Makes call to other method to get events for the org
                    getEventsByOrgId(adapter, eventList, org.getKey(), minPopularity, true);
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
                    getEventsByOrgId(adapter, eventList, org.getKey(), 0, true);
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

    /**
     * Add an Event object to an input param eventsFollowing based off of an RSVP object.
     *
     * NOTE: only package scope. Only to be used by other util methods
     * DO NOT MAKE PUBLIC
     *
     * @param rsvp
     * @param eventsFollowing
     * @param signal
     */
    static final void addEventFromId(final RSVP rsvp, final List<Event> eventsFollowing, final CountDownLatch signal) {


        Log.w("add event, rsvp orgid", ""+rsvp.getOrgid());
        Log.w("add event, rsvp eventid", ""+rsvp.getEventid());

        //final CountDownLatch synchToken = new CountDownLatch(1);

        DatabaseReference eventReference = eventsDB.child(rsvp.getOrgid()).child(rsvp.getEventid());

        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Event event = dataSnapshot.getValue(Event.class);

                Log.d("in eventutils rsvp is ", "" + rsvp);
                Log.d("in eventutils event is ", "" + event);
                if (event != null)
                    eventsFollowing.add(dataSnapshot.getValue(Event.class));

                Log.w("countdown", "" + signal.getCount());

                signal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Create event. Sets onComplete with the event's key as the string.
     */
    public static final void createEvent(Event event, final OnCompleteListener<String> onCompleteListener) {
        String orgId = event.getOrgid();
        DatabaseReference eventRef = eventsDB.child(orgId).push();
        final String eventKey = eventRef.getKey();
        OnCompleteListener onEventCreated = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                onCompleteListener.onComplete(Tasks.forResult(eventKey));
            }
        };
        eventRef.setValue(event).addOnCompleteListener(onEventCreated);
    }

    /**
     * Delete event by passing in the eventId and orgId
     */
    public static final void deleteEvent(String eventId,
                                         String orgId,
                                         OnCompleteListener<Void> onDelete) {
        eventsDB.child(orgId).child(eventId).removeValue().addOnCompleteListener(onDelete);
    }
}
