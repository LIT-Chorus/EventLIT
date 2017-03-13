package com.cse110.eventlit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.LitUtils;
import com.cse110.utils.OrganizationUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditEventActivity extends AppCompatActivity implements IPickResult{

    // Fields for Organizer User to fill in
    private TextInputLayout mTitle;
    private TextInputLayout mLocation;
    private TextInputLayout mDescription;
    private ImageView mEventPic;    // Event Pic

    private List<String> orgsManaging;     // Orgs that the Organizer User manages
    private Spinner orgspinner;
    private Spinner tagspinner;

    private Calendar startDatetime;
    private Calendar endDatetime;
    private String eventId;
    private String category;
    Bundle extras;
    Button editBut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: FAB for adding event picture
        mEventPic = (ImageView) findViewById(R.id.event);
        mEventPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               PickImageDialog.build(new PickSetup()
                .setTitle("Please select a photo for the event")
                .setPickTypes(EPickType.GALLERY))
                .setOnPickResult(EditEventActivity.this)
                .show(getSupportFragmentManager());

            }
        });

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        // Add edit_profilephoto xml
        final View profView = factory.inflate(R.layout.edit_profilephoto, null);
        dialog.setView(profView);

        // Adjust default time to prevent automatic deletions
        startDatetime = Calendar.getInstance();
        endDatetime = Calendar.getInstance();
        endDatetime.add(Calendar.HOUR, 1);

        // Gets the Orgs that the Organization User is managing
        User user = UserUtils.getCurrentUser();
        ArrayList<Organization> orgs = UserUtils.getOrgsManaging();
        orgsManaging = new ArrayList<>();
        for (Organization org: orgs) {
            orgsManaging.add(org.getName());
        }

        // Cancel and Create button functionality
        Button cancelBut = (Button) findViewById(R.id.cancelButton);
        editBut = (Button) findViewById(R.id.editButton);

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Go back to organizer feed
                startActivity(new Intent(EditEventActivity.this, OrganizerFeedActivity.class));
                finish();
            }
        });

        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Create event (Add event to database)
                addEventToDB();

            }
        });

        populateSpinners();

        // Input Fields
        mTitle = (TextInputLayout) findViewById(R.id.title);
        mLocation = (TextInputLayout) findViewById(R.id.locationtext);
        mDescription = (TextInputLayout) findViewById(R.id.descriptiontext);

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


        Intent i = getIntent();
        extras = i.getExtras();

        populateFields(extras);
    }

    private void populateSpinners() {

        // Populate spinner for selecting org that the event is for
        orgspinner = (Spinner)findViewById(R.id.orgspinner);
        orgspinner.setPrompt("Organization holding event");
        ArrayAdapter<String> orgSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, orgsManaging);
        orgSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgspinner.setAdapter(orgSpinnerAdapter);

        // Populate spinner for selecting categories
        List<String> eventCategories = Arrays.asList("Academics","Career","Faith","Food","Social");
        tagspinner = (Spinner)findViewById(R.id.tagspinner);
        tagspinner.setPrompt("Category for Event");
        ArrayAdapter<String> tagSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, eventCategories);
        tagSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagspinner.setAdapter(tagSpinnerAdapter);
    }

    /* Populate the fields with the event data */
    private void populateFields(Bundle extras) {

        /* Fill fields */
        mTitle.getEditText().setText(extras.getString("eventName"));
        mLocation.getEditText().setText(extras.getString("location"));
        mDescription.getEditText().setText(extras.getString("description"));

        /* Fill time field */
        TextView startTimeText = (TextView) findViewById(R.id.starttimetext);
        startTimeText.setText(extras.getString("time"));

        /* Fill date field */
        TextView startDateText = (TextView) findViewById(R.id.startdatetext);
        startDateText.setText(extras.getString("date"));

        /* Fill in default value for spinner (org hosting event) */
        ArrayAdapter myAdap = (ArrayAdapter) orgspinner.getAdapter(); //cast to an ArrayAdapter
        int orgSpinnerPosition = myAdap.getPosition(extras.getString("org_name"));
        //set the default according to value
        orgspinner.setSelection(orgSpinnerPosition);

        /* Fill in default value for spinner (org hosting event) */
        myAdap = (ArrayAdapter) tagspinner.getAdapter(); //cast to an ArrayAdapter
        int tagSpinnerPosition = myAdap.getPosition(extras.getString("category"));
        //set the default according to value
        tagspinner.setSelection(orgSpinnerPosition);

        eventId = extras.getString("event_id");
    }

    /* Show the start time picker dialog */
    public void onStartTimeClicked(View v){
        TimePickerFragment newFragment = new TimePickerFragment();
        TextView startTimeText = (TextView) findViewById(R.id.starttimetext);
        startTimeText.setText("Start Time: ");

        newFragment.setCalendar(startDatetime);

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("timetext", R.id.starttimetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    /* Show the start date picker dialog */
    public void onStartDateClicked(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        TextView startDateText = (TextView) findViewById(R.id.startdatetext);
        startDateText.setText("Start Date: ");

        newFragment.setCalendar(startDatetime);

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("datetext", R.id.startdatetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");
    }

    /* Show the end time picker dialog */
    public void onEndTimeClicked(View v){
        TimePickerFragment newFragment = new TimePickerFragment();
        TextView endTimeText = (TextView) findViewById(R.id.endtimetext);
        endTimeText.setText("End Time: ");

        newFragment.setCalendar(endDatetime);

        // Pass in the end time textview
        Bundle args = new Bundle();
        args.putInt("timetext", R.id.endtimetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    /* Show the end date picker dialog */
    public void onEndDateClicked(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        TextView startDateText = (TextView) findViewById(R.id.enddatetext);
        startDateText.setText("End Date: ");

        newFragment.setCalendar(endDatetime);

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("datetext", R.id.enddatetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");
    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        startActivity(new Intent(this, OrganizerFeedActivity.class));
        finish();

    }

    /* Gets the text fields to make event in database */
    private void addEventToDB() {
        String title = mTitle.getEditText().getText().toString();
        String location = mLocation.getEditText().getText().toString();
        String description = mDescription.getEditText().getText().toString();

        // Get the organization name
        Spinner orgSpinner = (Spinner)findViewById(R.id.orgspinner);
        String orgName = orgSpinner.getSelectedItem().toString();
        String orgId = OrganizationUtils.getOrgId(orgName);


        Event event = new Event(title, description, orgId, eventId, startDatetime,
                endDatetime ,location, category, 0);
        
        EventUtils.updateEvent(event, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d("Finished Update", "Finished");
                if (task.isSuccessful()) {
                    String eventId = task.getResult();

                    try {
                        // Store in db
                        FileStorageUtils.uploadImageFromLocalFile(eventId,
                                ((BitmapDrawable)mEventPic.getDrawable()).getBitmap());
                        Log.w("Updated Event", task.getResult());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Log.w("Dismissed Edit", "Dismissed");
                    Intent openFeedView = new Intent(EditEventActivity.this,
                            OrganizerFeedActivity.class);
                    extras.putString("whereFrom", "edit");
                    openFeedView.putExtras(extras);
                    LitUtils.hideSoftKeyboard(EditEventActivity.this, editBut);
                    startActivity(openFeedView);
                    finish();
                }

            }
        });

    }

    /* Check validity of title */
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

        EditText editText = mDescription.getEditText();

        if (editText != null && editText.getError() != null) return false;

        String text = null;
        if (editText != null) {
            text = editText.getText().toString();
        }

        // Criteria
        if (text != null && text.length() > 100) {
            editText.setError("Title can contain at most 100 characters");
        }

        return true;
    }

    @Override
    public void onPickResult(PickResult r) {

        // Check for errors with event picture selection
        if (r.getError() == null) {

            // Store image Organizer User selected in global var (will store in db later)
            Bitmap imageSelected = r.getBitmap();
            mEventPic.setImageBitmap(imageSelected);

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
