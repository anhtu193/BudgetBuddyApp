package com.example.budgetbuddyapp.categories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.budgetbuddyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class CategoryHome extends AppCompatActivity {

    ImageView backBtn;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    CategoryViewPagerApdater adapter;
    FloatingActionButton addNewCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_home);

        backBtn = findViewById(R.id.backBtn);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        addNewCategory = findViewById(R.id.addNewCategoryBtn);
//        tabLayout.addTab(tabLayout.newTab().setText("CHI TIÊU"));
//        tabLayout.addTab(tabLayout.newTab().setText("THU NHẬP"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new CategoryViewPagerApdater(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryHome.this, AddNewCategory.class));

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}