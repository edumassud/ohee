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
import com.example.ohee.adapter.AdapterFeedExplore;
import com.example.ohee.adapter.AdapterFeedHome;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.FeedExplore;
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
public class ExploreUsersFragment extends Fragment {
    private RecyclerView recycler;
    private AdapterFeedExplore adapter;
    private SwipeRefreshLayout swipeRefresh;

    private List<Post> posts = new ArrayList<>();
    private List<String> universities = new ArrayList<>();
    private List<String> following = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference userRef           = databaseReference.child("user").child(loggedUserId);
    DatabaseReference universitiesRef   = databaseReference.child("universities");
    DatabaseReference postsRef          = databaseReference.child("posts");
    DatabaseReference followingref      = databaseReference.child("following");

    private ValueEventListener valueEventListener;

    public ExploreUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_your_university_feed, container, false);

        recycler        = view.findViewById(R.id.recycler);
        swipeRefresh    = view.findViewById(R.id.refresh);

        // Set adapter
        adapter = new AdapterFeedExplore(posts, getActivity());

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
    public void onResume() {
        super.onResume();
        getFeed();
    }

    private void getFeed() {
        posts.clear();
        universities.clear();
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

                                        DatabaseReference myFollowing = followingref.child(loggedUserId);
                                        myFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    String id = ds.getValue(String.class);
                                                    following.add(id);
                                                }
                                                boolean notFollowing = !following.contains(post.getIdUser());
                                                boolean difUni = !loggedUser.getUniversityDomain().equals(post.getUniversityDomain());
                                                boolean privacy = post.getType().equals("public");
                                                boolean notDuplicate = !posts.contains(post);
                                                if (notFollowing && difUni && privacy && notDuplicate) {
                                                    posts.add(post);
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
