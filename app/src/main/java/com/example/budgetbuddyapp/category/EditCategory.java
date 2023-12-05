package com.example.budgetbuddyapp.category;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.ActivityEditCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditCategory extends AppCompatActivity {
    private Category selectedCategory;
    ActivityEditCategoryBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    int[] iconURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // Lấy dữ liệu Category từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("category")) {
            selectedCategory = (Category) intent.getSerializableExtra("category");
        }

        iconURL = new int[]{0};
        final Boolean[] isOutcome = {true};

        //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

//        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        CategoryGridViewAdapter gridAdapter = new CategoryGridViewAdapter(EditCategory.this, categoryImages);
        binding.gridview.setAdapter(gridAdapter);

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                binding.categoryIcon.setImageResource(categoryImages[position]);
                iconURL[0] = position;
                selectedCategory.setCategoryImage(position);
            }
        });

        binding.spinnerIncomeOutcome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("Chi tiêu"))
                {
                    isOutcome[0] = true;
                    selectedCategory.setCategoryType("Chi tiêu");
                } else if (item.equals("Thu nhập"))
                {
                    isOutcome[0] = false;
                    selectedCategory.setCategoryType("Thu nhập");
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

        if (selectedCategory != null) {
            // Điền dữ liệu Category vào các trường EditText, ImageView, Spinner, ...
            // Ví dụ: binding.inputCategoryName.setText(selectedCategory.getCategoryName());
            // ...
            binding.inputCategoryName.setText(selectedCategory.getCategoryName());
            binding.categoryIcon.setImageResource(categoryImages[selectedCategory.getCategoryImage()]);

            if (selectedCategory.getCategoryType().equals("Thu nhập")) {
                binding.spinnerIncomeOutcome.setSelection(0); // Select the second item in the Spinner (index 1) for "Thu nhập"
            } else {
                binding.spinnerIncomeOutcome.setSelection(1); // Select the first item in the Spinner (index 0) for "Chi tiêu"
            }

            binding.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Lưu thông tin chỉnh sửa của Category vào Firestore
                    updateCategory(selectedCategory); // Tạo phương thức này để cập nhật thông tin Category lên Firestore
                }
            });
        }
    }
    // Phương thức để cập nhật thông tin Category lên Firestore
    private void updateCategory(Category category) {
        fStore.collection("categories").document(category.getCategoryID())
                .update("categoryName",  binding.inputCategoryName.getText().toString(),
                        "categoryImage",  iconURL[0],
                        "categoryType", category.getCategoryType())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật thành công
                        Toast.makeText(getApplicationContext(), "Cập nhật loại chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: category updated with ID: " + category.getCategoryID());
                        finish(); // Kết thúc hoạt động chỉnh sửa sau khi cập nhật
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi cập nhật
                        Toast.makeText(getApplicationContext(), "Cập nhật loại chi tiêu thất bại!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: +" + e.toString());
                        // Xử lý lỗi (nếu cần thiết)
                    }
                });
    }
}