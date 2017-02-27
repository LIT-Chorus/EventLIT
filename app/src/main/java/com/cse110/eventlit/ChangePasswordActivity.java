package com.cse110.eventlit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Michelle on 2/23/2017.
 */

public class ChangePasswordActivity  extends AppCompatActivity {

    private AppCompatButton mSubmitChangePass;
    private EditText mOldPass;
    private EditText mPass1;
    private EditText mPass2;
    private FirebaseUser mUser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.change_password);

            // Initialize buttons and EditTexts
            mSubmitChangePass = (AppCompatButton) findViewById(R.id.submitNewPassButton);
            mOldPass = (EditText)findViewById(R.id.oldPassword);
            mPass1 = (EditText)findViewById(R.id.newPassword1);
            mPass2 = (EditText)findViewById(R.id.newPassword2);

            // Get the Firebase user instance
            mUser = FirebaseAuth.getInstance().getCurrentUser();



            // Handling Change Password Button Listener
            mSubmitChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get TextValues
                    String oldPass = mOldPass.getText() + "";
                    String newPass1 = mPass1.getText() + "";
                    String newPass2 = mPass2.getText() + "";

                    // Do this when reset is done
                    OnCompleteListener<String> passwordResetListener = new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            Toast result = Toast.makeText(ChangePasswordActivity.this,
                                    task.getResult(), Toast.LENGTH_SHORT);
                            result.show();
                        }
                    };

                    // Checks for empty input
                    if (oldPass.equals("") || newPass1.equals("") || newPass2.equals("")) {
                        Toast toast = Toast.makeText(ChangePasswordActivity.this,
                                "Please enter valid password", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    // Edge Case: new passwords do not match
                    else if (!newPass1.equals(newPass2)){
                        Toast error = Toast.makeText(ChangePasswordActivity.this,
                                "Passwords do not match", Toast.LENGTH_SHORT);
                        error.show();
                    }
                    else {
                        // PS: Backend handles case for incorrect old password

                        // Backend will take care of it from here
                        UserUtils.resetPassword(mUser, oldPass, newPass1, passwordResetListener);
                    }

                }
            });

        }
}
