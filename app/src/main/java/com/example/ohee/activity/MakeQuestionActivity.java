package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Post;
import com.example.ohee.model.Question;
import com.example.ohee.model.University;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MakeQuestionActivity extends AppCompatActivity {
    private ImageView btClose, btPost1;
    private TextInputEditText txtCaption;
    private Button btPublic, btMyList, btPost;
    private TextView txtInfo;
    private ProgressBar progressBar, progressBar1;

    private String type = "my list";

    private String idLoggedUSer = SetFirebaseUser.getUsersId();
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference userRef = databaseReference.child("highschoolers").child(idLoggedUSer);

    private University selectedUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_question);

        txtCaption          = findViewById(R.id.txtCaption);
        btPublic            = findViewById(R.id.btPublic);
        btMyList            = findViewById(R.id.btMyList);
        txtInfo             = findViewById(R.id.txtInfo);
        btClose             = findViewById(R.id.btClose);
        btPost1             = findViewById(R.id.btPost1);
        btPost              = findViewById(R.id.btPost);
        progressBar1        = findViewById(R.id.progressBar1);
        progressBar         = findViewById(R.id.progressBar);

        idLoggedUSer = SetFirebaseUser.getUsersId();

        // Get data from specific question
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUniversity = (University) bundle.getSerializable("selectedUniversity");

            btPublic.setVisibility(View.GONE);
            btMyList.setVisibility(View.GONE);
            txtInfo.setVisibility(View.GONE);

            type = "specific";
        }

        makeChoice();

        btPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtCaption.getText().toString().isEmpty()) {
                    progressBar1.setVisibility(View.VISIBLE);
                    postQuestion();
                } else {
                    Toast.makeText(MakeQuestionActivity.this, "Please write your question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtCaption.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    postQuestion();
                } else {
                    Toast.makeText(MakeQuestionActivity.this, "Please write your question", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeChoice() {

        btPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPublic.setBackgroundResource(R.drawable.bg_message_balloon);
                btMyList.setBackgroundResource(R.drawable.button_background);

                type = "public";

                txtInfo.setText("Ambassadors from all universities can see this post.");
            }
        });

        btMyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btMyList.setBackgroundResource(R.drawable.bg_message_balloon);
                btPublic.setBackgroundResource(R.drawable.button_background);

                type = "my list";

                txtInfo.setText("Ambassadors from the universities on your list can see this post.");
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void postQuestion() {
        if (type.equals("my list")) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                    Question question = new Question();
                    question.setIdUser(idLoggedUSer);
                    question.setQuestion(txtCaption.getText().toString());
                    question.setType(type);
                    question.setUniversities(user.getInterests());
                    question.save();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (type.equals("specific")) {
            Question question = new Question();
            question.setIdUser(idLoggedUSer);
            question.setQuestion(txtCaption.getText().toString());
            question.setType(type);
            question.setSpecificUniversity(selectedUniversity);
            question.save();
        } else {
            Question question = new Question();
            question.setIdUser(idLoggedUSer);
            question.setQuestion(txtCaption.getText().toString());
            question.setType(type);
            question.save();
        }



        startActivity(new Intent(getApplicationContext(), HSMainActivity.class));
    }
}