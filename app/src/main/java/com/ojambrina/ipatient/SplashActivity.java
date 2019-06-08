package com.ojambrina.ipatient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ojambrina.ipatient.UI.home.HomeActivity;
import com.ojambrina.ipatient.UI.login.LoginActivity;
import com.ojambrina.ipatient.utils.AppPreferences;

import static com.ojambrina.ipatient.utils.Constants.SPLASH_DISPLAY_LENGTH;

public class SplashActivity extends AppCompatActivity {

    Context context;
    FirebaseAuth firebaseAuth;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;
        appPreferences = new AppPreferences();
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (appPreferences.getCheckboxLogin()) {
                    if (user != null) {
                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
