package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView imgSelected;
    private Bitmap imgOriginal;
    private Bitmap imgFilter;
    private RecyclerView recyclerFilters;
    private TextInputEditText txtCaption;
    private Button btPrivate, btHome, btPublic;
    private TextView txtInfo;

    private String type = "public";

    private AdapterFilters adapter;

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    private User loggedUser;
    private String idLoggedUSer;
    private DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
    private DatabaseReference loggedUserRef;
    private DatabaseReference usersRef = databaseReference.child("user");

    private List<ThumbnailItem> listFilters = new ArrayList<>();

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

        idLoggedUSer = SetFirebaseUser.getUsersId();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        // Getting the img
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] imgData = bundle.getByteArray("selectedPic");
            imgOriginal = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            imgSelected.setImageBitmap(imgOriginal);

            imgFilter = imgOriginal.copy(imgOriginal.getConfig(), true);

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

            getFilters();
        }

        makeChoice();
    }

    private void makeChoice() {

        btPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPrivate.setBackgroundResource(R.drawable.bg_message_balloon);
                btHome.setBackgroundResource(R.drawable.button_background);
                btPublic.setBackgroundResource(R.drawable.button_background);

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

                type = "home";

                txtInfo.setText("Only students from your  can see this post.");
            }
        });

        btPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPublic.setBackgroundResource(R.drawable.bg_message_balloon);
                btHome.setBackgroundResource(R.drawable.button_background);
                btPrivate.setBackgroundResource(R.drawable.button_background);

                type = "public";

                txtInfo.setText("Everyone can see your post");
            }
        });
    }

    private void getFilters() {
        listFilters.clear();
        ThumbnailsManager.clearThumbs();

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

                        post.save();

                        // Increment post count
                        loggedUser.setPostCount(loggedUser.getPostCount() + 1);
                        loggedUser.updateInfo();

                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btPost:
                makePost();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void getLoggedUserData() {
        loggedUserRef = usersRef.child(idLoggedUSer);
        loggedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedUser = dataSnapshot.getValue(User.class);
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
