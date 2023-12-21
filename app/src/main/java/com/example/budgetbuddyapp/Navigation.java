package com.example.budgetbuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.budgetbuddyapp.transaction.AddNewTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class Navigation extends AppCompatActivity {
    FloatingActionButton addNewTransaction;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        addNewTransaction = findViewById(R.id.addNewTransaction);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        addNewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Navigation.this, AddNewTransaction.class));
            }
        });

        // Gắn HomeFragment khi Navigation Activity được tạo
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
           new NavigationBarView.OnItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                   Fragment selectedFragment = null;
                   switch (item.getItemId()) {
                       case R.id.home:
                           // Xử lý khi chọn tab Home
                           // Ví dụ:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                               new HomeFragment()).commit();
                           break;
                       case R.id.report:
                           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                   new ReportFragment()).commit();
                           break;
                       case R.id.budget:
                           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                   new BudgetFragment()).commit();
                           break;
                       case R.id.profile:
                           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                   new ProfileFragment()).commit();
                           break;
                   }

                   // Thay đổi Fragment khi chọn tab
                   if (selectedFragment != null) {
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                               selectedFragment).commit();
                   }
                   return true;
               }
           };
}