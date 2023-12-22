package com.example.budgetbuddyapp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.budgetbuddyapp.R;

public class ForgotPasswordSuccessfully extends AppCompatActivity {

    Button btn_loginagain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_successfullly);

        btn_loginagain = findViewById(R.id.btn_loginagain);
        btn_loginagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordSuccessfully.this, Login.class);
                startActivity(intent);
            }
        });
    }
}