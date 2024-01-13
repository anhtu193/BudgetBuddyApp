package com.example.budgetbuddyapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText, txtview_incorrect, txt_forgotpassword;
    private Button loginButton;
    private CheckBox checkbox;
    public static final String SHARED_PREFS = "sharePrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.btn_login);
        signupRedirectText = findViewById(R.id.register_redirect);
        checkbox = findViewById(R.id.check_save);
        txtview_incorrect = findViewById(R.id.incorrect);
        txt_forgotpassword = findViewById(R.id.forgot_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        // lưu đăng nhập //auto lưu
                                        if (checkbox.isChecked()) {
                                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("name", "true");
                                            editor.apply();
                                        }

                                        Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, Navigation.class ));
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        txtview_incorrect.setVisibility(View.VISIBLE);
                                        Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Mật khẩu không thể trống");
                    }

                } else if (email.isEmpty()) {
                    loginEmail.setError("Email không thể trống!");
                } else {
                    loginEmail.setError("Email không tồn tại!");
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        txt_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });
    }
}