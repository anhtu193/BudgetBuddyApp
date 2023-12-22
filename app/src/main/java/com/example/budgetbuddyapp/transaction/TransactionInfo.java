package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.EditCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class TransactionInfo extends AppCompatActivity {

    ImageView categoryIcon, editTransaction, deleteTransaction, closeButton;
    TextView categoryName, amount, date, time, note;

    int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
            R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
            R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    String categoryType;
    String categoryID;

    String transactionID;
    String noteI;
    long amountI;
    String dateI;
    String timeI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_info);
        closeButton = findViewById(R.id.closeButton);
        editTransaction = findViewById(R.id.editTransaction);
        deleteTransaction = findViewById(R.id.deleteTransaction);
        categoryIcon = findViewById(R.id.categoryIcon);
        categoryName = findViewById(R.id.categoryName);
        amount = findViewById(R.id.amount);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        note = findViewById(R.id.note);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            transactionID = intent.getStringExtra("TransactionID");
            categoryID = intent.getStringExtra("CategoryID");
            if (transactionID != null) {
                Log.e(TAG, "Transaction Id is " + transactionID);
                fStore.collection("transactions").document(transactionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Listen failed: " + error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            noteI = value.getString("note");
                            amountI = value.getLong("amount");
                            dateI = value.getString("date");
                            timeI = value.getString("time");

                            date.setText(dateI);
                            time.setText(timeI);
                            if (noteI.equals("")) {
                                note.setText("Không có");
                            } else {
                                note.setText(noteI);
                            }
                        }
                    }
                });
            }
            else
            {
                Log.e(TAG, "Transaction Id is null ");
            }

            if (categoryID != null)
            {
                fStore.collection("categories").document(categoryID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Listen failed: " + error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            Number categoryImageIndex = value.getLong("categoryImage");
                            int categoryImage = categoryImageIndex.intValue();
                            String FirestoreCategoryName = value.getString("categoryName");
                            categoryType = value.getString("categoryType");
                            if (categoryType.equals("Thu nhập")) {
                                int color = ContextCompat.getColor(TransactionInfo.this, R.color.earn);
                                amount.setTextColor(color);
                                amount.setText(String.format("%,d", amountI));
                            }
                            else {
                                int color = ContextCompat.getColor(TransactionInfo.this, R.color.spend);
                                amount.setTextColor(color);
                                amount.setText(String.format("%,d", amountI));
                            }
                            categoryName.setText(FirestoreCategoryName);
                            categoryIcon.setImageResource(categoryImages[categoryImage]);
                        }
                    }
                });
            }




            deleteTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Hiển thị hộp thoại xác nhận xóa khi người dùng nhấn vào ImageView "deleteCategory"
                    showDeleteConfirmationDialog(transactionID, categoryID, amountI);

                }
            });

            editTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TransactionInfo.this, EditTransaction.class);

                    intent.putExtra("TransactionID", transactionID);
                    intent.putExtra("CategoryID", categoryID);
                    intent.putExtra("Note", noteI);
                    intent.putExtra("Amount", amountI);
                    intent.putExtra("Date", dateI);
                    intent.putExtra("Time", timeI);

                    startActivity(intent);
                }
            });
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
            if (transactionID != null) {
                Log.e(TAG, "Transaction Id is " + transactionID);
                fStore.collection("transactions").document(transactionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Listen failed: " + error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            String noteI = value.getString("note");
                            Long amountI = value.getLong("amount");
                            String dateI = value.getString("date");
                            String timeI = value.getString("time");

                            date.setText(dateI);
                            time.setText(timeI);
                            amount.setText(String.format("%,d", amountI));
                            if (noteI.equals("")) {
                                note.setText("Không có");
                            } else {
                                note.setText(noteI);
                            }
                        }
                    }
                });
            }
            else
            {
                Log.e(TAG, "Transaction Id is null ");
            }



    }

    private void showDeleteConfirmationDialog(String transactionId, String categoryId, Long amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionInfo.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa giao dịch này không?");

        // Nếu người dùng xác nhận muốn xóa
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Thực hiện xóa Category
                deleteTransaction(transactionId, categoryId, amount);
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

    private void deleteTransaction(String transactionId, String categoryId, Long amount) {
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (categoryId != null)
        {
            Log.d(TAG, "Category Id is :" + categoryId);
            fStore.collection("categories").document(categoryId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String categoryType = documentSnapshot.getString("categoryType");

                                // Xóa giao dịch
                                fStore.collection("transactions").document(transactionId).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Xóa giao dịch thành công!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onSuccess: category deleted with ID: " + transactionId);

                                                // Thực hiện cập nhật số dư của người dùng
                                                updateBalance(categoryType, amount);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Xóa giao dịch thất bại!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onFailure: " + e.toString());
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
        }
        else
        {
            Log.d(TAG, "Category Id is null");
        }

    }
    private void updateBalance(String categoryType, long amount) {
        fStore.collection("users").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            long currentBalance = documentSnapshot.getLong("balance");

                            if (categoryType.equals("Chi tiêu")) {
                                long updatedBalance = currentBalance + amount;
                                updateBalanceFirestore(updatedBalance);
                            } else if (categoryType.equals("Thu nhập")) {
                                long updatedBalance = currentBalance - amount;
                                updateBalanceFirestore(updatedBalance);
                            }
                        }
                    }
                });
    }

    private void updateBalanceFirestore(long updatedBalance) {
        fStore.collection("users").document(auth.getCurrentUser().getUid())
                .update("balance", updatedBalance)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Balance updated successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

}