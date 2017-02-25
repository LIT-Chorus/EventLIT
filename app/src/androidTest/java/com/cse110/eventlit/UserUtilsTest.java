package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.db.Organization;
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
 * Created by vansh on 2/24/17.
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
        fbDB = DatabaseUtils.getUserPrivateDataDB();
        user = new User();

        final CountDownLatch latch = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    String uid = fbUser.getUid();

                    fbDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            data.put("email", USER_EMAIL);
                            user.set(data);
                            latch.countDown();
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

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void addOrgFromId() throws Exception {
        final String ORG_ID = "0";

        List<Organization> orgs = new ArrayList<>();
        CountDownLatch signal = new CountDownLatch(1);

        UserUtils.addOrgFromId(ORG_ID, orgs, signal);

        signal.await();

        Log.wtf("UserUtilsTest", "let's see what we added");

        for (Organization org: orgs) {
            if (org == null)  Log.d("UserUtilsTest", "it is null");
            else Log.d("addOrgFromId", org.toString());
        }

        assertEquals(orgs.size(), 1);
    }

    @Test
    public void getOrgsManaging() throws Exception {

    }

    @Test
    public void getOrgsFollowing() throws Exception {

    }

    @Test
    public void getEventsFollowing() throws Exception {

    }

}