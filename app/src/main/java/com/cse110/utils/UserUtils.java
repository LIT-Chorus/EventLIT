package com.cse110.utils;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.Executor;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.eventlit.db.UserEventRSVP;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

/**
 * Created by sandeep on 2/23/17.
 */

public class UserUtils {
    private static DatabaseReference userPrivateDataRef = DatabaseUtils.getUserPrivateDataDB();

    /**
     * Reset the user's password
     * @param user - a Firebase Authenticated user
     * @param currentPassword - the current password of the user
     * @param newPassword - a new password
     * @param onComplete - a listener to be notified when done
     */
    public static void resetPassword(@NonNull final FirebaseUser user, @NonNull String currentPassword,
                                     @NonNull final String newPassword,
                                     @NonNull final OnCompleteListener<Void> onComplete){
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        OnCompleteListener<Void> verifyOnComplete = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Try to update the password
                    updatePassword(user, newPassword, onComplete);
                }
                else {
                    // Old password was wrong
                    Log.w("Password Reset", "Old password was wrong");
                    onComplete.onComplete(new InvalidPasswordTask());
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
     * @param user - a user object
     * @param newPassword - a new password that the user has entered
     * @param onComplete - listener to notify when done
     */
    private static void updatePassword(@NonNull FirebaseUser user, @NonNull String newPassword,
                                     @NonNull final OnCompleteListener<Void> onComplete){
        OnCompleteListener<Void> verifyOnComplete = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.w("Password Reset", "Successful!");
                    onComplete.onComplete(task);
                }
                else {
                    // New Password Rejected by Firebase
                    Log.w("Password Reset", "Firebase does not like your password.");
                    onComplete.onComplete(new InvalidPasswordTask());

                }
            }
        };
        // Make a Firebase call to change the password. Use the verifyComplete to see whether
        // Firebase accepted the password
        user.updatePassword(newPassword).addOnCompleteListener(verifyOnComplete);
    }

    /**
     * Set the users notification setting
     * @param user - a Firebase Authenticated user
     * @param value - a boolean flag for whether
     * @param onComplete - a listener to notify when the the setting has been completed
     */
    public static void setNotifications(@NonNull FirebaseUser user, boolean value, @NonNull OnCompleteListener<Void> onComplete){
        DatabaseReference currentUserPrivateData = userPrivateDataRef.child(user.getUid());
        currentUserPrivateData.setValue("notifications", value).addOnCompleteListener(onComplete);
    }

    // Task to notify that the password reset was unsuccessful
    private static class InvalidPasswordTask extends Task<Void> {
        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public Void getResult() {
            return null;
        }

        @Override
        public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }
    }


    // TODO create methods to modify the User database

    public static final void addOrgFromId(int orgid, final List<Organization> orgs) {

        DatabaseReference orgdb = DatabaseUtils.getOrganizationsDB();

        orgdb = orgdb.child(String.valueOf(orgid));

        orgdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orgs.add(dataSnapshot.getValue(Organization.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static final void getOrgsManaging(String uid, final List<Organization> orgsManaging) {

        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db = upd_db.child(uid);

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                User user = new User();

                for (int orgid: user.extractPrivateData().getOrgsManaging()) {
                    addOrgFromId(orgid, orgsManaging);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void getOrgsFollowing(String uid, final List<Organization> orgsFollowing) {
        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db = upd_db.child(uid);

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                User user = new User();

                for (int orgid: user.extractPrivateData().getOrgsFollowing()) {
                    addOrgFromId(orgid, orgsFollowing);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void addEventFromIds(UserEventRSVP rsvp, final List<Event> eventsFollowing) {
        DatabaseReference e_db = DatabaseUtils.getEventsDB();

        e_db = e_db.child(String.valueOf(rsvp.getOrgid())).child(rsvp.getEventid());

        e_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsFollowing.add(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static final void getEventsFollowing(String uid, final List<Event> eventsFollowing) {

        DatabaseReference upd_db = DatabaseUtils.getUserPrivateDataDB();

        upd_db = upd_db.child(uid);

        upd_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                User user = new User();

                for (UserEventRSVP rsvp: user.extractPrivateData().getEventsFollowing()) {
                    addEventFromIds(rsvp, eventsFollowing);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
