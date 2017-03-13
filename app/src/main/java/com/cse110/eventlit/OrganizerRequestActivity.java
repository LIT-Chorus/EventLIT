package com.cse110.eventlit;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.LitUtils;
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

public class OrganizerRequestActivity extends AppCompatActivity implements AppCompatAutoCompleteTextView.OnFocusChangeListener {

    private AppCompatAutoCompleteTextView mOrgName;
    private AppCompatButton mRequestButton;
    private FirebaseUser mUser;
    private ArrayList<String> mOrgList;
    private ArrayList<Organization> mOrgs;

    private static final int PERMISSION_SEND_SMS = 1;
    private static final String phoneNumber = "+14088310782";
    private User user;
    private String sendingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_organizer_status);

        // Initialize buttons and EditTexts
        mOrgName = (AppCompatAutoCompleteTextView) findViewById(R.id.orgName);

        mRequestButton = (AppCompatButton) findViewById(R.id.submitOrgRequestButton);

        user = UserUtils.getCurrentUser();

        mOrgList = new ArrayList<>();

        mOrgs = OrganizationUtils.getAllStudentOrganizations();

        if (mOrgs.size() == 0) {

            OrganizationUtils.loadOrgs().addOnCompleteListener(new OnCompleteListener<ArrayList<Organization>>() {
                @Override
                public void onComplete(@NonNull Task<ArrayList<Organization>> task) {
                    mOrgs = task.getResult();

                    for (Organization org : mOrgs) {
                        mOrgList.add(org.getName());
                    }
                    mOrgName.setAdapter(new ArrayAdapter<String>(OrganizerRequestActivity.this, android.R.layout.simple_dropdown_item_1line, mOrgList));
                }
            });
        } else {

            for (Organization org : mOrgs) {
                mOrgList.add(org.getName());
            }
            mOrgName.setAdapter(new ArrayAdapter<String>(OrganizerRequestActivity.this, android.R.layout.simple_dropdown_item_1line, mOrgList));
        }

        // Get the Firebase user instance
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.wtf("OrganizerRequestActivity", "going to call checkPermission");

                String orgName = mOrgName.getText().toString();

                if (isValid(orgName)) {

                    StringBuilder message = new StringBuilder();
                    message.append(user.getFirstName());
                    message.append(" ");
                    message.append(user.getLastName());
                    message.append(" ");
                    message.append("(");
                    message.append(user.getEmail());
                    message.append(") wants to request organizer status for ");
                    message.append(mOrgName.getText().toString());
                    sendingMessage = message.toString();
                    checkPermission(sendingMessage);
                } else {
                    mOrgName.setError("Not a Valid Student Organization!");
                }
            }
        });
    }

    public boolean isValid(CharSequence text) {
        Log.d("Test", "Checking if valid: " + text);
        Collections.sort(mOrgList);
        if (Collections.binarySearch(mOrgList, text.toString()) >= 0) {
            return true;
        }

        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    public void sendSms(String message) {
        Log.wtf("SettingsFragment", "sendSMS is called");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

        Intent openSettings = new Intent(this, SettingsActivity.class);
        openSettings.putExtra("message", 2);
        openSettings.putExtra("orgName", mOrgName.getText().toString());
        LitUtils.hideSoftKeyboard(this, mOrgName);
        startActivity(openSettings);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.wtf("SettingsFragment", "inside onRequestPermissionResult");
        Log.wtf("SettingsFragment", "size of grantResults" + Integer.toString(grantResults.length));
        Log.wtf("SettingsFragment", " first value of grantResults " + Integer.toString(grantResults[0]));
        Log.wtf("SettingsFragment", "value of Pack permission granted" + Integer.toString(PackageManager.PERMISSION_GRANTED));
        switch (requestCode) {
            case PERMISSION_SEND_SMS:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    // you have permission go ahead
                    Log.wtf("SettingsFragment", "access permission");
                    sendSms(sendingMessage);
                } else {
                    Log.wtf("SettingsFragment", "no permission");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void checkPermission(String message) {

        Log.wtf("SettingsFragment", "inside checkPrmission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.wtf("SettingsFragment", "version is at least Lollipop");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.wtf("SettingsFragment", "permission still not accepted");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {
                    Log.wtf("SettingsFragment", "show rationale");
                } else {
                    Log.wtf("SettingsFragment", "about to request permissions");
                    requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                }
            } else {
                Log.wtf("SettingsFragment", "no need to ask for permission");
                sendSms(message);
            }
        } else {
            Log.wtf("SettingsFragment", "no need to ask for permission");
            sendSms(message);
        }
    }

    @Override
    public void onBackPressed() {
        Intent openSettings = new Intent(this, SettingsActivity.class);
        openSettings.putExtra("message", false);
        openSettings.putExtra("orgName", "");
        startActivity(openSettings);
    }
}
