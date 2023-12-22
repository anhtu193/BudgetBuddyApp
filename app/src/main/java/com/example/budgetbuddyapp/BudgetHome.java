package com.example.budgetbuddyapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM - yyyy", Locale.getDefault());
                        String currentMonthYear = dateFormat.format(calendar.getTime());

                        // Check if there are expenses
                        if (!value.isEmpty()) {
                            for (QueryDocumentSnapshot document : value) {
                                if (!document.getString("expenseTime").equals(currentMonthYear)) {
                                    deleteExpenseWhenEndMonth();
                                }
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
        boolean hasCorrespondingExpense = hasCorrespondingExpense(categoryID);

        // Exclude categories with corresponding expenses
        if (!hasCorrespondingExpense) {
            expenseList.add(new Expense(categoryID, userID, categoryName, categoryImage, categoryID));
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

    private boolean hasCorrespondingExpense(String categoryID) {
        for (Expense expense : expenseList) {
            if (expense.getCategoryID().equals(categoryID)) {
                return true;
            }
        }
        return false;
    }

    private void processExpenseDocument(QueryDocumentSnapshot expenseDocument) {
        String expenseID = expenseDocument.getId();
        String expenseTime = expenseDocument.getString("expenseTime");

        // Ensure to use the correct method based on the actual data type in Firestore
        Object limitValue = expenseDocument.get("expenseLimit");
        int expenseLimit = (limitValue instanceof Number) ? ((Number) limitValue).intValue() : 0;

        String categoryID = expenseDocument.getString("categoryID");

        fStore.collection("categories")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot categoryDocument : task.getResult()) {
                                if (categoryID.equals(categoryDocument.getId())) {
                                    String expenseName = categoryDocument.getString("categoryName");
                                    Number categoryImageIndex = categoryDocument.getLong("categoryImage");
                                    int expenseImage = categoryImageIndex.intValue();
                                    expenseList.add(new ExpenseProgress(expenseID, userID, expenseName, expenseImage, categoryID, expenseTime, expenseLimit));
                                }
                            }
                        } else {
                            Log.e(TAG, "Error getting categories: ", task.getException());
                        }
                    }
                });
    }

    private void deleteExpenseWhenEndMonth() {

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM - yyyy", Locale.getDefault());
        String currentMonthYear = dateFormat.format(calendar.getTime());

        // Thực hiện truy vấn để lấy các expense cần xóa
        fStore.collection("expenses")
                .whereEqualTo("expenseTime", currentMonthYear)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua danh sách expense và xóa chúng
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting expenses: " + e.getMessage());
                    }
                });
    }

    // Phần khung của phần khi thêm mới giao dịch thì sẽ xuất hiện giới hạn chi tiêu
}