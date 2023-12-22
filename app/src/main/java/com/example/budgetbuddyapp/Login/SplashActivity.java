package com.example.budgetbuddyapp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    //Kiểm tra đã đăng nhập hay chưa
    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, Navigation.class);
            startActivity(intent);
        }
        finish();
    }
}