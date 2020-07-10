package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ohee.R;
import com.example.ohee.activity.PostActivity;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Post;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityGridFragment extends Fragment {
    private GridView grid;

    private University university;

    private AdapterGrid adapter;

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference postsRef = databaseReference.child("posts");
    DatabaseReference userRef = databaseReference.child("user").child(SetFirebaseUser.getUsersId());

    private List<Post> posts = new ArrayList<>();

    public UniversityGridFragment() {
        // Required empty public constructor
    }

    public UniversityGridFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_university_grid, container, false);

        grid = view.findViewById(R.id.grid);

        initImgLoader();
        loadPosts();
        setClick();

        return view;
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
        // Set grid size
        int sizeGrid = getResources().getDisplayMetrics().widthPixels;
        int sizeImg = sizeGrid / 3;
        grid.setColumnWidth(sizeImg);

        DatabaseReference thisUniversitysPosts = postsRef.child(university.getDomain());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                thisUniversitysPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> urls = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Post post = ds.getValue(Post.class);

                            boolean isPublic = post.getType().equals("public");
                            boolean sameSchoolFriend = user.getFollowing().contains(post.getIdUser()) && post.getUniversityDomain().equals(user.getUniversityDomain());
                            boolean sameSchool = user.getUniversityDomain().equals(post.getUniversityDomain()) && !post.getType().equals("private");
                            boolean isFriend = user.getFollowing().contains(post.getIdUser()) && !post.getType().equals("homeExclusive");

                            if (isPublic || sameSchoolFriend || sameSchool || isFriend) {
                                urls.add(post.getPath());
                                posts.add(post);
                            }

                        }

                        // Set adapter
                        adapter = new AdapterGrid(getActivity(), R.layout.grid_post, urls);

                        // Set gridView
                        grid.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setClick() {
        // Set click
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = posts.get(position);

                DatabaseReference selectedUserRef = databaseReference.child("user").child(post.getIdUser());
                selectedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Intent i = new Intent(getContext(), PostActivity.class);
                        i.putExtra("selectedPost", post);
                        i.putExtra("selectedUser", user);

                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}
