package com.cse110.eventlit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.eventlit.db.Organization;
import com.cse110.utils.OrganizationUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Michelle on 2/23/2017.
 */

public class OrganizerRequestActivity extends AppCompatActivity implements
        AppCompatAutoCompleteTextView.Validator, AppCompatAutoCompleteTextView.OnFocusChangeListener {

    private AppCompatAutoCompleteTextView mOrgName;
    private AppCompatButton mRequestButton;
    private FirebaseUser mUser;
    private ArrayList<String> mOrgList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.request_organizer_status);

            // Initialize buttons and EditTexts
            mOrgName = (AppCompatAutoCompleteTextView) findViewById(R.id.orgName);

            mRequestButton = (AppCompatButton) findViewById(R.id.submitOrgRequestButton);

            mOrgList = new ArrayList<>();

            OrganizationUtils.loadOrgs().addOnCompleteListener(new OnCompleteListener<ArrayList<Organization>>() {
                @Override
                public void onComplete(@NonNull Task<ArrayList<Organization>> task) {
                    ArrayList<Organization> orgs = task.getResult();
                    for (Organization org: orgs) {
                        mOrgList.add(org.getName());
                    }
                    mOrgName.setAdapter(new ArrayAdapter<String>(OrganizerRequestActivity.this, android.R.layout.simple_dropdown_item_1line, mOrgList));
                    mOrgName.setValidator(OrganizerRequestActivity.this);
                }
            });

            // Get the Firebase user instance
            mUser = FirebaseAuth.getInstance().getCurrentUser();

            mRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
        }

    @Override
    public boolean isValid(CharSequence text) {
        Log.d("Test", "Checking if valid: "+ text);
        Collections.sort(mOrgList);
        if (Collections.binarySearch(mOrgList, text.toString()) > 0) {
            return true;
        }

        return false;
    }

    @Override
    public CharSequence fixText(CharSequence charSequence) {
        return null;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
