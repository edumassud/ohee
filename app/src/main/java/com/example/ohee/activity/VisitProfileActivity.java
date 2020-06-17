package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitProfileActivity extends AppCompatActivity {
    private Button btFollow;
    private CircleImageView imgProfile;
    private TextView txtPosts, txtFollowing, txtFollowers, profileNameAndUniversity, txtBio;

    private User selectedUser;
    private User loggedUser;

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef = databaseReference.child("user");
    private DatabaseReference userHostRef;
    private DatabaseReference loggedUserRef;
    private DatabaseReference followersRef = databaseReference.child("followers");

    private String idLoggedUSer;

    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);

        txtPosts                    = findViewById(R.id.postsCount);
        txtFollowing                = findViewById(R.id.followingCount);
        txtFollowers                = findViewById(R.id.followersCount);
        imgProfile                  = findViewById(R.id.profileImg);
        btFollow                    = findViewById(R.id.btEditProfile);
        profileNameAndUniversity    = findViewById(R.id.profileNameAndUniversity);
        txtBio                      = findViewById(R.id.profileBio);

        idLoggedUSer = SetFirebaseUser.getUsersId();

        btFollow.setText("Loading...");

        // Get user's data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (User) bundle.getSerializable("selectedUser");

            // Customize name & university
            profileNameAndUniversity.setText(selectedUser.getName() + " • " + selectedUser.getUniversity());

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

        DatabaseReference followerRef = followersRef
                .child(idLoggedUSer)
                .child(selectedUser.getIdUser());

        followerRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            btFollow.setText("Following");
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

    private void saveFollower(User loggedUser, User friendsUser) {
        HashMap<String, Object> friendsData = new HashMap<>();
        friendsData.put("name", friendsUser.getName());
        friendsData.put("picturePath", friendsUser.getPicturePath());

        DatabaseReference followerRef = followersRef
                .child(loggedUser.getIdUser())
                .child(friendsUser.getIdUser());

        followerRef.setValue(friendsData);

        btFollow.setText("Following");
        btFollow.setOnClickListener(null);

        // Add following to logged user
        int following = loggedUser.getFollowingCount() + 1;

        HashMap<String, Object> followingData = new HashMap<>();
        followingData.put("followingCount", following);

        DatabaseReference userFollowing = usersRef
                .child(loggedUser.getIdUser());

        userFollowing.updateChildren(followingData);

        // Add follower to friend
        // Add following to logged user
        int followers = friendsUser.getFollowerCount() + 1;

        HashMap<String, Object> followerData = new HashMap<>();
        followerData.put("followerCount", followers);

        DatabaseReference userFollower = usersRef
                .child(friendsUser.getIdUser());

        userFollower.updateChildren(followerData);
    }

}
