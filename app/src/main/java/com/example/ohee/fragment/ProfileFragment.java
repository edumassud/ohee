package com.example.ohee.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.EditProfileActivity;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private CircleImageView profileImg;
    private TextView profileNameAndUniversity;
    private TextView postsCount, followingCount, followersCount;
    private TextView profileBio;
    private Button btEditProfile;

    private FirebaseUser user = SetFirebaseUser.getUser();
    private DatabaseReference userRef = SetFirebase.getFirebaseDatabase().child("user").child(user.getUid());

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Screen components
        profileImg = view.findViewById(R.id.profileImg);
        profileNameAndUniversity = view.findViewById(R.id.profileNameAndUniversity);
        postsCount = view.findViewById(R.id.postsCount);
        followersCount = view.findViewById(R.id.followersCount);
        followingCount = view.findViewById(R.id.followingCount);
        profileBio = view.findViewById(R.id.profileBio);
        btEditProfile = view.findViewById(R.id.btEditProfile);

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String txtNameAndUniversity     = user.getName() + " • " + user.getUniversityName();
                String posts                    = String.valueOf(user.getPostCount());
                String following                = String.valueOf(user.getFollowingCount());
                String followers                = String.valueOf(user.getFollowerCount());
                String bio                      = String.valueOf(user.getBio());
                String picturePath              = String.valueOf(user.getPicturePath());

                // Setting data on screen
                postsCount.setText(posts);
                followingCount.setText(following);
                followersCount.setText(followers);
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

        return view;
    }

}
