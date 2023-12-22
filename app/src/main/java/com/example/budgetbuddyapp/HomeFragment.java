package com.example.budgetbuddyapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetbuddyapp.categories.CategoryHome;
import com.example.budgetbuddyapp.transaction.RecentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView fullName, balance, categoryNumber, goalNumber, budgetNumber, categoryViewAll, transactionViewAll;
    ImageView hideBalance, notification;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, container, false);

        fullName = view.findViewById(R.id.txtViewUserName);
        balance = view.findViewById(R.id.balance);
        categoryNumber = view.findViewById(R.id.categoryNumber);
        budgetNumber = view.findViewById(R.id.budgetNumber);
        goalNumber = view.findViewById(R.id.goalNumber);
        hideBalance = view.findViewById(R.id.hideBalance);
        notification = view.findViewById(R.id.imgViewNotification);
        categoryViewAll = view.findViewById(R.id.categoryViewAll);
        transactionViewAll = view.findViewById(R.id.transactionViewAll);

        final boolean[] isPasswordVisible = {false};

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                startActivity(new Intent(getContext(), CategoryHome.class));
            }
        });


        transactionViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RecentTransaction.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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