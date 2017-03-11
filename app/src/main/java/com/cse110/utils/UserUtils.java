package com.cse110.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.CardFragment;
import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.RSVP;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sandeep on 2/23/17.
 */

public class UserUtils {
    private static FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private static DatabaseReference fbDB = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference currentUserDB;

    /**
     * Reference to the current user object.
     * All methods in this class assume that this reference will remain constant from
     * logIn() to logOut(). DO NOT RE-ASSIGN THIS REFERENCE IN OTHER METHODS.
     */
    private static User currentUser;
    private static UserDataListener userDataListener;

    /**
     * Reset the user's password
     *
     * @param user            - a Firebase Authenticated user
     * @param currentPassword - the current password of the user
     * @param newPassword     - a new password
     * @param onComplete      - a listener to be notified when done
     */
    public static void resetPassword(@NonNull final FirebaseUser user, @NonNull String currentPassword,
                                     @NonNull final String newPassword,
                                     @NonNull final OnCompleteListener<String> onComplete)
    {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        OnCompleteListener<Void> verifyOnComplete = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Try to updateWith the password
                    updatePassword(user, newPassword, onComplete);
                } else {
                    // Old password was wrong
                    Log.w("Password Reset", "Old password was wrong");
                    onComplete.onComplete(Tasks.forResult("Old Password was Wrong"));
                }
            }
        };
        // See if the user got their old password right. If they did, they're allowed to set a new
        // password
        user.reauthenticate(credential).addOnCompleteListener(verifyOnComplete);
    }

    /**
     * Resets the password of a given user, and notifies the on complete listener when
     * done.
     *
     * @param user        - a user object
     * @param newPassword - a new password that the user has entered
     * @param onComplete  - listener to notify when done
     */
    private static void updatePassword(@NonNull FirebaseUser user, @NonNull String newPassword,
                                     @NonNull final OnCompleteListener<String> onComplete){

        OnCompleteListener<Void> verifyOnComplete = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w("Password Reset", "Successful!");
                    onComplete.onComplete(Tasks.forResult("Reset Success!"));
                }
                else {
                    // New Password Rejected by Firebase
                    Log.w("Password Reset", "Firebase does not like your password.");
                    String fbErrorMessage = task.getException().getMessage().toString();
                    onComplete.onComplete(Tasks.forResult(fbErrorMessage));

                }
            }
        };
        // Make a Firebase call to change the password. Use the verifyComplete to see whether
        // Firebase accepted the password
        user.updatePassword(newPassword).addOnCompleteListener(verifyOnComplete);
    }

    public static User getUserFromIdSynch(String uid) {

        DatabaseReference userRef = DatabaseUtils.getUsersDB().child(uid);

        final User[] user = new User[1];

        final CountDownLatch latch = new CountDownLatch(1);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);

                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("getUserFromIdSynch", "user not read in correctly");
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return user[0];

    }

    // TODO create methods to modify the User database

    public static final List<Organization> getOrgsFollowingSynch(User user) {

        List<Organization> orgsFollowing = new ArrayList<>();

        List<String> orgid_following = user.getOrgsFollowing();
        CountDownLatch finished = new CountDownLatch(orgid_following.size());

        for (String orgid : orgid_following) {
            OrganizationUtils.addOrgFromId(orgid, orgsFollowing, finished);
        }

        try {
            finished.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return orgsFollowing;

    }

    // TODO create methods to modify the User database

    public static final ArrayList<Organization> getOrgsManagingSynch(User user) {

        ArrayList<Organization> orgsManaging = new ArrayList<>();

        List<String> orgid_managing = user.getOrgsManaging();
        CountDownLatch finished = new CountDownLatch(orgid_managing.size());

        for (String orgid : orgid_managing) {
            OrganizationUtils.addOrgFromId(orgid, orgsManaging, finished);
        }

        try {
            finished.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return orgsManaging;

    }

    public static final ArrayList<Event> getEventsFollowingSynch(User user) {

        ArrayList<Event> eventsFollowing = new ArrayList<>();

        Collection<RSVP> rsvps = user.getEventsFollowing().values();

        if (rsvps.size() == 0) {
            return eventsFollowing;
        }

        CountDownLatch finished = new CountDownLatch(rsvps.size());

        for (RSVP rsvp: rsvps) {
            Log.d("getEvents: rsvp object:", rsvp.toString());
            EventUtils.addEventFromId(rsvp, eventsFollowing, finished);
            Log.d("getEvents list size:", "" + eventsFollowing.size());
        }

        try {
            finished.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return eventsFollowing;
    }

    public static Task<ArrayList<Event>> getEventsFollowing() {
        final WrappedTask<ArrayList<Event>> wrappedTask = new WrappedTask<>();
        currentUserDB.child("eventsFollowing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Event>> maptype = new GenericTypeIndicator<Map<String, Event>>(){};
                Map<String, Event> eventMap = dataSnapshot.getValue(maptype);
                // If null, means that there are no events following -- initialize empty map.
                if (eventMap == null)
                    eventMap = new HashMap<>();
                ArrayList<Event> events = new ArrayList<>(eventMap.values());
                wrappedTask.wrapResult(events);
                Log.d("getEventsFollowing", "Got events following successfully!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getEventsFollowing", "Bad things @ " + currentUser.toString());
            }
        });
        return wrappedTask.unwrap();
    }

    public static final void getEventsForOrgs(final CardFragment.MyAdapter adapter,
                                              final ArrayList<Event> events,
                                              User user) {
        List<String> orgIds = user.getOrgsFollowing();
        for (String orgId : orgIds) {
            EventUtils.getEventsByOrgId(adapter, events, orgId, 0, null, true);
        }
    }


    public static final void updateUserOnBackend(User user, String uid) {
        DatabaseReference userRef = DatabaseUtils.getUsersDB().child(uid);
        userRef.setValue(user);
    }

    /**
     * For keeping track of changes in user data, and updating the associated user with it.
     * It's important that the initial User reference does not switch references,
     * or this listener will be lost.
     */
    static class UserDataListener implements ValueEventListener {
        // User that this listener will be updating
        private User myUser;

        public UserDataListener(User u) {
            myUser = u;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User tmpUser = dataSnapshot.getValue(User.class);
            myUser.updateWith(tmpUser);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("UserDataListener", "Something went wrong while listening to " + myUser + "!");
        }
    }

    /**
     * Replaces the singleton user with the passed in user object. The changes will also be applied
     * to the Firebase endpoint associated with the user.
     *
     * Caution: Will overwrite ALL properties of the old user's data.
     *
     * TODO: think about and analyze any synchronization/race conditions that might occur here (with UserDataListener)
     * @param newData
     */
    public static void updateCurrentUser(User newData) {
        currentUser.updateWith(newData);
        currentUserDB.setValue(currentUser);
    }

    public static void updateUserFirstName(String firstName) {
        currentUser.setFirstName(firstName);
        currentUserDB.child("firstName").setValue(firstName);
    }

    public static void updateUserLastName(String lastName) {
        currentUser.setLastName(lastName);
        currentUserDB.child("lastName").setValue(lastName);
    }

    public static void updateUserEmail(String email) {
        currentUser.setEmail(email);
        currentUserDB.child("email").setValue(email);
    }

    public static void updateOrgsFollowing(ArrayList<String> orgs) {
        currentUser.setOrgsFollowing(orgs);
        currentUserDB.child("orgsFollowing").setValue(orgs);
    }

    public static void updateOrgsManaging(ArrayList<String> orgs) {
        currentUser.setOrgsManaging(orgs);
        currentUserDB.child("orgsManaging").setValue(orgs);
    }

    public static void updateEventsFollowing(String eventid, RSVP rsvp) {
        currentUser.addEventFollowing(eventid, rsvp);
        currentUserDB.child("eventsFollowing").setValue(currentUser.getEventsFollowing());
    }

    /**
     * Logs in a user with their email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @param completionListener Listener that will receive a Task&lt;User&gt; containing a
     *                           finalized User object when the log in is complete, and we have
     *                           gathered all the current user information.
     *                           If the user cannot be logged in, the completion listener will be
     *                           notified with a NullTask, which states that the operation was
     *                           unsuccessful.
     */
    public static Task<User> logIn(final String email, String password, final OnCompleteListener<User> completionListener) {
        // Incomplete task to pass back, and so that we can asynchronously set the value in callbacks below
        final WrappedTask<User> wrappedUserTask = new WrappedTask<>();

        OnCompleteListener<AuthResult> logInCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(final @NonNull Task<AuthResult> task) {
                // If sign in successful, we want to grab the user's data from Firebase
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    final DatabaseReference userRef = DatabaseUtils.getUsersDB().child(fbUser.getUid());

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            // Set new User with data to our singleton
                            currentUser = user;
                            currentUserDB = userRef;

                            // Grab that data and pass to listener, wrap *copy* in task
                            User copy = new User(user);
                            wrappedUserTask.wrapResult(copy);
                            completionListener.onComplete(wrappedUserTask.unwrap());

                            // Important: add listener to this endpoint so that we updateWith the currentUser
                            // w.r.t. changes in the Firebase data
                            userDataListener = new UserDataListener(currentUser);
                            userRef.addValueEventListener(userDataListener);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("UserUtils.logIn", "Could not get User data.");
                        }
                    });
                } else {
                    // Sign in unsuccessful, return failed Task to listener, and wrap to return
                    NullTask<User> fail = new NullTask<User>();
                    wrappedUserTask.wrap(fail);
                    completionListener.onComplete(fail);
                    Log.e("UserUtils.logIn", "Could not log in!");
                }
            }
        };

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(logInCompleteListener);

        // Actually sign in now:
        return wrappedUserTask.unwrap();
    }

    /**
     * Log out the user. On completion of log out, will notify the listener.
     * @param completionListener
     */
    public static Task<User> logOut(final OnCompleteListener<User> completionListener) {
        final WrappedTask<User> logOutTask = new WrappedTask<>();
        fbAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user has been successfully logged out
                if (firebaseAuth.getCurrentUser() == null) {
                    NullTask<User> successTask = new NullTask<User>().withSuccess(true);
                    // Remove this listener so that this method doesn't get called again.
                    // According to the internet (stackoverflow), onAuthStateChanged may be called
                    // multiple times for reasons unknown to (and shielded from) us, so best to remove
                    // all connections when we're done with the user.
                    fbAuth.removeAuthStateListener(this);

                    // Remove reference to current user.
                    currentUser = null;
                    currentUserDB = null;

                    // Respond with a simple success task if we got signed out
                    completionListener.onComplete(successTask);
                    logOutTask.wrap(successTask);
                    Log.d("UserUtils.logOut", "User signed out.");
                } else {
                    // If user not logged out, send fail task
                    NullTask<User> failTask = new NullTask<User>();
                    completionListener.onComplete(failTask);
                    logOutTask.wrap(failTask);
                    Log.e("UserUtils.logOut", "User could not be logged out!");
                }
            }
        });

        // Actually sign out to trigger listener
        fbAuth.signOut();

        return logOutTask.unwrap();
    }

    /**
     * @return A *copy* of the user singleton, don't try to modify this object to modify the user,
     *         use other methods provided in the utils class (updateCurrentUser)
     */
    public static User getCurrentUser() {
        if (currentUser == null)
            return null;
        else
            return new User(currentUser);
    }
}

