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
import com.cse110.utils.OrganizationUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements IPickResult{

    // Fields for Organizer User to fill in
    private TextInputLayout mTitle;
    private TextInputLayout mLocation;
    private TextInputLayout mDescription;
    private TextInputLayout mCapacity;

    private TextView mStartTime;
    private TextView mEndTime;

    // Event Pic
    private ImageView mEventPic;

    // Orgs that the Organizer User manages
    private List<String> orgsManaging;
    private List<String> eventCategories;

    private Calendar startDatetime;
    private Calendar endDatetime;
    private TextView mStartDate;
    private TextView mEndDate;

    private String openDate;
    private String openTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
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
                .setOnPickResult(CreateEventActivity.this)
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
        Button createBut = (Button) findViewById(R.id.createButton);

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Go back to organizer feed
                startActivity(new Intent(CreateEventActivity.this, OrganizerFeedActivity.class));
                finish();
            }
        });

        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Create event (Add event to database)
                addEventToDB();
                startActivity(new Intent(CreateEventActivity.this, OrganizerFeedActivity.class));
                finish();
                Toast.makeText(CreateEventActivity.this, "Event created.",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Populate spinner for selecting org that the event is for
        Spinner spinner = (Spinner)findViewById(R.id.orgspinner);
        spinner.setPrompt("Organization holding event");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, orgsManaging);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        createTagSpinner();

        // Input Fields
        mTitle = (TextInputLayout) findViewById(R.id.title);
        mLocation = (TextInputLayout) findViewById(R.id.locationtext);
        mDescription = (TextInputLayout) findViewById(R.id.descriptiontext);
        mCapacity = (TextInputLayout) findViewById(R.id.peopletext);

        mStartDate = (TextView) findViewById(R.id.startdatetext);
        mEndDate = (TextView) findViewById(R.id.enddatetext);
        mStartTime = (TextView) findViewById(R.id.starttimetext);
        mEndTime = (TextView) findViewById(R.id.endtimetext);

        setCurrentTime();

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

        mCapacity.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkCapacity();
                }
            }
        });
    }

    private void setCurrentTime() {
        Calendar now = Calendar.getInstance();

        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int year = now.get(Calendar.YEAR);
        int minute = now.get(Calendar.MINUTE);

        String date = month + "/" + day + "/" + year;

        int hour = now.get(Calendar.HOUR_OF_DAY);

        boolean PM = false;

        //Make the 24 hour time format to 12 hour time format
        if (hour == 0) {
            hour = 12;
        }
        if (hour >12) {
            PM = true;
            hour -= 12;
        }

        String time = hour + ":";

        if (minute < 10) {
            time += "0" + minute;
        } else {
            time += minute;
        }

        if (PM) {
            time += "PM";
        } else {
            time += "AM";
        }

        openDate = date;
        openTime = time;

        defaultStartDate();
        defaultStartTime();
        defaultEndDate();
        defaultEndTime();

    }

    private void defaultEndTime() {
        mEndTime.setText("End Time: " + openTime);
    }

    private void defaultEndDate() {
        mEndDate.setText("End Date: " + openDate);
    }

    private void defaultStartTime() {
        mStartTime.setText("Start Time: " + openTime);
    }

    private void defaultStartDate() {
        mStartDate.setText("Start Date: " + openDate);
    }

    private void createTagSpinner() {

        // Populate spinner for selecting the category of the event
        eventCategories = Arrays.asList("Academics","Career","Faith","Food","Social");
        Spinner spinner = (Spinner)findViewById(R.id.tagspinner);
        spinner.setPrompt("Event Category");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, eventCategories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
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
        args.putString("prefix", "Start Time: ");
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");

        if (startTimeText.getText().toString().equals("Start Time: ")) {
            defaultStartTime();
        }
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
        args.putString("prefix", "Start Date: ");
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");

        if (startDateText.getText().toString().equals("Start Date: ")) {
            defaultStartDate();
        }
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
        args.putString("prefix", "End Time: ");
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");

        if (endTimeText.getText().toString().equals("End Time: ")) {
            defaultEndTime();
        }
    }

    /* Show the end date picker dialog */
    public void onEndDateClicked(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        TextView endDateText = (TextView) findViewById(R.id.enddatetext);
        endDateText.setText("End Date: ");

        newFragment.setCalendar(endDatetime);

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("datetext", R.id.enddatetext);
        args.putString("prefix", "End Date: ");
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");

        if (endDateText.getText().toString().equals("End Date: ")) {
            defaultEndDate();
        }
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
        String capacity = mCapacity.getEditText().getText().toString();

        // Get the organization name
        Spinner spinner = (Spinner)findViewById(R.id.orgspinner);
        String orgName = spinner.getSelectedItem().toString();
        String orgId = OrganizationUtils.getOrgId(orgName);

        // Get Category
        spinner = (Spinner) findViewById(R.id.tagspinner);
        String category = spinner.getSelectedItem().toString();

        Log.w("Start", startDatetime.getTimeInMillis() + "");
        Log.w("End", endDatetime.getTimeInMillis() + "");

        if (capacity.equals("")) {
            capacity = "0";
        }

        Event event = new Event(title, description, orgId, "0", startDatetime,
                endDatetime ,location, category, Integer.parseInt(capacity));
        EventUtils.createEvent(event, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // TODO dismiss activity, Event has been created at this point
                    String eventId = task.getResult();

                    try {
                        if (!mEventPic.getDrawable().equals(getDrawable(R.drawable.event_pic))) {
                            // Store in db
                            FileStorageUtils.uploadImageFromLocalFile(eventId,
                                    ((BitmapDrawable)mEventPic.getDrawable()).getBitmap());
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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
    protected boolean checkCapacity() {
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
