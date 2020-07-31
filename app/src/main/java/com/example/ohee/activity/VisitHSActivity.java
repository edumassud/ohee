package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.adapter.AdapterQandA;
import com.example.ohee.fragment.QAndAFragment;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Post;
import com.example.ohee.model.Question;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitHSActivity extends AppCompatActivity {
    private Button btGoToChat;
    private CircleImageView imgProfile;
    private TextView questionCount, txtFollowing, profileNameAndUniversity, txtBio, txtUserName;
    private RecyclerView recycler;

    private HighSchooler selectedUser;

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference questionsRef      = databaseReference.child("questions");


    private AdapterQandA adapter;
    private List<Question> questions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_h_s);

        questionCount               = findViewById(R.id.questionsCount);
        txtFollowing                = findViewById(R.id.followingCount);
        txtUserName                 = findViewById(R.id.txtUsername);
        imgProfile                  = findViewById(R.id.profileImg);
        btGoToChat                  = findViewById(R.id.btGoToChat);
        profileNameAndUniversity    = findViewById(R.id.profileNameAndUniversity);
        txtBio                      = findViewById(R.id.profileBio);
        recycler                    = findViewById(R.id.recycler);

        // Get user's data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (HighSchooler) bundle.getSerializable("selectedUser");

            // Customize name & university
            profileNameAndUniversity.setText(selectedUser.getName());

            // Customize picture
            String picPath = selectedUser.getPicturePath();
            if (picPath != null) {
                Uri url = Uri.parse(picPath);
                Glide.with(VisitHSActivity.this)
                        .load(url)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.avatar);
            }
            txtBio.setText(selectedUser.getBio());
            txtFollowing.setText(selectedUser.getInterests().size()+"");
            txtUserName.setText(selectedUser.getUserName());

            questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Question question = ds.getValue(Question.class);
                        if (question.getIdUser().equals(selectedUser.getIdUser())) {
                            total++;
                        }
                    }
                    questionCount.setText(total+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        btGoToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("chatContato", selectedUser);
                i.putExtra("isHighschooler", true);
                startActivity(i);
            }
        });

        // Set adapter
        adapter = new AdapterQandA(questions, this);

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        getQuestions();
    }

    private void getQuestions() {
        questions.clear();
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Question question = ds.getValue(Question.class);
                    if (question.getIdUser().equals(selectedUser.getIdUser())) {
                        questions.add(question);
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