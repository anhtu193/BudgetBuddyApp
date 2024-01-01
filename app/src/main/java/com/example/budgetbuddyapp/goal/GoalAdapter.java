package com.example.budgetbuddyapp.goal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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

public class GoalAdapter extends ArrayAdapter<Goal> {
    private Fragment fragment;
    private List<Goal> goalList;

    public GoalAdapter(Fragment fragment, int layoutID, List<Goal> goalList) {
        super(fragment.isAdded() ? fragment.requireActivity() : null, layoutID, goalList);
        this.fragment  = fragment ;
        this.goalList = goalList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int[] categoryImages = {R.drawable.food, R.drawable.c_electricitybill, R.drawable.c_fuel, R.drawable.c_clothes,
                R.drawable.c_bonus, R.drawable.c_shopping, R.drawable.c_book, R.drawable.c_salary, R.drawable.c_wallet,
                R.drawable.c_phone, R.drawable.c_celebration, R.drawable.c_makeup, R.drawable.c_celebration2, R.drawable.c_basketball, R.drawable.c_gardening};

        if (convertView == null) {
            // Inflate the appropriate layout based on the view type
            LayoutInflater inflater = LayoutInflater.from(fragment.requireActivity());
            // Get the current goal
            Goal goal = getItem(position);

            convertView = inflater.inflate(R.layout.item_goal, parent, false);

            ImageView goalImage = convertView.findViewById(R.id.goalImage);
            TextView goalName = convertView.findViewById(R.id.goalName);
            TextView goalCurrent = convertView.findViewById(R.id.goalCurrent);
            TextView goalNumber = convertView.findViewById(R.id.goalNumber);

            // Set data to views
            goalName.setText(goal.getGoalName());
            goalImage.setImageResource(categoryImages[goal.getGoalImage()]);

            ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
            int CurrentProgress = (int) ((float) goal.getGoalCurrent() / goal.getGoalNumber() * 100);
            progressBar.setProgress(CurrentProgress);

            goalCurrent.setText(String.format("%,d", goal.getGoalCurrent()));
            goalNumber.setText(String.format("%,d", goal.getGoalNumber()));

            // Set the click listener for the goal item
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle button click here, e.g., start a new activity
                    Intent intent = new Intent(fragment.requireActivity(), GoalFund.class);
                    intent.putExtra("goalID", goal.getGoalID());
                    intent.putExtra("goalName", goal.getGoalName());
                    intent.putExtra("goalCurrent", goal.getGoalCurrent());
                    intent.putExtra("goalNumber", goal.getGoalNumber());
                    intent.putExtra("date", goal.getDate());
                    intent.putExtra("goalImage", goal.getGoalImage());
                    fragment.requireActivity().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}