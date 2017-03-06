package com.cse110.eventlit;

/**
 * Created by vansh on 2/28/17.
 */

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;


@RunWith(AndroidJUnit4.class)
public class FileStorageUtilsTest {

    private static final String USER_EMAIL = "vcsingh@ucsd.edu";
    private static final String USER_PASSWORD = "heythere";

    FirebaseAuth fbAuth;
    DatabaseReference fbDB;

    String uid;

    @Before
    public void setUp() throws Exception {
        fbAuth = FirebaseAuth.getInstance();
        fbDB = FirebaseDatabase.getInstance().getReference();

        //final String[] uid = new String[1];

        final CountDownLatch latch = new CountDownLatch(1);
        fbAuth.signInWithEmailAndPassword(USER_EMAIL, USER_PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = fbAuth.getCurrentUser();
                    uid = fbUser.getUid();

                } else {
                    Log.e("UserUtilsTest", "Authorization task not successful!");
                }

                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.wtf("UserUtilsTest", "setup is complete");
    }

    @Test
    public void addPictureToUser() {
        /*try {
            FileStorageUtils.uploadImageFromLocalFile(uid, "/sdcard/lulz.png");
        } catch (FileNotFoundException e) {
            Log.d("failed", e.toString());
        }*/
    }

    @Test
    public void addPictureToEvent() {

    }

    @Test
    public void getPictureFromEvent() {

    }
}
