package com.cse110.utils;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public static ArrayList<Organization> getOrgsFollowing() {
        ArrayList<Organization> orgsFollowing = new ArrayList<>();
        List<String> orgid_following = currentUser.getOrgsFollowing();

        for (String orgid : orgid_following) {
            orgsFollowing.add(OrganizationUtils.orgFromId(orgid));
        }

        return orgsFollowing;
    }

    /**
     * Get organizations the current user is managing
     */
    public static ArrayList<Organization> getOrgsManaging() {
        ArrayList<Organization> orgsManaging = new ArrayList<>();
        List<String> orgid_managing = currentUser.getOrgsManaging();

        for (String orgid : orgid_managing) {
            orgsManaging.add(OrganizationUtils.orgFromId(orgid));
        }

        return orgsManaging;
    }

    public static void getEventsFollowing(final CardFragment.MyAdapter adapter,
                                          final ArrayList<Event> events,
                                          final ArrayList<Event> copy,
                                          final Set<String> eventIdsAdded)
    {
//        final WrappedTask<ArrayList<Event>> wrappedTask = new WrappedTask<>();
        DatabaseReference userEvents = currentUserDB.child("eventsFollowing");
        userEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, RSVP>> genericTypeIndicator = new GenericTypeIndicator<Map<String, RSVP>>() {};
                Map<String, RSVP> rsvps = dataSnapshot.getValue(genericTypeIndicator);
                if (rsvps != null) {
                    for (RSVP rsvp : rsvps.values()) {
                        final String eventId = rsvp.getEventid();
                        String orgId = rsvp.getOrgid();
                        RSVP.Status status = rsvp.rsvpStatus;
                        if  (status != RSVP.Status.NOT_GOING) {
                            if (eventId != null && orgId != null) {
                                DatabaseReference eventRef = DatabaseUtils.getEventsDB().child(orgId).child(eventId);
                                eventRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Event event = dataSnapshot.getValue(Event.class);

                                        //Log.w("Event", dataSnapshot.toString());

                                        if (event != null && !eventIdsAdded.contains(event.getEventid())) {

                                            eventIdsAdded.add(event.getEventid());

                                            events.add(event);
                                            copy.add(event);

                                            adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                            adapter.notifyDataSetChanged();
                                        }

                                        else if (event == null){
                                            UserUtils.removeEventsFollowing(eventId);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void getEventsForOrgs(final CardFragment.MyAdapter adapter,
                                              final ArrayList<Event> events,
                                              final ArrayList<Event> copy,
                                              final Set<String> eventIdsAdded,
                                              User user) {
        List<String> orgIds = user.getOrgsFollowing();
        for (String orgId : orgIds) {
            EventUtils.getEventsByOrgId(adapter, events, copy, eventIdsAdded, orgId, 0, null, true);
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

    public static void removeEventsFollowing(String eventid) {
        currentUser.removeEventFollowing(eventid);
        currentUserDB.child("eventsFollowing").setValue(currentUser.getEventsFollowing());
    }

    public static void addEventsFollowing(String eventid, RSVP rsvp) {
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
    @Nullable
    public static User getCurrentUser() {
        if (currentUser == null)
            return null;
        else
            return new User(currentUser);
    }
}

