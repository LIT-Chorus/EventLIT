package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FirebaseTest {
    FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    DatabaseReference testRef;
    User setUser;
    User retrievedUser;

    public static final String TEST_EVENTID = "Test Event ID#";

    @Before
    public void setUp() throws Exception {
        testRef = fbDB.getReference().child("firebase_test");

        // User we will be using in tests.
        setUser = new User("Firebase", "Test", "ftest@ucsd.edu");

        // Add some private data to the setUser.
        setUser.addOrgFollowing(0);
        setUser.addOrgFollowing(1);
        setUser.addOrgManaging(0);
        setUser.addEventFollowing(0, TEST_EVENTID + 0, Event.RSVPStatus.GOING);
        setUser.addEventFollowing(1, TEST_EVENTID + 1, Event.RSVPStatus.INTERESTED);

        // Construct new empty user for retriving stuff.
        retrievedUser = new User();

        // For primary tests just analyzing how Firebase handles classes:
        shallow = new Shallow();
        deep = new Deep();
    }

    @Test
    public void testUserData() throws Exception {
        // Actually clear out PrivateData since we'll only be grabbing public info
        setUser.applyPrivateData(new User.PrivateData());

        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference userRef = testRef.child("user_data");
        Log.d("FirebaseTest", "Setting new child...");
        Log.d("FirebaseTest", "Child is " + setUser.toString());
        Log.d("FirebaseTest", "With private data " + setUser.extractPrivateData().toString());

        // Set the value, passing in a reference to the setUser.
        userRef.setValue(setUser).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        Log.d("FirebaseTest", "Getting public data...");
        final CountDownLatch getLatch = new CountDownLatch(1);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrievedUser = dataSnapshot.getValue(User.class);
                getLatch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getLatch.await();

        Log.d("FirebaseTest", "Data coming back is " + retrievedUser.toString());
        Log.d("FirebaseTest", "With private data " + retrievedUser.extractPrivateData().toString());
        assertEquals(setUser, retrievedUser);
    }

    @Test
    public void testUserPrivateData() throws Exception {
        final CountDownLatch setLatch = new CountDownLatch(1);
        DatabaseReference privateRef = testRef.child("user_private_data");
        Log.d("FirebaseTest", "Setting new child...");
        Log.d("FirebaseTest", "With private data " + setUser.extractPrivateData().toString());

        // Set the value, passing in a reference to the setUser's privateData.
        privateRef.setValue(setUser.extractPrivateData()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest", "Set successful!");
                } else {
                    Log.e("FirebaseTest", "Set unsuccessful");
                }
                setLatch.countDown();
            }
        });
        setLatch.await();

        Log.d("FirebaseTest", "Getting private data...");
        final CountDownLatch getLatch = new CountDownLatch(1);
        privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrievedUser.applyPrivateData(dataSnapshot.getValue(User.PrivateData.class));
                getLatch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getLatch.await();

        Log.d("FirebaseTest", "Data coming back is " + retrievedUser.extractPrivateData().toString());
        assertEquals(setUser.extractPrivateData(), retrievedUser.extractPrivateData());
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