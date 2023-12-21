package com.example.budgetbuddyapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.CategoryHome;
import com.example.budgetbuddyapp.transaction.AddNewTransaction;
import com.example.budgetbuddyapp.transaction.RecentTransaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Home extends AppCompatActivity {
    TextView fullName, balance, categoryNumber, goalNumber, budgetNumber, categoryViewAll, transactionViewAll;
    ImageView hideBalance, notification;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    FloatingActionButton addNewTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        transactionViewAll = findViewById(R.id.transactionViewAll);
        fullName = findViewById(R.id.txtViewUserName);
        balance = findViewById(R.id.balance);
        categoryNumber = findViewById(R.id.categoryNumber);
        budgetNumber = findViewById(R.id.budgetNumber);
        goalNumber = findViewById(R.id.goalNumber);
        hideBalance = findViewById(R.id.hideBalance);
        notification = findViewById(R.id.imgViewNotification);
        categoryViewAll = findViewById(R.id.categoryViewAll);
        addNewTransaction = findViewById(R.id.addNewTransaction);

        final boolean[] isPasswordVisible = {false};

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    Long balanceValue = value.getLong("balance");
                    Long categoriesValue = value.getLong("categories");
                    Long budgetsValue = value.getLong("budgets");
                    Long goalsValue = value.getLong("goals");

                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore
                    fullName.setText(fullNameText != null ? fullNameText : "");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                    Log.d(TAG, "User's balance updated: " +  String.format("%,d", balanceValue));
                    categoryNumber.setText(categoriesValue != null ? categoriesValue.toString() : "");
                    budgetNumber.setText(budgetsValue != null ? budgetsValue.toString() : "");
                    goalNumber.setText(goalsValue != null ? goalsValue.toString() : "");
                } else {
                    Log.d(TAG, "No such document");
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
        categoryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, CategoryHome.class));
            }
        });
        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, AddNewTransaction.class));
            }
        });

        transactionViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, RecentTransaction.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    Long balanceValue = value.getLong("balance");
                    Long categoriesValue = value.getLong("categories");
                    Long budgetsValue = value.getLong("budgets");
                    Long goalsValue = value.getLong("goals");

                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore
                    fullName.setText(fullNameText != null ? fullNameText : "");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ": "");
                    Log.d(TAG, "User's balance updated: " +  String.format("%,d", balanceValue));
                    categoryNumber.setText(categoriesValue != null ? categoriesValue.toString() : "");
                    budgetNumber.setText(budgetsValue != null ? budgetsValue.toString() : "");
                    goalNumber.setText(goalsValue != null ? goalsValue.toString() : "");
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }
}