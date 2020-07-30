package com.example.ohee.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.HSEditProfileActivity;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Question;
import com.example.ohee.model.University;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.example.ohee.activity.FriendsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HSProfileFragment extends Fragment {
    private CircleImageView profileImg;
    private TextView profileNameAndUniversity;
    private TextView profileUsername;
    private TextView profileBio;
    private TextView questionsCount, followingCount;
    private Button btEditProfile;

    private FirebaseUser user = SetFirebaseUser.getUser();
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference userRef           = databaseReference.child("highschoolers").child(user.getUid());
    private DatabaseReference questionsRef      = databaseReference.child("questions");


    private ValueEventListener valueEventListener;

    public HSProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_hs, container, false);

        // Screen components
        profileImg                  = view.findViewById(R.id.profileImg);
        profileUsername             = view.findViewById(R.id.txtUsername);
        profileNameAndUniversity    = view.findViewById(R.id.profileNameAndUniversity);
        profileBio                  = view.findViewById(R.id.profileBio);
        btEditProfile               = view.findViewById(R.id.btEditProfile);
        questionsCount              = view.findViewById(R.id.questionsCount);
        followingCount              = view.findViewById(R.id.followingCount);

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HSEditProfileActivity.class));
            }
        });

        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                String txtNameAndUniversity     = user.getName();
                String username                 = String.valueOf(user.getUserName());
                String bio                      = String.valueOf(user.getBio());
                String picturePath              = String.valueOf(user.getPicturePath());
                List<University> universities   = user.getInterests();

                //loadPosts();

                // Setting data on screen
                followingCount.setText(universities.size()+"");
                profileUsername.setText(username);
                profileBio.setText(bio);
                profileNameAndUniversity.setText(txtNameAndUniversity);
                if (user.getPicturePath() != null && getActivity() != null) {
                    Uri uri = Uri.parse(picturePath);
                    Glide.with(getActivity()).load(uri).into(profileImg);
                } else {
                    profileImg.setImageResource(R.drawable.avatar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    if (question.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                        total++;
                    }
                }
                questionsCount.setText(total + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //loadPosts();


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //userRef.removeEventListener(valueEventListener);
    }

}
