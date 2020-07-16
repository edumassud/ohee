package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFeedHome;
import com.example.ohee.adapter.ContactsAdapter;
import com.example.ohee.adapter.SearchAdapter;
import com.example.ohee.fragment.RatingsFragment;
import com.example.ohee.fragment.StatutsDistFragment;
import com.example.ohee.fragment.UniversityDataFragment;
import com.example.ohee.fragment.UniversityProfileMainFragment;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //getGenderDist();
        //getUniDist();

    }

//    private void getIds() {
//        if (type.equals("following")) {
//            DatabaseReference myFollowing = followingRef.child(loggedUserId);
//            myFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        String id = ds.getValue(String.class);
//                        ids.add(id);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        } else {
//            DatabaseReference myFollowers = followerRef.child(loggedUserId);
//            myFollowers.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        String id = ds.getValue(String.class);
//                        ids.add(id);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

//    private void getGenderDist() {
//        getIds();
//        Query searchStudents;
//
//
//        for (int i = 0; i < ids.size(); i++) {
//            searchStudents  = usersRef.orderByChild("idUser").equalTo(ids.get(i));
//
//            searchStudents.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        User user = ds.getValue(User.class);
//                        if (user.getSex() != null) {
//                            if (user.getSex().equals("male")) {
//                                dudesCount = dudesCount + 1;
//                            } else if (user.getSex().equals("female")) {
//                                chicksCount = chicksCount + 1;
//                            } else if (user.getSex().equals("other")) {
//                                otherCount++;
//                            }
//                            setGenderDist();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

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

//    private void getUniDist() {
//        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
//        DatabaseReference usersRef = databaseReference.child("user");
//        DatabaseReference universitiesRef = databaseReference.child("universities");
//        Query searchStudents;
//
//
//
//
//        for (int i = 0; i < ids.size(); i++) {
//            searchStudents  = usersRef.orderByChild("idUser").equalTo(ids.get(i));
//
//            searchStudents.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        User user = ds.getValue(User.class);
//                        universities.add(user.getUniversityName());
//                        countFrequencies(universities);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

//    private void countFrequencies(List<String> list) {
//        // hashmap to store the frequency of element
//        Map<String, Integer> hm = new HashMap<String, Integer>();
//
//        for (String i : list) {
//            Integer j = hm.get(i);
//            hm.put(i, (j == null) ? 1 : j + 1);
//        }
//
//        // displaying the occurrence of elements in the arraylist
//        for (Map.Entry<String, Integer> val : hm.entrySet()) {
//
//
//            Log.i("Test", "Element " + val.getKey() + " "
//                    + "occurs"
//                    + ": " + val.getValue() + " times");
//        }
//    }
//
//    private void setUniDist() {
//        float total = dudesCount + chicksCount + otherCount;
//
//        float dudesPerCent = dudesCount / total;
//        float chicksPerCent = chicksCount / total;
//        float otherPerCent = otherCount / total;
//
////        txtDudesPercent.setText("Dudes: " + Math.round(dudesPerCent * 100) + " %");
////        txtChicksPercent.setText("Girls: " + Math.round(chicksPerCent * 100) + " %");
////        txtOtherPercent.setText("Other: " + Math.round(otherPerCent * 100) + " %");
//
//
//        float[] dataSet;
//        int[] colors;
//
//        dataSet = new float[]{dudesPerCent, chicksPerCent, otherPerCent};
//        colors = new int[]{R.color.colorPrimary, R.color.colorPink, R.color.other};
//
//        chartGender.setDataPoints(dataSet);
//        chartGender.setSliceColor(colors);
//    }

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
