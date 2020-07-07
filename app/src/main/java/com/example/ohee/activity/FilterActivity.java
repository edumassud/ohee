package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFilters;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Post;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imgSelected, btClose, btPost1;
    private Bitmap imgOriginal;
    private Bitmap imgFilter;
    private RecyclerView recyclerFilters;
    private TextInputEditText txtCaption;
    private Button btPrivate, btHome, btPublic, btPost, btPickImg, btInclusive, btExclusive;
    private TextView txtInfo;
    private LinearLayout extraOpts;
    private ProgressBar progressBar, progressBar1;

    private String type = "public";

    private AdapterFilters adapter;

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    private DataSnapshot ds;

    private User loggedUser;
    private String idLoggedUSer;
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference loggedUserRef;
    private DatabaseReference usersRef = databaseReference.child("user");

    private List<ThumbnailItem> listFilters = new ArrayList<>();

    private static final int GALLERY_SELECTION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        imgSelected         = findViewById(R.id.imgSelected);
        recyclerFilters     = findViewById(R.id.recyclerFilters);
        txtCaption          = findViewById(R.id.txtCaption);
        btHome              = findViewById(R.id.btHome);
        btPrivate           = findViewById(R.id.btPrivate);
        btPublic            = findViewById(R.id.btPublic);
        txtInfo             = findViewById(R.id.txtInfo);
        btClose             = findViewById(R.id.btClose);
        btPost1             = findViewById(R.id.btPost1);
        btPost              = findViewById(R.id.btPost);
        progressBar1        = findViewById(R.id.progressBar1);
        progressBar         = findViewById(R.id.progressBar);
        btPickImg           = findViewById(R.id.btPickImage);
        btInclusive         = findViewById(R.id.btInclusive);
        btExclusive         = findViewById(R.id.btExclusive);
        extraOpts           = findViewById(R.id.extraOpts);

        idLoggedUSer = SetFirebaseUser.getUsersId();

        btPickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, GALLERY_SELECTION);
                }
            }
        });

            // Set adapter
            adapter = new AdapterFilters(listFilters, getApplicationContext());

            // Set recycler
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFilters.setLayoutManager(layoutManager);
            recyclerFilters.setHasFixedSize(true);
            recyclerFilters.setAdapter(adapter);

            // Set recycler click
            recyclerFilters.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFilters,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    ThumbnailItem item = listFilters.get(position);

                                    imgFilter = imgOriginal.copy(imgOriginal.getConfig(), true);
                                    Filter filter = item.filter;
                                    imgSelected.setImageBitmap(filter.processFilter(imgFilter));
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            //getFilters();


        makeChoice();

        btPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btPickImg.getVisibility() == View.GONE) {
                    btPost1.setVisibility(View.GONE);
                    btPost.setVisibility(View.GONE);
                    progressBar1.setVisibility(View.VISIBLE);
                    makePost();
                } else {
                    Toast.makeText(FilterActivity.this, "Pick an image first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btPickImg.getVisibility() == View.GONE) {
                    btPost1.setVisibility(View.GONE);
                    btPost.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    makePost();
                }
                else {
                    Toast.makeText(FilterActivity.this, "Pick an image first", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap img = null;

            try {
                switch (requestCode) {
                    case GALLERY_SELECTION:
                        Uri selectedImage = data.getData();
                        img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                }

                if (img != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] imgData = baos.toByteArray();

                    // Send to filter application
                    imgOriginal = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    imgSelected.setImageBitmap(imgOriginal);

                    imgFilter = imgOriginal.copy(imgOriginal.getConfig(), true);

                    btPickImg.setVisibility(View.GONE);
                    imgSelected.setVisibility(View.VISIBLE);
                    getFilters();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeChoice() {

        btPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPrivate.setBackgroundResource(R.drawable.bg_message_balloon);
                btHome.setBackgroundResource(R.drawable.button_background);
                btPublic.setBackgroundResource(R.drawable.button_background);

                extraOpts.setVisibility(View.GONE);

                type = "private";

                txtInfo.setText("Only people following you can see this post.");
            }
        });

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btHome.setBackgroundResource(R.drawable.bg_message_balloon);
                btPrivate.setBackgroundResource(R.drawable.button_background);
                btPublic.setBackgroundResource(R.drawable.button_background);

                extraOpts.setVisibility(View.VISIBLE);

                type = "homeInclusive";

                usersRef.child(SetFirebaseUser.getUsersId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txtInfo.setText("Students from " + user.getUniversityName() + " and your followers can see this post.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                makeHomeChoice();
            }
        });

        btPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPublic.setBackgroundResource(R.drawable.bg_message_balloon);
                btHome.setBackgroundResource(R.drawable.button_background);
                btPrivate.setBackgroundResource(R.drawable.button_background);

                extraOpts.setVisibility(View.GONE);

                type = "public";

                txtInfo.setText("Everyone can see your post");
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void makeHomeChoice() {
        btInclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInclusive.setBackgroundResource(R.drawable.bg_message_balloon);
                btExclusive.setBackgroundResource(R.drawable.button_background);

                type = "homeInclusive";

                usersRef.child(SetFirebaseUser.getUsersId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txtInfo.setText("Students from " + user.getUniversityName() + " and your followers can see this post.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btExclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btExclusive.setBackgroundResource(R.drawable.bg_message_balloon);
                btInclusive.setBackgroundResource(R.drawable.button_background);

                type = "homeExclusive";

                usersRef.child(SetFirebaseUser.getUsersId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txtInfo.setText("Only students from " + user.getUniversityName() + " can see this post.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getFilters() {
        listFilters.clear();
        ThumbnailsManager.clearThumbs();
        recyclerFilters.setVisibility(View.VISIBLE);

        // Set no filter
        ThumbnailItem noFilter = new ThumbnailItem();
        noFilter.image = imgOriginal;
        noFilter.filterName = "Original";
        ThumbnailsManager.addThumb(noFilter);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = imgOriginal;
            item.filter = filter;
            item.filterName = filter.getName();
            ThumbnailsManager.addThumb(item);
        }

        listFilters.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapter.notifyDataSetChanged();

    }

    private void makePost() {
        Post post = new Post();
        post.setIdUser(idLoggedUser);
        post.setCaption(txtCaption.getText().toString());
        post.setUniversityDomain(loggedUser.getUniversityDomain());
        post.setType(type);

        // Get img data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgFilter.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imgData = baos.toByteArray();

        // Save img to firebase storage
        StorageReference storageReference = SetFirebase.getFirebaseStorage();
        StorageReference imgRef = storageReference
                .child("images")
                .child("posts")
                .child(post.getId() + ".jpeg");

        UploadTask uploadTask = imgRef.putBytes(imgData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FilterActivity.this, "Error uploading the image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        post.setPath(uri.toString());

                        post.save(ds);

                        // Increment post count
                        loggedUser.setPostCount(loggedUser.getPostCount() + 1);
                        loggedUser.updateInfo();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            }
        });
    }


    private void getLoggedUserData() {
        loggedUserRef = usersRef.child(idLoggedUSer);
        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedUser = dataSnapshot.getValue(User.class);

                    DatabaseReference followersRef = databaseReference.child("followers").child(idLoggedUSer);
                    followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ds = dataSnapshot;
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

    @Override
    protected void onStart() {
        super.onStart();
        getLoggedUserData();
    }
}
