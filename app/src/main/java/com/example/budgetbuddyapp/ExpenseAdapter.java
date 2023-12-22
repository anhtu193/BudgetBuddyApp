package com.example.budgetbuddyapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private Activity activity;
    private List<Expense> expenseList;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_PROGRESS = 1;

    public ExpenseAdapter(Activity activity, int layoutID, List<Expense> expenseList) {
        super(activity, layoutID, expenseList);
        this.activity = activity;
        this.expenseList = expenseList;
    }

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
            LayoutInflater inflater = LayoutInflater.from(activity);
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
                        Intent intent = new Intent(activity, AddNewExpense.class);
                        intent.putExtra("expenseID", expense.getExpenseID());
                        intent.putExtra("expenseName", expense.getExpenseName());
                        intent.putExtra("expenseLimit", expense.getExpenseLimit());
                        intent.putExtra("expenseTime", expense.getExpenseTime());
                        intent.putExtra("expenseImage", expense.getExpenseImage());
                        intent.putExtra("categoryID", expense.getCategoryID());
                        activity.startActivity(intent);
                    }
                });
            } else {
                // Get the current expense progress
                ExpenseProgress expense = (ExpenseProgress) getItem(position);

                convertView = inflater.inflate(R.layout.item_expense_progress, parent, false);

                ImageView expenseImage = convertView.findViewById(R.id.expenseImage);
                TextView expenseName = convertView.findViewById(R.id.expenseName);
                TextView expenseLimit = convertView.findViewById(R.id.expenseLimit);
                TextView expenseTime = convertView.findViewById(R.id.expenseTime);

                expenseName.setText(expense.getExpenseName());
                expenseImage.setImageResource(categoryImages[expense.getExpenseImage()]);
                expenseLimit.setText(NumberFormat.getNumberInstance(Locale.US).format(expense.getExpenseLimit()));
                expenseTime.setText(expense.getExpenseTime());

                // Set the click listener for the expense item
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle button click here, e.g., start a new activity
                        Intent intent = new Intent(activity, ExpenseEditScreen.class);
                        intent.putExtra("expenseID", expense.getExpenseID());
                        intent.putExtra("expenseName", expense.getExpenseName());
                        intent.putExtra("expenseLimit", expense.getExpenseLimit());
                        intent.putExtra("expenseTime", expense.getExpenseTime());
                        intent.putExtra("expenseImage", expense.getExpenseImage());
                        intent.putExtra("categoryID", expense.getCategoryID());
                        activity.startActivity(intent);
                    }
                });
            }
        }

        return convertView;
    }
}