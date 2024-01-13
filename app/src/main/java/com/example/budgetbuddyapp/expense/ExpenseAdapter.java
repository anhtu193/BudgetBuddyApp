package com.example.budgetbuddyapp.expense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.budgetbuddyapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    private Fragment fragment;
    private Context mContext;
    private List<Expense> expenseList;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_PROGRESS = 1;

//    private static Context getNonNullContext(Fragment fragment) {
//        if (fragment != null && fragment.isAdded()) {
//            return fragment.requireActivity();
//        } else {
//            // Nếu Fragment chưa được thêm vào Activity, trả về Application Context
//            return fragment != null ? fragment.requireContext().getApplicationContext() : null;
//        }
//    }
    public ExpenseAdapter(Context context, int layoutID, List<Expense> expenseList) {
        super(context, layoutID, expenseList);
        mContext = context;
        this.expenseList = expenseList;
    }

//    public ExpenseAdapter(Fragment fragment, int layoutID, List<Expense> expenseList) {
//        super(getNonNullContext(fragment), layoutID, expenseList);
//        this.fragment  = fragment;
//        this.expenseList = expenseList;
//    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on your condition, e.g., isProgress field in Expense class
        return getItem(position) instanceof ExpenseProgress ? VIEW_TYPE_PROGRESS : VIEW_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Number of view types
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        if (convertView == null) {
            // Inflate the appropriate layout based on the view type
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if (viewType == VIEW_TYPE_NORMAL) {
                // Get the current expense
                Expense expense = getItem(position);

                convertView = inflater.inflate(R.layout.item_expense, parent, false);

                ImageView expenseImage = convertView.findViewById(R.id.expenseImage);
                TextView expenseName = convertView.findViewById(R.id.expenseName);

                // Set data to views
                expenseName.setText(expense.getExpenseName());
                expenseImage.setImageResource(categoryImages[expense.getExpenseImage()]);

                // Button handling
                ImageView addExpenseButton = convertView.findViewById(R.id.addExpenseButton);
                addExpenseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle button click here, e.g., start a new activity
                        Intent intent = new Intent(mContext, AddNewExpense.class);
                        intent.putExtra("expenseID", expense.getExpenseID());
                        intent.putExtra("expenseName", expense.getExpenseName());
                        intent.putExtra("expenseLimit", expense.getExpenseLimit());
                        intent.putExtra("expenseTime", expense.getExpenseTime());
                        intent.putExtra("expenseImage", expense.getExpenseImage());
                        intent.putExtra("categoryID", expense.getCategoryID());
                        mContext.startActivity(intent);
                    }
                });
            } else {
                // Get the current expense progress
                ExpenseProgress expense = (ExpenseProgress) getItem(position);

                convertView = inflater.inflate(R.layout.item_expense_progress, parent, false);

                ImageView expenseImage = convertView.findViewById(R.id.expenseImage);
                TextView expenseName = convertView.findViewById(R.id.expenseName);
                TextView expenseTime = convertView.findViewById(R.id.expenseTime);
                TextView expenseCurrent = convertView.findViewById(R.id.expenseCurrent);
                TextView expenseLimit = convertView.findViewById(R.id.expenseLimit);

                expenseName.setText(expense.getExpenseName());
                expenseImage.setImageResource(categoryImages[expense.getExpenseImage()]);
                expenseTime.setText(expense.getExpenseTime());

                ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
                int CurrentProgress = (int) ((float) expense.getExpenseCurrent() / expense.getExpenseLimit() * 100);

                if (CurrentProgress >= 100) {
                    CurrentProgress = 100;
                    progressBar.getProgressDrawable().setColorFilter(
                            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    expenseCurrent.setTextColor(Color.RED);
                } else {
                    expenseCurrent.setTextColor(Color.BLACK);
                }

                progressBar.setProgress(CurrentProgress);

                expenseCurrent.setText(NumberFormat.getNumberInstance(Locale.US).format(expense.getExpenseCurrent()));
                expenseLimit.setText(NumberFormat.getNumberInstance(Locale.US).format(expense.getExpenseLimit()));

                // Set the click listener for the expense item
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle button click here, e.g., start a new activity
                        Intent intent = new Intent(mContext, ExpenseEditScreen.class);
                        intent.putExtra("expenseID", expense.getExpenseID());
                        intent.putExtra("expenseName", expense.getExpenseName());
                        intent.putExtra("expenseLimit", expense.getExpenseLimit());
                        intent.putExtra("expenseTime", expense.getExpenseTime());
                        intent.putExtra("expenseImage", expense.getExpenseImage());
                        intent.putExtra("categoryID", expense.getCategoryID());
                        intent.putExtra("expenseCurrent", expense.getExpenseCurrent());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        return convertView;
    }
}