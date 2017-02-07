package com.cse110.chrous;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Firebase Authutentication and Firebase Authentication state listener
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener fbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.login);
        Button signup = (Button) findViewById(R.id.signup);

        final TextInputLayout email = (TextInputLayout) findViewById(R.id.email);
        final TextInputLayout password = (TextInputLayout) findViewById(R.id.password);

        final TextView backendRet = (TextView) findViewById(R.id.backendReturn);

        // Tracks whether a user is signed in or not
        fbAuth = FirebaseAuth.getInstance();
        fbListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    backendRet.setText("Hi " + user.getDisplayName());
                }
                else {
                    backendRet.setText("Not signed in!");
                }
            }
        };

        // Login Behavior
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (email.getEditText() != null && password.getEditText() != null) {
                    String emailText = email.getEditText().getText().toString();
                    String passwordText = password.getEditText().getText().toString();
                    signIn(emailText, passwordText);
                }
            }
        });

        // TODO #Frontend sets up a sign-up page AND-1/AND-2

    }

    /**
     *
     * @param firstName
     * @param lastName
     * @param schoolEmail
     * @param password
     * @param confirmPass
     */
    protected void signUp(String firstName, String lastName, String schoolEmail,
                          String password, String confirmPass){

        // TODO #Chris AND-6

    }

    /**
     * Performs sign-in validation and logs the user in.
     * @param emailText
     * @param passwordText
     */
    protected void signIn(final String emailText, final String passwordText){
        if (fbAuth != null){
            final TextView backendRet = (TextView) findViewById(R.id.backendReturn);

            // Checks that text was entered in email and password field
            if (emailText == null || emailText.isEmpty()){
                backendRet.setText("Please enter a valid email");
            }
            else if (!emailText.contains("ucsd.edu")){
                backendRet.setText("Please use your UCSD email");
            }
            else if (passwordText == null || passwordText.isEmpty()) {
                backendRet.setText("Please enter a valid password");
            }

            // Now, attempt to sign the user in
            else {
                fbAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Firebase reported error on the server side displayed here
                                if (!task.isSuccessful()) {
                                    backendRet.setText(task.getException().getMessage());
                                }
                                else {
                                    // TODO #Frontend change this to go to new activity with intent
                                    // Current implementation for testing only.
                                    Toast.makeText(MainActivity.this, "Sign in successful!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }


    // App resumes
    @Override
    protected  void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(fbListener);
    }

    // App exit
    @Override
    protected void onStop() {
        super.onStop();
        if (fbListener != null){
            fbAuth.removeAuthStateListener(fbListener);
        }
    }
}
