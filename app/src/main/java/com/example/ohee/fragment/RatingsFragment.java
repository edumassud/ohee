package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.model.Rating;
import com.example.ohee.model.University;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingsFragment extends Fragment {
    private TextView txtGradeParty, txtGradeStudentLife, txtGradeAthletics, txtGradeDiversity, txtGradeCampus,
                     txtGradeFood, txtGradeDorms, txtGradeLocation, txtGradeAcademics, txtGradeProfessors, txtGradeCareer, txtGradeSafety;
    private ImageView imgParty, imgStudentLife, imgAthletics, imgDiversity, imgCampus, imgFood, imgDorms, imgLocation, imgAcademics,
                      imgProfessors, imgCareer, imgSafety;

    private University university;

    public RatingsFragment() {
        // Required empty public constructor
    }

    public RatingsFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ratings, container, false);

        txtGradeParty           = view.findViewById(R.id.txtGradeParty);
        txtGradeStudentLife     = view.findViewById(R.id.txtGradeStudentLife);
        txtGradeAthletics       = view.findViewById(R.id.txtGradeAthletics);
        txtGradeDiversity       = view.findViewById(R.id.txtGradeDiversity);
        txtGradeCampus          = view.findViewById(R.id.txtGradeCampus);
        txtGradeFood            = view.findViewById(R.id.txtGradeCampusFood);
        txtGradeDorms           = view.findViewById(R.id.txtGradeDorms);
        txtGradeLocation        = view.findViewById(R.id.txtGradeLocation);
        txtGradeAcademics       = view.findViewById(R.id.txtGradeAcademics);
        txtGradeProfessors      = view.findViewById(R.id.txtGradeProfessors);
        txtGradeCareer          = view.findViewById(R.id.txtGradeCareer);
        txtGradeSafety          = view.findViewById(R.id.txtGradeSafety);

        imgParty        = view.findViewById(R.id.imgParty);
        imgStudentLife  = view.findViewById(R.id.imgStudentLife);
        imgAthletics    = view.findViewById(R.id.imgAthletics);
        imgDiversity    = view.findViewById(R.id.imgDiversity);
        imgCampus       = view.findViewById(R.id.imgCampus);
        imgFood         = view.findViewById(R.id.imgCampusFood);
        imgDorms        = view.findViewById(R.id.imgDorms);
        imgLocation     = view.findViewById(R.id.imgLocation);
        imgAcademics    = view.findViewById(R.id.imgAcademics);
        imgProfessors   = view.findViewById(R.id.imgProfessors);
        imgCareer       = view.findViewById(R.id.imgCareer);
        imgSafety       = view.findViewById(R.id.imgSafety);

        // Get Ratings
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference ratingsRef = databaseReference.child("ratings").child(university.getDomain());

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float total = dataSnapshot.getChildrenCount();
                float party = 0;
                float lifeGrade = 0;
                float athletics = 0;
                float diversity = 0;
                float campus = 0;
                float food = 0;
                float dorms = 0;
                float location = 0;
                float academics = 0;
                float professors = 0;
                float career = 0;
                float safety = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Rating rating = ds.getValue(Rating.class);

                    party       += rating.getParties();
                    lifeGrade   += rating.getStudentLife();
                    athletics   += rating.getAthletics();
                    diversity   += rating.getDiversity();
                    campus      += rating.getCampus();
                    food        += rating.getFood();
                    dorms       += rating.getDorms();
                    location    += rating.getLocation();
                    academics   += rating.getAcademics();
                    professors  += rating.getProfessors();
                    career      += rating.getCareerCounsiling();
                    safety      += rating.getSafety();
                }

                if (party/total >= 4.6) {
                    txtGradeParty.setText("A+");
                } else if (party/total >= 4.3) {
                    txtGradeParty.setText("A");
                } else if (party/total >= 4.0) {
                    txtGradeParty.setText("A-");
                } else if (party/total >= 3.6) {
                    txtGradeParty.setText("B+");
                    imgParty.setImageResource(R.drawable.circleb);
                } else if (party/total >= 3.3) {
                    txtGradeParty.setText("B");
                    imgParty.setImageResource(R.drawable.circleb);
                } else if (party/total >= 3.0) {
                    txtGradeParty.setText("B-");
                    imgParty.setImageResource(R.drawable.circleb);
                } else if (party/total >= 2.6) {
                    txtGradeParty.setText("C+");
                    imgParty.setImageResource(R.drawable.circlec);
                } else if (party/total >= 2.3) {
                    txtGradeParty.setText("C");
                    imgParty.setImageResource(R.drawable.circlec);
                } else if (party/total >= 2.0) {
                    txtGradeParty.setText("C-");
                    imgParty.setImageResource(R.drawable.circlec);
                } else if (party/total >= 1.6) {
                    txtGradeParty.setText("F+");
                    imgParty.setImageResource(R.drawable.circlef);
                } else if (party/total >= 1.3) {
                    txtGradeParty.setText("F");
                    imgParty.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeParty.setText("F-");
                    imgParty.setImageResource(R.drawable.circlef);
                }


                if (lifeGrade/total >= 4.6) {
                    txtGradeStudentLife.setText("A+");
                } else if (lifeGrade/total >= 4.3) {
                    txtGradeStudentLife.setText("A");
                } else if (lifeGrade/total >= 4.0) {
                    txtGradeStudentLife.setText("A-");
                } else if (lifeGrade/total >= 3.6) {
                    txtGradeStudentLife.setText("B+");
                    imgStudentLife.setImageResource(R.drawable.circleb);
                } else if (lifeGrade/total >= 3.3) {
                    txtGradeStudentLife.setText("B");
                    imgStudentLife.setImageResource(R.drawable.circleb);
                } else if (lifeGrade/total >= 3.0) {
                    txtGradeStudentLife.setText("B-");
                    imgStudentLife.setImageResource(R.drawable.circleb);
                } else if (lifeGrade/total >= 2.6) {
                    txtGradeStudentLife.setText("C+");
                    imgStudentLife.setImageResource(R.drawable.circlec);
                } else if (lifeGrade/total >= 2.3) {
                    txtGradeStudentLife.setText("C");
                    imgStudentLife.setImageResource(R.drawable.circlec);
                } else if (lifeGrade/total >= 2.0) {
                    txtGradeStudentLife.setText("C-");
                    imgStudentLife.setImageResource(R.drawable.circlec);
                } else if (lifeGrade/total >= 1.6) {
                    txtGradeStudentLife.setText("F+");
                    imgStudentLife.setImageResource(R.drawable.circlef);
                } else if (lifeGrade/total >= 1.3) {
                    txtGradeStudentLife.setText("F");
                    imgStudentLife.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeStudentLife.setText("F-");
                    imgStudentLife.setImageResource(R.drawable.circlef);
                }

                if (athletics/total >= 4.6) {
                    txtGradeAthletics.setText("A+");
                } else if (athletics/total >= 4.3) {
                    txtGradeAthletics.setText("A");
                } else if (athletics/total >= 4.0) {
                    txtGradeAthletics.setText("A-");
                } else if (athletics/total >= 3.6) {
                    txtGradeAthletics.setText("B+");
                    imgAthletics.setImageResource(R.drawable.circleb);
                } else if (athletics/total >= 3.3) {
                    txtGradeAthletics.setText("B");
                    imgAthletics.setImageResource(R.drawable.circleb);
                } else if (athletics/total >= 3.0) {
                    txtGradeAthletics.setText("B-");
                    imgAthletics.setImageResource(R.drawable.circleb);
                } else if (athletics/total >= 2.6) {
                    txtGradeAthletics.setText("C+");
                    imgAthletics.setImageResource(R.drawable.circlec);
                } else if (athletics/total >= 2.3) {
                    txtGradeAthletics.setText("C");
                    imgAthletics.setImageResource(R.drawable.circlec);
                } else if (athletics/total >= 2.0) {
                    txtGradeAthletics.setText("C-");
                    imgAthletics.setImageResource(R.drawable.circlec);
                } else if (athletics/total >= 1.6) {
                    txtGradeAthletics.setText("F+");
                    imgAthletics.setImageResource(R.drawable.circlef);
                } else if (athletics/total >= 1.3) {
                    txtGradeAthletics.setText("F");
                    imgAthletics.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeAthletics.setText("F-");
                    imgAthletics.setImageResource(R.drawable.circlef);
                }

                if (diversity/total >= 4.6) {
                    txtGradeDiversity.setText("A+");
                } else if (diversity/total >= 4.3) {
                    txtGradeDiversity.setText("A");
                } else if (diversity/total >= 4.0) {
                    txtGradeDiversity.setText("A-");
                } else if (diversity/total >= 3.6) {
                    txtGradeDiversity.setText("B+");
                    imgDiversity.setImageResource(R.drawable.circleb);
                } else if (diversity/total >= 3.3) {
                    txtGradeDiversity.setText("B");
                    imgDiversity.setImageResource(R.drawable.circleb);
                } else if (diversity/total >= 3.0) {
                    txtGradeDiversity.setText("B-");
                    imgDiversity.setImageResource(R.drawable.circleb);
                } else if (diversity/total >= 2.6) {
                    txtGradeDiversity.setText("C+");
                    imgDiversity.setImageResource(R.drawable.circlec);
                } else if (diversity/total >= 2.3) {
                    txtGradeDiversity.setText("C");
                    imgDiversity.setImageResource(R.drawable.circlec);
                } else if (diversity/total >= 2.0) {
                    txtGradeDiversity.setText("C-");
                    imgDiversity.setImageResource(R.drawable.circlef);
                } else if (diversity/total >= 1.6) {
                    txtGradeDiversity.setText("F+");
                    imgDiversity.setImageResource(R.drawable.circlef);
                } else if (diversity/total >= 1.3) {
                    txtGradeDiversity.setText("F");
                    imgDiversity.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeDiversity.setText("F-");
                }

                if (campus/total >= 4.6) {
                    txtGradeCampus.setText("A+");
                } else if (campus/total >= 4.3) {
                    txtGradeCampus.setText("A");
                } else if (campus/total >= 4.0) {
                    txtGradeCampus.setText("A-");
                } else if (campus/total >= 3.6) {
                    txtGradeCampus.setText("B+");
                    imgCampus.setImageResource(R.drawable.circleb);
                } else if (campus/total >= 3.3) {
                    txtGradeCampus.setText("B");
                    imgCampus.setImageResource(R.drawable.circleb);
                } else if (campus/total >= 3.0) {
                    txtGradeCampus.setText("B-");
                    imgCampus.setImageResource(R.drawable.circleb);
                } else if (campus/total >= 2.6) {
                    txtGradeCampus.setText("C+");
                    imgCampus.setImageResource(R.drawable.circlec);
                } else if (campus/total >= 2.3) {
                    txtGradeCampus.setText("C");
                    imgCampus.setImageResource(R.drawable.circlec);
                } else if (campus/total >= 2.0) {
                    txtGradeCampus.setText("C-");
                    imgCampus.setImageResource(R.drawable.circlec);
                } else if (campus/total >= 1.6) {
                    txtGradeCampus.setText("F+");
                    imgCampus.setImageResource(R.drawable.circlef);
                } else if (campus/total >= 1.3) {
                    txtGradeCampus.setText("F");
                    imgCampus.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeCampus.setText("F-");
                    imgCampus.setImageResource(R.drawable.circlef);
                }

                if (food/total >= 4.6) {
                    txtGradeFood.setText("A+");
                } else if (food/total >= 4.3) {
                    txtGradeFood.setText("A");
                } else if (food/total >= 4.0) {
                    txtGradeFood.setText("A-");
                } else if (food/total >= 3.6) {
                    txtGradeFood.setText("B+");
                    imgFood.setImageResource(R.drawable.circleb);
                } else if (food/total >= 3.3) {
                    txtGradeFood.setText("B");
                    imgFood.setImageResource(R.drawable.circleb);
                } else if (food/total >= 3.0) {
                    txtGradeFood.setText("B-");
                    imgFood.setImageResource(R.drawable.circleb);
                } else if (food/total >= 2.6) {
                    txtGradeFood.setText("C+");
                    imgFood.setImageResource(R.drawable.circlec);
                } else if (food/total >= 2.3) {
                    txtGradeFood.setText("C");
                    imgFood.setImageResource(R.drawable.circlec);
                } else if (food/total >= 2.0) {
                    txtGradeFood.setText("C-");
                    imgFood.setImageResource(R.drawable.circlec);
                } else if (food/total >= 1.6) {
                    txtGradeFood.setText("F+");
                    imgFood.setImageResource(R.drawable.circlef);
                } else if (food/total >= 1.3) {
                    txtGradeFood.setText("F");
                    imgFood.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeFood.setText("F-");
                    imgFood.setImageResource(R.drawable.circlef);
                }

                if (dorms/total >= 4.6) {
                    txtGradeDorms.setText("A+");
                } else if (dorms/total >= 4.3) {
                    txtGradeDorms.setText("A");
                } else if (dorms/total >= 4.0) {
                    txtGradeDorms.setText("A-");
                } else if (dorms/total >= 3.6) {
                    txtGradeDorms.setText("B+");
                    imgDorms.setImageResource(R.drawable.circleb);
                } else if (dorms/total >= 3.3) {
                    txtGradeDorms.setText("B");
                    imgDorms.setImageResource(R.drawable.circleb);
                } else if (dorms/total >= 3.0) {
                    txtGradeDorms.setText("B-");
                    imgDorms.setImageResource(R.drawable.circleb);
                } else if (dorms/total >= 2.6) {
                    txtGradeDorms.setText("C+");
                    imgDorms.setImageResource(R.drawable.circlec);
                } else if (dorms/total >= 2.3) {
                    txtGradeDorms.setText("C");
                    imgDorms.setImageResource(R.drawable.circlec);
                } else if (dorms/total >= 2.0) {
                    txtGradeDorms.setText("C-");
                    imgDorms.setImageResource(R.drawable.circlec);
                } else if (dorms/total >= 1.6) {
                    txtGradeDorms.setText("F+");
                    imgDorms.setImageResource(R.drawable.circlef);
                } else if (dorms/total >= 1.3) {
                    txtGradeDorms.setText("F");
                    imgDorms.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeDorms.setText("F-");
                    imgDorms.setImageResource(R.drawable.circlef);
                }

                if (location/total >= 4.6) {
                    txtGradeLocation.setText("A+");
                } else if (location/total >= 4.3) {
                    txtGradeLocation.setText("A");
                } else if (location/total >= 4.0) {
                    txtGradeLocation.setText("A-");
                } else if (location/total >= 3.6) {
                    txtGradeLocation.setText("B+");
                    imgLocation.setImageResource(R.drawable.circleb);
                } else if (location/total >= 3.3) {
                    txtGradeLocation.setText("B");
                    imgLocation.setImageResource(R.drawable.circleb);
                } else if (location/total >= 3.0) {
                    txtGradeLocation.setText("B-");
                    imgLocation.setImageResource(R.drawable.circleb);
                } else if (location/total >= 2.6) {
                    txtGradeLocation.setText("C+");
                    imgLocation.setImageResource(R.drawable.circlec);
                } else if (location/total >= 2.3) {
                    txtGradeLocation.setText("C");
                    imgLocation.setImageResource(R.drawable.circlec);
                } else if (location/total >= 2.0) {
                    txtGradeLocation.setText("C-");
                    imgLocation.setImageResource(R.drawable.circlec);
                } else if (location/total >= 1.6) {
                    txtGradeLocation.setText("F+");
                    imgLocation.setImageResource(R.drawable.circlef);
                } else if (location/total >= 1.3) {
                    txtGradeLocation.setText("F");
                    imgLocation.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeLocation.setText("F-");
                    imgLocation.setImageResource(R.drawable.circlef);
                }

                if (academics/total >= 4.6) {
                    txtGradeAcademics.setText("A+");
                } else if (academics/total >= 4.3) {
                    txtGradeAcademics.setText("A");
                } else if (academics/total >= 4.0) {
                    txtGradeAcademics.setText("A-");
                } else if (academics/total >= 3.6) {
                    txtGradeAcademics.setText("B+");
                    imgAcademics.setImageResource(R.drawable.circleb);
                } else if (academics/total >= 3.3) {
                    txtGradeAcademics.setText("B");
                    imgAcademics.setImageResource(R.drawable.circleb);
                } else if (academics/total >= 3.0) {
                    txtGradeAcademics.setText("B-");
                    imgAcademics.setImageResource(R.drawable.circleb);
                } else if (academics/total >= 2.6) {
                    txtGradeAcademics.setText("C+");
                    imgAcademics.setImageResource(R.drawable.circlec);
                } else if (academics/total >= 2.3) {
                    txtGradeAcademics.setText("C");
                    imgAcademics.setImageResource(R.drawable.circlec);
                } else if (academics/total >= 2.0) {
                    txtGradeAcademics.setText("C-");
                    imgAcademics.setImageResource(R.drawable.circlec);
                } else if (academics/total >= 1.6) {
                    txtGradeAcademics.setText("F+");
                    imgAcademics.setImageResource(R.drawable.circlef);
                } else if (academics/total >= 1.3) {
                    txtGradeAcademics.setText("F");
                    imgAcademics.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeAcademics.setText("F-");
                    imgAcademics.setImageResource(R.drawable.circlef);
                }

                if (professors/total >= 4.6) {
                    txtGradeProfessors.setText("A+");
                } else if (professors/total >= 4.3) {
                    txtGradeProfessors.setText("A");
                } else if (professors/total >= 4.0) {
                    txtGradeProfessors.setText("A-");
                } else if (professors/total >= 3.6) {
                    txtGradeProfessors.setText("B+");
                    imgProfessors.setImageResource(R.drawable.circleb);
                } else if (professors/total >= 3.3) {
                    txtGradeProfessors.setText("B");
                    imgProfessors.setImageResource(R.drawable.circleb);
                } else if (professors/total >= 3.0) {
                    txtGradeProfessors.setText("B-");
                    imgProfessors.setImageResource(R.drawable.circleb);
                } else if (professors/total >= 2.6) {
                    txtGradeProfessors.setText("C+");
                    imgProfessors.setImageResource(R.drawable.circlec);
                } else if (professors/total >= 2.3) {
                    txtGradeProfessors.setText("C");
                    imgProfessors.setImageResource(R.drawable.circlec);
                } else if (professors/total >= 2.0) {
                    txtGradeProfessors.setText("C-");
                    imgProfessors.setImageResource(R.drawable.circlec);
                } else if (professors/total >= 1.6) {
                    txtGradeProfessors.setText("F+");
                    imgProfessors.setImageResource(R.drawable.circlef);
                } else if (professors/total >= 1.3) {
                    txtGradeProfessors.setText("F");
                    imgProfessors.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeProfessors.setText("F-");
                    imgProfessors.setImageResource(R.drawable.circlef);
                }

                if (career/total >= 4.6) {
                    txtGradeCareer.setText("A+");
                } else if (career/total >= 4.3) {
                    txtGradeCareer.setText("A");
                } else if (career/total >= 4.0) {
                    txtGradeCareer.setText("A-");
                } else if (career/total >= 3.6) {
                    txtGradeCareer.setText("B+");
                    imgCareer.setImageResource(R.drawable.circleb);
                } else if (career/total >= 3.3) {
                    txtGradeCareer.setText("B");
                    imgCareer.setImageResource(R.drawable.circleb);
                } else if (career/total >= 3.0) {
                    txtGradeCareer.setText("B-");
                    imgCareer.setImageResource(R.drawable.circleb);
                } else if (career/total >= 2.6) {
                    txtGradeCareer.setText("C+");
                    imgCareer.setImageResource(R.drawable.circlec);
                } else if (career/total >= 2.3) {
                    txtGradeCareer.setText("C");
                    imgCareer.setImageResource(R.drawable.circlec);
                } else if (career/total >= 2.0) {
                    txtGradeCareer.setText("C-");
                    imgCareer.setImageResource(R.drawable.circlec);
                } else if (career/total >= 1.6) {
                    txtGradeCareer.setText("F+");
                    imgCareer.setImageResource(R.drawable.circlef);
                } else if (career/total >= 1.3) {
                    txtGradeCareer.setText("F");
                    imgCareer.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeCareer.setText("F-");
                    imgCareer.setImageResource(R.drawable.circlef);
                }

                if (safety/total >= 4.6) {
                    txtGradeSafety.setText("A+");
                } else if (safety/total >= 4.3) {
                    txtGradeSafety.setText("A");
                } else if (safety/total >= 4.0) {
                    txtGradeSafety.setText("A-");
                } else if (safety/total >= 3.6) {
                    txtGradeSafety.setText("B+");
                    imgSafety.setImageResource(R.drawable.circleb);
                } else if (safety/total >= 3.3) {
                    txtGradeSafety.setText("B");
                    imgSafety.setImageResource(R.drawable.circleb);
                } else if (safety/total >= 3.0) {
                    txtGradeSafety.setText("B-");
                    imgSafety.setImageResource(R.drawable.circleb);
                } else if (safety/total >= 2.6) {
                    txtGradeSafety.setText("C+");
                    imgSafety.setImageResource(R.drawable.circlec);
                } else if (safety/total >= 2.3) {
                    txtGradeSafety.setText("C");
                    imgSafety.setImageResource(R.drawable.circlec);
                } else if (safety/total >= 2.0) {
                    txtGradeSafety.setText("C-");
                    imgSafety.setImageResource(R.drawable.circlec);
                } else if (safety/total >= 1.6) {
                    txtGradeSafety.setText("F+");
                    imgSafety.setImageResource(R.drawable.circlef);
                } else if (safety/total >= 1.3) {
                    txtGradeSafety.setText("F");
                    imgSafety.setImageResource(R.drawable.circlef);
                } else {
                    txtGradeSafety.setText("F-");
                    imgSafety.setImageResource(R.drawable.circlef);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
