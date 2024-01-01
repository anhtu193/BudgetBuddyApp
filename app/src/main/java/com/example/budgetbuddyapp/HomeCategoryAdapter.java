package com.example.budgetbuddyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.EditCategory;

import java.util.ArrayList;

public class HomeCategoryAdapter extends ArrayAdapter<Category> {
    private Activity activity;
    private ArrayList<Category> categoryList;
    private Context context;

    public HomeCategoryAdapter(Activity activity, int layoutID, ArrayList<Category> categoryList, Context context) {
        super(activity,layoutID, categoryList);
        this.activity = activity;
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            view = LayoutInflater.from(activity).inflate(R.layout.home_category_item, null, false);
        }

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
        TextView categoryName = (TextView)view.findViewById(R.id.categoryName);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);

        Category category = getItem(i);

        categoryName.setText(category.getCategoryName());
        categoryIcon.setImageResource(categoryImages[category.getCategoryImage()]);
        return view;
    }
}
