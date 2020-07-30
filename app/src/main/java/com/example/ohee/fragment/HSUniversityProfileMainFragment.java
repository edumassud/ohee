package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ohee.R;
import com.example.ohee.activity.MakeQuestionActivity;
import com.example.ohee.activity.RateYourUniActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Rating;
import com.example.ohee.model.University;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HSUniversityProfileMainFragment extends Fragment {
    private TextView txtName, txtLocation, txtCount, txtRatingsCount;
    private University university;
    private ScaleRatingBar uniRate;
    private LikeButton btLike;
    private Button btAsk;

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference userRef = databaseReference.child("highschoolers").child(SetFirebaseUser.getUsersId());

    public HSUniversityProfileMainFragment() {
        // Required empty public constructor
    }

    public HSUniversityProfileMainFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.hs_fragment_university_profile_main, container, false);

        txtName         = view.findViewById(R.id.txtName);
        txtLocation     = view.findViewById(R.id.txtLocation);
        txtCount        = view.findViewById(R.id.txtCount);
        uniRate         = view.findViewById(R.id.uniRating);
        txtRatingsCount = view.findViewById(R.id.txtReviewCount);
        btLike          = view.findViewById(R.id.btLike);
        btAsk           = view.findViewById(R.id.btAsk);

        if (university != null) {
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
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                for (int i = 0; i < user.getInterests().size(); i++) {
                    if (user.getInterests().get(i).getDomain().equals(university.getDomain())) {
                        btLike.setLiked(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                        List<University> universities = user.getInterests();
                        universities.add(university);
                        user.setInterests(universities);
                        user.updateInterests();

                        university.setLikes(university.getLikes() + 1);
                        university.update();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                        for (int i = 0; i < user.getInterests().size(); i++) {
                            if (user.getInterests().get(i).getDomain().equals(university.getDomain())) {
                                List<University> universities = user.getInterests();
                                universities.remove(i);
                                user.setInterests(universities);
                                user.updateInterests();
                                break;
                            }
                        }
                        university.setLikes(university.getLikes() - 1);
                        university.update();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MakeQuestionActivity.class);
                i.putExtra("selectedUniversity", university);
                startActivity(i);
            }
        });

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
