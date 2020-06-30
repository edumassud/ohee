package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ohee.R;
import com.example.ohee.model.University;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityProfileMainFragment extends Fragment {
    private TextView txtName, txtLocation, txtCount;
    private University university;

    public UniversityProfileMainFragment() {
        // Required empty public constructor
    }

    public UniversityProfileMainFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_university_profile_main, container, false);

        txtName         = view.findViewById(R.id.txtName);
        txtLocation     = view.findViewById(R.id.txtLocation);
        txtCount        = view.findViewById(R.id.txtCount);

        txtName.setText(university.getName());
        String location = "";
        if (university.getCity() != null) {
            location = location + university.getCity();
        }
        if (university.getState() != null) {
            location = location + ", " + university.getState();
        }
        txtLocation.setText(location);
        txtCount.setText("Number of users: " + university.getCount());

        return view;
    }
}
