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
import com.example.ohee.adapter.AdapterFeedExplore;
import com.example.ohee.adapter.AdapterFeedHome;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.FeedExplore;
import com.example.ohee.model.Post;
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

    private List<FeedExplore> posts = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference userRef = databaseReference.child("user").child(loggedUserId);
    DatabaseReference feedRef = databaseReference.child("feedExplore");

    private ValueEventListener valueEventListener;

    public ExploreUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_your_university_feed, container, false);

        recycler = view.findViewById(R.id.recycler);

        // Set adapter
        adapter = new AdapterFeedExplore(posts, getActivity());

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
        userRef.removeEventListener(valueEventListener);
        posts.clear();
    }


    private void getFeed() {
        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                feedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        posts.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FeedExplore post = ds.getValue(FeedExplore.class);
                            if (!user.getFollowing().contains(post.getIdUser()) && !user.getUniversityDomain().equals(post.getUserDomain()) && !user.getIdUser().equals(post.getIdUser())) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
