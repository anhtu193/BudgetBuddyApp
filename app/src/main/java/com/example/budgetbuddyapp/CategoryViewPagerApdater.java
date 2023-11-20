package com.example.budgetbuddyapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryViewPagerApdater extends FragmentStateAdapter {

    public CategoryViewPagerApdater(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1)
        {
            return new OutcomeFragment();
        }
        return new IncomeFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
