package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Task<User> userTask = UserUtils.logIn(USER_EMAIL, USER_PASSWORD, new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {}
        });

        fbAuth = FirebaseAuth.getInstance();

        // Wait for it...
        Tasks.await(userTask);
        user = userTask.getResult();
    }

    @Test
    public void getOrgsManaging() throws Exception {
        List<Organization> orgsManaging = UserUtils.getOrgsManaging();
    }

    @Test
    public void testgetEventsFollowingAsync() throws Exception {
        ArrayList<Event> events = new ArrayList<>();


        Set<String> set = new HashSet<>();
        //UserUtils.getEventsFollowing(null, events, set);

        Log.wtf("yo", events.toString());

        ArrayList<String> descriptions = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            descriptions.add(events.get(i).getDescription());
        }
        Log.wtf("yo", descriptions.toString());
    }

    @Test
    public void testUpdateCurrentUser() throws Exception {
        // User already logged in, proceed to use util function
        User oldData = user;
        User newData = new User("Kratiper", "Pants", "kpants@ucsd.edu");
        UserUtils.updateCurrentUser(newData);

        User currentData = UserUtils.getCurrentUser();
        assertEquals(newData, currentData);

        // It has been verified that Firebase will have the new user data after the call,
        // now set back to old data for persistence
        UserUtils.updateCurrentUser(oldData);

        // Check for race conditions
        currentData = UserUtils.getCurrentUser();
        assertEquals(oldData, currentData);
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        // User already logged in, proceed to use util function
        User user = UserUtils.getCurrentUser();

        assertEquals(user.getEmail(), USER_EMAIL);
        assertEquals(user.getFirstName(), "Christopher");
        assertEquals(user.getLastName(), "Pence");
    }

    @Test
    public void testLogIn() throws Exception {
        Task<User> userTask = UserUtils.logIn(USER_EMAIL, USER_PASSWORD, new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {}
        });

        // Wait for it...
        Tasks.await(userTask);
        User user = userTask.getResult();

        assertEquals(user.getEmail(), USER_EMAIL);
        assertEquals(user.getFirstName(), "Christopher");
        assertEquals(user.getLastName(), "Pence");
    }

    @Test
    public void testLogOut() throws Exception {
        // User has been logged in, just call logOut now
        Task<User> logOutTask = UserUtils.logOut(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {

            }
        });

        // Wait for it...
        Tasks.await(logOutTask);

        assertEquals(null, UserUtils.getCurrentUser());
    }
}