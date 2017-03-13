package com.cse110.eventlit;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.cse110.chrous.R;

import com.cse110.eventlit.db.User;
import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A {@link PreferenceActivity} that presents a set of application activity_settings. On
 * handset devices, activity_settings are presented as a single list. On tablets,
 * activity_settings are split by category, with category headers shown to the left of
 * the list of activity_settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean mOrganizerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setupActionBar();
        setContentView(R.layout.activity_settings);

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
            fragment = new SettingsFragment();
            Bundle arguments = new Bundle();
            arguments.putBoolean("organizer", mOrganizerStatus);
            fragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

        // My Code
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            updateHeader(user.getDisplayName(), user.getEmail());
        }

        boolean requested = getIntent().getBooleanExtra("message", false);
        if (requested) {
            String orgName = getIntent().getStringExtra("orgName");
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("Organizer Request Submitted")
                    .setMessage("You have successfully submitted a request to be an organizer " +
                            "for: " + orgName + ". You will receive a response via a text " +
                            "message regarding your request within 24 hours.")
                    .setPositiveButton(android.R.string.ok, null);
            builder.create().show();
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
            startActivity(new Intent(SettingsActivity.this, OrganizerFeedActivity.class));
        } else {
            startActivity(new Intent(SettingsActivity.this, StudentFeedActivity.class));
        }
        finish();
    }

    // TODO: Open intents to other activities here
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (mOrganizerStatus) {
                startActivity(new Intent(SettingsActivity.this, OrganizerFeedActivity.class ));
            } else {
                startActivity(new Intent(SettingsActivity.this, StudentFeedActivity.class));
            }
            finish();
        } else if (id == R.id.nav_explore) {
            Intent act = new Intent(SettingsActivity.this, ExploreActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_follow_orgs) {
            Intent act = new Intent(SettingsActivity.this, OrganizationSelectionActivity.class);
            act.putExtra("organizer", mOrganizerStatus);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_settings) {
            //
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
