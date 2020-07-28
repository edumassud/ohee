package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.ohee.R;
import com.example.ohee.adapter.ContactsAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikesListActivity extends AppCompatActivity {
    private ImageView btClose;
    private RecyclerView recycler;

    private Post post;
    private ContactsAdapter adapter;
    private List<User> likers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_list);

        btClose  = findViewById(R.id.btClose);
        recycler = findViewById(R.id.recycler);

        // Get post
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            post = (Post) bundle.getSerializable("post");
        }

        // Get list users
        if (!post.getType().equals("highschool")) {
            for (int i = 0; i < post.getLikedBy().size(); i++) {
                DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
                DatabaseReference userRef = databaseReference.child("user").child(post.getLikedBy().get(i));
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        likers.add(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            for (int i = 0; i < post.getLikedBy().size(); i++) {
                DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
                DatabaseReference userRef = databaseReference.child("highschoolers").child(post.getLikedBy().get(i));
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HighSchooler highSchooler = dataSnapshot.getValue(HighSchooler.class);
                        User user = new User();
                        user.setName(highSchooler.getName());
                        user.setEmail(highSchooler.getEmail());
                        user.setUniversityName("");
                        user.setPicturePath(highSchooler.getPicturePath());
                        likers.add(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        // Set adapter
        adapter = new ContactsAdapter(likers, this);

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set click
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<User> listUsersUpdated = adapter.getContatos();
                        User selectedUser = listUsersUpdated.get(position);

                        if (!selectedUser.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                            Intent i = new Intent(getApplicationContext(), VisitProfileActivity.class);
                            i.putExtra("selectedUser", selectedUser);
                            startActivity(i);
                        }
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
    }
}
