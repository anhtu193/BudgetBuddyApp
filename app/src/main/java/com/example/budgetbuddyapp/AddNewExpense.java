package com.example.budgetbuddyapp;

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

import com.example.budgetbuddyapp.databinding.UiAddExpenseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AddNewExpense extends AppCompatActivity {

    UiAddExpenseBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        final Boolean[] isOnly = {true};

        Intent intent = getIntent();
        if (intent != null) {
            String expenseName = intent.getStringExtra("expenseName");
            int expenseImage = intent.getIntExtra("expenseImage", 0);

            // update UI accordingly
            binding.expenseName.setText(expenseName);
            binding.expenseImage.setImageResource(categoryImages[expenseImage]);

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
        }

        binding.spinnerAddExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("Chỉ tháng này"))
                {
                    isOnly[0] = true;
                } else if (item.equals("Tất cả các tháng"))
                {
                    isOnly[0] = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> spinner_choice = new ArrayList<>();
        spinner_choice.add("Chỉ tháng này");
        spinner_choice.add("Tất cả các tháng");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_choice);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerAddExpense.setAdapter(adapter);

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.inputExpenseLimit.getText()) || !TextUtils.isDigitsOnly(binding.inputExpenseLimit.getText()))
                {
                    Toast.makeText(AddNewExpense.this, "Vui lòng nhập giới hạn chi tiêu, giới hạn chi tiêu là một số!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userID", userID);
                    data.put("expenseName" , binding.expenseName.getText());

                    String expenseLimitText = binding.inputExpenseLimit.getText().toString();
                    int expenseLimitValue = Integer.parseInt(expenseLimitText);
                    data.put("expenseLimit", expenseLimitValue);

                    Intent intent = getIntent();
                    int expenseImage = intent.getIntExtra("expenseImage", 0);
                    data.put("expenseImage" , expenseImage);

                    if (isOnly[0] == true)
                    {
                        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // 1 - 12
                        int year = Calendar.getInstance().get(Calendar.YEAR); // 2023
                        data.put("expenseTime", month + " - " + year);
                    } else
                    {
                        data.put("expenseTime", "Tất cả các tháng");
                    }
                    fStore.collection("expenses").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: expense cap created with ID: " + documentReference.getId());

                            // Refresh the BudgetHome activity
                            Intent refreshIntent = new Intent(AddNewExpense.this, BudgetHome.class);
                            refreshIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(refreshIntent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: +" + e.toString());
                        }
                    });
                }
            }
        });
    }
}