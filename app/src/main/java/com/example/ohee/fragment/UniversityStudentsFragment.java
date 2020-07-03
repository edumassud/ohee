package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.ohee.R;
import com.example.ohee.activity.ChatActivity;
import com.example.ohee.activity.GroupActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.adapter.ContactsAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.University;
import com.example.ohee.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UniversityStudentsFragment extends Fragment {
    private RecyclerView recycler;

    private ContactsAdapter adapter;
    private ArrayList<User> listStudents = new ArrayList<>();
    private University university;


    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser currentUser;

    public UniversityStudentsFragment() {
        // Required empty public constructor
    }

    public UniversityStudentsFragment(University university) {
        this.university = university;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_university_students, container, false);

        usersRef = databaseReference.child("user");
        recycler = view.findViewById(R.id.recycler);
        currentUser = SetFirebaseUser.getUser();

        // Set adapter
        adapter = new ContactsAdapter(listStudents, getActivity());

        // Set recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        // Set click
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recycler,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<User> listStudentsUpdated = adapter.getContatos();
                                User selectedUser = listStudentsUpdated.get(position);
                                Intent i = new Intent(getActivity(), VisitProfileActivity.class);
                                i.putExtra("selectedUser", selectedUser);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getStudents();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContatos);
    }

    public void getStudents() {
        valueEventListenerContatos = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listStudents.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (!user.getEmail().equals(currentUser.getEmail()) && user.getUniversityDomain().equals(university.getDomain())) {
                        listStudents.add(user);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
