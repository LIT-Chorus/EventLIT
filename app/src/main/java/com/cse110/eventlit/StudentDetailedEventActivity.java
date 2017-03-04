package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StudentDetailedEventActivity extends AppCompatActivity {

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
        Button goingBut = (Button) findViewById(R.id.goingButton);
        Button interestedBut = (Button) findViewById(R.id.interestedButton);
        Button notGoingBut = (Button) findViewById(R.id.notGoingButton);


        goingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        interestedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        notGoingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        finish();

    }

    /* Sets the text fields dynamically */
    private void fillFields(Bundle bundle) {

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(bundle.getString("eventName"));

        TextView date = (TextView) findViewById(R.id.timetext);
        date.setText(bundle.getString("date").replaceAll("[\\t\\n\\r]+"," ")
                + " at " + bundle.getString("time").trim());

        TextView location = (TextView) findViewById(R.id.locationtext);
        location.setText(bundle.getString("location"));

        TextView category = (TextView) findViewById(R.id.tagtext);
        category.setText(bundle.getString("category"));

    }

}
