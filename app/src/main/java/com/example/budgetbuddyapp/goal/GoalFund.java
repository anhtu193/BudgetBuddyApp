package com.example.budgetbuddyapp.goal;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetbuddyapp.BudgetFragment;
import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.UiGoalFundBinding;
import com.example.budgetbuddyapp.expense.ExpenseEdit;
import com.example.budgetbuddyapp.expense.ExpenseEditScreen;
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

public class GoalFund extends AppCompatActivity {

    UiGoalFundBinding binding;
    String userID;
    FirebaseAuth auth;
    FirebaseFirestore fStore;

    String goalID;
    @Override
    protected void onResume() {
        super.onResume();
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
        DocumentReference documentReference = fStore.collection("goals").document(goalID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(ContentValues.TAG, "Listen failed: " + error);
                    return;
                }
                if (value != null && value.exists()) {
                    String goalName = value.getString("goalName");
                    Long goalNumber = value.getLong("goalNumber");
                    Number goalImageIndex = value.getLong("goalImage");
                    int goalImage = goalImageIndex.intValue();
                    String date = value.getString("date");

                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore
                    binding.date.setText(date);
                    binding.goalName.setText(goalName != null ? goalName : "");
                    binding.goalNumber.setText(String.format("%,d", goalNumber));
                    binding.goalImage.setImageResource(categoryImages[goalImage]);
                    Log.d(ContentValues.TAG, "Goal Info updated ID: " + goalID);
                } else {
                    Log.d(ContentValues.TAG, "No such document with ID: " + goalID);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiGoalFundBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        Intent intent = getIntent();
        if (intent != null) {
            goalID = intent.getStringExtra("goalID");
            String goalName = intent.getStringExtra("goalName");
            String date = intent.getStringExtra("date");
            int goalImage = intent.getIntExtra("goalImage", 0);
            Long goalCurrent = intent.getLongExtra("goalCurrent", 0);
            Long goalNumber = intent.getLongExtra("goalNumber", 0);

            // update UI accordingly
            binding.goalName.setText(goalName);
            binding.date.setText(date);
            binding.goalImage.setImageResource(categoryImages[goalImage]);

            ProgressBar progressBar = binding.progressBar;
            int CurrentProgress = (int) ((float) goalCurrent / goalNumber * 100);
            progressBar.setProgress(CurrentProgress);

            if (CurrentProgress >= 100) {
                binding.reachGoal.setVisibility(View.VISIBLE);
            } else {
                binding.reachGoal.setVisibility(View.GONE);
            }

            binding.goalCurrent.setText(String.format("%,d", goalCurrent));
            binding.goalNumber.setText(String.format("%,d", goalNumber));

            TextView balance = findViewById(R.id.balance);
            DocumentReference documentReference = fStore.collection("users").document(auth.getCurrentUser().getUid());
            documentReference.addSnapshotListener(GoalFund.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        Long balanceValue = value.getLong("balance");
                        balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                    }
                }
            });
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
                Intent intent = new Intent(GoalFund.this, GoalEdit.class);
                intent.putExtra("goalID", getIntent().getStringExtra("goalID"));

                GoalFund.this.startActivity(intent);
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalID = getIntent().getStringExtra("goalID");

                String goalCurrentText = binding.inputGoalCurrent.getText().toString();
                String balanceText = binding.balance.getText().toString();

                Long goalCurrent = formatStringToNumber(goalCurrentText);
                Long balance = formatStringToNumber(balanceText);

                if (binding.inputGoalCurrent.getText().toString().isEmpty())
                {
                    Toast.makeText(GoalFund.this, "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
                } else if (goalCurrent > balance) {
                    Toast.makeText(GoalFund.this, "Không đủ số dư!", Toast.LENGTH_SHORT).show();
                } else {
                    updateGoalData(goalID);
                }
            }
        });
    }

    private void deleteGoal() {
        // Get the document ID or reference for the item you want to delete
        String goalID = getIntent().getStringExtra("goalID");

        if (goalID != null) {
            // Get the reference to the document
            DocumentReference documentReference = fStore.collection("goals").document(goalID);

            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Long goalCurrent = documentSnapshot.getLong("goalCurrent");

                            documentReference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document successfully deleted
                                            Toast.makeText(GoalFund.this, "Xóa mục tiêu thành công", Toast.LENGTH_SHORT).show();
                                            updateUserBalance(goalCurrent, "delete");

                                            finish();

                                            // Refresh the BudgetHome activity
//                                            Intent refreshIntent = new Intent(GoalFund.this, Navigation.class);
//                                            refreshIntent.putExtra("selectedTab", 2);
//                                            startActivity(refreshIntent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                            Log.e(TAG, "Error deleting expense", e);
                                            Toast.makeText(GoalFund.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failures
                            Log.e(TAG, "Error getting document", e);
                        }
                    });
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ui_delete_goal, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGoal();
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

    private Long formatStringToNumber(String formattedAmount) {
        String amountString = formattedAmount.replaceAll("[,.\\sđ]", "");
        try {
            Long amount = Long.parseLong(amountString);

            System.out.println("Parsed number: " + amount);

            return amount;
        } catch (NumberFormatException e) {
            // Handle the case where the string cannot be parsed into a number
            System.out.println("Cannot parse string to number: " + e.getMessage());
            return 0L; // Return 0 if parsing fails
        }
    }

    private void updateGoalData(String goalID) {
        Map<String, Object> updatedData = new HashMap<>();
        Long inputGoalCurrent = formatStringToNumber(binding.inputGoalCurrent.getText().toString());
        Long goalCurrent = formatStringToNumber(binding.goalCurrent.getText().toString());

        goalCurrent = goalCurrent + inputGoalCurrent;

        updatedData.put("goalCurrent", goalCurrent);

        fStore.collection("goals").document(goalID).update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful update
                        Log.d(ContentValues.TAG, "Cập nhật quỹ mục tiêu thành công!");
                        updateUserBalance(inputGoalCurrent, "update");

                        // Refresh the BudgetHome activity
//                        Intent refreshIntent = new Intent(GoalFund.this, Navigation.class);
//                        refreshIntent.putExtra("selectedTab", 2);
//                        startActivity(refreshIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed update
                        Log.w(ContentValues.TAG, "Error updating goal document", e);
                    }
                });
    }

    private void updateUserBalance(double goalCurrent, String type) {
        // Đọc số dư hiện tại của người dùng từ Firestore
        DocumentReference userDocRef = fStore.collection("users").document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    double currentBalance = documentSnapshot.getDouble("balance");

                    if (type.equals("update")) {
                        // Cập nhật số dư dựa trên việc tạo quỹ
                        currentBalance = currentBalance - goalCurrent;
                    } else {
                        // Cập nhật số dư dựa trên việc xóa quỹ
                        currentBalance = currentBalance + goalCurrent;
                    }

                    // Cập nhật số dư mới vào Firestore
                    userDocRef.update("balance", currentBalance)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(ContentValues.TAG, "User's balance updated successfully.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(ContentValues.TAG, "Failed to update user's balance: " + e.toString());
                                }
                            });
                } else {
                    Log.d(ContentValues.TAG, "User document does not exist.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(ContentValues.TAG, "Failed to fetch user document: " + e.toString());
            }
        });
    }
}