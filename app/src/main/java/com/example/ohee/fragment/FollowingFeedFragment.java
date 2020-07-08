package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFeedHome;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Post;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFeedFragment extends Fragment {
    private RecyclerView recycler;
//    private AdapterFeedFollowing adapter;
    private AdapterFeedHome adapter;

//    private List<FeedFollowing> posts = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    private List<String> universities = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference feedRef = databaseReference.child("feedFollowing").child(loggedUserId);
    DatabaseReference userRef = databaseReference.child("user").child(loggedUserId);
    DatabaseReference universitiesRef = databaseReference.child("universities");
    DatabaseReference postsRef = databaseReference.child("posts");

    private ValueEventListener valueEventListener;

    public FollowingFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following_feed, container, false);

        recycler = view.findViewById(R.id.recycler);

        // Set adapter
        adapter = new AdapterFeedHome(posts, getActivity());

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getFeed();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

//    private void getFeed() {
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                valueEventListener = feedRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        posts.clear();
//                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            FeedFollowing post = ds.getValue(FeedFollowing.class);
//                            boolean dontAdd = post.getType().equals("homeExclusive") && !user.getUniversityDomain().equals(post.getDomain());
//                            if (!dontAdd) {
//                                posts.add(ds.getValue(FeedFollowing.class));
//                            }
//                        }
//                        Collections.reverse(posts);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getFeed() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User loggedUser = dataSnapshot.getValue(User.class);
                universitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            University university = ds.getValue(University.class);
                            universities.add(university.getDomain());
                        }

                        for (int i = 0; i < universities.size(); i++) {
                            DatabaseReference postUniref = postsRef.child(universities.get(i));
                            postUniref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Post post = ds.getValue(Post.class);
                                        boolean isFollowing = loggedUser.getFollowing().contains(post.getIdUser());
                                        boolean exclusive = post.getType().equals("homeExclusive") && !loggedUser.getUniversityDomain().equals(post.getUniversityDomain());
                                        if (isFollowing && !exclusive) {
                                            posts.add(post);
                                        }
                                    }
                                    Collections.reverse(posts);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
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
}
