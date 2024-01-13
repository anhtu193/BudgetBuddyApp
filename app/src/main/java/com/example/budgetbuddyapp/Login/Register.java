package com.example.budgetbuddyapp.Login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private EditText register_fullname, retype_password;
    private FirebaseFirestore fStore;
    private String userID;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.register_email);
        signupPassword = findViewById(R.id.register_password);
        signupButton = findViewById(R.id.btn_register);
        loginRedirectText = findViewById(R.id.login_redirect);
        register_fullname = findViewById(R.id.register_fullname);
        backButton = findViewById(R.id.btn_backtologin);
        retype_password = findViewById(R.id.register_retype_password);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String retype_pass = retype_password.getText().toString().trim();
                String fullname = register_fullname.getText().toString();
                if (user.isEmpty()){
                    signupEmail.setError("Email không thể trống!");
                }
                if(pass.isEmpty()){
                    signupPassword.setError("Mật khẩu không thể trống!");
                }
                if (!pass.equals(retype_pass))
                {
                    signupPassword.setError("Mật khẩu không trùng nhau!");
                    retype_password.setError("Mật khẩu không trùng nhau!");
                }
                else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnSuccessListener(authResult -> {
                        Toast.makeText(getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            userID = auth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> data = new HashMap<>();
                            data.put("fullname", fullname);
                            data.put("balance", 0);
                            data.put("userID", userID);
                            documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user created with UID: " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: +" + e.toString());
                                }
                            });
                            startActivity(new Intent(Register.this, Login.class));
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Register.this, "Đăng ký thất bại" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
//                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener((task -> {
//                        if(task.isSuccessful()){
//                            Toast.makeText(getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
//                            userID = auth.getCurrentUser().getUid();
//                            DocumentReference documentReference = fStore.collection("users").document(userID);
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("fullname", fullname);
//                            data.put("balance", 0);
//                            data.put("categories", 0);
//                            data.put("budgets", 0);
//                            data.put("goals",0);
//                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "onSuccess: user created with UID: " + userID);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: +" + e.toString());
//                                }
//                            });
//                            startActivity(new Intent(Register.this, Login.class));
//                        } else{
//
//                        }
//                    }));
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}