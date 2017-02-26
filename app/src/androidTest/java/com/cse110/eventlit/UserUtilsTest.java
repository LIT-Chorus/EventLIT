package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.Rsvp;
import com.cse110.eventlit.db.User;
import com.cse110.utils.DatabaseUtils;
import com.cse110.utils.UserUtils;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Created by vansh on 2/25/17.
 */
public class UserUtilsTest {

    private static final String USER_EMAIL = "cpence@ucsd.edu";
    private static final String USER_PASSWORD = "hellothere";

    User user;
    FirebaseAuth fbAuth;
    DatabaseReference fbDB;

    @Before
    public void setUp() throws Exception {
        fbAuth = FirebaseAuth.getInstance();
        fbDB = DatabaseUtils.getUsersDB();

        final CountDownLatch latch = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    String uid = fbUser.getUid();

                    Log.d("UserUtilsTest", "onComplete");

                    fbDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("UserUtilsTest", "onDataChange");
                            user = dataSnapshot.getValue(User.class);

                            latch.countDown();
                            Log.d("UserUtilsTest", user.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.e("UserUtilsTest", "Authorization task not successful!");
                }
            }
        });

        latch.await();
        Log.wtf("UserUtilsTest", "setup is complete");

    }

    @Test
    public void getOrgsManaging() throws Exception {

        List<Organization> orgsManaging = UserUtils.getOrgsManagingSynch(user);

    }

    @Test
    public void getOrgsFollowing() throws Exception {
        List<Organization> orgsFollowing = UserUtils.getOrgsFollowingSynch(user);
    }

    @Test
    public void getEventsFollowing() throws Exception {

        Log.d("Hello", user.toString());

        List<Event> eventsFollowing = UserUtils.getEventsFollowingSynch(user);

        assertEquals(2, eventsFollowing.size());

        for (Event event: eventsFollowing) {
            Log.d("aaaz", event.toString());
        }


    }



}