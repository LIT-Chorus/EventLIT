package com.cse110.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.RSVP;
import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Created by sandeep on 2/23/17.
 */

public class UserUtils {

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
                    // Try to update the password
                    updatePassword(user, newPassword, onComplete);
                } else {
                    // Old password was wrong
                    Log.w("Password Reset", "Old password was wrong");
                    Task<String> invalidPassword = new PasswordTask("Old Password was Wrong");
                    onComplete.onComplete(invalidPassword);
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

                    PasswordTask successfulPasswordResetTask = new PasswordTask("Reset Success!");
                    onComplete.onComplete(successfulPasswordResetTask);
                }
                else {
                    // New Password Rejected by Firebase
                    Log.w("Password Reset", "Firebase does not like your password.");
                    String fbErrorMessage = task.getException().getMessage().toString();
                    PasswordTask invalidPassword = new
                            PasswordTask(fbErrorMessage);
                    onComplete.onComplete(invalidPassword);

                }
            }
        };
        // Make a Firebase call to change the password. Use the verifyComplete to see whether
        // Firebase accepted the password
        user.updatePassword(newPassword).addOnCompleteListener(verifyOnComplete);
    }

    // Task to notify that the password reset was unsuccessful
    private static class PasswordTask extends Task<String> {
        String result;
        PasswordTask(String errorMessage){
            result = errorMessage;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public String getResult() {
            return result;
        }

        @Override
        public <X extends Throwable> String getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @NonNull
        @Override
        public Task<String> addOnSuccessListener(@NonNull OnSuccessListener<? super String> onSuccessListener) {
            return null;
        }

        @NonNull
        public Task<String> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super String> onSuccessListener) {
            return null;
        }

        @NonNull
        public Task<String> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super String> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<String> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<String> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<String> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }
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

        List<Integer> orgid_following = user.orgsFollowing;
        CountDownLatch finished = new CountDownLatch(orgid_following.size());

        for (int orgid : orgid_following) {
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

    public static final List<Organization> getOrgsManagingSynch(User user) {

        List<Organization> orgsManaging = new ArrayList<>();

        List<Integer> orgid_managing = user.orgsManaging;
        CountDownLatch finished = new CountDownLatch(orgid_managing.size());

        for (int orgid : orgid_managing) {
            OrganizationUtils.addOrgFromId(orgid, orgsManaging, finished);
        }

        try {
            finished.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return orgsManaging;

    }

    public static final List<Event> getEventsFollowingSynch(User user) {

        List<Event> eventsFollowing = new ArrayList<>();

        List<RSVP> rsvps = user.eventsFollowing;
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

}

