package com.cse110.eventlit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.eventlit.db.RSVP;
import com.cse110.utils.UserUtils;

import org.w3c.dom.Text;

import me.grantland.widget.AutofitHelper;

public class StudentDetailedEventActivity extends AppCompatActivity {

    private String orgId;
    private String eventId;

    private boolean mOrganizerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_detailed_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fill in the detailed events info with bundle passed in
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        fillFields(extras);

        // TODO: Set database going/interested/not going entry
        final Button goingBut = (Button) findViewById(R.id.goingButton);
        final Button interestedBut = (Button) findViewById(R.id.interestedButton);
        final Button notGoingBut = (Button) findViewById(R.id.notGoingButton);

        int rsvp = extras.getInt("RSVP");
        if (rsvp == 1) {
            goingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.goingColor, null));
            interestedBut.setBackgroundColor(Color.GRAY);
            notGoingBut.setBackgroundColor(Color.GRAY);
        } else if (rsvp == 2) {
            interestedBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.interestedColor, null));
            goingBut.setBackgroundColor(Color.GRAY);
            notGoingBut.setBackgroundColor(Color.GRAY);
        } else if (rsvp == 3) {
            notGoingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.notGoingColor, null));
            goingBut.setBackgroundColor(Color.GRAY);
            interestedBut.setBackgroundColor(Color.GRAY);
        }


        goingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.GOING);
                UserUtils.addEventsFollowing(eventId, status);
                goingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.goingColor, null));
                interestedBut.setBackgroundColor(Color.GRAY);
                notGoingBut.setBackgroundColor(Color.GRAY);
            }
        });

        interestedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.INTERESTED);
                UserUtils.addEventsFollowing(eventId, status);
                interestedBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.interestedColor, null));
                goingBut.setBackgroundColor(Color.GRAY);
                notGoingBut.setBackgroundColor(Color.GRAY);
            }
        });

        notGoingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.NOT_GOING);
                UserUtils.addEventsFollowing(eventId, status);
                notGoingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.notGoingColor, null));
                goingBut.setBackgroundColor(Color.GRAY);
                interestedBut.setBackgroundColor(Color.GRAY);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        if (mOrganizerStatus) {
            startActivity(new Intent(this, OrganizerFeedActivity.class));
        } else {
            startActivity(new Intent(this, StudentFeedActivity.class));
        }
        finish();
    }

    /* Sets the text fields dynamically */
    private void fillFields(Bundle bundle) {

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(bundle.getString("eventName"));
        AutofitHelper.create(title);

        TextView date = (TextView) findViewById(R.id.timetext);
        date.setText(bundle.getString("date").replaceAll("[\\t\\n\\r]+"," ")
                + " at " + bundle.getString("time").trim());

        TextView location = (TextView) findViewById(R.id.locationtext);
        location.setText(bundle.getString("location"));

        TextView category = (TextView) findViewById(R.id.tagtext);
        category.setText(bundle.getString("category"));

        TextView description = (TextView) findViewById(R.id.descriptiontext);
        description.setText(bundle.getString("description"));

        TextView numAttend = (TextView) findViewById(R.id.peopletext);
        numAttend.setText("" + bundle.getInt("num_attending"));

        TextView name = (TextView) findViewById(R.id.orgname);
        name.setText("Hosted By " + bundle.getString("org_name"));

        orgId = bundle.getString("org_id");
        eventId = bundle.getString("event_id");
        mOrganizerStatus = bundle.getBoolean("organizer");

    }

}
