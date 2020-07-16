package com.example.ohee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohee.R;
import com.example.ohee.adapter.ContactsAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import id.yuana.chart.pie.PieChartView;

public class FriendsActivity extends AppCompatActivity {
    private ImageView btClose;
    private TextView txtType;
    private RecyclerView recycler;
    private PieChartView chartGender, chartUniversities;

    private String type;
    private User selectedUser;

    private List<User> users = new ArrayList<>();
    private ContactsAdapter adapter;

    private float dudesCount = 0;
    private float chicksCount = 0;
    private float otherCount = 0;

    private List<String> universities = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private List<String> following = new ArrayList<>();
    private List<String> followers = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference followingRef      = databaseReference.child("following");
    private DatabaseReference followerRef       = databaseReference.child("followers");
    private DatabaseReference usersRef          = databaseReference.child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        btClose             = findViewById(R.id.btClose);
        recycler            = findViewById(R.id.recycler);
        txtType             = findViewById(R.id.txtType);
        chartGender         = findViewById(R.id.pieChartGender);
        chartUniversities   = findViewById(R.id.pieChartUniversities);

        chartGender.setCenterColor(R.color.colorBlack);
        chartUniversities.setCenterColor(R.color.colorBlack);

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (User) bundle.getSerializable("user");
            type = (String) bundle.getSerializable("type");
        }

        // Set adapter
        if (type.equals("following")) {
            txtType.setText("Following");
            getFollowing();
        } else {
            txtType.setText("Followers");
            getFollowers();
        }
        adapter = new ContactsAdapter(users, getApplicationContext());

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        // Set click
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = users.get(position);

                        Intent i = new Intent(getApplicationContext(), VisitProfileActivity.class);
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

    }

    private void setGenderDist() {
        float total = dudesCount + chicksCount + otherCount;

        float dudesPerCent = dudesCount / total;
        float chicksPerCent = chicksCount / total;
        float otherPerCent = otherCount / total;


        float[] dataSet;
        int[] colors;

        dataSet = new float[]{dudesPerCent, chicksPerCent, otherPerCent};
        colors = new int[]{R.color.colorPrimary, R.color.colorPink, R.color.other};

        chartGender.setDataPoints(dataSet);
        chartGender.setSliceColor(colors);
    }

    private void getFollowing() {
        DatabaseReference myFollowing = followingRef.child(loggedUserId);
        myFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.getValue(String.class);
                    following.add(id);
                }
                for (int i = 0; i < following.size(); i++) {
                    DatabaseReference userRef = usersRef.child(following.get(i));
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            users.add(user);
                            adapter.notifyDataSetChanged();

                            if (user.getSex() != null) {
                                if (user.getSex().equals("male")) {
                                    dudesCount = dudesCount + 1;
                                } else if (user.getSex().equals("female")) {
                                    chicksCount = chicksCount + 1;
                                } else if (user.getSex().equals("other")) {
                                    otherCount++;
                                }
                                setGenderDist();
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

    private void getFollowers() {
        DatabaseReference myFollowers = followerRef.child(loggedUserId);
        myFollowers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.getValue(String.class);
                    followers.add(id);
                }
                for (int i = 0; i < followers.size(); i++) {
                    DatabaseReference userRef = usersRef.child(followers.get(i));
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            users.add(user);
                            adapter.notifyDataSetChanged();

                            if (user.getSex() != null) {
                                if (user.getSex().equals("male")) {
                                    dudesCount = dudesCount + 1;
                                } else if (user.getSex().equals("female")) {
                                    chicksCount = chicksCount + 1;
                                } else if (user.getSex().equals("other")) {
                                    otherCount++;
                                }
                                setGenderDist();
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
}
