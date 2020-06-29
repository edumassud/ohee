package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohee.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversitiesExploreFragment extends Fragment {

    public UniversitiesExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_universities_explore, container, false);
    }
}
