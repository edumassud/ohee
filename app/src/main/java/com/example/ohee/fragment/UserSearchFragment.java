package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.ohee.R;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.adapter.ContactsAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSearchFragment extends Fragment {
    private RecyclerView recycler;
    private List<User> listUsers = new ArrayList<>();
    private ContactsAdapter adapter;
    private DatabaseReference database = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef = database.child("user");
    private ChildEventListener childEventListenerConversas;
    private FirebaseUser currentUser = SetFirebaseUser.getUser();

    public UserSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);

        recycler = view.findViewById(R.id.recycler);

        // Set adapter
        adapter = new ContactsAdapter(listUsers, getActivity());

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
                        List<User> listUsersUpdated = adapter.getContatos();
                        User selectedUser = listUsersUpdated.get(position);

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
        getUsers();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(childEventListenerConversas);
    }

    public void getUsers() {
        listUsers.clear();
        childEventListenerConversas = usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (!user.getEmail().equals(currentUser.getEmail())) {
                    listUsers.add(user);
                    adapter.notifyDataSetChanged();
                }
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

    public void searchUser(String txt) {
        List<User> listUserSearch = new ArrayList<>();

        for (User user : listUsers) {
            String name = user.getName().toLowerCase();
            String university = user.getUniversityName().toLowerCase();
            if (name.contains(txt) || university.contains(txt)) {
                listUserSearch.add(user);
            }
        }

        adapter = new ContactsAdapter(listUserSearch, getActivity());
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadUsers() {
        adapter = new ContactsAdapter(listUsers, getActivity());
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
