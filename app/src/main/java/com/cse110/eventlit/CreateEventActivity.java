package com.cse110.eventlit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputLayout mTitle;
    private TextInputLayout mLocation;
    private TextInputLayout mDescription;
    private TextInputLayout mTag;
    private TextInputLayout mCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Cancel and Create button functionality
        Button cancelBut = (Button) findViewById(R.id.cancelButton);
        Button createBut = (Button) findViewById(R.id.createButton);

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Go back to organizer feed
                finish();
            }
        });

        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Create event (Add event to database)
                getFields();
            }
        });

        // Input Fields
        mTitle = (TextInputLayout) findViewById(R.id.title);
        mLocation = (TextInputLayout) findViewById(R.id.locationtext);
        mDescription = (TextInputLayout) findViewById(R.id.descriptiontext);
        mTag = (TextInputLayout) findViewById(R.id.tagtext);
        mCapacity = (TextInputLayout) findViewById(R.id.peopletext);

        // Check for validity of input
        mTitle.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkTitle();
                }
            }
        });
        mLocation.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkLocation();
                }
            }
        });
        mDescription.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkDescription();
                }
            }
        });
        mTag.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkTag();
                }
            }
        });
        mCapacity.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkCapacity();
                }
            }
        });

    }

    /* Show the time picker dialog */
    public void onTimeButtonClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        finish();

    }

    /* Gets the text fields to make event in database */
    private void getFields() {
        // TODO Get inputs and make event

    }

    protected boolean checkTitle() {
        EditText titleEditText = mTitle.getEditText();

        if (titleEditText != null && titleEditText.getError() != null) return false;

        String titleText = null;
        if (titleEditText != null) {
            titleText = titleEditText.getText().toString();
        }

        // Title Criteria
        if (titleText != null && titleText.length() < 1) {
            titleEditText.setError("Title must contain at least 2 characters");
        }

        return true;
    }

    // TODO write validity checks
    protected boolean checkLocation() {
        return true;
    }
    protected boolean checkDescription() {
        return true;
    }
    protected boolean checkCapacity() {
        return true;
    }
    protected boolean checkTag() {
        return true;
    }

}
