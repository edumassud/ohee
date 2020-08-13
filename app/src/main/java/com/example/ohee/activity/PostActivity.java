package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Comment;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Notification;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    private User selectedUser;
    private Post selectedPost;

    private CircleImageView imgProfile;
    private ImageView imgPost, btClose, btComment;
    private TextView txtUserName, txtNameBar, txtCaption, txtNameCap, txtLikesCount, txtCommentsCount, txtCommenter, txtComment;
    private LikeButton btLike;

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Set components
        imgProfile       = findViewById(R.id.imgUser);
        imgPost          = findViewById(R.id.imgPost);
        txtUserName      = findViewById(R.id.txtUserName);
        txtNameBar       = findViewById(R.id.txtNameBar);
        txtCaption       = findViewById(R.id.txtCaption);
        txtNameCap       = findViewById(R.id.txtNamCap);
        btClose          = findViewById(R.id.btClose);
        txtLikesCount    = findViewById(R.id.txtLikesCount);
        txtCommentsCount = findViewById(R.id.txtCommentsCount);
        txtCommenter     = findViewById(R.id.txtCommenter);
        txtComment       = findViewById(R.id.txtComment);
        btLike           = findViewById(R.id.btLike);
        btComment        = findViewById(R.id.btComment);

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
        imgProfile.setRotation(selectedUser.getRotation());

        // Set texts
        txtUserName.setText(selectedUser.getName());
        txtNameBar.setText(selectedUser.getName());
        String fullComment = "<b>" + selectedUser.getName() + "</b>" + "  " + selectedPost.getCaption();
//                    holder.txtName.setText(Html.fromHtml(fullComment));
        txtNameCap.setText(Html.fromHtml(fullComment));
//        txtCaption.setText(selectedPost.getCaption());
//        txtNameCap.setText(selectedUser.getName());
//        txtLikesCount.setText(selectedPost.getLikedBy().size() + " Likes");

        // Set close bt
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (selectedPost.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
            btLike.setLiked(true);
        } else {
            btLike.setLiked(false);
        }

        // Set like event
        btLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                selectedPost.getLikedBy().add(SetFirebaseUser.getUsersId());
                selectedPost.upDateLikes();
//                txtLikesCount.setText(selectedPost.getLikedBy().size() + " Likes");
//                if (selectedPost.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
//                    btLike.setLiked(true);
//                } else {
//                    btLike.setLiked(false);
//                }

                if (!SetFirebaseUser.getUsersId().equals(selectedPost.getIdUser())) {
                    Notification notification = new Notification();
                    notification.setIdReceiver(selectedPost.getIdUser());
                    notification.setIdSender(SetFirebaseUser.getUsersId());
                    notification.setAction("postLiked");
                    notification.setIdPost(selectedPost.getId());
                    notification.save();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                selectedPost.getLikedBy().remove(SetFirebaseUser.getUsersId());
                selectedPost.upDateLikes();
//                txtLikesCount.setText(selectedPost.getLikedBy().size() + " Likes");

                if (!SetFirebaseUser.getUsersId().equals(selectedPost.getIdUser())) {
                    Notification notification = new Notification();
                    notification.setIdReceiver(selectedPost.getIdUser());
                    notification.setIdSender(SetFirebaseUser.getUsersId());
                    notification.setAction("postLiked");
                    notification.setIdPost(selectedPost.getId());
                    notification.deleteNotification();
                }
            }
        });


        // Set comment event
        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CommentsActivity.class);
                i.putExtra("post", selectedPost);
                startActivity(i);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtNameBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getComments() {
        // Set comment count
        DatabaseReference postRef = databaseReference.child("posts").child(selectedPost.getUniversityDomain()).child(selectedPost.getId());
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                int commentCount = post.getComments().size();
                if (commentCount == 0) {
                    txtCommentsCount.setVisibility(View.GONE);
                } else if (commentCount == 1) {
                    txtCommentsCount.setText(commentCount + " comment");
                } else if (commentCount > 1){
                    txtCommentsCount.setText(commentCount + " comments");
                }

                // Set featured comment
                if (post.getComments().size() == 0) {
                    txtCommenter.setText("Be the first one to comment!");
                    txtComment.setText("");
                } else {
                    Comment firstComment = post.getComments().get(0);

                    //holder.txtComment.setText(firstComment.getComment());

                    if (!post.getType().equals("highschool")) {
                        DatabaseReference commenterRef = databaseReference.child("user").child(firstComment.getIdUser());
                        commenterRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                String fullComment = "<b>" + user.getName() + "</b>" + "  " + firstComment.getComment();
                                txtCommenter.setText(Html.fromHtml(fullComment));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        DatabaseReference commenterRef = databaseReference.child("highschoolers").child(firstComment.getIdUser());
                        commenterRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HighSchooler user = dataSnapshot.getValue(HighSchooler.class);
                                String fullComment = "<b>" + user.getName() + "</b>" + "  " + firstComment.getComment();
                                txtCommenter.setText(Html.fromHtml(fullComment));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikes() {
        DatabaseReference postRef = databaseReference.child("posts").child(selectedPost.getUniversityDomain()).child(selectedPost.getId());
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                txtLikesCount.setText(post.getLikedBy().size() + " Likes");

                if (post.getLikedBy().contains(SetFirebaseUser.getUsersId())) {
                    btLike.setLiked(true);
                } else {
                    btLike.setLiked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getComments();
        getLikes();
    }
}
