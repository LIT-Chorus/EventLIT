package com.cse110.eventlit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110.eventlit.db.User;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Firebase Authutentication and Firebase Authentication state listener
    private FirebaseAuth fbAuth;
    private AppCompatButton mLoginBut;

    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;

    private ProgressDialog mSignInProgress;
    private OnCompleteListener<User> mSignInListener;

    private TextView mForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializes Global Vars
        mForgot = (TextView) findViewById(R.id.forgotPassText);
        mLoginBut = (AppCompatButton) findViewById(R.id.login);
        mEmailEntry = (TextInputLayout) findViewById(R.id.email);
        mPasswordEntry = (TextInputLayout) findViewById(R.id.password);
        mSignInProgress = new ProgressDialog(this);

        // Set up ProgressDialog
        mSignInProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mSignInProgress.setTitle("Login");
        mSignInProgress.setMessage("Logging in to EventLIT");

        mEmailEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkEmail();
                }
            }
        });


        // Tracks whether a user is signed in or not
        try {
            fbAuth = FirebaseAuth.getInstance();
        }
        catch (IllegalStateException e){
            Toast.makeText(LoginActivity.this, "Firebase App failed to initialize",
                    Toast.LENGTH_SHORT);
            // Elegantly handle this later...
        }

        Bundle extras = getIntent().getExtras();
        boolean signedup = extras.getBoolean("signedup");
        String emailIdSignUp = extras.getString("email");

        if (signedup) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogCustom);
            builder.setTitle("Registration Successful!")
                    .setMessage("A verification email has been sent to " + emailIdSignUp +
                            ". Please verify your email then log back in to begin using our application!")
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).create().show();
        }

        // TODO #Frontend change this to go to new activity with intent
        mSignInListener = new OnCompleteListener<User>(){
            @Override
            public void onComplete(@NonNull Task<User> task){
                mSignInProgress.dismiss();
                // Firebase reported error on the server side displayed here
                if (!task.isSuccessful()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogCustom);
                    builder.setTitle("Invalid Credentials")
                            .setMessage("Please enter your registered account credentials!")
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                    mLoginBut.setClickable(true);
                } else {

                    // TODO: Backend team add logic to check if user is a student or an organizer

                    FirebaseUser currentUser = fbAuth.getCurrentUser();

                    if (currentUser.isEmailVerified()) {

                        Log.d("EmailVerification", "email is verified");
                        // Starts activity based on student or organizer
                        mLoginBut.setClickable(true);

                        // TODO: Add check if user has orgs following. If not, send to org selection. If they do, send to feed

                        User curr = UserUtils.getCurrentUser();

                        if (curr == null) {
                            Log.d("User", "Failed getting current user");
                        } else {

                            if (curr.getOrgsManaging().size() == 0) {
                                 if(curr.getOrgsFollowing().size() == 0)
                                 {
                                     Log.d("problem", "yash problem");
                                     Intent orgselection = new Intent(LoginActivity.this, OrganizationSelection_registrationActivity.class);
                                     mEmailEntry.getEditText().setText("");
                                     mPasswordEntry.getEditText().setText("");
                                     mPasswordEntry.clearFocus();
                                     mEmailEntry.clearFocus();
                                     startActivity(orgselection);
                                     finish();

                                     /*Log.d("problem", "Rahul problem");
                                     Intent openFeed = new Intent(LoginActivity.this, StudentFeedActivity.class);
                                     mEmailEntry.getEditText().setText("");
                                     mPasswordEntry.getEditText().setText("");
                                     mPasswordEntry.clearFocus();
                                     mEmailEntry.clearFocus();
                                     startActivity(openFeed);*/

                                 }
                                else {
                                     Log.d("problem", "Rahul problem");
                                     Intent openFeed = new Intent(LoginActivity.this, StudentFeedActivity.class);
                                     mEmailEntry.getEditText().setText("");
                                     mPasswordEntry.getEditText().setText("");
                                     mPasswordEntry.clearFocus();
                                     mEmailEntry.clearFocus();
                                     startActivity(openFeed);
                                     finish();
                                     /*Log.d("problem", "yash problem");
                                     Intent orgselection = new Intent(LoginActivity.this, OrganizationSelection_registrationActivity.class);
                                     mEmailEntry.getEditText().setText("");
                                     mPasswordEntry.getEditText().setText("");
                                     mPasswordEntry.clearFocus();
                                     mEmailEntry.clearFocus();
                                     startActivity(orgselection);
                                     finish();*/

                                 }
                            } else {
                                Intent openFeed = new Intent(LoginActivity.this, OrganizerFeedActivity.class);
                                mEmailEntry.getEditText().setText("");
                                mPasswordEntry.getEditText().setText("");
                                mPasswordEntry.clearFocus();
                                mEmailEntry.clearFocus();
                                startActivity(openFeed);
                                finish();
                            }
                        }
                    } else {
                        mLoginBut.setClickable(true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogCustom);
                        builder.setTitle("Email Not Verified")
                                .setMessage("Please Verify your Email before using EventLIT")
                        .setPositiveButton(android.R.string.ok, null).create().show();
                    }
                }
            }
        };

        // Login Behavior
        mLoginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLoginBut.setClickable(false);
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {
                    checkEmail();

                    if (mEmailEntry.getEditText().getError() == null) {


                        String emailText = mEmailEntry.getEditText().getText().toString();
                        String passwordText = mPasswordEntry.getEditText().getText().toString();

                        mSignInProgress.show();

                        signIn(emailText, passwordText);
                    } else {
                        mLoginBut.setClickable(true);
                    }
                } else {
                    mLoginBut.setClickable(true);
                }
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPage = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPage);
                finish();
            }
        });

    }

    /**
     * Performs sign-in validation and logs the user in.
     *
     * @param emailText
     * @param passwordText
     */
    protected void signIn(final String emailText, final String passwordText) {
            UserUtils.logIn(emailText, passwordText, mSignInListener);
    }

    protected void checkEmail() {
        EditText emailEditText = mEmailEntry.getEditText();
        String emailText = emailEditText.getText().toString();

        // Checks email valid and that it ends with @ucsd.edu
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailEditText.setError("Invalid Email ID");
        } else if (emailText.length() < 9 || !emailText.substring(emailText.length() - 9, emailText.length()).equals("@ucsd.edu")) {
            emailEditText.setError("Please use your UCSD Email!");

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
