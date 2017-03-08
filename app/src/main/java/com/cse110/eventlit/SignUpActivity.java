package com.cse110.eventlit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110.eventlit.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    // Firebase Authutentication and Firebase Authentication state listener
    private FirebaseAuth mFbAuth;

    // Firebase Database
    private DatabaseReference fbDB;

    private Button mNextBut;

    private TextInputLayout mFirstNameEntry;
    private TextInputLayout mLastNameEntry;
    private TextInputLayout mEmailEntry;
    private TextInputLayout mPasswordEntry;
    private TextInputLayout mConfirmPasswordEntry;

    private OnCompleteListener<Void> mSignUpListener;

    private AlertDialog mDialog;

    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializes Global Vars
        mNextBut = (Button) findViewById(R.id.next);

        mFirstNameEntry = (TextInputLayout) findViewById(R.id.firstname);
        mLastNameEntry = (TextInputLayout) findViewById(R.id.lastname);

        mEmailEntry = (TextInputLayout) findViewById(R.id.email);

        mPasswordEntry = (TextInputLayout) findViewById(R.id.password);
        mConfirmPasswordEntry = (TextInputLayout) findViewById(R.id.confirmpassword);

        // Tracks whether a user is signed in or not
        Log.w("FBAuth", mFbAuth + "");
        mFbAuth = FirebaseAuth.getInstance();
        Log.w("FBAuth", mFbAuth.toString());

        // Set up database reference.
        fbDB = FirebaseDatabase.getInstance().getReference();

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

        mSignUpListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Firebase reported error on the server side displayed here
                if (task.isSuccessful()) {

                    Intent backToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                    backToLogin.putExtra("signedup", true);
                    backToLogin.putExtra("email", mEmailEntry.getEditText().getText().toString());

                    startActivity(backToLogin);
                    finish();
                        // Move to Organization selection
//                            Intent intent = new Intent(SignUpActivity.this, OrganizationSelectionActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                } else {
                    Toast.makeText(SignUpActivity.this, "Does not work", Toast.LENGTH_LONG).show();
                }
            }
        };


        // Login Behavior
        mNextBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mNextBut.setClickable(false);
                if (mEmailEntry.getEditText() != null && mPasswordEntry.getEditText() != null) {

                    if (checkFirstName() && checkLastName() && checkEmail() && checkPass() &&
                            checkPassMatch()) {

                        String firstName = mFirstNameEntry.getEditText().getText().toString();
                        String lastName = mLastNameEntry.getEditText().getText().toString();
                        String emailText = mEmailEntry.getEditText().getText().toString();
                        String passwordText = mPasswordEntry.getEditText().getText().toString();

                        mPassword = passwordText;

                        signUp(firstName, lastName, emailText, passwordText);
                    }
                }

                mNextBut.setClickable(true);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mFbAuth.getCurrentUser();

        if (user != null) {

            String email = mFbAuth.getCurrentUser().getEmail();

            if (email != null && email.length() > 0 && mPassword != null && mPassword.length() > 0) {
                mFbAuth.signOut();
                mFbAuth.signInWithEmailAndPassword(email, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser userAuthenticated = mFbAuth.getCurrentUser();

                        Log.d("Verified", userAuthenticated.isEmailVerified() + "");
                        if (userAuthenticated.isEmailVerified()) {
                            if (mDialog != null) {
                                mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }
                    }
                });
            }


        }
    }

    protected boolean checkEmail() {
        EditText emailEditText = mEmailEntry.getEditText();

        if (emailEditText != null && emailEditText.getError() != null) return false;

        String emailText = null;
        if (emailEditText != null) {
            emailText = emailEditText.getText().toString();
        }

        if (emailText != null) {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                emailEditText.setError("Invalid Email ID");
            } else if (!emailText.substring(emailText.length() - 9, emailText.length()).equals("@ucsd.edu")) {
                emailEditText.setError("Please use your UCSD Email!");
            } else {
                return true;
            }
        }

        return false;
    }

    protected boolean checkPass() {
        EditText passEditText = mPasswordEntry.getEditText();

        if (passEditText != null && passEditText.getError() != null) return false;

        String passwordText = null;
        if (passEditText != null) {
            passwordText = passEditText.getText().toString();
        }

        // Password Criteria
        if (passwordText != null && passwordText.length() < 6) {
            passEditText.setError("Password must contain at least 6 characters");
        }

        return true;
    }

    protected boolean checkPassMatch() {
        EditText passEditText = mPasswordEntry.getEditText();
        if (passEditText != null && passEditText.getError() != null) return false;

        String passwordText = null;
        if (passEditText != null) {
            passwordText = passEditText.getText().toString();
        }

        EditText confirmPassEditText = mConfirmPasswordEntry.getEditText();
        if (confirmPassEditText != null && confirmPassEditText.getError() != null) return false;


        String confirmPasswordText = null;
        if (confirmPassEditText != null) {
            confirmPasswordText = confirmPassEditText.getText().toString();
        }

        // Password Criteria
        if (confirmPasswordText != null && !confirmPasswordText.equals(passwordText)) {
            confirmPassEditText.setError("Passwords Don't Match");
            return false;
        }

        return true;
    }

    protected boolean checkFirstName() {
        EditText firstNameEditText = mFirstNameEntry.getEditText();

        if (firstNameEditText != null && firstNameEditText.getError() != null) return false;


        String firstNameText = null;
        if (firstNameEditText != null) {
            firstNameText = firstNameEditText.getText().toString();
        }

        if (firstNameText != null && firstNameText.length() == 0) {
            firstNameEditText.setError("Enter First Name");
            return false;
        }

        return true;

    }

    protected boolean checkLastName() {

        EditText lastNameEditText = mLastNameEntry.getEditText();

        if (lastNameEditText != null && lastNameEditText.getError() != null) return false;

        String lastNameText = null;
        if (lastNameEditText != null) {
            lastNameText = lastNameEditText.getText().toString();
        }

        if (lastNameText != null && lastNameText.length() == 0) {
            lastNameEditText.setError("Enter Last Name");
            return false;
        }

        return true;
    }

    /**
     * Takes preliminary user information and creates a new user account.
     * Inputs should have already been validated before passing to this method.
     *
     * @param firstName
     * @param lastName
     * @param schoolEmail
     * @param password
     */
    protected void signUp(final String firstName, final String lastName, final String schoolEmail,
                          String password) {

        // TODO #Chris AND-6
        // Register user first, and have them signed in.
        mFbAuth.createUserWithEmailAndPassword(schoolEmail, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Add a new entry to the `users` table for the user's
                            // non-auth information.
                            if (mFbAuth.getCurrentUser() != null) {
                                String uid = mFbAuth.getCurrentUser().getUid();
                                FirebaseUser userProfile = mFbAuth.getCurrentUser();

                                // Set a display name of the user
                                UserProfileChangeRequest profileUpdates = new
                                        UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " "
                                                + lastName).build();
                                userProfile.updateProfile(profileUpdates);


                                DatabaseReference user = fbDB.child("users").child(uid);
                                user.setValue(new User(firstName, lastName, schoolEmail));

                                mNextBut.setClickable(true);

                                mFbAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(SignUpActivity.this, mSignUpListener);


                                // TODO: Move user to email verification page (instead of LoginActivity)
                            }

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle("Registration Error")
                                    .setPositiveButton(android.R.string.ok, null);
                            builder.create().show();
                            mNextBut.setClickable(true);
                        }
                    }
                });


    }
}
