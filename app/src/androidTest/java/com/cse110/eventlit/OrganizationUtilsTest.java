package com.cse110.eventlit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.OrganizationUtils;
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
public class OrganizationUtilsTest {

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
    public void testloadOrgs() throws Exception {
        Task<ArrayList<Organization>> orgsTask = OrganizationUtils.loadOrgs();

        Tasks.await(orgsTask);
        Log.d("testloadOrgs", orgsTask.getResult().toString());
    }
}