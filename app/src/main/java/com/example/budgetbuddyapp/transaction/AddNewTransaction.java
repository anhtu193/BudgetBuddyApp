package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetbuddyapp.Home;
import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.CategoryHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

public class AddNewTransaction extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ImageView categoryIcon, closeButton;
    EditText transactionAmount, note;
    TextView date, time;

    Category selectedCategory;
    int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
            R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
            R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

    private void getSelectedCategoryFromFirestore() {
        fStore.collection("categories")
                .whereEqualTo("userID", userID)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.exists()) {
                            String categoryID = documentSnapshot.getId();
                            String categoryName = documentSnapshot.getString("categoryName");
                            Number categoryImageIndex = documentSnapshot.getLong("categoryImage");
                            Boolean isSelected = documentSnapshot.getBoolean("isSelected");
                            int categoryImage = categoryImageIndex.intValue();

                            selectedCategory = new Category(categoryID, userID, categoryName, "Chi tiêu", categoryImage, true);
                            // Perform any necessary updates here based on selectedCategory
                            // For example, update UI elements with selectedCategory data
                            categoryIcon.setImageResource(categoryImages[selectedCategory.getCategoryImage()]);
                            fStore.collection("categories").document(categoryID)
                                    .update("isSelected", true)
                                    .addOnSuccessListener(aVoid -> {
                                        // Xử lý khi cập nhật thành công
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý khi cập nhật thất bại
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch selectedCategory
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_transaction);

        closeButton = findViewById(R.id.closeButton);
        categoryIcon = findViewById(R.id.categoryIcon);
        transactionAmount = findViewById(R.id.transactionAmount);
        note = findViewById(R.id.note);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSelectedCategoryFromFirestore();



        categoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategory == null) {
                    selectedCategory = new Category(); // Initialize with default constructor or appropriate values
                }

                ChooseCategoryBottomSheet bottomSheet = new ChooseCategoryBottomSheet(selectedCategory);
                bottomSheet.show(getSupportFragmentManager(), "ChooseCategoryBottomSheet");
                Log.d(TAG, "Chọn category thành công");
            }
        });
    }
    public void updateCategory(Category fragmentSelectedCategory) {
        selectedCategory = fragmentSelectedCategory;
        // Update SelectedCategory và các Category còn lại trên Firestore:
        Query query = fStore.collection("categories").whereEqualTo("userID", userID);
        Category finalSelectedCategory = selectedCategory;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Lấy ID của mỗi document
                        String documentId = document.getId();

                        if (documentId.equals(finalSelectedCategory.getCategoryID())) {
                            // Cập nhật trường hoặc thông tin trong mỗi document tại đây
                            // Ví dụ: cập nhật trường "fieldToUpdate" thành giá trị mới
                            fStore.collection("categories").document(documentId)
                                    .update("isSelected", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xử lý thành công khi cập nhật
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý khi cập nhật thất bại
                                        }
                                    });
                        }
                        else
                        {
                            fStore.collection("categories").document(documentId)
                                    .update("isSelected", false)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xử lý thành công khi cập nhật
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý khi cập nhật thất bại
                                        }
                                    });
                        }
                    }
                } else {
                    // Xử lý khi không lấy được documents
                }
            }
        });
        // Set biểu tượng đã chọn vào ImageView (categoryIcon)
        categoryIcon.setImageResource(categoryImages[selectedCategory.getCategoryImage()]);
    }

}