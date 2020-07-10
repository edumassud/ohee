package com.example.ohee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.adapter.CommentsAdapter;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CommentsActivity extends AppCompatActivity {
    private ImageView btClose;
    private RecyclerView recycler;
    private EditText editComment;
    private FloatingActionButton fabComment;

    protected CommentsAdapter adapter;

    private Post selectedPost;

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        btClose         = findViewById(R.id.btClose);
        recycler        = findViewById(R.id.recycler);
        editComment     = findViewById(R.id.editComment);
        fabComment      = findViewById(R.id.fabComment);

        // Get Post
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedPost = (Post) bundle.getSerializable("post");
        }

        // Set adapter
        adapter = new CommentsAdapter(selectedPost.getComments(), this);

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
                    Toast.makeText(CommentsActivity.this, "Write a comment", Toast.LENGTH_SHORT).show();
                } else {
                    Comment comment = new Comment(idLoggedUser, txtComment);
                    selectedPost.getComments().add(comment);
                    selectedPost.upDateComments();
                    adapter.notifyDataSetChanged();
                    editComment.setText("");

                    if (!selectedPost.getIdUser().equals(SetFirebaseUser.getUsersId())) {
                        Notification notification = new Notification();
                        notification.setIdReceiver(selectedPost.getIdUser());
                        notification.setIdSender(SetFirebaseUser.getUsersId());
                        notification.setAction("comment");
                        notification.setIdPost(selectedPost.getId());
                        notification.setComment(comment.getComment());
                        notification.save();
                    }
                }
            }
        });

    }
}
