package com.example.budgetbuddyapp.expense;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetbuddyapp.BudgetFragment;
import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.UiEditExpenseBinding;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExpenseEdit extends AppCompatActivity {

    UiEditExpenseBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;

    int[] categoryImages;
    Boolean[] isOnly;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiEditExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        categoryImages = new int[]{R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        isOnly = new Boolean[]{true};

        Intent intent = getIntent();
        if (intent != null) {
            String expenseID = intent.getStringExtra("expenseID");

            // Fetch expense details from Firestore using expenseID and populate UI
            fetchExpenseDetails(expenseID);
        }

        binding.spinnerAddExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("Chỉ tháng này")) {
                    isOnly[0] = true;
                }
//                else if (item.equals("Tất cả các tháng")) {
//                    isOnly[0] = false;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> spinner_choice = new ArrayList<>();
        spinner_choice.add("Chỉ tháng này");
//        spinner_choice.add("Tất cả các tháng");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_choice);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerAddExpense.setAdapter(adapter);

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the expense details in Firestore
                String expenseID = getIntent().getStringExtra("expenseID");
                if (TextUtils.isEmpty(binding.inputExpenseLimit.getText()) || !TextUtils.isDigitsOnly(binding.inputExpenseLimit.getText()))
                {
                    Toast.makeText(ExpenseEdit.this, "Vui lòng nhập giới hạn chi tiêu, giới hạn chi tiêu là một số!", Toast.LENGTH_SHORT).show();
                } else {
                    updateExpenseDetails(expenseID);
                }
            }
        });
    }

    private void fetchExpenseDetails(String expenseID) {
         fStore.collection("expenses").document(expenseID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()) {
                     DocumentSnapshot document = task.getResult();
                     if (document.exists()) {
                         // Populate UI with existing details
                         binding.expenseName.setText(document.getString("expenseName"));

                         int expenseImage = document.getLong("expenseImage").intValue();
                         binding.expenseImage.setImageResource(categoryImages[expenseImage]);

                         int expenseLimit = document.getLong("expenseLimit").intValue();
                         binding.inputExpenseLimit.setText(String.valueOf(expenseLimit));

                         TextView balance = findViewById(R.id.balance);
                         DocumentReference documentReference = fStore.collection("users").document(auth.getCurrentUser().getUid());
                         documentReference.addSnapshotListener(ExpenseEdit.this, new EventListener<DocumentSnapshot>() {
                             @Override
                             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                 if (value != null && value.exists()) {
                                     Long balanceValue = value.getLong("balance");
                                     balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                                 }
                             }
                         });
                     } else {
                         Log.d(TAG, "No such document");
                     }
                 } else {
                     Log.d(TAG, "get failed with ", task.getException());
                 }
             }
         });
    }

    private void updateExpenseDetails(String expenseID) {
        // Get the updated limit and selected spinner value
        String updatedLimitText = binding.inputExpenseLimit.getText().toString();
        int updatedLimit = Integer.parseInt(updatedLimitText);
        String selectedSpinnerValue = binding.spinnerAddExpense.getSelectedItem().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("expenseLimit", updatedLimit);


        fStore.collection("expenses").document(expenseID).update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful updat
                        Log.d(TAG, "Cập nhật giới hạn chi tiêu thành công!");

                        // Refresh the BudgetHome activity

                        finish();
                    }
                })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         // Handle failed update
                         Log.w(TAG, "Error updating expense document", e);
                     }
                 });
    }
}