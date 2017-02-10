package com.cse110.eventlit;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.StringTokenizer;

public class SignUpActivity extends AppCompatActivity {

    // Firebase Authutentication and Firebase Authentication state listener
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener fbListener;

    private Button mRegisterBut;
    private FloatingActionButton mSignupBut;

    private TextInputLayout mNameEntry;
    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;

    private TextView backendRet;

    private ProgressDialog mSignUpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializes Global Vars
        mRegisterBut = (Button) findViewById(R.id.register);
        mNameEntry = (TextInputLayout) findViewById(R.id.name);
        mEmailEntry = (TextInputLayout) findViewById(R.id.email);
        mPasswordEntry = (TextInputLayout) findViewById(R.id.password);
        backendRet = (TextView) findViewById(R.id.backendReturn);
        mSignUpProgress = new ProgressDialog(this);

        // Tracks whether a user is signed in or not
        fbAuth = FirebaseAuth.getInstance();
        fbListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Login Check", "Hi " + user.getDisplayName());
                } else {
                    Log.d("Login Check", "Not signed in!");
                }
            }
        };

        // Set up ProgressDialog
        mSignUpProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mSignUpProgress.setTitle("Register");
        mSignUpProgress.setMessage("Registering new user account");

        mNameEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkName();
                }
            }
        });

        mEmailEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkEmail();
                }
            }
        });

        mPasswordEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkPass();
                }
            }
        });

        // Login Behavior
        mRegisterBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {

                    checkName();
                    checkPass();
                    checkEmail();

                    if (mPasswordEntry.getEditText().getError() == null &&
                            mEmailEntry.getEditText().getError() == null &&
                            mNameEntry.getEditText().getError() == null) {

                        StringTokenizer name = new StringTokenizer(mNameEntry.getEditText().getText().toString());
                        String firstName = name.nextToken();
                        String lastName = name.nextToken();
                        String emailText = mEmailEntry.getEditText().getText().toString();
                        String passwordText = mPasswordEntry.getEditText().getText().toString();

                        mSignUpProgress.show();

                        signUp(firstName, lastName, emailText, passwordText, passwordText);
                    }
                }
            }
        });
    }

    protected void checkEmail() {
        EditText emailEditText = mEmailEntry.getEditText();
        String emailText = emailEditText.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailEditText.setError("Enter a Valid UCSD Email");
        } else if (!emailText.contains("@ucsd.edu")) {
            emailEditText.setError("Please use your UCSD Email!");

        }
    }

    protected void checkPass() {
        EditText passEditText = mPasswordEntry.getEditText();
        String passwordText = passEditText.getText().toString();

        // Password Criteria
        if (passwordText.isEmpty()) {
            passEditText.setError("Invalid Password");
        }
    }

    protected void checkName() {
        EditText nameEditText = mNameEntry.getEditText();
        String nameText = nameEditText.getText().toString();

        StringTokenizer name = new StringTokenizer(nameText);

        if (nameText.length() == 0 || name.countTokens() != 2) {
            nameEditText.setError("Enter Your First and Last Name");
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
}
