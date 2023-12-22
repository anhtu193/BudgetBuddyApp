package com.example.budgetbuddyapp.transaction;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TransactionViewPagerAdapter extends FragmentStateAdapter {
    public TransactionViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1)
        {
            return new TransactionIncomeFragment();
        }
        return new TransactionOutcomeFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
