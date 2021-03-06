package com.cse110.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.CardFragment;
import com.cse110.eventlit.db.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                                        final ArrayList<Event> copy,
                                        final Set<String> eventIdsAdded,
                                        final String orgId,
                                        final ArrayList<String> categories){
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
                            Event event = eventSnapshot.getValue(Event.class);


                            Date eventDate = new Date(event.getEndDate());
                            Date today = new Date();

                            Log.d("EventUtils end date", "" + event.getEndDate());

                            if (eventDate.before(today)) {
                                Log.w("Deleted", event.toString());
                                EventUtils.deleteEvent(event.getEventid(), event.getOrgid(), null);
                            }

                            if (categories != null && categories.size() > 0) {
                                String category = event.getCategory();
                                if (categories.contains(category)) {
                                    if (!eventIdsAdded.contains(event.getEventid())) {

                                        eventIdsAdded.add(event.getEventid());

                                        adapterArray.add(event);
                                        copy.add(event);

                                        Log.d("adding event", "" + copy.size());

                                        adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else {
                                    if (!eventIdsAdded.contains(event.getEventid())) {

                                        eventIdsAdded.add(event.getEventid());

                                        adapterArray.add(event);
                                        copy.add(event);

                                        adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                        adapter.notifyDataSetChanged();
                                    }
                            }
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

    public static Task<ArrayList<Event>> getEventsByOrgIdCont(final String orgId) {
        final DatabaseReference events = eventsDB.child(orgId);
        final WrappedTask<ArrayList<Event>> wrappedTask = new WrappedTask<>();
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Event>> eventMapInd = new GenericTypeIndicator<Map<String, Event>>() {};
                Map<String, Event> eventMap = dataSnapshot.getValue(eventMapInd);
                if (eventMap == null) {
                    wrappedTask.wrapResult(new ArrayList<Event>());
                }
                else {
                    ArrayList<Event> eventArrayList = new ArrayList<Event>(eventMap.values());
                    wrappedTask.wrapResult(eventArrayList);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return wrappedTask.unwrap();
    }


    /**
     * Fetch a list of all events regardless of organization.
     */
    public static Task<ArrayList<Event>> getAllEvents() {
        final WrappedTask<ArrayList<Event>> wrappedTask = new WrappedTask<>();
        final ArrayList<Event> allEventsArray = new ArrayList<Event>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Enumerate through all the organizations
                    // Makes call to other method to get events for the org
                    GenericTypeIndicator<Map<String, HashMap<String, Event>>> typeIndicator = new GenericTypeIndicator<Map<String, HashMap<String, Event>>>() {};
                    Map<String, HashMap<String, Event>> stringEventMap = dataSnapshot.getValue(typeIndicator);

                    for (HashMap<String, Event> events: stringEventMap.values()) {
                        for (Event event: events.values()) {
                            allEventsArray.add(event);
                        }
                    }

                    wrappedTask.wrapResult(allEventsArray);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DatabaseUtils", "Could not retrieve events");
            }
        };
        eventsDB.addValueEventListener(eventListener);
        return wrappedTask.unwrap();
    }


    /**
     * Create event. Sets onComplete with the event's key as the string.
     */
    public static final void createEvent(Event event, final OnCompleteListener<String> onCompleteListener) {
        String orgId = event.getOrgid();
        DatabaseReference eventRef = eventsDB.child(orgId).push();
        final String eventKey = eventRef.getKey();
        event.setEventid(eventKey);
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
        if (orgId != null && eventId != null) {
            eventsDB.child(orgId).child(eventId).removeValue().addOnCompleteListener(onDelete);
        }
    }

    /**
     * Update all fields of the event
     * @param event
     */
    public static final void updateEvent(final Event event,
                                         final OnCompleteListener<String> onCompleteListener) {
        String orgId = event.getOrgid();
        final String eventId = event.getEventid();
        OnCompleteListener onEventUpdated = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                onCompleteListener.onComplete(Tasks.forResult(eventId));
            }
        };
        eventsDB.child(orgId).child(eventId).setValue(event).addOnCompleteListener(onEventUpdated);
    }

    public static void modAttendees(String orgId, String eventId, final int modBy) {

        final DatabaseReference eventRef = eventsDB.child(orgId).child(eventId);

        final WrappedTask<Event> wrappedTask = new WrappedTask<>();

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);

                wrappedTask.wrapResult(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Task<Event> t_attendance = wrappedTask.unwrap();

        t_attendance.addOnCompleteListener(new OnCompleteListener<Event>() {
            @Override
            public void onComplete(@NonNull Task<Event> task) {
                Event event = t_attendance.getResult();

                event.setAttendees(event.getAttendees() + modBy);

                eventRef.setValue(event);

            }
        });

    }
}
