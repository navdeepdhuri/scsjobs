package com.scsjobspunjab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            // Check if user is logged in using FirebaseAuth
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User is logged in, proceed to MainPageActivity
                startActivity(new Intent(this, MainPageActivity.class));
            } else {
                // User is not logged in, proceed to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish(); // Prevent going back to MainActivity by pressing back button
        }, SPLASH_TIME_OUT);
    }
}
