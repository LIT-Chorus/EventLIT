package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import at.favre.lib.dali.Dali;
import me.grantland.widget.AutofitHelper;

public class OrganizerDetailedEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_detailed_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fill in the detailed events info with bundle passed in
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        fillFields(extras);

        // TODO: Set database going/interested/not going entry
        Button editBut = (Button) findViewById(R.id.editButton);
        Button deleteBut = (Button) findViewById(R.id.deleteButton);

        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Open intent to edit event (send bundle of event info as well)
            }
        });

        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Delete event
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, OrganizerFeedActivity.class));
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

        String orgId = bundle.getString("org_id");
        String eventId = bundle.getString("event_id");

    }
}
