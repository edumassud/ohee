package com.example.ohee.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.yuana.chart.pie.PieChartView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityDataFragment extends Fragment {
    private University university;
    private List<String> students;
//    private PieChart chart;
    private PieChartView pieChart;

    private float dudesCount = 0;
    private float chicksCount = 0;
    private float otherCount = 0;
    private String test = "fail";

    public UniversityDataFragment() {
        // Required empty public constructor
    }

    public UniversityDataFragment(University university) {
        this.university = university;
        this.students = university.getStudents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_university_data, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setCenterColor(R.color.colorBg);


//        chart = (PieChart) view.findViewById(R.id.chart);
//        chart.setNoDataText("");
//        chart.setEntryLabelColor(Color.WHITE);
//        chart.setUsePercentValues(true);
//        chart.getDescription().setEnabled(false);
//        chart.getLegend().setEnabled(false);
//        chart.setDragDecelerationFrictionCoef(0.95f);
//        chart.setDrawHoleEnabled(true);
//        chart.setHoleColor(Color.TRANSPARENT);
//        chart.setDrawCenterText(true);
//        chart.setRotationAngle(0);
//        chart.setRotationEnabled(false);
//        chart.setHighlightPerTapEnabled(false);

        getUsersData();

        return view;
    }

    private void getUsersData() {
       DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
       DatabaseReference usersRef = databaseReference.child("user");
        Query searchStudents;

       for (int i = 0; i < students.size(); i++) {
           searchStudents   = usersRef.orderByChild("idUser").equalTo(students.get(i));

           searchStudents.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       User user = ds.getValue(User.class);
                       if (user.getSex().equals("male")) {dudesCount = dudesCount + 1; test = "success";}
                       else if (user.getSex().equals("female")) {chicksCount = chicksCount + 1;}
                       else if (user.getSex().equals("other")) {otherCount++;}
                       setUsersData();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }


    }

    private void setUsersData() {
        float total = dudesCount + chicksCount + otherCount;

        float dudesPerCent = dudesCount / total;
        float chicksPerCent = chicksCount / total;
        float otherPerCent = otherCount / total;

        float[] dataSet;
        int[] colors;
        if (otherPerCent > 5) {
            dataSet = new float[]{dudesPerCent, chicksPerCent, otherPerCent};
            colors = new int[]{R.color.colorPrimary, R.color.colorPink, R.color.colorPrimaryDark};
        } else {
            dataSet = new float[]{dudesPerCent, chicksPerCent};
            colors = new int[]{R.color.colorPrimary, R.color.colorPink};
        }

        pieChart.setDataPoints(dataSet);
        pieChart.setSliceColor(colors);

//        List<PieEntry> entries = new ArrayList<>();
//        entries.add(new PieEntry(dudes, "Dudes"));
//        entries.add(new PieEntry(chicks, "Chicks"));
//        if (other > 5) {
//            entries.add(new PieEntry(other, "Other"));
//        }
//
//
//        PieDataSet set = new PieDataSet(entries, "");
//        PieData data = new PieData(set);
//
//        set.setColors(R.color.colorPrimary, R.color.colorPink);
//
//        data.setValueFormatter(new PercentFormatter(chart));
//        data.setValueTextSize(12f);
//        data.setValueTextColor(Color.WHITE);
//
//        data.getDataSetByLabel("",true);
//
//        chart.setData(data);
//        chart.invalidate();
    }
}