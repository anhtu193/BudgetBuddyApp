package com.example.budgetbuddyapp;

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

import com.example.budgetbuddyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class IncomeFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;

    ListView listView;
    ArrayList<Category> categoryList;

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

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

//        String[] categoryImagesString = {"drawable/food.png","drawable/c_electricitybill.png", "drawable/c_fuel.png", "drawable/c_clothes.png",
//                "drawable/c_bonus.png", "drawable/c_shopping.png", "drawable/c_book.png", "drawable/c_salary.png","drawable/c_wallet.png",
//                "drawable/c_phone.png", "drawable/c_celebration.png", "drawable/c_makeup.png", "drawable/c_celebration2.png", "drawable/c_basketball.png", "drawable/c_gardening.png"};

        fStore.collection("categories")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String categoryType = documentSnapshot.getString("categoryType");
                            if (categoryType.equals("Thu nhập"))
                            {
                                String categoryID = documentSnapshot.getId();
                                String categoryName = documentSnapshot.getString("categoryName");
                                Number categoryImageIndex = documentSnapshot.getLong("categoryImage");
                                int categoryImage = categoryImageIndex.intValue();
                                categoryList.add(new Category(categoryID, userID, categoryName, categoryType, categoryImage));
                                CategoryAdapter adapter = new CategoryAdapter(getActivity(),R.layout.category_item, categoryList);
                                listView.setAdapter(adapter);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: +" + e.toString());
                    }
                });



    }
}