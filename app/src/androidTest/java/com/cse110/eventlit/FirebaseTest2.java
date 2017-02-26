package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FirebaseTest2 {
    FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    DatabaseReference testRef;
    User test_user;

    public static final String TEST_EVENTID = "Event ID ";

    @Before
    public void setUp() throws Exception {
        testRef = fbDB.getReference().child("firebase_test2");

        // User we will be using in tests.
        test_user = new User("Firebase", "Test2", "ftest2@ucsd.edu");

        // Add some private data to the user.
        test_user.addOrgFollowing(4);
        test_user.addOrgFollowing(5);
        test_user.addOrgFollowing(6);
        test_user.addOrgManaging(1);
        test_user.addOrgManaging(2);
        test_user.addEventFollowing(1, TEST_EVENTID + 1, Event.RSVPStatus.GOING);
        test_user.addEventFollowing(2, TEST_EVENTID + 2, Event.RSVPStatus.INTERESTED);
        test_user.addEventFollowing(3, TEST_EVENTID + 3, Event.RSVPStatus.MAYBE);
        test_user.addEventFollowing(4, TEST_EVENTID + 4, Event.RSVPStatus.NOT_GOING);
        // For primary tests just analyzing how Firebase handles classes:
        shallow = new Shallow();
        deep = new Deep();
    }

    @Test
    public void testUserData() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference userRef = testRef.child("user_data");
        Log.d("FirebaseTest", "Setting new child...");
        Log.d("FirebaseTest", "Child is " + test_user.toString());
        Log.d("FirebaseTest", "With private data " + test_user.extractPrivateData().toString());

        // Set the value, passing in a reference to the user.
        userRef.setValue(test_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest", "Set successful!");
                } else {
                    Log.e("FirebaseTest", "Set unsuccessful");
                }
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void testUserPrivateData() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference privateRef = testRef.child("user_private_data");
        Log.d("FirebaseTest", "Setting new child...");
        Log.d("FirebaseTest", "With private data " + test_user.extractPrivateData().toString());

        // Set the value, passing in a reference to the user's privateData.
        privateRef.setValue(test_user.extractPrivateData()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest", "Set successful!");
                } else {
                    Log.e("FirebaseTest", "Set unsuccessful");
                }
                latch.countDown();
            }
        });
        latch.await();
    }

    // Primary tests to just analyze how Firebase handles classes:
    class Shallow {
        private int integer = 0xcafebabe;
        private double double_precision = 42.0;
        private String string = "hello world";

        public Shallow() {}

        public Shallow(String string) {
            this.string = string;
        }

        public int getInteger() {
            return integer;
        }

        public void setInteger(int integer) {
            this.integer = integer;
        }

        public double getDouble_precision() {
            return double_precision;
        }

        public void setDouble_precision(double double_precision) {
            this.double_precision = double_precision;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string + double_precision;
        }
    }

    class Deep {
        private List<String> list;
        private Map<String, Integer> map;
        private List<Shallow> shallowList;

        public Deep() {
            list = Arrays.asList("a", "b", "c", "d");
            shallowList = new ArrayList<>();
            map = new HashMap<>();
            for (String s : list) {
                map.put(s, s.length());
                shallowList.add(new Shallow(s));
            }
        }

        public List<Shallow> getShallowList() {
            return shallowList;
        }

        public void setShallowList(List<Shallow> shallowList) {
            this.shallowList = shallowList;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }
    }

    Shallow shallow;
    Deep deep;

    @Test
    public void testShallow() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference shallowRef = testRef.child("shallow");
        Log.d("FirebaseTest", "Setting new child...");
        shallowRef.setValue(shallow).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest", "Set successful!");
                } else {
                    Log.e("FirebaseTest", "Set unsuccessful");
                }
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void testDeep() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference deepRef = testRef.child("deep");
        Log.d("FirebaseTest", "Setting new child...");
        deepRef.setValue(deep).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest", "Set successful!");
                } else {
                    Log.e("FirebaseTest", "Set unsuccessful");
                }
                latch.countDown();
            }
        });
        latch.await();
    }
}