package com.example.budgetbuddyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.budgetbuddyapp.expense.Expense;
import com.example.budgetbuddyapp.expense.ExpenseAdapter;
import com.example.budgetbuddyapp.expense.ExpenseProgress;
import com.example.budgetbuddyapp.goal.AddNewGoal;
import com.example.budgetbuddyapp.goal.GoalAdapter;
import com.example.budgetbuddyapp.goal.Goal;
import com.example.budgetbuddyapp.transaction.RecentTransaction;
import com.example.budgetbuddyapp.transaction.TransactionAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BudgetFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "BudgetFragment";

    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ListView expenseListView, goalListView;
    ArrayList<Expense> expenseList;
    ArrayList<Goal> goalList;
    TextView balance, noExpense, noGoal;
    Button addNewGoal;
    View view;
    com.example.budgetbuddyapp.expense.ExpenseAdapter ExpenseAdapter;
    com.example.budgetbuddyapp.goal.GoalAdapter GoalAdapter;
    private static final int REQUEST_CODE = 1;
    ImageView addExpenseButton;
    public BudgetFragment() {
        // Required empty public constructor
    }

    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
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
        view = inflater.inflate(R.layout.ui_budget, container, false);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        expenseListView = view.findViewById(R.id.expenseListView);
        noExpense = view.findViewById(R.id.noExpense);
        expenseList = new ArrayList<Expense>();

        goalListView = view.findViewById(R.id.goalListView);
        noGoal = view.findViewById(R.id.noGoal);
        goalList = new ArrayList<Goal>();

        balance = view.findViewById(R.id.balance);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Long balanceValue = value.getLong("balance");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                }
            }
        });

        addNewGoal = view.findViewById(R.id.addNewGoal);
        addNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AddNewGoal.class);
                startActivity(intent);
            }
        });

        loadGoal();

        loadExpense();

        return view;
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
//                                processExpenseDocument(document);
                                String expenseID = document.getId();
                                String expenseTime = document.getString("expenseTime");
                                String expenseName = document.getString("expenseName");
                                Number expenseImageIndex = document.getLong("expenseImage");
                                Number expenseCurrentN = document.getLong("expenseCurrent");
                                int expenseCurrent = expenseCurrentN.intValue();
                                int expenseImage = expenseImageIndex.intValue();
                                String categoryID = document.getString("categoryID");
                                // Ensure to use the correct method based on the actual data type in Firestore
                                Object limitValue = document.get("expenseLimit");
                                int expenseLimit = (limitValue instanceof Number) ? ((Number) limitValue).intValue() : 0;

                                if (expenseLimit == 0)
                                {
                                    expenseList.add(new Expense(expenseID, userID, expenseName, expenseImage, categoryID));
                                }
                                else
                                {
                                    expenseList.add(new ExpenseProgress(expenseID, userID, expenseName, expenseImage, categoryID, expenseTime, expenseLimit, expenseCurrent));
                                }
                            }
                        }
                        if (expenseList.isEmpty()) noExpense.setVisibility(View.VISIBLE);
                        else noExpense.setVisibility(View.GONE);

                        ExpenseAdapter = new ExpenseAdapter(view.getContext(), R.layout.item_expense, expenseList);
                        expenseListView.setAdapter(ExpenseAdapter);


                        // Load categories regardless of whether there are expenses or not
//                        fStore.collection("categories")
//                                .whereEqualTo("userID", userID)
//                                .whereEqualTo("categoryType", "Chi tiêu")
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                                        if (task.isSuccessful()) {
//                                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                                processCategoryDocument(document);
//                                            }
//
//                                            // Remove duplicates
////                                            removeDuplicateCategories();
//
//                                            if (ExpenseAdapter == null) {
//                                                // Adapter not initialized, create and set it
//                                                ExpenseAdapter = new ExpenseAdapter(BudgetFragment.this, R.layout.item_expense, expenseList);
//                                                expenseListView.setAdapter(ExpenseAdapter);
//                                            } else {
//                                                // Adapter already initialized, notify data set changed
//                                                ExpenseAdapter.notifyDataSetChanged();
//                                            }
//                                            ExpenseAdapter.notifyDataSetChanged();
//                                        } else {
//                                            Log.e(TAG, "Error getting categories: ", task.getException());
//                                        }
//                                    }
//                                });
                    }
                });
    }


//    private void processCategoryDocument(QueryDocumentSnapshot document) {
//        String categoryID = document.getId();
//
//        String categoryName = document.getString("categoryName");
//        Number categoryImageIndex = document.getLong("categoryImage");
//        int categoryImage = categoryImageIndex.intValue();
//
//        // Check if there is a corresponding expense for this category
//        boolean hasCorrespondingExpense = hasCorrespondingExpense(categoryID);
//
//        // Exclude categories with corresponding expenses
//        if (!hasCorrespondingExpense) {
//            expenseList.add(new Expense(categoryID, userID, categoryName, categoryImage, categoryID));
//        }
//    }

//    private boolean hasCorrespondingExpense(String categoryID) {
//        for (Expense expense : expenseList) {
//            if (expense.getCategoryID().equals(categoryID)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private void processExpenseDocument(QueryDocumentSnapshot expenseDocument) {
//        String expenseID = expenseDocument.getId();
//        String expenseTime = expenseDocument.getString("expenseTime");
//        String expenseName = expenseDocument.getString("expenseName");
//        Number expenseImageIndex = expenseDocument.getLong("expenseImage");
//        int expenseImage = expenseImageIndex.intValue();
//        String categoryID = expenseDocument.getString("categoryID");
//        // Ensure to use the correct method based on the actual data type in Firestore
//        Object limitValue = expenseDocument.get("expenseLimit");
//        int expenseLimit = (limitValue instanceof Number) ? ((Number) limitValue).intValue() : 0;
//
//        if (expenseLimit == 0)
//        {
//            expenseList.add(new Expense(expenseID, userID, expenseName, expenseImage, categoryID));
//        }
//        else
//        {
//            expenseList.add(new ExpenseProgress(expenseID, userID, expenseName, expenseImage, categoryID, expenseTime, expenseLimit));
//        }
////        fStore.collection("categories")
////                .whereEqualTo("userID", userID)
////                .get()
////                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////
////                        if (task.isSuccessful()) {
////
////                            for (QueryDocumentSnapshot categoryDocument : task.getResult()) {
////                                if (categoryID.equals(categoryDocument.getId())) {
////                                    String expenseName = categoryDocument.getString("categoryName");
////                                    Number categoryImageIndex = categoryDocument.getLong("categoryImage");
////                                    int expenseImage = categoryImageIndex.intValue();
////                                    expenseList.add(new ExpenseProgress(expenseID, userID, expenseName, expenseImage, categoryID, expenseTime, expenseLimit));
////                                }
////                            }
////                        } else {
////                            Log.e(TAG, "Error getting categories: ", task.getException());
////                        }
////                    }
////                });
//    }

    private void deleteExpenseWhenEndMonth() {

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM - yyyy", Locale.getDefault());
        String currentMonthYear = dateFormat.format(calendar.getTime());

        Map<String, Object> updates = new HashMap<>();
        updates.put("expenseLimit", 0);
        updates.put("expenseCurrent", 0);
        updates.put("expenseTime", "Chưa đặt giới hạn");

        // Thực hiện truy vấn để lấy các expense cần xóa
        fStore.collection("expenses")
                .whereNotEqualTo("expenseTime", currentMonthYear)
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua danh sách expense và xóa chúng
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update(updates);
                        }
                        Log.w(TAG, "Đã xóa các giới hạn vì đã hết tháng");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting expenses: " + e.getMessage());
                    }
                });
    }

    private void loadGoal() {
        fStore.collection("goals")
                .whereEqualTo("userID", userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        goalList.clear(); // Clear old data before updating

                        // Check if there are expenses
                        if (!value.isEmpty()) {
                            for (QueryDocumentSnapshot document : value) {
//                                processGoalDocument(document);
                                String goalID = document.getId();
                                String goalName = document.getString("goalName");
                                Number goalImageIndex = document.getLong("goalImage");
                                int goalImage = goalImageIndex.intValue();
                                String date = document.getString("date");

                                Long goalNumber = document.getLong("goalNumber");
                                Long goalCurrent = document.getLong("goalCurrent");

                                goalList.add(new Goal(goalID, userID, goalName, goalCurrent, goalNumber, goalImage, date));
                            }
                        }
                        if (goalList.isEmpty()) noGoal.setVisibility(View.VISIBLE);
                        else noGoal.setVisibility(View.GONE);

                        GoalAdapter = new GoalAdapter(BudgetFragment.this, R.layout.item_goal, goalList);
                        goalListView.setAdapter(GoalAdapter);
                    }
                });
    }

    private void processGoalDocument(QueryDocumentSnapshot goalDocument) {
        String goalID = goalDocument.getId();
        String goalName = goalDocument.getString("goalName");
        Number goalImageIndex = goalDocument.getLong("goalImage");
        int goalImage = goalImageIndex.intValue();
        String date = goalDocument.getString("date");

        Long goalNumber = goalDocument.getLong("goalNumber");
        Long goalCurrent = goalDocument.getLong("goalCurrent");

        goalList.add(new Goal(goalID, userID, goalName, goalCurrent, goalNumber, goalImage, date));
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}