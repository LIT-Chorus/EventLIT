package com.cse110.eventlit;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.cse110.chrous.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity implements IPickResult {

    private LinearLayout mChangePass;
    private LinearLayout mReqOrgStatus;
    private de.hdodenhof.circleimageview.CircleImageView mProfilePhoto;
    private TextView mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setupActionBar();
        setContentView(R.layout.settings);

        // My Code

        // Get Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // Get user's profile photo
        mProfilePhoto = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profilePhoto);

        // If user has already uploaded a photo, display that
        // TODO
        /* if ( ){
            mProfilePhoto
        } */

        // Set user's name
        mName = (TextView) findViewById(R.id.name);
        mName.setText(user.getDisplayName());


        // Initialize buttons
        mChangePass = (LinearLayout) findViewById(R.id.changePass);
        mReqOrgStatus = (LinearLayout) findViewById(R.id.reqOrgStatus);

        // Handling Change Password Button Listener
        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
            }
        });

        // Handling ReqOrgStatus Button Listener
        mReqOrgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO implement the request org status
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup()
                        .setTitle("Select a new Profile Picture!")
                        .setPickTypes(EPickType.GALLERY))
                        .show(SettingsActivity.this);
            }
        });


        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        // Add edit_profilephoto xml
        final View view = factory.inflate(R.layout.edit_profilephoto, null);
        dialog.setView(view);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            //If you want the Bitmap.
            Bitmap imageSelected = r.getBitmap();

            mProfilePhoto.setImageBitmap(imageSelected);

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String[] photoOptions = {"Choose from photo gallery", "Remove photo"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(photoOptions, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // The 'which' argument contains the index position
//                // of the selected item
//                if (which == 0){
//                    //Choose photo from Gallery
//                    // TODO Change Photo
//                }
//                else if (which == 1){
//                    // Remove Photo
//
//                }
//            }
//        });
//        return builder.create();
//    }
//
