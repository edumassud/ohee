package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private AdapterFeedHome adapter;
    private SwipeRefreshLayout swipeRefresh;

    private List<Post> posts = new ArrayList<>();
    private List<String> universities = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference feedRef = databaseReference.child("feedFollowing").child(loggedUserId);
    DatabaseReference userRef = databaseReference.child("user").child(loggedUserId);
    DatabaseReference universitiesRef = databaseReference.child("universities");
    DatabaseReference postsRef = databaseReference.child("posts");

    public FollowingFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_university_feed, container, false);

        recycler        = view.findViewById(R.id.recycler);
        swipeRefresh    = view.findViewById(R.id.refresh);

        // Set adapter
        adapter = new AdapterFeedHome(posts, getActivity());

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeed();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getFeed();
    }

    private void getFeed() {
        universities.clear();
        posts.clear();
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
                            postUniref.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    swipeRefresh.setRefreshing(false);
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
