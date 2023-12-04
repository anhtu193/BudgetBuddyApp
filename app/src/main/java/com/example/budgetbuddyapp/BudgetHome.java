package com.example.budgetbuddyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BudgetHome extends AppCompatActivity {

    private static final String TAG = "BudgetHome";

    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ListView expenseListView;
    ArrayList<Expense> expenseList;
    ExpenseAdapter ExpenseAdapter;
    ImageView addExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_budget);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        expenseListView = findViewById(R.id.expenseListView);
        expenseList = new ArrayList<>();

        TextView balance = findViewById(R.id.balance);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Long balanceValue = value.getLong("balance");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                }
            }
        });

        loadExpense();
    }

    private void loadExpense() {
        fStore.collection("expenses")
                .whereEqualTo("userID", userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        expenseList.clear(); // Clear old data before updating

                        // Check if there are expenses
                        if (!value.isEmpty()) {
                            for (QueryDocumentSnapshot document : value) {
                                processExpenseDocument(document);
                            }
                        }

                        // Load categories regardless of whether there are expenses or not
                        fStore.collection("categories")
                                .whereEqualTo("userID", userID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                processCategoryDocument(document);
                                            }

                                            // Remove duplicates
//                                            removeDuplicateCategories();

                                            if (ExpenseAdapter == null) {
                                                // Adapter not initialized, create and set it
                                                ExpenseAdapter = new ExpenseAdapter(BudgetHome.this, R.layout.item_expense, expenseList);
                                                expenseListView.setAdapter(ExpenseAdapter);
                                            } else {
                                                // Adapter already initialized, notify data set changed
                                                ExpenseAdapter.notifyDataSetChanged();
                                            }
                                            ExpenseAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.e(TAG, "Error getting categories: ", task.getException());
                                        }
                                    }
                                });
                    }
                });
    }

    private void processCategoryDocument(QueryDocumentSnapshot document) {
        String categoryID = document.getId();
        String categoryName = document.getString("categoryName");
        Number categoryImageIndex = document.getLong("categoryImage");
        int categoryImage = categoryImageIndex.intValue();

        // Check if there is a corresponding expense for this category
        boolean hasCorrespondingExpense = hasCorrespondingExpense(categoryName);

        // Exclude categories with corresponding expenses
        if (!hasCorrespondingExpense) {
            expenseList.add(new Expense(categoryID, userID, categoryName, categoryImage));
        }
    }

//    private void removeDuplicateCategories() {
//        Set<String> categoryNames = new HashSet<>();
//        List<Expense> filteredList = new ArrayList<>();
//
//        for (Expense expense : expenseList) {
//            // If the category name is not already in the set, add it to the set and the filtered list
//            if (categoryNames.add(expense.getExpenseName())) {
//                filteredList.add(expense);
//            }
//        }
//
//        expenseList.clear();
//        expenseList.addAll(filteredList);
//    }

    private boolean hasCorrespondingExpense(String categoryName) {
        for (Expense expense : expenseList) {
            if (expense.getExpenseName().equals(categoryName)) {
                return true;
            }
        }
        return false;
    }

    private void processExpenseDocument(QueryDocumentSnapshot document) {
        String expenseID = document.getId();
        String expenseName = document.getString("expenseName");
        Number expenseImageIndex = document.getLong("expenseImage");
        String expenseTime = document.getString("expenseTime");

        // Ensure to use the correct method based on the actual data type in Firestore
        Object limitValue = document.get("expenseLimit");

        int expenseLimit = (limitValue instanceof Number) ? ((Number) limitValue).intValue() : 0;
        int expenseImage = expenseImageIndex.intValue();

        // Handle the expense data as needed
        expenseList.add(new ExpenseProgress(expenseID, userID, expenseName, expenseImage, expenseTime, expenseLimit));
    }
}