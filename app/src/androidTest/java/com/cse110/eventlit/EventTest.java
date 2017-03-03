package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.utils.EventUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventTest {
    Event event;
    private JSONObject expectedJSON;
    private Map<String, Object> expectedMap;
    Calendar now;

    @Before
    public void setUp() throws Exception {
        now = new GregorianCalendar();
        event = new Event("Test Event", "Here it is.", "abcdefg",
                now, now, "Nowhere", "Other", 100);
        expectedMap = new HashMap<>();
        expectedJSON = new JSONObject();

        expectedMap.put("title", "Test Event");
        expectedMap.put("description", "Here it is.");
        expectedMap.put("orgid", "abcdefg");
        expectedMap.put("startTime", event.getStartDate());
        expectedMap.put("endTime", event.getEndDate());
        expectedMap.put("location", "Nowhere");
        expectedMap.put("category", "Other");
        expectedMap.put("maxCapacity", 100);

        for (String k : expectedMap.keySet()) {
            expectedJSON.put(k, expectedMap.get(k));
        }

        expectedMap.put("attendees", new ArrayList<String>());
        expectedJSON.put("attendees", new JSONArray());
    }

    @Test
    public void testAttendees() throws Exception {
        String uid = "0xgarysb@n@n@s";
        event.addAttendee(uid);
        assertTrue("Couldn't find added attendee", event.getAttendees().indexOf(uid) != -1);
        event.removeAttendee(uid);
        assertTrue("Was able to find removed attendee", event.getAttendees().indexOf(uid) == -1);
    }

    @Test
    public void testToString() throws Exception {
        // Parse toString output for JSON
        JSONObject testJSON = new JSONObject(event.toString());

        Iterator<String> it = expectedJSON.keys();
        while (it.hasNext()) {
            String key = it.next();
            Object testVal = testJSON.get(key);
            Object expectVal = expectedJSON.get(key);
            assertEquals("Could not find " + key + " in toString",testVal, expectVal);
        }
    }

    @Test
    public void testFormat() throws Exception {
        String fmt = "yyyy-MM-dd";
        Log.d("testFormat", event.formattedStartTime(fmt));
    }

    @Test
    public void testCreatedEvent() throws  Exception {
        Event createdEvent = new Event("Test Create Event", "Here it is.", "test-org-1",
                now, now, "Nowhere", "Other", 100);
        final CountDownLatch latch = new CountDownLatch(1);
        OnCompleteListener onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                assertEquals(true, task.isSuccessful());
                latch.countDown();
            }
        };
        EventUtils.createEvent(createdEvent, onCompleteListener);
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}