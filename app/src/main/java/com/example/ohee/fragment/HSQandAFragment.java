package com.example.ohee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFeedExplore;
import com.example.ohee.adapter.AdapterQandA;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Post;
import com.example.ohee.model.Question;
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
public class HSQandAFragment extends Fragment {
    private RecyclerView recycler;
    private AdapterQandA adapter;
    private SwipeRefreshLayout swipeRefresh;

    private List<Question> questions = new ArrayList<>();

    private String loggedUserId = SetFirebaseUser.getUsersId();

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    DatabaseReference userRef           = databaseReference.child("highschoolers").child(loggedUserId);
    DatabaseReference questionsRef      = databaseReference.child("questions");

    private ValueEventListener valueEventListener;

    private boolean isPublic;

    public HSQandAFragment() {
        // Required empty public constructor
    }

    public HSQandAFragment(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_your_university_feed, container, false);

        recycler        = view.findViewById(R.id.recycler);
        swipeRefresh    = view.findViewById(R.id.refresh);

        // Set adapter
        adapter = new AdapterQandA(questions, getActivity());

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
        questions.clear();

        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    if (isPublic && question.getType().equals("public")) {
                        questions.add(question);
                    } else if (!isPublic && question.getType().equals("my list")){
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                                for (int i = 0; i < question.getUniversities().size(); i++) {
                                    for (int j = 0; j < user.getInterests().size(); j++) {
                                        if (user.getInterests().get(j).getDomain().equals(question.getUniversities().get(i).getDomain()) && !questions.contains(question)) {
                                            questions.add(question);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else if (!isPublic && question.getType().equals("specific")) {
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                                for (int j = 0; j < user.getInterests().size(); j++) {
                                    if (user.getInterests().get(j).getDomain().equals(question.getSpecificUniversity().getDomain())) {
                                        questions.add(question);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
