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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import id.yuana.chart.pie.PieChartView;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatutsDistFragment extends Fragment {
    private PieChartView pieChartDudes, pieChartGirls;

    private University university;
    private List<String> students;

    private float dudesSingle = 0;
    private float dudesComplicated = 0;
    private float dudesTaken = 0;
    private float girlsSingle = 0;
    private float girlsComplicated = 0;
    private float girlsTaken = 0;

    public StatutsDistFragment() {
        // Required empty public constructor
    }

    public StatutsDistFragment(University university) {
        this.university = university;
        this.students = university.getStudents();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_statuts_dist, container, false);


        pieChartDudes = view.findViewById(R.id.pieChartDudes);
        pieChartGirls = view.findViewById(R.id.pieChartGirls);

        pieChartDudes.setCenterColor(R.color.colorBg);
        pieChartGirls.setCenterColor(R.color.colorBg);

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

                        if (user.getStatus() != null) {
                            switch (user.getStatus()) {
                                case "single":
                                    if (user.getSex().equals("male")) {
                                        dudesSingle++;
                                    } else if (user.getSex().equals("female")) {
                                        girlsSingle++;
                                    }
                                    break;
                                case "complicated":
                                    if (user.getSex().equals("male")) {
                                        dudesComplicated++;
                                    } else if (user.getSex().equals("female")) {
                                        girlsComplicated++;
                                    }
                                    break;
                                case "taken":
                                    if (user.getSex().equals("male")) {
                                        dudesTaken++;
                                    } else if (user.getSex().equals("female")) {
                                        girlsTaken++;
                                    }
                                    break;
                            }
                        }

                    }
                    setUsersData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setUsersData() {
        float totalDudes = dudesComplicated + dudesSingle + dudesTaken;
        float totalGirls = girlsComplicated + girlsSingle + girlsTaken;

        float dudesSinglePerCent = dudesSingle / totalDudes;
        float dudesCompPerCent = dudesComplicated / totalDudes;
        float dudesTakenPerCent = dudesTaken / totalDudes;


        float girlsSinglePerCent = girlsSingle / totalGirls;
        float girlsCompPerCent = girlsComplicated / totalGirls;
        float girlsTakenPerCent = girlsTaken / totalGirls;

        float[] dataSetDudes;
        float[] dataSetGirls;
        int[] colors;

        dataSetDudes = new float[]{dudesSinglePerCent, dudesCompPerCent, dudesTakenPerCent};
        dataSetGirls = new float[]{girlsSinglePerCent, girlsCompPerCent, girlsTakenPerCent};
        colors = new int[]{R.color.colorSingle, R.color.colorComplicated, R.color.colorTaken};



        pieChartDudes.setDataPoints(dataSetDudes);
        pieChartGirls.setDataPoints(dataSetGirls);
        pieChartDudes.setSliceColor(colors);
        pieChartGirls.setSliceColor(colors);
    }
}
