package com.example.budgetbuddyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart lineChart;

    private List<String> xValues;

    double cFirst, tFirst, cSecond, tSecond, cThird, tThird, cFourth, tFourth, cFifth, tFifth, cSixth, tSixth, cToday, tToday;
    PieChart pieChartExpense, pieChartRevenue;
    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
        View view = inflater.inflate(R.layout.ui_report, container, false);
        pieChartExpense = view.findViewById(R.id.pieChartExpense);
        pieChartRevenue = view.findViewById(R.id.pieChartRevenue);
        setupChartView();

        return view;
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