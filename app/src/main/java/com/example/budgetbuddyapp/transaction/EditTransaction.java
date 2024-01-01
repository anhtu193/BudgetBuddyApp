package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditTransaction extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    //Copy array này đến toàn bộ những nơi cần fetch dữ liệu từ Firestore về
    int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
            R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
            R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

    ImageView closeButton, categoryIcon, saveTransaction;
    EditText transactionAmount, note;
    TextView date, time;

    String categoryType;
    Calendar calendar;
    Long differenceAmount;
    int[] iconURL;
    private Long formatStringToNumber(String formatedAmount) {
        String amountString = formatedAmount.replaceAll("[,.]", "");
        try {
            Long amount = Long.parseLong(amountString); // Chuyển đổi chuỗi thành số
            // Nếu bạn muốn số nguyên, sử dụng:
            // int parsedInt = Integer.parseInt(cleanString);

            // Sử dụng số parsed (hoặc parsedInt) để thực hiện các thao tác xử lý
            System.out.println("Số đã parse: " + amount);
            return amount;
            // Do whatever you want with 'parsed' here...
        } catch (NumberFormatException e) {
            // Xử lý nếu chuỗi không thể parse thành số
            System.out.println("Không thể parse chuỗi thành số: " + e.getMessage());
            Long a = Long.valueOf(0);
            return a;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        closeButton = findViewById(R.id.closeButton);
        categoryIcon = findViewById(R.id.categoryIcon);
        transactionAmount = findViewById(R.id.transactionAmount);
        saveTransaction = findViewById(R.id.saveTransaction);
        note = findViewById(R.id.note);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);



        Intent intent = getIntent();
        if (intent != null) {
            String transactionID = intent.getStringExtra("TransactionID");
            String categoryID = intent.getStringExtra("CategoryID");
            String noteI = intent.getStringExtra("Note");
            long amountI = intent.getLongExtra("Amount", 0); // Giá trị mặc định 0 nếu không có dữ liệu
            String dateI = intent.getStringExtra("Date");
            String timeI = intent.getStringExtra("Time");

            calendar = Calendar.getInstance();
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            auth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            userID = auth.getCurrentUser().getUid();
            transactionAmount.setText(String.format("%,d", amountI));
            time.setText(timeI);
            date.setText(dateI);
            note.setText(noteI);
            transactionAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            transactionAmount.addTextChangedListener(new TextWatcher() {
                private DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                private String current = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Không cần thực hiện gì trong trường hợp này
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Không cần thực hiện gì trong trường hợp này
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals(current)) {
                        transactionAmount.removeTextChangedListener(this);

                        String cleanString = s.toString().replaceAll("[,.]", ""); // Loại bỏ các dấu , và .
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = decimalFormat.format(parsed);

                            current = formatted;
                            transactionAmount.setText(formatted);
                            transactionAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Xử lý nếu không thể parse sang số double
                        }

                        transactionAmount.addTextChangedListener(this);
                    }
                }
            });

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
                        categoryType = value.getString("categoryType");

                        categoryIcon.setImageResource(categoryImages[categoryImage]);
                    }
                }
            });

            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePickerDialog();
                }
            });

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog();
                }
            });
            categoryIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(EditTransaction.this, "Bạn không thể thay đổi loại giao dịch!", Toast.LENGTH_SHORT).show();
                }
            });



            saveTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (transactionAmount.getText().toString().equals("")) {
                        Toast.makeText(EditTransaction.this, "Vui lòng nhập số tiền giao dịch!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("userID", userID);
                        updatedData.put("date", date.getText().toString());
                        updatedData.put("time", time.getText().toString());
                        updatedData.put("categoryId", categoryID);
                        Long amount = formatStringToNumber(transactionAmount.getText().toString());
                        updatedData.put("amount", amount);
                        differenceAmount = amount - amountI;
                        updatedData.put("note", note.getText().toString());

                        fStore.collection("transactions").document(transactionID).update(updatedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Transaction document updated successfully!");
                                updateUserBalance(differenceAmount, categoryType);
                                if (categoryType.equals("Chi tiêu")) {
                                    fStore.collection("expenses")
                                            .whereEqualTo("categoryID", categoryID)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {
                                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                                        Long expenseLimit = document.getLong("expenseLimit");
                                                        String expenseId = document.getId();
                                                        Log.d(TAG, "expenseLimit: " + expenseLimit);
                                                        if (expenseLimit != 0) { //nếu có expense limit được tạo
                                                            Long currentExpense = document.getLong("expenseCurrent");
                                                            currentExpense += differenceAmount;
                                                            fStore.collection("expenses").document(expenseId)
                                                                    .update("expenseCurrent", currentExpense)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Log.d(TAG, "Đã cập nhật expenseCurrent với ID: " + expenseId);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d(TAG, "Lỗi khi cập nhật expenseCurrent với ID: " + expenseId);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "Error getting documents", e);
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error updating transaction document: " + e.getMessage());
                            }
                        });
                        finish();
                    }
                }
            });
        }

    }

    private void updateUserBalance(double transactionAmount, String categoryType) {
        // Đọc số dư hiện tại của người dùng từ Firestore
        DocumentReference userDocRef = fStore.collection("users").document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    double currentBalance = documentSnapshot.getDouble("balance");

                    // Cập nhật số dư dựa trên loại giao dịch (Thu nhập hoặc Chi tiêu)
                    if (categoryType.equals("Thu nhập")) {
                        currentBalance += transactionAmount; // Tăng số dư nếu là thu nhập
                    } else {
                        currentBalance -= transactionAmount; // Giảm số dư nếu là chi tiêu
                    }

                    // Cập nhật số dư mới vào Firestore
                    userDocRef.update("balance", currentBalance)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User's balance updated successfully.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Failed to update user's balance: " + e.toString());
                                }
                            });
                } else {
                    Log.d(TAG, "User document does not exist.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch user document: " + e.toString());
            }
        });
    }

    public void showTimePickerDialog() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    updateTimeInView();
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void updateTimeInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        time.setText(sdf.format(calendar.getTime()));
    }

    public void showDatePickerDialog() {
        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateInView();
        }
    };

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        date.setText(sdf.format(calendar.getTime()));
    }
}