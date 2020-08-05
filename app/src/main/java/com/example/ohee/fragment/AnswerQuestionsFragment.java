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
import com.example.ohee.adapter.AdapterQandA;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.Question;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;


public class AnswerQuestionsFragment extends Fragment {
    private Boolean isHome;
    private String mode;

    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefresh;

    private AdapterQandA adapter;
    private List<Question> questions = new ArrayList<>();

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference questionsRef      = databaseReference.child("questions");
    private DatabaseReference userRef           = databaseReference.child("user").child(SetFirebaseUser.getUsersId());


    public AnswerQuestionsFragment() {
        // Required empty public constructor
    }

    public AnswerQuestionsFragment(Boolean isHome, String mode) {
        this.isHome = isHome;
        this.mode = mode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer_questions, container, false);

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
                getQuestions();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getQuestions();
    }

    private void getQuestions() {
        questions.clear();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Question question = ds.getValue(Question.class);

                            boolean specificToThis = question.getType().equals("specific") && question.getSpecificUniversity().getDomain().equals(user.getUniversityDomain());

                            if (!isHome && question.getType().equals("public")) {
                                questions.add(0, question);
                            } else if (isHome && specificToThis) {
                                questions.add(question);
                            } else if (isHome && question.getType().equals("my list")) {
                                for (int i = 0; i < question.getUniversities().size(); i++) {
                                    if (question.getUniversities().get(i).getDomain().equals(user.getUniversityDomain())) {
                                        questions.add(0, question);
                                        break;
                                    }
                                }
                            }
                        }
                        if (mode.equals("top")) {
                            Collections.sort(questions, Question.Comparators.LIKES);
                            Collections.reverse(questions);
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
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