package com.example.budgetbuddyapp.category;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    private Activity activity;
    private ArrayList<Category> categoryList;
    private Context context;

    public CategoryAdapter(Activity activity, int layoutID, ArrayList<Category> categoryList, Context context) {
        super(activity,layoutID, categoryList);
        this.activity = activity;
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            view = LayoutInflater.from(activity).inflate(R.layout.category_item, null, false);
        }

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
        TextView categoryName = (TextView)view.findViewById(R.id.categoryName);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);

        Category category = getItem(i);
        categoryName.setText(category.getCategoryName());
        categoryIcon.setImageResource(categoryImages[category.getCategoryImage()]);
        ImageView deleteCategory = view.findViewById(R.id.deleteCategory);
        ImageView editCategory = view.findViewById(R.id.editCategory);

        deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại xác nhận xóa khi người dùng nhấn vào ImageView "deleteCategory"
                showDeleteConfirmationDialog(i);
            }
        });
        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category selectedCategory = categoryList.get(i);
                // Chuyển dữ liệu Category sang màn hình chỉnh sửa
                Intent intent = new Intent(getContext(), EditCategory.class);
                intent.putExtra("category", selectedCategory);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa loại chi tiêu này không?");

        // Nếu người dùng xác nhận muốn xóa
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Thực hiện xóa Category
                deleteCategory(position);
            }
        });

        // Nếu người dùng không muốn xóa, hoặc hủy bỏ hộp thoại
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCategory(int position) {
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Category categoryToDelete = categoryList.get(position);
        categoryList.remove(position);
        notifyDataSetChanged();

        fStore.collection("categories").document(categoryToDelete.getCategoryID()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Xóa loại chi tiêu thành công!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: category deleted with ID: " + categoryToDelete.getCategoryID());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Xóa loại chi tiêu thất bại!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: +" + e.toString());
                    }
                });

    }
}
