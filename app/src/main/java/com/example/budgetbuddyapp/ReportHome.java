package com.example.budgetbuddyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ReportHome extends AppCompatActivity {

    PieChart pieChartExpense, pieChartRevenue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_report);

        pieChartExpense = findViewById(R.id.pieChartExpense);
        pieChartRevenue = findViewById(R.id.pieChartRevenue);
        setupChartView();
    }

    private void setupChartView() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(500000, "12/12/2023"));
        entries.add(new PieEntry(500000, "13/12/2023"));
        entries.add(new PieEntry(500000, "14/12/2023"));
        entries.add(new PieEntry(500000, "15/12/2023"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Date");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChartExpense.setData(pieData);
        pieChartRevenue.setData(pieData);

        pieChartExpense.getDescription().setEnabled(false);
        pieChartExpense.animateY(1000);
        pieChartExpense.invalidate();

        pieChartRevenue.getDescription().setEnabled(false);
        pieChartRevenue.animateY(1000);
        pieChartRevenue.invalidate();

    }

}
