package com.example.budgetbuddyapp.categories;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.budgetbuddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IncomeFragment extends Fragment{
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;

    ListView listView;
    ArrayList<Category> categoryList;
    CategoryAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        categoryList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listview);
        TextView noItem = (TextView) view.findViewById(R.id.noItem);
        //copy dãy này cho toàn bộ các chức năng chọn hình ảnh
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

//        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        fStore.collection("categories")
                .whereEqualTo("userID", userID)
                .whereEqualTo("categoryType", "Thu nhập") // Lọc dữ liệu theo điều kiện categoryType
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        categoryList.clear(); // Xóa dữ liệu cũ trước khi cập nhật mới
                        for (QueryDocumentSnapshot document : value) {
                            String categoryID = document.getId();
                            String categoryName = document.getString("categoryName");
                            Number categoryImageIndex = document.getLong("categoryImage");
                            int categoryImage = categoryImageIndex.intValue();
                            categoryList.add(new Category(categoryID, userID, categoryName, "Thu nhập", categoryImage));
                            Log.w(TAG, "fetched category with id " + categoryID);
                        }
                        if (categoryList.isEmpty()) {
                            noItem.setVisibility(View.VISIBLE);
                        } else
                        {
                            noItem.setVisibility(View.GONE);
                        }
                        if (adapter == null) {
                            adapter = new CategoryAdapter(getActivity(), R.layout.category_item, categoryList, getContext());
                            listView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "category list Length" + categoryList.size());
                            adapter.notifyDataSetChanged(); // Cập nhật ListView nếu adapter đã được khởi tạo trước đó
                        }
                    }
                });


    }


}