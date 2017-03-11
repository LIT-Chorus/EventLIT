package com.cse110.eventlit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cse110.utils.FileStorageUtils;
import com.cse110.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.FileNotFoundException;

import android.support.v4.app.Fragment;
/**
 * Created by rahulsabnis on 2/22/17.
 */

public class SettingsFragment extends android.support.v4.app.Fragment implements IPickResult {

    private LinearLayout mChangePass;
    private LinearLayout mReqOrgStatus;
    private de.hdodenhof.circleimageview.CircleImageView mProfilePhoto;
    private AppCompatTextView mName;
    private AppCompatTextView mOrgStatus;
    private boolean mOrganizerStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Get Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get user's profile photo
        mProfilePhoto = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.profilePhoto);

        // Set user's name
        mName = (AppCompatTextView) view.findViewById(R.id.name);

        // Initialize buttons
        mChangePass = (LinearLayout) view.findViewById(R.id.changePass);
        mReqOrgStatus = (LinearLayout) view.findViewById(R.id.reqOrgStatus);
        mOrgStatus = (AppCompatTextView) view.findViewById(R.id.organizer);

        mOrganizerStatus = UserUtils.isOrganizer();

        if (mOrganizerStatus) {
            mOrgStatus.setText("Organizer");
        } else {
            mOrgStatus.setText("Student");
        }

        mName.setText(user.getDisplayName());



        // If user has already uploaded a photo, display that
        // TODO
        /* if ( ){
            mProfilePhoto
        } */


        // Handling Change Password Button Listener
        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("SettingsFragment", "going to call checkPermission");
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });

        // Handling ReqOrgStatus Button Listener
        mReqOrgStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: KEVIN PUT YOUR LOGIC IN HERE FOR NOW
                startActivity(new Intent(getActivity(), OrganizerRequestActivity.class));
                getActivity().finish();
            }
        });

        FileStorageUtils.getImageView(mProfilePhoto, getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid());

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup()
                        .setTitle("Select a new profile picture!")
                        .setPickTypes(EPickType.GALLERY))
                        .setOnPickResult(SettingsFragment.this)
                        .show(getFragmentManager());
            }
        });


        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        // Add edit_profilephoto xml
        final View profView = factory.inflate(R.layout.edit_profilephoto, null);
        dialog.setView(profView);

        return view;
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            //If you want the Bitmap.
            Bitmap imageSelected = r.getBitmap();

            mProfilePhoto.setImageBitmap(imageSelected);

            try {
                FileStorageUtils.uploadImageFromLocalFile(FirebaseAuth.getInstance().getCurrentUser().getUid(), imageSelected);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
