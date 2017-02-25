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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserTest {
    private static final String USER_EMAIL = "cpence@ucsd.edu";
    private static final String USER_PASSWORD = "hellothere";
    User user;
    FirebaseAuth fbAuth;
    DatabaseReference fbDB;

    @Before
    public void setUp() throws Exception {
        fbAuth = FirebaseAuth.getInstance();
        fbDB = FirebaseDatabase.getInstance().getReference();
        user = new User();
    }

    /**
     * Test the User constructor that initializes via a Map
     * @throws Exception
     */
    @Test
    public void testMapConstructor() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    String uid = fbUser.getUid();
                    final String email = fbUser.getEmail();
                    fbDB.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            data.put("email", email);
                            user.set(data);
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("testMapConstructor", "onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("testMapConstructor", "Authorization task not successful!");
                }
            }
        });
        latch.await();
        Log.w("testMapConstructor", "Data is... " + user);
        assertEquals("Christopher", user.getFirstName());
        assertEquals("Pence", user.getLastName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    public void testEventsFollowing() throws Exception {
        final User expectedu = new User();
        String testorgid = "Test Org XYZ";
        String testeventid = "Test Event ABC";
        Event.RSVPStatus teststatus = Event.RSVPStatus.GOING;
        expectedu.addEventFollowing(testorgid, testeventid, teststatus);

        final CountDownLatch latch1 = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    String uid = fbUser.getUid();
                    final String email = fbUser.getEmail();
                    // Set test user data into database.
                    fbDB.child("users").child(uid).child("eventsFollowing").setValue(expectedu.getEventsFollowing());

                    // See if we can retrieve the same data afterwards.
                    fbDB.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            data.put("email", email);
                            user.set(data);
                            latch1.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("testMapConstructor", "onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("testMapConstructor", "Authorization task not successful!");
                }
            }
        });
        latch1.await();
        Log.w("testMapConstructor", "Data is... " + user);
        assertEquals(expectedu.getEventsFollowing(), user.getEventsFollowing());
    }
}