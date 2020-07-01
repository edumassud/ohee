package com.example.ohee.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityDataFragment extends Fragment {
    private University university;
    private PieChart chart;

    private int dudes = 0;
    private int chicks = 0;
    private int other = 0;

    public UniversityDataFragment() {
        // Required empty public constructor
    }

    public UniversityDataFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_university_data, container, false);

        chart = (PieChart) view.findViewById(R.id.chart);
        chart.setNoDataText("");
        chart.setEntryLabelColor(Color.WHITE);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        getUsersData();
        setUsersData();

        return view;
    }

    private void getUsersData() {
        for (int i = 0; i < university.getStudents().size(); i++) {
            User user = university.getStudents().get(i);
            String userSex = user.getSex();
            if (userSex != null && !userSex.isEmpty()) {
                if (userSex.equals("male")) {dudes = dudes + 1;}
                else if (userSex.equals("female")) {chicks = chicks + 1;}
                else if (userSex.equals("other")) {other = other + 1;}
            }
        }
    }

    private void setUsersData() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(dudes, "Dudes"));
        entries.add(new PieEntry(chicks, "Chicks"));
        if (other > 5) {
            entries.add(new PieEntry(other, "Other"));
        }


        PieDataSet set = new PieDataSet(entries, "");
        PieData data = new PieData(set);

        set.setColors(R.color.colorPrimary, R.color.colorPink);

        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        data.getDataSetByLabel("",true);

        chart.setData(data);
        chart.invalidate();
    }
}
