package com.cse110.eventlit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;

import com.cse110.utils.OrganizationUtils;

import at.favre.lib.dali.Dali;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView background = (ImageView) findViewById(R.id.background);
        AppCompatButton loginBut = (AppCompatButton) findViewById(R.id.loginButton);
        AppCompatButton signUpBut = (AppCompatButton) findViewById(R.id.signUpButton);

        Dali.create(this.getApplicationContext()).load(R.drawable.home_background).blurRadius(20).into(background);

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startLoginAct = new Intent(MainActivity.this, LoginActivity.class);
                startLoginAct.putExtra("signedup", false);
                startActivity(startLoginAct);
            }
        });

        signUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSignUpAct = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(startSignUpAct);
            }
        });

        //OrganizationUtils.loadOrgs();
    }
}
