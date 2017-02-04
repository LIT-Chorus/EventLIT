package com.cse110.chrous;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.login);
        Button signup = (Button) findViewById(R.id.signup);

        TextInputLayout email = (TextInputLayout) findViewById(R.id.email);
        TextInputLayout password = (TextInputLayout) findViewById(R.id.password);

        TextView backendRet = (TextView) findViewById(R.id.backendReturn);
    }
}
