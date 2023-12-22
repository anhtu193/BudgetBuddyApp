package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    private Activity activity;
    private ArrayList<Transaction> transactionList;
    private Context context;

    int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
            R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
            R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

    public TransactionAdapter(Activity activity, int layoutID, ArrayList<Transaction> transactionList, Context context) {
        super(activity,layoutID, transactionList);
        this.activity = activity;
        this.transactionList = transactionList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null)
        {
            view = LayoutInflater.from(activity).inflate(R.layout.transaction_item, null, false);
        }
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        TextView categoryName = (TextView) view.findViewById(R.id.categoryName);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
        TextView transactionDate = (TextView) view.findViewById(R.id.transactionDate);
        TextView transactionNote = (TextView) view.findViewById(R.id.transactionNote);
        TextView transactionAmount = (TextView) view.findViewById(R.id.transactionAmount);

        Transaction transaction = getItem(position);
        String categoryId = transaction.getCategoryId();
        if (categoryId != null)
        {
            Log.e(TAG, "Category ID selected is :" + categoryId);
            fStore.collection("categories").document(transaction.getCategoryId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        String categoryType = value.getString("categoryType");

                        categoryName.setText(FirestoreCategoryName);
                        categoryIcon.setImageResource(categoryImages[categoryImage]);

                        if (categoryType.equals("Thu nhập")) {
                            int color = ContextCompat.getColor(getContext(), R.color.earn);
                            transactionAmount.setTextColor(color);
                            String amount = "+" + String.format("%,d", transaction.getAmount());
                            transactionAmount.setText(amount);
                        }
                        else {
                            int color = ContextCompat.getColor(getContext(), R.color.spend);
                            transactionAmount.setTextColor(color);
                            String amount = "-" + String.format("%,d", transaction.getAmount());
                            transactionAmount.setText(amount);
                        }
                    }
                }
            });
        }
        else
        {
            Log.e(TAG, "Category ID is null");
        }

        transactionDate.setText(transaction.getDate());
        transactionNote.setText(transaction.getNote());
        return view;
    }
}
