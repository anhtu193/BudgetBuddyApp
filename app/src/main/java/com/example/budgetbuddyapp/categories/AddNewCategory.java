package com.example.budgetbuddyapp.categories;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.NewCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewCategory extends AppCompatActivity {

    NewCategoryBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = auth.getCurrentUser().getUid();

        final int[] iconURL = {0};
        final Boolean[] isOutcome = {true};

        //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
        R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
        R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

//        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        // Hiển thị các biểu tượng của người dùng
        CategoryGridViewAdapter gridAdapter = new CategoryGridViewAdapter(AddNewCategory.this, categoryImages);
        binding.gridview.setAdapter(gridAdapter);

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                binding.categoryIcon.setImageResource(categoryImages[position]);
                iconURL[0] = position;
            }
        });

        binding.spinnerIncomeOutcome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("Chi tiêu"))
                {
                    isOutcome[0] = true;
                } else if (item.equals("Thu nhập"))
                {
                    isOutcome[0] = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> spinner_choice = new ArrayList<>();
        spinner_choice.add("Chi tiêu");
        spinner_choice.add("Thu nhập");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_choice);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerIncomeOutcome.setAdapter(adapter);

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
                if (binding.inputCategoryName.getText().toString().equals(""))
                {
                    Toast.makeText(AddNewCategory.this, "Vui lòng nhập tên loại chi tiêu!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userID", userID);
                    data.put("categoryName", binding.inputCategoryName.getText().toString());
                    data.put("categoryImage", iconURL[0]);
                    data.put("isSelected", false);
                    if (isOutcome[0] == true)
                    {
                        data.put("categoryType", "Chi tiêu");
                    } else
                    {
                        data.put("categoryType", "Thu nhập");
                    }

                    fStore.collection("categories").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Lấy ID của document được tạo
                            String documentId = documentReference.getId();

                            // Cập nhật document với trường 'id' là documentId
                            fStore.collection("categories").document(documentId).update("categoryId", documentId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Category ID updated successfully.");
                                            if (isOutcome[0] == true) { // nếu loại là chi tiêu
                                                Map<String, Object> expenseData = new HashMap<>();
                                                expenseData.put("userID", userID);
                                                expenseData.put("expenseName" , binding.inputCategoryName.getText().toString());
                                                expenseData.put("categoryID", documentId);
                                                expenseData.put("expenseLimit", 0);
                                                expenseData.put("expenseImage" , iconURL[0]);
                                                expenseData.put("expenseTime", "Chưa đặt giới hạn");
                                                String expenseID = fStore.collection("expenses").document().getId();
                                                expenseData.put("expenseID", expenseID);
                                                expenseData.put("expenseCurrent", 0);
                                                fStore.collection("expenses").document(expenseID).set(expenseData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.e(TAG, "Expense rỗng đã được tạo cho category với ID: " + documentId);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Lỗi add Expense rỗng cho category với ID " + documentId, e);
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Failed to update document ID: " + e.toString());
                                        }
                                    });
                            Log.d(TAG, "onSuccess: category created with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: +" + e.toString());
                        }
                    });


                    finish();
                }

            }
        });
    }
}