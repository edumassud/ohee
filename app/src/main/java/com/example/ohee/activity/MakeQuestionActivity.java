package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFilters;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Question;
import com.example.ohee.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MakeQuestionActivity extends AppCompatActivity {
    private ImageView btClose, btPost1;
    private TextInputEditText txtCaption;
    private Button btPublic, btMyList, btPost;
    private TextView txtInfo;
    private ProgressBar progressBar, progressBar1;

    private String type = "my list";

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    private HighSchooler loggedUser;
    private String idLoggedUSer;
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef = databaseReference.child("highschooler");

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
        Question question = new Question();
        question.setIdUser(idLoggedUser);
        question.setQuestion(txtCaption.getText().toString());
        question.setType(type);

        question.save();

        startActivity(new Intent(getApplicationContext(), MainHSActivity.class));
    }
}