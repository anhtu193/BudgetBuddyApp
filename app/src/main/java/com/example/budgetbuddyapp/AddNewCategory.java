package com.example.budgetbuddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.budgetbuddyapp.databinding.NewCategoryBinding;

public class AddNewCategory extends AppCompatActivity {

    NewCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final String[] iconURL = {""};

        //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
        R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
        R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        CategoryGridViewAdapter gridAdapter = new CategoryGridViewAdapter(AddNewCategory.this, categoryImages);
        binding.gridview.setAdapter(gridAdapter);

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                binding.categoryIcon.setImageResource(categoryImages[position]);
                iconURL[0] = categoryImagesString[position];
            }
        });
    }


}