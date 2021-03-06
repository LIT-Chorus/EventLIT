package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.RSVP;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.LitUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.GregorianCalendar;

import at.favre.lib.dali.Dali;
import me.grantland.widget.AutofitHelper;

public class OrganizerDetailedEventActivity extends AppCompatActivity {

    TextView title;
    TextView date;
    TextView location;
    TextView category;
    TextView description;

    Event event;
    Bundle extras;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_detailed_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fill in the detailed events info with bundle passed in
        Intent i = getIntent();
        extras = i.getExtras();

        // TODO: Set database going/interested/not going entry
        final Button editBut = (Button) findViewById(R.id.editButton);
        final Button deleteBut = (Button) findViewById(R.id.deleteButton);

        title = (TextView) findViewById(R.id.title);

        date = (TextView) findViewById(R.id.timetext);

        location = (TextView) findViewById(R.id.locationtext);

        category = (TextView) findViewById(R.id.tagtext);

        description = (TextView) findViewById(R.id.descriptiontext);


        fillFields(extras);

        event = new Event(
                extras.getString("eventName"),
                extras.getString("description"),
                extras.getString("org_id"),
                extras.getString("event_id"),
                extras.getLong("startDate"),
                extras.getLong("endDate"),
                extras.getString("location"),
                extras.getString("category"),
                0
        );


        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open intent to edit event (send bundle of event info as well)
                Intent openEditView = new Intent(OrganizerDetailedEventActivity.this,
                        EditEventActivity.class);

                openEditView.putExtras(extras);
                startActivity(openEditView);
                finish();
            }
        });

        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                event = new Event(
                        extras.getString("title"),
                        extras.getString("description"),
                        extras.getString("org_id"),
                        extras.getString("event_id"),
                        extras.getLong("startDate"),
                        extras.getLong("endDate"),
                        extras.getString("location"),
                        extras.getString("category"),
                        0
                );

                EventUtils.deleteEvent(event.getEventid(), event.getOrgid(),
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent openFeedView = new Intent(OrganizerDetailedEventActivity.this,
                                OrganizerFeedActivity.class);
                        extras.putString("whereFrom", "delete");
                        openFeedView.putExtras(extras);
                        LitUtils.hideSoftKeyboard(OrganizerDetailedEventActivity.this, deleteBut);
                        startActivity(openFeedView);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (type.equals("feed")) {
            startActivity(new Intent(this, OrganizerFeedActivity.class));
        } else {
            Intent explore = new Intent(this, ExploreActivity.class);
            explore.putExtra("organizer", true);
            startActivity(explore);
        }
        finish();

    }
    /* Sets the text fields dynamically */
    private void fillFields(Bundle bundle) {

        title.setText(bundle.getString("eventName"));
        AutofitHelper.create(title);

        String startDate = LitUtils.getDateAndTime(bundle.getLong("startDate"));
        String endDate = LitUtils.getDateAndTime(bundle.getLong("endDate"));
        date.setText(startDate + " - " + endDate);

        location.setText(bundle.getString("location"));

        category.setText(bundle.getString("category"));

        TextView description = (TextView) findViewById(R.id.descriptiontext);
        description.setText(bundle.getString("description"));

        TextView numAttend = (TextView) findViewById(R.id.peopletext);
        numAttend.setText("" + bundle.getInt("num_attending"));

        TextView name = (TextView) findViewById(R.id.orgname);
        name.setText("Hosted By " + bundle.getString("org_name"));

        ImageView eventPic = (ImageView) findViewById(R.id.event);

        String orgId = bundle.getString("org_id");
        String eventId = bundle.getString("event_id");
        type = bundle.getString("type");

        FileStorageUtils.getImageView(eventPic, this, eventId);
    }
}
