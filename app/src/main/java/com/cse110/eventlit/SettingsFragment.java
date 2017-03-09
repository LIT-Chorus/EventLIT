package com.cse110.eventlit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.RSVP;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.OrganizationUtils;
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

import java.util.ArrayList;

/**
 * Created by rahulsabnis on 2/22/17.
 */

public class SettingsFragment extends android.support.v4.app.Fragment implements IPickResult {

    private LinearLayout mChangePass;
    private LinearLayout mReqOrgStatus;
    private de.hdodenhof.circleimageview.CircleImageView mProfilePhoto;
    private AppCompatTextView mName;

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

        // If user has already uploaded a photo, display that
        // TODO
        /* if ( ){
            mProfilePhoto
        } */

        // Set user's name
        mName = (AppCompatTextView) view.findViewById(R.id.name);
        mName.setText(user.getDisplayName());


        // Initialize buttons
        mChangePass = (LinearLayout) view.findViewById(R.id.changePass);
        mReqOrgStatus = (LinearLayout) view.findViewById(R.id.reqOrgStatus);

        // Handling Change Password Button Listener
        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });

        // Handling ReqOrgStatus Button Listener
        mReqOrgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: KEVIN PUT YOUR LOGIC IN HERE FOR NOW
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup()
                        .setTitle("Select a new Profile Picture!")
                        .setPickTypes(EPickType.GALLERY))
                        .show(getActivity());
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

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
