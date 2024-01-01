package com.example.budgetbuddyapp.goal;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.Profile.Editprofile;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.AddNewCategory;
import com.example.budgetbuddyapp.categories.CategoryHome;
import com.example.budgetbuddyapp.databinding.UiEditGoalBinding;
import com.example.budgetbuddyapp.expense.ExpenseEdit;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GoalEdit extends AppCompatActivity {

    UiEditGoalBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    TextView date;
    Calendar calendar;
    int[] iconURL = {0};
    int[] goalImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
            R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
            R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiEditGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        date = findViewById(R.id.date);
        calendar = Calendar.getInstance();

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if (intent != null) {
            String goalID = intent.getStringExtra("goalID");

            fetchGoalData(goalID);
        }

        // Hiển thị các biểu tượng của người dùng
        GoalGridViewAdapter gridAdapter = new GoalGridViewAdapter(GoalEdit.this, goalImages);
        binding.gridview.setAdapter(gridAdapter);

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                binding.goalImage.setImageResource(goalImages[position]);
                iconURL[0] = position;
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String documentId;
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalID = getIntent().getStringExtra("goalID");
                if (binding.inputGoalName.getText().toString().equals(""))
                {
                    Toast.makeText(GoalEdit.this, "Vui lòng nhập tên mục tiêu!", Toast.LENGTH_SHORT).show();
                } else if (binding.inputGoalNumber.getText().toString().equals("")) {
                    Toast.makeText(GoalEdit.this, "Vui lòng nhập số tiền mục tiêu!", Toast.LENGTH_SHORT).show();
                } else {
                    updateGoalData(goalID);
                }
            }
        });
    }

    private Long formatStringToNumber(String formatedAmount) {
        String amountString = formatedAmount.replaceAll("[,.]", "");
        try {
            Long amount = Long.parseLong(amountString); // Chuyển đổi chuỗi thành số
            // Nếu bạn muốn số nguyên, sử dụng:
            // int parsedInt = Integer.parseInt(cleanString);

            // Sử dụng số parsed (hoặc parsedInt) để thực hiện các thao tác xử lý
            System.out.println("Số đã parse: " + amount);
            return amount;
            // Do whatever you want with 'parsed' here...
        } catch (NumberFormatException e) {
            // Xử lý nếu chuỗi không thể parse thành số
            System.out.println("Không thể parse chuỗi thành số: " + e.getMessage());
            Long a = Long.valueOf(0);
            return a;
        }
    }

    public void showDatePickerDialog() {
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateInView();
        }
    };

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        date.setText(sdf.format(calendar.getTime()));
    }

    private void fetchGoalData(String goalID) {
        fStore.collection("goals").document(goalID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Populate UI with existing details
                        binding.inputGoalName.setText(document.getString("goalName"));
                        binding.date.setText(document.getString("date"));

                        int goalImage = document.getLong("goalImage").intValue();
                        binding.goalImage.setImageResource(goalImages[goalImage]);
                        iconURL[0] = goalImage;

                        Long goalNumber = document.getLong("goalNumber");
                        binding.inputGoalNumber.setText(goalNumber.toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateGoalData(String goalID) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("goalName", binding.inputGoalName.getText().toString());
        updatedData.put("goalImage", iconURL[0]);
        Long inputGoalNumber = formatStringToNumber(binding.inputGoalNumber.getText().toString());
        updatedData.put("goalNumber", inputGoalNumber);
        updatedData.put("date", date.getText().toString());

        fStore.collection("goals").document(goalID).update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful updat
                        Log.d(TAG, "Cập nhật mục tiêu thành công!");
                        Toast.makeText(GoalEdit.this, "Cập nhật mục tiêu thành công!", Toast.LENGTH_SHORT).show();
                        // Refresh the BudgetHome activity
//                        Intent refreshIntent = new Intent(GoalEdit.this, Navigation.class);
//                        refreshIntent.putExtra("selectedTab", 2);
//                        startActivity(refreshIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed update
                        Log.w(TAG, "Error updating goal document", e);
                    }
                });
    }
}