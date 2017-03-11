package com.cse110.eventlit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Michelle on 2/23/2017.
 */

public class OrganizerRequestActivity extends AppCompatActivity {

    private AppCompatAutoCompleteTextView mOrgName;
    private AppCompatButton mRequestButton;
    private FirebaseUser mUser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.request_organizer_status);

            // Initialize buttons and EditTexts
            mOrgName = (AppCompatAutoCompleteTextView) findViewById(R.id.orgName);

            mRequestButton = (AppCompatButton) findViewById(R.id.submitOrgRequestButton);

            // Get the Firebase user instance
            mUser = FirebaseAuth.getInstance().getCurrentUser();


        }
}
