package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Rating;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

public class RateYourUniActivity extends AppCompatActivity {
    private ScaleRatingBar ratingParty,ratingStudentLife, ratingCampus, ratingDorms, ratingCampusFood,
            ratingSafety, ratingProfessors, ratingLocation, ratingAcademics, ratingAthletics, ratingDiversity, ratingCareer;
    private Button btSave;
    private ProgressBar progressBar;
    private ImageView btClose;
    private TextView txtUniName;

    private University university;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_your_uni);

        ratingParty         = findViewById(R.id.ratingParty);
        ratingStudentLife   = findViewById(R.id.ratingStudentLife);
        ratingCampus        = findViewById(R.id.ratingCampus);
        ratingDorms         = findViewById(R.id.ratingDorms);
        ratingCampusFood    = findViewById(R.id.ratingCampusFood);
        ratingSafety        = findViewById(R.id.ratingSafety);
        ratingProfessors    = findViewById(R.id.ratingProfessors);
        ratingLocation      = findViewById(R.id.ratingLocation);
        ratingAcademics     = findViewById(R.id.ratingAcademics);
        ratingAthletics     = findViewById(R.id.ratingAthletics);
        ratingDiversity     = findViewById(R.id.ratingDiversity);
        ratingCareer        = findViewById(R.id.ratingCareer);
        btSave              = findViewById(R.id.btSave);
        progressBar         = findViewById(R.id.progressBar);
        btClose             = findViewById(R.id.btClose);
        txtUniName          = findViewById(R.id.txtUniversityName);

        getUniversity();
        getRatings();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Layout settings
                btSave.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                Rating rating = new Rating();

                rating.setUniversity(university.getDomain());

                rating.setParties(ratingParty.getRating());
                rating.setStudentLife(ratingStudentLife.getRating());
                rating.setCampus(ratingCampus.getRating());
                rating.setFood(ratingCampusFood.getRating());
                rating.setDorms(ratingDorms.getRating());
                rating.setSafety(ratingSafety.getRating());
                rating.setProfessors(ratingProfessors.getRating());
                rating.setLocation(ratingLocation.getRating());
                rating.setAcademics(ratingAcademics.getRating());
                rating.setAthletics(ratingAthletics.getRating());
                rating.setDiversity(ratingDiversity.getRating());
                rating.setCareerCounsiling(ratingCareer.getRating());

                rating.save();
                finish();
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUniversity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            university = (University) bundle.getSerializable("selectedUniversity");
            txtUniName.setText(university.getName());
        }
    }

    private void getRatings() {
        // Get ratings
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference ratingsRef = databaseReference.child("ratings").child(university.getDomain()).child(SetFirebaseUser.getUsersId());

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rating userRatings = dataSnapshot.getValue(Rating.class);

                if (userRatings != null) {
                    ratingParty.setRating(userRatings.getParties());
                    ratingStudentLife.setRating(userRatings.getStudentLife());
                    ratingCampus.setRating(userRatings.getCampus());
                    ratingDorms.setRating(userRatings.getDorms());
                    ratingCampusFood.setRating(userRatings.getFood());
                    ratingSafety.setRating(userRatings.getSafety());
                    ratingProfessors.setRating(userRatings.getProfessors());
                    ratingLocation.setRating(userRatings.getLocation());
                    ratingAcademics.setRating(userRatings.getAcademics());
                    ratingAthletics.setRating(userRatings.getAthletics());
                    ratingDiversity.setRating(userRatings.getDiversity());
                    ratingCareer.setRating(userRatings.getCareerCounsiling());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
