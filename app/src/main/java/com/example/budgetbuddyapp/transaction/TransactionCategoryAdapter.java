package com.example.budgetbuddyapp.transaction;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TransactionCategoryAdapter extends ArrayAdapter<Category> {
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    private Activity activity;
    private ArrayList<Category> categoryList;
    private Context context;

    public TransactionCategoryAdapter(Activity activity, int layoutID, ArrayList<Category> categoryList, Context context) {
        super(activity,layoutID, categoryList);
        this.activity = activity;
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null)
        {
            view = LayoutInflater.from(activity).inflate(R.layout.transaction_category_item, null, false);
        }

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
        TextView categoryName = (TextView)view.findViewById(R.id.categoryName);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
        ImageView check = (ImageView) view.findViewById(R.id.check);

        Category category = getItem(i);
        categoryName.setText(category.getCategoryName());
        categoryIcon.setImageResource(categoryImages[category.getCategoryImage()]);

        // Kiểm tra xem danh mục có được chọn hay không, sau đó cập nhật trạng thái hiển thị của biểu tượng check
        if (category.isSelected()) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }
        return view;
    }
}
