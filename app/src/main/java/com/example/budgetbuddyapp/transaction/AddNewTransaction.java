package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class AddNewTransaction extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ImageView categoryIcon, closeButton, addNewTransaction;
    EditText transactionAmount, note;
    TextView date, time;
    Calendar calendar;
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
                            String categoryType = documentSnapshot.getString("categoryType");
                            Number categoryImageIndex = documentSnapshot.getLong("categoryImage");
                            Boolean isSelected = documentSnapshot.getBoolean("isSelected");
                            int categoryImage = categoryImageIndex.intValue();

                            selectedCategory = new Category(categoryID, userID, categoryName, categoryType, categoryImage, isSelected);
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

    private void resetSelectedStateInFirestore() {
        Query query = fStore.collection("categories").whereEqualTo("userID", userID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Lấy ID của mỗi document
                        String documentId = document.getId();

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
                        Log.d(TAG, "Reset all categories");
                    }
                } else {
                    // Xử lý khi không lấy được documents
                    Log.d(TAG, "Reset all categories failed!");
                }
            }
        });
    }

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

        calendar = Calendar.getInstance();

        addNewTransaction = findViewById(R.id.addNewTransaction);

        updateDateInView();
        updateTimeInView();
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

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        resetSelectedStateInFirestore();
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

        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (transactionAmount.getText().toString().equals("")) {
                    Toast.makeText(AddNewTransaction.this, "Vui lòng nhập số tiền giao dịch!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (selectedCategory == null)
                    {
                        Toast.makeText(AddNewTransaction.this, "Hãy tạo mới loại chi tiêu để có thể thêm giao dịch nhé!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Map<String, Object> data = new HashMap<>();
                        data.put("userID", userID);
                        data.put("date", date.getText().toString());
                        data.put("time", time.getText().toString());
                        data.put("categoryId", selectedCategory.getCategoryID());
                        Long amount = formatStringToNumber(transactionAmount.getText().toString());
                        data.put("amount", amount);
                        data.put("note", note.getText().toString());

                        fStore.collection("transactions").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Lấy ID của document được tạo
                                String documentId = documentReference.getId();

                                fStore.collection("transactions").document(documentId).update("transactionId", documentId)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Transaction ID updated successfully.");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Failed to update document ID: " + e.toString());
                                            }
                                        });

                                fStore.collection("categories").document(selectedCategory.getCategoryID())
                                        .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                       if (documentSnapshot.exists()) {
                                                           String categoryType = documentSnapshot.getString("categoryType");
                                                           Log.d(TAG, "categoryType: " + categoryType);
                                                           // update User's balance
                                                           updateUserBalance(amount, categoryType);
                                                           // update if there's an expense created

                                                           if (categoryType.equals("Chi tiêu")) {
                                                               fStore.collection("expenses")
                                                                       .whereEqualTo("categoryID", selectedCategory.getCategoryID())
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
                                                                                       currentExpense += amount;
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
                                                       else {
                                                           Log.d(TAG, "No such document");
                                                       }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Error getting category Type", e);
                                                    }
                                                });

                                Log.d(TAG, "onSuccess: New transaction created with ID: " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: +" + e.toString());
                            }
                        });

                        finish();
                    }
                }
            }
        });
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
                            fStore.collection("categories").document(documentId)
                                    .update("isSelected", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xử lý thành công khi cập nhật
                                            String categoryType = document.getString("categoryType");
                                            selectedCategory.setCategoryType(categoryType);
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