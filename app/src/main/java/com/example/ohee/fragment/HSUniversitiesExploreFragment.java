package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohee.R;
import com.example.ohee.activity.HSUniProfileActivity;
import com.example.ohee.activity.UniversityProfileActivity;
import com.example.ohee.adapter.SearchUniversityAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.University;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HSUniversitiesExploreFragment extends Fragment {
    private RecyclerView recycler;
    private ProgressBar progressBar;
    private List<University> listUniversities = new ArrayList<>();
    private SearchUniversityAdapter adapter;
    private DatabaseReference database = SetFirebase.getFirebaseDatabase();
    private DatabaseReference universityRef = database.child("universities");
    private ChildEventListener childEventListenerConversas;

    public HSUniversitiesExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_universities_explore, container, false);

        recycler        = view.findViewById(R.id.recycler);
        progressBar     = view.findViewById(R.id.progressBar);

        // Set adapter
        adapter = new SearchUniversityAdapter(listUniversities, getActivity());

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        // Set click
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        University selectedUniversity = listUniversities.get(position);

                        Intent i = new Intent(getActivity(), HSUniProfileActivity.class);
                        i.putExtra("selectedUniversity", selectedUniversity);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUniversities();
    }

    @Override
    public void onStop() {
        super.onStop();
        universityRef.removeEventListener(childEventListenerConversas);
    }

    public void getUniversities() {
        listUniversities.clear();
        childEventListenerConversas = universityRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                University university = dataSnapshot.getValue(University.class);

                if (university.getStudents().contains(SetFirebaseUser.getUsersId())) {
                    listUniversities.add(0, university);
                } else {
                    listUniversities.add(university);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
