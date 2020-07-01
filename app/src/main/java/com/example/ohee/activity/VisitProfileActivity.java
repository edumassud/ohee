package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitProfileActivity extends AppCompatActivity {
    private Button btFollow, btGoToChat;
    private CircleImageView imgProfile;
    private TextView txtPosts, txtFollowing, txtFollowers, profileNameAndUniversity, txtBio;
    private GridView gridView;

    private User selectedUser;
    private User loggedUser;

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef = databaseReference.child("user");
    private DatabaseReference userHostRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference followingRef = databaseReference.child("following");
    private DatabaseReference followerRef = databaseReference.child("followers");
    private DatabaseReference postsRef = databaseReference.child("posts");
    private DatabaseReference userPostsRef;

    private String idLoggedUSer;

    private ValueEventListener eventListener;

    private AdapterGrid adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);

        txtPosts                    = findViewById(R.id.postsCount);
        txtFollowing                = findViewById(R.id.followingCount);
        txtFollowers                = findViewById(R.id.followersCount);
        imgProfile                  = findViewById(R.id.profileImg);
        btFollow                    = findViewById(R.id.btEditProfile);
        btGoToChat                  = findViewById(R.id.btGoToChat);
        profileNameAndUniversity    = findViewById(R.id.profileNameAndUniversity);
        txtBio                      = findViewById(R.id.profileBio);
        gridView                    = findViewById(R.id.gridView);

        idLoggedUSer = SetFirebaseUser.getUsersId();

        btFollow.setText("Loading...");

        // Get user's data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (User) bundle.getSerializable("selectedUser");

            // Get posts ref
            userPostsRef = postsRef.child(selectedUser.getIdUser());

            // Customize name & university
            profileNameAndUniversity.setText(selectedUser.getName() + " • " + selectedUser.getUniversityName());

            // Customize picture
            String picPath = selectedUser.getPicturePath();
            if (picPath != null) {
                Uri url = Uri.parse(picPath);
                Glide.with(VisitProfileActivity.this)
                        .load(url)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.avatar);
            }

        }

        btGoToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("chatContato", selectedUser);
                startActivity(i);
            }
        });

        initImgLoader();
        loadPosts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getHostData();
        getLoggedUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userHostRef.removeEventListener(eventListener);
    }


    private void getHostData() {
        userHostRef = usersRef.child(selectedUser.getIdUser());
        eventListener = userHostRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String posts        = String.valueOf(user.getPostCount());
                        String following    = String.valueOf(user.getFollowingCount());
                        String followers    = String.valueOf(user.getFollowerCount());
                        String bio          = String.valueOf(user.getBio());

                        // Set values on profile card
                        txtPosts.setText(posts);
                        txtFollowing.setText(following);
                        txtFollowers.setText(followers);
                        txtBio.setText(bio);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void initImgLoader() {
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration
                .Builder(this)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);

    }

    private void loadPosts() {
        userPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> urls = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    urls.add(post.getPath());
                }

                // Set adapter
                adapter = new AdapterGrid(getApplicationContext(), R.layout.grid_post, urls);

                // Set gridView
                gridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLoggedUserData() {
        loggedUserRef = usersRef.child(idLoggedUSer);
        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedUser = dataSnapshot.getValue(User.class);

                checkFollowing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowing() {

        DatabaseReference followerRef = followingRef
                .child(idLoggedUSer)
                .child(selectedUser.getIdUser());

        followerRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            btFollow.setText("Following");
                            btFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    unfollow(loggedUser, selectedUser);
                                }
                            });
                        } else {
                            btFollow.setText("Follow");
                            btFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveFollower(loggedUser, selectedUser);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void unfollow(User loggedUser, User friendsUser) {
        DatabaseReference followingNode = followingRef
                .child(loggedUser.getIdUser())
                .child(friendsUser.getIdUser());

        followingNode.removeValue();

        DatabaseReference followerNode = followerRef
                .child(friendsUser.getIdUser())
                .child(loggedUser.getIdUser());

        followerNode.removeValue();

        btFollow.setText("Follow");

        loggedUser.setFollowingCount(loggedUser.getFollowingCount() - 1);
        selectedUser.setFollowerCount(selectedUser.getFollowerCount() - 1);

        loggedUser.updateInfo();
        selectedUser.updateInfo();

//
    }

    private void saveFollower(User loggedUser, User friendsUser) {
        HashMap<String, Object> friendsData = new HashMap<>();
        friendsData.put("name", friendsUser.getName());
        friendsData.put("picturePath", friendsUser.getPicturePath());

        HashMap<String, Object> loggedUserData = new HashMap<>();
        loggedUserData.put("name", loggedUser.getName());
        loggedUserData.put("picturePath", loggedUser.getPicturePath());

        DatabaseReference followingNode = followingRef
                .child(loggedUser.getIdUser())
                .child(friendsUser.getIdUser());

        followingNode.setValue(friendsData);

        DatabaseReference followerNode = followerRef
                .child(friendsUser.getIdUser())
                .child(loggedUser.getIdUser());

        followerNode.setValue(loggedUserData);

        btFollow.setText("Following");

        loggedUser.setFollowingCount(loggedUser.getFollowingCount() + 1);
        selectedUser.setFollowerCount(selectedUser.getFollowerCount() + 1);

        loggedUser.updateInfo();
        selectedUser.updateInfo();

    }
}
