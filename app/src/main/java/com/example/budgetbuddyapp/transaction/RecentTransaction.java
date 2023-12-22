package com.example.budgetbuddyapp.transaction;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.budgetbuddyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecentTransaction extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ListView transactionListView;
    ArrayList<Transaction> transactionList;
    TransactionAdapter adapter;
    ImageView backButton;

    FloatingActionButton addNewTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_transaction);

        backButton = findViewById(R.id.backButton);
        addNewTransaction = findViewById(R.id.addNewTransaction);
        transactionListView = findViewById(R.id.transactionListView);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        transactionList = new ArrayList<>();

        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecentTransaction.this, AddNewTransaction.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fStore.collection("transactions")
                .whereEqualTo("userID", userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        transactionList.clear(); // Xóa dữ liệu cũ trước khi cập nhật mới

                        for (QueryDocumentSnapshot document : value) {
                            String transactionID = document.getString("transactionId");
                            String categoryID = document.getString("categoryId");
                            String note = document.getString("note");
                            Long amount = document.getLong("amount");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            transactionList.add(new Transaction(transactionID, userID, categoryID, note, date, time, amount));
                        }

                        if (adapter == null) {
                            adapter = new TransactionAdapter(RecentTransaction.this, R.layout.transaction_item, transactionList, getApplicationContext());
                            transactionListView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged(); // Cập nhật ListView nếu adapter đã được khởi tạo trước đó
                        }
                    }
                });
        transactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Transaction selectedTransaction = transactionList.get(position);

                Intent intent = new Intent(RecentTransaction.this, TransactionInfo.class);

                intent.putExtra("TransactionID", selectedTransaction.getTransactionId());
                intent.putExtra("CategoryID", selectedTransaction.getCategoryId());
                intent.putExtra("Note", selectedTransaction.getNote());
                intent.putExtra("Amount", selectedTransaction.getAmount());
                intent.putExtra("Date", selectedTransaction.getDate());
                intent.putExtra("Time", selectedTransaction.getTime());

                startActivity(intent);
            }
        });
    }
}