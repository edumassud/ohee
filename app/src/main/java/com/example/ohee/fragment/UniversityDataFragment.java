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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import id.yuana.chart.pie.PieChartView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityDataFragment extends Fragment {
    private University university;
    private List<String> students;

    private PieChartView pieChart;
    private TextView txtDudesPercent, txtChicksPercent, txtOtherPercent;

    private float dudesCount = 0;
    private float chicksCount = 0;
    private float otherCount = 0;

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

        txtDudesPercent     = view.findViewById(R.id.txtDudePercent);
        txtChicksPercent    = view.findViewById(R.id.txtChickPercent);
        txtOtherPercent     = view.findViewById(R.id.txtOtherPercent);

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setCenterColor(R.color.colorBlack);

        getUsersData();

        return view;
    }

    private void getUsersData() {
       DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
       DatabaseReference usersRef = databaseReference.child("user");
       Query searchStudents;

       for (int i = 0; i < students.size(); i++) {
           searchStudents  = usersRef.orderByChild("idUser").equalTo(students.get(i));

           searchStudents.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       User user = ds.getValue(User.class);
                       if (user.getSex().equals("male")) {dudesCount = dudesCount + 1;}
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

        txtDudesPercent.setText("Dudes: " + Math.round(dudesPerCent * 100) + " %");
        txtChicksPercent.setText("Girls: " + Math.round(chicksPerCent * 100) + " %");
        txtOtherPercent.setText("Other: " + Math.round(otherPerCent * 100) + " %");


        float[] dataSet;
        int[] colors;

        dataSet = new float[]{dudesPerCent, chicksPerCent, otherPerCent};
        colors = new int[]{R.color.colorPrimary, R.color.colorPink, R.color.other};

        pieChart.setDataPoints(dataSet);
        pieChart.setSliceColor(colors);
    }
}
