package com.cse110.eventlit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.User;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 3/3/17.
 */

public class ExploreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private boolean mOrganizerStatus = false;

    private CardFragment fragment;

    private final ArrayList<String> categoriesCurrent = new ArrayList<>();

    private boolean[] checked = {true, true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_main_scrolling);

        mOrganizerStatus = getIntent().getExtras().getBoolean("organizer");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        FragmentManager fm = getSupportFragmentManager();
        fragment = (CardFragment) fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new CardFragment();
            Bundle pageType = new Bundle();
            pageType.putString("type", "explore");
            pageType.putBoolean("organizer", mOrganizerStatus);
            fragment.setArguments(pageType);
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

        categoriesCurrent.add("Academics");
        categoriesCurrent.add("Career");
        categoriesCurrent.add("Faith");
        categoriesCurrent.add("Food");
        categoriesCurrent.add("Social");


        // TODO Frontend use this ArrayAdapter to populate a ListView or something

        // TODO call updateHeader with user's name/email/pic
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            updateHeader(user.getDisplayName(), user.getEmail());
        }

    }

    // Updates the name/email/profile pic that is displayed in the hamburger menu
    public void updateHeader(String name, String email) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View profileView = navigationView.getHeaderView(0);
        TextView nameTextView = (TextView) profileView.findViewById(R.id.nameTextView);
        TextView emailTextView = (TextView) profileView.findViewById(R.id.emailTextView);
        nameTextView.setText(name);
        emailTextView.setText(email);
        // TODO update prof pic
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
            final CharSequence[] items = {" Time "," Popularity "};
            // arraylist to keep the selected items
            int checkedItem = 0;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Sort by")
                    .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected) {
                            if (indexSelected == 0) {
                                fragment.sortBy(Event.eventComparatorDate);
                            }
                            else {
                                fragment.sortBy(Event.eventComparatorPopularity);
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            //  You can write the code  to save the selected item here
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                        }
                    }).create();
            dialog.show();
        }

        else if (id == R.id.action_filter) {
            final CharSequence[] items = {"Academics","Career","Faith","Food","Social"};

            final ArrayList<String> categoryChanges = new ArrayList<>();
            categoryChanges.addAll(categoriesCurrent);

            for (int i = 0; i < items.length; i++) {
                if (categoriesCurrent.contains(items[i].toString())) {
                    checked[i] = true;
                }
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Filter by")
                    .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                categoryChanges.add(items[indexSelected].toString());
                            } else {
                                categoryChanges.remove(items[indexSelected].toString());
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            categoriesCurrent.clear();
                            categoriesCurrent.addAll(categoryChanges);
                            fragment.filterBy(categoriesCurrent);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            categoryChanges.clear();
                            categoryChanges.addAll(categoriesCurrent);
                        }
                    }).create();
            dialog.show();
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
            if (mOrganizerStatus) {
                startActivity(new Intent(ExploreActivity.this, OrganizerFeedActivity.class ));
            } else {
                startActivity(new Intent(ExploreActivity.this, StudentFeedActivity.class));
            }
            finish();
        } else if (id == R.id.nav_explore) {
            // This activity
        } else if (id == R.id.nav_follow_orgs) {
            Intent act = new Intent(ExploreActivity.this, OrganizationSelectionActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();

        } else if (id == R.id.nav_settings) {
            Intent act = new Intent(ExploreActivity.this, SettingsActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            UserUtils.logOut(new OnCompleteListener<User>() {
                @Override
                public void onComplete(@NonNull Task<User> task) {
                    finish();
                }
            });
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mOrganizerStatus) {
            startActivity(new Intent(ExploreActivity.this, OrganizerFeedActivity.class ));
        } else {
            startActivity(new Intent(ExploreActivity.this, StudentFeedActivity.class));
        }
        finish();
    }
}
