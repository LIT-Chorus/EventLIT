package com.cse110.chrous;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
    private FirebaseAuth.AuthStateListener fbListener;

    private Button mLoginBut;
    private Button mSignupBut;

    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;

    private TextView backendRet;

    private ProgressDialog mSignInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializes Global Vars
        mLoginBut = (Button) findViewById(R.id.login);
        mSignupBut = (Button) findViewById(R.id.signup);
        mEmailEntry = (TextInputLayout) findViewById(R.id.email);
        mPasswordEntry = (TextInputLayout) findViewById(R.id.password);
        backendRet = (TextView) findViewById(R.id.backendReturn);
        mSignInProgress = new ProgressDialog(this);

        // Set up ProgressDialog
        mSignInProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mSignInProgress.setTitle("Login");
        mSignInProgress.setMessage("Logging in to EventLIT");

        mEmailEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    EditText emailEditText = mEmailEntry.getEditText();
                    String emailText = emailEditText.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                        emailEditText.setError("Enter a Valid UCSD Email");
                    } else if (!emailText.contains("@ucsd.edu")) {
                        emailEditText.setError("Please use your UCSD Email!");

                    }
                }
            }
        });

        mPasswordEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    EditText passEditText = mPasswordEntry.getEditText();
                    String passwordText = passEditText.getText().toString();

                    // Password Criteria
                    if (passwordText.isEmpty()) {
                        passEditText.setError("Invalid Password");
                    }
                }
            }
        });


        // Tracks whether a user is signed in or not
        fbAuth = FirebaseAuth.getInstance();
        fbListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    backendRet.setText("Hi " + user.getDisplayName());
                } else {
                    backendRet.setText("Not signed in!");
                }
            }
        };

        // Login Behavior
        mLoginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {
                    String emailText = mEmailEntry.getEditText().getText().toString();
                    String passwordText = mPasswordEntry.getEditText().getText().toString();

                    mSignInProgress.show();

                    signIn(emailText, passwordText);
                }
            }
        });

        // TODO #Frontend sets up a sign-up page AND-1/AND-2

    }

    /**
     * @param firstName
     * @param lastName
     * @param schoolEmail
     * @param password
     * @param confirmPass
     */
    protected void signUp(String firstName, String lastName, String schoolEmail,
                          String password, String confirmPass) {

        // TODO #Chris AND-6

    }

    /**
     * Performs sign-in validation and logs the user in.
     *
     * @param emailText
     * @param passwordText
     */
    protected void signIn(final String emailText, final String passwordText) {
        if (fbAuth != null) {
            if (mPasswordEntry.getEditText().getError() == null &&
                    mEmailEntry.getEditText().getError() == null) {
                fbAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mSignInProgress.hide();
                                // Firebase reported error on the server side displayed here
                                if (!task.isSuccessful()) {
                                    backendRet.setText(task.getException().getMessage());
                                } else {
                                    // TODO #Frontend change this to go to new activity with intent
                                    // Current implementation for testing only.
                                    Toast.makeText(LoginActivity.this, "Sign in successful!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                mSignInProgress.hide();
                Toast.makeText(LoginActivity.this, "An error has occured", Toast.LENGTH_SHORT).show();

            }
        }
        else {
            mSignInProgress.hide();
            Toast.makeText(LoginActivity.this, "Already signed in", Toast.LENGTH_SHORT).show();
        }
    }


    // App resumes
    @Override
    protected void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(fbListener);
    }

    // App exit
    @Override
    protected void onStop() {
        super.onStop();
        if (fbListener != null) {
            fbAuth.removeAuthStateListener(fbListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mEmailEntry.clearFocus();
        mEmailEntry.getEditText().clearFocus();
        mPasswordEntry.clearFocus();
        mPasswordEntry.getEditText().clearFocus();
    }
}
