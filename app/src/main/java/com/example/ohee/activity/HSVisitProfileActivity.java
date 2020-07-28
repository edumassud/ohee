package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ohee.R;
import com.example.ohee.adapter.AdapterGrid;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HSVisitProfileActivity extends AppCompatActivity {
    private Button btGoToChat;
    private CircleImageView imgProfile;
    private TextView txtPosts, txtFollowing, txtFollowers, profileNameAndUniversity, txtBio;
    private GridView gridView;

    private User selectedUser;
    private HighSchooler loggedUser;

    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference usersRef          = databaseReference.child("user");
    private DatabaseReference postsRef          = databaseReference.child("posts");
    private DatabaseReference followingRef      = databaseReference.child("following");
    private DatabaseReference followerRef       = databaseReference.child("followers");
    private DatabaseReference usersUniversitysPosts;
    private DatabaseReference userHostRef;
    private DatabaseReference loggedUserRef;

    private String idLoggedUSer;
    private List<Post> posts;

    private ValueEventListener eventListener;

    private AdapterGrid adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_s_visit_profile);

        txtPosts                    = findViewById(R.id.postsCount);
        txtFollowing                = findViewById(R.id.followingCount);
        txtFollowers                = findViewById(R.id.followersCount);
        imgProfile                  = findViewById(R.id.profileImg);
        btGoToChat                  = findViewById(R.id.btGoToChat);
        profileNameAndUniversity    = findViewById(R.id.profileNameAndUniversity);
        txtBio                      = findViewById(R.id.profileBio);
        gridView                    = findViewById(R.id.gridView);

        idLoggedUSer = SetFirebaseUser.getUsersId();

        // Get user's data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedUser = (User) bundle.getSerializable("selectedUser");

            // Get posts ref
            usersUniversitysPosts = postsRef.child(selectedUser.getUniversityDomain());

            // Customize name & university
            profileNameAndUniversity.setText(selectedUser.getName() + " • " + selectedUser.getUniversityName());

            // Customize picture
            String picPath = selectedUser.getPicturePath();
            if (picPath != null) {
                Uri url = Uri.parse(picPath);
                Glide.with(HSVisitProfileActivity.this)
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

        // Set click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = posts.get(position);
                Intent i = new Intent(getApplicationContext(), PostActivity.class);
                i.putExtra("selectedPost", post);
                i.putExtra("selectedUser", selectedUser);

                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getHostData();
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
                        String bio          = String.valueOf(user.getBio());

                        // Set values on profile card
                        txtPosts.setText(posts);
                        txtBio.setText(bio);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        followingRef.child(selectedUser.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int following = (int) dataSnapshot.getChildrenCount();
                txtFollowing.setText(following + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followerRef.child(selectedUser.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followers = (int) dataSnapshot.getChildrenCount();
                txtFollowers.setText(followers + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initImgLoader() {
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);

    }

    private void loadPosts() {

        posts = new ArrayList<>();

        // Set grid size
        int sizeGrid = getResources().getDisplayMetrics().widthPixels;
        int sizeImg = sizeGrid / 3;
        gridView.setColumnWidth(sizeImg);

        DatabaseReference userRef = databaseReference.child("highschooler").child(SetFirebaseUser.getUsersId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HighSchooler loggedUser = dataSnapshot.getValue(HighSchooler.class);
                usersUniversitysPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> urls = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Post post = ds.getValue(Post.class);

                            boolean usersPost = post.getIdUser().equals(selectedUser.getIdUser());
                            boolean isHighSchool = post.getType().equals("highschool");

                            if (usersPost && isHighSchool) {
                                posts.add(post);
                                urls.add(post.getPath());
                            }
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}