package com.example.budgetbuddyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Home extends AppCompatActivity {
    TextView fullName, balance, categoryNumber, goalNumber, budgetNumber;
    ImageView hideBalance, notification, addNewTransaction;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        fullName = findViewById(R.id.txtViewUserName);
        balance = findViewById(R.id.balance);
        categoryNumber = findViewById(R.id.categoryNumber);
        budgetNumber = findViewById(R.id.budgetNumber);
        goalNumber = findViewById(R.id.goalNumber);
        hideBalance = findViewById(R.id.hideBalance);
        notification = findViewById(R.id.imgViewNotification);
        addNewTransaction = findViewById(R.id.addNewTransaction);

        final boolean[] isPasswordVisible = {false};

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    Long balanceValue = value.getLong("balance");
                    Long categoriesValue = value.getLong("categories");
                    Long budgetsValue = value.getLong("budgets");
                    Long goalsValue = value.getLong("goals");

                    fullName.setText(fullNameText != null ? fullNameText : "");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) : "");
                    categoryNumber.setText(categoriesValue != null ? categoriesValue.toString() : "");
                    budgetNumber.setText(budgetsValue != null ? budgetsValue.toString() : "");
                    goalNumber.setText(goalsValue != null ? goalsValue.toString() : "");
                }
            }
        });

        hideBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible[0]) {
                    // Nếu password đang hiển thị, chuyển về dạng ẩn
                    balance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible[0] = false;
                } else {
                    // Nếu password đang ẩn, chuyển về dạng hiển thị
                    balance.setTransformationMethod(null);
                    isPasswordVisible[0] = true;
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CategoryHome.class ));
            }
        });

        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, BudgetHome.class ));
            }
        });

        fullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, ReportHome.class ));
            }
        });
    }
}