package com.cse110.eventlit;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AppCompatButton mResetBut;
    private TextInputLayout mEmailEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initializes Global Vars
        mResetBut = (AppCompatButton) findViewById(R.id.reset);
        mEmailEntry = (TextInputLayout) findViewById(R.id.email);

        mResetBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Backend send reset link to user

                mResetBut.setClickable(false);

                if (checkEmail()) {

                    final AlertDialog dialog = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.AlertDialogCustom)
                            .setTitle("Reset Confirmation")
                            .setMessage("A reset link has been sent to " +
                                    mEmailEntry.getEditText().getText().toString())
                            .setCancelable(false).create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            mResetBut.setClickable(true);
                        }
                    });

                    LitUtils.hideSoftKeyboard(ForgotPasswordActivity.this, mEmailEntry);

                    dialog.show();
                    mEmailEntry.getEditText().setText("");
                } else {
                    mResetBut.setClickable(true);
                }
            }
        });
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
