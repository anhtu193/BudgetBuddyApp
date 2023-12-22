package com.example.budgetbuddyapp.Profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.budgetbuddyapp.Login.Login;
import com.example.budgetbuddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {

    private TextView fullname, email;
    private ConstraintLayout editprofile, changepassword, logout;
    String userID;
    public static final String SHARED_PREFS = "sharePrefs";
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        fullname = findViewById(R.id.txtViewUserName);
        email = findViewById(R.id.txtView_changepassword);
        editprofile = findViewById(R.id.editprofile);
        changepassword = findViewById(R.id.changepassword);
        logout = findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        //Hiển thị tên, email
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    fullname.setText(fullNameText != null ? fullNameText : "");
                }
            }
        });

        String Email = user.getEmail();
        email.setText(Email != null ? Email: "");


        //Sửa thông tin
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Editprofile.class);
                startActivity(intent);
            }
        });

        //Đăng xuất
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }


}