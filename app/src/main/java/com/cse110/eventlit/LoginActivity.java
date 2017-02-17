package com.cse110.eventlit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Firebase Authutentication and Firebase Authentication state listener
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private AppCompatButton mLoginBut;

    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;

    private ProgressDialog mSignInProgress;
    private OnCompleteListener<AuthResult> mSignInListener;

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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Login Check", "Signed in as: " + user.getEmail());
                } else {
                    Log.d("Login Check", "Not signed in!");
                }
            }
        };

        // TODO #Frontend change this to go to new activity with intent
        mSignInListener = new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                mSignInProgress.hide();
                // Firebase reported error on the server side displayed here
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_LONG);
                    Log.d("Firebase Error", task.getException().getMessage());
                } else {

                    // TODO: Backend team add logic to check if user is a student or an organizer

                    // TODO: Backend team add logic to check if user is a student orhi@ an organizer

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser.isEmailVerified()) {

                        Log.d("EmailVerification", "email is verified");
                        // Starts activity based on student or organizer
                        Intent openFeed = new Intent(LoginActivity.this, StudentFeedActivity.class);
                        startActivity(openFeed);
                    }

                    else {
                        Log.d("EmailVerification", "email is not verified");
                    }

//                    Intent openFeed = new Intent(LoginActivity.this, OrganizerFeedActivity.class);
//                    startActivity(openFeed);
                }
            }
        };

        // Login Behavior
        mLoginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {
                    checkEmail();

                    if (mEmailEntry.getEditText().getError() == null) {


                        String emailText = mEmailEntry.getEditText().getText().toString();
                        String passwordText = mPasswordEntry.getEditText().getText().toString();

                        mSignInProgress.show();

                        signIn(emailText, passwordText);
                    }
                }
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPage = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPage);
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
            fbAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this,
                    mSignInListener);
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


    // App resumes
    @Override
    protected void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(mAuthStateListener);
    }

    // App exit
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            fbAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
