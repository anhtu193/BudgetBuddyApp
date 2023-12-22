package com.example.budgetbuddyapp.categories;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.Navigation;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.databinding.ActivityEditCategoryBinding;
import com.example.budgetbuddyapp.expense.ExpenseEdit;
import com.example.budgetbuddyapp.expense.ExpenseProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCategory extends AppCompatActivity {
    private Category selectedCategory;
    ActivityEditCategoryBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    int iconURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        iconURL = 0;
        // Lấy dữ liệu Category từ Intent
        Intent intent = getIntent();
        //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};


        if (intent != null && intent.hasExtra("category")) {
            selectedCategory = (Category) intent.getSerializableExtra("category");
            if (selectedCategory != null) {

                binding.inputCategoryName.setText(selectedCategory.getCategoryName());
                iconURL = selectedCategory.getCategoryImage();
                binding.categoryIcon.setImageResource(categoryImages[iconURL]);

                binding.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Lưu thông tin chỉnh sửa của Category vào Firestore
                        updateCategory(selectedCategory); // Tạo phương thức này để cập nhật thông tin Category lên Firestore
                    }
                });
            }
        }


//        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        CategoryGridViewAdapter gridAdapter = new CategoryGridViewAdapter(EditCategory.this, categoryImages);
        binding.gridview.setAdapter(gridAdapter);

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                binding.categoryIcon.setImageResource(categoryImages[position]);
                iconURL = position;
                selectedCategory.setCategoryImage(position);
            }
        });



        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
    // Phương thức để cập nhật thông tin Category lên Firestore
    private void updateCategory(Category category) {
        fStore.collection("categories").document(category.getCategoryID())
                .update("categoryName",  binding.inputCategoryName.getText().toString(),
                        "categoryImage",  iconURL)
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

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("expenseImage", iconURL);
        updatedData.put("expenseName", binding.inputCategoryName.getText().toString());

        fStore.collection("expenses")
                .whereEqualTo("categoryID", category.getCategoryID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(updatedData);
                            }
                            Log.e(TAG, "Cập nhật tên giới hạn chi tiêu thành công!");
                        } else {
                            Log.e(TAG, "Error getting categories: ", task.getException());
                        }
                    }
                });
    }
}