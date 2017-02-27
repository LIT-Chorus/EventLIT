package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.cse110.eventlit.db.Event;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserTest {
    private static final String USER_EMAIL = "cpence@ucsd.edu";
    private static final String USER_PASSWORD = "hellothere";

    public static final String TEST_EVENTID = "Test Event ID#";

    User user;
    FirebaseAuth fbAuth;
    DatabaseReference fbDB;

    @Before
    public void setUp() throws Exception {
        fbAuth = FirebaseAuth.getInstance();
        fbDB = FirebaseDatabase.getInstance().getReference();
    }

    @Test public void testReadUser() {
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

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.wtf("UserUtilsTest", "setup is complete");

    }

    // Unit testing for Password Reset
    @Test
    public void testPasswordReset() {
        final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        Log.w("Password Test", "Testing begins");
        final CountDownLatch signal = new CountDownLatch(2);
        final OnCompleteListener passwordResetListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                // Check if the tests pass
                signal.countDown();
                assertEquals(task.isSuccessful(), true);
                if (task.isSuccessful()){
                    Log.w("Password Test", "Password was successfully Reset!");
                }
                else {
                    Log.w("Password Test", "Password failed to be reset!");
                }
            }
        };

        // Executed after signIn is complete
        final OnCompleteListener signInCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                signal.countDown();
                if (task.isSuccessful()){
                    Log.w("Password Test", "Login Successful!");
                    UserUtils.resetPassword(fbAuth.getCurrentUser(),"NewPassword", "NewPassword",
                            passwordResetListener);
                }
                else {
                    Log.w("Password Test", "Login Failed!");
                }
            }
        };

        fbAuth
                .signInWithEmailAndPassword("saraghun@ucsd.edu", "NewPassword")
                .addOnCompleteListener(signInCompleteListener);
        try {
            signal.await();
        }
        catch (InterruptedException e){
            Log.w("Password Test", "Test interrupted");
        }
    }

    @Test
    public void testPublicData() throws Exception {
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
                            user = dataSnapshot.getValue(User.class);
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("testPublicData", "onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("testPublicData", "Authorization task not successful!");
                }
            }
        });
        latch.await();
        Log.w("testPublicData", "Data is... " + user);
        assertEquals("Christopher", user.getFirstName());
        assertEquals("Pence", user.getLastName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    /*
    @Test
    public void testPrivateData() throws Exception {
        final User expectedUser = new User();
        expectedUser.addOrgFollowing(0);
        expectedUser.addOrgFollowing(1);
        expectedUser.addOrgManaging(0);
        expectedUser.addEventFollowing(0, TEST_EVENTID + 0, Event.RSVPStatus.GOING);
        expectedUser.addEventFollowing(1, TEST_EVENTID + 1, Event.RSVPStatus.INTERESTED);

        final CountDownLatch latch1 = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    String uid = fbUser.getUid();
                    final String email = fbUser.getEmail();
                    // Set test user data into database.
                    fbDB.child("userPrivateData").child(uid).setValue(expectedUser.extractPrivateData());

                    // See if we can retrieve the same data afterwards.
                    fbDB.child("userPrivateData").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User.PrivateData data = dataSnapshot.getValue(User.PrivateData.class);
                            user.applyPrivateData(data);
                            latch1.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("testPrivateData", "onCancelled", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("testPrivateData", "Authorization task not successful!");
                }
            }
        });
        latch1.await();
        Log.w("testMapConstructor", "Private data is... " + user.extractPrivateData());
        assertEquals(expectedUser.extractPrivateData().getEventsFollowing(), user.extractPrivateData().getEventsFollowing());
    }
    */
}