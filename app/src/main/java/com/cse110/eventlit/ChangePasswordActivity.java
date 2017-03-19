package com.cse110.eventlit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.utils.LitUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

/**
 * Created by Michelle on 2/23/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity {

    private AppCompatButton mSubmitChangePass;
    private TextInputLayout mOldPass;
    private TextInputLayout mPass1;
    private TextInputLayout mPass2;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Initialize buttons and EditTexts
        mSubmitChangePass = (AppCompatButton) findViewById(R.id.submitNewPassButton);
        mOldPass = (TextInputLayout) findViewById(R.id.current_password);
        mPass1 = (TextInputLayout) findViewById(R.id.new_pass);
        mPass2 = (TextInputLayout) findViewById(R.id.confirm_pass);

        // Get the Firebase user instance
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mOldPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkOldPass();
            }
        });

        // Handling Change Password Button Listener
        mSubmitChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get TextValues
                String oldPass = mOldPass.getEditText().getText().toString();
                String newPass1 = mPass1.getEditText().getText().toString();
                String newPass2 = mPass2.getEditText().getText().toString();

                // Do this when reset is done
                OnCompleteListener<String> passwordResetListener = new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String message = task.getResult();
                        if (message.equalsIgnoreCase("reset success!")) {
                            Intent openSettings = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                            openSettings.putExtra("message", 1);
                            LitUtils.hideSoftKeyboard(ChangePasswordActivity.this, mSubmitChangePass);
                            startActivity(openSettings);
                            finish();
                        } else {
                            new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AlertDialogCustom)
                                    .setTitle("Password Change Failure")
                                    .setMessage(task.getResult())
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Log.w("Edit", "Successful");
                                        }
                                    }).create().show();
                        }
                    }
                };


                // Edge Case: new passwords do not match

                boolean old = checkOldPass();
                boolean updated = checkPassMatch();

                if (old && updated) {
                    // PS: Backend handles case for incorrect old password

                    // Backend will take care of it from here
                    UserUtils.resetPassword(mUser, oldPass, newPass1, passwordResetListener);
                }

            }
        });

    }

    protected boolean checkOldPass() {
        EditText passEditText = mOldPass.getEditText();

        if (passEditText != null && passEditText.getError() != null) return false;

        String passwordText = null;
        if (passEditText != null) {
            passwordText = passEditText.getText().toString();
        }

        // Password Criteria
        if (passwordText != null) {
            if (passwordText.length() < 6) {
                passEditText.setError("Password must contain at least 6 characters");
                return false;
            } else if (passwordText.length() == 0) {
                passEditText.setError("Please enter your old password");
                return false;
            }
        }

        return true;
    }

    protected boolean checkPassMatch() {

        boolean fail = true;

        EditText passEditText = mPass1.getEditText();

        if (passEditText != null && passEditText.getError() != null) return false;

        String passwordTextOne = null;

        if (passEditText != null) {
            passwordTextOne = passEditText.getText().toString();
        }

        EditText confirmPassEditText = mPass2.getEditText();
        if (confirmPassEditText != null && confirmPassEditText.getError() != null) return false;


        String confirmPasswordText = null;
        if (confirmPassEditText != null) {
            confirmPasswordText = confirmPassEditText.getText().toString();
        }

        if (passwordTextOne == null || passwordTextOne.length() == 0) {
            passEditText.setError("Please Enter Your New Password");
            fail = false;
        } else if (passwordTextOne.length() < 6) {
            passEditText.setError("Password must contain at least 6 characters");
            fail = false;
        }

        if (confirmPasswordText == null || confirmPasswordText.length() == 0) {
            confirmPassEditText.setError("Please Confirm your Password");
            fail = false;
        } else if (confirmPasswordText.length() < 6) {
            confirmPassEditText.setError("Password must contain at least 6 characters");
            fail = false;
        }

        // Password Criteria
        if (passwordTextOne != null && confirmPasswordText != null &&
                !confirmPasswordText.equals(passwordTextOne)) {
            confirmPassEditText.setError("Passwords Don't Match");
            fail = false;
        }

        return fail;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChangePasswordActivity.this, SettingsActivity.class));
    }
}
