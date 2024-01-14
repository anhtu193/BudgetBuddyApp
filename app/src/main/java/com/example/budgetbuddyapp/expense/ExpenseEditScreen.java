package com.example.budgetbuddyapp.expense;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetbuddyapp.BudgetFragment;
import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.UiExpenseEditScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExpenseEditScreen extends AppCompatActivity {

    UiExpenseEditScreenBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String expenseID;
    int expenseCurrent;
    @Override
    protected void onResume() {
        super.onResume();
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
        DocumentReference documentReference = fStore.collection("expenses").document(expenseID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(ContentValues.TAG, "Listen failed: " + error);
                    return;
                }
                if (value != null && value.exists()) {
                    Number expense = value.getLong("expenseLimit");
                    int expenseLimit = expense.intValue();

                    binding.expenseLimitRemaining.setText(String.format("%,d", expenseLimit));
                    binding.expenseLimit.setText(String.format("%,d", expenseLimit) + " đ");
                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore

                    Log.d(ContentValues.TAG, "Expense limit updated ID: " + expenseID);
                } else {
                    Log.d(ContentValues.TAG, "No such document with ID: " + expenseID);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiExpenseEditScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        Intent intent = getIntent();
        if (intent != null) {
            expenseID = intent.getStringExtra("expenseID");
            String expenseName = intent.getStringExtra("expenseName");
            String expenseTime = intent.getStringExtra("expenseTime");
            int expenseImage = intent.getIntExtra("expenseImage", 0);
            int expenseLimit = intent.getIntExtra("expenseLimit", 0);
            expenseCurrent = intent.getIntExtra("expenseCurrent", 0);
            if (!expenseTime.equals("Tất cả các tháng")) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();

                // Get the last day of the current month
                int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                // Get the current day of the month
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                // Calculate the remaining days
                int remainingDays = lastDayOfMonth - currentDay;

                binding.expenseTimeRemaining.setText("Còn lại " + remainingDays + " ngày");

            } else {
                binding.expenseTimeRemaining.setText("");
            }

            // update UI accordingly
            binding.expenseName.setText(expenseName);
            binding.expenseTime.setText(expenseTime);
            binding.expenseImage.setImageResource(categoryImages[expenseImage]);

            ProgressBar progressBar = binding.progressBar;
            int CurrentProgress = (int) ((float) expenseCurrent / expenseLimit * 100);

            if (CurrentProgress >= 100) {
                CurrentProgress = 100;
                progressBar.getProgressDrawable().setColorFilter(
                        Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                binding.expenseCurrent.setTextColor(Color.RED);
                binding.reachLimit.setVisibility(View.VISIBLE);
            } else {
                binding.expenseCurrent.setTextColor(Color.BLACK);
                binding.reachLimit.setVisibility(View.GONE);
            }

            progressBar.setProgress(CurrentProgress);

            binding.expenseLimit.setText(String.format("%,d", expenseLimit) + " đ");
            binding.expenseCurrent.setText(String.format("%,d", expenseCurrent));
            binding.expenseLimitRemaining.setText(String.format("%,d", expenseLimit));
        }

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click here, e.g., start a new activity
                Intent intent = new Intent(ExpenseEditScreen.this, ExpenseEdit.class);
                intent.putExtra("expenseID", getIntent().getStringExtra("expenseID"));

                ExpenseEditScreen.this.startActivity(intent);
            }
        });
    }

    private void deleteExpense() {
        // Get the document ID or reference for the item you want to delete
        String expenseID = getIntent().getStringExtra("expenseID");

        if (expenseID != null) {
            // Get the reference to the document
            DocumentReference documentReference = fStore.collection("expenses").document(expenseID);

            //Reset expense limit to 0
            Map<String, Object> updates = new HashMap<>();
            updates.put("expenseLimit", 0);
            updates.put("expenseCurrent", 0);
            updates.put("expenseTime", "Vì đã xóa nên không có");

            fStore.collection("expenses").document(expenseID).update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(ContentValues.TAG, "Xóa/Reset giới hạn chi tiêu thành công! ID:" +expenseID);
//                            Intent refreshIntent = new Intent(ExpenseEditScreen.this, Navigation.class);
//                            refreshIntent.putExtra("selectedTab", 2);
//                            startActivity(refreshIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(ContentValues.TAG, "Lỗi khi reset giới hạn", e);
                        }
                    });

            // Delete the document
//            documentReference.delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Document successfully deleted
//                            Toast.makeText(ExpenseEditScreen.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
//
//                            finish();
//
//                            // Refresh the BudgetHome activity
//                            Intent refreshIntent = new Intent(ExpenseEditScreen.this, BudgetFragment.class);
//                            refreshIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(refreshIntent);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Handle the error
//                            Log.e(TAG, "Error deleting expense", e);
//                            Toast.makeText(ExpenseEditScreen.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
//                        }
//                    });
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ui_delete_expense, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteExpense();
                dialog.dismiss();
            }
        });

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}