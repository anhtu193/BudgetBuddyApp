package com.example.budgetbuddyapp.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Editprofile extends AppCompatActivity {

    private EditText fullname, email;
    private ImageButton btn_yes1, btn_yes2;
    private String userID;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        fullname = findViewById(R.id.editTxt_username);
        email = findViewById(R.id.editTxt_email);
        btn_yes1 = findViewById(R.id.btn_yes1);
        btn_yes2 = findViewById(R.id.btn_yes2);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = auth.getCurrentUser().getUid();


        DocumentReference documentReference = fStore.collection("users").document(userID);
        btn_yes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullname.getText().toString().equals("")) {
                    Toast.makeText(Editprofile.this, "Vui lòng nhập tên hoặc email cần sửa đổi!", Toast.LENGTH_SHORT).show();
                } else {
                    String FULLNAME = fullname.getText().toString();
                    documentReference.set("fullname", FULLNAME);
                }
            }
        });

        btn_yes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    Toast.makeText(Editprofile.this, "Vui lòng nhập tên hoặc email cần sửa đổi!", Toast.LENGTH_SHORT).show();
                } else {
                    user.updateEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Editprofile.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

}