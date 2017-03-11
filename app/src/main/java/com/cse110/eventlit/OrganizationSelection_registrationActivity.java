package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cse110.eventlit.db.User;
import com.cse110.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class OrganizationSelection_registrationActivity extends AppCompatActivity {

    private OrganizationSelectionFragment_registration mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_selection_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new OrganizationSelectionFragment_registration();
            mFragment = (OrganizationSelectionFragment_registration) fragment;
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    public void onBackPressed() {
    }

    public void HandleClick1(View v){
        Log.d("click", "next is clicked");
        ArrayList<String> orgs_to_follow = OrganizationsAdapter.getSelectedOrganization();
        User user = UserUtils.getCurrentUser();
        for(int i = 0; i < orgs_to_follow.size(); i++){
            user.addOrgFollowing(orgs_to_follow.get(i));
            UserUtils.updateCurrentUser(user);
            Log.d("add org", orgs_to_follow.get(i));
        }

        Intent openFeed = new Intent(OrganizationSelection_registrationActivity.this, StudentFeedActivity.class);
        startActivity(openFeed);
        finish();
    }

    public void HandleClick2 (View v){
        Log.d("click", "skip is clicked");
        //clear the vector of items the user may have clicked
        OrganizationsAdapter.clearSelectedOrganization();
        Intent openFeed = new Intent(OrganizationSelection_registrationActivity.this, StudentFeedActivity.class);
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

}
