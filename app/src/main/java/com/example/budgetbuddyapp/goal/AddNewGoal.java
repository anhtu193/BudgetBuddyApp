package com.example.budgetbuddyapp.goal;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.AddNewCategory;
import com.example.budgetbuddyapp.categories.CategoryHome;
import com.example.budgetbuddyapp.databinding.NewGoalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNewGoal extends AppCompatActivity {

    NewGoalBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    TextView date;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        date = findViewById(R.id.date);
        calendar = Calendar.getInstance();

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        updateDateInView();

        final int[] iconURL = {0};

        //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
        int[] goalImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

//        String[] goalImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        // Hiển thị các biểu tượng của người dùng
        GoalGridViewAdapter gridAdapter = new GoalGridViewAdapter(AddNewGoal.this, goalImages);
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
        binding.addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.inputGoalName.getText().toString().equals(""))
                {
                    Toast.makeText(AddNewGoal.this, "Vui lòng nhập tên mục tiêu!", Toast.LENGTH_SHORT).show();
                } else if (binding.inputGoalNumber.getText().toString().equals("")) {
                    Toast.makeText(AddNewGoal.this, "Vui lòng nhập số tiền mục tiêu!", Toast.LENGTH_SHORT).show();
                } else if (binding.inputGoalNumber.getText().toString().equals("0")) {
                    Toast.makeText(AddNewGoal.this, "Vui lòng nhập số tiền mục tiêu hợp lệ!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userID", userID);
                    data.put("goalName", binding.inputGoalName.getText().toString());
                    data.put("goalImage", iconURL[0]);
                    data.put("goalCurrent", 0);
                    Long inputGoalNumber = formatStringToNumber(binding.inputGoalNumber.getText().toString());
                    data.put("goalNumber", inputGoalNumber);
                    data.put("date", date.getText().toString());
//                    data.put("isSelected", false);

                    fStore.collection("goals").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: goal created with ID: " + documentReference.getId());

                            String goalID = documentReference.getId();
                            documentReference.update("goalID", goalID);

                            finish();

                            // Refresh the BudgetHome activity
//                            Intent refreshIntent = new Intent(AddNewGoal.this, Navigation.class);
//                            refreshIntent.putExtra("selectedTab", 2);
//                            startActivity(refreshIntent
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
}