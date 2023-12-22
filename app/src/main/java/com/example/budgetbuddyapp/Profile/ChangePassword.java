package com.example.budgetbuddyapp.Profile;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends AppCompatActivity {

    private EditText editTxt_password1, editTxt_password2;
    private Button btn_continue;
    private ImageButton btn_back;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        editTxt_password1 = findViewById(R.id.editTxt_password1);
        editTxt_password2 = findViewById(R.id.editTxt_password2);
        btn_continue = findViewById(R.id.btn_continue2);
        btn_back = findViewById(R.id.btn_backto_profile2);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = auth.getCurrentUser().getUid();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTxt_password1.getText().toString() == editTxt_password2.getText().toString()) {
                    String newPassword = editTxt_password1.getText().toString();
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        Intent refreshIntent = new Intent(ChangePassword.this, Navigation.class);
                                        refreshIntent.putExtra("selectedTab", 3);
                                        startActivity(refreshIntent);
                                    }
                                }
                            });
                }
            }
        });
    }
}