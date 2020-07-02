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
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.activity.EditProfileActivity;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

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
    private GridView gridView;

    private AdapterGrid adapter;

    private FirebaseUser user = SetFirebaseUser.getUser();
    private DatabaseReference userRef = SetFirebase.getFirebaseDatabase().child("user").child(user.getUid());

    private ValueEventListener valueEventListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Screen components
        profileImg                  = view.findViewById(R.id.profileImg);
        profileNameAndUniversity    = view.findViewById(R.id.profileNameAndUniversity);
        postsCount                  = view.findViewById(R.id.postsCount);
        followersCount              = view.findViewById(R.id.followersCount);
        followingCount              = view.findViewById(R.id.followingCount);
        profileBio                  = view.findViewById(R.id.profileBio);
        btEditProfile               = view.findViewById(R.id.btEditProfile);
        gridView                    = view.findViewById(R.id.gridView);

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
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
        initImgLoader();
        loadPosts();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //userRef.removeEventListener(valueEventListener);
    }

    private void initImgLoader() {
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration
                .Builder(getContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);

    }

    private void loadPosts() {

        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference postsRef = databaseReference.child("posts");
        DatabaseReference myPosts  = postsRef.child(user.getUid());

        // Set grid size
        int sizeGrid = getResources().getDisplayMetrics().widthPixels;
        int sizeImg = sizeGrid / 3;
        gridView.setColumnWidth(sizeImg);

        myPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> urls = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    urls.add(0, post.getPath());
                }

                // Set adapter
                adapter = new AdapterGrid(getActivity(), R.layout.grid_post, urls);

                // Set gridView
                gridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
