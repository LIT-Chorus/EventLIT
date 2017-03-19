package com.cse110.eventlit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.cse110.utils.LitUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AppCompatButton mResetBut;
    private TextInputLayout mEmailEntry;
    private FirebaseAuth fbAuth;
    private OnCompleteListener onPasswordResetSent;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initializes Global Vars
        mResetBut = (AppCompatButton) findViewById(R.id.reset);
        mEmailEntry = (TextInputLayout) findViewById(R.id.email);

        // Initial fb auth object
        fbAuth = FirebaseAuth.getInstance();

        // Setup an on complete listener
        onPasswordResetSent = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    final AlertDialog dialog = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogCustom)
                            .setTitle("Reset Failed")
                            .setMessage("User with email " +
                                    mEmailEntry.getEditText().getText().toString() + " does not exist")
                            .setCancelable(false).create();
                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            mResetBut.setClickable(true);
                        }
                    });

                    mDialog.hide();

                    LitUtils.hideSoftKeyboard(ForgotPasswordActivity.this, mEmailEntry);

                    dialog.show();
                    mEmailEntry.getEditText().setText("");
                } else {
                    final AlertDialog dialog = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogCustom)
                            .setTitle("Reset Confirmation")
                            .setMessage("A reset link has been sent to " +
                                    mEmailEntry.getEditText().getText().toString())
                            .setCancelable(false).create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mResetBut.setClickable(true);
                            dialog.dismiss();
                        }
                    });

                    mDialog.hide();

                    LitUtils.hideSoftKeyboard(ForgotPasswordActivity.this, mEmailEntry);

                    dialog.show();
                    mEmailEntry.getEditText().setText("");
                }
            }
        };


        mResetBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResetBut.setClickable(false);
                if (checkEmail()) {
                    String email = mEmailEntry.getEditText().getText().toString();
                    mDialog.show();
                    fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(onPasswordResetSent);
                } else {
                    mResetBut.setClickable(true);
                }
            }
        });

        // Set up Progress Dialog
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Reset Password Request");
        mDialog.setMessage("Submitting request to reset your password!");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    protected boolean checkEmail() {
        EditText emailEditText = mEmailEntry.getEditText();

        if (emailEditText.getError() != null) return false;

        String emailText = emailEditText.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailEditText.setError("Invalid Email ID");
        } else if (!emailText.substring(emailText.length() - 9, emailText.length()).equals("@ucsd.edu")) {
            emailEditText.setError("Please use your UCSD Email!");
        } else {
            return true;
        }

        return false;
    }
}
