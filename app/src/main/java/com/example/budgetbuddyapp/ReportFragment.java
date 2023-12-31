package com.example.budgetbuddyapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.transaction.RecentTransaction;
import com.example.budgetbuddyapp.transaction.Transaction;
import com.example.budgetbuddyapp.transaction.TransactionAdapter;
import com.example.budgetbuddyapp.transaction.TransactionCategoryAdapter;
import com.example.budgetbuddyapp.transaction.TransactionInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth auth;
    private String userID;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LineChart lineChart;
    private List<String> xValues;
    double cFirst, tFirst, cSecond, tSecond, cThird, tThird, cFourth, tFourth, cFifth, tFifth, cSixth, tSixth, cToday, tToday;
    PieChart pieChartExpense, pieChartRevenue;
    TextView tv_expense_number, tv_revenue_number;
    ListView revenueListView, expenseListView;
    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_report, container, false);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        pieChartExpense = view.findViewById(R.id.pieChartExpense);
        pieChartRevenue = view.findViewById(R.id.pieChartRevenue);

        tv_expense_number = view.findViewById(R.id.tv_expense_number);
        tv_revenue_number = view.findViewById(R.id.tv_revenue_number);

        revenueListView = view.findViewById(R.id.recentRevenue);
        expenseListView = view.findViewById(R.id.recentExpense);

        fetchDataAndUpdateChart(pieChartRevenue, revenueListView, "Thu nhập");
        fetchDataAndUpdateChart(pieChartExpense, expenseListView, "Chi tiêu");

        return view;
    }

    private void fetchDataAndUpdateChart(final PieChart pieChart, final ListView listView, final String type) {
        // List to store PieEntry objects
        final List<PieEntry> pieEntries = new ArrayList<>();
        final double[] totalAmount = {0};

        // List to store 7 days
        final List<String> sevenDays = new ArrayList<>();

        // Fetch data from Firestore for the last 7 days
        for (int i = 0; i < 7; i++) {
            // Create a new Calendar instance for each iteration
            Calendar calendar = Calendar.getInstance();

            // Set the calendar to the current date
            calendar.setTime(new Date());

            // Subtract i days from the current date
            calendar.add(Calendar.DAY_OF_YEAR, -i);

            // Format the date as "dd-MM-yyyy"
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            final String dateString = dateFormat.format(calendar.getTime());

            sevenDays.add(dateString);
        }

        // List to store transactions for each category type
        final ArrayList<Transaction> transactionList = new ArrayList<>();

        // Fetch data from Firestore for the last 7 days
        fStore.collection("transactions")
                .whereEqualTo("userID", userID)
                .whereIn("date", sevenDays)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final Map<String, Double> totalAmountMap = new HashMap<>();

                            transactionList.clear(); // Xóa dữ liệu cũ trước khi cập nhật mới

                            for (QueryDocumentSnapshot transactionDoc : task.getResult()) {
                                String categoryId = transactionDoc.getString("categoryId");

                                String transactionID = transactionDoc.getString("transactionId");
                                String categoryID = transactionDoc.getString("categoryId");
                                String note = transactionDoc.getString("note");
                                Long amount = transactionDoc.getLong("amount");
                                String date = transactionDoc.getString("date");
                                String time = transactionDoc.getString("time");

                                Transaction transaction = new Transaction(transactionID, userID, categoryID, note, date, time, amount);

                                if (categoryId != null) {
                                    fStore.collection("categories")
                                            .document(categoryId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String categoryType = documentSnapshot.getString("categoryType");
                                                        String date = transactionDoc.getString("date");

                                                        if (categoryType.equals(type) && sevenDays.contains(date)) {
                                                            double amount = transactionDoc.getDouble("amount");

                                                            // Update the total amount for the specific date
                                                            totalAmountMap.put(date, totalAmountMap.getOrDefault(date, 0.0) + amount);

                                                            transactionList.add(transaction);
                                                        }

                                                        // Update UI after processing all documents
                                                        updateUI(pieChart, type, totalAmountMap);

                                                        TransactionAdapter adapter = (TransactionAdapter) listView.getAdapter();

                                                        if (adapter == null) {
                                                            adapter = new TransactionAdapter(getActivity(), R.layout.transaction_item, transactionList, getContext());
                                                            listView.setAdapter(adapter);
                                                        } else {
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    private void updateUI(PieChart pieChart, String type, Map<String, Double> totalAmountMap) {
        List<PieEntry> pieEntries = new ArrayList<>();
        double totalAmount = 0;

        for (Map.Entry<String, Double> entry : totalAmountMap.entrySet()) {
            String date = entry.getKey();
            double amount = entry.getValue();

            pieEntries.add(new PieEntry((float) amount, date));
            totalAmount += amount;
        }

        updatePieChart(pieChart, pieEntries, type);

        if (type.equals("Thu nhập")) {
            int color = ContextCompat.getColor(getContext(), R.color.earn);
            tv_revenue_number.setTextColor(color);
            tv_revenue_number.setText(String.format("%,.2f", totalAmount));
        } else {
            int color = ContextCompat.getColor(getContext(), R.color.spend);
            tv_expense_number.setTextColor(color);
            tv_expense_number.setText(String.format("%,.2f", totalAmount));
        }
    }

    private void updatePieChart(PieChart pieChart, List<PieEntry> pieEntries, String type) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        Description description = pieChart.getDescription();
        description.setText(type);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }
}