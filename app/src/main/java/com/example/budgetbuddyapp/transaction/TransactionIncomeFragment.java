package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.CategoryAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TransactionIncomeFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ListView listView;
    ArrayList<Category> categoryList;
    TransactionCategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transaction_income_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        categoryList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listview);
        TextView noItem = (TextView) view.findViewById(R.id.noItem);
        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

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
                            Boolean isSelected = document.getBoolean("isSelected");
                            int categoryImage = categoryImageIndex.intValue();
                            categoryList.add(new Category(categoryID, userID, categoryName, "Thu nhập", categoryImage, isSelected));
                        }
                        if (categoryList.isEmpty()) {
                            noItem.setVisibility(View.VISIBLE);
                        } else
                        {
                            noItem.setVisibility(View.GONE);
                        }
                        Log.d(TAG, "Lấy dữ liệu thành công");
                        if (adapter == null) {
                            adapter = new TransactionCategoryAdapter(getActivity(), R.layout.transaction_category_item, categoryList, getContext());
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged(); // Cập nhật ListView nếu adapter đã được khởi tạo trước đó
                        }
                    }
                });
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Category selectedCategory = categoryList.get(position);
            // Cập nhật Adapter để áp dụng thay đổi lên giao diện người dùng
            adapter.notifyDataSetChanged();
            // Gửi dữ liệu biểu tượng của danh mục đã chọn về cho ChooseCategoryBottomSheet
            ((ChooseCategoryBottomSheet) getParentFragment()).updateCategory(selectedCategory);
        });
    }

}
