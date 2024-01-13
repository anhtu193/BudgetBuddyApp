package com.example.budgetbuddyapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.budgetbuddyapp.categories.CategoryHome;
import com.example.budgetbuddyapp.categories.CategoryViewPagerApdater;
import com.example.budgetbuddyapp.transaction.HomeTransactionAdapter;
import com.example.budgetbuddyapp.transaction.RecentTransaction;
import com.example.budgetbuddyapp.transaction.Transaction;
import com.example.budgetbuddyapp.transaction.TransactionAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView fullName, balance, categoryViewAll, transactionViewAll;
    ImageView hideBalance;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<Transaction> transactionList;
    HomeTransactionAdapter adapter;
    ListView recentTrasactions;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter CategoryApdater;

    public static final String SHARED_PREFS = "sharePrefs"; // đăng xuất
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.home_screen, container, false);

        fullName = view.findViewById(R.id.txtViewUserName);
        balance = view.findViewById(R.id.balance);
        hideBalance = view.findViewById(R.id.hideBalance);
        categoryViewAll = view.findViewById(R.id.categoryViewAll);
        transactionViewAll = view.findViewById(R.id.transactionViewAll);
        recentTrasactions = view.findViewById(R.id.recentTrasactions);
        transactionList = new ArrayList<>();
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CategoryApdater = new ViewPagerAdapter(fragmentManager, getLifecycle());
        TextView noItem = (TextView) view.findViewById(R.id.noItem);
        viewPager.setAdapter(CategoryApdater);
        final boolean[] isPasswordVisible = {false};

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();

        tabLayout.addTab(tabLayout.newTab().setText("CHI TIÊU"));
        tabLayout.addTab(tabLayout.newTab().setText("THU NHẬP"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    Long balanceValue = value.getLong("balance");

                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore
                    fullName.setText(fullNameText != null ? fullNameText : "");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ" : "");
                    Log.d(TAG, "User's balance updated: " +  String.format("%,d", balanceValue));
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });

        hideBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible[0]) {
                    // Nếu password đang hiển thị, chuyển về dạng ẩn
                    balance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible[0] = false;
                } else {
                    // Nếu password đang ẩn, chuyển về dạng hiển thị
                    balance.setTransformationMethod(null);
                    isPasswordVisible[0] = true;
                }
            }
        });
        categoryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CategoryHome.class));
            }
        });


        transactionViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RecentTransaction.class));
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
                        if (transactionList.isEmpty()) {
                            noItem.setVisibility(View.VISIBLE);
                        } else
                        {
                            noItem.setVisibility(View.GONE);
                        }
                        if (adapter == null) {
                            adapter = new HomeTransactionAdapter(HomeFragment.this, R.layout.transaction_item, transactionList);
                            recentTrasactions.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged(); // Cập nhật ListView nếu adapter đã được khởi tạo trước đó
                        }
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: " + error);
                    return;
                }
                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    Long balanceValue = value.getLong("balance");

                    // Cập nhật giao diện người dùng với dữ liệu mới từ Firestore
                    fullName.setText(fullNameText != null ? fullNameText : "");
                    balance.setText(balanceValue != null ? String.format("%,d", balanceValue) + " đ": "");
                    Log.d(TAG, "User's balance updated: " +  String.format("%,d", balanceValue));
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }
}