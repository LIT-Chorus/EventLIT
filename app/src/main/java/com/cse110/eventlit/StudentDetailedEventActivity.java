package com.cse110.eventlit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.eventlit.db.RSVP;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.UserUtils;

import org.w3c.dom.Text;

import java.util.HashMap;

import me.grantland.widget.AutofitHelper;

public class StudentDetailedEventActivity extends AppCompatActivity {

    private String orgId;
    private String eventId;

    private boolean mOrganizerStatus = false;
    private String type;

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
        final ImageView eventPic = (ImageView) findViewById(R.id.event);

        User user = UserUtils.getCurrentUser();
        HashMap<String, RSVP> events = user.getEventsFollowing();

        if (events.get(extras.getString("event_id")).rsvpStatus == RSVP.Status.GOING) {
            goingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.goingColor, null));
            interestedBut.setBackgroundColor(Color.GRAY);
            notGoingBut.setBackgroundColor(Color.GRAY);
        } else if (events.get(extras.getString("event_id")).rsvpStatus == RSVP.Status.INTERESTED) {
            interestedBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.interestedColor, null));
            goingBut.setBackgroundColor(Color.GRAY);
            notGoingBut.setBackgroundColor(Color.GRAY);
        } else if (events.get(extras.getString("event_id")).rsvpStatus == RSVP.Status.NOT_GOING) {
            notGoingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.notGoingColor, null));
            goingBut.setBackgroundColor(Color.GRAY);
            interestedBut.setBackgroundColor(Color.GRAY);
        }

        FileStorageUtils.getImageView(eventPic, this, eventId);


        goingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.GOING);
                int modBy = UserUtils.addEventsFollowing(eventId, status);
                goingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.goingColor, null));
                interestedBut.setBackgroundColor(Color.GRAY);
                notGoingBut.setBackgroundColor(Color.GRAY);

                EventUtils.modAttendees(orgId, eventId, modBy);
            }
        });

        interestedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.INTERESTED);
                int modBy = UserUtils.addEventsFollowing(eventId, status);
                interestedBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.interestedColor, null));
                goingBut.setBackgroundColor(Color.GRAY);
                notGoingBut.setBackgroundColor(Color.GRAY);

                EventUtils.modAttendees(orgId, eventId, modBy);
            }
        });

        notGoingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSVP status = new RSVP(orgId, eventId, RSVP.Status.NOT_GOING);
                int modBy = UserUtils.addEventsFollowing(eventId, status);
                notGoingBut.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.notGoingColor, null));
                goingBut.setBackgroundColor(Color.GRAY);
                interestedBut.setBackgroundColor(Color.GRAY);

                EventUtils.modAttendees(orgId, eventId, modBy);
            }
        });


    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        if (type.equals("feed")) {
            if (mOrganizerStatus) {
                startActivity(new Intent(this, OrganizerFeedActivity.class));
            } else {
                startActivity(new Intent(this, StudentFeedActivity.class));
            }

        } else {
            Intent explore = new Intent(this, ExploreActivity.class);
            explore.putExtra("organizer", mOrganizerStatus);
            startActivity(explore);
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
        type = bundle.getString("type");
    }

}
