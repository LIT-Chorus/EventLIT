package com.cse110.eventlit;

import com.cse110.eventlit.db.Event;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.cse110.utils.FirebaseUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentFeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_main_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Example call to Organizer Utils
        final TextView test = (TextView)findViewById(R.id.test);
        final ArrayAdapter<String> organizations = new ArrayAdapter<>(this,
                                                        R.layout.student_activity_main_scrolling);
        // TODO call this when ready
//        FirebaseUtils.getAllStudentOrganizations(organizations);


        // Test Code for getting events
        final ArrayAdapter<Event> events = new ArrayAdapter<>(this,
                R.layout.student_activity_main_scrolling);
        FirebaseUtils.getEventsByOrgId(events, "1");


        // TODO Frontend use this ArrayAdapter to populate a ListView or something

        // Mock Data stored in Firebase: Test Code
//        Event first_event = new Event("Spring Quarter Job Fair", "Job me plz", "1", "San Diego", "Networking", 2000);
//        Event second_event = new Event("Google Tech Talk", "Big Data", "2", "San Francisco", "Networking", 50);
//        Event third_event = new Event("Ice Cream Run", "Come get ice cream", "3", "LA", "Food", 60);
//        Event fourth_event = new Event("IEEE Micromouse", "Annual Competition", "4", "Davis", "Academics", 400);
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference();
//        DatabaseReference eventsRef = ref.child("events").child("1");
//        eventsRef.push().setValue(first_event);
//        eventsRef.push().setValue(second_event);
//        eventsRef.push().setValue(third_event);
//        eventsRef.push().setValue(fourth_event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_scrolling, menu);
        return true;
    }

    // Three dots menu
    // TODO: Handle sorting/filtering here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Open intents to other activities here
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Go to home page
        } else if (id == R.id.nav_explore) {

        } else if (id == R.id.nav_preferences) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
