package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cse110.eventlit.db.User;
import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrganizationSelectionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private OrganizationSelectionFragment mFragment;

    private boolean mOrganizerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_selection);

        mOrganizerStatus = UserUtils.isOrganizer();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new OrganizationSelectionFragment();
            mFragment = (OrganizationSelectionFragment) fragment;
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

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
        CircleImageView profPic = (CircleImageView) profileView.findViewById(R.id.profile_image);
        FileStorageUtils.getImageView(profPic, this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        // TODO update prof pic
    }

    @Override
    public void onBackPressed() {
        if (mOrganizerStatus) {
            startActivity(new Intent(OrganizationSelectionActivity.this, OrganizerFeedActivity.class ));
        } else {
            startActivity(new Intent(OrganizationSelectionActivity.this, StudentFeedActivity.class));
        }
        finish();
    }

    public void HandleClick(View v){
        Log.d("click", "save is clicked");
        ArrayList<String> orgs_to_follow = OrganizationsAdapter.getSelectedOrganization();
        User user = UserUtils.getCurrentUser();
        for(int i = 0; i < orgs_to_follow.size(); i++){
            user.addOrgFollowing(orgs_to_follow.get(i));
            UserUtils.updateCurrentUser(user);
            Log.d("add org", orgs_to_follow.get(i));
        }

        //return to the feed
        Intent openFeed = new Intent(OrganizationSelectionActivity.this, StudentFeedActivity.class);
        startActivity(openFeed);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFragment.filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (mOrganizerStatus) {
                startActivity(new Intent(OrganizationSelectionActivity.this, OrganizerFeedActivity.class ));
            } else {
                startActivity(new Intent(OrganizationSelectionActivity.this, StudentFeedActivity.class));
            }
            finish();
        } else if (id == R.id.nav_explore) {
            Intent act = new Intent(OrganizationSelectionActivity.this, ExploreActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_follow_orgs) {
            //
        } else if (id == R.id.nav_settings) {
            Intent act = new Intent(OrganizationSelectionActivity.this, SettingsActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_logout) {
            UserUtils.logOut(new OnCompleteListener<User>() {
                @Override
                public void onComplete(@NonNull Task<User> task) {
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
