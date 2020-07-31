package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.adapter.AnswersAdapter;
import com.example.ohee.adapter.CommentsAdapter;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnswersActivity extends AppCompatActivity {
    private ImageView btClose;
    private RecyclerView recycler;
    private EditText editComment;
    private FloatingActionButton fabComment;

    protected AnswersAdapter adapter;
    private List<Comment> answers = new ArrayList<>();

    private Question selectedQuestion;

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        btClose         = findViewById(R.id.btClose);
        recycler        = findViewById(R.id.recycler);
        editComment     = findViewById(R.id.editComment);
        fabComment      = findViewById(R.id.fabComment);

        // Get Question
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedQuestion = (Question) bundle.getSerializable("question");
        }

        // Set adapter
        adapter = new AnswersAdapter(answers, this);

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

        fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtComment = editComment.getText().toString();
                if (txtComment.isEmpty()) {
                    Toast.makeText(AnswersActivity.this, "Write an asnwer.", Toast.LENGTH_SHORT).show();
                } else {
                    Comment comment = new Comment(idLoggedUser, txtComment);
                    comment.setIdPost(selectedQuestion.getId());
                    comment.save();
                    selectedQuestion.getAnswers().add(comment);
                    selectedQuestion.upDateAnswers();
                    adapter.notifyDataSetChanged();
                    editComment.setText("");

                    if (!selectedQuestion.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                        Notification notification = new Notification();
                        notification.setIdReceiver(selectedQuestion.getIdUser());
                        notification.setIdSender(SetFirebaseUser.getUsersId());
                        notification.setAction("answer");
                        notification.setIdPost(selectedQuestion.getId());
                        notification.setComment(comment.getComment());
                        notification.save();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAnswers();
    }

    private void getAnswers() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference answersRef = databaseReference.child("comments");

        answersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Comment answer = ds.getValue(Comment.class);
                    if (answer.getIdPost().equals(selectedQuestion.getId())) {
                        answers.add(answer);
                    }
                }
                Collections.sort(answers, Comment.Comparators.LIKES);
                Collections.reverse(answers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}