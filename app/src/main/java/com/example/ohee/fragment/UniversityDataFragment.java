package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
    private List<User> listUser = new ArrayList();
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


        getUsersData();



        // Setting data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(dudes, "Dudes"));
        entries.add(new PieEntry(chicks, "Chicks"));
        entries.add(new PieEntry(other, "Other"));

        PieDataSet set = new PieDataSet(entries, "");
        PieData data = new PieData(set);
        chart.setData(data);
        chart.invalidate(); // refresh

        return view;
    }

    private void getUsersData() {
        for (int i = 0; i <= university.getStudents().size(); i++) {
            String userSex = listUser.get(i).getSex();
            if (userSex != null && !userSex.isEmpty()) {
                if (userSex.equals("male")) {dudes = dudes + 1;}
                else if (userSex.equals("female")) {chicks = chicks + 1;}
                else if (userSex.equals("other")) {other = other + 1;}
            }
        }
    }
}
