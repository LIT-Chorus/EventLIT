package com.cse110.eventlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by Michelle on 2/23/2017.
 */

public class ChangePasswordActivity  extends AppCompatActivity {

    private AppCompatButton mSubmitChangePass;
    private EditText mOldPass;
    private EditText mPass1;
    private EditText mPass2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Initialize buttons and EditTexts
            mSubmitChangePass = (AppCompatButton) findViewById(R.id.submitNewPassButton);
            mOldPass = (EditText)findViewById(R.id.oldPassword);
            mPass1 = (EditText)findViewById(R.id.newPassword1);
            mPass2 = (EditText)findViewById(R.id.newPassword2);


            // Handling Change Password Button Listener
            mSubmitChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get TextValues
                    String oldPass = mOldPass.getText().toString();
                    String newPass1 = mPass1.getText().toString();
                    String newPass2 = mPass2.getText().toString();

                    // Edge Case: Empty params
                    if (oldPass == null || newPass1 == null || newPass2 == null) {


                    }

                    // Edge Case: new passwords do not match
                    if (newPass1 != newPass2){

                    }

                    // Edge Case: Old password is incorrect
                    // TODO if (oldPass != )


                    // TODO Change Password
                }
            });

        }
}
