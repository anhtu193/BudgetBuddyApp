package com.example.budgetbuddyapp.Profile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.expense.AddNewExpense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Editprofile extends AppCompatActivity {

    private EditText fullname, email;
    private ImageButton btn_yes1, btn_yes2;
    private String userID;
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private ProgressDialog progressDialog;
    private ImageView backButton;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        fullname = findViewById(R.id.editTxt_username);
        btn_yes1 = findViewById(R.id.btn_yes1);
        backButton = findViewById(R.id.backButton);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = auth.getCurrentUser().getUid();


        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                {
                    String nameFromFirestore = documentSnapshot.getString("fullname");
                    fullname.setText(nameFromFirestore);
                }
                else {
                    Log.d(TAG, "Document does not exist");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_yes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullname.getText().toString().equals("")) {
                    Toast.makeText(Editprofile.this, "Vui lòng nhập tên hoặc email cần sửa đổi!", Toast.LENGTH_SHORT).show();
                } else {
                    String FULLNAME = fullname.getText().toString();
                    documentReference.update("fullname", FULLNAME)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Editprofile.this, "Cập nhật tên người dùng thành công!", Toast.LENGTH_SHORT).show();
//                                    Intent refreshIntent = new Intent(Editprofile.this, Navigation.class);
//                                    refreshIntent.putExtra("selectedTab", 3);
//                                    startActivity(refreshIntent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });
    }

}