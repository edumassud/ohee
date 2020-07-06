package com.example.ohee.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterPosts;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Feed;
import com.example.ohee.model.Post;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFeedFragment extends Fragment {
    private RecyclerView recycler;
    private AdapterPosts adapter;

    private List<Feed> posts = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference feedRef = databaseReference.child("feed").child(loggedUserId);

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

        feedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Feed post = dataSnapshot.getValue(Feed.class);
                if (post.getPath() != null) {
                    posts.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set adapter
        adapter = new AdapterPosts(posts, getActivity());

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
        feedRef.removeEventListener(valueEventListener);
    }

    private void getFeed() {
        valueEventListener = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        posts.add(ds.getValue(Feed.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
