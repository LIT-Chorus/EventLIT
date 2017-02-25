package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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

    @Before
    public void setUp() throws Exception {
        Calendar now = new GregorianCalendar();
        event = new Event("Test Event", "Here it is.", "abcdefg",
                now, now, "Nowhere", "Other", 100);
        expectedMap = new HashMap<>();
        expectedJSON = new JSONObject();

        expectedMap.put("title", "Test Event");
        expectedMap.put("description", "Here it is.");
        expectedMap.put("orgid", "abcdefg");
        expectedMap.put("startTime", event.getStartTime());
        expectedMap.put("endTime", event.getEndTime());
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
    public void testGetMap() throws Exception {
        Map<String, Object> testMap = event.getMap();
        for (String key : expectedMap.keySet()) {
            Object testVal = testMap.get(key);
            Object expectVal = expectedMap.get(key);
            assertEquals("Could not find " + key + " in map", testVal, expectVal);
        }
    }
}