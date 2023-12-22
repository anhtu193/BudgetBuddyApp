package com.example.budgetbuddyapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {

    private Button btn_countinue;
    private ImageButton btn_backtologin;
    private TextView txtview_loginemail;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();

        btn_countinue = findViewById(R.id.btn_countinue);
        btn_backtologin = findViewById(R.id.btn_backtologin2);
        txtview_loginemail = findViewById(R.id.login_email2);

        String emailAddress = txtview_loginemail.getText().toString();
        btn_countinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(ForgotPassword.this, ForgotPasswordSuccessfully.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });
        btn_backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });
    }
}