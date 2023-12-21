package com.example.budgetbuddyapp.transaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.categories.Category;
import com.example.budgetbuddyapp.categories.CategoryHome;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChooseCategoryBottomSheet extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {
    private ViewPager2 viewPager;

    private Category selectedCategory;

    public ChooseCategoryBottomSheet(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    private TabLayout tabLayout;
    ImageView categorySettings;

    public void updateCategory(Category selectedCategory){
        ((AddNewTransaction) getActivity()).updateCategory(selectedCategory);
        // Đóng bottom sheet sau khi chọn danh mục
        dismiss();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_choose_category, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tab_layout);
        categorySettings = view.findViewById(R.id.categorySettings);
        categorySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CategoryHome.class));
            }
        });
        setupViewPager();
        return view;
    }

    private void setupViewPager() {
        TransactionViewPagerAdapter adapter = new TransactionViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Chi tiêu"); // Đặt tiêu đề cho tab thứ nhất
                    } else {
                        tab.setText("Thu nhập"); // Đặt tiêu đề cho tab thứ hai
                    }
                }
        ).attach();
    }

}
