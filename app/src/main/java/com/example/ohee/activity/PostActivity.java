package com.example.ohee.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    private User selectedUser;
    private Post selectedPost;

    private CircleImageView imgProfile;
    private ImageView imgPost, btClose;
    private TextView txtUserName, txtNameBar, txtCaption, txtNameCap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Set components
        imgProfile      = findViewById(R.id.imgUser);
        imgPost         = findViewById(R.id.imgPost);
        txtUserName     = findViewById(R.id.txtUserName);
        txtNameBar      = findViewById(R.id.txtNameBar);
        txtCaption      = findViewById(R.id.txtCaption);
        txtNameCap      = findViewById(R.id.txtNamCap);
        btClose         = findViewById(R.id.btClose);

        // Get Post and User
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (User) bundle.getSerializable("selectedUser");
            selectedPost = (Post) bundle.getSerializable("selectedPost");
        }

        // Set post img
        Uri uri = Uri.parse(selectedPost.getPath());
        Glide.with(getApplicationContext()).load(uri).into(imgPost);

        // Set img profile
        Uri uri1 = Uri.parse(selectedUser.getPicturePath());
        Glide.with(getApplicationContext()).load(uri1).into(imgProfile);

        // Set texts
        txtUserName.setText(selectedUser.getName());
        txtNameBar.setText(selectedUser.getName());
        txtCaption.setText(selectedPost.getCaption());
        txtNameCap.setText(selectedUser.getName());

        // Set close bt
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
