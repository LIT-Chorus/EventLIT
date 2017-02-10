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

    private TextInputLayout mFirstNameEntry;
    private TextInputLayout mLastNameEntry;
    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;
    private TextInputLayout mConfirmPasswordEntry;

    private TextView backendRet;

    private ProgressDialog mSignUpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializes Global Vars
        mRegisterBut = (Button) findViewById(R.id.register);

        mFirstNameEntry = (TextInputLayout) findViewById(R.id.firstname);
        mLastNameEntry = (TextInputLayout) findViewById(R.id.lastname);

        mEmailEntry = (TextInputLayout) findViewById(R.id.email);

        mPasswordEntry = (TextInputLayout) findViewById(R.id.password);
        mConfirmPasswordEntry = (TextInputLayout) findViewById(R.id.confirmpassword);

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

        mFirstNameEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkFirstName();
                }
            }
        });

        mLastNameEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkLastName();
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

        mConfirmPasswordEntry.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkPassMatch();
                }
            }
        });


        // Login Behavior
        mRegisterBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {

                    if (checkFirstName() && checkLastName() && checkEmail() && checkPass() &&
                            checkPassMatch()) {

                        String firstName = mFirstNameEntry.getEditText().toString();
                        String lastName = mLastNameEntry.getEditText().toString();
                        String emailText = mEmailEntry.getEditText().getText().toString();
                        String passwordText = mPasswordEntry.getEditText().getText().toString();

                        mSignUpProgress.show();

                        signUp(firstName, lastName, emailText, passwordText);
                    }
                }
            }
        });
    }

    protected boolean checkEmail() {
        EditText emailEditText = mEmailEntry.getEditText();

        if (emailEditText.getError() != null) return false;

        String emailText = emailEditText.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailEditText.setError("Enter a Valid UCSD Email");
        } else if (!emailText.contains("@ucsd.edu")) {
            emailEditText.setError("Please use your UCSD Email!");

        } else {
            return true;
        }

        return false;
    }

    protected boolean checkPass() {
        EditText passEditText = mPasswordEntry.getEditText();

        if (passEditText.getError() != null) return false;

        String passwordText = passEditText.getText().toString();

        // Password Criteria
        if (passwordText.isEmpty()) {
            passEditText.setError("Invalid Password");
            return false;
        }

        return true;
    }

    protected boolean checkPassMatch() {
        EditText passEditText = mPasswordEntry.getEditText();
        if (passEditText.getError() != null) return false;

        String passwordText = passEditText.getText().toString();

        EditText confirmPassEditText = mConfirmPasswordEntry.getEditText();
        if (confirmPassEditText.getError() != null) return false;



        String confirmPasswordText = confirmPassEditText.getText().toString();

        // Password Criteria
        if (!confirmPasswordText.equals(passwordText)) {
            confirmPassEditText.setError("Passwords Don't Match");
            return false;
        }

        return true;
    }

    protected boolean checkFirstName() {
        EditText firstNameEditText = mFirstNameEntry.getEditText();

        if (firstNameEditText.getError() != null) return false;


        String firstNameText = firstNameEditText.getText().toString();

        if (firstNameText.length() == 0) {
            firstNameEditText.setError("Enter First Name");
            return false;
        }

        return true;

    }

    protected boolean checkLastName() {

        EditText lastNameEditText = mLastNameEntry.getEditText();

        if (lastNameEditText.getError() != null) return false;

        String lastNameText = lastNameEditText.getText().toString();

        if (lastNameText.length() == 0) {
            lastNameEditText.setError("Enter Last Name");
            return false;
        }

        return true;
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
     */
    protected void signUp(String firstName, String lastName, String schoolEmail,
                          String password) {

        // TODO #Chris AND-6

    }
}
