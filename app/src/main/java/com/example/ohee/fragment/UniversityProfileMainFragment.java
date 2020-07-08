package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ohee.R;
import com.example.ohee.activity.ChatActivity;
import com.example.ohee.activity.MainActivity;
import com.example.ohee.activity.RateYourUniActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Rating;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.willy.ratingbar.ScaleRatingBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityProfileMainFragment extends Fragment {
    private TextView txtName, txtLocation, txtCount, txtRatingsCount;
    private University university;
    private Button btRate;
    private ScaleRatingBar uniRate;

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
        btRate          = view.findViewById(R.id.brRate);
        uniRate         = view.findViewById(R.id.uniRating);
        txtRatingsCount = view.findViewById(R.id.txtReviewCount);

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

        // Check if its the logged users university
        if (university.getStudents().contains(SetFirebaseUser.getUsersId())) {
            btRate.setVisibility(View.VISIBLE);
            btRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), RateYourUniActivity.class);
                    i.putExtra("selectedUniversity", university);
                    startActivity(i);
                }
            });
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get rating
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference ratings = databaseReference.child("ratings").child(university.getDomain());

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float total = dataSnapshot.getChildrenCount();
                int txtTotal = (int) Math.round(total);
                txtRatingsCount.setText(txtTotal + " reviews");
                float average = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Rating rating = ds.getValue(Rating.class);
                    average += rating.getAvarage();
                }
                uniRate.setRating(average / total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
